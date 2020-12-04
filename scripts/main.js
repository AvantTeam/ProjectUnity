global.unity = {};
Vars.enableConsole = true;
const loadFile = (prev, array) => {
    var results = [];
    var names = [];

    var p = prev;

    for(var i = 0; i < array.length; i++){
        var file = array[i];

        if(typeof(file) === "object"){
            p.push(file.name);
            var temp = loadFile(p, file.childs);

            results = results.concat(temp.res);
            names = names.concat(temp.fileNames);

            p.pop();
        }else{
            var temp = p.join("/") + "/" + file;

            results.push(temp);
            names.push(file);
        };
    };

    return {
        res: results,
        fileNames: names
    };
};

const script = [
    {
        name: "libraries",
        childs: [
            {
                name: "light",
                childs: [
                    "light",
                    "lightSource",
                    "lightConsumer", "lightCombiner", "lightRouter"
                ]
            },

            {
                name: "units",
                childs: [
                    "ai"
                ]
            },

            {
                name: "bullets",
                childs: [
                    "tractorbeam",
                    "shieldbullet"
                ]
            },

            {
                name: "loaders",
                childs: [
                    "loader",
                    "unitloader",
                    "musicloader"
                ]
            },

            "expsense",
            "sounds",
            "funclib",
            "effects",
            "extraeffects",
            "wavefront",
            "exporb",
            "wormlib",
            "unitlib",
            "copterbase",
            "exp",
            "expcrafter",
            "limitwall",
            "multi-lib",
            "status",
            "arena",
			"graphlib",
            "rotpowerlib",
            "heatlib",
            "cruciblelib",
            "turretmodui",
			"modularparts",
      "sparkle"
        ]
    },

    {
        name: "global",
        childs: [
            {
                name: "blocks",
                childs: [
                    "recursivereconstructor",

                    "light-lamp", "light-reflector", "light-extra",
                    "walls",
                    "ores",
                    "multi-test-younggam"
                ]
            },

            {
                name: "flying-units",
                childs: [
                    "caelifera",
                    "schistocerca",
                    "anthophila",
                    "vespula",
                    "lepidoptera",
                    "angel",
                    "malakhim"
                ]
            },

            {
                name: "ground-units",
                childs: [
                    "project-spiboss",
                    "arcaetana"
                ]
            },

            {
                name: "naval-units",
                childs: [
                    "rexed", "storm",

                    "amphibi", "craber"
                ]
            },

            {
                name: "turrets",
                childs: [
                    "burnade-test"
                ]
            },

            "planets",
            "maps"
        ]
    },

    {
        name: "scar",
        childs: [
            {
                name: "units",
                childs: [
                    "hovos",
                    "ryzer",

                    "whirlwind",
                    "jetstream",
                    "vortex"
                ]
            }
        ]
    },

    {
        name: "dark",
        childs: [
            {
                name: "turrets",
                childs: [
                    "apparition",
                    "ghost",
                    "banshee",

                    "fallout",
                    "catastrophe",
                    "calamity",
					"extinction"
                ]
            },

            {
                name: "factories",
                childs: [
                    "darkalloyfactory"
                ]
            },

            "darkwalls"
        ]
    },

    {
        name: "advance",
        childs: [
            {
                name: "turrets",
                childs: [
                    "celsius",
                    "kelvin"
                ]
            }
        ]
    },

    {
        name: "imber",
        childs: [
            {
                name: "turrets",
                childs: [
                    "orb",
                    "shockwire",
                    "current",
                    "plasma",
                    "shielder",
                    "electrobomb"
                ]
            },

            {
                name: "factories",
                childs: [
                    "sparkalloyfactory"
                ]
            },

            {
                name: "units",
                childs: [
                    "arcnelidia"
                ]
            }
        ]
    },

    {
        name: "koruh",
        childs: [
            "crafters",
            "walls",
            "lava",
            "conveyors",
            {
                name: "turrets",
                childs: [
                    "laser",
                    "lasercharge",
                    "laserfrost",
                    "laserswarmer",
                    "laserkelvin",
                    "laserbt",
                    "inferno"
                ]
            },

            {
                name: "blocks",
                childs: [
                    "shieldGenerator",
                    "deflectGenerator",
                    "shieldedWall",
                    "teleporter",
                    "teleunit",
                    "expoutput",
                    "expunloader",
                    "exptank",
                    "expchest",
                    "expfountain",
                    "expvoid"
                ]
            }
        ]
    },

    {
        name: "light",
        childs: [
            {
                name: "turrets",
                childs: [
                    "reflector"
                ]
            }
        ]
    },

    {
        name: "monolith",
        childs: [
            {
                name: "factories",
                childs: [
                    "monolithalloyfactory"
                ]
            },

            {
                name: "turrets",
                childs: [
                    "mage",
                    "oracle",
                    "spectrum"
                ]
            },

            {
                name: "units",
                childs: [
                    "stele",
                    "pedestal",
                    "pilaster",
                    "pylon",
                    "monument",
                    "colossus"
                ]
            },

            {
                name: "planets",
                childs: [
                    "megalith"
                ]
            },

            {
                name: "sectors",
                childs: [
                    "accretion"
                ]
            }
        ]
    },

    {
        name: "youngcha",
        childs: [
            {
                name: "distribution",
                childs: [
                    "heatdistributor",
                    "driveshaft",
                    "inlinegearbox",
                    "shaftrouter",
                    "simpletransmission",
                    "cruciblepump",
                    "mechanicalconveyor"
                ]
            },

            {
                name: "generation",
                childs: [
                    "heatgenerators",
                    "magnets",
                    "torqueinfi",
                    "handcrank",
                    "windturbine",
                    "waterturbine",
                    "electricmotor"
                ]
            },

            {
                name: "producers",
                childs: [
                    "augerdrill",
                    "crucible",
					"sporepyro",
					"sporefarm"
                ]
            },

            {
                name: "defense",
                childs: [
                    "youngchawalls",
                ]
            },
			{
                name: "turrets",
                childs: [
                    "chopper",
					"modularTurret",
                ]
            }
        ]
    },

    {
        name: "end",
        childs: [
            {
                name: "factories",
                childs: [
                    "terminalcrucible",
                    "endforge"
                ]
            },

            {
                name: "turrets",
                childs: [
                    "endgame"
                ]
            },

            {
                name: "units",
                childs: [
                    "devourer"
                ]
            }
        ]
    }
];
const loadedScript = loadFile([], script);
for(var i = 0; i < loadedScript.res.length; i++){
    var res = loadedScript.res[i];
    var name = loadedScript.fileNames[i];
    try{
        var content = require("unity/" + res);
        if(typeof(content) !== "undefined"){
            global.unity[name] = content;
        };
    }catch(e){
		print(e);
	};
};

if(!Vars.headless){
    Core.app.post(() => {
        var mod = Vars.mods.locateMod("unity");
        var change = "mod."+ mod.meta.name + ".";
        mod.meta.displayName = Core.bundle.get(change + "name");
        mod.meta.description = Core.bundle.get(change + "description");
    });


};
