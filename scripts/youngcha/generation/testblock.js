const graphLib = require("libraries/graphlib");
print("init");
let blankobj = graphLib.init();
print("got blankobj");
const testgraphblock = Object.assign(Object.create(graphLib.graphCommon));
const testgraphbuild = Object.assign(Object.create(graphLib.graphProps));
const testgraph = {
	updateGraph() {
		print("graph size: "+this.connected.size);
	},
};
print("init2");
const testgraphtype = {
	block:testgraphblock,
	build:testgraphbuild,
	graph:testgraph,
}
print("init"+testgraphtype);
graphLib.setGraphName(testgraphtype,"torque graph");
print("init"+testgraphtype.graph.name);
graphLib.addGraph(blankobj, testgraphtype);
const testBlock = graphLib.finaliseExtend(Block, Building,"testgraph-block",blankobj,{
	
	load(){
		this.super$load();
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.basesprite = Core.atlas.find(this.name);
		

	},
	
},{
	
	updatePre()
	{
		
	},
	
	draw() {
		Draw.rect(testBlock.basesprite, this.x, this.y, 0);
		Draw.rect(testBlock.topsprite, this.x, this.y, this.rotdeg());
        this.drawTeamTop();

	}
	
});

print("finshed"+testBlock);

/*driveShaft.buildType= ()=>{
	
	
}*/
testBlock.rotate = true;
testBlock.update = true;
testBlock.solid = true;
testBlock.getGraphConnectorBlock("torque graph").setAccept( [0,1,1,1]);