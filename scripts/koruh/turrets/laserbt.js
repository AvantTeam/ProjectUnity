const lib = this.global.unity.exp;
const expColor = Color.valueOf("84ff00");

const laserCharge = new Effect(38, e => {
    Draw.color(e.color);
    Angles.randLenVectors(e.id, e.id % 3 + 1, 1 + 20 * e.fout(), e.rotation, 120, (x, y) => {
        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
    });
});

const laserChargeBegin = new Effect(100, 100, e => {
    Draw.color(e.color);
    Lines.stroke(e.fin() * 3);
    Lines.circle(e.x, e.y, 4 + e.fout() * 120);

    Fill.circle(e.x, e.y, e.fin() * 23);

    Angles.randLenVectors(e.id, 20, 50 * e.fout(), (x, y) => {
        Fill.circle(e.x + x, e.y + y, e.fin() * 6);
    });

    Draw.color();

    Fill.circle(e.x, e.y, e.fin() * 13);
});

const laserChargeShoot = new Effect(40, e => {
    Draw.color(e.color);

    Lines.stroke(e.fout() * 2.5);
    Lines.circle(e.x, e.y, e.finpow() * 100);

    Lines.stroke(e.fout() * 5);
    Lines.circle(e.x, e.y, e.fin() * 100);

    Draw.color(e.color, Color.white, e.fout());

    Angles.randLenVectors(e.id, 20, 80 * e.finpow(), (x, y) => {
        Fill.circle(e.x + x, e.y + y, e.fout() * 5);
    });


    for(var i=0; i<4; i++){
        Drawf.tri(e.x, e.y, 9 * e.fout(), 170, e.rotation + 90 * i + e.finpow() * (0.5-Mathf.randomSeed(e.id)) * 150);
    }
});

//last laser only levels up once, so just make 2 different bullets
const chargeLaser = extend(LaserBulletType, {
    init(b){
        if(!b) return;
        this.super$init(b);

        var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
        if(target instanceof Hitboxc){
            b.owner.incExp(0.15);
        }
        else if(target instanceof Building){
            b.owner.incExp(0.3);
        }
    }
});
chargeLaser.length = 500;
chargeLaser.damage = 1280;
chargeLaser.width = 80;
chargeLaser.lifetime = 65;
chargeLaser.lightningSpacing = 35;
chargeLaser.lightningLength = 5;
chargeLaser.lightningDelay = 1.1;
chargeLaser.lightningLengthRand = 15;
chargeLaser.lightningDamage = 90;
chargeLaser.lightningAngleRand = 40;
chargeLaser.largeHit = true;
chargeLaser.lightColor = chargeLaser.lightningColor = Pal.lancerLaser;
chargeLaser.sideAngle = 15;
chargeLaser.sideWidth = 0;
chargeLaser.sideLength = 0;
chargeLaser.colors = [Pal.sapBullet.cpy().lerp(Pal.lancerLaser, 0.5).mul(1, 1, 1, 0.4), Pal.lancerLaser, Color.white];

const chargeLaser2 = extend(LaserBulletType, {});
chargeLaser2.length = 650;
chargeLaser2.damage = 2280;
chargeLaser2.width = 90;
chargeLaser2.lifetime = 70;
chargeLaser2.lightningSpacing = 30;
chargeLaser2.lightningLength = 5;
chargeLaser2.lightningDelay = 1.1;
chargeLaser2.lightningLengthRand = 15;
chargeLaser2.lightningDamage = 120;
chargeLaser2.lightningAngleRand = 40;
chargeLaser2.largeHit = true;
chargeLaser2.lightColor = chargeLaser2.lightningColor = expColor;
chargeLaser2.sideAngle = 15;
chargeLaser2.sideWidth = 0;
chargeLaser2.sideLength = 0;
chargeLaser2.colors = [expColor.cpy().mul(1, 1, 1, 0.4), expColor, Color.white];



const chargeLaserTurret = lib.extend(PowerTurret, PowerTurret.PowerTurretBuild, "bt-laser-turret", {
    maxLevel: 1,
    expFields: [
        {
            type: "list",
            field: "heatColor",
            intensity: [Pal.lancerLaser, expColor]
        }
    ],
    rwPrecision: 20,
    orbMultiplier: 0.07,
    load(){
        this.super$load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    }
}, {
    getShootColor(lvl){
        return lvl > 0 ? expColor : Pal.lancerLaser;
    },
    shoot(ammo){
        this.useAmmo();
        var lvl = this.totalLevel();

        chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
        chargeLaserTurret.chargeBeginEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation, this.getShootColor(lvl));
        chargeLaserTurret.chargeSound.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, 1);

        /*
        for(var i = 0; i < chargeLaserTurret.chargeEffects; i++){
            Time.run(Mathf.random(chargeLaserTurret.chargeMaxDelay), () => {
                if(!this.isValid()) return;
                chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
                chargeLaserTurret.chargeEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation, this.getShootColor(lvl));
            });
        }*/

        this.charging = true;

        Time.run(chargeLaserTurret.chargeTime, () => {
            if(!this.isValid()) return;
            chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
            this.recoil = chargeLaserTurret.recoilAmount;
            this.heat = 1;
            this.bullet(lvl > 0 ? chargeLaser2 : chargeLaser, this.rotation);
            this.effects();
            this.charging = false;
        });
    },
    effects(){
        var fshootEffect = chargeLaserTurret.shootEffect == Fx.none ? this.peekAmmo().shootEffect : chargeLaserTurret.shootEffect;
        //var fsmokeEffect = chargeLaserTurret.smokeEffect == Fx.none ? this.peekAmmo().smokeEffect : chargeLaserTurret.smokeEffect;
        fshootEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation, this.getShootColor(this.totalLevel()));
        //fsmokeEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation);
        chargeLaserTurret.shootSound.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, Mathf.random(0.9, 1.1));

        if(chargeLaserTurret.shootShake > 0){
            Effect.shake(chargeLaserTurret.shootShake, chargeLaserTurret.shootShake, this);
        }

        this.recoil = chargeLaserTurret.recoilAmount;
    }
});

chargeLaserTurret.drawer = tile => {
    Draw.rect(chargeLaserTurret.region, tile.x + chargeLaserTurret.tr2.x, tile.y + chargeLaserTurret.tr2.y, tile.rotation - 90);
    if(tile.totalExp() >= chargeLaserTurret.maxExp){
        //Draw.blend(Blending.additive);
        Draw.color(expColor);
        Draw.alpha(Mathf.absin(Time.time, 20, 0.6));
        Draw.rect(chargeLaserTurret.topRegion, tile.x + chargeLaserTurret.tr2.x, tile.y + chargeLaserTurret.tr2.y, tile.rotation - 90);
        Draw.color();
        //Draw.blend();
    }
}

chargeLaserTurret.chargeTime = 100;
chargeLaserTurret.chargeMaxDelay = 100;
chargeLaserTurret.chargeEffects = 0;
chargeLaserTurret.recoilAmount = 5;
chargeLaserTurret.cooldown = 0.03;
chargeLaserTurret.powerUse = 17;
chargeLaserTurret.shootShake = 4;
chargeLaserTurret.shootEffect = laserChargeShoot;
chargeLaserTurret.smokeEffect = Fx.none;
chargeLaserTurret.chargeEffect = Fx.none;
chargeLaserTurret.chargeBeginEffect = laserChargeBegin;
chargeLaserTurret.heatColor = Pal.lancerLaser;
chargeLaserTurret.shootSound = Sounds.laserblast;
chargeLaserTurret.chargeSound = Sounds.lasercharge;
chargeLaserTurret.shootType = chargeLaser;
chargeLaserTurret.buildVisibility = BuildVisibility.sandboxOnly;
