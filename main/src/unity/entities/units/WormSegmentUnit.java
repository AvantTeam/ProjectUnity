package unity.entities.units;

import arc.math.*;
import arc.util.*;
import arc.struct.Seq;
import arc.graphics.g2d.*;
import mindustry.audio.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.entities.units.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.Effect;
import unity.content.UnityUnitTypes;
import unity.type.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class WormSegmentUnit extends UnitEntity{
    public UnityUnitType wormType;
    protected WormDefaultUnit trueParentUnit;
    protected Unit parentUnit;
    protected boolean isBugged;
    protected int shootSequence, segmentType;

    public int getSegmentLength(){
        return wormType.segmentLength;
    }

    @Override
    public void type(UnitType type){
        super.type(type);
        if(type instanceof UnityUnitType w) wormType = w;
        else throw new ClassCastException("you set this unit's type a in sneaky way");
    }

    @Override
    public boolean collides(Hitboxc other){
        if(trueParentUnit == null) return true;
        WormSegmentUnit[] segs = trueParentUnit.segmentUnits;
        for(int i = 0, len = getSegmentLength(); i < len; i++){
            if(segs[i] == other) return false;
        }
        return true;
    }

    @Override
    public void add(){
        if(added == true) return;
        isBugged = true;
        Groups.all.add(this);
        Groups.unit.add(this);
        Groups.sync.add(this);
        //Groups.draw.add(this);
        added = true;
        updateLastPosition();
    }

    @Override
    public void setType(UnitType type){
        this.type = type;
        maxHealth = type.health;
        drag = type.drag;
        armor = type.armor;
        hitSize = type.hitSize;
        hovering = type.hovering;

        if(controller == null) controller(type.createController());
        if(mounts().length != type.weapons.size) setupWeapons(type);
        if(type instanceof UnityUnitType w) wormType = w;
        else throw new ClassCastException("you set this unit's type in sneaky way");
    }

    @Override
    public void remove(){
        if(!added) return;
        Groups.all.remove(this);
        Groups.unit.remove(this);
        Groups.sync.remove(this);
        //Groups.draw.remove(this);
        added = false;
        controller.removed(this);
        if(net.client()) netClient.addRemovedEntity(id);
    }

    @Override
    public void damage(float amount){
        trueParentUnit.damage(amount);
    }

    @Override
    public void damage(float amount, boolean withEffect){
        trueParentUnit.damage(amount, withEffect);
    }

    @Override
    public void controller(UnitController next){
        if(!(next instanceof Player)){
            controller = next;
            if(controller.unit() != this) controller.unit(this);
        }else if(trueParentUnit != null){
            trueParentUnit.controller = next;
            if(trueParentUnit.controller.unit() != trueParentUnit) trueParentUnit.controller.unit(trueParentUnit);
        }
    }

    @Override
    public boolean isPlayer(){
        if(trueParentUnit == null) return false;
        return trueParentUnit.controller instanceof Player;
    }

    @Override
    public Player getPlayer(){
        if(trueParentUnit == null) return null;
        return isPlayer() ? (Player)trueParentUnit.controller : null;
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(1);
    }

    @Override
    public void heal(float amount){
        if(trueParentUnit != null) trueParentUnit.heal(amount);
        health += amount;
        clampHealth();
    }

    @Override
    public void kill(){
        if(dead || net.client()) return;
        if(trueParentUnit != null) Call.unitDeath(trueParentUnit.id);
        Call.unitDeath(id);
    }

    public void setSegmentType(int val){
        segmentType = val;
    }

    @Override
    public void setupWeapons(UnitType def){
        if(!(def instanceof UnityUnitType w)) super.setupWeapons(def);
        else{
            Seq<WeaponMount> tmpSeq = new Seq<>();
            Seq<Weapon> originSeq = w.segWeapSeq;
            for(int i = 0; i < originSeq.size; i++) tmpSeq.add(new WeaponMount(originSeq.get(i)));
            mounts = tmpSeq.toArray(WeaponMount.class);
        }
    }

    @Override
    public boolean serialize(){
        return false;
    }

    @Override
    public void update(){
        if(parentUnit == null || parentUnit.dead){
            dead = true;
            remove();
        }
        if(trueParentUnit != null && isBugged){
            if(!Structs.contains(trueParentUnit.segmentUnits, s -> s == this)) remove();
            else isBugged = false;
        }
    }

    public void wormSegmentUpdate(){
        if(trueParentUnit != null){
            health = trueParentUnit.health;
            maxHealth = trueParentUnit.maxHealth;
            hitTime = trueParentUnit.hitTime;
            ammo = trueParentUnit.ammo;
        }else{
            return;
        }
        if(team != trueParentUnit.team) team = trueParentUnit.team;
        if(!net.client() && !dead && controller != null) controller.updateUnit();
        if(controller == null || !controller.isValidController()) resetController();
        updateWeapon();
        updateStatus();
    }

    protected void updateStatus(){
        if(trueParentUnit == null || trueParentUnit.dead) return;
        if(!statuses.isEmpty()) statuses.each(s -> trueParentUnit.apply(s.effect, s.time));
        statuses.clear();
    }

    protected void updateWeapon(){
        boolean can = canShoot();
        for(WeaponMount mount : mounts){
            Weapon weapon = mount.weapon;
            mount.reload = Math.max(mount.reload - Time.delta * reloadMultiplier, 0);
            float weaponRotation = this.rotation - 90 + (weapon.rotate ? mount.rotation : 0);
            float mountX = this.x + Angles.trnsx(this.rotation - 90, weapon.x, weapon.y);
            float mountY = this.y + Angles.trnsy(this.rotation - 90, weapon.x, weapon.y);
            float shootX = mountX + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY);
            float shootY = mountY + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
            float shootAngle = weapon.rotate ? weaponRotation + 90 : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (this.rotation - angleTo(mount.aimX, mount.aimY));
            if(weapon.continuous && mount.bullet != null){
                if(!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != weapon.bullet){
                    mount.bullet = null;
                }else{
                    mount.bullet.rotation(weaponRotation + 90);
                    mount.bullet.set(shootX, shootY);
                    mount.reload = weapon.reload;
                    vel.add(Tmp.v1.trns(rotation + 180.0F, mount.bullet.type.recoil));
                    if(weapon.shootSound != Sounds.none && !headless){
                        if(mount.sound == null) mount.sound = new SoundLoop(weapon.shootSound, 1.0F);
                        mount.sound.update(x, y, true);
                    }
                }
            }else{
                mount.heat = Math.max(mount.heat - Time.delta * reloadMultiplier / mount.weapon.cooldownTime, 0);
                if(mount.sound != null){
                    mount.sound.update(x, y, false);
                }
            }
            if(weapon.otherSide != -1 && weapon.alternate && mount.side == weapon.flipSprite && mount.reload + Time.delta > weapon.reload / 2.0F && mount.reload <= weapon.reload / 2.0F){
                mounts[weapon.otherSide].side = !mounts[weapon.otherSide].side;
                mount.side = !mount.side;
            }
            if(weapon.rotate && (mount.rotate || mount.shoot) && can){
                float axisX = this.x + Angles.trnsx(this.rotation - 90, weapon.x, weapon.y);
                float axisY = this.y + Angles.trnsy(this.rotation - 90, weapon.x, weapon.y);
                mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - this.rotation;
                mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, weapon.rotateSpeed * Time.delta);
            }else if(!weapon.rotate){
                mount.rotation = 0;
                mount.targetRotation = angleTo(mount.aimX, mount.aimY);
            }
            if(mount.shoot && can && (ammo > 0 || !state.rules.unitAmmo || team().rules().infiniteAmmo) && (!weapon.alternate || mount.side == weapon.flipSprite) && (vel.len() >= mount.weapon.minShootVelocity || (net.active() && !isLocal())) && mount.reload <= 1.0E-4F && Angles.within(weapon.rotate ? mount.rotation : this.rotation, mount.targetRotation, mount.weapon.shootCone)){
                shoot(mount, shootX, shootY, mount.aimX, mount.aimY, mountX, mountY, shootAngle, Mathf.sign(weapon.x));
                mount.reload = weapon.reload;
                ammo--;
                if(ammo < 0) ammo = 0;
            }
        }
    }

    protected void shoot(WeaponMount mount, float x, float y, float aimX, float aimY, float mountX,
                       float mountY, float rotation, int side){
        Weapon weapon = mount.weapon;
        float baseX = this.x;
        float baseY = this.y;
        boolean delay = weapon.firstShotDelay + weapon.shotDelay > 0f;
        (delay ? weapon.chargeSound : weapon.continuous ? Sounds.none : weapon.shootSound).at(x, y, Mathf.random(weapon.soundPitchMin, weapon.soundPitchMax));
        BulletType ammo = weapon.bullet;
        float lifeScl = ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(x, y, aimX, aimY) / ammo.range()) : 1f;
        sequenceNum = 0;
        if (delay) {
            Angles.shotgun(weapon.shots, weapon.spacing, rotation, (f)->{
                Time.run(sequenceNum * weapon.shotDelay + weapon.firstShotDelay, ()->{
                    if (!isAdded()) return;
                    mount.bullet = bullet(weapon, x + this.x - baseX, y + this.y - baseY, f + Mathf.range(weapon.inaccuracy), lifeScl);
                });
                sequenceNum++;
            });
        } else {
            Angles.shotgun(weapon.shots, weapon.spacing, rotation, f -> mount.bullet = bullet(weapon, x, y, f + Mathf.range(weapon.inaccuracy), lifeScl));
        }
        boolean parentize = ammo.keepVelocity;
        if(delay){
            Time.run(weapon.firstShotDelay, () -> {
                if(!isAdded()) return;
                vel.add(Tmp.v1.trns(rotation + 180f, ammo.recoil));
                Effect.shake(weapon.shake, weapon.shake, x, y);
                mount.heat = 1f;
                if(!weapon.continuous){
                    weapon.shootSound.at(x, y, Mathf.random(weapon.soundPitchMin, weapon.soundPitchMax));
                }
            });
        }else{
            vel.add(Tmp.v1.trns(rotation + 180f, ammo.recoil));
            Effect.shake(weapon.shake, weapon.shake, x, y);
            mount.heat = 1f;
        }
        weapon.ejectEffect.at(mountX, mountY, rotation * side);
        ammo.shootEffect.at(x, y, rotation, parentize ? this : null);
        ammo.smokeEffect.at(x, y, rotation, parentize ? this : null);
        apply(weapon.shootStatus, weapon.shootStatusDuration);
    }

    protected Bullet bullet(Weapon weapon, float x, float y, float angle, float lifescl){
        return weapon.bullet.create(this, this.team, x, y, angle, 1.0f - weapon.velocityRnd + Mathf.random(weapon.velocityRnd), lifescl);
    }

    public void drawBody(){
        float z = Draw.z();
        type.applyColor(this);
        TextureRegion region = segmentType == 0 ? wormType.segmentRegion : wormType.tailRegion;
        Draw.rect(region, this, rotation - 90);
        TextureRegion segCellReg = wormType.segmentCellRegion;
        if(segmentType == 0 && segCellReg != atlas.find("error")) drawCell(segCellReg);
        TextureRegion outline = wormType.segmentOutline == null || wormType.tailOutline == null ? null : segmentType == 0 ? wormType.segmentOutline : wormType.tailOutline;
        if(outline != null){
            Draw.z(Draw.z() - UnitType.outlineSpace);
            Draw.rect(outline, this, rotation - 90f);
            Draw.z(z);
        }
        Draw.reset();
    }

    public void drawCell(TextureRegion cellRegion){
        Draw.color(type.cellColor(this));
        Draw.rect(cellRegion, x, y, rotation - 90f);
    }

    public void drawSoftShadow(){
        TextureRegion region = segmentType == 0 ? wormType.segmentRegion : wormType.tailRegion;
        Draw.color(Pal.shadow); //seems to not exist in v106
        float e = Math.max(elevation, type.visualElevation);
        Draw.rect(region, x + (UnitType.shadowTX * e), y + UnitType.shadowTY * e, rotation - 90f);
        Draw.color();
    }

    @Override
    public void draw(){

    }

    @Override
    public void collision(Hitboxc other, float x, float y){
        super.collision(other, x, y);
        if(trueParentUnit != null) trueParentUnit.handleCollision(this, other, x, y);
    }

    protected void setTrueParent(WormDefaultUnit parent){
        shootSequence = 0;
        trueParentUnit = parent;
    }

    public void setParent(Unit parent){
        parentUnit = parent;
    }
}
