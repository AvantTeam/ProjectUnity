package unity.entities.bullet.highTier;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.util.*;

public class HealingShockWaveBulletType extends BulletType{
    public int segments = 64;
    public float width = 17f, shockwaveSpeed = 8f;
    public Color color = Pal.heal;
    public StatusEffect allyStatus = StatusEffects.overclock;
    public float allyStatusDuration = 5f * 60f;
    public float fadeTime = 20f;

    public HealingShockWaveBulletType(float damage){
        super(0f, damage);
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        reflectable = false;
        despawnEffect = shootEffect = smokeEffect = Fx.none;
        hitEffect = Fx.hitLaser;
        hitColor = Pal.heal;
    }

    @Override
    public void init(){
        super.init();
        drawSize = shockwaveSpeed * lifetime * 2f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new HealingShockWaveData(b);
    }

    @Override
    public void removed(Bullet b){
        super.removed(b);
        if(b.data instanceof HealingShockWaveData data){
            data.remove();
        }
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof HealingShockWaveData data){
            float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
            Draw.color(color);
            Lines.stroke(width * fout);
            Lines.beginLine();
            for(int i = 0; i < data.posData.length; i++){
                ShockWavePositionData ad = data.posData[i], bd = data.posData[Mathf.mod(i + 1, data.posData.length)];
                Lines.linePoint(ad.x, ad.y);
                Drawf.light(b.team, ad.x, ad.y, bd.x, bd.y, width * 3f, color, 0.3f * fout);
            }
            Lines.endLine(true);
            Draw.color();
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof HealingShockWaveData data){
            for(int i = 0; i < data.posData.length; i++){
                ShockWavePositionData d = data.posData[i];
                float ang = i * 360f / data.posData.length;
                if(!d.hit){
                    d.x += Angles.trnsx(ang, shockwaveSpeed) * Time.delta;
                    d.y += Angles.trnsy(ang, shockwaveSpeed) * Time.delta;
                }
                Building ins = Vars.world.buildWorld(d.x, d.y);
                if(ins != null && ins.block.absorbLasers) d.hit = true;
            }
            if(b.timer(1, 5f)){
                int idx = 0;
                float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
                for(ShockWavePositionData d : data.posData){
                    ShockWavePositionData a = data.posData[Mathf.mod(idx + 1, data.posData.length)];

                    if(!d.hit && !a.hit){
                        Utils.collideLineRaw(d.x, d.y, a.x, a.y, width * fout, bd -> true, u -> true, (building, direct) -> {
                            if(direct && data.collided.add(building.id)){
                                if(building.team == b.team){
                                    building.heal((healPercent / 100f) * building.maxHealth);
                                }else{
                                    building.damage(damage);
                                }
                            }
                            return false;
                        }, unit -> {
                            if(data.collided.add(unit.id)){
                                if(unit.team == b.team){
                                    unit.heal((healPercent / 100f) * unit.maxHealth);
                                    unit.apply(allyStatus, allyStatusDuration);
                                }else{
                                    unit.damage(damage);
                                    unit.apply(status, statusDuration);
                                }
                            }
                            return false;
                        }, null, (ex, ey) -> hit(b, ex, ey), true);
                    }

                    idx++;
                }
            }
        }
    }

    class HealingShockWaveData{
        ShockWavePositionData[] posData = new ShockWavePositionData[segments];
        IntSet collided = new IntSet(409);

        HealingShockWaveData(Bullet b){
            for(int i = 0; i < posData.length; i++){
                ShockWavePositionData d = Pools.obtain(ShockWavePositionData.class, ShockWavePositionData::new);
                d.x = b.x;
                d.y = b.y;
                d.hit = false;
                posData[i] = d;
            }
        }

        void remove(){
            for(ShockWavePositionData d : posData){
                Pools.free(d);
            }
        }
    }

    static class ShockWavePositionData{
        float x, y;
        boolean hit = false;
    }
}
