package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;

public class RampupPowerTurret extends PowerTurret{
    public float barBaseY, barLength, barStroke = 1.5f;
    public Color[] barColors = {Color.valueOf("00d9ff"), Color.valueOf("ccffff")};
    public float maxSpeedMul = 13f, speedInc = 0.2f, speedDec = 0.05f, accInc = 4f;
    public boolean lightning;
    public Color lightningColor = Color.valueOf("a9d8ff");
    public int baseLightningLength, lightningLengthDec;
    public float lightningThreshold, baseLightningDamage, lightningDamageDec;

    public TextureRegion topRegion;

    protected Vec2 tr3 = new Vec2();
    
    public RampupPowerTurret(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    public class RampupPowerTurretBuild extends PowerTurretBuild{
        public float speed = 1f;

        @Override
        public void updateTile(){
            if(!isShooting() || !consValid()){
                changeSpeed(-speedDec * Time.delta);
            }
            super.updateTile();
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            
            Draw.z(Layer.turret);

            tr2.trns(rotation, -recoil);

            Drawf.shadow(region, x + tr2.x - elevation, y + tr2.y - elevation, rotation - 90);
            
            Draw.rect(region, x + tr2.x, y + tr2.y, rotation - 90);

            if(speed > 1.001f){
                tr3.trns(rotation, -recoil + barBaseY);
                Draw.color(barColors[0], barColors[1], heat);
                Lines.stroke(barStroke);
                Lines.lineAngle(x + tr3.x, y + tr3.y, rotation, speedf() * barLength, false);
                Draw.reset();
            }

            Draw.rect(topRegion, x + tr2.x, y + tr2.y, rotation - 90);

            if(heatRegion != Core.atlas.find("error")){
                heatDrawer.get(this);
            }
        }

        @Override
        public void shoot(BulletType type){
            changeSpeed(speedInc);
            
            //when charging is enabled, use the charge shoot pattern
            if(chargeTime > 0){
                useAmmo();

                tr.trns(rotation, shootLength);
                chargeBeginEffect.at(x + tr.x, y + tr.y, rotation);
                chargeSound.at(x + tr.x, y + tr.y, 1);

                for(int i = 0; i < chargeEffects; i++){
                    Time.run(Mathf.random(chargeMaxDelay), () -> {
                        if(!isValid()) return;
                        tr.trns(rotation, shootLength);
                        chargeEffect.at(x + tr.x, y + tr.y, rotation);
                    });
                }

                charging = true;

                Time.run(chargeTime, () -> {
                    if(!isValid()) return;
                    tr.trns(rotation, shootLength);
                    recoil = recoilAmount;
                    heat = 1f;
                    if(lightning && speed < lightningThreshold) Lightning.create(team, lightningColor, baseLightningDamage - lightningDamageDec * speed, x + tr.x, y + tr.y, rotation, baseLightningLength - (int)((speed - 1) * lightningLengthDec));
                    bullet(type, rotation + Mathf.range(inaccuracy / (1f + Mathf.clamp(speed / accInc, 0, maxSpeedMul))));
                    effects();
                    charging = false;
                });

                //when burst spacing is enabled, use the burst pattern
            }else if(burstSpacing > 0.0001f){
                for(int i = 0; i < shots; i++){
                    Time.run(burstSpacing * i, () -> {
                        if(!isValid() || !hasAmmo()) return;

                        recoil = recoilAmount;

                        tr.trns(rotation, shootLength, Mathf.range(xRand));
                        if(lightning && speed < lightningThreshold) Lightning.create(team, lightningColor, baseLightningDamage - lightningDamageDec * speed, x + tr.x, y + tr.y, rotation, baseLightningLength - (int)((speed - 1) * lightningLengthDec));
                        bullet(type, rotation + Mathf.range(inaccuracy / (1f + Mathf.clamp(speed / accInc, 0, maxSpeedMul))));
                        effects();
                        useAmmo();
                        recoil = recoilAmount;
                        heat = 1f;
                    });
                }

            }else{
                //otherwise, use the normal shot pattern(s)

                if(alternate){
                    float i = (shotCounter % shots) - (shots-1)/2f;

                    tr.trns(rotation - 90, spread * i + Mathf.range(xRand), shootLength);
                    if(lightning && speed < lightningThreshold) Lightning.create(team, lightningColor, baseLightningDamage - lightningDamageDec * speed, x + tr.x, y + tr.y, rotation, baseLightningLength - (int)((speed - 1) * lightningLengthDec));
                    bullet(type, rotation + Mathf.range(inaccuracy / (1f + Mathf.clamp(speed / accInc, 0, maxSpeedMul))));
                }else{
                    tr.trns(rotation, shootLength, Mathf.range(xRand));

                    for(int i = 0; i < shots; i++){
                        if(lightning && speed < lightningThreshold) Lightning.create(team, lightningColor, baseLightningDamage - lightningDamageDec * speed, x + tr.x, y + tr.y, rotation, baseLightningLength - (int)((speed - 1) * lightningLengthDec));
                        bullet(type, rotation + Mathf.range((inaccuracy + type.inaccuracy) / (1f + Mathf.clamp(speed / accInc, 0, maxSpeedMul))) + (i - (int)(shots / 2f)) * spread);
                    }
                }

                shotCounter++;

                recoil = recoilAmount;
                heat = 1f;
                effects();
                useAmmo();
            }
        }

        @Override
        protected float baseReloadSpeed(){
            return efficiency() * speed;
        }

        public void changeSpeed(float amount){
            speed = Mathf.clamp(speed + amount, 1f, maxSpeedMul);
        }

        public float speedf(){
            //Convert (1 -> maxSpeed) to (0 -> maxSpeed - 1)
            return (speed - 1f) / (maxSpeedMul - 1f);
        }
    }
}