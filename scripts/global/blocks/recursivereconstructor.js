const fLib = this.global.unity.funclib;
const recrect = extendContent(Reconstructor, "recursive-reconstructor", {
	init(){
		this.super$init();
		this.t6upgrades = [
			[
				UnitTypes.toxopid,
				Vars.content.getByName(ContentType.unit, "unity-project-spiboss")
			]
		]
		this.t7upgrades = [
			[
				Vars.content.getByName(ContentType.unit, "unity-project-spiboss"),
				Vars.content.getByName(ContentType.unit, "unity-arcaetana")
			]
		]
	},
	
	setStats(){
		this.super$setStats();
		this.stats.remove(Stat.output);
		this.stats.add(Stat.output, fLib.customValue(table => {
			table.row();
			table.add("T6").color(Pal.accent).row();
			for(var upgrade of this.t6upgrades){
				var size = 8 * 3;
				if(upgrade[0].unlockedNow() && upgrade[1].unlockedNow()){
					table.image(upgrade[0].icon(Cicon.small)).size(size).padRight(4).padLeft(10).scaling(Scaling.fit).right();
                    table.add(upgrade[0].localizedName).left();

                    table.add("[lightgray] -> ");

                    table.image(upgrade[1].icon(Cicon.small)).size(size).padRight(4).scaling(Scaling.fit);
                    table.add(upgrade[1].localizedName).left();
                    table.row();
				}
			}
			table.row();
			table.add("T7").color(Pal.accent).row();
			for(var upgrade of this.t7upgrades){
				var size = 8 * 3;
				if(upgrade[0].unlockedNow() && upgrade[1].unlockedNow()){
					table.image(upgrade[0].icon(Cicon.small)).size(size).padRight(4).padLeft(10).scaling(Scaling.fit).right();
                    table.add(upgrade[0].localizedName).left();

                    table.add("[lightgray] -> ");

                    table.image(upgrade[1].icon(Cicon.small)).size(size).padRight(4).scaling(Scaling.fit);
                    table.add(upgrade[1].localizedName).left();
                    table.row();
				}
			}
		}));
	},
	
	load(){
		this.super$load();
		this.outRegion = Core.atlas.find("unity-factory-out-11");
		this.inRegion = Core.atlas.find("unity-factory-in-11");
	},

	icons(){
		return [
			Core.atlas.find("unity-recursive-reconstructor"),
			Core.atlas.find("unity-factory-out-11"),
			Core.atlas.find("unity-recursive-reconstructor-top")
		]
	}
});

recrect.buildType = () => {
	return extendContent(Reconstructor.ReconstructorBuild, recrect, {
		tier : 6,
		
		buildConfiguration(table){
			

			table.button("T6", Styles.togglet, () => {
				this.tier = 6;
			}).width(50).height(50).update(b => b.setChecked(this.tier == 6));

			table.button("T7", Styles.togglet, () => {
				this.tier = 7;
			}).width(50).height(50).update(b => b.setChecked(this.tier == 7));
		},

		upgrade(type){
			switch(this.tier){
				case 6:
					var u = this.t6upgrades.find(u => u[0] == type);
					break;

				case 7:
					var u = this.t7upgrades.find(u => u[0] == type);
					break;
			}

			return u == null ? null : u[1];
		},

		write(write){
            this.super$write(write);
            write.b(this.tier);
        },

        read(read, revision){
            this.super$read(read, revision);
            this.tier = read.b();
        }

	})
}
