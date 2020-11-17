const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");
const crucibleLib = require("libraries/cruciblelib");

let crublankobj = graphLib.init();
graphLib.addGraph(crublankobj, heatlib.baseTypesHeat.heatConnector);
graphLib.addGraph(crublankobj, crucibleLib.baseTypes.crucibleConnector);



const randomPos=[
	{x:0,y:0},
	{x:-1.6,y:1.6},
	{x:-1.6,y:-1.6},
	{x:1.6,y:-1.6},
	{x:-1.6,y:-1.6},
	{x:0,y:0},
		];


const crucible = graphLib.finaliseExtend(Block, Building, "crucible", crublankobj, {
	viewPos:null,
    load() {
        this.super$load();
        this.liquidsprite = Core.atlas.find(this.name + "-liquid");
        this.bottom = Core.atlas.find(this.name + "-base");
		this.floorSprite = Core.atlas.find(this.name + "-floor");
		this.roof = Core.atlas.find(this.name + "-roof");
        this.solidItem = Core.atlas.find(this.name + "-solid");
		this.solidItemStrip = Core.atlas.find(this.name + "-solidstrip");
		this.heatSprite = Core.atlas.find(this.name + "-heat");
    },
	getViewingGraph(){
		return this.viewPos;
	},
	setViewingGraph(s){
		this.viewPos=s;
	}
}, {
	
	
	
	
	buildConfiguration(table) {
		if(!Vars.headless){
			let buttoncell = table.button(Tex.whiteui, Styles.clearTransi, 50, run(() => {this.configure(0);}));
			buttoncell.size(50);
			buttoncell.get().getStyle().imageUp = Icon.eye;
		}
	},
	configured(player, value) {
		if(!Vars.headless){
			let thisg = this.getGraphConnector("crucible graph").getNetwork();
			this.block.setViewingGraph(this.block.getViewingGraph()==thisg?null:thisg);
		}
	},
	drawConfigure() {},
    updatePost() {},
    draw() {
       
        let dex = this.getGraphConnector("crucible graph");
		let tileindex = crucibleLib.tileIndexMap[dex.getTileIndex()];
		if(this.block.getViewingGraph()==dex.getNetwork()){
			 let temp = this.getGraphConnector("heat graph").getTemp();
			Draw.rect(crucible.floorSprite, this.x, this.y, 8, 8, 0);
			this.drawContents(this.getGraphConnector("crucible graph"),crucible.liquidsprite,tileindex,crucible.solidItem,crucible.solidItemStrip);
			crucibleLib.drawTile(crucible.bottom, this.x, this.y, 8, 8, 0, tileindex);
			heatlib.drawHeat(crucibleLib.getRegion(crucible.heatSprite, tileindex), this.x, this.y, 0, temp);
		}else{
			crucibleLib.drawTile(crucible.roof, this.x, this.y, 8, 8, 0, tileindex);
		}
        this.drawTeamTop();
    },

    //move out later
    //whether to accept the item? or the accepting of the item
    acceptItem(source, item) {
        let dex = this.getGraphConnector("crucible graph");
        return dex.canContainMore(1) && dex.getMelt(item);
    },

    //nvm thats handle item
    handleItem(source, item) {
        let dex = this.getGraphConnector("crucible graph");
        dex.addItem(item);
    },
	
	drawContents(crucgraph,liquidsprite,tindex, itemsprite, itemsprite2){
		let cc = crucgraph.getContained();
		if(!cc || cc.length==0){return;}
		
		let col = {r:0,g:0,b:0};
		let tliquid = 0;
		let fraction = crucgraph.liquidcap/crucgraph.getTotalLiquidCapacity();
		for(let i = 0;i<cc.length;i++){
			let itmcol = Color.valueOf("555555");
			if(cc[i].meltedRatio>0){
				let liquidvol = cc[i].meltedRatio*cc[i].volume;
				tliquid +=liquidvol;
				if(cc[i].item){
					itmcol = cc[i].item.color;
				}
				col.r +=itmcol.r*liquidvol;
				col.g +=itmcol.g*liquidvol;
				col.b +=itmcol.b*liquidvol;
			}
		}
		if(tliquid>0){
			let invt = 1.0/tliquid;
			Draw.color(new Color(col.r*invt,col.g*invt,col.b*invt), Mathf.clamp(tliquid*fraction*2.0));	
			Draw.rect(crucibleLib.getRegion(liquidsprite, tindex), this.x, this.y, 8, 8, 0);
		}
		for(let i = 0;i<cc.length;i++){
			if(cc[i].meltedRatio<1&&cc[i].volume*fraction>0.1){
				let itmcol = Color.valueOf("555555");
				if(cc[i].item){
					itmcol = cc[i].item.color;
				}
				
				let ddd = (1-cc[i].meltedRatio)*cc[i].volume*fraction;
				if(ddd>0.1){
					Draw.color(itmcol);	
					if(ddd>1){
						Draw.rect(heatlib.getRegion(itemsprite2,Math.floor(ddd)-1,6,1), this.x, this.y, 0);
					}
					let siz = 8.0*(ddd%1.0);
					Draw.rect(itemsprite, randomPos[Math.min(Math.floor(ddd),5)].x + this.x, randomPos[Math.min(Math.floor(ddd),5)].y + this.y,siz,siz);
				}
			}
		}
		Draw.color();	
	}
	

});
crucible.configurable = true;
crucible.update = true;
crucible.solid = true;
crucible.getGraphConnectorBlock("crucible graph").setAccept([1, 1, 1, 1]);
crucible.getGraphConnectorBlock("heat graph").setAccept([1, 1, 1, 1]);
crucible.getGraphConnectorBlock("heat graph").getBaseHeatConductivity(0.2);
crucible.getGraphConnectorBlock("heat graph").getBaseHeatCapacity(75); //maybe make it dynamic later...
crucible.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.006);













//Casting tables ----------------------------------------------------------------------------------------





let cmblankobj = graphLib.init();
graphLib.addGraph(cmblankobj, heatlib.baseTypesHeat.heatConnector);
graphLib.addGraph(cmblankobj, crucibleLib.baseTypes.crucibleConnector);


const castingMold = graphLib.finaliseExtend(Block, Building, "casting-mold", cmblankobj, {
    load() {
        this.super$load();
        this.liquidsprite = Core.atlas.find(this.name + "-liquid");
        this.bottom = [Core.atlas.find(this.name + "-base1"),Core.atlas.find(this.name + "-base2"),Core.atlas.find(this.name + "-base3"),Core.atlas.find(this.name + "-base4")];
		this.top = [Core.atlas.find(this.name + "-top1"),Core.atlas.find(this.name + "-top2"),Core.atlas.find(this.name + "-top3"),Core.atlas.find(this.name + "-top4")];
    },
}, {
	pourProgress:0,
	castProgress:0,
	castingMelt:null,
	outputBuildings:null,
	writeExt(stream) {
		stream.i(this.castingMelt?this.castingMelt.id:-1);
		stream.f(this.pourProgress);
		stream.f(this.castProgress);
	},
	readExt(stream, revision) {
		this.castingMelt = crucibleLib.meltTypes[stream.i()];
		this.pourProgress=stream.f();
		this.castProgress=stream.f();
	},
	proxUpdate() {
		this.updateOutput();
	},
	onRotationChanged() {
		this.updateOutput();
	},
	displayExt(table){
		let that =this;
		table.row();
        table.table(
            cons(sub => {
                sub.clearChildren();
                sub.left();
				if(that.castingMelt){
					sub.image(that.castingMelt.item.icon(Cicon.medium));
					sub.label(prov(() => {
						return Strings.fixed((that.pourProgress+that.castProgress) *50.0, 2) + "%";
					})).color(Color.lightGray);
				}else{
					sub.labelWrap("Nothing being casted").color(Color.lightGray);
				}
            })
        ).left();
	},
	updateOutput(){
		let newouts = [];
		for(let i =0;i<=1;i++){
			let pos = this.getConnectSidePos(i);
			let b = this.nearby(pos.toPos.x,pos.toPos.y);
			if(b){
				newouts.push(b);
			}
		}
		this.outputBuildings = newouts;
	},
    updatePost() {
		if(this.items.total()>0){
			this.pourProgress=0;
			this.castProgress=0;
			if(this.timer.get(this.block.timerDump, this.block.dumpTime)){
				if(!this.outputBuildings){
					this.updateOutput();
				}
				let itempass = this.items.first();
				for(let i =0;i<this.outputBuildings.length;i++){	
					if(this.outputBuildings[i].team == this.team && this.outputBuildings[i].acceptItem(this, itempass)){
						this.outputBuildings[i].handleItem(this, itempass);
						this.items.remove(itempass, 1);
						return;
					}
				}
			}
			return;
		}
		let dex = this.getGraphConnector("crucible graph");
		if(!this.castingMelt){
			this.pourProgress=0;
			this.castProgress=0;
			let cc = dex.getContained();
			for(let i = 0;i<cc.length;i++){
				if(cc[i].meltedRatio*cc[i].volume>1){
					dex.getNetwork().addLiquidToSlot(cc[i],-1);
					let melttype = crucibleLib.meltTypes[cc[i].id];
					if(melttype.notItem){
						continue;
					}
					this.castingMelt = melttype;
					break;
				}
			}
		}else{
			if(this.pourProgress<1.0){
				this.pourProgress += this.edelta()*0.05;
				if(this.pourProgress>1){
					this.pourProgress=1;
				}
				return;
			}
			if(this.castProgress<1.0){
				let temp = this.getGraphConnector("heat graph").getTemp();
				this.castProgress += Math.max(0,((1.0-(temp/this.castingMelt.meltpoint))*0.5)*this.castingMelt.meltspeed);
				if(this.castProgress>1){
					this.castProgress=1;
				}
				return;
			}
			this.items.add(this.castingMelt.item,1);
			this.castingMelt=null;
		}
	},
    draw() {
        let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(castingMold.bottom[this.rotation], this.x, this.y,0);
		if(this.castingMelt){
			if(this.pourProgress>0){
				Draw.color(this.castingMelt.item.color,1.0-Math.abs(this.pourProgress-0.5)*2.0);
				Draw.rect(castingMold.liquidsprite, this.x, this.y,this.rotdeg());
				Draw.color();
				Draw.rect(this.castingMelt.item.icon(Cicon.medium),this.x, this.y,this.pourProgress*8,this.pourProgress*8);
			}
			if(this.castProgress<1&&this.pourProgress>0){
				heatlib.drawHeat(this.castingMelt.item.icon(Cicon.medium), this.x, this.y, 0, Mathf.map(this.castProgress,0,1,this.castingMelt.meltpoint,275));
			}
		}
		Draw.rect(castingMold.top[this.rotation], this.x, this.y,0);
        this.drawTeamTop();
    },
});
castingMold.update = true;
castingMold.rotate = true;
castingMold.hasItems = true;
castingMold.solid = true;
castingMold.itemCapacity=1;
castingMold.getGraphConnectorBlock("crucible graph").setAccept([0,0,0,0,1,1,0,0]);
castingMold.getGraphConnectorBlock("crucible graph").setDoesCrafting(false);
castingMold.getGraphConnectorBlock("crucible graph").setBaseLiquidCapacity(2);
castingMold.getGraphConnectorBlock("heat graph").setAccept([1,1,1,1,1,1,1,1]);
castingMold.getGraphConnectorBlock("heat graph").getBaseHeatConductivity(0.2);
castingMold.getGraphConnectorBlock("heat graph").getBaseHeatCapacity(55); 
castingMold.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.01);
