var multi = extendContent(GenericCrafter, "multi",
{
    tmpRecs: [
    { //1  you can skip recipe properties
        input:
        {

        },
        output:
        {
            power: 5.25
        },
        craftTime: 12
    },
    { //2
        input:
        {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output:
        {
            liquids: ["slag/5"],
        },
        craftTime: 60
    },
    { //3
        input:
        {
            items: ["pyratite/1", "blast-compound/1"],
            liquids: ["water/5"],
            power: 1
        },
        output:
        {
            items: ["scrap/1", "plastanium/2", "spore-pod/2"],
            liquids: ["oil/5"],
        },
        craftTime: 72
    },
    { //4
        input:
        {
            items: ["sand/1"],
        },
        output:
        {
            items: ["silicon/1"],
        },
        craftTime: 30
    },
    { //5
        input:
        {
            items: ["sand/1", "lead/2"],
            liquids: ["water/5"],
        },
        output:
        {
            items: ["unity-contagium/1"],
        },
        craftTime: 12
    },
    { //6
        input:
        {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output:
        {
            items: ["thorium/1", "surge-alloy/2"],
            liquids: ["slag/5"],
        },
        craftTime: 60
    },
    { //7
        input:
        {
            items: ["pyratite/1", "blast-compound/1"],
            liquids: ["water/5"],
            power: 1
        },
        output:
        {
            items: ["scrap/1", "plastanium/2", "spore-pod/2"],
            liquids: ["oil/5"],
        },
        craftTime: 72
    },
    { //8
        input:
        {
            items: ["sand/1"],
        },
        output:
        {
            items: ["silicon/1"],
        },
        craftTime: 30
    },
    { //9
        input:
        {
            items: ["sand/1", "lead/2"],
            liquids: ["water/5"],
        },
        output:
        {
            items: ["unity-contagium/1"],
        },
        craftTime: 12
    },
    { //10
        input:
        {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output:
        {
            items: ["thorium/1", "surge-alloy/2"],
            liquids: ["slag/5", "oil/5"],
        },
        craftTime: 60
    }],
    recs: [],
    itemList: [],
    liquidSet: new ObjectSet(),
    hasOutputItem: false,
    inputItemSet: new ObjectSet(),
    inputLiquidSet: new ObjectSet(),
    outputItemSet: new ObjectSet(),
    outputLiquidSet: new ObjectSet(),
    powerBarI: false,
    powerBarO: false,
    init()
    {
        for (var i = 0; i < this.tmpRecs.length; i++)
        {
            var tmp = this.tmpRecs[i];
            var isInputExist = tmp.input != null,
                isOutputExist = tmp.output != null;
            var tmpInput = tmp.input;
            var tmpOutput = tmp.output;
            if (isInputExist && tmpInput.power > 0) this.powerBarI = true;
            if (isOutputExist && tmpOutput.power > 0) this.powerBarO = true;
            this.recs[i] = {
                input:
                {
                    items: [],
                    liquids: [],
                    power: isInputExist ? typeof tmpInput.power == "number" ? tmpInput.power : 0 : 0
                },
                output:
                {
                    items: [],
                    liquids: [],
                    power: isOutputExist ? typeof tmpOutput.power == "number" ? tmpOutput.power : 0 : 0
                },
                craftTime: typeof tmp.craftTime == "number" ? tmp.craftTime : 80
            };
            var vc = Vars.content;
            var ci = ContentType.item;
            var cl = ContentType.liquid;
            var realInput = this.recs[i].input;
            var realOutput = this.recs[i].output;
            if (isInputExist)
            {
                if (tmpInput.items != null)
                {
                    for (var j = 0, len = tmpInput.items.length; j < len; j++)
                    {
                        if (typeof tmpInput.items[j] != "string") throw "It is not string at " + j + "th input item in " + i + "th recipe";
                        var words = tmpInput.items[j].split("/");
                        if (words.length != 2) throw "Malform at " + j + "th input item in " + i + "th recipe";
                        var item = vc.getByName(ci, words[0]);
                        if (item == null) throw "Invalid item: " + words[0] + " at " + j + "th input item in " + i + "th recipe";
                        this.inputItemSet.add(item);
                        if (isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th input item in " + i + "th recipe";
                        realInput.items[j] = new ItemStack(item, words[1] * 1);
                    }
                }
                if (tmpInput.liquids != null)
                {
                    for (var j = 0, len = tmpInput.liquids.length; j < len; j++)
                    {
                        if (typeof tmpInput.liquids[j] != "string") throw "It is not string at " + j + "th input liquid in " + i + "th recipe";
                        var words = tmpInput.liquids[j].split("/");
                        if (words.length != 2) throw "Malform at " + j + "th input liquid in " + i + "th recipe";
                        var liquid = vc.getByName(cl, words[0]);
                        if (liquid == null) throw "Invalid liquid: " + words[0] + " at " + j + "th input liquid in " + i + "th recipe";
                        this.inputLiquidSet.add(liquid);
                        this.liquidSet.add(liquid);
                        if (isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th input liquid in " + i + "th recipe";
                        realInput.liquids[j] = new LiquidStack(liquid, words[1] * 1);
                    }
                }
            }
            if (isOutputExist)
            {
                if (tmpOutput.items != null)
                {
                    for (var j = 0, len = tmpOutput.items.length; j < len; j++)
                    {
                        if (typeof tmpOutput.items[j] != "string") throw "It is not string at " + j + "th output item in " + i + "th recipe";
                        var words = tmpOutput.items[j].split("/");
                        if (words.length != 2) throw "Malform at " + j + "th output item in " + i + "th recipe"
                        var item = vc.getByName(ci, words[0]);
                        if (item == null) throw "Invalid item: " + words[0] + " at " + j + "th output item in " + i + "th recipe";
                        this.outputItemSet.add(item);
                        if (isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th output item in " + i + "th recipe";
                        realOutput.items[j] = new ItemStack(item, words[1] * 1);
                    }
                    if (j != 0) this.hasOutputItem = true;
                }
                if (tmpOutput.liquids != null)
                {
                    for (var j = 0, len = tmpOutput.liquids.length; j < len; j++)
                    {
                        if (typeof tmpOutput.liquids[j] != "string") throw "It is not string at " + j + "th output liquid in " + i + "th recipe";
                        var words = tmpOutput.liquids[j].split("/");
                        if (words.length != 2) throw "Malform at " + j + "th output liquid in " + i + "th recipe";
                        var liquid = vc.getByName(cl, words[0]);
                        if (liquid == null) throw "Invalid liquid: " + words[0] + " at " + j + "th output liquid in " + i + "th recipe";
                        this.outputLiquidSet.add(liquid);
                        this.liquidSet.add(liquid);
                        if (isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th output liquid in " + i + "th recipe";
                        realOutput.liquids[j] = new LiquidStack(liquid, words[1] * 1);
                    }
                }
            }
        }
        this.super$init();
    },
    setStats()
    {
        this.super$setStats();
        //this.stats.remove(BlockStat.powerUse);
        this.stats.remove(BlockStat.productionTime);
        var recLen = this.recs.length;
        //crafTimes
        for (var i = 0; i < recLen; i++)
        {
            var rec = this.recs[i];
            var outputItems = rec.output.items,
                inputItems = rec.input.items;
            var outputLiquids = rec.output.liquids,
                inputLiquids = rec.input.liquids;
            this.stats.add(BlockStat.productionTime, i + 1, StatUnit.none);
            this.stats.add(BlockStat.productionTime, rec.craftTime / 60, StatUnit.seconds);
            this.stats.add(BlockStat.input, i + 1, StatUnit.none);
            //items
            for (var l = 0, len = inputItems.length; l < len; l++) this.stats.add(BlockStat.input, inputItems[l]);
            //liquids
            for (var l = 0, len = inputLiquids.length; l < len; l++) this.stats.add(BlockStat.input, inputLiquids[l].liquid, inputLiquids[l].amount, false);
            this.stats.add(BlockStat.output, i + 1, StatUnit.none);
            //items
            for (var jj = 0, len = outputItems.length; jj < len; jj++) this.stats.add(BlockStat.output, outputItems[jj]);
            //liquids
            for (var jj = 0, len = outputLiquids.length; jj < len; jj++) this.stats.add(BlockStat.output, outputLiquids[jj].liquid, outputLiquids[jj].amount, false);
            if (this.powerBarI)
            {
                this.stats.add(BlockStat.powerUse, i + 1, StatUnit.none);
                if (this.recs[i].input.power > 0) this.stats.add(BlockStat.powerUse, this.recs[i].input.power * 60, StatUnit.powerSecond);
                else this.stats.add(BlockStat.powerUse, 0, StatUnit.powerSecond);
            }
            if (this.powerBarO)
            {
                this.stats.add(BlockStat.basePowerGeneration, i + 1, StatUnit.none);
                if (this.recs[i].output.power > 0) this.stats.add(BlockStat.basePowerGeneration, this.recs[i].output.power * 60, StatUnit.powerSecond);
                else this.stats.add(BlockStat.basePowerGeneration, 0, StatUnit.powerSecond);
            }
        }
    },
    setBars()
    {
        this.super$setBars();
        //initialize
        this.bars.remove("liquid");
        this.bars.remove("items");
        if (!this.powerBarI)
        {
            this.bars.remove("power");
        }
        if (this.powerBarO)
        {
            this.outputsPower = true;
            this.bars.add("poweroutput", func(entity =>
                new Bar(prov(() => Core.bundle.format("bar.poweroutput", entity.block.getPowerProduction(entity.tile) * 60 * entity.timeScale)), prov(() => Pal.powerBar), floatp(() => typeof entity["getPowerStat"] === "function" ? entity.getPowerStat() : 0))
            ));
        }
        else if (!this.powerBarI) this.outputsPower = true;
        else this.outputsPower = false;
        //display every Liquids that can contain
        var i = 0;
        if (!this.liquidSet.isEmpty())
        {
            this.liquidSet.each(cons(k =>
            {
                this.bars.add("liquid" + i, func(entity =>
                    new Bar(prov(() => k.localizedName), prov(() => k.barColor()), floatp(() => entity.liquids.get(k) / this.liquidCapacity))
                ));
                i++;
            }));
        }
    },
});
multi.configurable = true;
multi.hasItems = true;
multi.hasLiquids = true;
multi.hasPower = true;
multi.dumpToggle=false;
multi.size=3;
multi.entityType=prov(()=>extend(GenericCrafter.GenericCrafterBuild,{

}));
multi.buildVisibility=BuildVisibility.sandboxOnly;
multi.category=Category.crafting;
