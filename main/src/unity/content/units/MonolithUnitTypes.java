package unity.content.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.*;
import unity.ai.*;
import unity.ai.AssistantAI.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.content.effects.*;
import unity.entities.abilities.*;
import unity.entities.bullet.monolith.energy.*;
import unity.gen.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.type.*;
import unity.type.weapons.monolith.*;
import unity.util.*;

import static mindustry.Vars.*;

public final class MonolithUnitTypes{
    public static @FactionDef("monolith") @EntityPoint(MonolithSoul.class)
    UnitType monolithSoul;

    // monolith unit + mech
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Mechc.class, Monolithc.class})
    UnitType stele, pedestal, pilaster;

    // monolith unit + legs
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Legsc.class, Monolithc.class})
    UnitType pylon, monument, colossus, bastion;

    // monolith unit + assistant
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Assistantc.class, Monolithc.class})
    UnitType adsect, comitate/*, praesid*/;

    // monolith unit + trail
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Trailc.class, Monolithc.class})
    UnitType stray, tendence, liminality, calenture, hallucination, escapism, fantasy;

    private MonolithUnitTypes(){
        throw new AssertionError();
    }

    public static void load(){
        monolithSoul = new UnityUnitType("monolith-soul"){
            {
                defaultController = MonolithSoulAI::new;

                health = 300f;
                speed = 2.4f;
                rotateSpeed = 10f;
                accel = 0.2f;
                drag = 0.08f;
                flying = true;
                lowAltitude = true;
                fallSpeed = 1f;
                range = maxRange = miningRange = 96f;
                hitSize = 12f;
                omniMovement = false;
                engineColor = UnityPal.monolithLight;
                trailLength = 12;
                deathExplosionEffect = DeathFx.monolithSoulDeath;
                forceWreckRegion = true;

                trailType = unit -> new MultiTrail(new TrailHold(new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), 25){{
                    shrink = 1f;
                    fadeAlpha = 0.5f;
                    blend = Blending.additive;
                }}, engineColor), new TrailHold(new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), 32){{
                    shrink = 1f;
                    fadeAlpha = 0.5f;
                    blend = Blending.additive;
                }}, -4.8f, 6f, 0.75f, engineColor), new TrailHold(new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), 32){{
                    shrink = 1f;
                    fadeAlpha = 0.5f;
                    blend = Blending.additive;
                }}, 4.8f, 6f, 0.75f, engineColor)){{
                    rotation = (trail, x, y) -> unit.isValid() ? unit.rotation : trail.calcRot(x, y);
                    trailChance = 0.33f;
                    trailWidth = 1.8f;
                    trailColor = engineColor;
                }};
            }

            @Override
            public void update(Unit unit){
                if(unit instanceof MonolithSoul soul){
                    if(!(soul.trail instanceof MultiTrail trail)) return;

                    float width = (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * soul.elevation) * trailScl;
                    if(trail.trails.length == 3 && soul.corporeal()){
                        MultiTrail copy = trail.copy();
                        copy.rotation = MultiTrail::calcRot;

                        TrailFx.trailFadeLow.at(soul.x, soul.y, width, engineColor, copy);
                        soul.trail = new MultiTrail(new TrailHold(new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), trailLength){{
                            shrink = 1f;
                            fadeAlpha = 0.5f;
                            blend = Blending.additive;
                        }}, UnityPal.monolithLight)){{
                            trailChance = 0.67f;
                            trailWidth = 1.3f;
                            trailColor = engineColor;
                        }};
                    }else if(trail.trails.length == 1 && !soul.corporeal()){
                        MultiTrail copy = trail.copy();
                        copy.rotation = MultiTrail::calcRot;

                        TrailFx.trailFadeLow.at(soul.x, soul.y, width, engineColor, copy);
                        soul.trail = trailType.get(soul);
                    }

                    if(!soul.corporeal()){
                        if(Mathf.chance(Time.delta)) ParticleFx.monolithSoul.at(soul.x, soul.y, Time.time, new Vec2(soul.vel).scl(-0.3f));
                        if(soul.forming()){
                            for(Tile form : soul.forms()){
                                if(Mathf.chanceDelta(0.17f)) ParticleFx.monolithSpark.at(form.drawx(), form.drawy(), 4f);
                                if(Mathf.chanceDelta(0.67f)) LineFx.monolithSoulAbsorb.at(form.drawx(), form.drawy(), 0f, soul);
                            }
                        }else if(soul.joining() && Mathf.chanceDelta(0.33f)){
                            LineFx.monolithSoulAbsorb.at(soul.x + Mathf.range(6f), soul.y + Mathf.range(6f), 0f, soul.joinTarget());
                        }
                    }
                }

                super.update(unit);
            }

            @Override
            public void draw(Unit unit){
                if(!(unit instanceof MonolithSoul soul)) return;
                if(!soul.corporeal()){
                    float z = Draw.z();
                    Draw.z(Layer.flyingUnitLow);

                    soul.trail.draw(engineColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * soul.elevation) * trailScl);

                    Draw.z(Layer.effect - 0.01f);

                    Draw.blend(Blending.additive);
                    Draw.color(UnityPal.monolith);
                    Fill.circle(soul.x, soul.y, 6f);

                    Draw.color(UnityPal.monolithDark);
                    Draw.rect(softShadowRegion, soul.x, soul.y, 10f, 10f);

                    Draw.blend();
                    Lines.stroke(1f, UnityPal.monolithDark);

                    float rotation = Time.time * 3f * Mathf.sign(unit.id % 2 == 0);
                    for(int i = 0; i < 5; i++){
                        float r = rotation + 72f * i;
                        UnityDrawf.arcLine(soul.x, soul.y, 10f, 60f, r);

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

                    Lines.stroke(1.5f, UnityPal.monolith);

                    TextureRegion reg = Core.atlas.find("unity-monolith-chain");
                    Quat rot = Utils.q1.set(Vec3.Z, soul.ringRotation() + 90f).mul(Utils.q2.set(Vec3.X, 75f));
                    float t = Interp.pow3Out.apply(soul.joinTime()), w = reg.width * Draw.scl * 0.5f * t, h = reg.height * Draw.scl * 0.5f * t,
                        rad = t * 25f, a = Mathf.curve(t, 0.33f);

                    Draw.alpha(a);
                    UnityDrawf.panningCircle(reg,
                        soul.x, soul.y, w, h,
                        rad, 360f, Time.time * 6f * Mathf.sign(soul.id % 2 == 0) + soul.id * 30f,
                        rot, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                    );

                    Draw.color(Color.black, UnityPal.monolithDark, 0.5f);
                    Draw.alpha(a);

                    Draw.blend(Blending.additive);
                    UnityDrawf.panningCircle(Core.atlas.find("unity-line-shade"),
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
        };

        stele = new UnityUnitType("stele"){{
            health = 300f;
            speed = 0.6f;
            hitSize = 8f;
            armor = 5f;

            canBoost = true;
            boostMultiplier = 2.5f;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(name + "-shotgun"){{
                layerOffset = -0.01f;
                top = false;
                x = 5.25f;
                y = -0.25f;
                shootY = 5f;

                reload = 60f;
                recoil = 2.5f;
                inaccuracy = 0.5f;
                shootSound = Sounds.shootBig;

                bullet = new JoiningBulletType(3.5f, 36f){{
                    lifetime = 60f;
                    radius = 10f;
                    weaveScale = 5f;
                    weaveMag = 2f;
                    homingPower = 0.07f;
                    homingRange = range();
                    sensitivity = 0.5f;

                    trailInterval = 3f;
                    trailColor = UnityPal.monolithGreen;
                    hitEffect = despawnEffect = HitFx.soulConcentrateHit;
                    shootEffect = ShootFx.soulConcentrateShoot;
                    smokeEffect = Fx.lightningShoot;
                }};
            }});
        }};

        pedestal = new UnityUnitType("pedestal"){{
            health = 1200f;
            speed = 0.5f;
            rotateSpeed = 2.6f;
            hitSize = 11f;
            armor = 10f;
            singleTarget = true;
            maxSouls = 4;

            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 3.5f;
            engineOffset = 6f;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(name + "-gun"){{
                top = false;
                x = 10.75f;
                y = 2.25f;

                reload = 40f;
                recoil = 3.2f;
                shootSound = UnitySounds.energyBolt;

                BulletType subBullet = new LightningBulletType();
                subBullet.damage = 24f;

                bullet = new RicochetBulletType(3f, 72f, "shell"){
                    {
                        width = 16f;
                        height = 20f;
                        lifetime = 60f;
                        frontColor = UnityPal.monolithLight;
                        backColor = UnityPal.monolith.cpy().mul(0.75f);
                        trailColor = UnityPal.monolithDark.cpy().mul(0.5f);

                        trailChance = 0.25f;
                        trailEffect = UnityFx.ricochetTrailBig;
                        shootEffect = Fx.lightningShoot;
                        hitEffect = despawnEffect = HitFx.monolithHitBig;
                    }

                    @Override
                    public void init(Bullet b){
                        super.init(b);
                        for(int i = 0; i < 3; i++){
                            subBullet.create(b, b.x, b.y, b.rotation());
                            Sounds.spark.at(b.x, b.y, Mathf.random(0.6f, 0.8f));
                        }
                    }
                };
            }}, new ChargeShotgunWeapon(""){
                {
                    mirror = false;
                    rotate = true;
                    rotateSpeed = 8f;
                    x = 0f;
                    y = 4f;
                    shootX = 0f;
                    shootY = 24f;
                    shots = 5;
                    shotDelay = 3f;
                    reload = 72f;
                    addSequenceTime = 25f;
                    shootCone = 90f;

                    addEffect = ShootFx.pedestalShootAdd;
                    addedEffect = HitFx.monolithHitBig;
                    shootSound = UnitySounds.chainyShot;

                    bullet = new BasicBulletType(6f, 32f, "unity-twisting-shell"){{
                        width = 12f;
                        height = 16f;
                        shrinkY = 0f;
                        lifetime = 36f;
                        homingPower = 0.14f;
                        homingRange = range();

                        frontColor = UnityPal.monolith;
                        backColor = UnityPal.monolithDark;

                        trailChance = 0.25f;
                        trailEffect = UnityFx.ricochetTrailBig;
                        shootEffect = Fx.lightningShoot;
                        hitEffect = despawnEffect = HitFx.monolithHitBig;
                    }};
                }

                @Override
                public void drawCharge(float x, float y, float rotation, float shootAngle, Unit unit, ChargeShotgunMount mount){
                    if(bullet instanceof BasicBulletType b){
                        float z = Draw.z();
                        Draw.z(Layer.bullet);

                        Draw.color(b.backColor);
                        Draw.rect(b.backRegion, x, y, b.width, b.height, shootAngle - 90f);
                        Draw.color(b.frontColor);
                        Draw.rect(b.frontRegion, x, y, b.width, b.height, shootAngle - 90f);

                        Draw.z(z);
                    }
                }
            });
        }};

        pilaster = new UnityUnitType("pilaster"){{
            health = 2000f;
            speed = 0.4f;
            rotateSpeed = 2.2f;
            hitSize = 26.5f;
            armor = 15f;
            mechFrontSway = 0.55f;
            maxSouls = 5;

            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 5f;
            engineOffset = 10f;

            ammoType = new PowerAmmoType(1000);
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon("unity-monolith-medium-weapon-mount"){{
                top = false;
                x = 4f;
                y = 7.5f;
                shootY = 6f;

                rotate = true;
                recoil = 2.5f;
                reload = 25f;
                shots = 3;
                spacing = 3f;
                shootSound = Sounds.spark;

                bullet = new LightningBulletType(){{
                    damage = 30f;
                    lightningLength = 18;
                }};
            }}, new Weapon("unity-monolith-large-weapon-mount"){{
                top = false;
                x = 13f;
                y = 2f;
                shootY = 10.5f;

                rotate = true;
                rotateSpeed = 10f;
                recoil = 3f;
                reload = 40f;
                shootSound = Sounds.laser;

                bullet = new LaserBulletType(160f);
            }});
        }};

        pylon = new UnityUnitType("pylon"){{
            health = 14400f;
            speed = 0.43f;
            rotateSpeed = 1.48f;
            hitSize = 36f;
            armor = 23f;
            commandLimit = 8;
            maxSouls = 7;

            allowLegStep = hovering = true;
            visualElevation = 0.2f;
            legCount = 4;
            legExtension = 8f;
            legSpeed = 0.08f;
            legLength = 16f;
            legMoveSpace = 1.2f;
            legTrns = 0.5f;
            legBaseOffset = 11f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(name + "-laser"){{
                soundPitchMin = 1f;
                top = false;
                mirror = false;
                shake = 15f;
                shootY = 11f;
                x = y = 0f;
                reload = 280f;
                recoil = 0f;
                cooldownTime = 280f;

                shootStatusDuration = 60f * 1.8f;
                shootStatus = StatusEffects.unmoving;
                shootSound = Sounds.laserblast;
                chargeSound = Sounds.lasercharge;
                firstShotDelay = UnityFx.pylonLaserCharge.lifetime / 2f;

                bullet = UnityBullets.pylonLaser;
            }}, new Weapon("unity-monolith-large2-weapon-mount"){{
                x = 14f;
                y = 5f;
                shootY = 14f;

                rotate = true;
                rotateSpeed = 3.5f;
                shootSound = Sounds.laser;
                shake = 5f;
                reload = 20f;
                recoil = 4f;

                bullet = UnityBullets.pylonLaserSmall;
            }});
        }};

        monument = new UnityUnitType("monument"){{
            health = 32000f;
            speed = 0.42f;
            rotateSpeed = 1.4f;
            hitSize = 48f;
            armor = 32f;
            commandLimit = 8;
            maxSouls = 9;

            visualElevation = 0.3f;
            allowLegStep = hovering = true;
            legCount = 6;
            legLength = 30f;
            legExtension = 8f;
            legSpeed = 0.1f;
            legTrns = 0.5f;
            legBaseOffset = 15f;
            legMoveSpace = 1.2f;
            legPairOffset = 3f;
            legSplashDamage = 64f;
            legSplashRange = 48f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            BulletType laser = new LaserBulletType(640f);
            weapons.add(new Weapon("unity-monolith-large2-weapon-mount"){{
                top = false;
                x = 14f;
                y = 12f;
                shootY = 14f;

                rotate = true;
                rotateSpeed = 3.5f;
                reload = 36f;
                recoil = shake = 5f;
                shootSound = Sounds.laser;

                bullet = laser;
            }}, new Weapon("unity-monolith-large2-weapon-mount"){{
                top = false;
                x = 20f;
                y = 3f;
                shootY = 14f;

                rotate = true;
                rotateSpeed = 3.5f;
                reload = 48f;
                recoil = shake = 5f;
                shootSound = Sounds.laser;

                bullet = laser;
            }}, new Weapon("unity-monolith-railgun-big"){{
                mirror = false;
                x = 0f;
                y = -12f;
                shootY = 35f;
                shadow = 30f;

                reload = 200f;
                recoil = shake = 8f;
                shootCone = 2f;
                cooldownTime = 210f;
                shootSound = Sounds.railgun;

                bullet = UnityBullets.monumentRailBullet;
            }});
        }};

        colossus = new UnityUnitType("colossus"){{
            health = 60000f;
            speed = 0.4f;
            rotateSpeed = 1.2f;
            hitSize = 64f;
            armor = 45f;
            commandLimit = 8;
            maxSouls = 12;

            visualElevation = 0.5f;
            allowLegStep = hovering = true;
            legCount = 6;
            legLength = 48f;
            legExtension = 12f;
            legSpeed = 0.1f;
            legTrns = 0.5f;
            legBaseOffset = 15f;
            legMoveSpace = 0.82f;
            legPairOffset = 3f;
            legSplashDamage = 84f;
            legSplashRange = 48f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            abilities.add(new LightningSpawnAbility(8, 32f, 2f, 0.05f, 180f, 56f, 200f));

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 30f;
                y = 7.75f;
                shootY = 20f;

                reload = 144f;
                recoil = 8f;
                spacing = 1f;
                inaccuracy = 6f;
                shots = 5;
                shotDelay = 3f;
                shootSound = Sounds.laserblast;

                bullet = new LaserBulletType(1920f){{
                    width = 45f;
                    length = 400f;
                    lifetime = 32f;

                    lightningSpacing = 35f;
                    lightningLength = 4;
                    lightningDelay = 1.5f;
                    lightningLengthRand = 6;
                    lightningDamage = 48f;
                    lightningAngleRand = 30f;
                    lightningColor = Pal.lancerLaser;
                }};
            }});
        }};

        bastion = new UnityUnitType("bastion"){{
            health = 120000f;
            speed = 0.4f;
            rotateSpeed = 1.2f;
            hitSize = 67f;
            armor = 100f;
            commandLimit = 8;
            maxSouls = 15;

            visualElevation = 0.7f;
            allowLegStep = hovering = true;
            legCount = 6;
            legLength = 72f;
            legExtension = 16f;
            legSpeed = 0.12f;
            legTrns = 0.6f;
            legBaseOffset = 18f;
            legMoveSpace = 0.6f;
            legPairOffset = 3f;
            legSplashDamage = 140f;
            legSplashRange = 56f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            abilities.add(new LightningSpawnAbility(12, 16f, 3f, 0.05f, 300f, 96f, 640f));

            BulletType energy = new RicochetBulletType(6f, 50f, "shell"){{
                width = 9f;
                height = 11f;
                shrinkY = 0.3f;
                lifetime = 45f;
                weaveScale = weaveMag = 3f;
                trailChance = 0.3f;

                frontColor = UnityPal.monolithLight;
                backColor = UnityPal.monolith;
                trailColor = UnityPal.monolithDark;
                shootEffect = Fx.lancerLaserShoot;
                smokeEffect = Fx.hitLancer;
                hitEffect = despawnEffect = HitFx.monolithHitSmall;

                splashDamage = 60f;
                splashDamageRadius = 10f;

                lightning = 3;
                lightningDamage = 12f;
                lightningColor = Pal.lancerLaser;
                lightningLength = 6;
            }};

            weapons.add(new Weapon(name + "-mount"){{
                x = 9f;
                y = -11.5f;
                shootY = 10f;

                rotate = true;
                rotateSpeed = 8f;

                reload = 24f;
                recoil = 6f;
                shots = 8;
                velocityRnd = 0.3f;
                spacing = 5f;
                shootSound = UnitySounds.energyBolt;

                bullet = energy;
            }}, new Weapon(name + "-mount"){{
                x = 23.5f;
                y = 5.5f;
                shootY = 10f;

                rotate = true;
                rotateSpeed = 8f;

                reload = 15f;
                recoil = 6f;
                shots = 5;
                velocityRnd = 0.3f;
                spacing = 6f;
                shootSound = UnitySounds.energyBolt;

                bullet = energy;
            }}, new Weapon(name + "-gun"){{
                x = 12.5f;
                y = 12f;
                shootY = 13.5f;

                rotate = true;
                rotateSpeed = 6f;
                shots = 8;
                shotDelay = 3f;

                reload = 30f;
                recoil = 8f;
                shootSound = Sounds.shootBig;

                bullet = new RicochetBulletType(12.5f, 640f, "shell"){
                    {
                        width = 20f;
                        height = 25f;
                        shrinkY = 0.2f;
                        lifetime = 30f;
                        trailLength = 3;
                        pierceCap = 6;

                        frontColor = Color.white;
                        backColor = UnityPal.monolithLight;
                        trailColor = UnityPal.monolith;
                        shootEffect = Fx.lancerLaserShoot;
                        smokeEffect = Fx.hitLancer;
                        hitEffect = despawnEffect = HitFx.monolithHitBig;

                        lightning = 3;
                        lightningDamage = 12f;
                        lightningColor = Pal.lancerLaser;
                        lightningLength = 15;
                    }

                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        if(Mathf.chanceDelta(0.3f)){
                            Lightning.create(b, lightningColor, lightningDamage, b.x, b.y, b.rotation(), lightningLength / 2);
                        }
                    }
                };
            }});
        }};

        adsect = new UnityUnitType("adsect"){{
            defaultController = AssistantAI.create(Assistance.mendCore, Assistance.mine, Assistance.build);
            health = 180f;
            speed = 4f;
            accel = 0.4f;
            drag = 0.2f;
            rotateSpeed = 15f;
            flying = true;
            mineTier = 2;
            mineSpeed = 3f;
            buildSpeed = 0.8f;
            circleTarget = false;

            ammoType = new PowerAmmoType(500);
            engineColor = UnityPal.monolith;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(){{
                mirror = false;
                rotate = false;
                x = 0f;
                y = 4f;
                reload = 6f;
                shootCone = 40f;

                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(4f, 23f){{
                    healPercent = 1.5f;
                    lifetime = 40f;
                    collidesTeam = true;
                    frontColor = UnityPal.monolithLight;
                    backColor = UnityPal.monolith;
                    smokeEffect = hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                }};
            }});
        }};

        comitate = new UnityUnitType("comitate"){{
            defaultController = AssistantAI.create(Assistance.mendCore, Assistance.mine, Assistance.build, Assistance.heal);
            health = 420f;
            speed = 4.5f;
            accel = 0.5f;
            drag = 0.15f;
            rotateSpeed = 15f;
            flying = true;
            mineTier = 3;
            mineSpeed = 5f;
            buildSpeed = 1.3f;
            circleTarget = false;

            ammoType = new PowerAmmoType(500);
            engineColor = UnityPal.monolith;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(){{
                mirror = false;
                rotate = false;
                x = 0f;
                y = 6f;
                reload = 12f;
                shootCone = 40f;

                shootSound = UnitySounds.energyBolt;
                bullet = new LaserBoltBulletType(6.5f, 60f){{
                    width = 4f;
                    height = 12f;
                    keepVelocity = false;
                    healPercent = 3.5f;
                    lifetime = 35f;
                    collidesTeam = true;
                    frontColor = UnityPal.monolithLight;
                    backColor = UnityPal.monolith;
                    smokeEffect = hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                }};
            }}, new Weapon("unity-monolith-small-weapon-mount"){{
                top = false;
                mirror = alternate = true;
                x = 3f;
                y = 3f;
                reload = 40f;
                shots = 2;
                shotDelay = 5f;
                shootCone = 20f;

                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(4f, 30f){{
                    healPercent = 1.5f;
                    lifetime = 40f;
                    collidesTeam = true;
                    frontColor = UnityPal.monolithLight;
                    backColor = UnityPal.monolith;
                    smokeEffect = hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                }};
            }});
        }};

        stray = new UnityUnitType("stray"){{
            health = 300f;
            speed = 5f;
            accel = 0.08f;
            drag = 0.045f;
            rotateSpeed = 8f;
            flying = true;
            lowAltitude = true;
            rotateShooting = false;
            outlineColor = UnityPal.darkOutline;

            // just to show you how much I hate boxed types
            interface FuncI<T>{
                T get(int arg);
            }

            FuncI<TexturedTrail> trail = length -> new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), length){{
                blend = Blending.additive;
                shrink = 0f;
                fadeAlpha = 1f;
            }};

            engineColor = UnityPal.monolithLight;
            engineOffset = 11f;
            trailType = unit -> new MultiTrail(
                new TrailHold(trail.get(8), engineColor),
                new TrailHold(trail.get(12), -4.5f, 2.5f, 0.6f, engineColor),
                new TrailHold(trail.get(12), 4.5f, 2.5f, 0.6f, engineColor)
            ){{
                rotation = (trail, x, y) -> unit.isValid() ? unit.rotation : trail.calcRot(x, y);
                trailChance = 0.33f;
                trailWidth = 1.3f;
                trailColor = engineColor;
            }};
            trailLength = 12;

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    radius = 5.5f;
                    thickness = 1f;
                    spikes = 4;
                    spikeOffset = 1.5f;
                    spikeWidth = 2f;
                    spikeLength = 4f;
                    color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                }}, new Ring(){{
                    shootY = radius = 2.5f;
                    rotate = false;
                    thickness = 1f;
                    divisions = 2;
                    divisionSeparation = 30f;
                    angleOffset = 90f;
                    color = UnityPal.monolith;
                }});

                x = y = 0f;
                mirror = false;
                rotate = true;
                reload = 60f;
                shots = 6;
                shotDelay = 1f;
                inaccuracy = 30f;
                layerOffset = 10f;
                eyeRadius = 1.8f;

                shootSound = UnitySounds.energyBolt;
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

                        frontColor = trailColor = UnityPal.monolith;
                        backColor = UnityPal.monolithDark;
                        trailChance = 0.3f;
                        trailParam = 1.5f;
                        trailWidth = 2f;
                        trailLength = 6;

                        shootEffect = Fx.lightningShoot;
                        hitEffect = despawnEffect = Fx.hitLancer;
                    }

                    @Override
                    public void updateTrail(Bullet b){
                        if(!headless && trailLength > 0 && b.trail == null) b.trail = trail.get(trailLength);
                        super.updateTrail(b);
                    }

                    @Override
                    public void removed(Bullet b){
                        super.removed(b);
                        b.trail = null;
                    }
                };
            }});
        }};

        tendence = new UnityUnitType("tendence"){{
            health = 1200f;
            speed = 4.2f;
            accel = 0.08f;
            drag = 0.045f;
            rotateSpeed = 8f;
            flying = true;
            lowAltitude = true;
            rotateShooting = false;
            outlineColor = UnityPal.darkOutline;

            // again, i hate boxed types
            interface FuncI<T>{
                T get(int arg);
            }

            FuncI<TexturedTrail> trail = length -> new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), length){{
                blend = Blending.additive;
                shrink = 1f;
                fadeAlpha = 0.5f;
            }};

            engineColor = engineColorInner = Color.clear;
            engineOffset = 14f;
            trailType = unit -> new MultiTrail(
                new TrailHold(trail.get(12), -5f, 6f, 1f, engineColor),
                new TrailHold(trail.get(12), 5f, 6f, 1f, engineColor)
            ){{
                rotation = (trail, x, y) -> unit.isValid() ? unit.rotation : trail.calcRot(x, y);
                trailChance = 0.5f;
                trailWidth = 1.3f;
                trailColor = UnityPal.monolithLight;
            }};
            trailLength = 12;

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    radius = 6.5f;
                    thickness = 1f;
                    spikes = 8;
                    spikeOffset = 1.5f;
                    spikeWidth = 2f;
                    spikeLength = 5f;
                    color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                }}, new Ring(){{
                    shootY = radius = 3f;
                    rotate = false;
                    thickness = 1f;
                    divisions = 2;
                    divisionSeparation = 30f;
                    angleOffset = 90f;
                    color = UnityPal.monolith;
                }});

                x = 0f;
                y = 1f;
                mirror = false;
                rotate = true;
                reload = 72f;
                firstShotDelay = 35f;
                inaccuracy = 15f;
                layerOffset = 10f;
                eyeRadius = 1.8f;
                parentizeEffects = true;

                chargeSound = UnitySounds.energyCharge;
                shootSound = UnitySounds.energyBlast;
                bullet = new BasicBulletType(4.8f, 72f, "shell"){
                    {
                        lifetime = 48f;
                        width = 16f;
                        height = 25f;
                        keepVelocity = false;
                        homingPower = 0.03f;
                        homingRange = range() * 2f;

                        lightning = 3;
                        lightningColor = UnityPal.monolithLight;
                        lightningDamage = 12f;
                        lightningLength = 12;

                        frontColor = trailColor = UnityPal.monolith;
                        backColor = UnityPal.monolithDark;
                        trailEffect = ParticleFx.monolithSpark;
                        trailChance = 0.5f;
                        trailParam = 6f;
                        trailWidth = 4f;
                        trailLength = 12;

                        hitEffect = despawnEffect = HitFx.tendenceHit;
                        chargeShootEffect = ShootFx.tendenceShoot;
                        shootEffect = ChargeFx.tendenceCharge;
                    }

                    @Override
                    public void draw(Bullet b){
                        super.draw(b);

                        TextureRegion reg = Core.atlas.white(), light = Core.atlas.find("unity-line-shade");

                        Lines.stroke(2f);
                        for(int i = 0; i < 2; i++){
                            float r = b.id * 20f + Time.time * 6f * Mathf.sign(b.id % 2 == 0);
                            Utils.q1.set(i == 0 ? Vec3.X : Vec3.Y, r).mul(Utils.q2.set(Tmp.v31.set(Tmp.v1.trns(b.rotation(), 1f), 0f), r * Mathf.signs[i]));

                            Draw.color(i == 0 ? UnityPal.monolith : UnityPal.monolithDark);
                            UnityDrawf.panningCircle(reg,
                                b.x, b.y, 1f, 1f,
                                10f + i * 4f, 360f, 0f,
                                Utils.q1, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                            );

                            Draw.color(Color.black, UnityPal.monolithDark, i == 0 ? 0.5f : 0.25f);
                            Draw.blend(Blending.additive);

                            UnityDrawf.panningCircle(light,
                                b.x, b.y, 5f, 5f,
                                10f + i * 4f, 360f, 0f,
                                Utils.q1, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
                            );

                            Draw.blend();
                        }

                        Draw.reset();
                    }

                    @Override
                    public void updateTrail(Bullet b){
                        if(!headless && trailLength > 0 && b.trail == null) b.trail = trail.get(trailLength);
                        super.updateTrail(b);
                    }

                    @Override
                    public void removed(Bullet b){
                        super.removed(b);
                        b.trail = null;
                    }
                };
            }});
        }};

        liminality = new UnityUnitType("liminality"){{

        }};

        calenture = new UnityUnitType("calenture"){{

        }};

        hallucination = new UnityUnitType("hallucination"){{

        }};

        escapism = new UnityUnitType("escapism"){{

        }};

        fantasy = new UnityUnitType("fantasy"){{

        }};
    }
}
