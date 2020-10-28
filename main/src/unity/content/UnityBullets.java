package unity.content;

import arc.util.Tmp;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.io.JsonIO;
import mindustry.ctype.ContentList;
import unity.entities.bullet.*;
import unity.world.blocks.experience.*;

import arc.graphics.g2d.*;

import static arc.math.Mathf.*;
import static mindustry.Vars.tilesize;
import static mindustry.graphics.Drawf.*;
import static mindustry.graphics.Pal.*;
import static unity.content.UnityFx.*;

public class UnityBullets implements ContentList{
    public static BulletType laser, coalBlaze, pyraBlaze, falloutLaser, catastropheLaser, calamityLaser, orb;
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
            @Override
            public void draw(Bullet b){
                light(b.x, b.y, 16, surge, 0.6f);

                Draw.color(surge);
                Lines.circle(b.x, b.y, 4);

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
        };

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
