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
			return this.getCooldown()<20 ;
			}  ));
		buttoncell.get().getStyle().imageUp = Icon.redo;
	},
	configured(player, value) {
		this._force=40;
		this._cooldown=0;
		
	},
	updatePre()
	{
		let ratio = (20-this.getNetwork().lastVelocity)/20.0;
		this.setInertia(3);
		this.setFriction(0.01);
		this.setForce(ratio*this._force);
		this._cooldown+=Time.delta;
		this._force*=0.7;
	},

	draw() {
		let variant = (this.rotation==2||this.rotation==1) ? 1:0;
		Draw.rect(handCrank.base, this.x, this.y, 0);
		Draw.rect(handCrank.shaft[variant], this.x, this.y, this.rotdeg());
				//speeeeeeen
		Draw.rect(handCrank.handle, this.x, this.y, this.getRotation());
        this.drawTeamTop();
	}


});

handCrank.rotate = true;
handCrank.update = true;
handCrank.solid = false;
handCrank.configurable = true;
handCrank.setAccept([1,0,0,0]);

///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");