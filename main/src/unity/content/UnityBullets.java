package unity.content;

import arc.util.Tmp;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.io.JsonIO;
import mindustry.ctype.ContentList;
import unity.entities.bullet.*;
import unity.world.blocks.experience.*;

public class UnityBullets implements ContentList{
    public static BulletType laser, coalBlaze, pyraBlaze, falloutLaser, catastropheLaser, calamityLaser,
    //only enhanced
    standardDenseLarge, standardHomingLarge, standardIncendiaryLarge, standardThoriumLarge;

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
                b.owner.<ExpItemTurret.ExpItemTurretBuild>self().incExp(0.5f);
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
                b.owner.<ExpItemTurret.ExpItemTurretBuild>self().incExp(0.5f);
            }
        };

        falloutLaser = new SparkingContinuousLaserBulletType(95f){
            {
                length = 230f;
                fromBlockChance = 0.12f;
                fromBlockDamage=23f;
                fromLaserAmount = 0;
                incendChance = 0f;
                fromBlockLen=2;
                fromBlockLenRand=5;
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

        //only enhanced

        standardDenseLarge = new BasicBulletType();
        JsonIO.json().copyFields(Bullets.standardDenseBig, standardDenseLarge);
        standardDenseLarge.damage *= 1.2f;
        standardDenseLarge.speed *= 1.1f;
        ((BasicBulletType) standardDenseLarge).width *= 1.12f;
        ((BasicBulletType) standardDenseLarge).height *= 1.12f;

        standardHomingLarge = new BasicBulletType();
        JsonIO.json().copyFields(Bullets.standardDenseBig, standardHomingLarge);
        standardHomingLarge.damage *= 1.1f;
        standardHomingLarge.reloadMultiplier = 1.3f;
        standardHomingLarge.homingPower = 0.09f;
        standardHomingLarge.speed *= 1.1f;
        ((BasicBulletType) standardHomingLarge).width *= 1.09f;
        ((BasicBulletType) standardHomingLarge).height *= 1.09f;

        standardIncendiaryLarge = new BasicBulletType();
        JsonIO.json().copyFields(Bullets.standardIncendiaryBig, standardIncendiaryLarge);
        standardIncendiaryLarge.damage *= 1.2f;
        standardIncendiaryLarge.speed *= 1.1f;
        ((BasicBulletType) standardIncendiaryLarge).width *= 1.12f;
        ((BasicBulletType) standardIncendiaryLarge).height *= 1.12f;

        standardThoriumLarge = new BasicBulletType();
        JsonIO.json().copyFields(Bullets.standardThoriumBig, standardThoriumLarge);
        standardThoriumLarge.damage *= 1.2f;
        standardThoriumLarge.speed *= 1.1f;
        ((BasicBulletType) standardThoriumLarge).width *= 1.12f;
        ((BasicBulletType) standardThoriumLarge).height *= 1.12f;

        //endregion
    }
}
