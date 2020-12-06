const lightningb = new JavaAdapter(BulletType, {
    range(){
      return 70;
    },
    draw(b){},
    init(b){
        if(b == null) return;
        Lightning.create(b.team, Pal.lancerLaser, this.damage, b.x, b.y, b.rotation(), 30);
    }
}, 0.001, 12);
//lightningb.speed = 0.001;
//lightningb.damage = 12;
lightningb.lifetime = 1;
lightningb.shootEffect = Fx.hitLancer;
lightningb.smokeEffect = Fx.none;
lightningb.despawnEffect = Fx.none;
lightningb.hitEffect = Fx.hitLancer;
lightningb.keepVelocity = false;

const bufferWep = new Weapon("buffer-shockgun");
bufferWep.shake = 2;
bufferWep.y = 1;
bufferWep.reload = 55;
bufferWep.shotDelay = 3;
bufferWep.alternate = true;
bufferWep.shots = 2;
bufferWep.inaccuracy = 0;
bufferWep.ejectEffect = Fx.none;
bufferWep.bullet = lightningb;
bufferWep.shootSound = Sounds.spark;

const buffer = extendContent(UnitType, "buffer", {
    landed(u){
        Effect.shake(1, 1, u);
        Fx.landShock.at(u);
        for(var i = 0; i < 8; i++){
            Time.run(Mathf.random(8), () => Lightning.create(u.team, Pal.lancerLaser, 17, u.x, u.y, Mathf.random(360), 14));
        }
    }
});
buffer.mineTier = 1;
buffer.speed = 0.75;
buffer.boostMultiplier = 1.26;
buffer.itemCapacity = 15;
buffer.health = 150;
buffer.buildSpeed = 0.9;
buffer.engineColor = Color.valueOf("d3ddff");
buffer.weapons.add(bufferWep);
buffer.constructor = () => extend(MechUnit, {});
