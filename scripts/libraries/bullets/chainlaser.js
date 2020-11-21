module.exports = {
  chainLaser(color, damage, length, width, status, statusDuration, chain, nextChain){
    var laser = extend(BulletType, {
      draw(b){
        if(b.data instanceof Position){
          var data = b.data;
          Tmp.v1.set(data);

          Draw.color(this.color);
          Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
          Draw.reset();

          Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15 * b.fout(), this.lightColor, 0.6);
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
        }
        else if(target instanceof Building){
          var tile = target;

          if(tile.collide(b)){
            tile.collision(b);
            this.hit(b, tile.x, tile.y);
          }
        }else{
          b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
        }
      },
      hit(b, x, y){
        if(chain == true){
          var nearby = Units.closestEnemy(b.team, b.x, b.y, 3 * Vars.tilesize, boolf(nearby => !nearby.dead));
          if(nearby != null) Call.createBullet(nextChain, b.team, b.x, b.y, b.angleTo(nearby), nextChain.damage, nextChain.speed, nextChain.lifetime);
        }
        this.super$hit(b, x, y);
      }
    });
    laser.damage = damage;
    laser.speed = 0.0001;
    laser.despawnEffect = Fx.none;
    laser.pierce = true;
    laser.hitSize = 0;
    laser.status = status;
    laser.statusDuration = statusDuration;
    laser.width = width;
    laser.length = length;
    laser.color = color;
    laser.hittable = false;
    laser.hitEffect = Fx.hitLiquid;
    return laser;
  }
}
