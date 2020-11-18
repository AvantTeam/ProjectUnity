const reloadFatigueL = new StatusEffect("reload-fatigue");
reloadFatigueL.reloadMultiplier = 0.75;

const endgameDisableL = new StatusEffect("endgame-disable");
endgameDisableL.speedMultiplier = 0.01;
endgameDisableL.permanent = true;
endgameDisableL.color = Color.valueOf("f53036");

module.exports = {
	reloadFatigue: reloadFatigueL,
	endgameDisable: endgameDisableL
};