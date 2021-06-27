package unity.world.blocks.production;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class ExplosiveSeparator extends Separator{
    public Color lightColor = Color.valueOf("7f19ea");
    public Color coolColor = new Color(1, 1, 1, 0f);
    public Color hotColor = Color.valueOf("ff9575a3");
    /** Item that causes the separator to heat up */
    public Item fuelItem;
    /** heating per frame * fullness */
    public float heating = 0.01f;
    /** threshold at which block starts smoking */
    public float smokeThreshold = 0.3f;
    /** heat threshold at which lights start flashing */
    public float flashThreshold = 0.46f;
    public float explosionRadius = 19f;
    public float explosionDamage = 1350f;
    /** heat removed per unit of coolant */
    public float coolantPower = 0.5f;

    public TextureRegion lightsRegion, topRegion;

    protected Vec2 tr = new Vec2();

    public ExplosiveSeparator(String name){
       super(name);
    }

    @Override
    public void load(){
        super.load();

        topRegion = atlas.find(name + "-top");
        lightsRegion = atlas.find(name + "-lights");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("heat", entity -> new Bar("bar.heat", Pal.lightOrange, () -> ((ExplosiveSeparatorBuild)entity).heat));
    }

    public class ExplosiveSeparatorBuild extends SeparatorBuild{
        protected float heat, productionEfficiency;

        @Override
        public void updateTile(){
            super.updateTile();

            ConsumeLiquid cliquid = consumes.<ConsumeLiquid>get(ConsumeType.liquid);

            int fuel = items.get(fuelItem);
            float fullness = (float)fuel / itemCapacity;
            productionEfficiency = fullness;

            if(fuel > 0 && enabled && power.status > 0f){
                heat += fullness * heating * Math.min(delta(), 4f);
            }else{
                productionEfficiency = 0f;
            }

            var liquid = cliquid.liquid;

            if(heat > 0 && enabled){
                float maxUsed = Math.min(liquids.get(liquid), heat / coolantPower);
                heat -= maxUsed * coolantPower;
                liquids.remove(liquid, maxUsed);
            }

            if(heat > 0.3){
                float smoke = 1.0f + (heat - smokeThreshold) / (1f - smokeThreshold);
                if(Mathf.chance(smoke / 20f * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * tilesize / 2f),
                    y + Mathf.random(size * tilesize / 2f));
                }
            }

            heat = Mathf.clamp(heat);

            if(heat >= 0.999f){
                Events.fire(Trigger.thoriumReactorOverheat);
                kill();
            }
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();

            Sounds.explosionbig.at(tile);

            int fuel = items.get(fuelItem);

            if((fuel < 5 && heat < 0.5f) || !state.rules.reactorExplosions) return;

            Effect.shake(6f, 16f, x, y);
            Fx.nuclearShockwave.at(x, y);
            for(int i = 0; i < 6; i++){
                Time.run(Mathf.random(40f), () -> Fx.nuclearcloud.at(x, y));
            }

            Damage.damage(x, y, explosionRadius * tilesize, explosionDamage * 4f);

            for(int i = 0; i < 20; i++){
                Time.run(Mathf.random(50f), () -> {
                    tr.rnd(Mathf.random(40f));
                    Fx.explosion.at(tr.x + x, tr.y + y);
                });
            }

            for(int i = 0; i < 70; i++){
                Time.run(Mathf.random(80f), () -> {
                    tr.rnd(Mathf.random(120f));
                    Fx.nuclearsmoke.at(tr.x + x, tr.y + y);
                });
            }
        }

        @Override
        public void drawLight(){
            float fract = productionEfficiency;
            Drawf.light(team, x, y, (90f + Mathf.absin(5f, 5f)) * fract, Tmp.c1.set(lightColor).lerp(Color.scarlet, heat), 0.6f * fract);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.rect(topRegion, x, y);
            
            Draw.color(coolColor, hotColor, heat);
            Fill.rect(x, y, size * tilesize, size * tilesize);

            if(heat > flashThreshold){
                float flash = 1f + ((heat - flashThreshold) / (1f - 0.46f)) * 5.4f;
                flash += flash * delta();
                Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9f, 1f));
                Draw.alpha(0.6f);
                Draw.rect(lightsRegion, x, y);
            }

            Draw.reset();
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return heat;
            return super.sense(sensor);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heat = read.f();
        }
    }
}
