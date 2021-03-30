package unity.world.blocks.defense.turrets;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.*;
import unity.util.*;

import java.lang.reflect.*;

public class AttractLaserTurret extends LaserTurret{
    public float chargeWarmup = 0.002f;
    public float chargeCooldown = 0.01f;

    public Sound chargeSound = Sounds.none;
    public float chargeSoundVolume = 1f;

    public float attractionStrength = 6f;
    public float attractionDamage = 60f;
    public Cons2<AttractLaserTurretBuild, Unit> attractUnit = (tile, unit) -> {};
    public Cons<AttractLaserTurretBuild> attractor = tile -> {};

    public AttractLaserTurret(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("unity-block-" + size);
    }

    public class AttractLaserTurretBuild extends LaserTurretBuild{
        public float charge;
        public float phase;

        protected PitchedSoundLoop sound = new PitchedSoundLoop(chargeSound, chargeSoundVolume);

        // thanks anuke for making the fields package-private
        private Field bulletf = Utils.findField(getClass(), "bullet", true);
        private Field bulletLifef = Utils.findField(getClass(), "bulletLife", true);

        @Override
        public void updateTile(){
            if(!validateTarget() || !consValid()){
                charge = Mathf.lerpDelta(charge, 0f, chargeCooldown);
                charge = charge > 0.001f ? charge : 0f;
            }
            if(isShooting() && (bulletLife() <= 0f && bullet() == null)){
                attractUnits();
            }

            if(isShooting() || (bulletLife() > 0f && bullet() != null)){
                phase = Mathf.clamp(phase + chargeWarmup * edelta(), 0f, 1f);
            }else{
                phase = Mathf.lerpDelta(phase, 0f, chargeCooldown);
                phase = phase > 0.001f ? phase : 0f;
            }

            super.updateTile();

            if(isShooting() && (bulletLife() <= 0f && bullet() == null)){
                var liquid = liquids.current();
                float maxUsed = consumes.<ConsumeLiquidBase>get(ConsumeType.liquid).amount;

                float used = baseReloadSpeed() * ((cheating() ? maxUsed : Math.min(liquids.get(liquid), maxUsed * Time.delta)) * liquid.heatCapacity * coolantMultiplier);
                charge += 120f * chargeWarmup * used;
            }

            float prog = charge * 1.5f + 0.5f;
            sound.update(x, y, charge, prog);
        }

        @Override
        public boolean isShooting(){
            return super.isShooting() && efficiency() > 0f;
        }

        @Override
        protected void updateShooting(){
            if(bulletLife() > 0f && bullet() != null){
                return;
            }

            if(charge >= 1f && phase >= 1f && (consValid() || cheating())){
                BulletType type = peekAmmo();

                shoot(type);
                charge = 0f;
            }
        }

        @Override
        public void remove(){
            sound.stop();
            super.remove();
        }

        protected void attractUnits(){
            float rad = range() * 2f;
            Units.nearby(x - rad, y - rad, rad * 2f, rad * 2f, unit -> {
                if(unit.isValid() && unit.within(this, rad)){
                    float dst = unit.dst(this);
                    float strength = 1 - (dst / rad);
                    Tmp.v1.set(x - unit.x, y - unit.y)
                        .rotate(10f * (1f - charge))
                        .setLength(attractionStrength * charge * Time.delta)
                        .scl(strength);

                    unit.impulseNet(Tmp.v1);
                    if(unit.team != team) unit.damageContinuous((attractionDamage / 60f) * charge * strength);
                    attractUnit.get(this, unit);
                }
            });

            attractor.get(this);
        }

        public float bulletLife(){
            return Utils.getField(this, bulletLifef);
        }

        public Bullet bullet(){
            return Utils.getField(this, bulletf);
        }
    }
}
