package unity.world.blocks.defense;

import arc.audio.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.content.*;
import unity.entities.*;
import unity.entities.comp.*;

import static mindustry.Vars.*;

public class AbsorberTurret extends BaseTurret{
    public final int timerTarget = timers++;
    public float retargetTime = 5f;

    public float shootCone = 6f;

    public float powerProduction = 2.5f;
    public float resistance = 1f;

    public Sound shootSound = Sounds.tractorbeam;
    public float shootSoundVolume = 0.9f;

    public AbsorberTurret(String name){
        super(name);
        rotateSpeed = 20f;
        hasItems = hasLiquids = false;
        hasPower = consumesPower = true;
        acceptCoolant = false;
    }

    public class AbsorberTurretBuild extends BaseTurretBuild implements ExtensionHolder{
        protected Extensionc ext;
        protected Bullet target;
        protected boolean shoot;

        public float productionEfficiency;

        @Override
        public void created(){
            super.created();
            ext = (Extensionc)UnityUnitTypes.extension.create(team);
            ext.holder(this);
            ext.set(x, y);
            ext.add();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(timer(timerTarget, retargetTime)){
                target = Groups.bullet
                    .intersect(x - range, y - range, range * 2f, range * 2f)
                    .min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
            }

            if(target != null && target.within(this, range + target.hitSize / 2f) && target.team() != team && efficiency() > 0.02f){
                if(!headless){
                    control.sound.loop(shootSound, this, shootSoundVolume);
                }

                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                shoot = Angles.within(rotation, dest, shootCone);
            }else{
                shoot = false;
            }

            if(shoot){
                //TODO work on this
            }
        }

        @Override
        public float clipSizeExt(){
            if(target == null){
                return 0f;
            }
            return dst(target) * 2f;
        }

        @Override
        public void drawExt(){
            //TODO work on this
        }
    }
}
