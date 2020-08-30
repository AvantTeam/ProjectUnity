this.global.unity = {};

/* File "handler" */
function loadFile(array, dir){
	for(var file of array){
		this.global.unity[file] = require("unity/" + dir + "/" + file);
		print("Successfully loaded " + file + ".js");
	}
}

var libraries = ["copterbase", "loader", "chainlaser"];
loadFile(libraries, "libraries");

var groundUnits = ["project-spiboss"];
loadFile(groundUnits, "ground-units");

var flyingUnits = ["caelifera"];
loadFile(flyingUnits, "flying-units");

var navalUnits = ["rexed", "storm"];
loadFile(navalUnits, "naval-units")

var blocks = ["recursivereconstructor"];
loadFile(blocks, "blocks");

<<<<<<< HEAD
var turrets = ["orb-turret", "burnade"];
loadFile(turrets, "turrets");
=======
var turrets = ["orb", "shockwire"];
loadFile(turrets, "turrets");
>>>>>>> 743b95b1141f54035f30687bf8ed13f1478e2f41
