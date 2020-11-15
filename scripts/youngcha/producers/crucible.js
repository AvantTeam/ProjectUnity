const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");
const crucibleLib = require("libraries/cruciblelib");

let crublankobj = graphLib.init();
graphLib.addGraph(crublankobj, heatlib.baseTypesHeat.heatConnector);
graphLib.addGraph(crublankobj, crucibleLib.baseTypes.crucibleConnector);


const randomPos=[];

for(let i =0;i<30;i++){
	randomPos[i] = {x:Mathf.range(2.0),y:Mathf.range(2.0)};
}

const crucible = graphLib.finaliseExtend(Block, Building, "crucible", crublankobj, {
    load() {
        this.super$load();
        this.liquidsprite = Core.atlas.find(this.name + "-liquid");
        this.bottom = Core.atlas.find(this.name + "-base");
		this.floorSprite = Core.atlas.find(this.name + "-floor");
        this.solidItem = Core.atlas.find(this.name + "-solid");
		this.heatSprite = Core.atlas.find(this.name + "-heat");
    },
}, {
    updatePost() {},
    draw() {
        let temp = this.getGraphConnector("heat graph").getTemp();
        let dex = this.getGraphConnector("crucible graph").getTileIndex();
        let tileindex = crucibleLib.tileIndexMap[dex];
		Draw.rect(crucible.floorSprite, this.x, this.y, 8, 8, 0);
		this.drawContents(this.getGraphConnector("crucible graph"),crucible.liquidsprite,tileindex,crucible.solidItem);
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
	
	drawContents(crucgraph,liquidsprite,tindex, itemsprite){
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
			Draw.color(new Color(col.r*invt,col.g*invt,col.b*invt), Mathf.clamp(tliquid));	
			Draw.rect(crucibleLib.getRegion(liquidsprite, tindex), this.x, this.y, 8, 8, 0);
		}
		let rpos = (this.tile.x*69+this.tile.y)%randomPos.length; //haha funny number
		for(let i = 0;i<cc.length;i++){
			if(cc[i].meltedRatio<1){
				let itmcol = Color.valueOf("555555");
				if(cc[i].item){
					itmcol = cc[i].item.color;
					
				}
				Draw.color(itmcol);	
				let ddd = (1-cc[i].meltedRatio)*cc[i].volume*fraction;
				for(let z = 0;z<ddd;z++){
					let siz = 5;
					siz *= Mathf.clamp(ddd-z);
					Draw.rect(itemsprite, randomPos[rpos].x + this.x, randomPos[rpos].y + this.y,siz,siz);
					rpos++;
					rpos = rpos%randomPos.length;
				}
			}
		}
		Draw.color();	
	}
	

});
crucible.update = true;
crucible.rotate = true;
crucible.getGraphConnectorBlock("heat graph").setAccept([1, 1, 1, 1]);
crucible.getGraphConnectorBlock("heat graph").getBaseHeatConductivity(0.2);
crucible.getGraphConnectorBlock("heat graph").getBaseHeatCapacity(75); //maybe make it dynamic later...
crucible.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.006);