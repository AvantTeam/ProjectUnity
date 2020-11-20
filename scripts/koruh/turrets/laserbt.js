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
    Lines.stroke(e.fin() * 2);
    Lines.circle(e.x, e.y, 4 + e.fout() * 100);

    Fill.circle(e.x, e.y, e.fin() * 20);

    Angles.randLenVectors(e.id, 20, 40 * e.fout(), (x, y) => {
        Fill.circle(e.x + x, e.y + y, e.fin() * 5);
    });

    Draw.color();

    Fill.circle(e.x, e.y, e.fin() * 10);
});

const laserChargeShoot = new Effect(40, e => {
    Draw.color(e.color);

    Lines.stroke(e.fout() * 2.5);
    Lines.circle(e.x, e.y, e.finpow() * 85);

    Lines.stroke(e.fout() * 4);
    Lines.circle(e.x, e.y, e.fin() * 85);

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
            b.owner.incExp(0.05);
        }
        else if(target instanceof Building){
            b.owner.incExp(0.1);
        }
    }
});
chargeLaser.length = 450;
chargeLaser.damage = 780;
chargeLaser.width = 75;
chargeLaser.lifetime = 65;
chargeLaser.lightningSpacing = 35;
chargeLaser.lightningLength = 5;
chargeLaser.lightningDelay = 1.1;
chargeLaser.lightningLengthRand = 15;
chargeLaser.lightningDamage = 50;
chargeLaser.lightningAngleRand = 40;
chargeLaser.largeHit = true;
chargeLaser.lightColor = chargeLaser.lightningColor = Pal.lancerLaser;
chargeLaser.sideAngle = 15;
chargeLaser.sideWidth = 0;
chargeLaser.sideLength = 0;
chargeLaser.colors = [Pal.sapBullet.cpy().lerp(Pal.lancerLaser, 0.5).mul(1, 1, 1, 0.4), Pal.lancerLaser, Color.white];



const chargeLaserTurret = lib.extend(ChargeTurret, ChargeTurret.ChargeTurretBuild, "bt-laser-turret", {
    maxLevel: 1,
    expFields: [
        {
            type: "list",
            field: "heatColor",
            intensity: [Pal.lancerLaser, expColor]
        },
        {
            type: "list",
            field: "chargeTime",
            intensity: [100, 150]
        },
    ]
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

        this.shooting = true;

        Time.run(chargeLaserTurret.chargeTime, () => {
            if(!this.isValid()) return;
            chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
            this.recoil = chargeLaserTurret.recoilAmount;
            this.heat = 1;
            this.bullet(ammo, this.rotation);
            this.effects();
            this.shooting = false;
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
