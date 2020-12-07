//TODO: need to redo all weapons and stuff
const alib = this.global.unity.abilitylib;

const missileJavelin = new MissileBulletType(5, 21, "missile");
missileJavelin.width = 8;
missileJavelin.height = 8;
missileJavelin.shrinkY = 0;
missileJavelin.drag = -0.003;
missileJavelin.keepVelocity = false;
missileJavelin.splashDamageRadius = 20;
missileJavelin.splashDamage = 1;
missileJavelin.lifetime = 60;
missileJavelin.trailColor = Color.valueOf("b6c6fd");
missileJavelin.hitEffect = Fx.blastExplosion;
missileJavelin.despawnEffect = Fx.blastExplosion;
missileJavelin.backColor = Pal.bulletYellowBack;
missileJavelin.frontColor = Pal.bulletYellow;
missileJavelin.weaveScale = 8;
missileJavelin.weaveMag = 2;

const missilesWep = new Weapon("dijkstra-missile");

missilesWep.y = 1.5;
missilesWep.reload = 90;
missilesWep.shots = 4;
missilesWep.alternate = true;
missilesWep.ejectEffect = Fx.none;
missilesWep.velocityRnd = 0.2;
missilesWep.spacing = 1;
missilesWep.bullet = missileJavelin;
missilesWep.shootSound = Sounds.missile;
const dijkstra = extendContent(UnitType, "dijkstra", {
  setTypeID(id){
    this.idType = id;
  },
  getTypeID(){
    return this.idType;
  }
});

dijkstra.mineTier = -1;
dijkstra.speed = 7.5;
dijkstra.drag = 0.01;
dijkstra.health = 560;
dijkstra.flying = true;
dijkstra.armor = 8;
dijkstra.accel = 0.01;
dijkstra.weapons.add(missilesWep);

const slasheffect = new Effect(90, e => {
    Draw.color(Pal.lancerLaser);
    Drawf.tri(e.x, e.y, 4 * e.fout(), 45, (e.id*57 + 90)%360);
    Drawf.tri(e.x, e.y, 4 * e.fout(), 45, (e.id*57 - 90)%360);
});
const boostedskill = new StatusEffect("boostedskill");
boostedskill.color = Pal.lancerLaser;
boostedskill.effect = Fx.none;
boostedskill.speedMultiplier = 1.45;
const lightningsk = new JavaAdapter(BulletType, {
    range(){
      return 21 * Vars.tilesize;
    },
    draw(b){},
    init(b){
        if(b == null) return;
        Lightning.create(b.team, Pal.lancerLaser, this.damage, b.x, b.y, b.rotation(), 27);
    }
}, 0.001, 180);
//lightningsk.speed = 0.001;
//lightningsk.damage = 12;
lightningsk.lifetime = 1;
lightningsk.shootEffect = Fx.hitLancer;
lightningsk.smokeEffect = Fx.none;
lightningsk.despawnEffect = Fx.none;
lightningsk.hitEffect = Fx.hitLancer;
lightningsk.keepVelocity = false;

var classid = alib.add(dijkstra, UnitEntity, [
    {
      type: "active",
      name: "$ability.blastcut",
      rechargeTime: 30,

      used(u){
        var target = Units.closestEnemy(u.team ,u.x ,u.y ,25 * Vars.tilesize, boolf(tu => true));
        var dir = u.rotation;
        if(target) dir = Tmp.v1.set(target.x - u.x,target.y - u.y).angle();
        Fx.lightningShoot.at(u.x, u.y, dir);

        var b = lightningsk.create(u, u.team, u.x, u.y, dir, -1, 1, 1, null);
        Damage.collideLine(b, u.team, slasheffect, u.x, u.y, dir, 7 * Vars.tilesize, false);
        u.apply(boostedskill, 1);
        var posnew = Tmp.v1.set(8 * Vars.tilesize, 0).setAngle(dir);
        u.set(posnew.x + u.x, posnew.y + u.y);
        u.snapInterpolation();
        //u.set(posnew.x + u.x, posnew.y + u.y);//for good measure
        Core.app.post(() => {
          if(!Vars.headless && u.getPlayer() == Vars.player)  Core.camera.position.set(Vars.player);
        });

        if(!Vars.headless){
          Sounds.spark.at(u.x, u.y, 1.6);
          Fx.lancerLaserShootSmoke.at(u.x, u.y, (dir + 180) % 360);
        }

        u.vel.trns(dir, 4);
      }
    }
  ], {}, true);
dijkstra.setTypeID(classid);
