package unity.entities.merge;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.annotations.Annotations.*;
import unity.gen.Expc.*;

@MergeComp
class TurretComp extends Turret{
    /** Color of shoot effects. Shifts to second color as the turret levels up. */
    Color fromColor = Pal.lancerLaser, toColor = Pal.sapBullet;

    float secRange = range;
    Color secColor;

    public TurretComp(String name){
        super(name);
    }

    class TurretBuildComp extends TurretBuild{
        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, secRange, secColor == null ? team.color : secColor);
        }

        @Override
        @Replace
        public void shoot(BulletType type){
            if(chargeTime > 0f){
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
                            if(!isValid()){
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
            }
        }

        @Override
        @Replace
        public void effects(){
            if(this instanceof ExpBuildc){
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

        public Color getShootColor(float progress){
            return Tmp.c1.set(fromColor).lerp(toColor, progress).cpy();
        }
    }
}
