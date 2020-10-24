const rocket = new MissileBulletType()
rocket.damage = 100;
rocket.speed = 3.4;
rocket.width = 12;
rocket.height = 12;
rocket.splashDamage = 50;
rocket.splashDamageRadius = 40;
rocket.backColor = Color.valueOf("f53036");
rocket.frontColor = Color.valueOf("ff786e");


const main_cannon = new Weapon("project-unity-hovos-weapon");

main_cannon.reload = 120;
main_cannon.x = 0;
main_cannon.y = 0;
main_cannon.mirror = false;
main_cannon.rotate = true;
main_cannon.shake = 3;
main_cannon.rotateSpeed = 2;
main_cannon.bullet = rocket;

const hovos = this.global.unity.hoverbase.extend(LegsUnit, "hovos");

hovos.weapons.add(main_cannon);

hovos.speed = 3;
hovos.drag = 0.07;
hovos.accel = 0.03;
hovos.health = 340;
hovos.hitSize = 17;
hovos.range = 210;
