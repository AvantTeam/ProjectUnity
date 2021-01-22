package unity.content;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.graphics.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.ctype.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.entities.bullet.*;

import arc.graphics.g2d.*;
import unity.graphics.*;

import static arc.math.Mathf.*;
import static mindustry.Vars.*;
import static mindustry.graphics.Drawf.*;
import static mindustry.graphics.Pal.*;
import static unity.content.UnityFx.*;

public class UnityBullets implements ContentList{
    public static BulletType laser, coalBlaze, pyraBlaze, falloutLaser, catastropheLaser, calamityLaser, extinctionLaser, orb, shockBeam, currentStroke,
        shielderBullet, plasmaFragTriangle, plasmaTriangle, surgeBomb, pylonLightning, pylonLaser, pylonLaserSmall, exporb, monumentRailBullet, scarShrapnel, scarMissile, celsiusSmoke, kelvinSmoke,
        kamiBullet1;

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
        /*laser = new SapBulletType(){
            {
                length = 150f;
                width = 0.7f;
                damage = 30f;
                lifetime = 18f;
                despawnEffect = Fx.none;
                pierce = true;
                hitSize = 0f;
                status = StatusEffects.shocked;
                statusDuration = 3 * 60f;
                hittable = false;
                hitEffect = Fx.hitLiquid;
                sapStrength = 0f;
            }

            @Override
            public void init(Bullet b){
                ExpPowerTurret.ExpPowerTurretBuild exp = (ExpPowerTurret.ExpPowerTurretBuild)b.owner;
                int lvl = exp.getLevel();
                b.damage(damage + lvl * 10f);
                b.fdata = lvl / 10f;
                super.init(b);
                if(!(b.data instanceof Vec2)) exp.incExp(2f);
            }

            @Override
            public void draw(Bullet b){
                color = Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, b.fdata);
                super.draw(b);
            }
        };

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
        };TODO*/

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
                hitEffect = orbHit;
                trailEffect = orbTrail;
                trailChance = 0.4f;
            }

            @Override
            public void draw(Bullet b){
                light(b.x, b.y, 16, surge, 0.6f);

                Draw.color(surge);
                Fill.circle(b.x, b.y, 4);

                Draw.color();
                Fill.circle(b.x, b.y, 2.5f);
            }

            @Override
            public void update(Bullet b){
                super.update(b);
                if(b.timer.get(1, 7)) Units.nearbyEnemies(b.team, b.x - 5 * tilesize, b.y - 5 * tilesize, 5 * tilesize * 2, 5 * tilesize * 2, unit -> Lightning.create(b.team, Pal.surge, random(17, 33), b.x, b.y, b.angleTo(unit), random(7, 13)));
            }

            @Override
            public void drawLight(Bullet b){}
        };

        shockBeam = new ContinuousLaserBulletType(35f){
            {
                speed = 0.0001f;
                shootEffect = Fx.none;
                despawnEffect = Fx.none;
                pierce = true;
                hitSize = 0f;
                status = StatusEffects.shocked;
                statusDuration = 3f * 60f;
                width = 0.62f;
                length = 120f;
                hittable = false;
                hitEffect = Fx.hitLiquid;

            }

            @Override
            public void init(Bullet b){
                super.init(b);

                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
                b.data = target;

                if(!(target instanceof Building) && target instanceof Hitboxc hit){
                    hit.collision(hit, hit.x(), hit.y());
                    b.collision(hit, hit.x(), hit.y());
                }else{
                    b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
                }
            }

            @Override
            public void update(Bullet b){
                if(b.timer.get(1, 5)){
                    Posc target = ((LaserTurret.LaserTurretBuild)b.owner).target;
                    if(target == null) return;
                    Lightning.create(b.team, surge, Mathf.random(this.damage / 1.8f, this.damage / 1.2f), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / Vars.tilesize + 3));
                    if(target instanceof Healthc h) h.damage(this.damage);
                }
            }

            @Override
            public void draw(Bullet b){
                Posc target = ((LaserTurret.LaserTurretBuild)b.owner).target;
                if(target != null){
                    Draw.color(surge);
                    Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, target.x(), target.y(), width * b.fout());
                    Draw.reset();

                    Drawf.light(b.team, b.x, b.y, target.x(), target.y(), 15 * b.fout(), this.lightColor, 0.6f);
                }else if(b.data instanceof Position data){
                    Tmp.v1.set(data);

                    Draw.color(surge);
                    Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
                    Draw.reset();

                    Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, 15 * b.fout(), surge, 0.6f);
                }
            }
        };

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

        plasmaFragTriangle = new TriangleBulletType(4.5f, 90f){{
            lifetime = 160f;
            lifetimeRand = 40f;
            width = 10f;
            length = 11f;
            trailWidth = 4f;
            trailLength = 8;
            drag = 0.05f;
            collides = false;
            summonsLightning = true;
            shootEffect = plasmaFragAppear;
            hitEffect = despawnEffect = plasmaFragDisappear;
        }};

        plasmaTriangle = new TriangleBulletType(4f, 380f){{
            lifetime = 180f;
            width = 16f;
            length = 20f;
            trailWidth = 6.5f;
            trailLength = 10;
            hitEffect = plasmaTriangleHit;
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
            despawnEffect = UnityFx.surgeBomb;
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
                shootEffect = pylonLaserCharge;
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
            shootEffect = monumentShoot;
            despawnEffect = monumentDespawn;
            smokeEffect = shootEffect = Fx.blastExplosion;
            trailEffect = monumentTrail;
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
            color = b -> Tmp.c1.set(Color.red).shiftHue(b.time * 3f);
        }};

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
