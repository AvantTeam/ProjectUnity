package unity.content;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.ctype.*;
import unity.entities.bullet.*;
import unity.entities.comp.*;
import unity.gen.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class UnityBullets implements ContentList{
    public static BulletType laser, shardLaserFrag, shardLaser, frostLaser, branchLaserFrag, branchLaser, kelvinLaser, breakthroughLaser, coalBlaze, pyraBlaze, falloutLaser, catastropheLaser, calamityLaser, extinctionLaser, orb, shockBeam, currentStroke,
        shielderBullet, plasmaFragTriangle, plasmaTriangle, surgeBomb, pylonLightning, pylonLaser, pylonLaserSmall, exporb, monumentRailBullet, scarShrapnel, scarMissile, celsiusSmoke, kelvinSmoke,
        kamiBullet1, kamiLaser, kamiSmallLaser, supernovaLaser;

    //only enhanced
    public static BasicBulletType standardDenseLarge, standardHomingLarge, standardIncendiaryLarge, standardThoriumLarge, standardDenseHeavy, standardHomingHeavy, standardIncendiaryHeavy, standardThoriumHeavy, standardDenseMassive, standardHomingMassive,
        standardIncendiaryMassive, standardThoriumMassive;

    @SuppressWarnings("unchecked")
    private <T extends BulletType> T copy(BulletType from, Prov<T> constructor, Cons<T> setter){
        T target = constructor.get();
        JsonIO.copy((T)from, target);
        setter.get(target);
        return target;
    }

    @Override
    public void load(){
        laser = new ExpLaserBulletType(150f, 30f){{
                width = 0.7f;
                damageInc = 7f;
                status = StatusEffects.shocked;
                statusDuration = 3 * 60f;
                hitUnitExpGain = hitBuildingExpGain = 2f;
        }};

        shardLaserFrag = new LaserBoltBulletType(2f, 10f){
            {
                lifetime = 20f;
                pierceCap = 10;
                pierceBuilding = true;
                backColor = Color.white.cpy().lerp(Pal.lancerLaser, 0.1f);
                frontColor = Color.white;
                hitEffect = Fx.hitLancer;
                despawnEffect = Fx.hitLancer;
            }

            @Override
            public void draw(Bullet b){
                Draw.color((Color)b.data);
                Lines.stroke(2f);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), 7f);
                Lines.stroke(1.3f);
                Draw.color(frontColor);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), 4f);
                Draw.reset();
            }
        };

        shardLaser = new ExpLaserBulletType(150f, 30f){
            {
                damageInc = 5f;
                status = StatusEffects.shocked;
                statusDuration = 3 * 60f;
                hitUnitExpGain = hitBuildingExpGain = 2f;
                fragBullet = shardLaserFrag;
                toColor = Pal.sapBullet;
            }

            @Override
            public void hit(Bullet b, float x, float y){
                hitEffect.at(x, y, b.rotation(), hitColor);
                hitSound.at(x, y, hitSoundPitch, hitSoundVolume);
        
                Effect.shake(hitShake, hitShake, b);

                for(var i = 0; i < fragBullets; i++){
                    var len = Mathf.random(1f, 7f);
                    var a = b.rotation() + Mathf.randomSeed(b.id, 360f) + i * 360f/fragBullets;
                    var target = Damage.linecast(b, x, y, b.rotation(), length);
        
                    Object data = getColor(b);
                    b.data = target;
        
                    if(target instanceof Hitboxc hit){
                        fragBullet.create(b.owner, b.team, hit.x() + Angles.trnsx(a, len), hit.y() + Angles.trnsy(a, len), a, -1f, 1f, 1f, data);
        
                    }else if(target instanceof Building tile){
                        if(tile.collide(b)){
                            fragBullet.create(b.owner, b.team, tile.x() + Angles.trnsx(a, len), tile.y() + Angles.trnsy(a, len), a, -1f, 1f, 1f, data);
                        }
                    }else{
                        b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
                    }
                }
            }
        };

        frostLaser = new ExpLaserBulletType(170f, 130f){
            {
                damageInc = 2.5f;
                status = StatusEffects.freezing;
                statusDuration = 3 * 60f;
                hitUnitExpGain = 1.5f;
                hitBuildingExpGain = 2f;
                shootEffect = UnityFx.shootFlake;
                fromColor = Liquids.cryofluid.color;
                toColor = Color.cyan;
            }

            public void freezePos(Bullet b, float x, float y){
                var lvl = getLevel(b);
                float rad = 3.5f;
                if(!Vars.headless) UnityFx.freezeEffect.at(x, y, lvl / rad + 10f, getColor(b));
                if(!Vars.headless) UnitySounds.laserFreeze.at(x, y, 1f, 0.6f);
        
                Damage.status(b.team, x, y, 10f + lvl / rad, status, 60f + lvl * 6f, true, true);
                Damage.status(b.team, x, y, 10f + lvl / rad, UnityStatusEffects.disabled, 2f * lvl, true, true);
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
                    if(b.owner instanceof ExpBuildc exp) exp.incExp(hitUnitExpGain);
                }else if(target instanceof Building tile && tile.collide(b)){
                    tile.collision(b);
                    hit(b, tile.x, tile.y);
                    freezePos(b, tile.x, tile.y);
                    if(b.owner instanceof ExpBuildc exp) exp.incExp(hitBuildingExpGain);
                }else{
                    b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
                }
            }
        };

        branchLaserFrag = new BasicBulletType(3.5f, 15f){
            {
                frontColor = Pal.lancerLaser;
                backColor = Pal.lancerLaser.cpy().mul(0.7f);
                width = height = 2f;
                weaveScale = 0.6f;
                weaveMag = 0.5f;
                homingPower = 0.4f;
                lifetime = 30f;
                shootEffect = Fx.hitLancer;
                hitEffect = despawnEffect = UnityFx.branchFragHit;
                pierceCap = 10;
                pierceBuilding = true;
                splashDamageRadius = 4f;
                splashDamage = 4f;
                status = UnityStatusEffects.plasmaed;
                statusDuration = 180f;
            }

            @Override
            public void init(Bullet b){
                b.fdata = (float)b.data;
                b.data = new Trail(6);
            }

            public Color getColor(Bullet b){
                return Tmp.c1.set(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f)).lerp(Pal.sapBullet, b.fdata).cpy();
            }

            @Override
            public void draw(Bullet b){
                if(b.data instanceof Trail tr) tr.draw(frontColor, width);
        
                Draw.color(getColor(b));
                Fill.square(b.x, b.y, width, b.rotation() + 45);
                Draw.color();
            }
        
            @Override
            public void update(Bullet b){
                super.update(b);
                if(b.data instanceof Trail tr) tr.update(b.x, b.y);
            }
        
            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                if(b.data instanceof Trail tr) tr.clear();
            }
        };

        branchLaser = new ExpLaserBulletType(140f, 20f){
            {
                damageInc = 6f;
                lengthInc = 2f;
                status = StatusEffects.shocked;
                statusDuration = 3 * 60f;
                hitUnitExpGain = hitBuildingExpGain = 1f;
                fragBullet = shardLaserFrag;
                fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f);
                toColor = Pal.sapBullet;
                fragBullets = 3;
                fragBullet = branchLaserFrag;
                maxRange = 150f + 2f * 30f; //Account for range increase
            }

            public void makeFrag(Bullet b, float x, float y, Object color){
                for(int i = 0; i < fragBullets; i++){
                    fragBullet.create(b.owner, b.team, x, y, b.rotation() + Mathf.randomSeed(b.id, 360f) + i * 360f/fragBullets, -1f, 1f, 1f, color);
                }
            }

            @Override
            public void init(Bullet b){
                super.init(b);

                if(b.data instanceof Position point) makeFrag(b, point.getX(), point.getY(), getLevelf(b));
            }

            @Override
            public void hit(Bullet b, float x, float y){
                hitEffect.at(x, y, b.rotation(), hitColor);
                hitSound.at(b);
        
                Effect.shake(hitShake, hitShake, b);
            }
        };

        //TODO Implement SK's any liquid idea.
        kelvinLaser = new ExpLaserBulletType(170f, 130f){
            {
                damageInc = 2.5f;
                status = StatusEffects.freezing;
                statusDuration = 3 * 60f;
                hitUnitExpGain = 1.5f;
                hitBuildingExpGain = 2f;
                shootEffect = UnityFx.shootFlake;
                fromColor = Liquids.cryofluid.color;
                toColor = Color.cyan;
            }

            public void freezePos(Bullet b, float x, float y){
                var lvl = getLevel(b);
                float rad = 3.5f;
                if(!Vars.headless) UnityFx.freezeEffect.at(x, y, lvl / rad + 10f, getColor(b));
                if(!Vars.headless) UnitySounds.laserFreeze.at(x, y, 1f, 0.6f);
        
                Damage.status(b.team, x, y, 10f + lvl / rad, status, 60f + lvl * 6f, true, true);
                Damage.status(b.team, x, y, 10f + lvl / rad, UnityStatusEffects.disabled, 2f * lvl, true, true);
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
                    if(b.owner instanceof ExpBuildc exp) exp.incExp(hitUnitExpGain);
                }else if(target instanceof Building tile && tile.collide(b)){
                    tile.collision(b);
                    hit(b, tile.x, tile.y);
                    freezePos(b, tile.x, tile.y);
                    if(b.owner instanceof ExpBuildc exp) exp.incExp(hitBuildingExpGain);
                }else{
                    b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
                }
            }
        };

        breakthroughLaser = new ExpLaserBlastBulletType(500f, 1200f){
            {
                damageInc = 34;
                lengthInc = 5;
                largeHit = true;
                width = 80f;
                widthInc = 3.5f;
                lifetime = 65f;
                lightningSpacingInc = -5f/30f;
                lightningDamageInc = 1f;
                hitUnitExpGain = 0.2f;
                hitBuildingExpGain = 0.3f;
                sideLength = 0f;
                sideWidth = 0f;
            }
        };

        /* TODO koruh bullets
        coalBlaze = new BulletType(3.35f, 32f){
            {
                ammoMultiplier = 3;
                hitSize = 7f;
                lifetime = 24f;
                pierce = true;
                statusDuration = 60 * 4f;
                shootEffect = shootSmallBlaze;
                hitEffect = Fx.hitFlameSmall;
                despawnEffect = Fx.none;
                status = StatusEffects.burning;
                keepVelocity = true;
                hittable = true;
            }

            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                ((ExpItemTurret.ExpItemTurretBuild)b.owner).incExp(0.5f);
            }
        };

        pyraBlaze = new BulletType(3.35f, 46f){
            {
                ammoMultiplier = 3;
                hitSize = 7f;
                lifetime = 24f;
                pierce = true;
                statusDuration = 60 * 4f;
                shootEffect = shootPyraBlaze;
                hitEffect = Fx.hitFlameSmall;
                despawnEffect = Fx.none;
                status = StatusEffects.burning;
                keepVelocity = false;
                hittable = false;
            }

            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                ((ExpItemTurret.ExpItemTurretBuild)b.owner).incExp(0.5f);
            }
        };*/

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
            strokes = new float[]{2f * 2.2f, 1.5f * 2.2f, 1f * 2.2f, 0.3f * 2.2f};
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

        orb = new BulletType(){
            {
                lifetime = 240;
                speed = 1.24f;
                damage = 23;
                pierce = true;
                hittable = false;
                hitEffect = UnityFx.orbHit;
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
            hitEffect = UnityFx.plasmaTriangleHit;
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
            damage = 28f;
        }};

        pylonLaser = new LaserBulletType(420f){
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

        pylonLaserSmall = new LaserBulletType(48f){{
            length = 180f;
            width = 24f;
        }};

        exporb = new ExpOrb();

        monumentRailBullet = new PointBulletType(){{
            damage = 1000;
            buildingDamageMultiplier = 0.7f;
            speed = maxRange = 500f;
            lifetime = 1f;
            hitShake = 6f;
            trailSpacing = 35f;
            shootEffect = UnityFx.monumentShoot;
            despawnEffect = UnityFx.monumentDespawn;
            smokeEffect = shootEffect = Fx.blastExplosion;
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
            hitEffect = UnityFx.hitAdvanceFlame;
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
            hitEffect = UnityFx.hitAdvanceFlame;
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

        kamiLaser = new KamiLaserBulletType(230f){{
            lifetime = 4f * 60f;
            length = 760f;
            width = 140f;
            fadeInTime = 60f;
            drawSize = (length + (width * 2f)) * 2f;
        }};

        kamiSmallLaser = new KamiLaserBulletType(230f){{
            lifetime = 2f * 60f;
            length = 760f;
            width = 20f;
            fadeInTime = 15f;
            curveScl = 3f;
            drawSize = (length + (width * 2f)) * 2f;
        }};

        supernovaLaser = new ContinuousLaserBulletType(400f){
            final Effect plasmaEffect = new Effect(36f, e -> {
                Draw.color(Color.white, Pal.lancerLaser, e.fin());
                Fill.circle(
                    e.x + Angles.trnsx(e.rotation, e.fin() * 24f),
                    e.y + Angles.trnsy(e.rotation, e.fin() * 24f),
                    e.fout() * 5f
                );
            });

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
            }

            @Override
            public void draw(Bullet b){
                super.draw(b);

                if(!state.isPaused()){
                    for(int i = 0; i < 2; i++){
                        float f = Mathf.random(length * b.fout());
                        plasmaEffect.at(
                            b.x + Angles.trnsx(b.rotation(), f) + Mathf.range(6f),
                            b.y + Angles.trnsy(b.rotation(), f) + Mathf.range(6f),
                            b.rotation() + Mathf.range(85f)
                        );
                    }
                }
            }

            {
                colors = new Color[]{
                    Color.valueOf("4be3ca55"),
                    Color.valueOf("91eedeaa"),
                    Pal.lancerLaser.cpy(),
                    Color.white
                };

                length = 280f;
            }
        };

        //only enhanced

        standardDenseLarge = copy(Bullets.standardDenseBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.speed *= 1.1f;
            t.width *= 1.12f;
            t.height *= 1.12f;
        });

        standardHomingLarge = copy(Bullets.standardDenseBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.23f;
            t.reloadMultiplier = 1.3f;
            t.homingPower = 0.09f;
            t.speed *= 1.1f;
            t.width *= 1.09f;
            t.height *= 1.09f;
        });

        standardIncendiaryLarge = copy(Bullets.standardIncendiaryBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.speed *= 1.1f;
            t.width *= 1.12f;
            t.height *= 1.12f;
        });

        standardThoriumLarge = copy(Bullets.standardThoriumBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.speed *= 1.1f;
            t.width *= 1.12f;
            t.height *= 1.12f;
        });

        standardDenseHeavy = copy(Bullets.standardDenseBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.7f;
            t.speed *= 1.3f;
            t.width *= 1.32f;
            t.height *= 1.32f;
        });

        standardHomingHeavy = copy(Bullets.standardDenseBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.4f;
            t.reloadMultiplier = 1.3f;
            t.homingPower = 0.09f;
            t.speed *= 1.3f;
            t.width *= 1.19f;
            t.height *= 1.19f;
        });

        standardIncendiaryHeavy = copy(Bullets.standardIncendiaryBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.7f;
            t.speed *= 1.3f;
            t.width *= 1.32f;
            t.height *= 1.32f;
        });

        standardThoriumHeavy = copy(Bullets.standardThoriumBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.7f;
            t.speed *= 1.3f;
            t.width *= 1.32f;
            t.height *= 1.32f;
        });

        standardDenseMassive = copy(Bullets.standardDenseBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.8f;
            t.speed *= 1.3f;
            t.width *= 1.34f;
            t.height *= 1.34f;
            t.lifetime *= 1.1f;
        });

        standardHomingMassive = copy(Bullets.standardDenseBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.6f;
            t.reloadMultiplier = 1.3f;
            t.homingPower = 0.09f;
            t.speed *= 1.3f;
            t.width *= 1.21f;
            t.height *= 1.21f;
            t.lifetime *= 1.1f;
        });

        standardIncendiaryMassive = copy(Bullets.standardIncendiaryBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.8f;
            t.speed *= 1.3f;
            t.width *= 1.34f;
            t.height *= 1.34f;
            t.lifetime *= 1.1f;
        });

        standardThoriumMassive = copy(Bullets.standardThoriumBig, BasicBulletType::new, (BasicBulletType t) -> {
            t.damage *= 1.8f;
            t.speed *= 1.3f;
            t.width *= 1.34f;
            t.height *= 1.34f;
            t.lifetime *= 1.1f;
        });

        //endregion
    }
}
