const steleBullet = extend(BasicBulletType, {
    init(b){
        if(typeof(b) !== "undefined"){
            b.data = new Trail(6);
        };
    },

    draw(b){
        b.data.draw(this.frontColor, this.width);

        Draw.color(this.frontColor);
        Fill.circle(b.x, b.y, this.width)
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
});
steleBullet.frontColor = Pal.lancerLaser;
steleBullet.backColor = Pal.lancerLaser.cpy().mul(0.7);
steleBullet.width = steleBullet.height = 2;
steleBullet.weaveScale = 3;
steleBullet.weaveMag = 5;
steleBullet.homingPower = 1;
steleBullet.speed = 3.5;
steleBullet.lifetime = 60;
steleBullet.damage = 3;
steleBullet.shootEffect = Fx.hitLancer;

const steleWeap = extendContent(Weapon, "unity-stele-shotgun", {});
steleWeap.reload = 60;
steleWeap.recoil = 2.5;
steleWeap.x = 5.25;
steleWeap.y = -0.25;
steleWeap.shots = 12;
steleWeap.spacing = 0.5;
steleWeap.inaccuracy = 0.5;
steleWeap.velocityRnd = 0.2
steleWeap.shotDelay = 0;
steleWeap.shootSound = Sounds.shootBig;
steleWeap.bullet = steleBullet;

const stele = extendContent(UnitType, "stele", {});
stele.weapons.add(steleWeap);
stele.constructor = () => extend(MechUnit, {});
