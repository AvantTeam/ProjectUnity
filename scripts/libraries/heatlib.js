//imports
importPackage(Packages.arc.g2d);
const graphLib = require("libraries/graphlib");
//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.

function deepCopy(obj) {
    var clone = {};
    for (var i in obj) {
        if (typeof(obj[i]) == "object" && obj[i] != null) clone[i] = deepCopy(obj[i]);
        else clone[i] = obj[i];
    }
    return clone;
}
const _dirs = [{
        x: 1,
        y: 0
    },
    {
        x: 0,
        y: 1
    },
    {
        x: -1,
        y: 0
    },
    {
        x: 0,
        y: -1
    }

]

function sqrd(x) {
    return x * x;
}



const _HeatCommon = Object.assign(Object.create(graphLib.graphCommon),{
	_baseHeatCapacity:10.0, //heatenergy needed to raise temp by one deg.
	_baseHeatConductivity:0.5, //measure of how well it transfers heat 
	_baseHeatRadiativity:0.01, //amount of heat lost to entropy per update
	_maxtemp:1573.15, //max temp before damage in kelvin.
	getBaseHeatCapacity() {
        return this._baseHeatCapacity;
    },
    setBaseHeatCapacity(v) {
        this._baseHeatCapacity = v;
    },
	getBaseHeatConductivity() {
        return this._baseHeatConductivity;
    },
    setBaseHeatConductivity(v) {
        this._baseHeatConductivity = v;
    },
	getBaseHeatRadiativity() {
        return this._baseHeatRadiativity;
    },
    setBaseHeatRadiativity(v) {
        this._baseHeatRadiativity = v;
    },
    drawPlace(x, y, size ,rotation, valid) {
        for (let i = 0; i < this._accept.length; i++) {
            if (this._accept[i] == 0) {
                continue;
            }
            Lines.stroke(3.5, Color.pink);
            let outpos = getConnectSidePos(i, size, rotation);
            let dx = (outpos.toPos.x + x) * Vars.tilesize;
            let dy = (outpos.toPos.y + y) * Vars.tilesize;
            let dir = _dirs[outpos.dir];
            Lines.line(dx - dir.x, dy - dir.y, dx - dir.x * 2, dy - dir.y * 2);
        }

    },

    setStats(table) {
		table.row();
		table.left();
        table.add("Heat system").color(Pal.accent).fillX();
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.heatCapacity") + ":[] ").left();
		table.add((this.getBaseHeatCapacity())+"K J/K");
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.heatConductivity") + ":[] ").left();
		table.add(this.getBaseHeatConductivity()+"W/mK");
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.heatRadiativity") + ":[] ").left();
		table.add(this.getBaseHeatRadiativity()+"W/K");
		this.setStatsExt(table);
    },
	setStatsExt(table) {},
    otherStats(table) {},
});
// gets a conneciton point on its index, runs anticlockwise around the block.
/*
e.g:

1x1:      2x2:       3x3:
 1         32        543    
2▣0       4▣▣1      6▣▣▣2
 3        5▣▣0      7▣▣▣1   
           67       8▣▣▣0
		             9AB
*/
function getConnectSidePos(index, size, rotation) {
    let side = Mathf.floor(index / size);
    side = (side + rotation) % 4;
    let normal = _dirs[(side + 3) % 4]
    let tangent = _dirs[(side + 1) % 4]
    let originx = 0;
    let originy = 0;

    if (size > 1) {
        originx += Mathf.floor(size / 2);
        originy += Mathf.floor(size / 2);
        originy -= (size - 1);
        if (side > 0) {
            for (let i = 1; i <= side; i++) {
                originx += _dirs[i].x * (size - 1);
                originy += _dirs[i].y * (size - 1);
            }
        }
        originx += tangent.x * (index % size);
        originy += tangent.y * (index % size);
    }
    return {
        fromPos: {
            x: originx,
            y: originy
        },
        toPos: {
            x: originx + _dirs[side].x,
            y: originy + _dirs[side].y
        },
        dir: side

    }
}


const _HeatPropsCommon = Object.assign(deepCopy(graphLib.graphProps),{

    display(table) {
        if (!this._network) {
            return;
        }
        let ps = Core.bundle.get("stat.unity.temperatureUnit");
        table.row();
        table.table(
            cons(sub => {
                sub.clearChildren();
                sub.left();
                sub.label(prov(() => {
                    return Strings.fixed(this.getTemp()-273.15, 2) +ps;
                })).color(Color.lightGray);
            })
        ).left();
    },

    
    drawSelect() {
        if (this._network) {
            this._network.connected.each(cons(building => {
                Drawf.selected(building.getBuild().tileX(), building.getBuild().tileY(), building.getBuild().block, Pal.accent);
            }));
        }
    },
	
	updateProps(graph,index) {
		let temp = this.getTemp();
		let cond = this.getBlockData().getBaseHeatConductivity();
		this.setHeatBuffer(0);
		let that = this;
		this.eachNeighbour(function(neighbour){
			that.accumHeatBuffer((neighbour.build.getTemp()-temp)*cond*Time.delta);
		});
		this.accumHeatBuffer((293.15-temp)*this.getBlockData().getBaseHeatRadiativity()*Time.delta);
	},
	initStats(){
		this.setTemp(293.15);
	},
	
    _heat: 0,  /////MEAUSURED IN UNITS OF HEAT ENERGY (Joules) NOT TEMPERATURE!
	_heatbuffer: 0,  /////MEAUSURED IN UNITS OF HEAT ENERGY (Joules) NOT TEMPERATURE!
    getHeat() {
        return this._heat;
    },
    setHeat(h) {
        this._heat = h;
    },
	getHeatBuffer() {
        return this._heatbuffer;
    },
    setHeatBuffer(h) {
        this._heatbuffer = h;
    },
	accumHeatBuffer(h) {
        this._heatbuffer += h;
    },
	
	getTemp(){
		return this._heat/this.getBlockData().getBaseHeatCapacity();
	},
	setTemp(t){
		this.setHeat(t*this.getBlockData().getBaseHeatCapacity());
	},

	writeGlobal(stream) {
		stream.f(this._heat);
	},
	writeLocal(stream,graph) {
	},
	readGlobal(stream, revision) {
		this._heat = stream.f();
        this._heatbuffer = 0;
	},
	readLocal(stream, revision) {
	}

});



const heatGraph = {
	lastHeatFlow: 0,
	copyGraphStatsFrom(graph) {
		this.lastVelocity = graph.lastVelocity;
	},
	updateDirect(){

	},
	updateGraph() {
		this.lastHeatFlow=0;
		this.connected.each(cons(building => {
            building.setHeat(building.getHeat()+building.getHeatBuffer());
			this.lastHeatFlow+=building.getHeatBuffer();
        }));
	},
	mergeStats(graph){
		this.lastHeatFlow+=graph.lastHeatFlow;
	},
};


const heatcolor =  Pal.turretHeat;
const coldcolor =  Color.valueOf("6bc7ff");

function _drawHeat(reg,x,y,rot, temp){
	let a = 0;
	//Draper point is 798 K, but well do something 300K lower for aesthetic.
	if(temp>273.15){
		a = Math.max(0,(temp-498)*0.001);
		if(a<0.01){
			return;
		}
		if(a>1){
			let fcol = new Color(heatcolor);
			fcol.add(0,0,0.01*a);
			fcol.mul(a);
			Draw.color(fcol,a);
		}else{
			Draw.color(heatcolor,a);
		}
		
	}else{
		a =1.0-Mathf.clamp(temp/273.15);
		if(a<0.01){
			return;
		}
		Draw.color(coldcolor, a);
	}
	Draw.blend(Blending.additive);
	Draw.rect(reg, x,y, rot);
	Draw.blend();
	Draw.color();
}

function _getRegion(region, tile,sheetw,sheeth) {
    if (!region) {
        print("oh no there is no texture");
        return;
    }
    let nregion = new TextureRegion(region);
    let tilew = (nregion.u2 - nregion.u)/sheetw;
	let tileh = (nregion.v2 - nregion.v)/sheeth;
	let tilex = (tile%sheetw)/sheetw;
	let tiley = Math.floor(tile/sheetw)/sheeth;
	
	nregion.u = Mathf.map(tilex,0,1,nregion.u,nregion.u2)+tilew*0.02;
	nregion.v = Mathf.map(tiley,0,1,nregion.v,nregion.v2)+tileh*0.02; 
	nregion.u2 = nregion.u+tilew*0.96;
	nregion.v2 = nregion.v+tileh*0.96;
	nregion.width = 32;
	nregion.height = 32;
    return nregion;
}

const _baseTypesHeat = {
    heatConnector: {
        block: _HeatCommon,
        build: _HeatPropsCommon,
		graph: heatGraph,
    },
}



for(let key in _baseTypesHeat){
	graphLib.setGraphName(_baseTypesHeat[key],"heat graph");
}


module.exports = {
	getRegion: _getRegion,
	drawHeat: _drawHeat,
    heatGraph: heatGraph,
    heatProps: _HeatPropsCommon,
    heatCommon: _HeatCommon,
    dirs: _dirs,
    getConnectSidePos: getConnectSidePos,
    baseTypesHeat: _baseTypesHeat
}