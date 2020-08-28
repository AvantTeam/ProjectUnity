const orbHit = new Effect(12, e => {
  Draw.color(Pal.surge);
  Lines.stroke(e.fout() * 1.5);
  Angles.randLenVectors(e.id, 8, e.finpow() * 17, e.rotation, 360, new Floatc2({get: function(x, y){
    var ang = Mathf.angle(x, y);
    Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1);
  }}));
});

const orbShoot = new Effect(21, e => {
  Draw.color(Pal.surge);
  for(var i = 0; i < 2; i++){
    var l = Mathf.signs[i];
    Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 67 * l);
  };
});

const orbShootSmoke = new Effect(26, e => {
  Draw.color(Pal.surge);
  Angles.randLenVectors(e.id, 7, 80, e.rotation, 0, new Floatc2({get: function(x, y){
    Fill.circle(e.x + x, e.y + y, e.fout() * 4);
  }}));
});

const orbCharge = new Effect(38, e => {
  Draw.color(Pal.surge);
  Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, new Floatc2({get: function(x, y){
    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
  }}));
});

const orbChargeBegin = new Effect(71, e => {
  Draw.color(Pal.surge);
  Fill.circle(e.x, e.y, e.fin() * 3);

  Draw.color();
  Fill.circle(e.x, e.y, e.fin() * 2);
});

const orb = extend(BulletType, {
  draw(b){
    Draw.color(Pal.surge);
    Fill.circle(b.x, b.y, 4);
  },
  update(b){
    this.super$update(b);
    Units.nearbyEnemies(b.team, b.x - this.scanRadius, b.y - this.scanRadius, this.scanRadius * 2, this.scanRadius * 2, cons(unit => {
      if(b.timer.get(1, 9)){
        Lightning.create(b.team, Pal.surge, Mathf.random(18, 36), b.x, b.y, b.angleTo(unit), Mathf.random(5, 9));
      }
    }));
  }
});
orb.lifetime = 240;
orb.speed = 1.2;
orb.damage = 23;
orb.pierce = true;
orb.hittable = false;
orb.hitEffect = orbHit;
orb.scanRadius = 4 * Vars.tilesize;

const orbTurret = extendContent(ChargeTurret, "orb", {});
orbTurret.shootType = orb;
orbTurret.heatColor = Pal.surge;
orbTurret.shootEffect = orbShoot;
orbTurret.smokeEffect = orbShootSmoke;
orbTurret.chargeEffect = orbCharge;
orbTurret.chargeBeginEffect = orbChargeBegin;
