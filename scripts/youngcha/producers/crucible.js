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
    load() {
        this.super$load();
        this.liquidsprite = Core.atlas.find(this.name + "-liquid");
        this.bottom = Core.atlas.find(this.name + "-base");
		this.floorSprite = Core.atlas.find(this.name + "-floor");
        this.solidItem = Core.atlas.find(this.name + "-solid");
		this.solidItemStrip = Core.atlas.find(this.name + "-solidstrip");
		this.heatSprite = Core.atlas.find(this.name + "-heat");
    },
}, {
    updatePost() {},
    draw() {
        let temp = this.getGraphConnector("heat graph").getTemp();
        let dex = this.getGraphConnector("crucible graph").getTileIndex();
        let tileindex = crucibleLib.tileIndexMap[dex];
		Draw.rect(crucible.floorSprite, this.x, this.y, 8, 8, 0);
		this.drawContents(this.getGraphConnector("crucible graph"),crucible.liquidsprite,tileindex,crucible.solidItem,crucible.solidItemStrip);
		crucibleLib.drawTile(crucible.bottom, this.x, this.y, 8, 8, 0, tileindex);
        heatlib.drawHeat(crucibleLib.getRegion(crucible.heatSprite, tileindex), this.x, this.y, 0, temp);
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
			Draw.color(new Color(col.r*invt,col.g*invt,col.b*invt), Mathf.clamp(tliquid*fraction*3.0));	
			Draw.rect(crucibleLib.getRegion(liquidsprite, tindex), this.x, this.y, 8, 8, 0);
		}
		for(let i = 0;i<cc.length;i++){
			if(cc[i].meltedRatio<1){
				let itmcol = Color.valueOf("555555");
				if(cc[i].item){
					itmcol = cc[i].item.color;
				}
				Draw.color(itmcol);	
				let ddd = (1-cc[i].meltedRatio)*cc[i].volume*fraction;
				if(ddd>1){
					Draw.rect(heatlib.getRegion(itemsprite2,Math.floor(ddd)-1,6,1), this.x, this.y, 0);
				}
				if(ddd>0.1){
					let siz = 8.0*(ddd%1.0);
					Draw.rect(itemsprite, randomPos[Math.min(Math.floor(ddd),5)].x + this.x, randomPos[Math.min(Math.floor(ddd),5)].y + this.y,siz,siz);
				}
			}
		}
		Draw.color();	
	}
	

});
crucible.update = true;
crucible.rotate = true;
crucible.solid = true;
crucible.getGraphConnectorBlock("crucible graph").setAccept([1, 1, 1, 1]);
crucible.getGraphConnectorBlock("heat graph").setAccept([1, 1, 1, 1]);
crucible.getGraphConnectorBlock("heat graph").getBaseHeatConductivity(0.2);
crucible.getGraphConnectorBlock("heat graph").getBaseHeatCapacity(75); //maybe make it dynamic later...
crucible.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.006);