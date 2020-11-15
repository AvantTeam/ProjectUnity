const recrect = Reconstructor("recursive-reconstructor");

recrect.buildType = () => {
	return extendContent(Reconstructor.ReconstructorBuild, recrect, {
		update(){
			this.super$update();

			if(this.tier == null){
				this.tier = 6;
			}
		},

		buildConfiguration(table){
			

			table.button("T6", Styles.togglet, () => {
				this.tier = 6;
			}).width(50).height(50).update(b => b.setChecked(this.tier == 6));

			table.button("T7", Styles.togglet, () => {
				this.tier = 7;
			}).width(50).height(50).update(b => b.setChecked(this.tier == 7));
		},

		upgrade(type){
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
            write.f(this.tier);
        },

        read(read, revision){
            this.super$read(read, revision);
            this.tier = read.f();
        }

	})
}
