
const laser = extend(BulletType, {
    getDamage(b){
        return this.damage;
    },
    getColor(b){
        return Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, 0.5);
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
        }else if(target instanceof Building){
            var tile = target;

            if(tile.collide(b)){
                tile.collision(b);
                this.hit(b, tile.x, tile.y);
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

            Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15 * b.fout() + 5, this.getColor(b), 0.6);
        }
    }
});
laser.damage = 35;
laser.lifetime = 22;
laser.speed = 0.0001;
laser.despawnEffect = Fx.none;
laser.pierce = true;
laser.hitSize = 0;
laser.status = StatusEffects.shocked;
laser.statusDuration = 5 * 60;
laser.width = 0.7;
laser.length = 170;
laser.hittable = false;
laser.hitEffect = Fx.hitLiquid;

const laserWeapon = new Weapon("unity-laser-weapon");

laserWeapon.reload = 5;
laserWeapon.x = 6;
laserWeapon.y = -3;
laserWeapon.rotate = true;
laserWeapon.shake = 1;
laserWeapon.rotateSpeed = 6;
laserWeapon.bullet = laser;

var transformTime = 30;

const craber = extendContent(UnitType, "craber-naval", {
    load(){
        this.super$load();
        this.region = Core.atlas.find(this.name);
    }
});

craber.constructor = () => {
    //var time = transformTime;
    var unit = extend(CommanderUnitWaterMove, {
        setTransTimeC(a){
            this._timeTrnsC = a;
        },
        update(){
            this.super$update();
            if(!(this.floorOn().isLiquid) || (this.floorOn() instanceof ShallowLiquid)){
                
                if(this._timeTrnsC < 0 || this._timeTrnsC > transformTime){
                    var groundUnit = craberGround.create(this.team);
                    groundUnit.set(this.x, this.y);
                    groundUnit.rotation = this.rotation;
                    groundUnit.add();
                    groundUnit.vel.set(this.vel);
                    if(this.isPlayer()){
                        //groundUnit.controller(this.controller);
                        groundUnit.controller = this.controller;
                        if(groundUnit.controller.unit() != groundUnit.base()) groundUnit.controller.unit(groundUnit.base());
                    };
                    this.remove();
                }else{
                    this._timeTrnsC -= Time.delta;
                }
            }
        }
    });
    unit.setTransTimeC(transformTime);

    return unit;
};

craber.weapons.add(laserWeapon);

const craberGround = extendContent(UnitType, "craber", {
    load(){
        this.super$load();
        this.region = Core.atlas.find(this.name);
    }
});

craberGround.constructor = () => {
    //var time = transformTime;
    var unit = extend(LegsUnit, {
        setTransTimeC(a){
            this._timeTrnsC = a;
        },
        update(){
            this.super$update();
            if(this.floorOn().isLiquid && !(this.floorOn() instanceof ShallowLiquid)){
                if(this._timeTrnsC < 0 || this._timeTrnsC > transformTime){
                    var navalUnit = craber.create(this.team);
                    navalUnit.set(this.x, this.y);
                    navalUnit.rotation = this.rotation;
                    navalUnit.add();
                    navalUnit.vel.set(this.vel);
                    if(this.isPlayer()){
                        //navalUnit.controller(this.controller);
                        navalUnit.controller = this.controller;
                        if(navalUnit.controller.unit() != navalUnit.base()) navalUnit.controller.unit(navalUnit.base());
                    };
                    this.remove();
                }else{
                    this._timeTrnsC -= Time.delta;
                }
            }
        }
    });
    unit.setTransTimeC(transformTime);

    return unit;
};

craberGround.weapons.add(laserWeapon);
