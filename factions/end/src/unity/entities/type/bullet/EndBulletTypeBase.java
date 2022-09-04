package unity.entities.type.bullet;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.entities.type.bullet.effects.*;
import unity.gen.entities.*;
import unity.mod.*;

public abstract class EndBulletTypeBase extends BulletType{
    /** 0-1 */
    protected float percentileDamage = 0f;
    /** Percentile Damage starts if health is higher than this value */
    protected float percentileDamageStart = 900000f;
    /** Duration of bleed, Prevents/Limit regeneration */
    protected float bleedDuration = -1f;
    /** How much the collided unit can heal per 15 frames */
    protected float bleedLimit = 100f;
    /** If the targets health exceeds this value, damage starts increasing */
    protected float overDamage = 2500000f;
    protected float overDamageScl = 4000f;
    protected float overDamagePower = 2f;

    protected boolean pierceShields = false;

    protected EndBulletEffects[] modules;
    private float[] moduleDataTmp;

    public EndBulletTypeBase(float speed, float damage){
        super(speed, damage);
    }

    public EndBulletTypeBase(){
    }

    @Override
    public void init(){
        super.init();
        if(modules != null){
            moduleDataTmp = new float[modules.length];
        }
    }

    public void hitBuilding(Bullet b, Building build, float overrideDamage, boolean direct){
        if(build.health >= Float.MAX_VALUE || Float.isNaN(build.health) || build.health >= Float.POSITIVE_INFINITY){
            //AntiCheat.annihilateEntity(building, true);
            EndCurse.erase(build);
            return;
        }
        if(!collidesTiles) return;
        //boolean col = !(collidesTiles && collides);
        float mxh = Math.max(build.maxHealth, build.health);
        float pow = build.health > overDamage ? Mathf.pow((build.health - overDamage) / overDamageScl, overDamagePower) : 0f;
        float ratio = (mxh > percentileDamageStart && percentileDamage > 0) ? (percentileDamage * (mxh - percentileDamageStart)) : 0f;
        float damage = Math.max(ratio, (overrideDamage > 0f ? overrideDamage : (direct ? b.damage : 0f)) + pow);
        float h = build.health;

        if(bleedDuration > 0){
            EndCurse.bleed(build, bleedDuration, bleedLimit);
        }
        if(modules != null){
            for(EndBulletEffects mod : modules){
                mod.hitBuilding(build, b);
            }
        }

        if(damage > 0f){
            if(pierceShields){
                build.damagePierce(damage);
            }else{
                build.damage(damage);
            }

            EndCurse.bleedDamage(build.id, build.health);
            if(build.health >= h || EndCurse.contains(build.id)){
                EndCurse.notifyDamage(build, -(build.health - h));
            }
        }
    }

    public void hitUnit(Bullet b, Unit unit, float overrideDamage){
        float health = unit.health * unit.healthMultiplier;
        float mxh = Math.max(unit.maxHealth, unit.health) * unit.healthMultiplier;
        if(health >= Float.MAX_VALUE || Float.isNaN(health) || health >= Float.POSITIVE_INFINITY){
            //AntiCheat.annihilateEntity(unit, true);
            EndCurse.erase(unit);
            return;
        }
        float score = health + unit.type.dpsEstimate;
        float pow = score > overDamage ? Mathf.pow((score - overDamage) / overDamageScl, overDamagePower) : 0f;
        float ratio = (mxh > percentileDamageStart && percentileDamage > 0) ? (percentileDamage * (mxh - percentileDamageStart)) : 0f;
        float damage = Math.max(ratio, (overrideDamage > 0f ? overrideDamage : b.damage) + pow);
        float lh = unit.health + unit.shield;

        if(bleedDuration > 0){
            EndCurse.bleed(unit, bleedDuration, bleedLimit);
        }
        if(modules != null){
            int i = 0;
            for(EndBulletEffects mod : modules){
                moduleDataTmp[i] = mod.getData(unit);
                mod.hitUnit(unit, b);
                i++;
            }
            for(Ability ability : unit.abilities){
                for(EndBulletEffects mod : modules){
                    mod.handleAbility(ability, unit, b);
                }
            }
        }

        if(pierceShields){
            unit.damagePierce(damage);
        }else{
            unit.damage(damage);
        }

        Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
        if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
        unit.impulse(Tmp.v3);
        unit.apply(status, statusDuration);
        EndCurse.bleedDamage(unit.id, unit.health);

        float hd = (unit.health + unit.shield) - lh;
        if(hd >= 0f || EndCurse.contains(unit.id)){
            EndCurse.notifyDamage(unit, -hd);
        }

        if(modules != null){
            for(int i = 0; i < modules.length; i++){
                modules[i].handleUnitPost(unit, b, moduleDataTmp[i]);
            }
        }
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health){
        if(entity instanceof Unit){
            hitUnit(b, (Unit)entity, 0f);
            return;
        }
        super.hitEntity(b, entity, health);
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
        super.hitTile(b, build, x, y, initialHealth, direct);
        hitBuilding(b, build, 0f, false);
    }

    @Override
    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY){
        EndBullet bullet = EndBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        bullet.aimTile = Vars.world.tileWorld(aimX, aimY);
        bullet.aimX = aimX;
        bullet.aimY = aimY;
        bullet.initVel(angle, speed * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        if(owner instanceof Teamc){
            bullet.setTrueOwner((Teamc)owner);
        }
        //reset trail
        if(bullet.trail != null){
            bullet.trail.clear();
        }
        bullet.add();

        if(keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc)owner).vel());
        return bullet;
    }
}
