const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");

const _dirs = [{x: 1,y: 0},{x: 0,y: 1},{x: -1,y: 0},{x: 0,y: -1}];


let thblankobj = graphLib.init();
graphLib.addGraph(thblankobj, heatlib.baseTypesHeat.heatConnector);

const thermalHeater = graphLib.finaliseExtend(Block, Building,"thermal-heater",thblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = [Core.atlas.find(this.name)+1,Core.atlas.find(this.name)+2,Core.atlas.find(this.name)+3,Core.atlas.find(this.name)+4];
	},
	attribute: Attribute.heat,

	getTerrainAttrib(){return this.attribute;},

	canPlaceOn( tile,  team){
        return tile.getLinkedTilesAs(this, this.tempTiles).sumf(other => other.floor().attributes.get(this.attribute)) > 0.01;
    }
},{
	sum:0,
	updatePost(){
		let eff = this.sum + this.block.getTerrainAttrib().env();
		let hgraph = this.getGraphConnector("heat graph");
		let temp = hgraph.getTemp();
		hgraph.setHeat(hgraph.getHeat()+ Math.max(0,1100-temp)*0.11*eff);
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(thermalHeater.bottom[this.rotation], this.x, this.y, 0);
		heatlib.drawHeat(thermalHeater.heatsprite,this.x, this.y,this.rotdeg(), temp);
        this.drawTeamTop();
	},
	onProximityAdded(){
		this.super$onProximityAdded();
		this.sum = this.block.sumAttribute( this.block.getTerrainAttrib(), this.tile.x, this.tile.y);
	}
});

thermalHeater.update = true;
thermalHeater.rotate = true;
thermalHeater.getGraphConnectorBlock("heat graph").setAccept( [1,1, 0,0, 0,0, 0,0]);
thermalHeater.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.6);
thermalHeater.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(40);
thermalHeater.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.004);





let chblankobj = graphLib.init();
graphLib.addGraph(chblankobj, heatlib.baseTypesHeat.heatConnector);

const combustionHeater = graphLib.finaliseExtend(Block, Building,"combustion-heater",chblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = [Core.atlas.find(this.name)+"-base1",Core.atlas.find(this.name)+"-base2",Core.atlas.find(this.name)+"-base3",Core.atlas.find(this.name)+"-base4"];
	},
	init(){
		this.consumes.add(new ConsumeItemFilter(item => { return item.flammability>= 0.1})).update(false).optional(true, false);
		this.super$init();
	}
},{
	generateTime:0,
	productionEfficiency:0,
	productionValid(){
		return this.generateTime > 0;
	},
	updatePost(){
		let delt   = this.delta();
		if(!this.consValid()){
			this.productionEfficiency = 0.0;
			return;
		}
		
		if(this.generateTime <= 0 && this.items.total() > 0){
			Fx.generatespark.at(this.x + Mathf.range(3.0), this.y + Mathf.range(3.0));
			let item = this.items.take();
			this.productionEfficiency = (item.flammability);
			this.generateTime=1.0;
		}
		
		if(this.generateTime > 0){
            this.generateTime -= Math.min(1.0 / 100.0 * delt, this.generateTime);
		}else{
			this.productionEfficiency = 0.0;
		}
		let hgraph = this.getGraphConnector("heat graph");
		let temp = hgraph.getTemp();
		hgraph.setHeat(hgraph.getHeat()+ Math.max(0,1200-temp)*0.45*this.productionEfficiency);
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(combustionHeater.bottom[this.rotation], this.x, this.y, 0);
		heatlib.drawHeat(combustionHeater.heatsprite,this.x, this.y,this.rotdeg(), temp);
        this.drawTeamTop();
	},
	writeExt(stream) {
		stream.f(this.productionEfficiency);
	},
	readExt(stream, revision) {
		this.productionEfficiency=stream.f();
	}
});

combustionHeater.update = true;
combustionHeater.rotate = true;
combustionHeater.hasItems = true;
combustionHeater.itemCapacity = 5;
combustionHeater.getGraphConnectorBlock("heat graph").setAccept( [1,1, 0,0, 0,0, 0,0]);
combustionHeater.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.6);
combustionHeater.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(40);
combustionHeater.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.004);




//INF sources -----------------------------------------------------------------------------------------------------


let ihblankobj = graphLib.init();
graphLib.addGraph(ihblankobj, heatlib.baseTypesHeat.heatConnector);
const infiHeater = graphLib.finaliseExtend(Block, Building,"infi-heater",ihblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name+"-base");
	},
},{
	updatePost(){
		let hgraph = this.getGraphConnector("heat graph");
		let temp = hgraph.getTemp();
		hgraph.setHeat(hgraph.getHeat()+ Math.max(0,9999-temp)*0.5);
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(infiHeater.bottom, this.x, this.y, 0);
		heatlib.drawHeat(infiHeater.heatsprite,this.x, this.y,this.rotdeg(), temp);
        this.drawTeamTop();
	},
});
infiHeater.update = true;
infiHeater.rotate = false;
infiHeater.getGraphConnectorBlock("heat graph").setAccept( [1,1, 1,1]);
infiHeater.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(1.0);
infiHeater.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(1000);
infiHeater.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.0);






let icblankobj = graphLib.init();
graphLib.addGraph(icblankobj, heatlib.baseTypesHeat.heatConnector);
const infiCooler = graphLib.finaliseExtend(Block, Building,"infi-cooler",icblankobj,{
	load(){
		this.super$load();
    this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name+"-base");
	},
},{
	updatePost(){
		let hgraph = this.getGraphConnector("heat graph");
		let temp = hgraph.getTemp();
		hgraph.setHeat(0);
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(infiCooler.bottom, this.x, this.y, 0);
		heatlib.drawHeat(infiCooler.heatsprite,this.x, this.y,this.rotdeg(), temp);
        this.drawTeamTop();
	},
});
infiCooler.update = true;
infiCooler.rotate = false;
infiCooler.getGraphConnectorBlock("heat graph").setAccept( [1,1, 1,1]);
infiCooler.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(1.0);
infiCooler.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(1000);
infiCooler.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.0);


////SOLAR --------------------------------------------------------------------------------------------

let scblankobj = graphLib.init();
graphLib.addGraph(scblankobj, heatlib.baseTypesHeat.heatConnector);
const solarCollector = graphLib.finaliseExtend(Block, Building,"solar-collector",scblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.lightsprite = Core.atlas.find(this.name+"-light");
		this.bottom = [Core.atlas.find(this.name+"1"),Core.atlas.find(this.name+"2"),Core.atlas.find(this.name+"3"),Core.atlas.find(this.name+"4")];
	},
},{
	linkedReflect:[],
	thermalPwr: 0,

	getThermalPowerCoeff(ref){
		let dst = Mathf.dst(ref.x,ref.y,this.x,this.y);
		let dir = _dirs[this.rotation];
		return Mathf.clamp((dir.x*(ref.x-this.x)/dst + dir.y*(ref.y-this.y)/dst)*1.5);
	},
	recalcThermalPwr(){
		this.thermalPwr=0;
		if(!this.linkedReflect || !this.linkedReflect.length){return;}
		for(var i =0 ;i<this.linkedReflect.length;i++){
			this.thermalPwr+=this.getThermalPowerCoeff(this.linkedReflect[i]);
		}
	},
	appendSolarReflector(ref){
		if(!this.linkedReflect.length){
			let g = [];
			this.linkedReflect = g;
		}
		this.linkedReflect.push(ref);
		this.recalcThermalPwr();
	},
	removeReflector(ref){
		for(var i =0 ;i<this.linkedReflect.length;i++){
			if(this.linkedReflect[i]==ref){
				this.linkedReflect.splice(i,1);
				this.recalcThermalPwr();
				return;
			}
		}
	},
	onDelete(){
		if(!this.linkedReflect){return;}
		while(this.linkedReflect.length > 0){
			this.linkedReflect[0].setLink(-1);
		}
	},
	updatePost(){
		let hgraph = this.getGraphConnector("heat graph");
		let temp = hgraph.getTemp();
		hgraph.setHeat(hgraph.getHeat()+ Math.min(this.thermalPwr,Math.max(0,800-temp)*this.thermalPwr*0.03));
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(solarCollector.bottom[this.rotation], this.x, this.y,0);
		heatlib.drawHeat(solarCollector.heatsprite,this.x, this.y,this.rotdeg(), temp);
		if(this.thermalPwr>0){
			Draw.z(Layer.effect);
			Draw.color(new Color(this.thermalPwr,this.thermalPwr,this.thermalPwr));
			Draw.rect(solarCollector.lightsprite, this.x, this.y,this.rotdeg());
			Draw.z();
		}
        this.drawTeamTop();
	},

});
solarCollector.update = true;
solarCollector.solid = true;
solarCollector.rotate = true;
solarCollector.getGraphConnectorBlock("heat graph").setAccept( [0,0,0 ,0,0,0 ,0,1,0 ,0,0,0]);
solarCollector.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(1.0);
solarCollector.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(60);
solarCollector.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.02);



const solarReflector = extendContent(Block, "solar-reflector", {
	load(){
		this.super$load();
		this.mirror = Core.atlas.find(this.name+"-mirror");
		this.bottom = Core.atlas.find(this.name+"-base");
		this.config(Point2, ( tile,  point) => tile.setLink(Point2.pack(point.x + tile.tileX(), point.y + tile.tileY())));
        this.config(java.lang.Integer, ( tile,  point) => tile.setLink(point));
	},
});
solarReflector.buildType = () => {

	let building = extend(Building, {
		mirrorRot:0,
		_link:-1,
		_hasChanged:false,
		getLink(s){
			return this._link;
		},
		setLink(s){
			if(s==this._link){return;}
			if(this._link!=-1 && Vars.world.build(this._link) && Vars.world.build(this._link).removeReflector){
				Vars.world.build(this._link).removeReflector(this);
			}
			if(s!=-1){
				this._hasChanged=true;
			}
			this._link=s;
		},
		updateTile(){
			this.mirrorRot+=0.4;
			let link = Vars.world.build(this._link);
            let hasLink = this.linkValid();

            if(hasLink){
                this.setLink(link.pos());
				this.mirrorRot = Mathf.slerpDelta(this.mirrorRot, this.tile.angleTo(link.tile), 0.05);
				if(this._hasChanged){
					link.appendSolarReflector(this);
					this._hasChanged = false;
				}
            }

		},
		draw(){
			Draw.rect(solarReflector.bottom, this.x, this.y);
			Drawf.shadow(solarReflector.mirror, this.x -(this.block.size / 2), this.y - (this.block.size / 2), this.mirrorRot);
			Draw.rect(solarReflector.mirror, this.x, this.y,this.mirrorRot);
		},
		drawConfigure(){
			let sin = Mathf.absin(Time.time, 6, 1);

			if(this.linkValid()){
                let target = Vars.world.build(this._link);
                Drawf.circles(target.x, target.y, (target.block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.place);
                Drawf.arrow(this.x, this.y, target.x, target.y, this.block.size * Vars.tilesize + sin, 4 + sin);
            }

            Drawf.dashCircle(this.x, this.y, 100, Pal.accent);
		},
		onConfigureTileTapped(other){
			if(this == other){
                this.configure(new java.lang.Integer(-1));
				print("confgured this, returning -1");
                return false;
            }
			if(this._link == other.pos()){
                this.configure(new java.lang.Integer(-1));
				print("confgured same, returning -1");
                return false;
            }else if(other.appendSolarReflector && other.dst(this.tile) <= 100 && other.team == this.team){
				print("confgured a valid building :0, returning "+other.pos());
                this.configure(new java.lang.Integer(other.pos()));
                return false;
            }
			return true;

		},
		config(){
            return Point2.unpack(this._link).sub(this.tile.x, this.tile.y);
        },
		linkValid(){
            if(this._link == -1) return false;
            let link = Vars.world.build(this._link);
			if(!link){return false;}
            return link.appendSolarReflector && link.team == this.team && this.within(link, 100);
        },
		write(stream) {
			this.super$write(stream);
			stream.i(this._link);
		},
		read(stream, revision) {
			this.super$read(stream,revision);
			this.setLink(stream.i());
		},
        onRemoved(){
            var build = Vars.world.build(this._link);
            if(build!=null && build.removeReflector) build.removeReflector(this);
        }
	});
	building.block = solarReflector;
	return building;
};
solarReflector.solid = true;
solarReflector.update = true;
solarReflector.configurable = true;
