const rotL = require("libraries/rotpowerlib");

const handCrank = rotL.torqueExtend(Block, Building, "hand-crank", rotL.baseTypes.torqueConnector, {

	load(){
		this.super$load();
		this.handle = Core.atlas.find(this.name + "-handle");
		this.shaft = [Core.atlas.find(this.name + "-base1"),Core.atlas.find(this.name + "-base2")];
		this.base = Core.atlas.find(this.name + "-bottom");
	},
},{
	_cooldown:0,
	getCooldown(){return this._cooldown;},
	setCooldown(s){this._cooldown=s;},
	_force:0,
	
	
	buildConfiguration(table) {
		let buttoncell = table.button(Tex.whiteui, Styles.clearTransi, 50, run(() => {this.configure(0);}));
		buttoncell.size(50).disabled(boolf(b =>
			{ 
				return this.getCooldown()<30 ;
			}));
		buttoncell.get().getStyle().imageUp = Icon.redo;
	},
	configured(player, value) {
		this._force=40;
		this._cooldown=0;
		
	},
	updatePre()
	{
		let tgraph = this.getGraphConnector("torque graph");
		let ratio = (20-tgraph.getNetwork().lastVelocity)/20.0;
		tgraph.setInertia(3);
		tgraph.setFriction(0.01);
		tgraph.setForce(ratio*this._force);
		this._cooldown+=Time.delta;
		this._force*=0.8;
	},

	draw() {
		let tgraph = this.getGraphConnector("torque graph");
		let variant = (this.rotation==2||this.rotation==1) ? 1:0;
		Draw.rect(handCrank.base, this.x, this.y, 0);
		Draw.rect(handCrank.shaft[variant], this.x, this.y, this.rotdeg());
				//speeeeeeen
		Draw.rect(handCrank.handle, this.x, this.y, tgraph.getRotation());
        this.drawTeamTop();
	}


});

handCrank.rotate = true;
handCrank.update = true;
handCrank.solid = false;
handCrank.configurable = true;
handCrank.getGraphConnectorBlock("torque graph").setAccept([1,0,0,0]);
handCrank.getGraphConnectorBlock("torque graph").setBaseFriction(0.01);
handCrank.getGraphConnectorBlock("torque graph").setBaseInertia(3);