const wlib = this.global.unity.limitwall;

const stonewall = wlib.extend(Wall, Wall.WallBuild, "ustone-wall", {
    maxDamage: 40
}, {});

const densewall = wlib.extend(Wall, Wall.WallBuild, "dense-wall", {
    maxDamage: 32
}, {});

const steelwall = wlib.extend(Wall, Wall.WallBuild, "steel-wall", {
    maxDamage: 24
}, {});

const steelwallLarge = wlib.extend(Wall, Wall.WallBuild, "steel-wall-large", {
    maxDamage: 48
}, {});

const diriumwall = wlib.extend(Wall, Wall.WallBuild, "dirium-wall", {
    maxDamage: 75,
    blinkFrame: 20
}, {});

const diriumwallLarge = wlib.extend(Wall, Wall.WallBuild, "dirium-wall-large", {
    maxDamage: 150,
    blinkFrame: 20
}, {});
