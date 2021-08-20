package unity.world.blocks.defense.turrets;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.gen.*;
import unity.graphics.*;
import unity.util.*;

import java.lang.reflect.*;

@Merge(base = LaserTurret.class, value = Soulc.class)
public class SupernovaTurret extends SoulLaserTurret{
    public float chargeWarmup = 0.002f;
    public float chargeCooldown = 0.01f;

    public Sound chargeSound = UnitySounds.supernovaCharge;
    public float chargeSoundVolume = 1f;

    public float attractionStrength = 6f;
    public float attractionDamage = 60f;

    /** Temporary vector array to be used in the drawing method */
    private static final Vec2[] phases = {new Vec2(), new Vec2(), new Vec2(), new Vec2(), new Vec2(), new Vec2()};
    public float starRadius = 8f;
    public float starOffset = -2.25f;

    public final int timerChargeStar = timers++;
    public Effect chargeStarEffect = UnityFx.supernovaChargeStar;
    public Effect chargeStar2Effect = UnityFx.supernovaChargeStar2;
    public Effect starDecayEffect = UnityFx.supernovaStarDecay;
    public Effect heatWaveEffect = UnityFx.supernovaStarHeatwave;
    public Effect pullEffect = UnityFx.supernovaPullEffect;

    private static final Field bulletf = ReflectUtils.findField(LaserTurretBuild.class, "bullet", true);
    private static final Field bulletLifef = ReflectUtils.findField(LaserTurretBuild.class, "bulletLife", true);

    public SupernovaTurret(String name){
        super(name);

        drawer = b -> {
            if(b instanceof SupernovaTurretBuild tile){
                //core
                phases[0].trns(tile.rotation, -tile.recoil + Mathf.curve(tile.phase, 0f, 0.3f) * -2f);
                //left wing
                phases[1].trns(tile.rotation - 90,
                    Mathf.curve(tile.phase, 0.2f, 0.5f) * -2f,

                    -tile.recoil + Mathf.curve(tile.phase, 0.2f, 0.5f) * 2f +
                        Mathf.curve(tile.phase, 0.5f, 0.8f) * 3f
                );
                //left bottom wing
                phases[2].trns(tile.rotation - 90,
                    Mathf.curve(tile.phase, 0f, 0.3f) * -1.5f +
                        Mathf.curve(tile.phase, 0.6f, 1f) * -2f,

                    -tile.recoil + Mathf.curve(tile.phase, 0f, 0.3f) * 1.5f +
                        Mathf.curve(tile.phase, 0.6f, 1f) * -1f
                );
                //bottom
                phases[3].trns(tile.rotation, -tile.recoil + Mathf.curve(tile.phase, 0f, 0.6f) * -4f);
                //right wing
                phases[4].trns(tile.rotation - 90,
                    Mathf.curve(tile.phase, 0.2f, 0.5f) * 2f,

                    -tile.recoil + Mathf.curve(tile.phase, 0.2f, 0.5f) * 2f +
                        Mathf.curve(tile.phase, 0.5f, 0.8f) * 3f
                );
                //right bottom wing
                phases[5].trns(tile.rotation - 90,
                    Mathf.curve(tile.phase, 0f, 0.3f) * 1.5f +
                        Mathf.curve(tile.phase, 0.6f, 1f) * 2f,

                    -tile.recoil + Mathf.curve(tile.phase, 0f, 0.3f) * 1.5f +
                        Mathf.curve(tile.phase, 0.6f, 1f) * -1f
                );

                Draw.rect(Regions.supernovaWingLeftBottomOutlineRegion, tile.x + phases[2].x, tile.y + phases[2].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaWingRightBottomOutlineRegion, tile.x + phases[5].x, tile.y + phases[5].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaWingLeftOutlineRegion, tile.x + phases[1].x, tile.y + phases[1].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaWingRightOutlineRegion, tile.x + phases[4].x, tile.y + phases[4].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaBottomOutlineRegion, tile.x + phases[3].x, tile.y + phases[3].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaHeadOutlineRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90);
                Draw.rect(Regions.supernovaCoreOutlineRegion, tile.x + phases[0].x, tile.y + phases[0].y, tile.rotation - 90);

                Draw.rect(Regions.supernovaWingLeftBottomRegion, tile.x + phases[2].x, tile.y + phases[2].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaWingRightBottomRegion, tile.x + phases[5].x, tile.y + phases[5].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaWingLeftRegion, tile.x + phases[1].x, tile.y + phases[1].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaWingRightRegion, tile.x + phases[4].x, tile.y + phases[4].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaBottomRegion, tile.x + phases[3].x, tile.y + phases[3].y, tile.rotation - 90);
                Draw.rect(Regions.supernovaHeadRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90);

                float z = Draw.z();
                Draw.z(z + 0.001f);

                Draw.rect(Regions.supernovaCoreRegion, tile.x + phases[0].x, tile.y + phases[0].y, tile.rotation - 90);
                Draw.z(z);
            }else{
                throw new IllegalStateException("building isn't an instance of SupernovaTurretBuild");
            }
        };

        heatDrawer = tile -> {
            if(tile.heat <= 0.00001f) return;

            float r = Utils.pow6In.apply(tile.heat);
            float g = Interp.pow3In.apply(tile.heat);
            float b = Interp.pow2Out.apply(tile.heat);
            float a = Interp.pow2In.apply(tile.heat);

            Draw.color(Tmp.c1.set(r, g, b, a));
            Draw.blend(Blending.additive);

            Draw.rect(heatRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);

            Draw.color();
            Draw.blend();
        };
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("unity-block-" + size);
    }

    public class SupernovaTurretBuild extends SoulLaserTurretBuild{
        public float charge;
        public float phase;
        public float starHeat;

        protected PitchedSoundLoop sound = new PitchedSoundLoop(chargeSound, chargeSoundVolume);

        @Override
        public void updateTile(){
            if(!isShooting() || !validateTarget() || !consValid()){
                charge = Mathf.lerpDelta(charge, 0f, chargeCooldown);
                charge = charge > 0.001f ? charge : 0f;
            }

            if(isShooting() && (bulletLife() <= 0f && bullet() == null)) attractUnits();

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
                charge = Mathf.clamp(charge + 120f * chargeWarmup * used);
            }

            float prog = charge * 1.5f + 0.5f;
            sound.update(x, y, Mathf.curve(charge, 0f, 0.4f) * 1.2f, prog);

            boolean notShooting = bulletLife() <= 0f || bullet() == null;
            boolean tick = Mathf.chanceDelta(1f);
            boolean tickCharge = Mathf.chanceDelta(charge);

            starHeat = Mathf.approachDelta(starHeat, notShooting ? charge : 1f, chargeWarmup * 60f);

            Tmp.v1.trns(rotation, -recoil + starOffset + Mathf.curve(phase, 0f, 0.3f) * -2f);
            if(notShooting){
                if(charge > 0.1f && timer(timerChargeStar, 20f)){
                    chargeStarEffect.at(
                        x + Tmp.v1.x,
                        y + Tmp.v1.y,
                        rotation, charge
                    );
                }

                if(!Mathf.zero(charge) && tickCharge){
                    chargeStar2Effect.at(
                        x + Tmp.v1.x,
                        y + Tmp.v1.y,
                        rotation, charge
                    );
                }

                if(tickCharge){
                    chargeBeginEffect.at(
                        x + Angles.trnsx(rotation, -recoil + shootLength),
                        y + Angles.trnsy(rotation, -recoil + shootLength),
                        rotation, charge
                    );
                }
            }else{
                if(tick){
                    starDecayEffect.at(
                        x + Tmp.v1.x,
                        y + Tmp.v1.y,
                        rotation
                    );
                }

                if(timer(timerChargeStar, 20f)){
                    heatWaveEffect.at(
                        x + Tmp.v1.x,
                        y + Tmp.v1.y,
                        rotation
                    );
                }
            }

            if(Mathf.chanceDelta(notShooting ? charge : 1f)){
                Tmp.v1
                    .trns(rotation, -recoil + starOffset + Mathf.curve(phase, 0f, 0.3f) * -2f)
                    .add(this);

                Lightning.create(
                    team,
                    Pal.lancerLaser,
                    60f, Tmp.v1.x, Tmp.v1.y,
                    Mathf.randomSeed((long)(id + Time.time), 360f),
                    Mathf.round(Mathf.randomTriangular(12f, 18f) * (notShooting ? charge : 1f))
                );
            }
        }

        @Override
        public void draw(){
            super.draw();

            boolean notShooting = bulletLife() <= 0f || bullet() == null;
            Tmp.v1.trns(rotation, -recoil + starOffset + Mathf.curve(phase, 0f, 0.3f) * -2f);

            float z = Draw.z();
            Draw.z(Layer.effect);

            Draw.color(UnityPal.monolith);
            UnityDrawf.shiningCircle(id, Time.time,
                x + Tmp.v1.x,
                y + Tmp.v1.y,
                starHeat * starRadius,
                6, 20f,
                starHeat * starRadius, starHeat * starRadius * 1.5f,
                120f
            );

            if(notShooting){
                if(!Mathf.zero(charge)){
                    UnityDrawf.shiningCircle(id + 1, Time.time,
                        x + Angles.trnsx(rotation, -recoil + shootLength),
                        y + Angles.trnsy(rotation, -recoil + shootLength),
                        charge * 4f,
                        6, 12f,
                        charge * 4f, charge * 8f,
                        120f
                    );
                }
            }

            Draw.reset();
            Draw.z(z);
        }

        @Override
        public boolean isShooting(){
            return super.isShooting() && efficiency() > 0f;
        }

        @Override
        protected void updateShooting(){
            if(bulletLife() > 0f && bullet() != null) return;

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

                    if(Mathf.chanceDelta(0.1f)){
                        Tmp.v1
                            .trns(rotation, -recoil + starOffset + Mathf.curve(phase, 0f, 0.3f) * -2f)
                            .add(this);

                        pullEffect.at(x, y, rotation, new Float[]{unit.x, unit.y, Tmp.v1.x, Tmp.v1.y, charge * (3f + Mathf.range(0.2f))});
                    }
                }
            });
        }

        public float bulletLife(){
            return ReflectUtils.getField(this, bulletLifef);
        }

        public Bullet bullet(){
            return ReflectUtils.getField(this, bulletf);
        }
    }
}
