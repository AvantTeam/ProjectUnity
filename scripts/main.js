this.global.unity = {};
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

            "wormlib",
            "copterbase",
            "loader",
            "chainlaser",
            "exp",
            "multi-lib"
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
                    "caelifera", "schistocerca", "anthophila", "vespula", "lepidoptera"
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
					"whirlwind"
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
                    "fallout",
                    "catastrophe",
					"calamity"
                ]
            },

            {
                name: "factories",
                childs: [
                    "darkalloyfactory"
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
                    "plasma"
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
            {
                name: "turrets",
                childs: [
                    "laser",
                    "inferno"
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
                    "oracle"
                ]
            },

            {
                name: "units",
                childs: [
                    "electron", "neutron"
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
                    "terminalcrucible"
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
            this.global.unity[name] = content;
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
    /*try{
		if(Mathf.chance(0.05)) Core.app.openURI((Mathf.chance(0.7))?"https://youtu.be/dQw4w9WgXcQ":"https://youtu.be/H01BwSD9eyQ");
    }
    catch(err){
        print(err);
    }*/
};
