package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.gen.*;

public class InvisibleUnitType extends UnityUnitType{
    public Color tint = Color.red;

    public InvisibleUnitType(String name){
        super(name);
    }

    protected float fade(Invisiblec unit){
        float minimum = Vars.player.team() == unit.team() ? 0.1f : 0.01f;
        return Mathf.clamp(1f - unit.alphaLerp(), minimum, 1f);
    }

    @Override
    public void drawOutline(Unit unit){
        if(!(unit instanceof Invisiblec e)){
            super.drawOutline(unit);
            return;
        }
        Tmp.c1.set(Color.white).lerp(tint, Mathf.lerp(0f, 0.5f, e.alphaLerp()));
        Draw.color(Tmp.c1);
        Draw.alpha(1f - e.alphaLerp());

        if(Core.atlas.isFound(outlineRegion)){
            Draw.rect(outlineRegion, unit.x, unit.y, unit.rotation - 90);
        }
    }

    @Override
    public Color cellColor(Unit unit){
        if(unit instanceof Invisiblec e) return super.cellColor(unit).a(fade(e));
        return super.cellColor(unit);
    }

    @Override
    public void drawEngine(Unit unit){
        if(!unit.isFlying()) return;

        float scale = unit.elevation;
        float offset = engineOffset/2f + engineOffset/2f*scale;

        if(unit instanceof Trailc){
            Trail trail = ((Trailc)unit).trail();
            trail.draw(unit.team.color, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * scale) * trailScl);
        }

        Draw.color(unit.team.color);
        if(unit instanceof Invisiblec e) Draw.alpha(fade(e));
        Fill.circle(
        unit.x + Angles.trnsx(unit.rotation + 180, offset),
        unit.y + Angles.trnsy(unit.rotation + 180, offset),
        (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) * scale
        );
        Draw.color(Color.white);
        if(unit instanceof Invisiblec e) Draw.alpha(fade(e));
        Fill.circle(
        unit.x + Angles.trnsx(unit.rotation + 180, offset - 1f),
        unit.y + Angles.trnsy(unit.rotation + 180, offset - 1f),
        (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) / 2f  * scale
        );
        Draw.color();
    }

    @Override
    public void drawSoftShadow(Unit unit){
        if(!(unit instanceof Invisiblec e)){
            super.drawSoftShadow(unit);
            return;
        }
        Draw.color(0, 0, 0, 0.4f * fade(e));
        float rad = 1.6f;
        float size = Math.max(region.width, region.height) * Draw.scl;
        Draw.rect(softShadowRegion, unit, size * rad, size * rad);
        Draw.color();
    }

    @Override
    public void drawShadow(Unit unit){
        if(!(unit instanceof Invisiblec e)){
            super.drawShadow(unit);
            return;
        }

        Draw.color(Pal.shadow);
        Draw.alpha(Pal.shadow.a * fade(e));
        float el = Math.max(unit.elevation, visualElevation);
        Draw.rect(shadowRegion, unit.x + shadowTX * el, unit.y + shadowTY * el, unit.rotation - 90);
        Draw.color();
    }

    @Override
    public void drawLight(Unit unit){
        if(!(unit instanceof Invisiblec e)){
            super.drawLight(unit);
            return;
        }
        if(lightRadius > 0){
            Drawf.light(unit.team, unit.x, unit.y, lightRadius, lightColor, lightOpacity * (1f - e.alphaLerp()));
        }
    }

    @Override
    public void drawWeapons(Unit unit){
        float z = Draw.z();

        //super.drawWeapons(unit);
        //applyColor(unit);
        for(WeaponMount mount : unit.mounts){
            Weapon weapon = mount.weapon;
            boolean found = bottomWeapons.contains(weapon);

            float rotation = unit.rotation - 90;
            float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
            float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
            wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);

            float zC = Draw.z();
            if(found) Draw.z(zC - 0.005f);

            if(weapon.shadow > 0){
                float fade = 1f;
                if(unit instanceof Invisiblec e) fade = fade(e);
                Drawf.shadow(wx, wy, weapon.shadow, fade);
            }

            boolean outlineFound = weapon.outlineRegion.found();
            applyColor(unit);
            if(outlineFound){
                float zB = Draw.z();
                if(!weapon.top || found) Draw.z(zB/* - outlineSpace*/);

                Draw.rect(weapon.outlineRegion,
                wx, wy,
                weapon.outlineRegion.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
                weapon.region.height * Draw.scl,
                weaponRotation);

                Draw.z(zB);
            }

            if(unit instanceof Invisiblec e && outlineFound) Draw.alpha(1f - e.alphaLerp());
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
    public void applyColor(Unit unit){
        if(!(unit instanceof Invisiblec e)){
            super.applyColor(unit);
            return;
        }
        //float lerp = Mathf.lerp(1f, 0.1f, e.alphaLerp);
        float lerp = fade(e);
        Tmp.c1.set(Color.white).lerp(tint, Mathf.lerp(0f, 0.5f, e.alphaLerp()));
        Draw.color(Tmp.c1);
        Draw.alpha(lerp);
        Draw.mixcol(Color.white, unit.hitTime);
        if(unit.drownTime > 0 && unit.floorOn().isDeep()){
            Draw.mixcol(unit.floorOn().mapColor, unit.drownTime * 0.8f);
        }
    }
}
