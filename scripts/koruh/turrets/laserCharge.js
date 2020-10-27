const lib = this.global.unity.exp;

const laserCharge = new Effect(38, e => {
    Draw.color(e.color);
    Angles.randLenVectors(e.id, Mathf.random(2, 4), 1 + 20 * e.fout(), e.rotation, 120, (x, y) => {
        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
    });
});

const laserChargeBegin = new Effect(60, e => {
    Draw.color(e.color);
    Fill.circle(e.x, e.y, e.fin() * 3);

    Draw.color();
    Fill.circle(e.x, e.y, e.fin() * 2);
});

const laserChargeShoot = new Effect(21, e => {
    Draw.color(e.color);

    for(var i of Mathf.signs){
        Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 90 * i);
    }
});
const chargeLaser = extend(BulletType, {
    getDamage(b){
        return this.damage + (b.owner.totalLevel() * 12);
    },

    getColor(b){
        return Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, b.owner.totalLevel() / 10);
    },

    collision(other, x, y){
        this.hit(this.base(), x, y);
        if(other instanceof Healthc){
            var h = other;
            h.damage(this.getDamage(this));
        }else if(other instanceof Unit) {
            var unit = other;
            unit.impulse(Tmp.v3.set(unit).sub(this.x, this.y).nor().scl(this.knockback * 80.0));
            unit.apply(this.status, this.statusDuration);
        }if(!this.pierce){
            this.remove();
        }else{
            this.collided.add(other.id());
        }
    },

    init(b){
        if(!b) return;
        this.super$init(b);

        var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
        b.data = target;

        if(target instanceof Hitboxc){
            var hit = target;

            hit.collision(b, hit.x, hit.y);
            b.collision(hit, hit.x, hit.y);
            b.owner.incExp(2);
        }else if(target instanceof Building){
            var tile = target;

            if(tile.collide(b)){
                tile.collision(b);
                this.hit(b, tile.x, tile.y);
                b.owner.incExp(2);
            }
        }else{
            b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
        }
    },

    range(){
        return this.length;
    },

    draw(b){
        if(b.data instanceof Position){
            var data = b.data;
            Tmp.v1.set(data);

            Draw.color(this.getColor(b));
            Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
            Draw.reset();

            Drawf.light(Team.derelict, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15 * b.fout() + 5, this.getColor(b), 0.6);
        }
    },

    hit(b){
        this.hitEffect.at(b.x, b.y, b.rotation(), this.hitColor);
        this.hitSound.at(b);

        Effect.shake(this.hitShake, this.hitShake, b);

        for(var i = 0; i < this.fragBullets; i++){
            var len = Mathf.random(1, 7);
            var a = b.rotation() + i * 45;
            var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);

            b.data = target;

            if(target instanceof Hitboxc){
                var hit = target;
                this.fragBullet.create(b, hit.x + Angles.trnsx(a, len), hit.y + Angles.trnsy(a, len), a);

            }else if(target instanceof Building){
                var tile = target;
                if(tile.collide(b)){
                    this.fragBullet.create(b, tile.x + Angles.trnsx(a, len), tile.y + Angles.trnsy(a, len), a);
                }
            } else {
                b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
            }
        }
    }
});

const chargeLaserFrag = new JavaAdapter(LaserBoltBulletType, {
    draw(b){
        Draw.reset();
        this.super$draw(b);
    }
}, 2, 10);
chargeLaserFrag.lifetime = 20;
chargeLaserFrag.pierceCap = 10;
chargeLaserFrag.pierceBuilding = true;
chargeLaserFrag.backColor = Color.white.cpy().lerp(Pal.lancerLaser, 0.1);
chargeLaserFrag.frontColor = Color.white;
chargeLaserFrag.hitEffect = Fx.hitLancer;
chargeLaserFrag.despawnEffect = Fx.hitLancer;

chargeLaser.damage = 30;
chargeLaser.lifetime = 18;
chargeLaser.speed = 0.0001;
chargeLaser.despawnEffect = Fx.none;
chargeLaser.pierce = true;
chargeLaser.hitSize = 0;
chargeLaser.status = StatusEffects.shocked;
chargeLaser.statusDuration = 3 * 60;
chargeLaser.width = 0.7;
chargeLaser.length = 150;
chargeLaser.hittable = false;
chargeLaser.hitEffect = Fx.hitLiquid;
chargeLaser.fragBullet = chargeLaserFrag;


const chargeLaserTurret = lib.extend(ChargeTurret, ChargeTurret.ChargeTurretBuild, "charge-laser-turret", {
    maxLevel: 15,
    expFields: [
        {
            type: "linear",
            field: "reloadTime",
            start: 60,
            intensity: -2
        },
        {
            type: "bool",
            field: "targetAir",
            start: false,
            intensity: 5
        }
    ]
}, {
    shoot(ammo){
        this.useAmmo();

        chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
        chargeLaserTurret.chargeBeginEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation, Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, this.totalLevel() / 10));
        chargeLaserTurret.chargeSound.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, 1);

        for(var i = 0; i < chargeLaserTurret.chargeEffects; i++){
            Time.run(Mathf.random(chargeLaserTurret.chargeMaxDelay), () => {
                if(!this.isValid()) return;
                chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
                chargeLaserTurret.chargeEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation, Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, this.totalLevel() / 10));
            });
        }

        this.shooting = true;

        Time.run(chargeLaserTurret.chargeTime, () => {
            if(!this.isValid()) return;
            chargeLaserTurret.tr.trns(this.rotation, chargeLaserTurret.size * Vars.tilesize / 2);
            this.recoil = chargeLaserTurret.recoilAmount;
            this.heat = 1;
            this.bullet(ammo, this.rotation + Mathf.range(chargeLaserTurret.inaccuracy));
            this.effects();
            this.shooting = false;
        });
    },
    effects(){
        var fshootEffect = chargeLaserTurret.shootEffect == Fx.none ? this.peekAmmo().shootEffect : chargeLaserTurret.shootEffect;
        var fsmokeEffect = chargeLaserTurret.smokeEffect == Fx.none ? this.peekAmmo().smokeEffect : chargeLaserTurret.smokeEffect;
        fshootEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation, Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, this.totalLevel() / 10));
        fsmokeEffect.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, this.rotation);
        chargeLaserTurret.shootSound.at(this.x + chargeLaserTurret.tr.x, this.y + chargeLaserTurret.tr.y, Mathf.random(0.9, 1.1));

        if(chargeLaserTurret.shootShake > 0){
            Effect.shake(chargeLaserTurret.shootShake, chargeLaserTurret.shootShake, this);
        }

        this.recoil = chargeLaserTurret.recoilAmount;
    }
});

chargeLaserTurret.range = 155;
chargeLaserTurret.chargeTime = 50;
chargeLaserTurret.chargeMaxDelay = 30;
chargeLaserTurret.chargeEffects = 7;
chargeLaserTurret.recoilAmount = 2;
chargeLaserTurret.cooldown = 0.03;
chargeLaserTurret.powerUse = 6;
chargeLaserTurret.shootShake = 2;
chargeLaserTurret.shootEffect = laserChargeShoot;
chargeLaserTurret.smokeEffect = Fx.none;
chargeLaserTurret.chargeEffect = laserCharge;
chargeLaserTurret.chargeBeginEffect = laserChargeBegin;
chargeLaserTurret.heatColor = Color.red;
chargeLaserTurret.shootSound = Sounds.laser;
chargeLaserTurret.shootType = chargeLaser;
chargeLaserTurret.buildVisibility = BuildVisibility.sandboxOnly;
