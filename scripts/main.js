this.global.unity = {};

const loadFile = (prev, array) =>
{
    var results = [];
    var names = [];

    var p = prev;

    for (var i = 0; i < array.length; i++)
    {
        var file = array[i];

        if (typeof(file) === "object")
        {
            p.push(file.name);
            var temp = loadFile(p, file.childs);
            results = results.concat(temp.res);
            names = names.concat(temp.fileNames);
            p.pop();
        }
        else
        {
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
                    "lightReflector",
                    "lightReflector",
                    "lightRouter",
                    "lightRepeater"
                ]
            },
            "copterbase",
            "loader",
            "chainlaser",
            "exp",
            "multiCrafter"
        ]
    },

    {
        name: "global",
        childs: [
            {
                name: "blocks",
                childs: [
                    "recursivereconstructor"
                ]
            },

            {
                name: "flying-units",
                childs: [
                    "caelifera",
                    "schistocerca",
                    "anthophila"
                ]
            },

            {
                name: "ground-units",
                childs: [
                    "project-spiboss"
                    //"amphibi-ground"
                ]
            },

            {
                name: "naval-units",
                childs: [
                    "rexed",
                    "storm",
                    "amphibi" //naval version no legs
                ]
            },

            {
                name: "turrets",
                childs: [
                    "burnade",
                    "burnade-test"
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
                "shockwire"
            ]
        }]
    },

    {
        name: "koruh",
        childs: [
        {
            name: "turrets",
            childs: [
                "laser"
            ]
        }]
    },

    {
        name: "monolith",
        childs: [
        {
            name: "turrets",
            childs: [
                "oracle"
            ]
        }]
    }
];
const loadedScript = loadFile([], script);
for (var i = 0; i < loadedScript.res.length; i++)
{
    var res = loadedScript.res[i];
    var name = loadedScript.fileNames[i];
    this.global.unity[name] = require("unity/" + res);
};
