const mapAccretion = new JavaAdapter(SectorPreset, {}, "accretion", this.global.unity.planets.megalith, 30);
mapAccretion.alwaysUnlocked = true;
mapAccretion.captureWave = 25;

module.exports = {
	accretion: mapAccretion
};
