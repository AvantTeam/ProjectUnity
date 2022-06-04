package unity.content;

import arc.graphics.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.ammo.*;
import unity.assets.list.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.entities.type.bullet.energy.*;
import unity.entities.weapons.*;
import unity.gen.entities.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.graphics.trail.*;
import unity.mod.*;

import static mindustry.Vars.*;
import static unity.gen.entities.EntityRegistry.*;
import static unity.graphics.Palettes.*;
import static unity.mod.FactionRegistry.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} unit types.
 * @author GlennFolker
 */
public final class MonolithUnitTypes{
    public static PUUnitType stray, tendence, liminality, calenture, hallucination, escapism, fantasy;

    private MonolithUnitTypes(){
        throw new AssertionError();
    }

    public static void load(){
        stray = register(Faction.monolith, content("stray", MonolithUnit.class, n -> new PUUnitType(n){{
            health = 300f;
            speed = 5f;
            accel = 0.08f;
            drag = 0.045f;
            rotateSpeed = 8f;
            flying = true;
            hitSize = 12f;
            lowAltitude = true;
            faceTarget = false;
            outlineColor = darkOutline;

            engineColor = monolithLight;
            engineSize = 2.5f;
            engineOffset = 12.5f;
            setEnginesMirror(new UnitEngine(4.5f, -10f, 1.8f, -90f));

            trail(unit -> {
                TexturedTrail right = MonolithTrails.singlePhantasmal(20, new VelAttrib(0.14f, 0f, (t, v) -> unit.rotation, 0.15f));
                right.trailChance = 0f;
                right.gradientInterp = Interp.pow3Out;
                right.fadeInterp = e -> (1f - Interp.pow2Out.apply(Mathf.curve(e, 0.84f, 1f))) * Interp.pow2In.apply(Mathf.curve(e, 0f, 0.56f)) * 0.6f;
                right.sideFadeInterp = e -> (1f - Interp.pow3Out.apply(Mathf.curve(e, 0.7f, 1f))) * Interp.pow3In.apply(Mathf.curve(e, 0f, 0.7f)) * 0.6f;

                TexturedTrail left = right.copy();
                left.attrib(VelAttrib.class).velX *= -1f;

                return new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 16, 3.6f, 6f, speed, 2f), monolithLight),
                    new TrailHold(right, 4.5f, 2.5f, 0.3f, monolithLight),
                    new TrailHold(left, -4.5f, 2.5f, 0.3f, monolithLight)
                );
            });

            prop(new MonolithProps(){{
                maxSouls = 3;
                soulLackStatus = content.getByName(ContentType.status, "unity-disabled");
            }});

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    radius = 5.5f;
                    thickness = 1f;
                    spikes = 4;
                    spikeOffset = 1.5f;
                    spikeWidth = 2f;
                    spikeLength = 4f;
                    color = monolithDark.cpy().lerp(monolith, 0.5f);
                }}, new Ring(){{
                    shootY = radius = 2.5f;
                    rotate = false;
                    thickness = 1f;
                    divisions = 2;
                    divisionSeparation = 30f;
                    angleOffset = 90f;
                    color = monolith;
                }});

                x = y = 0f;
                mirror = false;
                rotate = true;
                reload = 60f;
                inaccuracy = 30f;
                layerOffset = 10f;
                eyeRadius = 1.8f;

                shootSound = PUSounds.energyBolt;
                shoot = new ShootPattern(){{
                    shots = 6;
                    shotDelay = 1f;
                }};

                bullet = new BasicBulletType(1f, 6f, "shell"){
                    {
                        drag = -0.08f;
                        lifetime = 35f;
                        width = 8f;
                        height = 13f;

                        homingDelay = 6f;
                        homingPower = 0.09f;
                        homingRange = 160f;
                        weaveMag = 6f;
                        keepVelocity = false;

                        frontColor = trailColor = monolith;
                        backColor = monolithDark;
                        trailChance = 0.3f;
                        trailParam = 1.5f;
                        trailWidth = 2f;
                        trailLength = 12;

                        shootEffect = MonolithFx.strayShoot;
                        hitEffect = despawnEffect = Fx.hitLancer;
                    }

                    @Override
                    public void updateTrail(Bullet b){
                        if(!headless && b.trail == null) b.trail = MonolithTrails.singlePhantasmal(trailLength);
                        super.updateTrail(b);
                    }

                    @Override
                    public void removed(Bullet b){
                        super.removed(b);
                        b.trail = null;
                    }
                };
            }});
        }}));

        tendence = register(Faction.monolith, content("tendence", MonolithUnit.class, n -> new PUUnitType(n){{
            health = 1200f;
            faceTarget = false;
            lowAltitude = true;
            flying = true;

            hitSize = 16f;
            speed = 4.2f;
            rotateSpeed = 7.2f;
            drag = 0.045f;
            accel = 0.08f;

            outlineColor = darkOutline;
            ammoType = new PowerAmmoType(1000f);

            prop(new MonolithProps(){{
                maxSouls = 4;
                soulLackStatus = content.getByName(ContentType.status, "unity-disabled");
            }});

            engineOffset = 8f;
            engineSize = 2.5f;
            engineColor = monolith;
            setEnginesMirror(new UnitEngine(5f, -11.5f, 2.5f, -90f));

            trail(unit -> {
                VelAttrib vel = new VelAttrib(-0.18f, 0f, (t, v) -> unit.rotation);
                return new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 24, speed, vel), 5f, -3.5f, 1f, monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 24, speed, vel.flip()), -5f, -3.5f, 1f, monolithLight)
                );
            });

            parts.add(new RegionPart("-top"){{
                layer = Layer.bullet - 0.02f;
            }});

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    radius = 6.5f;
                    thickness = 1f;
                    spikes = 8;
                    spikeOffset = 1.5f;
                    spikeWidth = 2f;
                    spikeLength = 4f;
                    color = monolithDark.cpy().lerp(monolith, 0.5f);
                }}, new Ring(){{
                    shootY = radius = 3f;
                    rotate = false;
                    thickness = 1f;
                    divisions = 2;
                    divisionSeparation = 30f;
                    angleOffset = 90f;
                    color = monolith;
                }});

                x = 0f;
                y = 1f;
                mirror = false;
                rotate = true;
                reload = 72f;
                inaccuracy = 15f;
                layerOffset = 10f;
                eyeRadius = 1.8f;
                parentizeEffects = true;

                chargeSound = PUSounds.energyCharge;
                shootSound = PUSounds.energyBlast;
                shoot = new ShootPattern(){{
                    firstShotDelay = 35f;
                }};

                bullet = new RevolvingRingsBulletType(4.8f, 72f, "shell"){
                    {
                        lifetime = 48f;
                        width = 16f;
                        height = 25f;
                        keepVelocity = false;
                        homingPower = 0.03f;
                        homingRange = range() * 2f;

                        lightning = 3;
                        lightningColor = monolithLight;
                        lightningDamage = 12f;
                        lightningLength = 12;

                        frontColor = trailColor = monolith;
                        backColor = monolithDark;
                        trailEffect = MonolithFx.spark;
                        trailChance = 0.4f;
                        trailParam = 6f;
                        trailWidth = 5f;
                        trailLength = 32;

                        radius = new float[]{10f, 14f};
                        thickness = new float[]{2f, 2f};
                        colors = new Color[]{monolith, monolithDark};
                        glows = new Color[]{Color.black.cpy().lerp(monolithDark, 0.5f), Color.black.cpy().lerp(monolithDark, 0.25f)};

                        hitEffect = despawnEffect = MonolithFx.tendenceHit;
                        chargeEffect = MonolithFx.tendenceCharge;
                        shootEffect = MonolithFx.tendenceShoot;
                    }

                    @Override
                    public void updateTrail(Bullet b){
                        if(!headless && trailLength > 0 && b.trail == null){
                            MultiTrail trail = MonolithTrails.soul(trailLength, 6, trailWidth - 0.3f, speed);
                            trail.forceCap = true;

                            b.trail = trail;
                            for(int i = 0; i < b.trail.length; i++) b.trail.update(b.x, b.y, 0f); // Give a head start.
                        }

                        super.updateTrail(b);
                    }

                    @Override
                    public void removed(Bullet b){
                        super.removed(b);
                        b.trail = null;
                    }
                };
            }});
        }}));

        liminality = register(Faction.monolith, content("liminality", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));

        calenture = register(Faction.monolith, content("calenture", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));

        hallucination = register(Faction.monolith, content("hallucination", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));

        escapism = register(Faction.monolith, content("escapism", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));

        fantasy = register(Faction.monolith, content("fantasy", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));
    }
}
