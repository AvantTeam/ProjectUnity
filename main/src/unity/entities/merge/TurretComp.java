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
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.LaserTurret.*;
import mindustry.world.blocks.defense.turrets.LiquidTurret.*;
import unity.annotations.Annotations.*;
import unity.gen.Expc.*;
import unity.gen.*;
import unity.gen.Turretc.*;

//this is as if disappointment is interpreted through codes
@SuppressWarnings({"unused", "unchecked"})
@MergeComponent
abstract class TurretComp extends Turret implements Stemc{
    /** Color of shoot effects. Shifts to second color as the turret levels up. */
    Color fromColor = Pal.lancerLaser, toColor = Pal.sapBullet;
    boolean lerpColor = false;

    Color rangeColor;

    /** Whether to accept ammo type of all kinds */
    boolean omni = false;
    BulletType defaultBullet = Bullets.placeholder;

    float basicFieldRadius = -1f;

    @ReadOnly Func<TurretBuildc, Object> bulletData = b -> null;
    @ReadOnly Cons2<BulletType, Bullet> bulletCons = (type, b) -> {};

    public TurretComp(String name){
        super(name);
    }

    public <T extends TurretBuildc> void bulletData(Func<T, Object> bulletData){
        this.bulletData = (Func<TurretBuildc, Object>)bulletData;
    }

    public <T extends BulletType> void bulletCons(Cons2<T, Bullet> bulletCons){
        this.bulletCons = (Cons2<BulletType, Bullet>)bulletCons;
    }

    public abstract class TurretBuildComp extends TurretBuild implements StemBuildc{
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
                return b == null ? defaultBullet : b;
            }else{
                return super.peekAmmo();
            }
        }

        @Override
        @Replace
        public boolean hasAmmo(){
            if(self() instanceof LiquidTurretBuild && omni){
                return liquids.currentAmount() >= 1f / peekAmmo().ammoMultiplier;
            }else{
                return super.hasAmmo();
            }
        }

        @Override
        @Replace
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, rangeColor == null ? team.color : rangeColor);
        }

        /*
        @Override
        @Replace
        public void shoot(BulletType type){
            if(chargeTime > 0f && this instanceof ExpBuildc){
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
                    Time.run(burstSpacing * i, () -> Time.run(chargeTime, () -> {
                        if(isValid()){
                            tr.trns(rotation, shootLength, Mathf.range(xRand));
                            recoil = recoilAmount;
                            heat = 1f;

                            bullet(type, rotation + Mathf.range(inaccuracy));
                            effects();
                            charging = false;
                        }
                    }));
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
            Bullet bullet = type.create(
                this, team,
                x + tr.x, y + tr.y, angle,
                type.damage, 1f + Mathf.range(velocityInaccuracy),
                type.scaleLife ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f,
                bulletData()
            );

            bulletCons(type, bullet);
            if(self() instanceof LaserTurretBuild laser && block instanceof LaserTurret turret){
                laser.bullet = bullet;
                laser.bulletLife = turret.shootDuration;
            }
        }
        */

        public Object bulletData(){
            return bulletData.get(self());
        }

        public void bulletCons(BulletType type, Bullet b){
            bulletCons.get(type, b);
        }

        public Color getShootColor(float progress){
            return Tmp.c1.set(fromColor).lerp(toColor, progress).cpy();
        }
    }
}
