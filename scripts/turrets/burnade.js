const burnade = extendContent(ItemTurret, "burnade", {
	init(){
		this.super$init();
		this.ammo(Items.coal, Bullets.basicFlame, Items.pyratite, Bullets.pyraFlame);
	}
});
