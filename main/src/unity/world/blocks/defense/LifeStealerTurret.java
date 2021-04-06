package unity.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.content.*;
import unity.entities.*;
import unity.entities.comp.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class LifeStealerTurret extends BaseTurret{
    public final int timerTarget = timers++;
    public float retargetTime = 5f;

    public TextureRegion baseRegion;
    public float shootCone = 6f;
    public float shootLength = -1f;

    public float powerUse = 1f;
    public float damage = 60f;
    public float maxContain = 100f;
    public float healPercent = 0.05f;

    public Color healColor = UnityPal.monolithLight;
    public Effect healEffect = Fx.healBlockFull;
    public Effect healTrnsEffect = UnityFx.supernovaPullEffect;

    public Sound shootSound = Sounds.tractorbeam;
    public float shootSoundVolume = 0.9f;

    public Color laserColor = UnityPal.monolith;
    public float laserWidth = 0.4f;
    public TextureRegion laser;
    public TextureRegion laserEnd;

    public LifeStealerTurret(String name){
        super(name);

        rotateSpeed = 20f;
        hasItems = hasLiquids = false;
        hasPower = consumesPower = outputsPower = true;
        acceptCoolant = false;
    }

    @Override
    public void init(){
        consumes.powerCond(powerUse, (LifeStealerTurretBuild build) -> build.target != null);

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

    public class LifeStealerTurretBuild extends BaseTurretBuild implements ExtensionHolder{
        protected Extensionc ext;
        public Unit target;
        public float lastX, lastY, strength;

        public float contained;

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
                target = Groups.unit
                    .intersect(x - range, y - range, range * 2f, range * 2f)
                    .min(u -> u.team != team, u -> u.dst2(this));
            }

            boolean shoot = false;
            if(target != null && target.within(this, range + target.hitSize / 2f) && target.team() != team && efficiency() > 0f){
                if(!headless){
                    control.sound.loop(shootSound, this, shootSoundVolume);
                }

                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                shoot = Angles.within(rotation, dest, shootCone);
            }

            if(shoot && target != null && efficiency() > 0f){
                float health = (damage / 60f) * efficiency();
                target.damageContinuous(health);
                contained += health * Time.delta;

                lastX = target.x;
                lastY = target.y;
                strength = Mathf.lerpDelta(strength, efficiency(), 0.1f);
            }else{
                strength = Mathf.lerpDelta(strength, 0f, 0.1f);
            }

            if(contained >= maxContain){
                tryHeal();
            }
        }

        protected void tryHeal(){
            boolean any = indexer.eachBlock(this, range, b -> b.isValid() && b.health() < b.maxHealth(), b -> {
                healTrnsEffect.at(x, y, rotation, new Float[]{x, y, b.x, b.y, 2.5f + Mathf.range(0.3f)});
                Time.run(healEffect.lifetime, () -> {
                    if(b != null && b.isValid()){
                        healEffect.at(b.x, b.y, b.block.size, healColor);
                        b.healFract(healPercent);
                    }
                });
            });

            if(any){
                contained %= maxContain;
            }
        }

        @Override
        public void draw(){
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
            if(strength > 0.1f){
                Draw.z(Layer.bullet);
                float ang = angleTo(lastX, lastY);

                Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

                Drawf.laser(team, laser, laserEnd,
                x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
                lastX, lastY, strength * efficiency() * laserWidth);

                Draw.mixcol();
            }
        }
    }
}
