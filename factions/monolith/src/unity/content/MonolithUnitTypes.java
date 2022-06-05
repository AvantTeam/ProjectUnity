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
    public static PUUnitType
    monolithSoul,
    stele, pedestal, pilaster, pylon, monument, colossus, bastion,
    stray, tendence, liminality, calenture, hallucination, escapism, fantasy;

    private MonolithUnitTypes(){
        throw new AssertionError();
    }

    public static void load(){
        monolithSoul = register(Faction.monolith, content("monolith-soul", MonolithSoul.class, n -> new PUUnitType(n){
            {
                health = 300f;
                range = maxRange = 96f;
                lowAltitude = true;
                flying = true;
                omniMovement = false;
                playerControllable = false;

                hitSize = 12f;
                speed = 2.4f;
                rotateSpeed = 10f;
                drag = 0.08f;
                accel = 0.2f;
                fallSpeed = 1f;

                deathExplosionEffect = MonolithFx.soulDeath;
                //deathSound = PUSounds.soulDeath;
                engineColor = monolithLight;
                trail(24, unit -> new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 30, 5.6f, 8.4f, speed, 4f), monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 48, speed), 4.8f, 6f, 0.56f, monolithLight),
                    new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), 48, speed), -4.8f, 6f, 0.56f, monolithLight)
                ));
            }

            @Override
            public void update(Unit unit){
                if(unit instanceof MonolithSoul soul){
                    if(soul.trail instanceof MultiTrail trail){
                        float width = (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * soul.elevation);
                        if(trail.trails.length == 3 && soul.corporeal()){
                            MultiTrail copy = trail.copy();
                            copy.rot = BaseTrail::rot;

                            MonolithFx.trailFadeLow.at(soul.x, soul.y, width, monolithLight, copy);
                            soul.trail = new MultiTrail(new TrailHold(MonolithTrails.soul(BaseTrail.rot(unit), trailLength, speed), monolithLight));
                        }else if(trail.trails.length == 1 && !soul.corporeal()){
                            MultiTrail copy = trail.copy();
                            copy.rot = BaseTrail::rot;

                            MonolithFx.trailFadeLow.at(soul.x, soul.y, width, monolithLight, copy);
                            soul.trail = kickstartTrail(soul, createTrail(soul));
                        }
                    }

                    if(!soul.corporeal()){
                        if(Mathf.chance(Time.delta)) MonolithFx.soul.at(soul.x, soul.y, Time.time, new Vec2(soul.vel).scl(-0.3f / Time.delta));
                        if(soul.forming()){
                            for(Tile form : soul.forms()){
                                if(Mathf.chanceDelta(0.17f)) MonolithFx.spark.at(form.drawx(), form.drawy(), 4f);
                                if(Mathf.chanceDelta(0.67f)) MonolithFx.soulAbsorb.at(form.drawx(), form.drawy(), 0f, soul);
                            }
                        }else if(soul.joining() && Mathf.chanceDelta(0.33f)){
                            MonolithFx.soulAbsorb.at(soul.x + Mathf.range(6f), soul.y + Mathf.range(6f), 0f, soul.joinTarget());
                        }
                    }
                }

                super.update(unit);
            }

            @Override
            public void draw(Unit unit){
                if(!(unit instanceof MonolithSoul soul)) return;
                if(!soul.corporeal()){
                    if(!headless && soul.trail == null) soul.trail = kickstartTrail(soul, createTrail(soul));

                    float z = Draw.z();
                    Draw.z(Layer.flyingUnitLow);

                    float trailSize = (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * soul.elevation) * trailScl;
                    soul.trail.drawCap(engineColor, trailSize);
                    soul.trail.draw(engineColor, trailSize);

                    Draw.z(Layer.effect - 0.01f);

                    Draw.blend(Blending.additive);
                    Draw.color(monolith);
                    Fill.circle(soul.x, soul.y, 6f);

                    Draw.color(monolithDark);
                    Draw.rect(softShadowRegion, soul.x, soul.y, 10f, 10f);

                    Draw.blend();
                    Lines.stroke(1f, monolithDark);

                    float rotation = Time.time * 3f * Mathf.sign(unit.id % 2 == 0);
                    for(int i = 0; i < 5; i++){
                        float r = rotation + 72f * i, sect = 60f;
                        Lines.arc(soul.x, soul.y, 10f, sect / 360f, r - sect / 2f);

                        Tmp.v1.trns(r, 10f).add(soul);
                        Drawf.tri(Tmp.v1.x, Tmp.v1.y, 2.5f, 6f, r);
                    }

                    Draw.z(Layer.flyingUnit);
                    Draw.reset();

                    for(int i = 0; i < wreckRegions.length; i++){
                        float off = (360f / wreckRegions.length) * i;
                        float fin = soul.formProgress(), fout = 1f - fin;

                        Tmp.v1.trns(soul.rotation + off, fout * 24f)
                            .add(Tmp.v2.trns((Time.time + off) * 4f, fout * 3f))
                            .add(soul);

                        Draw.alpha(fin);
                        Draw.rect(wreckRegions[i], Tmp.v1.x, Tmp.v1.y, soul.rotation - 90f);
                    }

                    Lines.stroke(1.5f, monolith);

                    TextureRegion reg = Core.atlas.find("unity-monolith-chain");
                    Quat rot = MathUtils.q1.set(Vec3.Z, soul.ringRotation() + 90f).mul(MathUtils.q2.set(Vec3.X, 75f));
                    float t = Interp.pow3Out.apply(soul.joinTime()), w = reg.width * Draw.scl * 0.5f * t, h = reg.height * Draw.scl * 0.5f * t,
                        rad = t * 25f, a = Mathf.curve(t, 0.33f);

                    Draw.alpha(a);
                    DrawUtils.panningCircle(reg,
                        soul.x, soul.y, w, h,
                        rad, 360f, Time.time * 6f * Mathf.sign(soul.id % 2 == 0) + soul.id * 30f,
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
                    Draw.z(z);
                }else{
                    super.draw(soul);
                }
            }
        }));

        stele = register(Faction.monolith, content("stele", MonolithMechUnit.class, n -> new PUUnitType(n){{
            
        }}));

        pedestal = register(Faction.monolith, content("pedestal", MonolithMechUnit.class, n -> new PUUnitType(n){{}}));

        pilaster = register(Faction.monolith, content("pilaster", MonolithMechUnit.class, n -> new PUUnitType(n){{}}));

        pylon = register(Faction.monolith, content("pylon", MonolithLegsUnit.class, n -> new PUUnitType(n){{}}));

        monument = register(Faction.monolith, content("monument", MonolithLegsUnit.class, n -> new PUUnitType(n){{}}));

        colossus = register(Faction.monolith, content("colossus", MonolithLegsUnit.class, n -> new PUUnitType(n){{}}));

        bastion = register(Faction.monolith, content("bastion", MonolithLegsUnit.class, n -> new PUUnitType(n){{}}));

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
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 32, 5.6f, 8f, speed, 0f), monolithLight),
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
                    spikeWidth = 1f;
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
