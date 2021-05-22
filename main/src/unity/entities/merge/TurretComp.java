package unity.entities.merge;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.LiquidTurret.*;
import unity.annotations.Annotations.*;
import unity.entities.bullet.exp.*;
import unity.gen.Expc.*;
import unity.gen.Turretc.*;

@SuppressWarnings({"unused", "unchecked"})
@MergeComponent
class TurretComp extends Turret{
    /** Color of shoot effects. Shifts to second color as the turret levels up. */
    Color fromColor = Pal.lancerLaser, toColor = Pal.sapBullet;
    boolean lerpColor = false;

    Color rangeColor;

    /** Whether to accept ammo type of all kinds */
    boolean omni = false;
    BulletType defaultBullet = Bullets.standardCopper;

    float basicFieldRadius = -1f;

    @ReadOnly Floatf<TurretBuildc> damageMultiplier = b -> 1f;
    @ReadOnly Func<TurretBuildc, Object> bulletData = b -> null;

    public TurretComp(String name){
        super(name);
    }

    public <T extends TurretBuildc> void damageMultiplier(Floatf<T> draw){
        this.damageMultiplier = (Floatf<TurretBuildc>)draw;
    }

    public <T extends TurretBuildc> void bulletData(Func<T, Object> update){
        this.bulletData = (Func<TurretBuildc, Object>)update;
    }

    public class TurretBuildComp extends TurretBuild{
        @Override
        @Replace
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(self() instanceof LiquidTurretBuild && omni){
                return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
            }else{
                return super.acceptLiquid(source, liquid);
            }
        }

        @Override
        @Replace
        public BulletType useAmmo(){
            if(self() instanceof LiquidTurretBuild && omni){
                BulletType b = peekAmmo();
                liquids.remove(liquids.current(), 1f / b.ammoMultiplier);

                return b;
            }else{
                return super.useAmmo();
            }
        }

        @Override
        @Replace
        public BulletType peekAmmo(){
            if(block instanceof LiquidTurret l && omni){
                BulletType b = l.ammoTypes.get(liquids.current());
                if(basicFieldRadius > 0f && b instanceof ExpLaserFieldBulletType type){
                    type.basicFieldRadius = basicFieldRadius;
                }

                return b == null ? defaultBullet : b;
            }else{
                return super.peekAmmo();
            }
        }

        @Override
        @Replace
        public boolean hasAmmo(){
            if(self() instanceof LiquidTurretBuild && omni){
                return liquids.total() >= 1f / peekAmmo().ammoMultiplier;
            }else{
                return super.hasAmmo();
            }
        }

        @Override
        @Replace
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, rangeColor == null ? team.color : rangeColor);
        }

        @Override
        @Replace
        public void shoot(BulletType type){
            if(chargeTime < 0f){
                super.shoot(type);
            }else if(this instanceof ExpBuildc){
                var exp = (TurretBuild & ExpBuildc)this;

                useAmmo();
                float lvl = exp.levelf();

                tr.trns(rotation, shootLength);
                chargeBeginEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(lvl));
                chargeSound.at(x + tr.x, y + tr.y, 1f);

                for(int i = 0; i < chargeEffects; i++){
                    Time.run(Mathf.random(chargeMaxDelay), () -> {
                        if(isValid()){
                            tr.trns(rotation, shootLength);
                            chargeEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(lvl));
                        }
                    });
                }

                charging = true;

                for(var i = 0; i < shots; i++){
                    Time.run(burstSpacing * i, () -> {
                        Time.run(chargeTime, () -> {
                            if(isValid()){
                                tr.trns(rotation, shootLength, Mathf.range(xRand));
                                recoil = recoilAmount;
                                heat = 1f;

                                bullet(type, rotation + Mathf.range(inaccuracy));
                                effects();
                                charging = false;
                            }
                        });
                    });
                }
            }else{
                super.shoot(type);
            }
        }

        @Override
        @Replace
        public void effects(){
            if(this instanceof ExpBuildc && lerpColor){
                var exp = (TurretBuild & ExpBuildc)this;

                recoil = recoilAmount;
                Effect shoot = shootEffect == Fx.none ? peekAmmo().shootEffect : shootEffect;
                Effect smoke = smokeEffect == Fx.none ? peekAmmo().smokeEffect : smokeEffect;

                shoot.at(x + tr.x, y + tr.y, rotation, getShootColor(exp.levelf()));
                smoke.at(x + tr.x, y + tr.y, rotation);
                shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));

                if(shootShake > 0){
                    Effect.shake(shootShake, shootShake, this);
                }
            }else{
                super.effects();
            }
        }

        @Override
        @Replace
        protected void bullet(BulletType type, float angle){
            type.create(
                this, team,
                x + tr.x, y + tr.y, angle,
                type.damage * damageMultiplier(), 1f + Mathf.range(velocityInaccuracy),
                type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f,
                bulletData()
            );
        }

        public float damageMultiplier(){
            return damageMultiplier.get(self());
        }

        public Object bulletData(){
            return bulletData.get(self());
        }

        public Color getShootColor(float progress){
            return Tmp.c1.set(fromColor).lerp(toColor, progress).cpy();
        }
    }
}
