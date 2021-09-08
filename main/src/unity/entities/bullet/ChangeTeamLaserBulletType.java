package unity.entities.bullet;

import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;

public class ChangeTeamLaserBulletType extends ContinuousLaserBulletType{
    public float minimumHealthPercent = 0.125f;
    public float minimumHealthOverride = 90f;
    public float ownerDamageRatio = 0.5f;
    public boolean convertUnits = true;
    public boolean convertBlocks = true;
    public StatusEffect conversionStatusEffect = StatusEffects.none;

    public ChangeTeamLaserBulletType(float damage){
        super(damage);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
        super.hitEntity(b, other, initialHealth);
        if(!(other instanceof Teamc t && other instanceof Statusc s)) return;
        if(convertUnits && (s.healthf() <= minimumHealthPercent || s.health() < minimumHealthOverride)){
            t.team(b.team);
            damageOwner(b, initialHealth * ownerDamageRatio);
            s.apply(conversionStatusEffect);
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct){
        super.hitTile(b, build, initialHealth, direct);
        if(convertBlocks && (build.healthf() <= minimumHealthPercent || build.health < minimumHealthOverride)){
            build.team(b.team);
            damageOwner(b, initialHealth * ownerDamageRatio);
        }
    }

    void damageOwner(Bullet b, float damage){
        if(damage == 0) return;
        if(b.owner instanceof Healthc h){
            if(damage < 0){
                h.heal(Math.abs(damage));
                return;
            }
            if(h.health() - damage > 1f || h.health() < h.maxHealth() / 2f){
                h.damage(damage);
            }else{
                h.health(1f);
            }
        }
    }
}
