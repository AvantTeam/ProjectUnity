package unity.world.blocks.defense.turrets;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.entities.bullet.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

public class BarrelsItemTurret extends ItemTurret{
    protected final Seq<Barrel> barrels = new Seq<>(1);
    protected boolean focus;
    protected Vec2 tr3 = new Vec2();

    public BarrelsItemTurret(String name){
        super(name);
    }

    protected void addBarrel(float x, float y, float reloadTime){
        barrels.add(new Barrel(x, y, reloadTime));
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("unity-block-" + size);
    }

    protected class Barrel{
        public final float x, y, reloadTime;

        public Barrel(float x, float y, float reloadTime){
            this.x = x;
            this.y = y;
            this.reloadTime = reloadTime;
        }
    }

    public class BarrelsItemTurretBuild extends ItemTurretBuild{
        protected float[] barrelReloads = new float[barrels.size];
        protected int[] barrelShotCounters = new int[barrels.size];

        @Override
        protected void shoot(BulletType type){
            if(focus){
                recoil = recoilAmount;
                heat = 1f;
                float i = shotCounter % 2 - 0.5f;
                
                tr.trns(rotation - 90f, spread * i + Mathf.range(xRand), size * tilesize / 2f);
                tr3.trns(rotation, Math.max(Mathf.dst(x, y, targetPos.x, targetPos.y), size * tilesize));
                
                float rot = Angles.angle(tr.x, tr.y, tr3.x, tr3.y);
                
                bullet(type, rot + Mathf.range(inaccuracy));
                shotCounter++;
                effects();
                useAmmo();
            }else{
                super.shoot(type);
            }
        }

        protected void shootBarrel(BulletType type, int index){
            recoil = Mathf.clamp(recoil + recoilAmount / 2f, 0f, recoilAmount);
            
            float i = barrelShotCounters[index] % 2 - 0.5f;
            tr.trns(rotation - 90f, barrels.get(index).x * i + Mathf.range(xRand), barrels.get(index).y);
            float rot = rotation;
            
            if(focus){
                tr3.trns(rotation, Math.max(Mathf.dst(x, y, targetPos.x, targetPos.y), size * tilesize));
                rot = Angles.angle(tr.x, tr.y, tr3.x, tr3.y);
            }
            
            bullet(type, rot + Mathf.range(inaccuracy));
            barrelShotCounters[index]++;
            effects();
            useAmmo();
        }

        @Override
        protected void updateShooting(){
            super.updateShooting();
            for(int i = 0, len = barrels.size; i < len; i++){
                if(hasAmmo()){
                    if(barrelReloads[i] >= barrels.get(i).reloadTime){
                        shootBarrel(peekAmmo(), i);
                        barrelReloads[i] = 0f;
                    }else{
                        barrelReloads[i] += delta() * peekAmmo().reloadMultiplier * baseReloadSpeed();
                    }
                }
            }
        }
    }
}
