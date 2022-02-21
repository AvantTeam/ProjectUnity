package unity.content;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.content.effects.*;
import unity.entities.bullet.anticheat.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.entities.bullet.energy.*;
import unity.entities.bullet.exp.*;
import unity.entities.bullet.kami.*;
import unity.entities.bullet.laser.*;
import unity.entities.bullet.misc.BlockStatusEffectBulletType;
import unity.gen.*;
import unity.graphics.*;
import unity.world.blocks.exp.*;

import static mindustry.Vars.*;
import static unity.content.UnityStatusEffects.*;

public class UnityBullets{
    public static BulletType
        laser, shardLaserFrag, shardLaser, frostLaser, branchLaserFrag, branchLaser, distField, smallDistField, fractalLaser, kelvinWaterLaser,
        kelvinSlagLaser, kelvinOilLaser, kelvinCryofluidLaser, kelvinLiquidLaser, celsiusSmoke, kelvinSmoke,
        breakthroughLaser, laserGeyser,

        basicMissile, citadelFlame,

        sapLaser, sapArtilleryFrag, continuousSapLaser,

        coalBlaze, pyraBlaze,

        falloutLaser, catastropheLaser, calamityLaser, extinctionLaser,

        plagueMissile,

        gluonWhirl, gluonEnergyBall, singularityBlackHole, singularityEnergyBall,

        orb, shockBeam, currentStroke, shielderBullet, plasmaFragTriangle, plasmaTriangle, surgeBomb,

        pylonLightning, pylonLaser, pylonLaserSmall, monumentRailBullet,

        scarShrapnel, scarMissile,

        kamiBullet1, kamiBullet2, kamiBullet3, kamiLaser, kamiVariableLaser, kamiSmallLaser,

        ricochetSmall, ricochetMedium, ricochetBig,

        stopLead, stopMonolite, stopSilicon,

        supernovaLaser,

        endLightning,

        ravagerLaser, ravagerArtillery,

        oppressionArea, oppressionShell,

        missileAntiCheat,

        endLaserSmall, endLaser,

        laserZap,

        plasmaBullet, phantasmalBullet,

        teleportLightning,

        statusEffect;

    //only enhanced
    public static BasicBulletType standardDenseLarge, standardHomingLarge, standardIncendiaryLarge, standardThoriumLarge, standardDenseHeavy, standardHomingHeavy, standardIncendiaryHeavy, standardThoriumHeavy, standardDenseMassive, standardHomingMassive,
        standardIncendiaryMassive, standardThoriumMassive, reignBulletWeakened;
    public static ArtilleryBulletType artilleryExplosiveT2;

    @SuppressWarnings("unchecked")
    private static <T extends BulletType> T copy(BulletType from, Cons<T> setter){
        T bullet = (T)from.copy();
        setter.get(bullet);
        return bullet;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BulletType> T deepCopy(BulletType from, Cons<T> setter){
        T bullet = (T)from.copy();
        if(from.fragBullet != null){
            bullet.fragBullet = deepCopy(bullet.fragBullet, b -> {});
        }
        setter.get(bullet);
        return bullet;
    }

    public static void load(){
        laser = new ExpLaserBulletType(150f, 30f){{
            damageInc = 7f;
            status = StatusEffects.shocked;
            statusDuration = 3 * 60f;
            expGain = buildingExpGain = 2;
            fromColor = Pal.accent;
            toColor = Pal.lancerLaser;
        }};

        shardLaserFrag = new ExpBasicBulletType(2f, 10f){
            {
                lifetime = 20f;
                pierceCap = 10;
                pierceBuilding = true;
                backColor = Color.white.cpy().lerp(Pal.lancerLaser, 0.1f);
                frontColor = Color.white;
                hitEffect = Fx.none;
                despawnEffect = Fx.none;
                smokeEffect = Fx.hitLaser;
                hittable = false;
                reflectable = false;
                lightColor = Color.white;
                lightOpacity = 0.6f;

                expChance = 0.15f;
                fromColor = Pal.lancerLaser;
                toColor = Pal.sapBullet;
            }

            @Override
            public void draw(Bullet b){
                Draw.color(getColor(b));
                Lines.stroke(2f * b.fout(0.7f) + 0.01f);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), 8f);
                Lines.stroke(1.3f * b.fout(0.7f) + 0.01f);
                Draw.color(frontColor);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), 5f);
                Draw.reset();
            }
        };

        shardLaser = new ExpLaserBulletType(150f, 30f){{
            status = StatusEffects.shocked;
            statusDuration = 3 * 60f;
            fragBullet = shardLaserFrag;

            expGain = buildingExpGain = 2;
            damageInc = 5f;
            fromColor = Pal.lancerLaser;
            toColor = Pal.sapBullet;
        }};

        frostLaser = new ExpLaserBulletType(170f, 130f){
            {
                status = StatusEffects.freezing;
                statusDuration = 3 * 60f;
                shootEffect = UnityFx.shootFlake;

                expGain = 2;
                buildingExpGain = 3;
                damageInc = 2.5f;
                fromColor = Liquids.cryofluid.color;
                toColor = Color.cyan;
                blip = true;
            }

            @Override
            public void handleExp(Bullet b, float x, float y, int amount){
                super.handleExp(b, x, y, amount);
                freezePos(b, x, y);
            }

            public void freezePos(Bullet b, float x, float y){
                int lvl = getLevel(b);
                float rad = 3.5f;
                UnityFx.freezeEffect.at(x, y, lvl / rad + 10f, getColor(b));
                UnitySounds.laserFreeze.at(x, y);

                Damage.status(b.team, x, y, 10f + lvl / rad, status, 60f + lvl * 6f, true, true);
                Damage.status(b.team, x, y, 10f + lvl / rad, UnityStatusEffects.disabled, 2f * lvl, true, true);
            }
        };

        branchLaserFrag = new ExpBulletType(3.5f, 15f){
            {
                trailWidth = 2f;
                weaveScale = 0.6f;
                weaveMag = 0.5f;
                homingPower = 0.4f;
                lifetime = 30f;
                shootEffect = Fx.hitLancer;
                hitEffect = despawnEffect = HitFx.branchFragHit;
                pierceCap = 10;
                pierceBuilding = true;
                splashDamageRadius = 4f;
                splashDamage = 4f;
                status = UnityStatusEffects.plasmaed;
                statusDuration = 180f;
                trailLength = 6;
                trailColor = Color.white;

                fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f);
                toColor = Pal.sapBullet;
                expGain = 1;
                expOnHit = true;
            }

            @Override
            public void init(){
                super.init();
                despawnHit = false;
            }

            @Override
            public void draw(Bullet b){
                drawTrail(b);

                Draw.color(getColor(b));
                Fill.square(b.x, b.y, trailWidth, b.rotation() + 45);
                Draw.color();
            }
        };

        branchLaser = new ExpLaserBulletType(140f, 20f){{
            status = StatusEffects.shocked;
            statusDuration = 3 * 60f;
            fragBullets = 3;
            fragBullet = branchLaserFrag;
            maxRange = 150f + 2f * 30f; //Account for range increase

            expGain = buildingExpGain = 1;
            damageInc = 6f;
            lengthInc = 2f;
            fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f);
            toColor = Pal.sapBullet;
            hitMissed = true;
        }};

        distField = new DistFieldBulletType(0, -1){{
            centerColor = Pal.lancerLaser.cpy().a(0);
            edgeColor = Pal.place;
            distSplashFx = UnityFx.distSplashFx;
            distStart = UnityFx.distStart;
            distStatus = distort;

            collidesTiles = false;
            collides = false;
            collidesAir = false;
            keepVelocity = false;

            lifetime = 6 * 60;
            radius = 3f*8;
            radiusInc = 0.1f*8;
            bulletSlow = 0.1f;
            bulletSlowInc = 0.025f;
            damageLimit = 100f;
            distDamage = 0.1f;
            expChance = 0.2f/60;
            expGain = 1;
        }};

        smallDistField = new DistFieldBulletType(0, -1){{
            centerColor = Pal.lancerLaser.cpy().a(0);
            edgeColor = Pal.place;
            distSplashFx = UnityFx.distSplashFx;
            distStart = UnityFx.distStart;
            distStatus = distort;

            collidesTiles = false;
            collides = false;
            collidesAir = false;
            keepVelocity = false;

            lifetime = 2.5f * 60;
            radius = 1.5f*8;
            radiusInc = 0.05f*8;
            bulletSlow = 0.05f;
            bulletSlowInc = 0.015f;
            damageLimit = 50f;
            distDamage = 0.05f;
            expChance = 0.1f/60;
            expGain = 1;
        }};

        fractalLaser = new ExpLaserFieldBulletType(170f, 130f){{
            damageInc = 6f;
            lengthInc = 2f;
            fields = 2;
            fieldInc = 0.15f;
            width = 2;
            expGain = buildingExpGain = 1;
            fromColor = Pal.lancerLaser.cpy().lerp(Pal.place, 0.5f);
            toColor = Pal.place;
            maxRange = 150f + 2f * 30f; //Account for range increase

            distField = UnityBullets.distField;
            smallDistField = UnityBullets.smallDistField;
        }};

        laserGeyser = new GeyserBulletType(){{
            damageInc = 2f;
        }};

        kelvinWaterLaser = new ExpLaserBulletType(170f, 130f){{
            damageInc = 7f;
            status = StatusEffects.wet;
            statusDuration = 3 * 60f;
            knockback = 10f;
            expGain = 2;
            buildingExpGain = 3;
            fromColor = Liquids.water.color;
            toColor = Color.sky;
        }};

        kelvinSlagLaser = new ExpLaserBulletType(170f, 130f){
            {
                damageInc = 7f;
                status = StatusEffects.burning;
                statusDuration = 3 * 60f;
                expGain = 2;
                buildingExpGain = 3;
                puddles = 10;
                puddleRange = 4f;
                puddleAmount = 15f;
                puddleLiquid = Liquids.slag;
                fromColor = Liquids.slag.color;
                toColor = Color.orange;
            }

            public void makeLava(float x, float y, Float level){
                for(int i = 0; i < puddles; i++){
                    Tile tile = world.tileWorld(x + Mathf.range(puddleRange), y + Mathf.range(puddleRange));
                    Puddles.deposit(tile, puddleLiquid, puddleAmount + level * 2);
                }
            }

            @Override
            public void init(Bullet b){
                super.init(b);

                if(b.data instanceof Position point) makeLava(point.getX(), point.getY(), getLevelf(b));
            }
        };

        kelvinOilLaser = new ExpLaserBulletType(170f, 130f){
            {

                damageInc = 7f;
                status = StatusEffects.burning;
                statusDuration = 3 * 60f;
                expGain = 2;
                buildingExpGain = 3;
                puddles = 10;
                puddleRange = 4f;
                puddleAmount = 15f;
                puddleLiquid = Liquids.oil;
                fromColor = Liquids.oil.color;
                toColor = Color.darkGray;
            }

            public void makeLava(float x, float y, Float level){
                for(int i = 0; i < puddles; i++){
                    Tile tile = world.tileWorld(x + Mathf.range(puddleRange), y + Mathf.range(puddleRange));
                    Puddles.deposit(tile, puddleLiquid, puddleAmount + level * 2);
                }
            }

            @Override
            public void init(Bullet b){
                super.init(b);

                if(b.data instanceof Position point) makeLava(point.getX(), point.getY(), getLevelf(b));
            }
        };

        kelvinCryofluidLaser = new ExpLaserBulletType(170f, 130f){
            {
                damageInc = 3f;
                status = StatusEffects.freezing;
                statusDuration = 3 * 60f;
                expGain = 2;
                buildingExpGain = 3;
                shootEffect = UnityFx.shootFlake;
                fromColor = Liquids.cryofluid.color;
                toColor = Color.cyan;
            }

            public void freezePos(Bullet b, float x, float y){
                int lvl = getLevel(b);
                float rad = 4.5f;
                if(!Vars.headless) UnityFx.freezeEffect.at(x, y, lvl / rad + 10f, getColor(b));
                if(!Vars.headless) UnitySounds.laserFreeze.at(x, y, 1f, 0.6f);

                Damage.status(b.team, x, y, 10f + lvl / rad, status, 60f + lvl * 7.5f, true, true);
                Damage.status(b.team, x, y, 10f + lvl / rad, UnityStatusEffects.disabled, 4.5f * lvl, true, true);
            }

            @Override
            public void init(Bullet b){
                super.init(b);

                setDamage(b);

                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), getLength(b));
                b.data = target;

                if(target instanceof Hitboxc hit){
                    hit.collision(b, hit.x(), hit.y());
                    b.collision(hit, hit.x(), hit.y());
                    freezePos(b, hit.x(), hit.y());
                    if(b.owner instanceof ExpTurret.ExpTurretBuild exp) exp.handleExp(expGain);
                }else if(target instanceof Building tile && tile.collide(b)){
                    tile.collision(b);
                    hit(b, tile.x, tile.y);
                    freezePos(b, tile.x, tile.y);
                    if(b.owner instanceof ExpTurret.ExpTurretBuild exp) exp.handleExp(buildingExpGain);
                }else{
                    b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
                }
            }
        };

        kelvinLiquidLaser = new ExpLaserBulletType(170f, 130f){
            final float damageMultiplier = 150f; //Multiply the liquid's heat capacity
            final float damageMultiplierInc = 10f;

            {
                status = StatusEffects.freezing;
                statusDuration = 3 * 60f;
                expGain = 2;
                buildingExpGain = 3;
                shootEffect = UnityFx.shootFlake;
                fromColor = Liquids.cryofluid.color;
                toColor = Color.cyan;
            }

            @Override
            public void setDamage(Bullet b){
                Liquid liquid = Liquids.cryofluid;
                if(b.owner instanceof Building build && !build.cheating()) liquid = build.liquids.current();
                float mul = damageMultiplier + damageMultiplierInc * getLevel(b);
                b.damage = liquid.heatCapacity * mul * b.damageMultiplier();
            }

            void freezePos(Bullet b, float x, float y){
                int lvl = getLevel(b);
                float rad = 4.5f;
                if(!Vars.headless) UnityFx.freezeEffect.at(x, y, lvl / rad + 10f, getColor(b));
                if(!Vars.headless) UnitySounds.laserFreeze.at(x, y, 1f, 0.6f);

                Damage.status(b.team, x, y, 10f + lvl / rad, status, 60f + lvl * 8f, true, true);
                Damage.status(b.team, x, y, 10f + lvl / rad, UnityStatusEffects.disabled, 3f * lvl, true, true);
            }

            @Override
            public void init(Bullet b){
                super.init(b);

                setDamage(b);

                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), getLength(b));
                b.data = target;

                if(target instanceof Hitboxc hit){
                    hit.collision(b, hit.x(), hit.y());
                    b.collision(hit, hit.x(), hit.y());
                    freezePos(b, hit.x(), hit.y());
                    if(b.owner instanceof ExpTurret.ExpTurretBuild exp) exp.handleExp(expGain);
                }else if(target instanceof Building tile && tile.collide(b)){
                    tile.collision(b);
                    hit(b, tile.x, tile.y);
                    freezePos(b, tile.x, tile.y);
                    if(b.owner instanceof ExpTurret.ExpTurretBuild exp) exp.handleExp(buildingExpGain);
                }else{
                    b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
                }
            }
        };

        breakthroughLaser = new ExpLaserBlastBulletType(500f, 1200f){{
            damageInc = 1000f;
            lengthInc = 150f;
            largeHit = true;
            width = 80f;
            widthInc = 10f;
            lifetime = 65f;
            lightningSpacingInc = -5f;
            lightningDamageInc = 30f;
            hitUnitExpGain = 1;
            hitBuildingExpGain = 1;
            sideLength = 0f;
            sideWidth = 0f;
        }};

        coalBlaze = new ExpBulletType(3.35f, 32f){{
            ammoMultiplier = 3;
            hitSize = 7f;
            lifetime = 24f;
            pierce = true;
            statusDuration = 60 * 4f;
            shootEffect = ShootFx.shootSmallBlaze;
            hitEffect = Fx.hitFlameSmall;
            despawnEffect = Fx.none;
            status = StatusEffects.burning;
            keepVelocity = true;
            hittable = false;

            expOnHit = true;
            expChance = 0.5f;
        }};

        pyraBlaze = new ExpBulletType(3.35f, 46f){{
            ammoMultiplier = 3;
            hitSize = 7f;
            lifetime = 24f;
            pierce = true;
            statusDuration = 60 * 4f;
            shootEffect = ShootFx.shootPyraBlaze;
            hitEffect = Fx.hitFlameSmall;
            despawnEffect = Fx.none;
            status = StatusEffects.burning;
            keepVelocity = false;
            hittable = false;

            expOnHit = true;
            expChance = 0.6f;
        }};

        basicMissile = new MissileBulletType(4.2f, 15){{
            homingPower = 0.12f;
            width = 8f;
            height = 8f;
            shrinkX = shrinkY = 0f;
            drag = -0.003f;
            homingRange = 80f;
            keepVelocity = false;
            splashDamageRadius = 35f;
            splashDamage = 30f;
            lifetime = 62f;
            trailColor = Pal.missileYellowBack;
            hitEffect = Fx.blastExplosion;
            despawnEffect = Fx.blastExplosion;
            weaveScale = 8f;
            weaveMag = 2f;
        }};

        citadelFlame = new FlameBulletType(4.2f, 50f){{
            lifetime = 20f;
            particleAmount = 17;
        }};

        sapArtilleryFrag = new ArtilleryBulletType(2.3f, 30){{
            hitEffect = Fx.sapExplosion;
            knockback = 0.8f;
            lifetime = 70f;
            width = height = 20f;
            collidesTiles = false;
            splashDamageRadius = 70f;
            splashDamage = 60f;
            backColor = Pal.sapBulletBack;
            frontColor = lightningColor = Pal.sapBullet;
            lightning = 2;
            lightningLength = 5;
            smokeEffect = Fx.shootBigSmoke2;
            hitShake = 5f;
            lightRadius = 30f;
            lightColor = Pal.sap;
            lightOpacity = 0.5f;

            status = StatusEffects.sapped;
            statusDuration = 60f * 10;
        }};

        sapLaser = new LaserBulletType(80f){{
            colors = new Color[]{Pal.sapBulletBack.cpy().a(0.4f), Pal.sapBullet, Color.white};
            length = 150f;
            width = 25f;
            sideLength = sideWidth = 0f;
            shootEffect = ShootFx.sapPlasmaShoot;
            hitColor = lightColor = lightningColor = Pal.sapBullet;
            status = StatusEffects.sapped;
            statusDuration = 80f;
            lightningSpacing = 17f;
            lightningDelay = 0.12f;
            lightningDamage = 15f;
            lightningLength = 4;
            lightningLengthRand = 2;
            lightningAngleRand = 15f;
        }};

        continuousSapLaser = new ContinuousLaserBulletType(60f){
            {
                colors = new Color[]{Pal.sapBulletBack.cpy().a(0.3f), Pal.sapBullet.cpy().a(0.6f), Pal.sapBullet, Color.white};
                length = 190f;
                width = 5f;
                shootEffect = ShootFx.sapPlasmaShoot;
                hitColor = lightColor = lightningColor = Pal.sapBullet;
                hitEffect = HitFx.coloredHitSmall;
                status = StatusEffects.sapped;
                statusDuration = 80f;
                lifetime = 180f;
                incendChance = 0f;
                largeHit = false;
            }

            @Override
            public void hitTile(Bullet b, Building build, float initialHealth, boolean direct){
                super.hitTile(b, build, initialHealth, direct);
                if(b.owner instanceof Healthc owner){
                    owner.heal(Math.max(initialHealth - build.health(), 0f) * 0.2f);
                }
            }

            @Override
            public void hitEntity(Bullet b, Hitboxc entity, float health){
                super.hitEntity(b, entity, health);
                if(entity instanceof Healthc h && b.owner instanceof Healthc owner){
                    owner.heal(Math.max(health - h.health(), 0f) * 0.2f);
                }
            }
        };

        falloutLaser = new SparkingContinuousLaserBulletType(95f){{
            length = 230f;
            fromBlockChance = 0.12f;
            fromBlockDamage = 23f;
            fromLaserAmount = 0;
            incendChance = 0f;
            fromBlockLen = 2;
            fromBlockLenRand = 5;
        }};

        catastropheLaser = new SparkingContinuousLaserBulletType(240f){{
            length = 340f;
            strokes = new float[]{2 * 1.4f, 1.5f * 1.4f, 1 * 1.4f, 0.3f * 1.4f};
            incendSpread = 7f;
            incendAmount = 2;
        }};

        calamityLaser = new SparkingContinuousLaserBulletType(580f){{
            length = 450f;
            strokes = new float[]{2 * 1.7f, 1.5f * 1.7f, 1 * 1.7f, 0.3f * 1.7f};
            lightStroke = 70f;
            spaceMag = 70f;
            fromBlockChance = 0.5f;
            fromBlockDamage = 34f;
            fromLaserChance = 0.8f;
            fromLaserDamage = 32f;
            fromLaserAmount = 3;
            fromLaserLen = 5;
            fromLaserLenRand = 7;
            incendChance = 0.6f;
            incendSpread = 9f;
            incendAmount = 2;
        }};

        extinctionLaser = new SparkingContinuousLaserBulletType(770f){{
            length = 560f;
            strokes = new float[]{2f * 2.2f, 1.5f * 2.2f, 2.2f, 0.3f * 2.2f};
            lightStroke = 90f;
            spaceMag = 70f;
            fromBlockChance = 0.5f;
            fromBlockDamage = 76f;
            fromBlockAmount = 4;
            fromLaserChance = 0.8f;
            fromLaserDamage = 46f;
            fromLaserAmount = 4;
            fromLaserLen = 10;
            fromLaserLenRand = 7;
            incendChance = 0.7f;
            incendSpread = 9f;
            incendAmount = 2;
            extinction = true;
        }};

        plagueMissile = new MissileBulletType(3.8f, 12f){{
            width = height = 8f;
            backColor = hitColor = lightColor = trailColor = UnityPal.plagueDark;
            frontColor = UnityPal.plague;
            shrinkY = 0f;
            drag = -0.01f;
            splashDamage = 30f;
            splashDamageRadius = 35f;
            hitEffect = Fx.blastExplosion;
            despawnEffect = Fx.blastExplosion;
        }};

        gluonWhirl = new GluonWhirlBulletType(4f){{
            lifetime = 5f * 60f;
            hitSize = 12f;
        }};

        gluonEnergyBall = new GluonOrbBulletType(8.6f, 10f){
            {
                lifetime = 50f;
                drag = 0.03f;
                hitSize = 9f;
            }

            @Override
            public void despawned(Bullet b){
                super.despawned(b);

                gluonWhirl.create(b, b.x, b.y, 0f);
            }
        };

        singularityBlackHole = new SingularityBulletType(26f){{
            lifetime = 3.5f * 60f;
            hitSize = 19f;
        }};

        singularityEnergyBall = new BasicBulletType(6.6f, 7f){
            {
                lifetime = 110f;
                drag = 0.018f;
                pierce = pierceBuilding = true;
                hitSize = 9f;
                despawnEffect = hitEffect = Fx.none;
            }

            @Override
            public void update(Bullet b){
                super.update(b);

                if(Units.closestTarget(b.team, b.x, b.y, 20f) != null){
                    b.remove();
                }

                if(b.timer.get(0, 2f + b.fslope() * 1.5f)){
                    UnityFx.lightHexagonTrail.at(b.x, b.y, 1f + b.fslope() * 4f, backColor);
                }
            }

            @Override
            public void despawned(Bullet b){
                super.despawned(b);

                singularityBlackHole.create(b, b.x, b.y, 0f);
            }

            @Override
            public void draw(Bullet b){
                Draw.color(Pal.lancerLaser);
                Fill.circle(b.x, b.y, 7f + b.fout() * 1.5f);
                Draw.color(Color.white);
                Fill.circle(b.x, b.y, 5.5f + b.fout());
            }
        };

        orb = new BulletType(){
            {
                lifetime = 240;
                speed = 1.24f;
                damage = 23;
                pierce = true;
                hittable = false;
                hitEffect = HitFx.orbHit;
                trailEffect = UnityFx.orbTrail;
                trailChance = 0.4f;
            }

            @Override
            public void draw(Bullet b){
                Drawf.light(b.x, b.y, 16, Pal.surge, 0.6f);

                Draw.color(Pal.surge);
                Fill.circle(b.x, b.y, 4);

                Draw.color();
                Fill.circle(b.x, b.y, 2.5f);
            }

            @Override
            public void update(Bullet b){
                super.update(b);
                if(b.timer.get(1, 7)) Units.nearbyEnemies(b.team, b.x - 5 * tilesize, b.y - 5 * tilesize, 5 * tilesize * 2, 5 * tilesize * 2, unit -> Lightning.create(b.team, Pal.surge, Mathf.random(17, 33), b.x, b.y, b.angleTo(unit), Mathf.random(7, 13)));
            }

            @Override
            public void drawLight(Bullet b){}
        };

        shockBeam = new BeamBulletType(120f, 35f){{
            status = StatusEffects.shocked;
            statusDuration = 3f * 60f;
            beamWidth = 0.62f;
            hitEffect = Fx.hitLiquid;
            castsLightning = true;
            minLightningDamage = damage / 1.8f;
            maxLightningDamage = damage / 1.2f;
            color = Pal.surge;
        }};

        currentStroke = new LaserBulletType(450){{
            lifetime = 65f;
            width = 20f;
            length = 430f;
            lightningSpacing = 35f;
            lightningLength = 5;
            lightningDelay = 1.1f;
            lightningLengthRand = 15;
            lightningDamage = 50f;
            lightningAngleRand = 40f;
            largeHit = true;
            lightColor = lightningColor = Pal.surge;
            sideAngle = 15f;
            sideWidth = 0f;
            sideLength = 0f;
            colors = new Color[]{Pal.surge.cpy(), Pal.surge, Color.white};
        }};

        shielderBullet = new ShieldBulletType(8){{
            drag = 0.03f;
            shootEffect = Fx.none;
            despawnEffect = Fx.none;
            collides = false;
            hitSize = 0;
            hittable = false;
            hitEffect = Fx.hitLiquid;
            breakSound = Sounds.wave;
            maxRadius = 10f;
            shieldHealth = 3000f;
        }};

        plasmaFragTriangle = new TriangleBulletType(11, 10, 4.5f, 90f){{
            lifetime = 160f;
            lifetimeRand = 40f;
            trailWidth = 3f;
            trailLength = 8;
            drag = 0.05f;
            collides = false;
            castsLightning = true;
            shootEffect = UnityFx.plasmaFragAppear;
            hitEffect = despawnEffect = UnityFx.plasmaFragDisappear;
        }};

        plasmaTriangle = new TriangleBulletType(13, 10, 4f, 380f){{
            lifetime = 180f;
            trailWidth = 3.5f;
            trailLength = 14;
            homingPower = 0.06f;
            hitSound = Sounds.plasmaboom;
            hitEffect = HitFx.plasmaTriangleHit;
            despawnEffect = Fx.none;
            fragBullet = plasmaFragTriangle;
            fragBullets = 8;
        }};

        surgeBomb = new SurgeBulletType(7f, 100f){{
            width = height = 30f;
            maxRange = 30f;
            backColor = Pal.surge;
            frontColor = Color.white;
            mixColorTo = Color.white;
            hitSound = Sounds.plasmaboom;
            despawnShake = 4f;
            collidesAir = false;
            lifetime = 70f;
            despawnEffect = UnityFx.surgeSplash;
            hitEffect = Fx.massiveExplosion;
            keepVelocity = false;
            spin = 2f;
            shrinkX = shrinkY = 0.7f;
            collides = false;
            splashDamage = 680f;
            splashDamageRadius = 120f;

            fragBullets = 8;
            fragLifeMin = 0.8f;
            fragLifeMax = 1.1f;
            scaleVelocity = true;
            fragBullet = plasmaFragTriangle;

            shocks = 10;
            shockDamage = 680f / 5f;
            shockLength = 20;
        }};

        pylonLightning = new LightningBulletType(){{
            lightningLength = 32;
            lightningLengthRand = 12;
            damage = 56f;
        }};

        pylonLaser = new LaserBulletType(2000f){
            {
                length = 520f;
                width = 60f;
                lifetime = 72f;
                largeHit = true;
                sideLength = sideWidth = 0f;
                shootEffect = UnityFx.pylonLaserCharge;
            }

            @Override
            public void init(Bullet b){
                super.init(b);

                for(int i = 0; i < 24; i++){
                    Time.run(2f * i, () -> {
                        pylonLightning.create(b, b.x, b.y, b.vel().angle());

                        Sounds.spark.at(b.x, b.y, Mathf.random(0.6f, 0.9f));
                    });
                }
            }
        };

        pylonLaserSmall = new LaserBulletType(192f){{
            length = 180f;
            width = 24f;
        }};

        monumentRailBullet = new PointBulletType(){{
            damage = 6000f;
            buildingDamageMultiplier = 0.8f;
            speed = maxRange = 540f;
            lifetime = 1f;
            hitShake = 6f;
            trailSpacing = 35f;
            shootEffect = ShootFx.monumentShoot;
            despawnEffect = UnityFx.monumentDespawn;
            smokeEffect = Fx.blastExplosion;
            trailEffect = UnityFx.monumentTrail;
        }};

        scarShrapnel = new ShrapnelBulletType(){{
            fromColor = UnityPal.endColor;
            toColor = UnityPal.scarColor;
            damage = 1f;
            length = 110f;
        }};

        scarMissile = new MissileBulletType(6f, 12f){{
            lifetime = 70f;
            speed = 5f;
            width = 7f;
            height = 12f;
            shrinkY = 0f;
            backColor = trailColor = UnityPal.scarColor;
            frontColor = UnityPal.endColor;
            splashDamage = 36f;
            splashDamageRadius = 20f;
            weaveMag = 3f;
            weaveScale = 6f;
            pierceBuilding = true;
            pierceCap = 3;
        }};

        celsiusSmoke = new SmokeBulletType(4.7f, 32f){{
            drag = 0.034f;
            lifetime = 18f;
            hitSize = 4f;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            hitEffect = HitFx.hitAdvanceFlame;
            despawnEffect = Fx.none;
            collides = true;
            collidesTiles = true;
            collidesAir = true;
            pierce = true;
            statusDuration = 770f;
            status = UnityStatusEffects.blueBurn;
        }};

        kelvinSmoke = new SmokeBulletType(4.7f, 16f){{
            drag = 0.016f;
            lifetime = 32f;
            hitSize = 4f;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            hitEffect = HitFx.hitAdvanceFlame;
            despawnEffect = Fx.none;
            collides = true;
            collidesTiles = true;
            collidesAir = true;
            pierce = true;
            statusDuration = 770f;
            status = UnityStatusEffects.blueBurn;
        }};

        kamiBullet1 = new CircleBulletType(4f, 7f){{
            lifetime = 240f;
            hitSize = 6f;
            despawnEffect = Fx.none;
            pierce = true;
            keepVelocity = false;
            color = b -> Tmp.c1.set(Color.red).shiftHue(b.time * 3f).cpy();
        }};

        kamiBullet2 = new KamiBulletType(){{
            lifetime = 240f;
            hitSize = 6f;
            despawnEffect = Fx.none;
            trailLength = 12;
        }};

        kamiBullet3 = new KamiBulletType(){{
            lifetime = 240f;
            hitSize = 6f;
            despawnEffect = Fx.none;
        }};

        kamiLaser = new KamiLaserBulletType(230f){{
            lifetime = 4f * 60f;
            length = 760f;
            width = 140f;
            fadeInTime = 60f;
            drawSize = (length + (width * 2f)) * 2f;
        }};

        kamiVariableLaser = new KamiAltLaserBulletType(60f);

        kamiSmallLaser = new KamiLaserBulletType(230f){{
            lifetime = 2f * 60f;
            length = 760f;
            width = 20f;
            fadeInTime = 15f;
            curveScl = 3f;
            drawSize = (length + (width * 2f)) * 2f;
        }};

        ricochetSmall = new RicochetBulletType(7f, 80f){{
            width = 9f;
            height = 12f;
            ammoMultiplier = 4;
            lifetime = 30f;
            trailEffect = UnityFx.ricochetTrailSmall;
            frontColor = Color.white;
            backColor = trailColor = Pal.lancerLaser;
        }};

        ricochetMedium = new RicochetBulletType(8.5f, 168f){{
            width = 12f;
            height = 16f;
            ammoMultiplier = 4;
            lifetime = 35f;
            pierceCap = 5;
            trailLength = 7;
            trailEffect = UnityFx.ricochetTrailMedium;
            frontColor = Color.white;
            backColor = trailColor = Pal.lancerLaser;
        }};

        ricochetBig = new RicochetBulletType(10f, 528f){{
            width = 14f;
            height = 18f;
            ammoMultiplier = 4;
            lifetime = 40f;
            pierceCap = 8;
            trailLength = 8;
            trailEffect = UnityFx.ricochetTrailBig;
            frontColor = Color.white;
            backColor = trailColor = Pal.lancerLaser;
        }};

        stopLead = new BasicBulletType(3.6f, 72f, "shell"){{
            width = 9f;
            height = 12f;
            ammoMultiplier = 4;
            lifetime = 60f;
            frontColor = Color.white;
            backColor = Pal.lancerLaser;
            status = StatusEffects.unmoving;
            statusDuration = 5f;
        }};

        stopMonolite = new BasicBulletType(4f, 100f, "shell"){{
            width = 9f;
            height = 12f;
            ammoMultiplier = 4;
            lifetime = 60f;
            frontColor = Color.white;
            backColor = Pal.lancerLaser;
            status = StatusEffects.unmoving;
            statusDuration = 8f;
        }};

        stopSilicon = new BasicBulletType(4f, 72f, "shell"){{
            width = 9f;
            height = 12f;
            ammoMultiplier = 4;
            lifetime = 60f;
            frontColor = Color.white;
            backColor = Pal.lancerLaser;
            status = StatusEffects.unmoving;
            statusDuration = 16f;
            homingPower = 0.08f;
        }};

        supernovaLaser = new ContinuousLaserBulletType(3200f){
            final Effect plasmaEffect;

            {
                length = 280f;
                colors = new Color[]{
                    Color.valueOf("4be3ca55"),
                    Color.valueOf("91eedeaa"),
                    Pal.lancerLaser.cpy(),
                    Color.white
                };

                plasmaEffect = new Effect(36f, e -> {
                    Draw.color(Color.white, Pal.lancerLaser, e.fin());
                    Fill.circle(
                        e.x + Angles.trnsx(e.rotation, e.fin() * 24f),
                        e.y + Angles.trnsy(e.rotation, e.fin() * 24f),
                        e.fout() * 5f
                    );
                });
            }

            @Override
            public void update(Bullet b){
                super.update(b);

                if(b.timer(2, 1f)){
                    float start = Mathf.randomSeed((long)(b.id + Time.time), length);
                    Lightning.create(b.team, Pal.lancerLaser, 12f,
                        b.x + Angles.trnsx(b.rotation(), start),
                        b.y + Angles.trnsy(b.rotation(), start),
                        b.rotation() + Mathf.randomSeedRange((long)(b.id + Time.time + 1f), 15f), Mathf.randomSeed((long)(b.id + Time.time + 2f), 10, 19)
                    );
                }

                for(int i = 0; i < 2; i++){
                    float f = Mathf.random(length * b.fout());
                    plasmaEffect.at(
                        b.x + Angles.trnsx(b.rotation(), f) + Mathf.range(6f),
                        b.y + Angles.trnsy(b.rotation(), f) + Mathf.range(6f),
                        b.rotation() + Mathf.range(85f)
                    );
                }
            }
        };

        endLightning = new AntiCheatBulletTypeBase(0f, 0f){{
            lifetime = Fx.lightning.lifetime;
            hitColor = UnityPal.scarColor;
            hitEffect = HitFx.coloredHitSmall;
            despawnEffect = Fx.none;
            status = StatusEffects.shocked;
            statusDuration = 10f;
            hittable = false;

            ratioStart = 15000f;
            ratioDamage = 1 / 60f;
        }};

        ravagerLaser = new EndPointBlastLaserBulletType(1210f){
            {
                length = 460f;
                width = 26.1f;
                lifetime = 25f;
                widthReduction = 6f;
                auraWidthReduction = 4f;
                damageRadius = 110f;
                auraDamage = 9000f;

                overDamage = 500000f;
                ratioDamage = 1f / 30f;
                ratioStart = 12000f;
                bleedDuration = 10f * 60f;

                hitEffect = HitFx.voidHit;

                laserColors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};

                modules = new AntiCheatBulletModule[]{
                new ArmorDamageModule(1f / 15f, 5f, 70f, 8f),
                new AbilityDamageModule(50f, 400f, 4f, 1f / 25f, 5f),
                new ForceFieldDamageModule(10f, 30f, 230f, 8f, 1f / 40f)
                };
            }
        };

        ravagerArtillery = new ArtilleryBulletType(4f, 130f){{
            lifetime = 110f;
            splashDamage = 325f;
            splashDamageRadius = 140f;
            width = height = 21f;
            backColor = lightColor = trailColor = UnityPal.scarColor;
            frontColor = lightningColor = UnityPal.endColor;
            lightning = 5;
            lightningLength = 10;
            lightningLengthRand = 5;

            fragBullets = 7;
            fragLifeMin = 0.9f;

            hitEffect = HitFx.endHitRedBig;

            fragBullet = new EndBasicBulletType(5.6f, 180f){{
                lifetime = 20f;
                pierce = pierceBuilding = true;
                pierceCap = 5;
                backColor = lightColor = UnityPal.scarColor;
                frontColor = UnityPal.endColor;
                width = height = 16f;

                overDamage = 950000f;
                ratioDamage = 1f / 400f;
                ratioStart = 3000f;
            }};
        }};

        oppressionArea = new VoidAreaBulletType(95f){{
            lifetime = 5f * 60f;
            bleedDuration = 30f;
            ratioDamage = 1f / 200f;
            ratioStart = 600000f;
            status = weaken;
            statusDuration = 30f;
            radius = 120f;

            modules = new AntiCheatBulletModule[]{
                new ForceFieldDamageModule(5f, 10f, 1000f, 1f, 1f / 50f, 3f * 60f),
                new AbilityDamageModule(10f, 5f * 60f, 10f, 1f / 60f, 2f)
            };
        }};

        oppressionShell = new EndBasicBulletType(7f, 410f, "shell"){{
            lifetime = 95f;
            splashDamage = 125f;
            splashDamageRadius = 70f;
            width = 18f;
            height = 23f;
            backColor = lightColor = trailColor = UnityPal.scarColor;
            frontColor = lightningColor = UnityPal.endColor;
            lightning = 5;
            lightningLength = 10;
            lightningLengthRand = 5;
            lightningType = endLightning;

            despawnEffect = HitFx.endHitRedBig;

            pierceCap = 3;
            pierce = pierceBuilding = true;
            bleedDuration = 5f * 60f;
        }};

        missileAntiCheat = new EndBasicBulletType(4f, 330f, "missile"){{
            lifetime = 60f;
            width = height = 12f;
            shrinkY = 0f;
            drag = -0.013f;
            splashDamageRadius = 45f;
            splashDamage = 220f;
            homingPower = 0.08f;
            trailChance = 0.2f;
            weaveScale = 6f;
            weaveMag = 1f;

            overDamage = 900000f;
            ratioDamage = 1f / 150f;
            ratioStart = 2000f;

            hitEffect = HitFx.endHitRedSmoke;

            backColor = lightColor = trailColor = UnityPal.scarColor;
            frontColor = UnityPal.endColor;
        }};

        endLaserSmall = new EndContinuousLaserBulletType(85f){{
            lifetime = 2f * 60;
            length = 230f;
            for(int i = 0; i < strokes.length; i++){
                strokes[i] *= 0.4f;
            }
            overDamage = 800000f;
            ratioDamage = 1f / 40f;
            ratioStart = 1000000f;
            colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
            modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.1f, 30f, 30f, 0.4f)};

            hitEffect = HitFx.endHitRedSmall;
        }};

        endLaser = new EndContinuousLaserBulletType(2400f){{
            length = 340f;
            lifetime = 5f * 60f;
            incendChance = -1f;
            shootEffect = ChargeFx.devourerChargeEffect;
            keepVelocity = true;
            lightColor = lightningColor = hitColor = UnityPal.scarColor;
            colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};

            lightningDamage = 80f;
            lightningChance = 0.8f;
            lightningLength = (int)(length / 8f);
            lightningLengthRand = 5;

            overDamage = 650000f;
            overDamagePower = 2.7f;
            overDamageScl = 4000f;
            ratioDamage = 1f / 80f;
            ratioStart = 19000f;
            bleedDuration = 10f * 60f;
            pierceShields = true;

            hitEffect = HitFx.endHitRedBig;

            modules = new AntiCheatBulletModule[]{
                new ArmorDamageModule(0.001f, 3f, 15f, 2f),
                new AbilityDamageModule(50f, 300f, 4f, 0.001f, 3f),
                new ForceFieldDamageModule(4f, 20f, 230f, 8f, 1f / 40f)
            };
        }};

        laserZap = new LaserBulletType(90f){{
            sideAngle = 15f;
            sideWidth = 1.5f;
            sideLength = 60f;
            width = 16f;
            length = 215f;
            shootEffect = Fx.shockwave;
            colors = new Color[]{Pal.lancerLaser.cpy().mul(1f, 1f, 1f, 0.7f), Pal.lancerLaser, Color.white};
        }};

        plasmaBullet = new BasicBulletType(3.5f, 15f){
            {
                frontColor = Pal.lancerLaser.cpy().lerp(Color.white, 0.5f);
                backColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f).mul(0.7f);
                width = height = 2f;
                weaveScale = 0.6f;
                weaveMag = 0.5f;
                homingPower = 0.4f;
                lifetime = 80f;
                shootEffect = Fx.hitLancer;
                hitEffect = despawnEffect = Fx.hitLancer;
                pierceCap = 10;
                pierceBuilding = true;
                splashDamageRadius = 4f;
                splashDamage = 4f;
                status = UnityStatusEffects.plasmaed;
                statusDuration = 180f;
                inaccuracy = 25f;
            }

            @Override
            public void init(Bullet b){
                b.data = new FixedTrail(9);
            }

            @Override
            public void draw(Bullet b){
                if(b.data instanceof FixedTrail trail){
                    trail.draw(frontColor, width);
                }

                Draw.color(frontColor);
                Fill.square(b.x, b.y, width, b.rotation() + 45f);
                Draw.color();
            }

            @Override
            public void update(Bullet b){
                super.update(b);
                if(b.data instanceof FixedTrail trail){
                    trail.update(b.x, b.y, b.rotation());
                }
            }

            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                if(b.data instanceof FixedTrail trail){
                    UnityFx.fixedTrailFade.at(b.x, b.y, width, frontColor, trail.copy());
                    trail.clear();
                }
            }
        };

        phantasmalBullet = new BasicBulletType(6f, 32f){{
            width = 6f;
            height = 12f;
            shrinkY = 0.3f;
            lifetime = 45f;

            frontColor = Color.white;
            backColor = Pal.lancerLaser;
            shootEffect = Fx.shootSmall;
            smokeEffect = Fx.shootSmallSmoke;
            hitEffect = Fx.flakExplosion;

            lightning = 3;
            lightningColor = Pal.lancerLaser;
            lightningLength = 6;
        }};

        teleportLightning = new LightningBulletType(){{
            damage = 12f;
            shootEffect = Fx.hitLancer;
            smokeEffect = Fx.none;
            despawnEffect = Fx.none;
            hitEffect = Fx.hitLancer;
            keepVelocity = false;
        }};

        //only enhanced

        standardDenseLarge = copy(Bullets.standardDenseBig, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.speed *= 1.1f;
            t.width *= 1.12f;
            t.height *= 1.12f;
        });

        standardHomingLarge = copy(Bullets.standardDenseBig, (BasicBulletType t) -> {
            t.damage *= 1.23f;
            t.reloadMultiplier = 1.3f;
            t.homingPower = 0.09f;
            t.speed *= 1.1f;
            t.width *= 1.09f;
            t.height *= 1.09f;
        });

        standardIncendiaryLarge = copy(Bullets.standardIncendiaryBig, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.speed *= 1.1f;
            t.width *= 1.12f;
            t.height *= 1.12f;
        });

        standardThoriumLarge = copy(Bullets.standardThoriumBig, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.speed *= 1.1f;
            t.width *= 1.12f;
            t.height *= 1.12f;
        });

        standardDenseHeavy = copy(Bullets.standardDenseBig, (BasicBulletType t) -> {
            t.damage *= 1.7f;
            t.speed *= 1.3f;
            t.width *= 1.32f;
            t.height *= 1.32f;
        });

        standardHomingHeavy = copy(Bullets.standardDenseBig, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.reloadMultiplier = 1.3f;
            t.homingPower = 0.09f;
            t.speed *= 1.3f;
            t.width *= 1.19f;
            t.height *= 1.19f;
        });

        standardIncendiaryHeavy = copy(Bullets.standardIncendiaryBig, (BasicBulletType t) -> {
            t.damage *= 1.7f;
            t.speed *= 1.3f;
            t.width *= 1.32f;
            t.height *= 1.32f;
        });

        standardThoriumHeavy = copy(Bullets.standardThoriumBig, (BasicBulletType t) -> {
            t.damage *= 1.7f;
            t.speed *= 1.3f;
            t.width *= 1.32f;
            t.height *= 1.32f;
        });

        standardDenseMassive = copy(Bullets.standardDenseBig, (BasicBulletType t) -> {
            t.damage *= 1.8f;
            t.speed *= 1.3f;
            t.width *= 1.34f;
            t.height *= 1.34f;
            t.lifetime *= 1.1f;
        });

        standardHomingMassive = copy(Bullets.standardDenseBig, (BasicBulletType t) -> {
            t.damage *= 1.6f;
            t.reloadMultiplier = 1.3f;
            t.homingPower = 0.09f;
            t.speed *= 1.3f;
            t.width *= 1.21f;
            t.height *= 1.21f;
            t.lifetime *= 1.1f;
        });

        standardIncendiaryMassive = copy(Bullets.standardIncendiaryBig, (BasicBulletType t) -> {
            t.damage *= 1.8f;
            t.speed *= 1.3f;
            t.width *= 1.34f;
            t.height *= 1.34f;
            t.lifetime *= 1.1f;
        });

        standardThoriumMassive = copy(Bullets.standardThoriumBig, (BasicBulletType t) -> {
            t.damage *= 1.8f;
            t.speed *= 1.3f;
            t.width *= 1.34f;
            t.height *= 1.34f;
            t.lifetime *= 1.1f;
        });

        reignBulletWeakened = copy(UnitTypes.reign.weapons.get(0).bullet, (BasicBulletType t) -> {
            t.damage = 45f;
        });

        artilleryExplosiveT2 = copy(Bullets.artilleryExplosive, (ArtilleryBulletType t) -> {
            t.speed = 4.5f;
            t.lifetime = 74f;
            t.ammoMultiplier = 2f;
            t.splashDamageRadius *= 1.3f;
            t.splashDamage *= 3f;
        });

        statusEffect = new BlockStatusEffectBulletType(0f, 0){{
            hitEffect = despawnEffect = Fx.none;
            lifetime = 180f;
            width = height = 1f;
        }};

        //endregion
    }
}
