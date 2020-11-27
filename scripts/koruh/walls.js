const wlib = this.global.unity.limitwall;

const stonewall = wlib.extend(Wall, Wall.WallBuild, "ustone-wall", {
    maxDamage: 40
}, {});

const densewall = wlib.extend(Wall, Wall.WallBuild, "dense-wall", {
    maxDamage: 36
}, {});

const steelwall = wlib.extend(Wall, Wall.WallBuild, "steel-wall", {
    maxDamage: 32
}, {});

const steelwallLarge = wlib.extend(Wall, Wall.WallBuild, "steel-wall-large", {
    maxDamage: 64
}, {});
