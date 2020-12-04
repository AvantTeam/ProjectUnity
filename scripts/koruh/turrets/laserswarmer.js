const lib = this.global.unity.exp;

const laserCharge = new Effect(38, e => {
    Draw.color(e.color);
    Angles.randLenVectors(e.id, e.id % 3 + 1, 1 + 20 * e.fout(), e.rotation, 120, (x, y) => {
        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
    });
});

const laserChargeBegin = new Effect(60, e => {
    Draw.color(e.color);
    Fill.circle(e.x, e.y, e.fin() * 5);

    Draw.color();
    Fill.circle(e.x, e.y, e.fin() * 3.5);
});

const laserChargeShoot = new Effect(21, e => {
    Draw.color(e.color, Color.white, e.fout());

    for(var i=0; i<4; i++){
        Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 90 * i + e.finpow() * 112);
    }
});

const fragHit = new Effect(8, e => {
    Draw.color(Color.white, Pal.lancerLaser, e.fin());

    Lines.stroke(0.5 + e.fout());
    Lines.circle(e.x, e.y, e.fin() * 5);

    Lines.stroke(e.fout());
    Lines.circle(e.x, e.y, e.fin() * 6);
});

const servBombs = new Effect(40, e => {
    Angles.randLenVectors(e.id + 1, 12, 1 + 29 * e.finpow(), (vx, vy) => {

        Draw.color(Liquids.cryofluid.color);

        Lines.stroke(e.fout() * 1);
        e.scaled(e.lifetime * 9 / 8, g1 => Lines.circle(e.x + vx, e.y + vy, g1.fout() * 6));

        for(var i = 0; i < 4; i++){
            Drawf.tri(e.x + vx, e.y + vy, 6, 100 * e.fout() * 0.1 * e.fslope(), i * 90);
        };

        Draw.color();
        for(var i = 0; i < 4; i++){
            Drawf.tri(e.x + vx, e.y + vy, 3, 35 * e.fout() * 0.1 * e.fslope(), i * 90);
        };
    });
});

const plasmaedEffect = new Effect(50, e => { //TOO MANY LIST DEFINED LOL
    Draw.color(Liquids.cryofluid.color, Color.white.cpy().mul(0.25, 0.25, 1, e.fout()), e.fout() / 6 + Mathf.randomSeedRange(e.id, 0.1));

    Fill.square(e.x, e.y, e.fslope() * 2, 45);
});

const plasmaed = new JavaAdapter(StatusEffect, {}, "plasmaed");
plasmaed.effectChange = 0.15;
plasmaed.damage = 0.5;
plasmaed.reloadMultiplier = 0.8;
plasmaed.healthMultiplier = 0.9;
plasmaed.damageMultiplier = 0.8;
plasmaed.effect = plasmaedEffect;

const chargeLaser = extend(BulletType, {
    getDamage(b){
        return this.damage + (b.owner.totalLevel() * 7);
    },

    getColor(b){
        return Tmp.c1.set(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5)).lerp(Pal.sapBullet, b.owner.totalLevel() / 30);
    },

    getRealLength(b){
        return 140 + (b.owner.totalLevel() * 2);
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

        var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.getRealLength(b));
        b.data = target;

        if(target instanceof Hitboxc){
            var hit = target;

            hit.collision(b, hit.x, hit.y);
            b.collision(hit, hit.x, hit.y);
            b.owner.incExp(1);
        }
        else if(target instanceof Building){
            var tile = target;

            if(tile.collide(b)){
                tile.collision(b);
                this.hit(b, tile.x, tile.y);
                b.owner.incExp(1);
            }
        }
        else{
            b.data = new Vec2().trns(b.rotation(), this.getRealLength(b)).add(b.x, b.y);
        }
        this.makeFrag(b.data.x, b.data.y, b);
    },

    makeFrag(x, y, b){
        for(var i=0; i<this.fragBullets; i++){
            this.fragBullet.create(b, x, y, b.rotation() + i * 120);
        }
    },

    range(){
        return this.length;
    },

    draw(b){
        if(b.data instanceof Position){
            var sizemulti = 0.25;
            var lifetime = 40;
            var data = b.data;
            var bcolor = this.getColor(b);
            Tmp.v1.set(data);

            //Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
            Lines.stroke(b.fout() * 2);

            Draw.color(bcolor);
            /*var h = new Effect(lifetime * 9 / 4, b1 => {
                Lines.stroke(b.fout() * 2);
                Draw.color(bcolor);
                Lines.circle(b1.x, b1.y, (4 + b.finpow() * 65) * sizemulti)
            });
            h.at(Tmp.v1.x, Tmp.v1.y);*/
            //이 미친 짓을 다시 하면 님 남은 수명을 미분해버릴 테니 그리 아쇼;
            Lines.circle(Tmp.v1.x, Tmp.v1.y, (4 + b.finpow() * 65) * sizemulti);

            for(var i = 0; i < 4; i++){
                Drawf.tri(Tmp.v1.x, Tmp.v1.y, 6, 100 * b.fout() * sizemulti, i * 90);
            };

            Draw.color();
            for(var i = 0; i < 4; i++){
                Drawf.tri(Tmp.v1.x, Tmp.v1.y, 3, 35 * b.fout() * sizemulti, i * 90);
            };

            Draw.alpha(0.4);
            Lines.stroke(b.fout()*4);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

            Draw.alpha(1);
            Lines.stroke(b.fout()*2.6);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

            Draw.color(Color.white);
            Lines.stroke(b.fout()*1.5);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
            Draw.reset();

            Drawf.light(Team.derelict, b.x, b.y, Tmp.v1.x, Tmp.v1.y, 15 * b.fout() + 5, Color.white, 0.6);
        }
    },
    //h

    hit(b){
        this.hitEffect.at(b.x, b.y, b.rotation(), this.hitColor);
        this.hitSound.at(b);

        Effect.shake(this.hitShake, this.hitShake, b);/*

        var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.getRealLength(b));//제에ㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔ발 밖으로 뺄 수 있는건 밖으로.
        b.data = target;

        for(var i = 0; i < this.fragBullets; i++){
            var len = Mathf.random(1, 7);
            var a = b.rotation() + i * 120;


            if(target instanceof Hitboxc){
                var hit = target;
                this.fragBullet.create(b, hit.x + Angles.trnsx(a, len), hit.y + Angles.trnsy(a, len), a);

            }
            else if(target instanceof Building){
                var tile = target;
                if(tile.collide(b)){
                    this.fragBullet.create(b, tile.x + Angles.trnsx(a, len), tile.y + Angles.trnsy(a, len), a);
                }
            }
            else{
                b.data = new Vec2().trns(b.rotation(), this.getRealLength(b)).add(b.x, b.y);
                var bdata = b.data;
                Tmp.v1.set(bdata);
                this.fragBullet.create(b, Tmp.v1.x + Angles.trnsx(a, len), Tmp.v1.y + Angles.trnsy(a, len), a);
            }
        }*/
    }
});
const steleBullet = new JavaAdapter(BasicBulletType, {//i hope he won't blame me.. lol
    init(b){
        if(typeof(b) !== "undefined"){
            b.data = new Trail(6);
        };
    },
    getColor(b){
        return Tmp.c1.set(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5)).lerp(Pal.sapBullet, b.owner.totalLevel() / 30);
    },
    draw(b){
        b.data.draw(this.frontColor, this.width);

        Draw.color(this.getColor(b));
        Fill.square(b.x, b.y, this.width, b.rotation() + 45);
        Draw.color();
    },

    update(b){
        this.super$update(b);

        b.data.update(b.x, b.y);
    },

    hit(b, x, y){
        this.super$hit(b, b.x, b.y);

        b.data.clear();
    }
}, 3.5, 15);
steleBullet.frontColor = Pal.lancerLaser;
steleBullet.backColor = Pal.lancerLaser.cpy().mul(0.7);
steleBullet.width = steleBullet.height = 2;
steleBullet.weaveScale = 0.6;
steleBullet.weaveMag = 0.5;
steleBullet.homingPower = 0.4;
steleBullet.lifetime = 30;
steleBullet.shootEffect = Fx.hitLancer;
steleBullet.hitEffect = steleBullet.despawnEffect = fragHit;
steleBullet.pierceCap = 10;
steleBullet.pierceBuilding = true;
steleBullet.splashDamageRadius = 4;
steleBullet.splashDamage = 4;
steleBullet.status = plasmaed;
steleBullet.statusDuration = 180;

chargeLaser.damage = 20;
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
chargeLaser.fragBullet = steleBullet;
chargeLaser.fragBullets = 3;
chargeLaser.shootEffect = Fx.hitLiquid;

const laserSwarmerTurret = lib.extend(PowerTurret, PowerTurret.PowerTurretBuild, "swarm-laser-turret", {
    maxLevel: 30,
    expFields: [
        {
            type: "linear",
            field: "reloadTime",
            start: 90,
            intensity: -2
        },
        {
            type: "linear",
            field: "range",
            start: 20 * 8,
            intensity: 0.25 * 8
        }
    ],
    drawPlace(x, y, rotation, valid){
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 20 * 8, Pal.placing);
    },
    canBeBuilt(){
        return false;
    }
}, {
    drawSelect(){
        Drawf.dashCircle(this.x, this.y, 20 * 8, this.team.color);
        var lvl = this.totalLevel();
        if(lvl > 0) Drawf.dashCircle(this.x, this.y, 160 + 2 * lvl, laserSwarmerTurret.exp0Color);
    },
    getShootColor(lvl){
        return Tmp.c1.set(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5)).lerp(Pal.sapBullet, this.totalLevel() / 30);
    },
    shoot(ammo){
        this.useAmmo();
        var lvl = this.totalLevel();

        laserSwarmerTurret.tr.trns(this.rotation, laserSwarmerTurret.size * Vars.tilesize / 2.7);
        laserSwarmerTurret.chargeBeginEffect.at(this.x + laserSwarmerTurret.tr.x, this.y + laserSwarmerTurret.tr.y, this.rotation, this.getShootColor(lvl));
        laserSwarmerTurret.chargeSound.at(this.x + laserSwarmerTurret.tr.x, this.y + laserSwarmerTurret.tr.y, 1);

        for(var i = 0; i < laserSwarmerTurret.chargeEffects; i++){
            Time.run(Mathf.random(laserSwarmerTurret.chargeMaxDelay), () => {
                if(!this.isValid()) return;
                laserSwarmerTurret.tr.trns(this.rotation, laserSwarmerTurret.size * Vars.tilesize / 2.7);
                laserSwarmerTurret.chargeEffect.at(this.x + laserSwarmerTurret.tr.x, this.y + laserSwarmerTurret.tr.y, this.rotation, this.getShootColor(lvl));
            });
        }

        this.charging = true;
        for(var i = 0; i < laserSwarmerTurret.shots; i++){
            Time.run(laserSwarmerTurret.burstSpacing * i, () => {
                Time.run(laserSwarmerTurret.chargeTime, () => {
                    if(!this.isValid()) return;

                    this.recoil = laserSwarmerTurret.recoilAmount;

                    laserSwarmerTurret.tr.trns(this.rotation, laserSwarmerTurret.size * Vars.tilesize / 2.7, Mathf.range(laserSwarmerTurret.xRand));

                    this.heat = 1;
                    this.bullet(ammo, this.rotation + Mathf.range(laserSwarmerTurret.inaccuracy));
                    this.effects();
                    this.charging = false;
                });
            });
        }
    },
    effects(){
        var fshootEffect = laserSwarmerTurret.shootEffect == Fx.none ? this.peekAmmo().shootEffect : laserSwarmerTurret.shootEffect;
        var fsmokeEffect = laserSwarmerTurret.smokeEffect == Fx.none ? this.peekAmmo().smokeEffect : laserSwarmerTurret.smokeEffect;
        fshootEffect.at(this.x + laserSwarmerTurret.tr.x, this.y + laserSwarmerTurret.tr.y, this.rotation, this.getShootColor(this.totalLevel()));
        fsmokeEffect.at(this.x + laserSwarmerTurret.tr.x, this.y + laserSwarmerTurret.tr.y, this.rotation);
        laserSwarmerTurret.shootSound.at(this.x + laserSwarmerTurret.tr.x, this.y + laserSwarmerTurret.tr.y, Mathf.random(0.9, 1.1));

        if(laserSwarmerTurret.shootShake > 0){
            Effect.shake(laserSwarmerTurret.shootShake, laserSwarmerTurret.shootShake, this);
        }

        this.recoil = laserSwarmerTurret.recoilAmount;
    }
});

laserSwarmerTurret.chargeTime = 50;
laserSwarmerTurret.chargeMaxDelay = 30;
laserSwarmerTurret.chargeEffects = 4;
laserSwarmerTurret.recoilAmount = 2;
laserSwarmerTurret.cooldown = 0.03;
laserSwarmerTurret.powerUse = 6;
laserSwarmerTurret.shootShake = 2;
laserSwarmerTurret.shootEffect = laserChargeShoot;
laserSwarmerTurret.smokeEffect = Fx.none;
laserSwarmerTurret.chargeEffect = laserCharge;
laserSwarmerTurret.chargeBeginEffect = laserChargeBegin;
laserSwarmerTurret.heatColor = Color.red;
laserSwarmerTurret.shootSound = Sounds.laser;
laserSwarmerTurret.shootType = chargeLaser;
laserSwarmerTurret.buildVisibility = BuildVisibility.sandboxOnly;

laserSwarmerTurret.shots = 4;
laserSwarmerTurret.burstSpacing = 5;
laserSwarmerTurret.inaccuracy = 10;
laserSwarmerTurret.xRand = 6;
