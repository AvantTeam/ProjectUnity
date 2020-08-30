/* File "handler" */
function loadFile(array, dir){
	for(var file of array){
		require("unity/" + dir + "/" + file);
		print("Successfully loaded " + file + ".js");
	}
}

const libraries = ["copterbase", "loader"];
loadFile(libraries, "libraries");

const groundUnits = ["project-spiboss"];
loadFile(groundUnits, "ground-units");

const flyingUnits = ["caelifera", "schistocerca"];
loadFile(flyingUnits, "flying-units");

const navalUnits = ["rexed", "storm"];
loadFile(navalUnits, "naval-units")

const blocks = ["recursive-reconstructor"];
loadFile(blocks, "blocks");

const turrets = ["orb-turret"];
loadFile(turrets, "turrets");