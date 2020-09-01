const burnade = extendContent(ItemTurret, "burnade", {
	init(){
		this.ammo(Items.coal, Bullets.basicFlame, Items.pyratite, Bullets.pyraFlame);
		this.super$init();
	}
});
