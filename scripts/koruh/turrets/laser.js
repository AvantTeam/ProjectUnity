const lib = this.global.unity.exp;

const laser = extend(BulletType, {
    getDamage(b){
        return this.damage + (b.owner.totalLevel() * 10);
    },

    collision(other, x, y){
        this.hit(this.base(), x, y);
        if(other instanceof Healthc){
            var h = other;
            h.damage(this.getDamage(this));
        }else if(other instanceof Unit) {
            unit = other;
            unit.impulse(Tmp.v3.set(unit).sub(this.x, this.y).nor().scl(this.knockback * 80.0));
            unit.apply(this.status, this.statusDuration);
        }if(!this.pierce){
         remove();
        }else{
            this.collided.add(other.id());
        }
    },

    init(b){
        if(typeof(b) === "undefined") return;
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
        var target = b.owner.target;
        if(target == null) return;

        Draw.color(this.color);
        Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, target.x, target.y, this.width * b.fout());
        Draw.reset();

        Drawf.light(b.team, b.x, b.y, b.x + target.x, b.y + target.y, 15 * b.fout(), this.lightColor, 0.6);
    }
});
laser.damage = 30;
laser.lifetime = 18;
laser.speed = 0.0001;
laser.despawnEffect = Fx.none;
laser.pierce = true;
laser.hitSize = 0;
laser.status = StatusEffects.shocked;
laser.statusDuration = 3 * 60;
laser.width = 0.7;
laser.length = 150;
laser.color = Color.white.cpy();
laser.hittable = false;
laser.hitEffect = Fx.hitLiquid;

const laserTurret = lib.extend(PowerTurret, PowerTurret.PowerTurretBuild, "laser-turret", {
    maxLevel: 15,
    expFields: [{
        type: "exp",
        field: "reloadTime",
        start: 30,
        intensity: -2
    }]
}, {});
laserTurret.shootType = laser;
