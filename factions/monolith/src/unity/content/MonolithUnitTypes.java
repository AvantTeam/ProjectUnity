package unity.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.ammo.*;
import mindustry.world.*;
import unity.assets.list.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.entities.type.bullet.energy.*;
import unity.entities.type.bullet.laser.*;
import unity.entities.weapons.*;
import unity.gen.entities.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.graphics.trail.*;
import unity.mod.*;
import unity.util.*;

import static mindustry.Vars.*;
import static unity.gen.entities.EntityRegistry.*;
import static unity.graphics.MonolithPalettes.*;
import static unity.mod.FactionRegistry.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} unit types.
 * @author GlennFolker
 */
public final class MonolithUnitTypes{
    public static final MonolithSoulType[] souls = new MonolithSoulType[MonolithSoul.constructors.length];

    public static PUUnitType
    stele, pedestal, pilaster, pylon, monument, colossus, bastion,
    stray, tendence, liminality, calenture, hallucination, escapism, fantasy;

    private MonolithUnitTypes(){
        throw new AssertionError();
    }

    public static void load(){
        souls[0] = register(Faction.monolith, content("monolith-soul-0", MonolithSoul.class, n -> new MonolithSoulType(n, new MonolithSoulProps(){{
            transferAmount = 1;
            formAmount = 1;
            formDelta = 0.2f;
        }}){
            {
                health = 80f;
                range = maxRange = 48f;

                hitSize = 7.2f;
                speed = 3.6f;
                rotateSpeed = 7.5f;
                drag = 0.04f;
                accel = 0.18f;

                formTileChance = 0.17f;
                formAbsorbChance = 0f;

                engineColor = trailColor = monolithLight;
                engineSize = 2f;
                engineOffset = 3.5f;

                trail(18, unit -> new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 24, 0), 0f, engineOffset, monolithLight)
                ));

                corporealTrail = soul -> new MultiTrail(
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(soul), 18, 0), monolithLight),
                    new TrailHold(MonolithTrails.singleSoul(6), monolith)
                );
            }

            @Override
            public void drawBase(MonolithSoul soul){
                draw(soul.x, soul.y, soul.rotation, 4f, 0.5f, 4.5f, 1.8f, -4.5f);
            }

            @Override
            public void drawForm(MonolithSoul soul){
                Tmp.v1.trns(soul.rotation, 1.8f).add(soul);

                Lines.stroke(2f + Mathf.absin(12f, 0.1f));
                for(Tile tile : soul.forms){
                    long seed = soul.id + tile.pos();
                    Tmp.v2.trns(Mathf.randomSeed(seed, 360f) + Time.time * MathUtils.randomSeedSign(seed), 3f).add(tile);

                    DrawUtils.lineFalloff(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.c1.set(monolithDark).a(0.4f), monolithLight, 4, 0.67f);
                }

                Draw.reset();
            }

            @Override
            public void drawJoin(MonolithSoul soul){
                if(soul.joinTarget != null){
                    Tmp.v1.trns(soul.rotation, 1.8f).add(soul);

                    Lines.stroke(2f + Mathf.absin(12f, 0.1f));
                    DrawUtils.lineFalloff(Tmp.v1.x, Tmp.v1.y, soul.joinTarget.getX(), soul.joinTarget.getY(), Tmp.c1.set(monolithDark).a(0.4f), monolithLight, 4, 0.67f);
                    Draw.reset();
                }

                Lines.stroke(1.5f, monolith);

                TextureRegion reg = Core.atlas.white();
                Quat rot = MathUtils.q1.set(Vec3.Z, soul.ringRotation() + 90f).mul(MathUtils.q2.set(Vec3.X, 75f));
                float
                    t = Interp.pow5Out.apply(soul.joinTime()),
                    rad = t * 7.2f, a = Mathf.curve(t, 0.25f),
                    s = Lines.getStroke();

                Draw.alpha(a);
                DrawUtils.panningCircle(reg,
                    soul.x, soul.y, s, s,
                    rad, 360f, Time.time * 4f * Mathf.sign(soul.id % 2 == 0) + soul.id * 30f,
                    rot, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                );

                Draw.color(Color.black, monolithDark, 0.67f);
                Draw.alpha(a);

                Draw.blend(Blending.additive);
                DrawUtils.panningCircle(Core.atlas.find("unity-line-shade"),
                    soul.x, soul.y, s + 6f, s + 6f,
                    rad, 360f, 0f,
                    rot, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                );

                Draw.blend();
            }
        }));

        souls[1] = register(Faction.monolith, content("monolith-soul-1", MonolithSoul.class, n -> new MonolithSoulType(n, new MonolithSoulProps(){{
            transferAmount = 2;
            formAmount = 3;
            formDelta = 0.4f;
        }}){
            {
                health = 180f;
                range = maxRange = 72f;

                hitSize = 9.6f;
                speed = 3f;
                rotateSpeed = 7.2f;
                drag = 0.04f;
                accel = 0.18f;

                //formTileChance = 0.17f;
                //formAbsorbChance = 0f;

                engineColor = trailColor = monolithLight;
                engineSize = 2f;
                engineOffset = 3.5f;
            }

            @Override
            public void drawBase(MonolithSoul soul){
                draw(soul.x, soul.y, soul.rotation, 5.2f, 1f, 6f, 1f, -7f);
            }

            @Override
            public void drawEyes(MonolithSoul soul){
                Tmp.v1.trns(soul.rotation, 6f);
                Tmp.v2.trns(soul.rotation, 3f).add(Tmp.v1);

                Lines.stroke(1.4f * 4f, Tmp.c1.set(0f, 0f, 0f, 0.4f));
                Lines.line(softShadowRegion, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, true);

                Lines.stroke(1.4f);
                DrawUtils.lineFalloff(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.c1.set(monolithDark).a(0.4f), monolithLight, 3, 0.75f);
                Draw.reset();
            }
        }));

        souls[2] = register(Faction.monolith, content("monolith-soul-2", MonolithSoul.class, n -> new MonolithSoulType(n, new MonolithSoulProps(){{
            transferAmount = 4;
            formAmount = 5;
            formDelta = 0.6f;

            joinEffect = MonolithFx.soulJoin;
            transferEffect = MonolithFx.soulTransfer;
        }}){
            {
                health = 300f;
                range = maxRange = 96f;

                hitSize = 12f;
                speed = 2.4f;
                rotateSpeed = 10f;
                drag = 0.08f;
                accel = 0.2f;

                trailChance = 1f;
                formTileChance = 0.17f;
                formAbsorbChance = 0.67f;
                joinChance = 0.33f;
                trailEffect = MonolithFx.soul;
                formTileEffect = MonolithFx.spark;
                formAbsorbEffect = MonolithFx.soulAbsorb;
                joinEffect = MonolithFx.soulAbsorb;

                deathExplosionEffect = MonolithFx.soulDeath;
                //deathSound = PUSounds.soulDeath;

                engineColor = trailColor = monolithLight;
                trail(24, unit -> new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 30, 2, 5.6f, 8.4f, speed, 4f, null), monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 48, speed), 4.8f, 6f, 0.56f, monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 48, speed), -4.8f, 6f, 0.56f, monolithLight)
                ));

                corporealTrail = soul -> MonolithTrails.soul(BaseTrail.rot(soul), trailLength, speed);
            }

            @Override
            public void drawBase(MonolithSoul soul){
                draw(soul.x, soul.y, soul.rotation, 7f, 1f, 6f, 1f, -7f);
                Lines.stroke(1f, monolithDark);

                float rotation = Time.time * 3f * Mathf.sign(soul.id % 2 == 0);
                for(int i = 0; i < 5; i++){
                    float r = rotation + 72f * i, sect = 60f;
                    Lines.arc(soul.x, soul.y, 10f, sect / 360f, r - sect / 2f);

                    Tmp.v1.trns(r, 10f).add(soul);
                    Drawf.tri(Tmp.v1.x, Tmp.v1.y, 2.5f, 6f, r);
                }

                Draw.reset();
            }

            @Override
            public void drawEyes(MonolithSoul soul){
                for(int sign : Mathf.signs){

                }
            }

            @Override
            public void drawJoin(MonolithSoul soul){
                Lines.stroke(1.5f, monolith);

                TextureRegion reg = Core.atlas.find("unity-monolith-chain");
                Quat rot = MathUtils.q1.set(Vec3.Z, soul.ringRotation() + 90f).mul(MathUtils.q2.set(Vec3.X, 75f));
                float
                    t = Interp.pow3Out.apply(soul.joinTime()),
                    rad = t * 25f, a = Mathf.curve(t, 0.33f),
                    w = (Mathf.PI2 * rad) / (reg.width * Draw.scl * 0.5f), h = w * ((float)reg.height / reg.width);

                Draw.alpha(a);
                DrawUtils.panningCircle(reg,
                    soul.x, soul.y, w, h,
                    rad, 360f, Time.time * 4f * Mathf.sign(soul.id % 2 == 0) + soul.id * 30f,
                    rot, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                );

                Draw.color(Color.black, monolithDark, 0.67f);
                Draw.alpha(a);

                Draw.blend(Blending.additive);
                DrawUtils.panningCircle(Core.atlas.find("unity-line-shade"),
                    soul.x, soul.y, w + 6f, h + 6f,
                    rad, 360f, 0f,
                    rot, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                );

                Draw.blend();
            }
        }));

        souls[3] = register(Faction.monolith, content("monolith-soul-3", MonolithSoul.class, n -> new MonolithSoulType(n, new MonolithSoulProps(){{
            transferAmount = 8;
            formAmount = 7;
            formDelta = 0.8f;
        }}){{

        }}));

        {
            StringBuilder msg = null;
            for(int i = 0; i < souls.length; i++){
                MonolithSoulType type = souls[i];
                if(type == null){
                    if(msg == null){
                        msg = new StringBuilder("Missing soul type for index ").append(i);
                    }else{
                        msg.append(", ").append(i);
                    }
                }else{
                    MonolithSoul.constructors[i] = type::create;
                }
            }

            if(msg != null) throw new IllegalStateException(msg.append("!").toString());
        }

        stele = register(Faction.monolith, content("stele", MonolithMechUnit.class, n -> new PUUnitType(n){{
            
        }}));

        pedestal = register(Faction.monolith, content("pedestal", MonolithMechUnit.class, n -> new PUUnitType(n){{

        }}));

        pilaster = register(Faction.monolith, content("pilaster", MonolithMechUnit.class, n -> new PUUnitType(n){{

        }}));

        pylon = register(Faction.monolith, content("pylon", MonolithLegsUnit.class, n -> new PUUnitType(n){{

        }}));

        monument = register(Faction.monolith, content("monument", MonolithLegsUnit.class, n -> new PUUnitType(n){{

        }}));

        colossus = register(Faction.monolith, content("colossus", MonolithLegsUnit.class, n -> new PUUnitType(n){{

        }}));

        bastion = register(Faction.monolith, content("bastion", MonolithLegsUnit.class, n -> new PUUnitType(n){{

        }}));

        stray = register(Faction.monolith, content("stray", MonolithUnit.class, n -> new PUUnitType(n){{
            health = 300f;
            faceTarget = false;
            lowAltitude = true;
            flying = true;

            hitSize = 12f;
            speed = 5f;
            rotateSpeed = 8f;
            drag = 0.045f;
            accel = 0.08f;

            outlineColor = monolithOutline;
            ammoType = new PowerAmmoType(500f);

            engineColor = monolithLight;
            engineSize = 2.5f;
            engineOffset = 12.5f;
            setEnginesMirror(new PUUnitEngine(4.5f, -10f, 1.8f, -90f));

            trail(unit -> {
                TexturedTrail right = MonolithTrails.singlePhantasmal(20, new VelAttrib(0.14f, 0f, (t, v) -> unit.rotation, 0.15f));
                right.trailChance = 0f;
                right.gradientInterp = Interp.pow3Out;
                right.fadeInterp = e -> (1f - Interp.pow2Out.apply(Mathf.curve(e, 0.84f, 1f))) * Interp.pow2In.apply(Mathf.curve(e, 0f, 0.56f)) * 0.6f;
                right.sideFadeInterp = e -> (1f - Interp.pow3Out.apply(Mathf.curve(e, 0.7f, 1f))) * Interp.pow3In.apply(Mathf.curve(e, 0f, 0.7f)) * 0.6f;

                TexturedTrail left = right.copy();
                left.attrib(VelAttrib.class).velX *= -1f;

                return new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 16, 2, 3.6f, 6f, speed, 2f, null), monolithLight),
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
                        if(!headless && b.trail == null){
                            TexturedTrail trail = MonolithTrails.singlePhantasmal(trailLength);
                            trail.forceCap = true;
                            trail.kickstart(b.x, b.y);

                            b.trail = trail;
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

            outlineColor = monolithOutline;
            ammoType = new PowerAmmoType(1000f);

            prop(new MonolithProps(){{
                maxSouls = 4;
                soulLackStatus = content.getByName(ContentType.status, "unity-disabled");
            }});

            engineOffset = 8f;
            engineSize = 2.5f;
            engineColor = monolith;
            setEnginesMirror(new PUUnitEngine(5f, -11.5f, 2.5f, -90f));

            trail(unit -> {
                VelAttrib vel = new VelAttrib(-0.18f, 0f, (t, v) -> unit.rotation);
                return new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 24, speed, vel), 5f, -3.5f, 1f, monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 24, speed, vel.flip()), -5f, -3.5f, 1f, monolithLight)
                );
            });

            parts.add(new RegionPart("-top"){{
                outline = false;
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
                        if(!headless && b.trail == null){
                            MultiTrail trail = MonolithTrails.soul(trailLength, 6f, trailWidth - 0.3f, speed);
                            trail.forceCap = true;
                            trail.kickstart(b.x, b.y);

                            b.trail = trail;
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
            health = 2000f;
            faceTarget = false;
            lowAltitude = true;
            flying = true;

            strafePenalty = 0.1f;
            hitSize = 36f;
            speed = 3.5f;
            rotateSpeed = 3.6f;
            drag = 0.06f;
            accel = 0.08f;

            outlineColor = monolithOutline;
            ammoType = new PowerAmmoType(2000f);

            prop(new MonolithProps(){{
                maxSouls = 5;
                soulLackStatus = content.getByName(ContentType.status, "unity-disabled");
            }});

            engineOffset = 22.25f;
            engineSize = 4f;
            engineColor = monolithLight;
            setEnginesMirror(
                new PUUnitEngine(17.875f, -16.25f, 3f, -90f),
                new PUUnitEngine(9f, -11f, 3.5f, -45f, monolith)
            );

            trail(unit -> {
                VelAttrib velInner = new VelAttrib(0.2f, 0f, (t, v) -> unit.rotation, 0.25f);
                VelAttrib velOuter = new VelAttrib(0.24f, 0.1f, (t, v) -> unit.rotation, 0.2f);
                return new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 32, 2, 5.6f, 8f, speed, 0f, null), monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 48, 6f, 3.2f, speed, velInner), 9f, 11.25f, 0.75f, monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 48, 6f, 3.2f, speed, velInner.flip()), -9f, 11.25f, 0.75f, monolithLight),
                    new TrailHold(MonolithTrails.singlePhantasmal(10, velOuter), 17.875f, 6f, 0.6f, monolithLight),
                    new TrailHold(MonolithTrails.singlePhantasmal(10, velOuter.flip()), -17.875f, 6f, 0.6f, monolithLight)
                );
            });

            parts.add(
                new RegionPart("-middle"){{
                    outline = false;
                    layer = Layer.bullet - 0.02f;
                }}, new RegionPart("-top"){{
                    outline = false;
                    layer = Layer.effect + 0.0199f;
                }}
            );

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    radius = 9f;
                    thickness = 1f;
                    spikes = 6;
                    spikeOffset = 1.5f;
                    spikeWidth = 2f;
                    spikeLength = 4f;
                    color = monolithDark.cpy().lerp(monolith, 0.5f);
                }}, new Ring(){{
                    shootY = radius = 5.6f;
                    rotate = false;
                    thickness = 1f;
                    divisions = 2;
                    divisionSeparation = 30f;
                    angleOffset = 90f;
                    color = monolith;
                }}, new Ring(){{
                    radius = 2f;
                    thickness = 1f;
                    spikes = 4;
                    spikeOffset = 0.4f;
                    spikeWidth = 1.5f;
                    spikeLength = 1.5f;
                    flip = true;
                    color = monolithDark;
                }});

                x = 0f;
                y = 5f;
                mirror = false;
                rotate = true;
                reload = 56f;
                layerOffset = 10f;
                eyeRadius = 2f;

                shootSound = Sounds.laser;
                bullet = new HelixLaserBulletType(240f){{
                    sideWidth = 1.4f;
                    sideAngle = 30f;
                }};
            }});
        }}));

        calenture = register(Faction.monolith, content("calenture", MonolithUnit.class, n -> new PUUnitType(n){{
            health = 14400f;
            faceTarget = false;
            lowAltitude = true;
            flying = true;

            strafePenalty = 0.3f;
            hitSize = 48f;
            speed = 3.5f;
            rotateSpeed = 3.6f;
            drag = 0.06f;
            accel = 0.08f;

            outlineColor = monolithOutline;
            ammoType = new PowerAmmoType(3000f);

            prop(new MonolithProps(){{
                maxSouls = 7;
                soulLackStatus = content.getByName(ContentType.status, "unity-disabled");
            }});

            parts.add(
                new RegionPart(name + "-middle"){{
                    outline = false;
                    layer = Layer.bullet - 0.02f;
                }}, new RegionPart(name + "-top"){{
                    outline = false;
                    layer = Layer.effect + 0.0199f;
                }}
            );

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    color = monolithLight;
                    radius = 14f;
                    rotateSpeed = 4f;
                    spikes = 8;
                    spikeOffset = 2f;
                    spikeWidth = 3f;
                    spikeLength = 4.5f;
                }}, new Ring(){{
                    color = monolithDark.cpy().lerp(monolith, 0.5f);
                    thickness = 1f;
                    radius = 12f;
                    rotateSpeed = 3.2f;
                    flip = true;
                    divisions = 2;
                    divisionSeparation = 30f;
                }}, new Ring(){{
                    color = monolith;
                    shootY = radius = 8.5f;
                    rotate = false;
                    angleOffset  = 90f;
                    divisions = 2;
                    divisionSeparation = 30f;
                }}, new Ring(){{
                    color = monolithDark;
                    thickness = 1f;
                    radius = 4f;
                    rotateSpeed = 2.4f;
                    spikes = 6;
                    spikeOffset = 0.4f;
                    spikeWidth = 2f;
                    spikeLength = 2f;
                }});

                x = 0f;
                y = 10f;
                mirror = false;
                rotate = true;
                reload = 120f;
                layerOffset = 10f;
                eyeRadius = 2f;
            }});
        }}));

        hallucination = register(Faction.monolith, content("hallucination", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));

        escapism = register(Faction.monolith, content("escapism", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));

        fantasy = register(Faction.monolith, content("fantasy", MonolithUnit.class, n -> new PUUnitType(n){{

        }}));
    }
}
