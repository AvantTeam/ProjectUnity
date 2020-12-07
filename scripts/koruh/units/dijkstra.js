//TODO: need to redo all weapons and stuff
const alib = this.global.unity.abilitylib;

const laserZap = new JavaAdapter(LaserBulletType, {}, 90);
laserZap.damage = 90;
laserZap.sideAngle = 15;
laserZap.sideWidth = 1.5;
laserZap.sideLength = 60;
laserZap.width = 25;
laserZap.length = 180;
laserZap.shootEffect = Fx.shockwave;
laserZap.colors = [Pal.lancerLaser.cpy().mul(1, 1, 1, 0.7), Pal.lancerLaser, Color.white]
/*
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
missileJavelin.weaveMag = 2;*/

const plasmaedEffect = new Effect(50, e => {
    Draw.color(Pal.lancerLaser, Color.white.cpy().mul(0.25, 0.25, 1, e.fout()), e.fout() / 6 + Mathf.randomSeedRange(e.id, 0.1));

    Fill.square(e.x, e.y, e.fslope() * 2, 45);
});

const plasmaed = new JavaAdapter(StatusEffect, {}, "plasmaed2");
plasmaed.effectChance = 0.15;
plasmaed.damage = 0.5;
plasmaed.reloadMultiplier = 0.8;
plasmaed.healthMultiplier = 0.9;
plasmaed.damageMultiplier = 0.8;
plasmaed.effect = plasmaedEffect;

const plasmaBullet = new JavaAdapter(BasicBulletType, {
    init(b){
        if(typeof(b) !== "undefined"){
            b.data = new Trail(9);
        };
    },

    draw(b){
        b.data.draw(this.frontColor, this.width);

        Draw.color(this.frontColor);
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
plasmaBullet.frontColor = Pal.lancerLaser.cpy().lerp(Color.white, 0.5);
plasmaBullet.backColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5).mul(0.7);
plasmaBullet.width = plasmaBullet.height = 2;
plasmaBullet.weaveScale = 0.6;
plasmaBullet.weaveMag = 0.5;
plasmaBullet.homingPower = 0.4;
plasmaBullet.lifetime = 80;
plasmaBullet.shootEffect = Fx.hitLancer;
plasmaBullet.hitEffect = plasmaBullet.despawnEffect = Fx.hitLancer;
plasmaBullet.pierceCap = 10;
plasmaBullet.pierceBuilding = true;
plasmaBullet.splashDamageRadius = 4;
plasmaBullet.splashDamage = 4;
plasmaBullet.status = plasmaed;
plasmaBullet.statusDuration = 180;
plasmaBullet.inaccuracy = 25;

const laserWeap = new Weapon("dijkstra-laser");

laserWeap.rotate = true;
laserWeap.rotateSpeed = 8;
laserWeap.occlusion = 20;
laserWeap.x = 0;
laserWeap.y = 0;
laserWeap.reload = 150;
laserWeap.shots = 1;
laserWeap.alternate = false;
laserWeap.ejectEffect = Fx.none;
laserWeap.bullet = laserZap;
laserWeap.shootSound = Sounds.laser;
laserWeap.mirror = false;


const plasmaWeap = new Weapon("dijkstra-plasmagun");

plasmaWeap.x = 0;
plasmaWeap.y = 0;
plasmaWeap.reload = 7;
plasmaWeap.shots = 1;
plasmaWeap.alternate = true;
plasmaWeap.ejectEffect = Fx.none;
plasmaWeap.velocityRnd = 1.5;
plasmaWeap.spacing = 15;
plasmaWeap.inaccuracy = 20;

plasmaWeap.bullet = plasmaBullet;
plasmaWeap.shootSound = Sounds.spark;

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
dijkstra.weapons.add(laserWeap);
dijkstra.weapons.add(plasmaWeap);

const slasheffect = new Effect(90, e => {
    Draw.color(Pal.lancerLaser);
    Drawf.tri(e.x, e.y, 4 * e.fout(), 45, (e.id*57 + 90)%360);
    Drawf.tri(e.x, e.y, 4 * e.fout(), 45, (e.id*57 - 90)%360);
});
const boostedskill = new StatusEffect("boostedskill");
boostedskill.color = Pal.lancerLaser;
boostedskill.effect = Fx.none;
boostedskill.speedMultiplier = 2;
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
      aiUse: true,
      aiShouldUse(u){
        return Units.closestEnemy(u.team ,u.x ,u.y ,20 * Vars.tilesize, boolf(tu => true)) != null;
      },

      used(u){
        var target = Units.closestEnemy(u.team ,u.x ,u.y, 35 * Vars.tilesize, boolf(tu => true));
        var dir = u.rotation;
        if(target) dir = Tmp.v1.set(target.x - u.x,target.y - u.y).angle();
        Fx.lightningShoot.at(u.x, u.y, dir);

        var b = lightningsk.create(u, u.team, u.x, u.y, dir, -1, 1, 1, null);
        Damage.collideLine(b, u.team, slasheffect, u.x, u.y, dir, 16 * Vars.tilesize, true);
        u.apply(boostedskill, 30);
        var posnew = Tmp.v1.set(18 * Vars.tilesize, 0).setAngle(dir);
        /*if(!Vars.net.client()){
          u.set(posnew.x + u.x, posnew.y + u.y);
          u.snapInterpolation();
          if(u.isPlayer()) u.getPlayer().snapSync();
        }*/

        //u.set(posnew.x + u.x, posnew.y + u.y);//for good measure
        /*Core.app.post(() => {
          if(!Vars.headless && u.getPlayer() == Vars.player) Core.camera.position.set(Vars.player);
        });*/

        if(Vars.mobile && !Vars.headless && u.getPlayer() == Vars.player){
          Core.camera.position.set(posnew.x + u.x, posnew.y + u.y);
        }

        if(!Vars.headless){
          Sounds.spark.at(posnew.x + u.x, posnew.y + u.y, 1.6);
          Fx.lancerLaserShootSmoke.at(posnew.x + u.x, posnew.y + u.y, (dir + 180) % 360);
        }
        Time.run(15, () => {
          if(u != null && u.isValid() && !u.dead) u.vel.trns(dir, 3);
        });

        u.vel.trns(dir, 21);
      }
    }
  ], {}, true);
dijkstra.setTypeID(classid);
