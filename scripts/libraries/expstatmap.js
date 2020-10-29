var statMap = {
  //camPAIN
  "launchTime": [Stat.launchTime, (a) => (a/60)],
  //defense
  "breakage": [Stat.shieldHealth, (a) => (a)],
  //"cooldownBrokenBase": This should be an exception: stats.add(Stat.cooldownTime, (int) (breakage / cooldownBrokenBase / 60f), StatUnit.seconds);
  "basePowerDraw": [Stat.powerUse, (a) => (a*60)],
  //stats.add(Stat.repairTime, (int)(100f / healPercent * reload / 60f), StatUnit.seconds);
  //"range": [Stat.range, (a) => (a/Vars.tilesize)], just ignore
  "speedBoost": [Stat.speedIncrease, (a) => (a*100)],
  "useTime": [Stat.productionTime, (a) => (a/60)],
  "chanceDeflect": [Stat.baseDeflectChance, (a) => (a)],
  "lightningChance": [Stat.lightningChance, (a) => (a*100)],
  //turrets
  //stats.add(Stat.reload, 60f / reloadTime * shots, StatUnit.none);
  "targetAir": [Stat.targetsAir, (a) => (a)],
  "targetGround": [Stat.targetsGround, (a) => (a)],
  "range": [Stat.shootRange, (a) => (a/Vars.tilesize)]
  //"reloadTime": []
}
//I give up
