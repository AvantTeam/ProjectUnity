// Unit Loader made by EoD
const Integer = java.lang.Integer;
const loader = this.global.unity.loader;

const tSeq1 = new Seq(UnitType);
// const UnitArrayClass = tSeq1.toArray(UnitType).getClass();
// const tSeq2 = new Seq(tSeq1.toArray(UnitType).getClass());
 const convertSeq = new Seq();
const unitPlans = new Seq(UnitFactory.UnitPlan);
// print(tSeq1.toArray(UnitType).getClass());
// var fac = null;

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
    
    unitPlans.add(plan);
    fac.plans.each(u => unitPlans.add(u));
    
    fac.plans = unitPlans.copy();
    
    fac.plans.each(uPlan => {
        var stack = uPlan.requirements;
        for(var j = 0; j < stack.length; j++){
            fac.capacities[stack[j].item.id] = Math.max(fac.capacities[stack[j].item.id], stack[j].amount * 2);
            fac.itemCapacity = Math.max(fac.itemCapacity, stack[j].amount * 2);
        }
    });
};

const unitLoader = extend(ContentList, {
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
        // 1 -> 2
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
                Vars.content.getByName(ContentType.unit, "unity-stele"),
                Vars.content.getByName(ContentType.unit, "unity-pedestal")
            ]
        ]);

        // 2 -> 3
        reconAdd(Blocks.multiplicativeReconstructor, [
            [
                Vars.content.getByName(ContentType.unit, "unity-schistocerca"),
                Vars.content.getByName(ContentType.unit, "unity-anthophila")
            ],

            [
                Vars.content.getByName(ContentType.unit, "unity-pedestal"),
                Vars.content.getByName(ContentType.unit, "unity-pilaster")
            ]
        ]);

        // 3 -> 4
        reconAdd(Blocks.exponentialReconstructor, [
            [
                Vars.content.getByName(ContentType.unit, "unity-anthophila"),
                Vars.content.getByName(ContentType.unit, "unity-vespula")
            ],

            [
                Vars.content.getByName(ContentType.unit, "unity-pilaster"),
                Vars.content.getByName(ContentType.unit, "unity-pylon")
            ]
        ]);

        // 4 -> 5
        reconAdd(Blocks.tetrativeReconstructor, [
            [
                Vars.content.getByName(ContentType.unit, "unity-vespula"),
                Vars.content.getByName(ContentType.unit, "unity-lepidoptera")
            ],

            [
                Vars.content.getByName(ContentType.unit, "unity-pylon"),
                Vars.content.getByName(ContentType.unit, "unity-monument")
            ]
        ]);

        // 5 -> 6
        global.unity.recursivereconstructor.addT6Upgrade(
            [
                UnitTypes.toxopid,
                Vars.content.getByName(ContentType.unit, "unity-project-spiboss")
            ]
        ).addT6Upgrade(
            [
                Vars.content.getByName(ContentType.unit, "unity-monument"),
                Vars.content.getByName(ContentType.unit, "unity-colossus")
            ]
        );

        // 6 -> 7
        global.unity.recursivereconstructor.addT7Upgrade(
            [
                Vars.content.getByName(ContentType.unit, "unity-project-spiboss"),
                Vars.content.getByName(ContentType.unit, "unity-arcaetana")
            ]
        );
    }
});

loader.addInit(unitLoader);