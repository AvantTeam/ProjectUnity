const wlib = this.global.unity.limitwall;

const stonewall = wlib.extend(Wall, Wall.WallBuild, "ustone-wall", {
    maxDamage: 32
}, {});

const densewall = wlib.extend(Wall, Wall.WallBuild, "dense-wall", {
    maxDamage: 30
}, {});

const steelwall = wlib.extend(Wall, Wall.WallBuild, "steel-wall", {
    maxDamage: 20
}, {});

const steelwallLarge = wlib.extend(Wall, Wall.WallBuild, "steel-wall-large", {
    maxDamage: 40
}, {});
