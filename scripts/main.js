/* File "handler" */
function loadFile(array, dir){
	for(var file of array){
		require("unity/" + dir + "/" + file);
		print("Successfully loaded " + file + ".js");
	}
}

var libraries = ["copterbase", "loader"];
loadFile(libraries, "libraries");

var groundUnits = ["project-spiboss"];
loadFile(groundUnits, "ground-units");

var flyingUnits = ["caelifera"];
loadFile(flyingUnits, "flying-units");

var navalUnits = ["rexed", "storm"];
loadFile(navalUnits, "naval-units")

var blocks = ["recursive-reconstructor"];
loadFile(blocks, "blocks");

var turrets = ["orb-turret", "burnade"];
loadFile(turrets, "turrets");