package unity.content;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.io.JsonIO;
import mindustry.ctype.ContentList;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import unity.entities.bullet.*;
import unity.world.blocks.experience.*;

import arc.graphics.g2d.*;

import static arc.math.Mathf.*;
import static mindustry.Vars.tilesize;
import static mindustry.graphics.Drawf.*;
import static mindustry.graphics.Pal.*;
import static unity.content.UnityFx.*;

public class UnityBullets implements ContentList{
    public static BulletType laser, coalBlaze, pyraBlaze, falloutLaser, catastropheLaser, calamityLaser, orb, shockBeam, currentStroke, shielderBullet, plasmaFragTriangle, plasmaTriangle;
    
    //only enhanced
    public static BasicBulletType standardDenseLarge, standardHomingLarge, standardIncendiaryLarge, standardThoriumLarge, standardDenseHeavy, standardHomingHeavy, standardIncendiaryHeavy, standardThoriumHeavy, standardDenseMassive, standardHomingMassive,
    standardIncendiaryMassive, standardThoriumMassive;


    @Override
    public void load(){
        laser = new SapBulletType(){
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
                ExpPowerTurret.ExpPowerTurretBuild exp = (ExpPowerTurret.ExpPowerTurretBuild) b.owner;
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
                shootEffect = UnityFx.shootSmallBlaze;
                hitEffect = Fx.hitFlameSmall;
                despawnEffect = Fx.none;
                status = StatusEffects.burning;
                keepVelocity = true;
                hittable = true;
            }

            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                ((ExpItemTurret.ExpItemTurretBuild) b.owner).incExp(0.5f);
            }
        };

        pyraBlaze = new BulletType(3.35f, 46f){
            {
                ammoMultiplier = 3;
                hitSize = 7f;
                lifetime = 24f;
                pierce = true;
                statusDuration = 60 * 4f;
                shootEffect = UnityFx.shootPyraBlaze;
                hitEffect = Fx.hitFlameSmall;
                despawnEffect = Fx.none;
                status = StatusEffects.burning;
                keepVelocity = false;
                hittable = false;
            }

            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                ((ExpItemTurret.ExpItemTurretBuild) b.owner).incExp(0.5f);
            }
        };

        falloutLaser = new SparkingContinuousLaserBulletType(95f){
            {
                length = 230f;
                fromBlockChance = 0.12f;
                fromBlockDamage = 23f;
                fromLaserAmount = 0;
                incendChance = 0f;
                fromBlockLen = 2;
                fromBlockLenRand = 5;
            }
        };

        catastropheLaser = new SparkingContinuousLaserBulletType(240f){
            {
                length = 340f;
                strokes = new float[]{2 * 1.4f, 1.5f * 1.4f, 1 * 1.4f, 0.3f * 1.4f};
                incendSpread = 7f;
                incendAmount = 2;
            }
        };

        calamityLaser = new SparkingContinuousLaserBulletType(580f){
            {
                length = 450f;
                strokes = new float[]{2 * 1.7f, 1.5f * 1.7f, 1 * 1.7f, 0.3f * 1.7f};
                incendSpread = 9f;
                incendAmount = 2;
                width = 9f;
            }
        };

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
                if(b.timer.get(1, 7)){
                    Units.nearbyEnemies(b.team, b.x - 5 * tilesize, b.y - 5 * tilesize, 5 * tilesize * 2, 5 * tilesize * 2, unit -> {
                        Lightning.create(b.team, Pal.surge, random(17, 33), b.x, b.y, b.angleTo(unit), random(7, 13));
                    });
                }
            }

            @Override
            public void drawLight(Bullet b){}
        };

        shockBeam = new ContinuousLaserBulletType(35){
            {
                speed = 0.0001f;
                shootEffect = Fx.none;
                despawnEffect = Fx.none;
                pierce = true;
                hitSize = 0;
                status = StatusEffects.shocked;
                statusDuration = 3 * 60;
                width = 0.42f;
                length = 120;
                hittable = false;
                hitEffect = Fx.hitLiquid;

            }
            
            @Override
            public void init(Bullet b){
                super.init(b);

                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
                b.data = target;

                if(target != null && !(target instanceof Building)){
                    Healthc hit = target;

                    ((Hitboxc) hit).collision((Hitboxc) hit, hit.x(), hit.y());
                    b.collision((Hitboxc) hit, hit.x(), hit.y());
                }else{
                    b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
                }
            }

            @Override
            public void update(Bullet b){
                if(b.timer.get(1, 5)){
                    if(((LaserTurret.LaserTurretBuild) b.owner).target == null) return;
                    Lightning.create(b.team, surge, Mathf.random(this.damage / 1.8f, this.damage / 1.2f), b.x, b.y, b.angleTo(((LaserTurret.LaserTurretBuild) b.owner).target), Mathf.floorPositive(b.dst(((LaserTurret.LaserTurretBuild) b.owner).target) / Vars.tilesize + 3));
                    if(((LaserTurret.LaserTurretBuild) b.owner).target instanceof Healthc){
                        ((Healthc) ((LaserTurret.LaserTurretBuild) b.owner).target).damage(this.damage);
                    }
                }
            }

            @Override
            public void draw(Bullet b){
                Posc target = ((LaserTurret.LaserTurretBuild) b.owner).target;
                if(target != null){
                    Draw.color(surge);
                    Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, target.x(), target.y(), width * b.fout());
                    Draw.reset();

                    Drawf.light(b.team, b.x, b.y, b.x + target.x(), b.y + target.y(), 15 * b.fout(), this.lightColor, 0.6f);
                }else if(b.data instanceof Position){
                    Object data = b.data;
                    Tmp.v1.set((Position) data);

                    Draw.color(surge);
                    Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
                    Draw.reset();

                    Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15 * b.fout(), surge, 0.6f);
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
            shootEffect = UnityFx.plasmaFragAppear;
            hitEffect = despawnEffect = UnityFx.plasmaFragDisappear;
        }};
        
        plasmaTriangle = new TriangleBulletType(4f, 380f){{
            lifetime = 180f;
            width = 16f;
            length = 20f;
            trailWidth = 6.5f;
            trailLength = 10;
            hitEffect = UnityFx.plasmaTriangleHit;
            despawnEffect = Fx.none;
            fragBullet = plasmaFragTriangle;
            fragBullets = 8;
        }};

        //only enhanced

        standardDenseLarge = new BasicBulletType();
        JsonIO.copy(Bullets.standardDenseBig, standardDenseLarge);
        standardDenseLarge.damage *= 1.2f;
        standardDenseLarge.speed *= 1.1f;
        standardDenseLarge.width *= 1.12f;
        standardDenseLarge.height *= 1.12f;

        standardHomingLarge = new BasicBulletType();
        JsonIO.copy(Bullets.standardDenseBig, standardHomingLarge);
        standardHomingLarge.damage *= 1.1f;
        standardHomingLarge.reloadMultiplier = 1.3f;
        standardHomingLarge.homingPower = 0.09f;
        standardHomingLarge.speed *= 1.1f;
        standardHomingLarge.width *= 1.09f;
        standardHomingLarge.height *= 1.09f;

        standardIncendiaryLarge = new BasicBulletType();
        JsonIO.copy(Bullets.standardIncendiaryBig, standardIncendiaryLarge);
        standardIncendiaryLarge.damage *= 1.2f;
        standardIncendiaryLarge.speed *= 1.1f;
        standardIncendiaryLarge.width *= 1.12f;
        standardIncendiaryLarge.height *= 1.12f;

        standardThoriumLarge = new BasicBulletType();
        JsonIO.copy(Bullets.standardThoriumBig, standardThoriumLarge);
        standardThoriumLarge.damage *= 1.2f;
        standardThoriumLarge.speed *= 1.1f;
        standardThoriumLarge.width *= 1.12f;
        standardThoriumLarge.height *= 1.12f;

        standardDenseHeavy = new BasicBulletType();
        JsonIO.copy(Bullets.standardDenseBig, standardDenseHeavy);
        standardDenseHeavy.damage *= 1.6f;
        standardDenseHeavy.speed *= 1.3f;
        standardDenseHeavy.width *= 1.32f;
        standardDenseHeavy.height *= 1.32f;

        standardHomingHeavy = new BasicBulletType();
        JsonIO.copy(Bullets.standardDenseBig, standardHomingHeavy);
        standardHomingHeavy.damage *= 1.4f;
        standardHomingHeavy.reloadMultiplier = 1.3f;
        standardHomingHeavy.homingPower = 0.09f;
        standardHomingHeavy.speed *= 1.3f;
        standardHomingHeavy.width *= 1.19f;
        standardHomingHeavy.height *= 1.19f;

        standardIncendiaryHeavy = new BasicBulletType();
        JsonIO.copy(Bullets.standardIncendiaryBig, standardIncendiaryHeavy);
        standardIncendiaryHeavy.damage *= 1.6f;
        standardIncendiaryHeavy.speed *= 1.3f;
        standardIncendiaryHeavy.width *= 1.32f;
        standardIncendiaryHeavy.height *= 1.32f;

        standardThoriumHeavy = new BasicBulletType();
        JsonIO.copy(Bullets.standardThoriumBig, standardThoriumHeavy);
        standardThoriumHeavy.damage *= 1.6f;
        standardThoriumHeavy.speed *= 1.3f;
        standardThoriumHeavy.width *= 1.32f;
        standardThoriumHeavy.height *= 1.32f;

        standardDenseMassive = new BasicBulletType();
        JsonIO.copy(Bullets.standardDenseBig, standardDenseMassive);
        standardDenseMassive.damage *= 1.7f;
        standardDenseMassive.speed *= 1.3f;
        standardDenseMassive.width *= 1.34f;
        standardDenseMassive.height *= 1.34f;
        standardDenseMassive.lifetime *= 1.1f;

        standardHomingMassive = new BasicBulletType();
        JsonIO.copy(Bullets.standardDenseBig, standardHomingMassive);
        standardHomingMassive.damage *= 1.5f;
        standardHomingMassive.reloadMultiplier = 1.3f;
        standardHomingMassive.homingPower = 0.09f;
        standardHomingMassive.speed *= 1.3f;
        standardHomingMassive.width *= 1.21f;
        standardHomingMassive.height *= 1.21f;
        standardDenseMassive.lifetime *= 1.1f;

        standardIncendiaryMassive = new BasicBulletType();
        JsonIO.copy(Bullets.standardIncendiaryBig, standardIncendiaryMassive);
        standardIncendiaryMassive.damage *= 1.7;
        standardIncendiaryMassive.speed *= 1.3;
        standardIncendiaryMassive.width *= 1.34;
        standardIncendiaryMassive.height *= 1.34;
        standardIncendiaryMassive.lifetime *= 1.1;

        standardThoriumMassive = new BasicBulletType();
        JsonIO.copy(Bullets.standardThoriumBig, standardThoriumMassive);
        standardThoriumMassive.damage *= 1.7;
        standardThoriumMassive.speed *= 1.3;
        standardThoriumMassive.width *= 1.34;
        standardThoriumMassive.height *= 1.34;
        standardThoriumMassive.lifetime *= 1.1;

        //endregion
    }
}
