const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");



const partinfo = 
[
	{
		name:"Pivot",
		desc:"",
		tx:4,
		ty:0,
		tw:1,
		th:1,
		cannotPlace:true,
		prePlace:{
			x:0,
			y:0,
		},
		isRoot:true,
		cost:[
		]
	},
	{
		name:"Blade",
		desc:"Slices and knocks back enemies",
		tx:0,
		ty:0,
		tw:1,
		th:1,
		cost:[
			{
				name:"unity-nickel",
				amount: 3
			},
			{
				name:"titanium",
				amount: 5
			}
		]
	},
	{
		name:"Serrated blade",
		desc:"A heavy reinforced blade.",
		tx:2,
		ty:0,
		tw:2,
		th:1,
		cost:[
			{
				name:"unity-nickel",
				amount: 8
			},
			{
				name:"lead",
				amount: 5
			}
		]
	},
	{
		name:"Rod",
		desc:"Supporting structure, does not collide",
		tx:1,
		ty:0,
		tw:1,
		th:1,
		cost:[
			{
				name:"titanium",
				amount: 3
			}
		]
	},
	
];

const chopperTurret = rotL.torqueExtend(Block, Building, "chopper", rotL.baseTypes.torqueConnector, {

	load(){
		this.super$load();
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.base = Core.atlas.find(this.name + "-base");
		this.partsAtlas = Core.atlas.find(this.name + "-parts");
	},
},{
	buildConfiguration(table) {
		let buttoncell = table.button(Tex.whiteui, Styles.clearTransi, 50, run(() => {
			let dialog = new BaseDialog("Edit Blueprint");
            dialog.setFillParent(false);
			modturretlib.applyUI(dialog.cont,chopperTurret.partsAtlas,5,1,partinfo,5,4);
			dialog.buttons.button("@ok", () => {
				this.configure(0);
				dialog.hide();
			}).size(130.0, 60.0);
			dialog.update(() => {
				if(this.tile.block() != chopperTurret){
					dialog.hide();
				}
			});
			dialog.show();
		
		
		
		}));
		buttoncell.size(50);
		buttoncell.get().getStyle().imageUp = Icon.pencil;
	},
	configured(player, value) {
		
	},
	
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(3);
		this.getGraphConnector("torque graph").setFriction(0.01);
	},

	draw() {
		let tgraph = this.getGraphConnector("torque graph");
		Draw.rect(chopperTurret.base, this.x, this.y, this.rotdeg());
		//speeeeeeen tgraph.getRotation()
		Draw.rect(chopperTurret.topsprite, this.x, this.y, 0);
        this.drawTeamTop();
	}


});

chopperTurret.rotate = true;
chopperTurret.update = true;
chopperTurret.solid = true;
chopperTurret.configurable = true;
chopperTurret.getGraphConnectorBlock("torque graph").setAccept([1,0,0,0]);
chopperTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.01);
chopperTurret.getGraphConnectorBlock("torque graph").setBaseInertia(3);