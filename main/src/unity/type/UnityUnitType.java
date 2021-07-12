package unity.type;

import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Log.*;
import arc.util.*;
import arc.util.noise.*;
import arc.util.pooling.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import unity.*;
import unity.entities.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.type.weapons.*;
import unity.util.*;

import static arc.Core.*;

/*unit.entities.units might be gradually deleted.
note that as classes are integrated, inner classes are extracted.*/
public class UnityUnitType extends UnitType{
    public final Seq<Weapon> segWeapSeq = new Seq<>();

    public TextureRegion segmentRegion, tailRegion, segmentCellRegion, segmentOutline, tailOutline, legBackRegion, legBaseBackRegion, footBackRegion, legMiddleRegion, payloadCellRegion;
    public TextureRegion[] abilityRegions = new TextureRegion[AbilityTextures.values().length];
    public ObjectSet<String> bottomWeapons = new ObjectSet<>();
    // Worms
    public int segmentLength = 9, maxSegments = -1;
    public float segmentOffset = 23f, headOffset = 0f;
    public float angleLimit = 30f;
    public float regenTime = -1f, healthDistribution = 0.1f;
    public float segmentDamageScl = 6f;
    public float anglePhysicsSide = 0.25f;
    public float anglePhysicsSmooth = 0f;
    public float jointStrength = 1f;
    // Hopefully make segment movement more consistent
    public boolean counterDrag = false;
    public boolean splittable = false, chainable = false;
    public Sound splitSound = Sounds.door, chainSound = Sounds.door;

    // Transforms
    public Func<Unit, UnitType> toTrans;
    public Boolf<Unit> transPred = unit -> {
        Floor floor = unit.floorOn();
        return floor.isLiquid && !(floor instanceof ShallowLiquid) ^ unit instanceof WaterMovec;
    };
    public float transformTime;

    // Tentacles
    public Seq<TentacleType> tentacles = new Seq<>();

    // Copters
    public final Seq<Rotor> rotors = new Seq<>(4);
    public float rotorDeathSlowdown = 0.01f;
    public float fallRotateSpeed = 2.5f;

    // Mech pad units
    public Color engineColor;

    // For shoot armor ability
    public FloatSeq weaponXs = new FloatSeq();
    
    // Legs extra
    protected static Vec2 legOffsetB = new Vec2();
    protected static float[][] jointOffsets = new float[2][2];

    public boolean customBackLegs = false;

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

    // Texture generation
    protected ObjectSet<String> outlined = new ObjectSet<>();

    public UnityUnitType(String name){
        super(name);
    }

    @Override
    public Unit create(Team team){
        Unit unit = super.create(team);

        Class<?> caller = ReflectUtils.classCaller();
        boolean fromWave = caller != null && SpawnGroup.class.isAssignableFrom(caller);

        if(fromWave){
            if(unit instanceof Monolithc e){
                int count = Mathf.random(Mathf.clamp(e.maxSouls(), 0, 1), maxSouls);
                for(int i = 0; i < count; i++){
                    e.join();
                }
            }
        }

        return unit;
    }

    @Override
    public void load(){
        super.load();

        //copter
        rotors.each(Rotor::load);
        //worm

        segmentRegion = atlas.find(name + "-segment");
        segmentCellRegion = atlas.find(name + "-segment-cell", cellRegion);
        tailRegion = atlas.find(name + "-tail");
        segmentOutline = atlas.find(name + "-segment-outline");
        tailOutline = atlas.find(name + "-tail-outline");
        legBackRegion = atlas.find(name + "-leg-back");
        legBaseBackRegion = atlas.find(name + "-leg-base-back");
        footBackRegion = atlas.find(name + "-foot-back");
        legMiddleRegion = atlas.find(name + "-leg-middle", legRegion);
        payloadCellRegion = atlas.find(name + "-cell-payload", cellRegion);

        //abilities
        for(AbilityTextures type : AbilityTextures.values()){
            abilityRegions[type.ordinal()] = atlas.find(name + "-" + type.name());
        }

        segWeapSeq.each(Weapon::load);
        tentacles.each(TentacleType::load);
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
        sortSegWeapons(segWeapSeq);
    }

    public void sortSegWeapons(Seq<Weapon> weaponSeq){
        Seq<Weapon> mapped = new Seq<>();
        for(int i = 0, len = weaponSeq.size; i < len; i++){
            Weapon w = weaponSeq.get(i);
            mapped.add(w);
            if(w.mirror){
                Weapon copy = w.copy();
                copy.x *= -1;
                copy.shootX *= -1;
                copy.flipSprite = !copy.flipSprite;
                mapped.add(copy);
                w.reload *= 2;
                copy.reload *= 2;
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

        if(unit.isTail()){
            Draw.draw(z, () -> {
                Tmp.v1.trns(unit.rotation + 180f, segmentOffset).add(unit);
                Drawf.construct(Tmp.v1.x, Tmp.v1.y, tailRegion, unit.rotation - 90f, unit.regenTime() / regenTime, 1f, unit.regenTime());
            });
        }

        drawBody(unit);
        if(drawCell && !unit.isTail()) drawCell(unit);

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
    public void drawBody(Unit unit){
        float z = Draw.z();

        if(unit instanceof Tentaclec t){
            Draw.z(Math.min(z - 0.01f, 99f));
            t.drawTentacles();
            Draw.z(z);
        }
        super.drawBody(unit);
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

        Draw.z(z);
    }

    @Override
    public void drawEngine(Unit unit){
        if(engineColor != null){
            if(!unit.isFlying()) return;

            float scale = unit.elevation;
            float offset = engineOffset / 2f + engineOffset / 2f * scale;

            if(unit instanceof Trailc trail){
                Trail t = trail.trail();
                t.draw(engineColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * scale) * trailScl);
            }

            Draw.color(engineColor);
            Fill.circle(
                unit.x + Angles.trnsx(unit.rotation + 180, offset),
                unit.y + Angles.trnsy(unit.rotation + 180, offset),
                (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) * scale
            );
            Draw.color(Color.white);
            Fill.circle(
                unit.x + Angles.trnsx(unit.rotation + 180, offset - 1f),
                unit.y + Angles.trnsy(unit.rotation + 180, offset - 1f),
                (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) / 2f  * scale
            );
            Draw.color();
        }else{
            super.drawEngine(unit);
        }
    }

    @Override
    public void drawWeapons(Unit unit){
        float z = Draw.z();

        //super.drawWeapons(unit);
        applyColor(unit);
        for(WeaponMount mount : unit.mounts){
            if(bottomWeapons.contains(mount.weapon.name)) Draw.z(z - 0.0001f);
            mount.weapon.draw(unit, mount);
            Draw.z(z);
        }

        Draw.reset();
    }

    public <T extends Unit & Copterc> void drawRotors(T unit){
        Draw.mixcol(Color.white, unit.hitTime);

        for(RotorMount mount : unit.rotors()){
            Rotor rotor = mount.rotor;

            float offX = Angles.trnsx(unit.rotation - 90, rotor.x, rotor.y);
            float offY = Angles.trnsy(unit.rotation - 90, rotor.x, rotor.y);

            float alpha = Mathf.curve(unit.rotorSpeedScl(), 0.2f, 1f);
            Draw.alpha(alpha * rotor.ghostAlpha);
            Draw.rect(rotor.bladeGhostRegion, unit.x + offX, unit.y + offY,
                rotor.bladeGhostRegion.width * rotor.scale * Draw.scl,
                rotor.bladeGhostRegion.height * rotor.scale * Draw.scl,
                mount.rotorRot
            );

            Draw.rect(rotor.bladeShadeRegion, unit.x + offX, unit.y + offY,
                rotor.bladeShadeRegion.width * rotor.scale * Draw.scl,
                rotor.bladeShadeRegion.height * rotor.scale * Draw.scl,
                mount.rotorShadeRot
            );

            float z = Draw.z();

            Draw.alpha(1f - alpha);
            for(int i = 0; i < 2; i++){
                Draw.z(z + i * 0.001f);
                for(int j = 0; j < rotor.bladeCount; j++){
                    float angle = (
                        unit.rotation +
                        (unit.id * 24f + mount.rotorRot +
                        (360f / (float)rotor.bladeCount) * j)
                    ) % 360;

                    Draw.rect(i == 0 ? rotor.bladeOutlineRegion : rotor.bladeRegion, unit.x + offX, unit.y + offY,
                        rotor.bladeRegion.width * rotor.scale * Draw.scl,
                        rotor.bladeRegion.height * rotor.scale * Draw.scl,
                    angle);
                }
            }

            Draw.z(z + 0.002f);
            Draw.alpha(1f);
            Draw.rect(rotor.topRegion, unit.x + offX, unit.y + offY, unit.rotation - 90f);

            Draw.z(z);
        }

        Draw.mixcol();
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);

        if(true) return;
        Color color = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f);
        try{
            float scl = Draw.scl / 4f;

            Cons<TextureRegion> outliner = t -> {
                if(t instanceof AtlasRegion at && outlined.add(at.name)){
                    GraphicUtils.outline(packer, t, outlineColor, outlineRadius, at.name, true);
                }
            };

            Cons2<TextureRegion, String> outlSeparate = (t, suffix) -> {
                if(t instanceof AtlasRegion at){
                    GraphicUtils.outline(packer, t, outlineColor, outlineRadius, at.name + "-" + suffix, false);
                }
            };

            Unit unit = constructor.get();

            if(unit instanceof Legsc || unit instanceof TriJointLegsc){
                outliner.get(jointRegion);
                outliner.get(footRegion);
                outliner.get(legBaseRegion);
                outliner.get(baseJointRegion);
                outliner.get(legRegion);

                outliner.get(legMiddleRegion);

                outliner.get(legBackRegion);
                outliner.get(legBaseBackRegion);
                outliner.get(footBackRegion);
            }

            if(unit instanceof Mechc){
                outliner.get(legRegion);
            }

            if(unit instanceof Copterc){
                for(Rotor rotor : rotors){
                    outlSeparate.get(rotor.bladeRegion, "outline");
                    outliner.get(rotor.topRegion);

                    if(atlas.has(rotor.name + "-blade-ghost") || !atlas.has(rotor.name + "-blade")) continue;

                    PixmapRegion bladeSprite = atlas.getPixmap(rotor.name + "-blade");

                    // This array is to be written in the order where colors at index 0 are located towards the center,
                    // and colors at the end of the array is located towards at the edge.
                    int[] heightAverageColors = new int[(bladeSprite.height >> 1) + 1]; // Go one extra so it becomes transparent especially if blade is full length
                    int bladeLength = populateColorArray(heightAverageColors, bladeSprite, bladeSprite.height >> 1);

                    Pixmap ghostSprite = new Pixmap(bladeSprite.height, bladeSprite.height);
                    drawRadial(ghostSprite, heightAverageColors, bladeLength);

                    packer.add(PageType.main, rotor.name + "-blade-ghost", ghostSprite);

                    if(atlas.has(rotor.name + "-blade-shade")) continue;

                    Pixmap shadeSprite = new Pixmap(bladeSprite.height, bladeSprite.height);
                    drawShade(shadeSprite, bladeLength);

                    packer.add(PageType.main, rotor.name + "-blade-shade", shadeSprite);
                }
            }

            if(unit instanceof WormDefaultUnit || unit instanceof Wormc){
                outlSeparate.get(segmentRegion, "outline");
                outlSeparate.get(tailRegion, "outline");
            }

            for(TextureRegion reg : abilityRegions){
                outliner.get(reg);
            }

            for(TentacleType tentacle : tentacles){
                outliner.get(tentacle.region);
                outliner.get(tentacle.tipRegion);
            }

            Pixmap icon = GraphicUtils.outline(region, outlineColor, outlineRadius);

            if(unit instanceof Mechc){
                GraphicUtils.drawCenter(icon, GraphicUtils.get(packer, baseRegion));
                GraphicUtils.drawCenter(icon, GraphicUtils.get(packer, legRegion));

                var flip = GraphicUtils.get(packer, legRegion).crop().flipX();
                GraphicUtils.drawCenter(icon, flip);
                flip.dispose();

                icon.draw(GraphicUtils.get(packer, name), true);
            }

            for(Weapon weapon : weapons){
                if(weapon.name.isEmpty()) continue;
                if(weapon instanceof MultiBarrelWeapon m && outlined.add(weapon.name + "-barrel")){
                    outlSeparate.get(m.barrelRegion, "outline");
                }
                if((!weapon.top || bottomWeapons.contains(weapon.name))){
                    var out = GraphicUtils.get(packer, weapon.name + "-outline");
                    Pixmap pix = out.crop();

                    if(weapon.flipSprite){
                        var newPix = pix.flipX();
                        pix.dispose();
                        pix = newPix;
                    }

                    icon.draw(pix,
                        (int)(weapon.x / scl + icon.width / 2f - out.width / 2f),
                        (int)(-weapon.y / scl + icon.height / 2f - out.height / 2f),
                        true
                    );

                    if(weapon.mirror){
                        var mirror = pix.flipX();

                        icon.draw(mirror,
                            (int)(-weapon.x / scl + icon.width / 2f - out.width / 2f),
                            (int)(-weapon.y / scl + icon.height / 2f - out.height / 2f),
                            true
                        );

                        mirror.dispose();
                    }

                    pix.dispose();
                }
            }

            icon.draw(GraphicUtils.get(packer, name), true);
            int baseColor = Color.valueOf(color, "ffa665").rgba();

            Pixmap baseCell = atlas.getPixmap(cellRegion).crop();
            Pixmap cell = new Pixmap(cellRegion.width, cellRegion.height);
            cell.each((x, y) -> cell.setRaw(x, y, Color.muli(baseCell.getRaw(x, y), baseColor)));

            baseCell.dispose();
            icon.draw(cell, icon.width / 2 - cell.width / 2, icon.height / 2 - cell.height / 2, true);

            for(Weapon weapon : weapons){
                if(weapon.name.isEmpty()) continue;

                PixmapRegion wepReg = weapon.top ? GraphicUtils.get(packer, weapon.name + "-outline") : GraphicUtils.get(packer, weapon.region);
                Pixmap pix = wepReg.crop();

                if(weapon.flipSprite){
                    var newPix = pix.flipX();
                    pix.dispose();
                    pix = newPix;
                }

                icon.draw(pix,
                    (int)(weapon.x / scl + icon.width / 2f - weapon.region.width / 2f),
                    (int)(-weapon.y / scl + icon.height / 2f - weapon.region.height / 2f),
                    true
                );

                if(weapon.mirror){
                    var mirror = pix.flipX();

                    icon.draw(mirror,
                        (int)(-weapon.x / scl + icon.width / 2f - weapon.region.width / 2f),
                        (int)(-weapon.y / scl + icon.height / 2f - weapon.region.height / 2f),
                        true
                    );

                    mirror.dispose();
                }

                pix.dispose();
            }

            if(unit instanceof Copterc){
                Pixmap propellers = new Pixmap(icon.width, icon.height);
                Pixmap tops = new Pixmap(icon.width, icon.height);

                for(Rotor rotor : rotors){
                    PixmapRegion bladeSprite = GraphicUtils.get(packer, rotor.bladeRegion);

                    float bladeSeparation = 360f / rotor.bladeCount;

                    float propXCenter = (rotor.x / scl + icon.width / 2f) - 0.5f;
                    float propYCenter = (-rotor.y / scl + icon.height / 2f) - 0.5f;

                    float bladeSpriteXCenter = bladeSprite.width / 2f - 0.5f;
                    float bladeSpriteYCenter = bladeSprite.height / 2f - 0.5f;

                    int propWidth = propellers.width;
                    int propHeight = propellers.height;
                    for(int x = 0; x < propWidth; x++){
                        for(int y = 0; y < propHeight; y++){
                            for(int blade = 0; blade < rotor.bladeCount; blade++){
                                float deg = blade * bladeSeparation;
                                float cos = Mathf.cosDeg(deg);
                                float sin = Mathf.sinDeg(deg);
                                int col = GraphicUtils.getColor(
                                    bladeSprite, color,
                                    ((propXCenter - x) * cos + (propYCenter - y) * sin) / rotor.scale + bladeSpriteXCenter,
                                    ((propXCenter - x) * sin - (propYCenter - y) * cos) / rotor.scale + bladeSpriteYCenter
                                ).rgba();

                                propellers.setRaw(x, y, Pixmap.blend(
                                    propellers.getRaw(x, y),
                                    col
                                ));
                            }
                        }
                    }

                    PixmapRegion topSprite = GraphicUtils.get(packer, rotor.topRegion);
                    int topXCenter = (int)(rotor.x / scl + icon.width / 2f - topSprite.width / 2f);
                    int topYCenter = (int)(-rotor.y / scl + icon.height / 2f - topSprite.height / 2f);

                    var out = topSprite.crop();
                    tops.draw(out, topXCenter, topYCenter, true);

                    if(rotor.mirror){
                        propXCenter = (-rotor.x / scl + icon.width / 2f) - 0.5f;
                        topXCenter = (int)(-rotor.x / scl + icon.width / 2f - topSprite.width / 2f);

                        for(int x = 0; x < propWidth; x++){
                            for(int y = 0; y < propHeight; y++){
                                for(int blade = 0; blade < rotor.bladeCount; blade++){
                                    float deg = blade * bladeSeparation;
                                    float cos = Mathf.cosDeg(deg);
                                    float sin = Mathf.sinDeg(deg);
                                    int col = GraphicUtils.getColor(
                                        bladeSprite, color,
                                        ((propXCenter - x) * cos + (propYCenter - y) * sin) / rotor.scale + bladeSpriteXCenter,
                                        ((propXCenter - x) * sin - (propYCenter - y) * cos) / rotor.scale + bladeSpriteYCenter
                                    ).rgba();

                                    propellers.setRaw(x, y, Pixmap.blend(
                                        propellers.getRaw(x, y),
                                        col
                                    ));
                                }
                            }
                        }

                        tops.draw(out, topXCenter, topYCenter, true);
                    }

                    out.dispose();
                }

                Pixmap propOutlined = GraphicUtils.outline(propellers, outlineColor, outlineRadius);
                icon.draw(propOutlined, true);
                icon.draw(tops, true);

                propellers.dispose();
                tops.dispose();

                Pixmap payloadCell = new Pixmap(baseCell.width, baseCell.height);
                int cellCenterX = payloadCell.width / 2;
                int cellCenterY = payloadCell.height / 2;
                int propCenterX = propOutlined.width / 2;
                int propCenterY = propOutlined.height / 2;

                payloadCell.each((x, y) -> {
                    int cellX = x - cellCenterX;
                    int cellY = y - cellCenterY;

                    float alpha = color.set(propOutlined.get(cellX + propCenterX, cellY + propCenterY)).a;
                    payloadCell.setRaw(x, y, color.set(baseCell.getRaw(x, y)).mul(1, 1, 1, 1 - alpha).rgba());
                });

                propOutlined.dispose();

                packer.add(PageType.main, name + "-cell-payload", payloadCell);
            }

            packer.add(PageType.main, name + "-full", icon);
            Unity.print(Strings.format("Created icons for @", name));
        }catch(Throwable t){
            Unity.print(LogLevel.err, Strings.format("Couldn't create icons for @", name), Strings.getFinalCause(t));
        }finally{
            Pools.free(color);
        }
    }

    private int populateColorArray(int[] heightAverageColors, PixmapRegion bladeSprite, int halfHeight){
        Color
            c1 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f),
            c2 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f),
            c3 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f);
        float hits = 0;
        int length = 0;

        for(int y = halfHeight - 1; y >= 0; y--){
            for(int x = 0; x < bladeSprite.width; x++){
                bladeSprite.get(x, y, c2);

                if(c2.a > 0){
                    hits++;
                    c1.r += c2.r;
                    c1.g += c2.g;
                    c1.b += c2.b;
                }
            }

            if(hits > 0){
                c1.r = c1.r / hits;
                c1.g = c1.g / hits;
                c1.b = c1.b / hits;
                c1.a = 1f;

                length = Math.max(length, halfHeight - y);

                c1.clamp();
                c3.set(c1).a(0);
            }else{
                // Use color from previous row with alpha 0. This avoids alpha bleeding when interpolating later
                c1.set(c3);
            }

            heightAverageColors[halfHeight - y] = c1.rgba8888();
            c1.set(0f, 0f, 0f, 0f);

            hits = 0;
        }

        heightAverageColors[length + 1] = heightAverageColors[length] & 0xFF_FF_FF_00; // Set final entry to be fully transparent

        Pools.free(c1);
        Pools.free(c2);
        Pools.free(c3);

        return length;
    }

    // Instead of ACTUALLY accounting for the insanity that is the variation of rotor configurations
    // including counter-rotating propellers and that jazz, number 4 will be used instead.
    private void drawRadial(Pixmap sprite, int[] colorTable, int tableLimit){
        Color
            c1 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f),
            c2 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f);
        float spriteCenter = 0.5f - (sprite.height >> 1);

        sprite.each((x, y) -> {
            // 0.5f is required since mathematically it'll put the position at an intersection between 4 pixels, since the sprites are even-sized
            float positionLength = Mathf.len(x + spriteCenter, y + spriteCenter);

            if(positionLength < tableLimit){
                int arrayIndex = Mathf.clamp((int)positionLength, 0, tableLimit);
                float a = Mathf.cos(Mathf.atan2(x + spriteCenter, y + spriteCenter) * (4 << 1)) * 0.05f + 0.95f;
                a *= a;

                sprite.set(x, y,
                    GraphicUtils.colorLerp(
                        c1.rgba8888(colorTable[arrayIndex]),
                        c2.rgba8888(colorTable[arrayIndex + 1]), positionLength % 1f
                    ).mul(a, a, a, a * (1 - 0.5f / (tableLimit - positionLength + 0.5f)))
                );
            }else{
                sprite.set(x, y, c1.rgba8888(0x00_00_00_00));
            }
        });

        Pools.free(c1);
        Pools.free(c2);
    }

    // To help visualize the expected output of this algorithm:
    //   Divide the circle of the rotor's blade into rings, with a new ring every 4 pixels.
    // 	 Within each band exists a circumferential parallelogram, which the upper and bottom lines are offset differently.
    //   Entire parallelograms are offset as well.
    // The resulting drawing looks like a very nice swooshy hourglass. It must be anti-aliased afterwards.
    private void drawShade(Pixmap sprite, int length){
        float spriteCenter = 0.5f - (sprite.height >> 1);
        // Divide by 2 then round down to nearest even positive number. This array will be accessed by pairs, hence the even number size.
        float[] offsets = new float[length >> 2 & 0xEFFFFFFE];
        for(int i = 0; i < offsets.length; i++){
            // The output values of the noise functions from the noise class are awful that
            // every integer value always result in a 0. Offsetting by 0.5 results in delicious good noise.
            // The additional offset is only that the noise values close to origin make for bad output for the sprite.

            offsets[i] = (float)Noise.rawNoise(i + 2.5f);
        }

        Color c1 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f);
        sprite.each((x, y) -> {
            float positionLength = Mathf.len(x + spriteCenter, y + spriteCenter);

            int arrayIndex = Mathf.clamp((int)positionLength >> 2 & 0xEFFFFFFE, 0, offsets.length - 2);
            float offset = GraphicUtils.pythagoreanLerp(offsets[arrayIndex], offsets[arrayIndex + 1], (positionLength / 8f) % 1);

            float a = Mathf.sin(Mathf.atan2(x + spriteCenter, y + spriteCenter) + offset);
            a *= a; // Square the sine wave to make it all positive values
            a *= a; // Square sine again to thin out intervals of value increases
            a *= a; // Sine to the 8th power - Perfection
            // To maintain the geometric-sharpness, the resulting alpha fractional is rounded to binary integer.
            sprite.set(x, y, c1.rgb888(0xFF_FF_FF).a(Mathf.round(a) * Mathf.clamp(length - positionLength, 0f, 1f)));
        });

        Pools.free(c1);
    }
}
