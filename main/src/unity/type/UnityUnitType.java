package unity.type;

import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import unity.entities.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.util.*;

import static arc.Core.*;

/*unit.entities.units might be gradually deleted.
note that as classes are integrated, inner classes are extracted.*/
public class UnityUnitType extends UnitType{
    public final Seq<Weapon> segWeapSeq = new Seq<>();
    public Color outlineColor = Pal.darkerMetal;

    public TextureRegion segmentRegion, tailRegion, segmentCellRegion, segmentOutline, tailOutline, legBackRegion, legBaseBackRegion, footBackRegion, legMiddleRegion, payloadCellRegion;
    public TextureRegion[] abilityRegions = new TextureRegion[AbilityTextures.values().length];
    public ObjectSet<String> bottomWeapons = new ObjectSet<>();
    // Worms
    public int segmentLength = 9, maxSegments = -1;
    public float segmentOffset = 23f, headOffset = 0f;
    public float angleLimit = 30f;
    public float regenTime = -1f, healthDistribution = 0.1f;
    public float segmentDamageScl = 6f;
    // Hopefully make segment movement more consistent
    public boolean counterDrag = false;
    public boolean splittable = false, chainable = false;
    public Sound splitSound = Sounds.door, chainSound = Sounds.door;
    public float headDamage = 0f;
    public float headTimer = 5f;

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

    // Lasers
    public Color laserColor = Pal.heal;
    public TextureRegion laserRegion, laserEndRegion;
    public float laserWidth = 0.6f;

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

    public UnityUnitType(String name){
        super(name);
        outlines = false;
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
        segmentCellRegion = atlas.find(name + "-segment-cell");
        tailRegion = atlas.find(name + "-tail");
        segmentOutline = atlas.find(name + "-segment-outline");
        tailOutline = atlas.find(name + "-tail-outline");
        legBackRegion = atlas.find(name + "-leg-back");
        legBaseBackRegion = atlas.find(name + "-leg-base-back");
        footBackRegion = atlas.find(name + "-foot-back");
        legMiddleRegion = atlas.find(name + "-leg-middle", legRegion);
        payloadCellRegion = atlas.find(name + "-cell-payload", cellRegion);
        laserRegion = atlas.find("laser");
        laserEndRegion = atlas.find("laser-end");
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

        TextureRegion tmp = region, tmpOutline = outlineRegion;
        if(!unit.isHead() || unit.isTail()){
            region = unit.isTail() ? tailRegion : segmentRegion;
            outlineRegion = unit.isTail() ? tailOutline : segmentOutline;
        }

        drawOutline(unit);
        drawWeaponOutlines(unit);

        if(unit.isTail()){
            Draw.draw(z, () -> {
                Tmp.v1.trns(unit.rotation + 180f, segmentOffset).add(unit);
                Drawf.construct(Tmp.v1.x, Tmp.v1.y, tailRegion, unit.rotation - 90f, unit.regenTime() / regenTime, 1f, unit.regenTime());
            });
        }

        drawBody(unit);

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
            Weapon weapon = mount.weapon;
            boolean found = bottomWeapons.contains(weapon.name);

            float rotation = unit.rotation - 90;
            float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
            float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
            wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);

            float zC = Draw.z();
            if(found) Draw.z(zC - 0.005f);

            if(weapon.shadow > 0){
                Drawf.shadow(wx, wy, weapon.shadow);
            }

            if(weapon.outlineRegion.found()){
                float zB = Draw.z();
                if(!weapon.top || found) Draw.z(Math.min(zB - 0.01f, 99f));

                Draw.rect(weapon.outlineRegion,
                wx, wy,
                weapon.outlineRegion.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
                weapon.region.height * Draw.scl,
                weaponRotation);

                Draw.z(zB);
            }

            Draw.rect(weapon.region,
            wx, wy,
            weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
            weapon.region.height * Draw.scl,
            weaponRotation);

            if(weapon.heatRegion.found() && mount.heat > 0){
                Draw.color(weapon.heatColor, mount.heat);
                Draw.blend(Blending.additive);
                Draw.rect(weapon.heatRegion,
                wx, wy,
                weapon.heatRegion.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
                weapon.heatRegion.height * Draw.scl,
                weaponRotation);
                Draw.blend();
                Draw.color();
            }
            Draw.z(zC);
        }

        Draw.reset();
        Draw.z(z);
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
}
