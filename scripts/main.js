this.global.unity = {};

/* File "handler" */
function loadFile(array, dir){
	for(var file of array){
		this.global.unity[file] = require("unity/" + dir + "/" + file);
		print("Successfully loaded " + file + ".js");
	}
}

const libraries = ["copterbase", "loader", "chainlaser"];
loadFile(libraries, "libraries");

const groundUnits = ["project-spiboss"];
loadFile(groundUnits, "ground-units");

const flyingUnits = ["caelifera", "schistocerca"];
loadFile(flyingUnits, "flying-units");

const navalUnits = ["rexed", "storm"];
loadFile(navalUnits, "naval-units")

const blocks = ["recursivereconstructor"];
loadFile(blocks, "blocks");

var turrets = ["orb", "shockwire", "burnade"];
loadFile(turrets, "turrets");
