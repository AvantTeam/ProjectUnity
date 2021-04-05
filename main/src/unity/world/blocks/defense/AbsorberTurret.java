package unity.world.blocks.defense;

import arc.Core;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.content.*;
import unity.entities.*;
import unity.entities.comp.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class AbsorberTurret extends BaseTurret{
    public final int timerTarget = timers++;
    public float retargetTime = 5f;

    public TextureRegion baseRegion;
    public float shootCone = 6f;
    public float shootLength = -1f;

    public float powerProduction = 2.5f;
    public float resistance = 0.4f;
    public float damageScale = 18f;
    public float speedScale = 3.5f;

    public Sound shootSound = Sounds.tractorbeam;
    public float shootSoundVolume = 0.9f;

    public Color laserColor = UnityPal.monolith;
    public float laserWidth = 0.4f;
    public TextureRegion laser;
    public TextureRegion laserEnd;

    public AbsorberTurret(String name){
        super(name);
        rotateSpeed = 20f;
        hasItems = hasLiquids = false;
        hasPower = consumesPower = outputsPower = true;
        acceptCoolant = false;
    }

    @Override
    public void init(){
        super.init();
        if(shootLength < 0) shootLength = size * tilesize / 2f;
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("block-" + size, "unity-block-" + size);
        laser = Core.atlas.find(name + "-laser");
        laserEnd = Core.atlas.find(name + "-laser-end");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class AbsorberTurretBuild extends BaseTurretBuild implements ExtensionHolder{
        protected Extensionc ext;
        protected Bullet target;
        public float lastX, lastY, strength;

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

        public boolean isShooting(){
            return shoot;
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

            if(shoot && target != null && efficiency() > 0.01f){
                target.vel.setLength(Math.max(target.vel.len() - resistance * strength, 0f));
                if(target.vel.isZero(0.01f)){
                    target.remove();
                }

                lastX = target.x;
                lastY = target.y;
                strength = Mathf.lerpDelta(strength, 1f, 0.1f);
            }else{
                strength = Mathf.lerpDelta(strength, 0f, 0.1f);
            }
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);
        }

        @Override
        public float clipSizeExt(){
            if(target == null) return 0f;

            return dst(target) * 2f;
        }

        @Override
        public void drawExt(){
            if(shoot){
                Draw.z(Layer.bullet);
                float ang = angleTo(lastX, lastY);

                Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

                Drawf.laser(team, laser, laserEnd,
                x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
                lastX, lastY, strength * efficiency() * laserWidth);

                Draw.mixcol();
            }
        }

        @Override
        public float getPowerProduction(){
            if(target == null || target.type == null) return 0f;

            return (target.type.damage / damageScale) * (target.vel.len() / speedScale) * powerProduction;
        }
    }
}
