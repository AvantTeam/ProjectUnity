const graphLib = require("libraries/graphlib");

const testgraphblock = Object.assign(Object.create(graphLib.graphCommon), {
	flux:0,
	getFlux(){
		return this.flux;
	},
	setFlux(s){
		this.flux=s;
	},
	setStats(table) {
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.flux") + ":[] ").left();
		table.add(this.getFlux()+"Wb");
	},
});
const testgraphbuild = Object.assign(Object.create(graphLib.graphProps),{
	display(table) {
        if (!this._network) {
            return;
        }
        let ps = " Wb";
        let net = this._network;
        table.row();
        table.table(
            cons(sub => {
                sub.clearChildren();
                sub.left();
                sub.label(prov(() => {
                    return Strings.fixed(net.flux, 2) + ps;
                })).color(Color.lightGray);
            })
        ).left();
    },
});
const testgraph = {
	flux:0,
	fluxtotal:0,
	getFlux(){
		return this.flux;
	},
	setFlux(s){
		this.flux=s;
	},
	updateGraph() {
		this.fluxtotal = 0;
		this.connected.each(cons(building => {
            this.fluxtotal += building.getBlockData().getFlux();
        }));
		this.flux = Math.sqrt(this.fluxtotal);
	},
	mergeStats(graph){
		//nothing indirectly derived (e.g. rotlib angular velocity) so nothing here.
	},
};
const testgraphtype = {
	block:testgraphblock,
	build:testgraphbuild,
	graph:testgraph,
}
graphLib.setGraphName(testgraphtype,"flux graph");


//adding a block
let blankobj = graphLib.init();
graphLib.addGraph(blankobj, testgraphtype);

const testBlock = graphLib.finaliseExtend(Block, Building,"testgraph-block",blankobj,{
	
	load(){
		this.super$load();
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.basesprite = Core.atlas.find(this.name);
		//stuff here
		//etc
	},
	
},{
	
	updatePre()
	{
		//update stuff here
		//etc
	},
	
	draw() {
		Draw.rect(testBlock.basesprite, this.x, this.y, 0);
		Draw.rect(testBlock.topsprite, this.x, this.y, this.rotdeg());
        this.drawTeamTop();
		//etc
	}
	
});

testBlock.rotate = true;
testBlock.update = true;
testBlock.solid = true;
testBlock.getGraphConnectorBlock("flux graph").setAccept( [0,1,1,1]);
testBlock.getGraphConnectorBlock("flux graph").setFlux(5);
