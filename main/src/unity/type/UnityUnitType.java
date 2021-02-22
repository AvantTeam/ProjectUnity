package unity.type;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.entities.*;
import unity.entities.comp.*;
import unity.entities.units.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/*unit.entities.units might be gradually deleted.
note that as classes are integrated, inner classes are extracted.*/
public class UnityUnitType extends UnitType{
    public final Seq<Weapon> segWeapSeq = new Seq<>();

    public TextureRegion segmentRegion, tailRegion, segmentCellRegion, segmentOutline, tailOutline, legBackRegion, legBaseBackRegion, footBackRegion;
    public TextureRegion[] abilityRegions = new TextureRegion[AbilityTextures.values().length];
    public Seq<String> bottomWeapons = new Seq<>();
    // worms
    public int segmentLength = 9;
    public float segmentOffset = 23f;
    public float angleLimit = 30f;
    public float regenTime = -1f;
    public float segmentDamageScl = 6f;
    //hopefully make segment movement more consistant.
    public boolean counterDrag = false;
        // don't touch, please
    public TextureRegion bodyRegion, bodyOutlineRegion, tailOutlineRegion;
    public boolean splittable = false;
    public float headDamage = 0f;
    public float headTimer = 5f;

    // transforms
    public Prov<UnitType> toTrans;
    public float transformTime;

    // copters
    public final Seq<Rotor> rotors = new Seq<>(4);
    public float rotorDeathSlowdown = 0.01f;
    public float fallRotateSpeed = 2.5f;

    // legs extra
    protected static Vec2 legOffsetB = new Vec2();

    public boolean customBackLegs = false;

    public UnityUnitType(String name){
        super(name);
    }

    @Override
    public Unit create(Team team){
        Unit ret = super.create(team);
        //transformer
        if(ret instanceof TransformerBase transformer) transformer.setTimeTrans(transformTime);
        return ret;
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
        //abilities
        for(AbilityTextures type : AbilityTextures.values()){
            abilityRegions[type.ordinal()] = atlas.find(name + "-" + type.name());
        }

        //please do not the touch
        bodyRegion = atlas.find(name + "-body");
        bodyOutlineRegion = atlas.find(name + "-body-outline");
        tailOutlineRegion = atlas.find(name + "-tail-outline");

        segWeapSeq.each(Weapon::load);
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
                copy.rotOffset += 180f; //might change later

                mapped.add(copy);
            }
        });

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

    @Override
    public void draw(Unit unit){
        super.draw(unit);

        // copter
        if(unit instanceof Copterc){
            drawRotors(unit);
        }
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
        if(unit instanceof Wormc){
            var worm = (Unit & Wormc)unit;

            Draw.color(Pal.shadow);
            float e = Math.max(unit.elevation, visualElevation);

            if(worm.isHead()){
                Draw.rect(region, worm.x + shadowTX * e, worm.y + shadowTY * e, worm.rotation - 90);
            }else if(worm.isTail()){
                Draw.rect(tailRegion, worm.x + shadowTX * e, worm.y + shadowTY * e, worm.rotation - 90);
            }else{
                Draw.rect(bodyRegion, worm.x + shadowTX * e, worm.y + shadowTY * e, worm.rotation - 90);
            }
        }else{
            super.drawShadow(unit);
        }

        if(unit instanceof WormDefaultUnit wormUnit) wormUnit.drawShadow();
    }

    @Override
    public void drawSoftShadow(Unit unit){
        super.drawSoftShadow(unit);
        //worm
        if(!(unit instanceof WormDefaultUnit wormUnit)) return;
        for(WormSegmentUnit s : wormUnit.segmentUnits){
            wormUnit.type.drawSoftShadow(s);
        }
    }

    @Override
    public void drawBody(Unit unit){
        float z = Draw.z();

        if(unit instanceof Wormc){
            var worm = (Unit & Wormc)unit;

            applyColor(unit);

            Draw.z(1.01f + z + worm.layer());
            if(worm.isHead()){
                Draw.rect(region, worm.x, worm.y, worm.rotation - 90);
            }else if(worm.isTail()){
                Draw.rect(tailRegion, worm.x, worm.y, worm.rotation - 90);
            }else{
                Draw.rect(bodyRegion, worm.x, worm.y, worm.rotation - 90);
            }

            Draw.reset();
        }else{
            super.drawBody(unit);
        }

        //worm
        if(unit instanceof WormDefaultUnit wormUnit){
            for(int i = 0; i < wormUnit.segmentUnits.length; i++){
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

        Draw.z(z);
    }

    @Override
    public void drawEngine(Unit unit){
        float z = Draw.z();

        if(unit instanceof Wormc worm){
            if(worm.isHead() && worm.child() == null){
                Draw.z(1.01f + z + worm.layer());

                super.drawEngine(unit);
                Draw.z(z);
            }
        }else{
            super.drawEngine(unit);
        }
    }

    @Override
    public void drawWeapons(Unit unit){
        float z = Draw.z();

        if(unit instanceof Wormc worm){
            Draw.z(1.01f + z + worm.layer());
        }

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
                if(!weapon.top || found) Draw.z(zB - outlineSpace);

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

    @Override
    public void drawCell(Unit unit){
        float z = Draw.z();

        if(unit instanceof Wormc worm){
            Draw.z(1.01f + z + worm.layer());
        }

        super.drawCell(unit);
        Draw.z(z);
    }

    @Override
    public void drawOutline(Unit unit){
        float z = Draw.z();
        Draw.reset();

        if(unit instanceof Wormc){
            var worm = (Unit & Wormc)unit;

            Draw.z(1.01f + z + worm.layer());
            if(worm.isHead()){
                Draw.rect(outlineRegion, worm.x, worm.y, worm.rotation - 90);
            }else if(worm.isTail()){
                Draw.rect(tailOutlineRegion, worm.x, worm.y, worm.rotation - 90);
            }else{
                Draw.rect(bodyOutlineRegion, worm.x, worm.y, worm.rotation - 90);
            }
        }else{
            super.drawOutline(unit);
        }

        Draw.z(z);
    }

    public void drawRotors(Unit unit){
        Draw.mixcol(Color.white, unit.hitTime);
        for(Rotor rotor : rotors){
            float offX = Angles.trnsx(unit.rotation - 90, rotor.x, rotor.y);
            float offY = Angles.trnsy(unit.rotation - 90, rotor.x, rotor.y);

            float w = rotor.bladeRegion.width * rotor.scale * Draw.scl;
            float h = rotor.bladeRegion.height * rotor.scale * Draw.scl;

            for(int i = 0; i < 2; i++){
                for(int j = 0; j < rotor.bladeCount; j++){
                    float angle = (unit.rotation + (unit.id * 24f + (((Copterc) unit).rotorRot().get(rotors.indexOf(rotor))) + (360f / (float) rotor.bladeCount) * j + rotor.rotOffset)) % 360;
                    Draw.alpha(state.isPaused() || unit.dead() ? 1f : Time.time % 2);

                    Draw.rect(i == 0 ? rotor.bladeOutlineRegion : rotor.bladeRegion, unit.x + offX, unit.y + offY, w, h, angle);
                }
            }

            Draw.alpha(1f);
            Draw.rect(rotor.topRegion, unit.x + offX, unit.y + offY, unit.rotation - 90f);
        }

        Draw.mixcol();
    }
}
