const standardDenseLarge = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardDenseBig, standardDenseLarge);
standardDenseLarge.damage *= 1.2;
standardDenseLarge.speed *= 1.1;
standardDenseLarge.width *= 1.12;
standardDenseLarge.height *= 1.12;

const standardHomingLarge = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardDenseBig, standardHomingLarge);
standardHomingLarge.damage *= 1.1;
standardHomingLarge.reloadMultiplier = 1.3;
standardHomingLarge.homingPower = 0.09;
standardHomingLarge.speed *= 1.1;
standardHomingLarge.width *= 1.09;
standardHomingLarge.height *= 1.09;

const standardIncendiaryLarge = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardIncendiaryBig, standardIncendiaryLarge);
standardIncendiaryLarge.damage *= 1.2;
standardIncendiaryLarge.speed *= 1.1;
standardIncendiaryLarge.width *= 1.12;
standardIncendiaryLarge.height *= 1.12;

const standardThoriumLarge = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardThoriumBig, standardThoriumLarge);
standardThoriumLarge.damage *= 1.2;
standardThoriumLarge.speed *= 1.1;
standardThoriumLarge.width *= 1.12;
standardThoriumLarge.height *= 1.12;

const apparition = extendContent(ItemTurret, "apparition", {
	load(){
		this.super$load();
		
		this.baseRegion = Core.atlas.find("unity-block-" + this.size);
	}
});
apparition.ammo(
	Items.graphite, standardDenseLarge,
	Items.silicon, standardHomingLarge,
	Items.pyratite, standardIncendiaryLarge,
	Items.thorium, standardThoriumLarge
);