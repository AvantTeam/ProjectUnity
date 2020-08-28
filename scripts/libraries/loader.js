const loaderBlock = extendContent(Block, "loader-block", {
	load(){
		this.region = Core.atlas.white();
	},
	
	init(){
		Core.app.post(run(() => {
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
				var newPlan = [];
				for(var i = 0; i < airFac.plans.length; i++){
					newPlan.push(airFac.plans[i]);
				};
				newPlan.push(caelifera);
				airFac.plans = newPlan;
				
				airFac.config(java.lang.Integer, (tile, i) => {
					tile.currentPlan = (Math.floor(i) < 0 || Math.floor(i) >= airFac.plans.length) ? -1 : Math.floor(i);
					tile.progress = 0;
				});
				
				for(var i = 0; i < airFac.plans.length; i++){
					for(var j = 0; j < airFac.plans[i].requirements.length; j++){
						var stack = airFac.plans[i].requirements[j];
						airFac.capacities[stack.item.id] = Math.max(airFac.capacities[stack.item.id], stack.amount * 2);
						airFac.itemCapacity = Math.max(airFac.itemCapacity, stack.amount * 2);
					}
				};
				
				airFac.consumes.add(extendContent(ConsumeItemDynamic, func(e => {
					return e.currentPlan != -1 ? (airFac.plans[e.currentPlan]).requirements : ItemStack.empty
				}), {}));
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