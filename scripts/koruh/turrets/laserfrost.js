const lib = this.global.unity.exp;
const freezeSound = loadSound("laser-freeze");

const disabled = new StatusEffect("disabled");
disabled.reloadMultiplier = 0.000001;
disabled.speedMultiplier = 0.001;

function snowflake(x, y, rot, size){
  Lines.lineAngleCenter(x, y, 0 + rot, size);
  Lines.lineAngleCenter(x, y, 60 + rot, size);
  Lines.lineAngleCenter(x, y, 120 + rot, size);
}

const freezeEffect = new Effect(30, e => {
  Draw.color(Color.white, e.color, e.fin());
  Lines.stroke(e.fout() * 2);
  Lines.poly(e.x, e.y, 6, 4 + e.rotation * 1.5 * e.finpow(), Mathf.randomSeed(e.id)*360);
  Draw.color();
  var i = 0;
  Angles.randLenVectors(e.id, 5, e.rotation * 1.6 * e.fin() + 16, e.fin() * 33, 360, (x, y) => {
    snowflake(e.x + x, e.y + y, e.finpow() * 60, Mathf.randomSeed(e.id + i) * 2 + 2);
    i++;
  });
  Angles.randLenVectors(e.id + 1, 3, e.rotation * 2.1 * e.fin() + 7, e.fin() * -19, 360, (x, y) => {
    snowflake(e.x + x, e.y + y, e.finpow() * 60, Mathf.randomSeed(e.id + i) * 2 + 2);
    i++;
  });
});

const shootFlake = new Effect(21, e => {
    Draw.color(e.color, Color.white, e.fout());

    for(var i=0; i<6; i++){
        Drawf.tri(e.x, e.y, 2.5 * e.fout(), 29, e.rotation + 60 * i);
    }
});

const laser = extend(BulletType, {
    getDamage(b){
        return this.damage + (b.owner.totalLevel() * 4);
    },

    getColor(b){
        return Tmp.c1.set(Liquids.cryofluid.color).lerp(Color.cyan, b.owner.totalLevel() / 15);
    },

    freezepos(b, x, y){
        var lvl =  b.owner.totalLevel();
        if(!Vars.headless) freezeEffect.at(x, y, lvl / 2 + 10, Tmp.c1.set(Liquids.cryofluid.color).lerp(Color.cyan, lvl / 15));
        if(!Vars.headless) freezeSound.at(x, y, 1, 0.6);

        Damage.status(b.team, x, y, 10 + lvl / 2, this.status, 60 + lvl * 60, true, true);
        Damage.status(b.team, x, y, 10 + lvl / 2, disabled, 10 * lvl, true, true);
    },

    collision(other, x, y){
        this.hit(this.base(), x, y);
        if(other instanceof Healthc){
            var h = other;
            h.damage(this.getDamage(this));
        }else if(other instanceof Unit) {
            var unit = other;
            unit.impulse(Tmp.v3.set(unit).sub(this.x, this.y).nor().scl(this.knockback * 80.0));
            var lvl =  b.owner.totalLevel();
            unit.apply(this.status, this.statusDuration + lvl * 60);
            unit.apply(disabled, 20 + 10 * lvl);
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
            this.freezepos(b, hit.x, hit.y);
            b.owner.incExp(2);
        }
        else if(target instanceof Building){
            var tile = target;

            if(tile.collide(b)){
                tile.collision(b);
                this.hit(b, tile.x, tile.y);
                this.freezepos(b, tile.x, tile.y);
                b.owner.incExp(2);
            }
        }
        else{
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
            Lines.stroke(b.fout() * 1.5);
            Lines.circle(Tmp.v1.x, Tmp.v1.y, b.finpow() * 8);
            Draw.alpha(0.4);
            //this looks horrible without bloom
            //Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
            Lines.stroke(b.fout()*4);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

            Draw.alpha(1);
            Lines.stroke(b.fout()*2.5);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

            Draw.color(Color.white);
            Lines.stroke(b.fout()*1.6);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
            Draw.reset();

            Drawf.light(Team.derelict, b.x, b.y, Tmp.v1.x, Tmp.v1.y, 15 * b.fout() + 5, Color.white, 0.6);
        }
    }
});
laser.damage = 130;
laser.lifetime = 18;
laser.speed = 0.0001;
laser.despawnEffect = Fx.none;
laser.pierce = true;
laser.hitSize = 0;
laser.status = StatusEffects.freezing;
laser.statusDuration = 3 * 60;
laser.width = 0.7;
laser.length = 170;
laser.hittable = false;
laser.hitEffect = Fx.hitLiquid;
laser.shootEffect = shootFlake;
laser.activeSound = Sounds.none;
laser.shootSound = Sounds.splash;

const laserTurret = lib.extend(LiquidTurret, LiquidTurret.LiquidTurretBuild, "frost-laser-turret", {
    maxLevel: 15,
    expFields: [
    ],
    init(){
        this.super$init();
        //this.consumes.powerCond(10, build => build.isActive());
    }
}, {
    acceptLiquid(source, liquid){
        return laserTurret.ammoTypes.get(liquid) != null;
    }
});
//laserTurret.shootType = laser;
laserTurret.ammo(Liquids.cryofluid, laser);
laserTurret.buildVisibility = BuildVisibility.sandboxOnly;
