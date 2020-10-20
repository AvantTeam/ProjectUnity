//Unit Loader made by EoD, an improved version than the one in @loader.js
const Integer = java.lang.Integer;
const loader = this.global.unity.loader;

const tSeq1 = new Seq(UnitType);
//const UnitArrayClass = tSeq1.toArray(UnitType).getClass();
//const tSeq2 = new Seq(tSeq1.toArray(UnitType).getClass());
//const convertSeq = new Seq();
const unitPlans = new Seq(UnitFactory.UnitPlan);
//print(tSeq1.toArray(UnitType).getClass());
//var fac = null;

//Seq in a seq
const reconAdd = (recon, planArray) => {
	/*var planSeq = new Seq(planArray);
	planSeq.each(e => {
		var f = new Seq(e);
		recon.upgrades.add(f.toArray(UnitArrayClass));
	});*/

	for(var i = 0; i < planArray.length; i++){
		var f = new Seq(planArray[i]);
		//tSeq1.clear();
		//tSeq1.add(planArray[0], planArray[1]);
		recon.upgrades.add(f.toArray(UnitType));
	};
};

//maybe later, my child
const reconChange = (recon, planArray) => {
	recon.upgrades.clear();

	for(var i = 0; i < planArray.length; i++){
		var f = new Seq(planArray[i]);
		//tSeq1.clear();
		//tSeq1.add(planArray[0], planArray[1]);
		recon.upgrades.add(f.toArray(UnitType));
	};
};

const addPlan = (factory, plan) => {
	unitPlans.clear();
	var fac = factory;
	
	fac.consumes.remove(ConsumeType.item);
	
	/*const caelifera = new UnitFactory.UnitPlan(
		Vars.content.getByName(ContentType.unit, "unity-caelifera"),
		60 * 25,
		ItemStack.with(
			Items.silicon, 15,
			Items.titanium, 25
		)
	);*/
	
	unitPlans.add(plan);
	fac.plans.each(u => unitPlans.add(u));
	
	fac.plans = unitPlans.copy();
	//whats the point of this again?, its created in the constructor. theres two of this now.
	fac.config(Integer, (build, i) => {
		build.currentPlan = (Math.floor(i) < 0 || Math.floor(i) >= fac.plans.size) ? -1 : Math.floor(i);
		build.progress = 0;
	});
	
	fac.plans.each(uPlan => {
		var stack = uPlan.requirements;
		for(var j = 0; j < stack.length; j++){
			fac.capacities[stack[j].item.id] = Math.max(fac.capacities[stack[j].item.id], stack[j].amount * 2);
			fac.itemCapacity = Math.max(fac.itemCapacity, stack[j].amount * 2);
		}
	});
	//also this
	fac.consumes.add(extendContent(ConsumeItemDynamic, func(e => {
		return e.currentPlan != -1 ? (fac.plans.get(e.currentPlan)).requirements : ItemStack.empty;
	}), {}));
};

const unitLoader = new ContentList(){
	load(){
		//Factories
		//Air
		const caelifera = new UnitFactory.UnitPlan(
			Vars.content.getByName(ContentType.unit, "unity-caelifera"),
			60 * 25,
			ItemStack.with(
				Items.silicon, 15,
				Items.titanium, 25
			)
		);
		addPlan(Blocks.airFactory, caelifera);
		
		//Naval
		const amphibi = new UnitFactory.UnitPlan(
			Vars.content.getByName(ContentType.unit, "unity-amphibi-naval"),
			60 * 25,
			ItemStack.with(
				Items.silicon, 15,
				Items.titanium, 25
			)
		);
		addPlan(Blocks.navalFactory, amphibi);
		//End
		//Reconstructors
		//1
		reconAdd(Blocks.additiveReconstructor, [
			[
				Vars.content.getByName(ContentType.unit, "unity-caelifera"),
				Vars.content.getByName(ContentType.unit, "unity-schistocerca")
			],
			[
				Vars.content.getByName(ContentType.unit, "unity-amphibi-naval"),
				Vars.content.getByName(ContentType.unit, "unity-craber-naval")
			],
			[
				Vars.content.getByName(ContentType.unit, "unity-electron"),
				Vars.content.getByName(ContentType.unit, "unity-neutron")
			]
		]);
		//2
		reconAdd(Blocks.multiplicativeReconstructor, [
			[
				Vars.content.getByName(ContentType.unit, "unity-schistocerca"),
				Vars.content.getByName(ContentType.unit, "unity-anthophila")
			]
		]);
		//3
		reconAdd(Blocks.exponentialReconstructor, [
			[
				Vars.content.getByName(ContentType.unit, "unity-anthophila"),
				Vars.content.getByName(ContentType.unit, "unity-vespula")
			]
		]);
		//4
		reconAdd(Blocks.tetrativeReconstructor, [
			[
				Vars.content.getByName(ContentType.unit, "unity-vespula"),
				Vars.content.getByName(ContentType.unit, "unity-lepidoptera")
			]
		]);
	}
};

loader.addInit(unitLoader);