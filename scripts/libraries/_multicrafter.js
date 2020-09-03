const Integer = java.lang.Integer;

const each = (array, predicate, consumer) => {
	for(var i = 0; i < array.length; i++){
		var val = array[i];
		if(predicate(val)) consumer(val);
	};
};

/*
Consumers and outputs format:
"rawConsumes": [
	{
		"items": [
			{"item": "copper", "amount": 1}
		],
		"liquid": {
			"liquid": "cryofluid",
			"amount": 1
		},
		"power": 1
	},
	
	{
		"items": [
			{"item": "copper", "amount": 1}
		],
		"liquid": {
			"liquid": "unity-<liquid-name>",
			"amount": 2
		},
		"power": 1
	},
]

"outputItems": [
	{"item": "unity-imberium", "amount": 6},
	{"item": "unity-light-alloy", "amount": 7},
]

"outputLiquids": [
	{"liquid": "cryofluid", "amount": 30},
	{"liquid": "unity-<liquid-name>", "amount": 12}
]
*/

module.exports = {
	extend(type, build, name, obj, objb){
		if(typeof(obj) === "undefined") obj = {};
		if(typeof(objb) === "undefined") objb = {};
		
		obj = Object.assign({
			outputItems: [],
			outputLiquids: [],
			craftTimes: [],
			
			drawers: [],
			
			rawConsumes: [],
			consumeTypes: []
		}, obj, {
			init(){
				each(this.outputItems, pred => true, outputType => {
					outputType = new ItemStack(Vars.content.getByName(ContentType.item, outputType.item), outputType.amount);
				});
				
				each(this.outputLiquids, pred => true, outputType => {
					outputType = new LiquidStack(Vars.content.getByName(ContentType.liquid, outputType.liquid), outputType.amount);
				});
				
				each(this.rawConsumes, pred => true, consType => {
					var tmpCons = new Consumers();
					var keys = Object.keys(consType);
					each(keys, pred => true, key => {
						switch(key){
							case "items":
								var tmpArray = [];
								each(consType[key], pred => true, consItem => {
									var item = Vars.content.getByName(ContentType.item, consItem.item);
									tmpArray.push(new ItemStack(item, consItem.amount));
								});
								tmpCons.items(tmpArray);
							case "liquid":
								var liquid = Vars.content.getByName(ContentType.liquid, consType.liquid.liquid);
								tmpCons.liquid(liquid, consType.liquid.amount);
							case "power":
								tmpCons.power(consType.power);
						};
					});
					this.consumeTypes.push(tmpCons);
				});
				
				each(this.consumeTypes, pred => true, consumes => {
					if(consumes.has(ConsumeType.item)){
						this.hasItems = true;
						this.acceptsItems = true;
					};
					
					if(consumes.has(ConsumeType.liquid)){
						this.hasLiquids = true;
						this.outputsLiquid = true;
					};
					
					if(consumes.has(ConsumeType.power)) this.hasPower = true;
					
					consumes.init();
				});
				
				if(typeof(this.customInit) === "function"){
					this.customInit();
				};
				
				this.super$init();
			},
			
			setStats(){
				this.super$setStats();
				
				var i = 0;
				each(this.consumeTypes, pred => true, consumes => {
					if(consumes.has(ConsumeType.liquid)) {
						var cons = consumes.get(ConsumeType.liquid);
						cons.timePeriod = this.craftTimes[i];
					};
					i++;
				});
				
				i = 0;
				each(this.craftTimes, pred => true, craftTime => {
					this.stats.add(BlockStat.productionTime, i + 1, StatUnit.none);
					this.stats.add(BlockStat.productionTime, craftTime / 60, StatUnit.seconds);
					i++;
				});
				
				each(this.consumeTypes, pred => true, consumes => {
					consumes.display(this.stats);
				});
				
				i = 0;
				each(this.outputItems, pred => true, outputItem => {
					this.stats.add(BlockStat.output, i + 1, StatUnit.none);
					this.stats.add(BlockStat.output, outputItem);
					i++;
				});
				
				i = 0;
				each(this.outputLiquids, pred => true, outputLiquid => {
					this.stats.add(BlockStat.output, i + 1, StatUnit.none);
					this.stats.add(BlockStat.output, outputLiquid.liquid, outputLiquid.amount, false);
					i++;
				});
			},
			
			load(){
				this.super$load();
				each(this.drawers, drawer => typeof(drawer) !== "undefined", drawer => drawer.load());
			},
			
			icons(){
				if(typeof(this.drawers[0]) !== "undefined"){
					return this.drawers[0].icons(this);
				};
				return [Core.atlas.find("error")];
			},
			
			outputsItems(){
				return this.outputItems.length > 0;
			}
		});
		
		var multiCrafter = extendContent(type, name, obj);
		multiCrafter.configurable = true;
		
		multiCrafter.config(Integer, (b, i) => {
			b.setConfiguration(i);
		});
		
		objb = Object.assign(objb, {
			progresses: [],
			totalProgresses: [],
			
			_cons: [],
			
			_configuration: 0,
			
			setConfiguration(config){
				this._configuration = config;
			},
			
			getConfiguration(){
				return this._configuration;
			},
			
			draw(){
				if(typeof(multiCrafter.drawers[this._configuration]) !== "undefined"){
					multiCrafter.drawers[this._configuration].draw(this);
				};
			},
			
			create(block, team){
				var consCapacity = 0;
				var liquidCapacity = 0;
				
				each(block.consumeTypes, pred => true, consumes => {
					consCapacity++;
				});
				
				for(var i = 0; i < consCapacity; i++){
					this._cons[i] = new ConsumeModule(this.base());
				};
				
				return this.super$create(block, team);
			},
			
			buildConfiguration(table){
				var i = 0;
				each(multiCrafter.outputItems, pred => true, outputItem => {
					table.button(new TextureRegionDrawable(outputItem.item.icon(Cicon.small)), () => {
						this.configure(new Integer(i));
					});
					i++;
				});
			},
			
			write(writes){
				this.super$write(writes);
				if(typeof(this.customWrite) === "function") this.customWrite(writes);
				
				writes.i(this._configuration);
			},
			
			read(reads){
				this.super$read(reads);
				if(typeof(this.customRead) === "function") this.customRead(reads);
				
				this._configuration = reads.i();
			},
			
			drawStatus(){
				if(this.block.enableDrawStatus && this.block.consumeTypes[this._configuration].any()) {
					var brcx = this.tile.drawx() + (this.block.size * 8) / 2 - 4;
					var brcy = this.tile.drawy() - (this.block.size * 8) / 2 + 4;
					
					Draw.z(71);
					
					Draw.color(Pal.gray);
					Fill.square(brcx, brcy, 2.5, 45);
					
					Draw.color(this._cons[this._configuration].status().color);
					Fill.square(brcx, brcy, 1.5, 45);
					
					Draw.color();
				}
			},
			
			readBase(reads){
				this.health = reads.f();
				this.rotation = reads.b();
				this.team = Team.get(reads.b());
				
				if(this.items != null) this.items.read(reads);
				if(this.power != null) this.power.read(reads);
				if(this.liquids != null) this.liquids.read(reads);
				
				each(this._cons, pred => true, cons => {
					cons.read(reads);
				});
			},
			
			writeBase(writes){
				writes.f(this.health);
				writes.b(this.rotation);
				writes.b(this.team);
				
				if(this.items != null) this.items.write(writes);
				if(this.power != null) this.power.write(writes);
				if(this.liquids != null) this.liquids.write(writes);
				
				each(this._cons, pred => true, cons => {
					cons.write(writes);
				});
			},
			
			update(){
				this.hitTime -= Time.delta / 9;
				this.timeScaleDuration -= Time.delta;
				
				if(this.timeScaleDuration <= 0.0 || !this.block.canOverdrive){
					this.timeScale = 1;
				};
				
				if(this.block.autoResetEnabled){
					this.enabledControlTime -= Time.delta;
					if(this.enabledControlTime <= 0){
						this.enabled = true;
					};
				};
				
				if(this.sound != null){
					this.sound.update(this.x, this.y, this.shouldActiveSound());
				};
				if(this.block.idleSound != Sounds.none && this.shouldIdleSound()){
					Vars.loops.play(this.block.idleSound, this.base(), this.block.idleSoundVolume);
				};
				
				if(this.enabled || !this.block.noUpdateDisabled){
					this.updateTile();
				};
				
				if(this.items != null){
					this.items.update(this.updateFlow);
				};
				if(this.liquids != null){
					this.liquids.update(this.updateFlow);
				};
				if(this._cons.length > 0){
					this._cons[this._configuration].update();
				};
				if(this.power != null){
					this.power.graph.update();
				};
				
				this.updateFlow = false;
			},
			
			consValid(){
				return this._cons[this._configuration].valid();
			},
			
			consume(){
				this._cons[this._configuration].trigger();
			},
			
			updateTile(){
				if(this.consValid()){
					this.progresses[this._configuration] += this.getProgressIncrease(multiCrafter.craftTimes[this._configuration]);
					this.totalProgresses[this._configuration] += this.delta();
					this.warmup = Mathf.lerpDelta(this.warmup, 1, 0.02);
					
					if(Mathf.chanceDelta(multiCrafter.updateEffectChance)){
						multiCrafter.updateEffect.at(this.x + Mathf.range(multiCrafter.size * 4), this.y + Mathf.range(multiCrafter.size * 4));
					};
				}else{
					this.warmup = Mathf.lerp(this.warmup, 0, 0.02);
				};
				
				if(this.progresses[this._configuration] >= 1){
					this.consume();
					
					var outputItem = multiCrafter.outputItems[this._configuration];
					if(typeof(outputItem) !== "undefined" && outputItem != null){
						for(var i = 0; i < outputItem.amount; i++){
							this.offload(outputItem.item);
						};
					};
					
					var outputLiquid = multiCrafter.outputLiquids[this._configuration];
					if(typeof(outputLiquid) !== "undefined" && outputLiquid != null){
						this.handleLiquid(this, outputLiquid.liquid, outputLiquid.amount);
					};
					
					multiCrafter.craftEffect.at(this.x, this.y);
					this.progress = 0;
				};
				
				if(typeof(outputItem) !== "undefined" && outputItem != null && this.timer(multiCrafter.timerDump, 5)){
					this.dump(outputItem.item);
				};
				
				if(typeof(outputLiquid) !== "undefined" && outputLiquid != null){
					this.dumpLiquid(outputLiquid.liquid);
				};
			},
			
			shouldIdleSound(){
				return this._cons[this._configuration].valid();
			}
		});
		
		multiCrafter.entityType = ent => {
			ent = extendContent(build, multiCrafter, objb);
			return ent;
		};
		
		return multiCrafter;
	}
};
