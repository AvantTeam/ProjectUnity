//TODO finish it 

const plasmaBeam = extend(ContinuousLaserBulletType, {});
plasmaBeam.damage = 60;
plasmaBeam.colors = [Pal.surge, Color.valueOf("d89e6b"), Color.valueOf("f2e87b"), Color.white];
plasmaBeam.strokes = [1.2, 1, 0.7, 0.3];

const plasma = extendContent(LaserTurret, "plasma", {});
plasma.shootType = plasmaBeam;
plasma.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();