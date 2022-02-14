package unity.world.blocks.defense.turrets;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import unity.entities.bullet.misc.BlockStatusEffectBulletType;

public class BlockOverdriveTurret extends ReloadTurret {
    public final int timerBuff = timers++;
    public float buffRange = 50f;
    public float buffReload = 180f;
    public BlockStatusEffectBulletType bullet;

    public @Load("@-buff") TextureRegion buffRegion;

    public BlockOverdriveTurret(String name) {
        super(name);

        hasPower = update = sync = solid = outlineIcon = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x, y, buffRange, Pal.accent);
    }

    public class BlockOverdriveTurretBuild extends ReloadTurretBuild{
        public Building target, lastTarget;
        float buffIntensity = 0;
        float buffingTime;
        boolean buffing;

        @Override
        public void drawSelect(){
            super.drawSelect();

            Drawf.circles(x, y, buffRange, Pal.accent);
            if (target != null) {
                Draw.color(Pal.heal, Color.valueOf("feb380"), Mathf.absin(4f, 1f));
                Draw.rect(buffRegion, target.x, target.y);
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if (target != null){
                if (!targetValid(target)){
                    target = null;
                }else if(consValid()){
                    bullet.create(this, target.x, target.y, 0f);
                    buffing = true;
                }

                buffIntensity = Mathf.lerpDelta(buffIntensity, buffing ? 1f : 0f, 0.08f * Time.delta);
            }

            if (cons.optionalValid() && efficiency() > 0){
                buffingTime += edelta();
                if (buffingTime >= buffReload){
                    consume();
                    buffingTime = 0f;
                }
            }

            if (timer(timerBuff, 60f)){
                target = Units.closestBuilding(team, x, y, buffRange, this::targetValid);
            }
        }

        public boolean targetValid(Building target){
            return !target.dead && target.dst(tile) <= buffRange && target.block.canOverdrive && target.health < target.maxHealth;
        }
    }

}