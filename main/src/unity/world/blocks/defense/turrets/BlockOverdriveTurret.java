package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.entities.Units;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import unity.content.UnityBullets;
import unity.entities.bullet.misc.BlockStatusEffectBulletType;
import unity.world.blocks.exp.ExpHolder;

public class BlockOverdriveTurret extends ReloadTurret {
    public final int timerBullet = timers++;

    public float buffRange = 50f;
    public float buffReload = 180f;

    TextureRegion buffRegion;
    public BlockStatusEffectBulletType bullet = (BlockStatusEffectBulletType) UnityBullets.statusEffect;

    public BlockOverdriveTurret(String name) {
        super(name);

        hasPower = update = sync = solid = outlineIcon = true;
    }

    @Override
    public void load(){
        super.load();

        buffRegion = Core.atlas.find(name+"-buff");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x, y, buffRange, Pal.accent);
    }

    public class BlockOverdriveTurretBuild extends ReloadTurretBuild{
        public Building target;
        float buffingTime;
        public boolean buffing;

        @Override
        public void drawSelect(){
            Drawf.circles(x, y, buffRange, Pal.accent);

            if (buffing){
                Draw.color(Pal.heal, Color.valueOf("feb380"), Mathf.absin(12f, 1f));
                Draw.alpha(target instanceof ExpHolder? 0.3f : 0.4f);
                Draw.rect(buffRegion, target.x, target.y, 8 * target.block.size, 8 * target.block.size);
                if (target instanceof ExpHolder){
                    //exp effect sth
                }
            }
        }

        @Override
        public void updateTile(){
            buffing = false;

            if (target != null){
                if (!targetValid(target)){
                    target = null;
                }else if(consValid() && enabled){
                    if (timer(timerBullet, buffReload)){
                        bullet.create(this, target.x, target.y, 0f);
                        timer.reset(timerBullet, 0);
                    }
                    buffing = true;
                }
            }

            if (cons.optionalValid() && efficiency() > 0){
                buffingTime += edelta();
                if (buffingTime >= buffReload){
                    consume();
                    buffingTime = 0f;
                }
            }

            if (timer(0, buffReload)){
                target = Units.closestBuilding(team, x, y, buffRange, this::targetValid);
            }
        }

        @Override
        public boolean shouldConsume(){
            return target != null && enabled;
        }

        public boolean targetValid(Building b){
            return b.isValid() && b.dst(tile) <= buffRange && b.block.canOverdrive && b != this && !proximity.contains(b) && !isBeingBuffed(b);
        }

        public boolean isBeingBuffed(Building b){
            Seq<Bullet> bullets = Groups.bullet.intersect(b.x, b.y, b.block.size * 8, b.block.size * 8);
            int num1 = bullets.size;

            if (num1 > 0){
                return bullets.get(0).owner != this;
            }else{
                return false;
            }
        }

        @Override
        public void read(Reads read){
            super.read(read);

            target = Vars.world.build(read.i());
        }

        @Override
        public void write(Writes write){
            super.write(write);

            if (target != null) write.i(target.pos());
        }
    }
}