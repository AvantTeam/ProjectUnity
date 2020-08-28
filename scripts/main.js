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

<<<<<<< HEAD
var blocks = ["recursive-reconstructor"];
loadFile(blocks, "blocks");
=======
var turrets = ["orb-turret"];
loadFile(turrets, "turrets");
>>>>>>> 62b28c66a572e47d1fd8b270419ca8ea18a04fad
