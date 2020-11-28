const weap1 = new Weapon("unity-dipole-sparker");
weap1.reload = 30;
weap1.x = 7;
weap1.y = -1;
weap1.rotate = false;
weap1.shake = 0;
weap1.shootSound = Sounds.spark;
weap1.bullet = Vars.content.getByName(ContentType.block, "unity-plasma").shootType.fragBullet;

const dipole = extendContent(UnitType, "dipole", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});

dipole.constructor = () => {
	const unit = extend(LegsUnit, {});
	return unit;
}

var weaps = [weap1];
for(var i = 0; i < weaps.length; i++){
	weaps[i].alternate = false;
	dipole.weapons.add(weaps[i]);
}

dipole.groundLayer = Layer.legUnit + 3;