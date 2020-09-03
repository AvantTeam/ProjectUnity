const lib = require("unity/libraries/light/lightSource");

const lamp = lib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "light-lamp", {
  lightLength: 30
  //The original Block extension object.
}, {
  //The original Building extension object.
});
