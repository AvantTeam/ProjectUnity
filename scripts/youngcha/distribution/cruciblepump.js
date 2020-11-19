const rotL = require("libraries/rotpowerlib");
const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");
const crucibleLib = require("libraries/cruciblelib");

const fillAm = [1.0,0.5,0.25];

let cpblankobj = graphLib.init();
graphLib.addGraph(cpblankobj, crucibleLib.baseTypes.crucibleMultiConnector);
graphLib.addGraph(cpblankobj, heatlib.baseTypesHeat.heatConnector);
const cruciblePump = graphLib.finaliseExtend(Block, Building,"crucible-pump",cpblankobj,{
	load(){
		this.super$load();
		this.top = [Core.atlas.find(this.name+"-top1"),Core.atlas.find(this.name+"-top2"),Core.atlas.find(this.name+"-top3"),Core.atlas.find(this.name+"-top4")];
		this.liquidSprite = Core.atlas.find(this.name+"-liquid");
		this.heatSprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name+"-bottom");
	},
},{
	filterItem:null,
	pumpMode:0,
	flowRate:0,
	flowAnimation:0,
	
	getFilterItemConfig(){return this.filterItem;},
	getPumpModeConfig(){return this.pumpMode;},
	
	buildConfiguration(table) {
		let that= this;
		table.labelWrap("Fill until:").growX().pad(5).center();
		table.row();
		table.table(cons((btable)=>{
			btable.button("Full", Styles.clearPartialt, run(() => {that.configure(0);})).left().size(50).disabled(boolf(b =>{ return that.pumpMode==0 ;}));
			btable.button("50%", Styles.clearPartialt, run(() => {that.configure(1);})).left().size(50).disabled(boolf(b =>{ return that.pumpMode==1 ;}));
			btable.button("25%", Styles.clearPartialt, run(() => {that.configure(2);})).left().size(50).disabled(boolf(b =>{ return that.pumpMode==2 ;}));
		}));
		
		
		table.row();
		table.labelWrap("Pump:").growX().pad(5).center();
		table.row();
		ItemSelection.buildTable(table, Vars.content.items(), () => this.filterItem, cons((x)=>{this.configure(x);}));
		table.setBackground(Styles.black5);
	},
	configured(player, value) {
		print(player);
		
		if(value instanceof Item){
			this.filterItem = value;
			print("set item: "+value);	
		}else if(!isNaN(value)){
			this.pumpMode = value;
			print("set mode: "+value);	
		}
	},
	displayExt(table){
		let that =this;
		let ps = " " + StatUnit.perSecond.localized();
		table.row();
        table.table(
            cons(sub => {
                sub.clearChildren();
                sub.left();
				if(that.filterItem){
					sub.image(that.filterItem.icon(Cicon.medium));
					sub.label(prov(() => {
						return Strings.fixed(that.flowRate *10.0, 2) + "units" + ps;
					})).color(Color.lightGray);
				}else{
					sub.labelWrap("No filter selected").color(Color.lightGray);
				}
            })
        ).left();
	},
	updatePost(){
		let rate = 0.08;
		let dex = this.getGraphConnector("crucible graph");
		this.flowRate/=2.0;
		if(this.filterItem){
			let fromNet = dex.getNetworkFromSet(1);
			let toNet = dex.getNetworkFromSet(0);
			if(fromNet&&toNet&&fromNet.contains&&toNet.contains){
				for(let i =0;i<fromNet.contains.length;i++){
					let fnc = fromNet.contains[i];
					if(fnc.item!=this.filterItem){
						continue;
					}
					let transfer = Math.min(rate*this.edelta(),fnc.volume*fnc.meltedRatio);
					transfer = Math.min(toNet.getRemainingSpace(),transfer);
					let toG = toNet.getMeltFromID(fnc.id);
					if(toG){
						transfer = Math.min(toNet.getLiquidCapacity()*fillAm[this.pumpMode]-toG.volume,transfer);
					}
					if(transfer<0){
						break;
					}
					if(transfer){
						fromNet.addLiquidToSlot(fnc,-transfer);
						toNet.addMeltItem(crucibleLib.meltTypes[fnc.id],transfer,true);
						this.flowRate = transfer;
					}
					break;
				}
				
			}
		}
		this.flowAnimation+=this.flowRate*0.4;
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(cruciblePump.bottom, this.x, this.y, 0);
		if(this.filterItem){
			Draw.color(this.filterItem.color, Mathf.clamp(this.flowRate*60.0));	
			rotL.drawSlideRect(cruciblePump.liquidSprite,this.x, this.y,16,16, 32,16, this.rotdeg()+180,16,this.flowAnimation);
			Draw.color();
		}
		heatlib.drawHeat(cruciblePump.heatSprite, this.x, this.y, this.rotdeg(), temp);
		Draw.rect(cruciblePump.top[this.rotation], this.x, this.y, 0);
        this.drawTeamTop();
	},
	writeExt(stream) {
		stream.s(this.filterItem == null ? -1 : this.filterItem.id);
		stream.i(this.pumpMode);
	},
	readExt(stream, revision) {
		this.filterItem = Vars.content.item(stream.s());
		this.pumpMode = stream.i();
	}
});
cruciblePump.rotate = true;
cruciblePump.update = true;
cruciblePump.solid = true;
cruciblePump.configurable = true;
cruciblePump.consumes.power(1.0);
cruciblePump.getGraphConnectorBlock("crucible graph").setAccept([1,1,0,0,2,2,0,0]);
cruciblePump.getGraphConnectorBlock("crucible graph").setDoesCrafting(false);
cruciblePump.getGraphConnectorBlock("crucible graph").setBaseLiquidCapacity(10);

cruciblePump.getGraphConnectorBlock("heat graph").setAccept( [1,1,1,1,1,1,1,1]);
cruciblePump.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.1);
cruciblePump.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(50);
cruciblePump.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.003);

