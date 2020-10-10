const Integer = java.lang.Integer;

const loaderBlock = extendContent(Block, "loader-block", {
	/*load(){
		this.region = Core.atlas.white();
	},*/

	init(){
		Core.app.post(run(() => {
			// Air Factory
			try{
				const airFac = Blocks.airFactory;
				
				airFac.consumes.remove(ConsumeType.item);

				const caelifera = new UnitFactory.UnitPlan(
					Vars.content.getByName(ContentType.unit, "unity-caelifera"),
					60 * 25,
					ItemStack.with(
						Items.silicon, 15,
						Items.titanium, 25
					)
				);

				//var newPlan = [];
				var newPlan = new Seq();
				airFac.plans.each(i => {
					newPlan.add(i);
				});
				//newPlan.push(caelifera);
				newPlan.add(caelifera);
				airFac.plans = newPlan;

				airFac.config(Integer, (tile, i) => {
					tile.currentPlan = (Math.floor(i) < 0 || Math.floor(i) >= airFac.plans.size) ? -1 : Math.floor(i);
					tile.progress = 0;
				});

				for(var i = 0; i < airFac.plans.size; i++){
					for(var j = 0; j < airFac.plans.get(i).requirements.length; j++){
						var stack = airFac.plans.get(i).requirements[j];
						airFac.capacities[stack.item.id] = Math.max(airFac.capacities[stack.item.id], stack.amount * 2);
						airFac.itemCapacity = Math.max(airFac.itemCapacity, stack.amount * 2);
					}
				};

				airFac.consumes.add(extendContent(ConsumeItemDynamic, func(e => {
					return e.currentPlan != -1 ? (airFac.plans.get(e.currentPlan)).requirements : ItemStack.empty
				}), {}));
			}catch(e){
				print(e);
			};

			// Naval Factory
			try{
				const NavalFac = Blocks.navalFactory;

				NavalFac.consumes.remove(ConsumeType.item);

				const amphibi = new UnitFactory.UnitPlan(
					Vars.content.getByName(ContentType.unit, "unity-amphibi-naval"),
					60 * 25,
					ItemStack.with(
						Items.silicon, 15,
						Items.metaglass, 30,
						Items.titanium, 25
					)
				);

				var newPlan = new Seq();
				/*for(var i = 0; i < NavalFac.plans.size; i++){
					newPlan.add(NavalFac.plans.get(i));
				};*/
				NavalFac.plans.each(i => {
					newPlan.add(i);
				});
				newPlan.add(amphibi);
				NavalFac.plans = newPlan;

				NavalFac.config(Integer, (tile, i) => {
					tile.currentPlan = (Math.floor(i) < 0 || Math.floor(i) >= NavalFac.plans.size) ? -1 : Math.floor(i);
					tile.progress = 0;
				});

				for(var i = 0; i < NavalFac.plans.size; i++){
					for(var j = 0; j < NavalFac.plans.get(i).requirements.length; j++){
						var stack = NavalFac.plans.get(i).requirements[j];
						NavalFac.capacities[stack.item.id] = Math.max(NavalFac.capacities[stack.item.id], stack.amount * 2);
						NavalFac.itemCapacity = Math.max(NavalFac.itemCapacity, stack.amount * 2);
					}
				};

				NavalFac.consumes.add(extendContent(ConsumeItemDynamic, func(e => {
					return e.currentPlan != -1 ? (NavalFac.plans.get(e.currentPlan)).requirements : ItemStack.empty
				}), {}));
			}catch(e){
				print(e);
			};

			// Additive Reconstructor
			try{
				const addReconstructor = Blocks.additiveReconstructor;

				var newUpgrades = new Seq();
				
				addReconstructor.upgrades.each(i => {
					newUpgrades.add(i);
				});
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-caelifera"),
						Vars.content.getByName(ContentType.unit, "unity-schistocerca"),
					]
				);
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-amphibi-naval"),
						Vars.content.getByName(ContentType.unit, "unity-craber-naval")
					]
				);
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-electron"),
						Vars.content.getByName(ContentType.unit, "unity-neutron")
					]
				);
				addReconstructor.upgrades = newUpgrades;
			}catch(e){
				print(e);
			};

			// Multiplicative Reconstructor
			try{
				const mulReconstructor = Blocks.multiplicativeReconstructor;

				var newUpgrades = new Seq();
				
				mulReconstructor.upgrades.each(i => {
					newUpgrades.add(i);
				});
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-schistocerca"),
						Vars.content.getByName(ContentType.unit, "unity-anthophila")
					]
				);
				mulReconstructor.upgrades = newUpgrades;
			}catch(e){
				print(e);
			};

			// Exponential Reconstructor
			try{
				const expReconstructor = Blocks.exponentialReconstructor;

				var newUpgrades = new Seq();
				
				expReconstructor.upgrades.each(i => {
					newUpgrades.add(i);
				});;
				newUpgrades.add(
					[
						UnitTypes.bryde,
						Vars.content.getByName(ContentType.unit, "unity-rexed")
					]
				);
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-anthophila"),
						Vars.content.getByName(ContentType.unit, "unity-vespula"),
					]
				);
				expReconstructor.upgrades = newUpgrades;
			}catch(e){
				print(e);
			};

			// Tetrative Reconstructor
			try{
				const tetraReconstructor = Blocks.tetrativeReconstructor;

				var newUpgrades = new Seq();
				/*for(var i = 0; i < tetraReconstructor.upgrades.length; i++){
					newUpgrades.push(tetraReconstructor.upgrades[i]);
				}*/
				tetraReconstructor.upgrades.each(i => {
					newUpgrades.add(i);
				});;
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-rexed"),
						Vars.content.getByName(ContentType.unit, "unity-storm")
					]
				);
				newUpgrades.add(
					[
						Vars.content.getByName(ContentType.unit, "unity-vespula"),
						Vars.content.getByName(ContentType.unit, "unity-lepidoptera")
					]
				);
				tetraReconstructor.upgrades = newUpgrades;
			}catch(e){
				print(e);
			};
		}));
	},

	isHidden(){
		return true;
	}
});

this.global.loader = {};
