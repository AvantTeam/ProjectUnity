const infusor = extendContent(Separator, "infusor", {
	load(){
		this.super$load();
		this.topRegion = Core.atlas.find(this.name + "-top");
		this.lightsRegion = Core.atlas.find(this.name + "-lights");
		this.tr = new Vec2();
	},
	icons(){
		return [
			Core.atlas.find(this.name),
			Core.atlas.find(this.name + "-top")
		]
	},
	setBars(){
		this.super$setBars();
		this.bars.add("heat", () => new Bar(Core.bundle.get("bar.heat"), Pal.lightOrange, () => 1));
	}
});

infusor.buildType = () => {
	return extendContent(Separator.SeparatorBuild, infusor, {
		draw(){
			this.super$draw();
			Draw.rect(infusor.topRegion, this.x, this.y);

			Draw.color(Blocks.thoriumReactor.coolColor, Blocks.thoriumReactor.hotColor, this.heat);
            Fill.rect(this.x, this.y, infusor.size * Vars.tilesize, infusor.size * Vars.tilesize);

            if(this.heat > 0.46){
                var flash = 1 + ((this.heat - 0.46) / (1 - 0.46)) * 5.4;
                flash += flash * Time.delta;
                Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9, 1));
                Draw.alpha(0.6);
                Draw.rect(infusor.lightsRegion, this.x, this.y);
            }

            Draw.reset();
		},

		updateTile(){
			this.super$updateTile();

			if(this.heat == null){
				this.heat = 0;
			}

			var cliquid = infusor.consumes.get(ConsumeType.liquid);
            var item = infusor.consumes.getItem().items[1].item;

            var fuel = this.items.get(item);
            var fullness = fuel / infusor.itemCapacity;

            if(fuel > 0 && this.enabled){
                this.heat += fullness * 0.01 * Math.min(this.delta(), 4);
            }

            var liquid = cliquid.liquid;

            if(this.heat > 0){
                var maxUsed = Math.min(this.liquids.get(liquid), this.heat / 0.5);
                this.heat -= maxUsed * 0.5;
                this.liquids.remove(liquid, maxUsed)
            }

            if(this.heat > 0.3){
                var smoke = 1.0 + (this.heat - 0.3) / (1 - 0.3);
                if(Mathf.chance(smoke / 20.0 * this.delta())){
                    Fx.reactorsmoke.at(this.x + Mathf.range(infusor.size * Vars.tilesize / 2),
                    this.y + Mathf.random(infusor.size * Vars.tilesize / 2));
                }
            }

            this.heat = Mathf.clamp(this.heat);

            if(this.heat >= 0.999){
                Events.fire(Trigger.thoriumReactorOverheat);
                this.kill();
            }
		},

		onDestroyed(){
			this.super$onDestroyed();

            Sounds.explosionbig.at(this.tile);

            var fuel = this.items.get(infusor.consumes.get(ConsumeType.item).items[1].item);

            if((fuel < 5 && this.heat < 0.5) || !Vars.state.rules.reactorExplosions) return;

            Effect.shake(6, 16, this.x, this.y);

            Fx.nuclearShockwave.at(this.x, this.y);

            for(var i = 0; i < 6; i++){
                Time.run(Mathf.random(40), () => Fx.nuclearcloud.at(this.x, this.y));
            }

            Damage.damage(this.x, this.y, 40 * Vars.tilesize, 1350 * 4);

            for(var i = 0; i < 20; i++){
                Time.run(Mathf.random(50), () => {
                    infusor.tr.rnd(Mathf.random(40));
                    Fx.explosion.at(infusor.tr.x + this.x, infusor.tr.y + this.y);
                });
            }

            for(var i = 0; i < 70; i++){
                Time.run(Mathf.random(80), () => {
                    infusor.tr.rnd(Mathf.random(120));
                    Fx.nuclearsmoke.at(infusor.tr.x + this.x, infusor.tr.y + this.y);
                });
            }
		},

		drawLight(){
            var fract = this.productionEfficiency;
            Drawf.light(this.team, this.x, this.y, (90 + Mathf.absin(5, 5)) * fract, Tmp.c1.set(Color.valueOf("7f19ea")).lerp(Color.scarlet, this.heat), 0.6 * fract);
        },

        write(write){
            this.super$write(write);
            write.f(this.heat);
        },

        read(read, revision){
            this.super$read(read, revision);
            this.heat = read.f();
        }
	});
}