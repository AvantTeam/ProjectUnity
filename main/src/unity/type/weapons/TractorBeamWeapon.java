package unity.type.weapons;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.audio.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class TractorBeamWeapon extends Weapon{
    public float pullStrength = 10f, scaledForce = 1f;
    public float beamWidth = 0.75f;
    public boolean includeDead = false;

    public TextureRegion laser, laserEnd, laserTop, laserTopEnd;

    public Color laserColor = Pal.lancerLaser, laserTopColor = Color.white;

    public TractorBeamWeapon(String name){
        super(name);
        reload = 1f;
        predictTarget = false;
        autoTarget = true;
        controllable = false;
        rotate = true;
        useAmmo = false;
        recoil = -3f;
        shootSound = Sounds.tractorbeam;
        alternate = false;
        mountType = TractorBeamMount::new;
    }

    @Override
    public void load(){
        super.load();

        laser = Core.atlas.find("laser-white");
        laserEnd = Core.atlas.find("laser-white-end");
        laserTop = Core.atlas.find("laser-top");
        laserTopEnd = Core.atlas.find("laser-top-end");
    }

    @Override
    public void addStats(UnitType u, Table t){
        t.row();
        String n = scaledForce != 0f ? pullStrength + "-" + (pullStrength + scaledForce) : String.valueOf(pullStrength);
        t.add("[lightgray]" + Core.bundle.get("stat.unity.pullstrength") + "[white]" + n);
        if(bullet.damage > 0f){
            t.row();
            t.add(Core.bundle.format("bullet.damage", bullet.damage));
        }
        if(bullet.status != null && bullet.status != StatusEffects.none){
            t.row();
            t.add((bullet.minfo.mod == null ? bullet.status.emoji() : "") + "[stat]" + bullet.status.localizedName);
        }
    }

    @Override
    public void update(Unit unit, WeaponMount mount){
        super.update(unit, mount);
        TractorBeamMount tm = (TractorBeamMount)mount;

        float weaponRotation = unit.rotation - 90,
        wx = unit.x + Angles.trnsx(weaponRotation, x, y),
        wy = unit.y + Angles.trnsy(weaponRotation, x, y);
        if(mount.target != null && Angles.within(unit.rotation + mount.rotation, mount.target.angleTo(wx, wy) + 180f, shootCone)){
            tm.targetP.set(mount.target);
            if(mount.target instanceof Unit u){
                tm.scl = Mathf.lerpDelta(tm.scl, 1f, 0.07f);
                float scl = tm.scl * (pullStrength + (Mathf.clamp(1f - (Mathf.dst(wx, wy, tm.targetP.x, tm.targetP.y) / bullet.range)) * scaledForce)),
                ang = mount.target.angleTo(wx, wy);

                u.impulseNet(Tmp.v1.trns(ang, scl));
                unit.impulseNet(Tmp.v1.scl(-1f));
                if((tm.timer += Time.delta) >= 5f){
                    u.damage(bullet.damage);
                    tm.timer = 0f;
                }
                u.apply(bullet.status, bullet.statusDuration);
            }
        }else{
            tm.scl = Mathf.lerpDelta(tm.scl, 0f, 0.07f);
        }
        if(tm.scl > 0.01f && !Vars.headless){
            if(mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
            mount.sound.update(wx, wy, true);
        }else{
            if(mount.sound != null) mount.sound.update(wx, wy, false);
        }
        mount.reload = tm.scl * reload;
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground){
        return Units.closestTarget(unit.team, x, y, range + Math.abs(shootY), u -> u.checkTarget(air, ground), t -> false);
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range){
        return (super.checkTarget(unit, target, x, y, range) && (!(target instanceof Healthc h) || !target.isAdded() || (h.dead() && !includeDead))) || target instanceof Building;
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){

    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);
        TractorBeamMount tm = (TractorBeamMount)mount;

        if(tm.scl > 0.01f){
            float z = Draw.z(),
            weaponRotation = unit.rotation - 90,
            wx = unit.x + Angles.trnsx(weaponRotation, x, y),
            wy = unit.y + Angles.trnsy(weaponRotation, x, y),
            ox = wx + Angles.trnsx(unit.rotation + mount.rotation, shootY),
            oy = wy + Angles.trnsy(unit.rotation + mount.rotation, shootY);

            Draw.z(Layer.flyingUnit + 1f);
            Draw.color(laserColor);
            Drawf.laser(laser, laserEnd, ox, oy, tm.targetP.x, tm.targetP.y, tm.scl * beamWidth);
            Draw.z(Layer.flyingUnit + 1.1f);
            Draw.color(laserTopColor);
            Drawf.laser(laserTop, laserTopEnd, ox, oy, tm.targetP.x, tm.targetP.y, tm.scl * beamWidth);
            Draw.z(z);
        }
    }

    public static class TractorBeamMount extends WeaponMount{
        Vec2 targetP = new Vec2();
        float scl, timer;

        TractorBeamMount(Weapon weapon){
            super(weapon);
        }
    }
}
