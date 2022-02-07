package unity.type;

import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import unity.entities.*;
import unity.entities.legs.*;
import unity.entities.legs.CLegType.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.decal.*;
import unity.type.decal.UnitDecorationType.*;
import unity.util.*;

import static arc.Core.*;
import static mindustry.Vars.content;

@SuppressWarnings("unchecked")
public class UnityUnitType extends UnitType{
    public final Seq<Weapon> segWeapSeq = new Seq<>();

    public TextureRegion segmentRegion, tailRegion, segmentCellRegion, segmentOutline, tailOutline,
    legBackRegion, legBaseBackRegion, footBackRegion, legMiddleRegion, legShadowRegion, legShadowBaseRegion,
    payloadCellRegion;
    public TextureRegion[] abilityRegions = new TextureRegion[AbilityTextures.values().length];
    public Seq<Weapon> bottomWeapons = new Seq<>();
    // AI
    public float bulletWidth = 2f;
    // Worms
    public WormDecal wormDecal;
    public int segmentLength = 9, maxSegments = -1;
    //Should reduce the "Whip" effect.
    public int segmentCast = 4;
    public float segmentOffset = 23f, headOffset = 0f;
    public float angleLimit = 30f;
    public float regenTime = -1f, healthDistribution = 0.1f;
    public float segmentDamageScl = 6f;
    public float anglePhysicsSmooth = 0f;
    public float jointStrength = 1f;
    public float barrageRange = 150f;
    // Hopefully make segment movement more consistent
    public boolean counterDrag = false;
    // Attempt to prevent angle drifting due to the inaccurate Atan2
    public boolean preventDrifting = false;
    public boolean splittable = false, chainable = false;
    public Sound splitSound = Sounds.door, chainSound = Sounds.door;
    /**
     * Weapons for each segment.
     * Last item of the array is the tail's weapon.
     */
    public Seq<Weapon>[] segmentWeapons;

    public Func<Unit, Trail> trailType = unit -> new Trail(trailLength);

    // Transforms
    public Func<Unit, UnitType> toTrans;
    public Boolf<Unit> transPred = unit -> {
        Floor floor = unit.floorOn();
        return floor.isLiquid && !(floor instanceof ShallowLiquid) ^ unit instanceof WaterMovec;
    };
    public float transformTime;

    // Decoration
    public Seq<UnitDecorationType> decorations = new Seq<>();

    // Tentacles
    public Seq<TentacleType> tentacles = new Seq<>();

    // Copters
    public final Seq<Rotor> rotors = new Seq<>(4);
    public float rotorDeathSlowdown = 0.01f;
    public float fallRotateSpeed = 2.5f;

    // For shoot armor ability
    public FloatSeq weaponXs = new FloatSeq();

    // Legs extra
    protected static Vec2 legOffsetB = new Vec2();
    protected static float[][] jointOffsets = new float[2][2];

    public Seq<ClegGroupType> legGroup = new Seq<>();

    public boolean customBackLegs = false;
    public boolean legShadows = false;

    // Worm Rendering
    private final static Rect viewport = new Rect(), viewport2 = new Rect();
    private final static int chunks = 4;

    // Linked units
    public UnityUnitType linkType;
    public int linkCount = 1;
    public float rotationSpeed = 20f;

    // Monolith units
    public int maxSouls = 3;

    // End units
    public AntiCheatVariables antiCheatType;
    protected boolean immuneAll = false;

    boolean wormCreating = false;

    // Imber units
    public float laserRange = -1f;
    public int maxConnections = -1;

    // World units
    public int worldWidth, worldHeight;

    public UnityUnitType(String name){
        super(name);
        outlines = false;
    }

    @Override
    public Unit create(Team team){
        Unit unit = super.create(team);

        Class<?> caller = ReflectUtils.classCaller();
        boolean fromWave = caller != null && SpawnGroup.class.isAssignableFrom(caller);

        if(unit instanceof Trailc){
            try{
                ReflectUtils.setField(unit, ReflectUtils.findField(unit.getClass(), "trail", true), trailType.get(unit));
            }catch(Throwable ignored){}
        }

        if(unit instanceof WaterMovec){
            try{
                ReflectUtils.setField(unit, ReflectUtils.findField(unit.getClass(), "tleft", true), trailType.get(unit));
                ReflectUtils.setField(unit, ReflectUtils.findField(unit.getClass(), "tright", true), trailType.get(unit));
            }catch(Throwable ignored){}
        }

        //if(fromWave){
            if(unit instanceof Monolithc e){
                //int count = Mathf.random(Mathf.clamp(e.maxSouls(), 0, 1), maxSouls);
                //for(int i = 0; i < count; i++){
                //    e.join();
                //}
                e.join();
            }
        //}

        if(!wormCreating && unit instanceof Wormc){
            wormCreating = true;
            var cur = (Unit & Wormc)unit;
            int cid = unit.id;
            //Tmp.v1.trns(unit.rotation + 180f, segmentOffset + headOffset).add(unit);
            for(int i = 0; i < segmentLength; i++){
                var t = (Unit & Wormc)create(team);
                t.elevation = unit.elevation;

                t.layer(1f + i);
                t.head(unit);
                t.parent(cur);

                cur.child(t);
                cur.childId(cid);
                cur.headId(unit.id);

                int idx = i >= segmentLength - 1 ? segmentWeapons.length - 1 : i % Math.max(1, segmentWeapons.length - 1);

                t.weaponIdx((byte)idx);
                t.setupWeapons(this);
                cid = t.id;
                cur = t;
            }

            wormCreating = false;
        }

        return unit;
    }

    @Override
    public void load(){
        super.load();

        //copter
        rotors.each(Rotor::load);
        //worm
        if(wormDecal != null) wormDecal.load();
        segmentRegion = atlas.find(name + "-segment");
        segmentCellRegion = atlas.find(name + "-segment-cell", cellRegion);
        tailRegion = atlas.find(name + "-tail");
        segmentOutline = atlas.find(name + "-segment-outline");
        tailOutline = atlas.find(name + "-tail-outline");
        legBackRegion = atlas.find(name + "-leg-back");
        legBaseBackRegion = atlas.find(name + "-leg-base-back");
        footBackRegion = atlas.find(name + "-foot-back");
        legMiddleRegion = atlas.find(name + "-leg-middle", legRegion);

        legShadowRegion = atlas.find(name + "-leg-shadow", legRegion);
        legShadowBaseRegion = atlas.find(name + "-leg-base-shadow", legBaseRegion);

        payloadCellRegion = atlas.find(name + "-cell-payload", cellRegion);

        //abilities
        for(AbilityTextures type : AbilityTextures.values()){
            abilityRegions[type.ordinal()] = atlas.find(name + "-" + type.name());
        }

        decorations.each(UnitDecorationType::load);
        if(segmentWeapons == null){
            segWeapSeq.each(Weapon::load);
        }else{
            for(Seq<Weapon> seq : segmentWeapons){
                seq.each(Weapon::load);
            }
        }
        tentacles.each(TentacleType::load);
        legGroup.each(ClegGroupType::load);
    }

    @Override
    public void init(){
        super.init();
        //copter
        Seq<Rotor> mapped = new Seq<>();

        rotors.each(rotor -> {
            mapped.add(rotor);

            if(rotor.mirror){
                Rotor copy = rotor.copy();
                copy.x *= -1f;
                copy.speed *= -1f;
                copy.shadeSpeed *= -1f;
                copy.rotOffset += 360f / (copy.bladeCount * 2);

                mapped.add(copy);
            }
        });

        TentacleType.set(tentacles);

        weapons.each(w -> weaponXs.add(w.x));

        rotors.set(mapped);
        //worm
        if(segmentWeapons == null){
            sortSegWeapons(segWeapSeq);
            segmentWeapons = new Seq[]{segWeapSeq};
        }else{
            for(Seq<Weapon> seq : segmentWeapons){
                sortSegWeapons(seq);
            }
        }

        Seq<Weapon> addBottoms = new Seq<>();
        for(Weapon w : weapons){
            if(bottomWeapons.contains(w) && w.otherSide != -1){
                addBottoms.add(weapons.get(w.otherSide));
            }
        }

        bottomWeapons.addAll(addBottoms.distinct());

        if(immuneAll){
            immunities.addAll(content.getBy(ContentType.status));
        }
    }

    public void sortSegWeapons(Seq<Weapon> weaponSeq){
        Seq<Weapon> mapped = new Seq<>();
        for(int i = 0, len = weaponSeq.size; i < len; i++){
            Weapon w = weaponSeq.get(i);
            if(w.recoilTime < 0f){
                w.recoilTime = w.reload;
            }
            mapped.add(w);

            if(w.mirror){
                Weapon copy = w.copy();
                copy.x *= -1;
                copy.shootX *= -1;
                copy.flipSprite = !copy.flipSprite;
                mapped.add(copy);

                w.reload *= 2;
                copy.reload *= 2;
                w.recoilTime *= 2;
                copy.recoilTime *= 2;
                w.otherSide = mapped.size - 1;
                copy.otherSide = mapped.size - 2;
            }
        }

        weaponSeq.set(mapped);
    }

    public <T extends Unit & Wormc> void drawWorm(T unit){
        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = (unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f)) - (unit.layer() * 0.00001f);

        if(unit.isFlying() || visualElevation > 0){
            TextureRegion tmpShadow = shadowRegion;
            if(!unit.isHead() || unit.isTail()){
                shadowRegion = unit.isTail() ? tailRegion : segmentRegion;
            }

            Draw.z(Math.min(Layer.darkness, z - 1f));
            drawShadow(unit);
            shadowRegion = tmpShadow;
        }

        Draw.z(z - 0.02f);
        if(mech != null){
            drawMech(mech);

            //side
            legOffsetB.trns(mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f/Mathf.PI, 1) * mechSideSway, 0f, unit.elevation));

            //front
            legOffsetB.add(Tmp.v1.trns(mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f/Mathf.PI, 1) * mechFrontSway, 0f, unit.elevation)));

            unit.trns(legOffsetB.x, legOffsetB.y);
        }
        if(unit instanceof Legsc){
            drawLegs((Unit & Legsc)unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if(unit instanceof Payloadc){
            drawPayload((Unit & Payloadc)unit);
        }

        drawSoftShadow(unit);

        Draw.z(z);

        TextureRegion tmp = region, tmpOutline = outlineRegion, tmpCell = cellRegion;
        if(!unit.isHead() || unit.isTail()){
            region = unit.isTail() ? tailRegion : segmentRegion;
            outlineRegion = unit.isTail() ? tailOutline : segmentOutline;
        }
        if(!unit.isHead()) cellRegion = segmentCellRegion;

        drawOutline(unit);
        drawWeaponOutlines(unit);

        if(unit.isTail() && unit.layer() < maxSegments){
            Draw.draw(z, () -> {
                Tmp.v1.trns(unit.rotation + 180f, segmentOffset).add(unit);
                Drawf.construct(Tmp.v1.x, Tmp.v1.y, tailRegion, unit.rotation - 90f, unit.regenTime() / regenTime, 1f, unit.regenTime());
            });
        }

        drawBody(unit);
        if(drawCell && !unit.isTail()) drawCell(unit);
        if(wormDecal != null) wormDecal.draw(unit, unit.parent());

        cellRegion = tmpCell;
        region = tmp;
        outlineRegion = tmpOutline;

        drawWeapons(unit);

        if(unit.shieldAlpha > 0 && drawShields){
            drawShield(unit);
        }

        if(mech != null){
            unit.trns(-legOffsetB.x, -legOffsetB.y);
        }

        if(unit.abilities.size > 0){
            for(Ability a : unit.abilities){
                Draw.reset();
                a.draw(unit);
            }

            Draw.reset();
        }
    }

    @Override
    public void draw(Unit unit){
        if(unit instanceof Wormc w && !w.isHead()){
            drawWorm((Unit & Wormc)w);
        }else{
            super.draw(unit);
        }

        // copter
        if(unit instanceof Copterc){
            drawRotors((Unit & Copterc)unit);
        }
    }

    @Override
    public Color cellColor(Unit unit){
        if(unit instanceof Monolithc e && e.disabled()){
            return Tmp.c1.set(Color.black).lerp(unit.team.color, 0.1f);
        }else{
            return super.cellColor(unit);
        }
    }

    @Override
    public void drawCell(Unit unit) {
        if(unit.isAdded()){
            super.drawCell(unit);
        }else{
            applyColor(unit);

            Draw.color(cellColor(unit));
            Draw.rect(payloadCellRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
        }
    }

    public <T extends Unit & TriJointLegsc> void drawTriLegs(T unit){
        applyColor(unit);

        TriJointLeg[] legs = unit.legs();

        float ssize = footRegion.width * Draw.scl * 1.5f;
        float rotation = unit.baseRotation();

        for(TriJointLeg leg : legs){
            Drawf.shadow(leg.joints[2].x, leg.joints[2].y, ssize);
        }

        for(int j = legs.length - 1; j >= 0; j--){
            int i = (j % 2 == 0 ? (j / 2) : legs.length - 1 - (j / 2));
            TriJointLeg leg = legs[i];
            float angle = unit.legAngle(rotation, i);
            boolean flip = i >= legs.length / 2f;
            int flips = Mathf.sign(flip);


            Vec2 position = legOffsetB.trns(angle, legBaseOffset).add(unit);
            for(int k = 0; k < 2; k++){
                Tmp.v1.set(leg.joints[1 + k]).sub(leg.joints[k]).inv().setLength(legExtension);
                jointOffsets[k][0] = Tmp.v1.x;
                jointOffsets[k][1] = Tmp.v1.y;
            }

            if(leg.moving && visualElevation > 0f){
                float scl = visualElevation;
                float elev = Mathf.slope(1f - leg.stage) * scl;
                Draw.color(Pal.shadow);
                Draw.rect(footRegion, leg.joints[2].x + shadowTX * elev, leg.joints[2].y + shadowTY * elev, position.angleTo(leg.joints[2]));
                applyColor(unit);
            }

            Draw.rect(footRegion, leg.joints[2].x, leg.joints[2].y, position.angleTo(leg.joints[2]));

            Lines.stroke(legRegion.height * Draw.scl * flips);
            Lines.line(legRegion, position.x, position.y, leg.joints[0].x, leg.joints[0].y, false);
            for(int k = 0; k < 2; k++){
                TextureRegion region = k == 0 ? legMiddleRegion : legBaseRegion;
                Lines.stroke(region.height * Draw.scl * flips);
                Lines.line(region, leg.joints[k].x + jointOffsets[k][0], leg.joints[k].y + jointOffsets[k][1], leg.joints[k + 1].x, leg.joints[k + 1].y, false);
            }
            if(baseJointRegion.found() || jointRegion.found()){
                for(int k = -1; k < 2; k++){
                    Vec2 pos = k == -1 ? position : leg.joints[k];
                    TextureRegion region = k == -1 ? baseJointRegion : jointRegion;
                    if(region.found()) Draw.rect(region, pos.x, pos.y, k == -1 ? rotation : 0f);
                }
            }
        }

        if(baseRegion.found()){
            Draw.rect(baseRegion, unit.x, unit.y, rotation);
        }

        Draw.reset();
    }

    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit){
        if(!customBackLegs){
            if(legShadows && visualElevation > 0){
                float z = Draw.z();
                float rotation = unit.baseRotation();
                Draw.z(Math.min(Layer.darkness, z - 0.98f));
                Draw.color(Pal.shadow);

                Leg[] legs = unit.legs();

                for(int j = legs.length - 1; j >= 0; j--){
                    int i = (j % 2 == 0 ? j / 2 : legs.length - 1 - j / 2);
                    Leg leg = legs[i];
                    float angle = unit.legAngle(rotation, i);
                    boolean flip = i >= legs.length / 2f;
                    int flips = Mathf.sign(flip);

                    Vec2 position = legOffsetB.trns(angle, legBaseOffset).add(unit),
                    v1 = Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension).add(leg.joint);

                    float elev = 0f;
                    if(leg.moving){
                        elev = Mathf.slope(1f - leg.stage);
                    }
                    float mid = (elev / 2f) + 0.5f;
                    float scl = visualElevation;

                    Lines.stroke(legShadowRegion.height * Draw.scl * flips);
                    Lines.line(legShadowRegion, position.x + (shadowTX * scl), position.y + (shadowTY * scl), leg.joint.x + (shadowTX * mid * scl), leg.joint.y + (shadowTY * mid * scl), false);

                    Lines.stroke(legShadowBaseRegion.height * Draw.scl * flips);
                    Lines.line(legShadowBaseRegion, v1.x + (shadowTX * mid * scl), v1.y + (shadowTY * mid * scl), leg.base.x + (shadowTX * elev * scl), leg.base.y + (shadowTY * elev * scl), false);
                }

                Draw.color();
                Draw.z(z);
            }

            super.drawLegs(unit);
        }else{
            applyColor(unit);

            Leg[] legs = unit.legs();

            float ssize = footRegion.width * Draw.scl * 1.5f;
            float rotation = unit.baseRotation();

            for(Leg leg : legs){
                Drawf.shadow(leg.base.x, leg.base.y, ssize);
            }

            //legs are drawn front first
            for(int j = legs.length - 1; j >= 0; j--){
                int i = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
                Leg leg = legs[i];
                float angle = unit.legAngle(rotation, i);
                boolean flip = i >= legs.length/2f;
                boolean back = j < legs.length - 2;
                int flips = Mathf.sign(flip);

                TextureRegion fr = back ? footRegion : footBackRegion;
                TextureRegion lr = back ? legRegion : legBackRegion;
                TextureRegion lbr = back ? legBaseRegion : legBaseBackRegion;

                Vec2 position = legOffsetB.trns(angle, legBaseOffset).add(unit);

                Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension);

                if(leg.moving && visualElevation > 0){
                    float scl = visualElevation;
                    float elev = Mathf.slope(1f - leg.stage) * scl;
                    Draw.color(Pal.shadow);
                    Draw.rect(fr, leg.base.x + shadowTX * elev, leg.base.y + shadowTY * elev, position.angleTo(leg.base));
                    Draw.color();
                }

                Draw.rect(fr, leg.base.x, leg.base.y, position.angleTo(leg.base));

                Lines.stroke(lr.height * Draw.scl * flips);
                Lines.line(lr, position.x, position.y, leg.joint.x, leg.joint.y, false);

                Lines.stroke(lbr.height * Draw.scl * flips);
                Lines.line(lbr, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);

                if(jointRegion.found()){
                    Draw.rect(jointRegion, leg.joint.x, leg.joint.y);
                }

                if(baseJointRegion.found()){
                    Draw.rect(baseJointRegion, position.x, position.y, rotation);
                }
            }

            if(baseRegion.found()){
                Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
            }

            Draw.reset();
        }
    }

    @Override
    public void drawShadow(Unit unit){
        super.drawShadow(unit);
        if(unit instanceof WormDefaultUnit wormUnit) wormUnit.drawShadow();
    }

    @Override
    public void drawSoftShadow(Unit unit){
        if(unit instanceof TriJointLegsc){
            float oz = Draw.z();
            Draw.z(oz - 0.01f);
            drawTriLegs((Unit & TriJointLegsc)unit);
            Draw.z(oz);
        }
        if(unit instanceof CLegc){
            for(CLegGroup group : ((CLegc)unit).legGroups()){
                group.draw(unit);
            }
        }

        super.drawSoftShadow(unit);
        //worm
        if(!(unit instanceof WormDefaultUnit wormUnit)) return;
        /*for(WormSegmentUnit s : wormUnit.segmentUnits){
            wormUnit.type.drawSoftShadow(s);
        }*/
        float z = Draw.z();
        for(int i = 0; i < wormUnit.segmentUnits.length; i++){
            Draw.z(z - (i + 1.1f) / 10000f);
            wormUnit.type.drawSoftShadow(wormUnit.segmentUnits[i]);
        }
        Draw.z(z);
    }

    @Override
    public void drawOutline(Unit unit){
        if(unit instanceof Decorationc d){
            for(UnitDecoration decor : d.decors()){
                if(!decor.type.top) decor.type.draw(unit, decor);
            }
        }
        super.drawOutline(unit);
    }

    @Override
    public void drawBody(Unit unit){
        float z = Draw.z();

        if(unit instanceof Tentaclec t){
            Draw.z(Math.min(z - 0.01f, 99f));
            t.drawTentacles();
            Draw.z(z);
        }
        super.drawBody(unit);

        if(unit instanceof Decorationc d){
            for(UnitDecoration decor : d.decors()){
                if(decor.type.top) decor.type.draw(unit, decor);
            }
        }
        //worm
        if(unit instanceof WormDefaultUnit wormUnit){
            camera.bounds(viewport);
            int index = -chunks;
            for(int i = 0; i < wormUnit.segmentUnits.length; i++){
                if(i >= index + chunks){
                    index = i;
                    Unit seg = wormUnit.segmentUnits[index];
                    Unit segN = wormUnit.segmentUnits[Math.min(index + chunks, wormUnit.segmentUnits.length - 1)];
                    float grow = wormUnit.regenAvailable() && (index + chunks) >= wormUnit.segmentUnits.length - 1 ? seg.clipSize() : 0f;
                    Tmp.r3.setCentered(segN.x, segN.y, segN.clipSize());
                    viewport2.setCentered(seg.x, seg.y, seg.clipSize()).merge(Tmp.r3).grow(grow + (seg.clipSize() / 2f));
                }
                if(viewport.overlaps(viewport2)){
                    Draw.z(z - (i + 1f) / 10000f);
                    if(wormUnit.regenAvailable() && i == wormUnit.segmentUnits.length - 1){
                        int finalI = i;
                        Draw.draw(z - (i + 2f) / 10000f, () -> {
                            Tmp.v1.trns(wormUnit.segmentUnits[finalI].rotation + 180f, segmentOffset).add(wormUnit.segmentUnits[finalI]);
                            Drawf.construct(Tmp.v1.x, Tmp.v1.y, tailRegion, wormUnit.segmentUnits[finalI].rotation - 90f, wormUnit.repairTime / regenTime, 1f, wormUnit.repairTime);
                        });
                    }
                    wormUnit.segmentUnits[i].drawBody();
                    drawWeapons(wormUnit.segmentUnits[i]);
                }
            }
        }

        if(unit instanceof Worldc){
            var w = (Unit & Worldc)unit;
            Draw.draw(z + 0.0001f, () -> {
                Seq<Building> build = w.buildings();
                World world = w.unitWorld();
                float cx = world.width() * Vars.tilesize / 2f, cy = world.height() * Vars.tilesize / 2f;
                float r = w.rotation - 90f;

                Mat proj = Tmp.m1.set(Draw.proj());
                Vec2 cam = camera.position;
                float camX = cam.x, camY = cam.y;
                float cw = camera.width / 2f, ch = camera.height / 2f;
                Tmp.v2.set(-cx, -cy).rotate(r);

                Tmp.v1.set(unit).sub(camX, camY).add(cw, ch).add(Tmp.v2);

                cam.set(cw - Tmp.v1.x, ch - Tmp.v1.y);
                camera.update();
                Draw.flush();
                Batch old = batch;
                batch = UnityDrawf.altBatch;

                Draw.proj(camera);

                Draw.proj().rotate(r);
                Draw.sort(true);

                for(int i = 0; i < build.size; i++){
                    Building b = build.get(i);
                    Draw.z(Layer.block);
                    b.draw();
                }

                //Should fix the blending bug.
                Draw.z(9999f);
                Draw.color(Color.clear);
                Fill.rect(0, 0, 0, 0);

                Draw.reset();
                Draw.flush();
                Draw.sort(false);

                cam.set(camX, camY);
                camera.update();
                Draw.proj(proj);
                batch = old;
            });
        }

        Draw.z(z);
    }

    @Override
    public void drawWeapons(Unit unit){
        float z = Draw.z();

        applyColor(unit);
        for(WeaponMount mount : unit.mounts){
            Weapon weapon = mount.weapon;
            if(bottomWeapons.contains(weapon)) Draw.z(z - 0.0001f);

            weapon.draw(unit, mount);
            Draw.z(z);
        }

        Draw.reset();
    }

    public <T extends Unit & Copterc> void drawRotors(T unit){
        applyColor(unit);

        RotorMount[] rotors = unit.rotors();
        for(RotorMount mount : rotors){
            Rotor rotor = mount.rotor;
            float x = unit.x + Angles.trnsx(unit.rotation - 90f, rotor.x, rotor.y);
            float y = unit.y + Angles.trnsy(unit.rotation - 90f, rotor.x, rotor.y);

            float alpha = Mathf.curve(unit.rotorSpeedScl(), 0.2f, 1f);
            Draw.color(0f, 0f, 0f, rotor.shadowAlpha);
            float rad = 1.2f;
            float size = Math.max(rotor.bladeRegion.width, rotor.bladeRegion.height) * Draw.scl;

            Draw.rect(softShadowRegion, x, y, size * rad * Draw.xscl, size * rad * Draw.yscl);

            Draw.color();
            Draw.alpha(alpha * rotor.ghostAlpha);

            Draw.rect(rotor.bladeGhostRegion, x, y, mount.rotorRot);
            Draw.rect(rotor.bladeShadeRegion, x, y, mount.rotorShadeRot);

            Draw.alpha(1f - alpha * rotor.bladeFade);
            for(int j = 0; j < rotor.bladeCount; j++){
                Draw.rect(rotor.bladeOutlineRegion, x, y, (unit.rotation + (
                    unit.id * 24f + mount.rotorRot +
                    (360f / rotor.bladeCount) * j
                )) % 360);
            }
        }

        for(RotorMount mount : rotors){
            Rotor rotor = mount.rotor;
            float x = unit.x + Angles.trnsx(unit.rotation - 90f, rotor.x, rotor.y);
            float y = unit.y + Angles.trnsy(unit.rotation - 90f, rotor.x, rotor.y);

            Draw.alpha(1f - Mathf.curve(unit.rotorSpeedScl(), 0.2f, 1f) * rotor.bladeFade);
            for(int j = 0; j < rotor.bladeCount; j++){
                Draw.rect(rotor.bladeRegion, x, y, (unit.rotation + (
                    unit.id * 24f + mount.rotorRot +
                    (360f / rotor.bladeCount) * j
                )) % 360);
            }

            Draw.alpha(1f);
            Draw.rect(rotor.topRegion, x, y, unit.rotation - 90f);
        }

        Draw.reset();
    }
}
