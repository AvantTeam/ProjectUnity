const expColor = Color.valueOf("84ff00");
const expAmount = 10;

const d4x = [1, 0, -1, 0], d4y = [0, 1, 0, -1];

const expAbsorb = new Effect(15, e => {
  Lines.stroke(e.fout() * 1.5);
  Draw.color(expColor);
  Lines.circle(e.x, e.y, e.fin() * 2.5 + 1);
});

const expDespawn = new Effect(15, e => {
  Draw.color(expColor);
  Angles.randLenVectors(e.id, 7, 2 + 5 * e.fin(), (x, y) => {
      Fill.circle(e.x + x, e.y + y, e.fout());
  });
});

const exporb = extend(BulletType, {
    init(b){
        if(!b) return;
        this.super$init(b);
    },
    draw(b){
        if((b.fin() > 0.5) && Time.time() % 14 < 7) return;//blinking
        Draw.color(expColor, Color.yellow, 0.5 + 0.5 * Mathf.sin(Time.time() * 0.03 + b.id * 2));
        Fill.circle(b.x, b.y, 1.5);
        Lines.stroke(0.5);
        Lines.circle(b.x, b.y, 2.2 + 0.6 * Mathf.sin(Time.time() * 0.1 + b.id * 5));
    },
    update(b){
        if(b.moving()) b.time = 0;//if this is idle it dies
        var tile = Vars.world.tileWorld(b.x, b.y);
        if(tile == null || tile.build == null) return;

        if(tile.block().hasExp && tile.build.consumesOrb()){
            tile.build.incExp(expAmount * tile.build.getOrbMuitiplier());
            expAbsorb.at(b.x, b.y);
            b.remove();
        }
        else if(tile.solid()){
            b.trns(-1.1 * b.vel.x, -1.1 * b.vel.y);
            b.vel.scl(0, 0);
        }
        else if(tile.block() instanceof Conveyor) this.conveyor(b, tile.block(), tile.build);

    },
    conveyor(b, block, build){
        if(build.clogHeat > 0.5 || !build.enabled) return;
        var mspeed = block.speed / 3;
        b.vel.add(d4x[build.rotation] * mspeed * build.delta(), d4y[build.rotation] * mspeed * build.delta());
    }
});

exporb.damage = 0;
exporb.drag = 0.05;
exporb.lifetime = 180; //idle lifetime
exporb.speed = 0.0001;
exporb.despawnEffect = expDespawn;
exporb.pierce = true;
exporb.hitSize = 2;
exporb.hittable = false;
exporb.collides = false;
exporb.collidesTiles = false;
exporb.collidesAir = false;
exporb.collidesGround = false;
exporb.keepVelocity = false;
exporb.absorbable = false;
exporb.lightColor = expColor;
exporb.hitEffect = Fx.none;
exporb.shootEffect = Fx.none;

module.exports = {
    exporb: exporb,
    hporb: null,
    exp: this.exporb,
    hp: this.hporb,
    createExp(x, y, amount, r){
        if(!Vars.net.client()){
            if(r == undefined) r = 4;
            var n = Mathf.floorPositive(amount / expAmount);
            for(var i=0; i<n; i++){
                this.exporb.createNet(Team.derelict, x - r + Mathf.random() * 2 * r, y - r + Mathf.random() * 2 * r, 0, 0, 1, 1);
            }
        }
    },
    spreadExp(x, y, amount, v){
        if(!Vars.net.client()){
            if(v == undefined) v = 4;
            v *= 1000;
            var n = Mathf.floorPositive(amount / expAmount);
            for(var i=0; i<n; i++){
                this.exporb.createNet(Team.derelict, x, y, Mathf.random() * 360, 0, v, 1);
            }
        }
    },
    spewExp(x, y, n, r, v){
        if(!Vars.net.client()){
            if(v == undefined) v = 8;
            v *= 1000;
            for(var i=0; i<n; i++){
                this.exporb.createNet(Team.derelict, x, y, r - 5 + 10 * Mathf.random(), 0, v, 1);
            }
        }
    }
};
