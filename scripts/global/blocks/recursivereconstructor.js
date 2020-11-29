const Class = java.lang.Class;

const t6upgrades = new Seq();
const t7upgrades = new Seq();

const fLib = this.global.unity.funclib;
const recrect = extendContent(Reconstructor, "recursive-reconstructor", {
	setStats(){
		this.super$setStats();

		this.stats.remove(Stat.output);
		this.stats.add(Stat.output, fLib.customValue(table => {
            let size = 8 * 3;

			table.row();
			table.add("T6").color(Pal.accent).row();
            t6upgrades.each(upgrade => {
				if(upgrade[0].unlockedNow() && upgrade[1].unlockedNow()){
					table.image(upgrade[0].icon(Cicon.small)).size(size).padRight(4).padLeft(10).scaling(Scaling.fit).right();
                    table.add(upgrade[0].localizedName).left();

                    table.add("[lightgray] -> ");

                    table.image(upgrade[1].icon(Cicon.small)).size(size).padRight(4).scaling(Scaling.fit);
                    table.add(upgrade[1].localizedName).left();
                    table.row();
				};
            });

			table.row();
			table.add("T7").color(Pal.accent).row();
            t7upgrades.each(upgrade => {
				if(upgrade[0].unlockedNow() && upgrade[1].unlockedNow()){
					table.image(upgrade[0].icon(Cicon.small)).size(size).padRight(4).padLeft(10).scaling(Scaling.fit).right();
                    table.add(upgrade[0].localizedName).left();

                    table.add("[lightgray] -> ");

                    table.image(upgrade[1].icon(Cicon.small)).size(size).padRight(4).scaling(Scaling.fit);
                    table.add(upgrade[1].localizedName).left();
                    table.row();
				};
            });
		}));
	},
	
	load(){
		this.super$load();

		this.outRegion = Core.atlas.find("unity-factory-out-11");
		this.inRegion = Core.atlas.find("unity-factory-in-11");
	},
});

recrect.buildType = () => {
	return extendContent(Reconstructor.ReconstructorBuild, recrect, {
		tier: 6,

		buildConfiguration(table){
			table.button("T6", Styles.togglet, () => {
				this.tier = 6;
			}).width(50).height(50).update(b => b.setChecked(this.tier == 6));

			table.button("T7", Styles.togglet, () => {
				this.tier = 7;
			}).width(50).height(50).update(b => b.setChecked(this.tier == 7));
		},

		upgrade(type){
            let u;

			switch(this.tier){
				case 6:
					u = t6upgrades.find(u => u[0].equals(type));
					break;

				case 7:
					u = t7upgrades.find(u => u[0].equals(type));
					break;

                default:
                    throw new Error("invalid tier: " + this.tier + "!");
			};

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
	});
};

module.exports = {
    addT6Upgrade(arr){
        t6upgrades.add(arr);

        return this; // for chaining
    },

    addT7Upgrade(arr){
        t7upgrades.add(arr);

        return this; // for chaining
    }
};
