
const crucibleLib = require("libraries/cruciblelib");
//tile.floor().liquidDrop == Liquids.water

const frames = 5;
const tilechkdirs = [
	{x:-1,y: 1},{x:0,y: 1},{x:1,y: 1},
	{x:-1,y: 0},/*[tile]*/ {x:1,y: 0},
	{x:-1,y:-1},{x:0,y:-1},{x:1,y:-1},
];

const sporeFarm = extendContent(Block, "spore-farm", {
	_timerid:0,
	_dumptimerid:0,
	load(){
		this.super$load();
		this.plantSprite = [Core.atlas.find(this.name+"-spore1"),Core.atlas.find(this.name+"-spore2"),Core.atlas.find(this.name+"-spore3"),Core.atlas.find(this.name+"-spore4"),Core.atlas.find(this.name+"-spore5")];
		this.bottom  = [Core.atlas.find(this.name+"-ground1"),Core.atlas.find(this.name+"-ground2"),Core.atlas.find(this.name+"-ground3"),Core.atlas.find(this.name+"-ground4"),Core.atlas.find(this.name+"-ground5")];
		this.fence  = Core.atlas.find(this.name+"-fence");
	},
	getTimerId(){
		return this._timerid;
	},
	getDTimerId(){
		return this._dumptimerid;
	}
});

const gtimer = sporeFarm.timers++;
const dtimer = sporeFarm.timers++;

sporeFarm.buildType = () => 
{
	let building = extend(Building, {
		growth:0,
		delay:-1,
		tileIndex:-1,
		needsTileUpdate: false,
		triggerTileUpdate(){
			this.needsTileUpdate=true;
		},
		randomChk() {
			let ctile = Vars.world.tile(Math.floor(this.tile.x+Mathf.range(3)),Math.floor(this.tile.y+Mathf.range(3)));
			return ctile && ctile.floor().liquidDrop == Liquids.water;
		},
		updateTilings(){
			let bitmask = 0;
			for(let i =0;i<tilechkdirs.length;i++){
				let tile = this.tile.nearby(tilechkdirs[i].x, tilechkdirs[i].y);
				if(!tile || !tile.bc() || !tile.bc().updateTilings){
					continue;
				}
				bitmask += 1<<i;
			}
			this.tileIndex=bitmask;
		},
		updateNeighbours(){
			for(let i =0;i<tilechkdirs.length;i++){
				let tile = this.tile.nearby(tilechkdirs[i].x, tilechkdirs[i].y);
				if(!tile || !tile.bc() || !tile.bc().triggerTileUpdate){
					continue;
				}
				tile.bc().triggerTileUpdate();
			}
		},
		onProximityRemoved() {
			this.super$onProximityRemoved();
			this.updateNeighbours();
		},
		updateTile(){
			if(this.tileIndex==-1){
				this.updateTilings();
				this.updateNeighbours();
			}
			if(this.needsTileUpdate){
				this.updateTilings();
				this.needsTileUpdate = false;
			}
			if(this.timer.get(gtimer, (60+this.delay)*5.0 )){
				if(this.delay==-1){
					this.delay =  (this.tile.x*89+this.tile.y*13)%21;
				}else{		
					if(this.growth==0 && (!this.randomChk())){
						return;
					}
					this.growth+=this.randomChk()?(this.growth>frames-2?0.1:0.45):-0.1;
					if(this.growth>=frames){
						this.growth=frames-1;
						if(this.items.total()<1){
							this.offload(Items.sporePod);
						}
					}
					if(this.growth<0){
						this.growth=0;
					}
				}
			}
			if(this.timer.get(dtimer,15)){
				this.dump(Items.sporePod);
			}
		},
		draw() {
			let rrot =  (this.tile.x*89+this.tile.y*13)%4;
			let rrot2 =  (this.tile.x*69+this.tile.y*42)%4;
			if(this.growth!=0){
				Draw.rect(sporeFarm.bottom[Math.floor(this.growth)], this.x, this.y, rrot*90.0);
				Draw.rect(sporeFarm.plantSprite[Math.floor(this.growth)], this.x, this.y, rrot2*90.0);
			}
			Draw.rect(crucibleLib.getRegion(sporeFarm.fence, crucibleLib.tileIndexMap[this.tileIndex]), this.x, this.y, 8, 8, 0);
			this.drawTeamTop();
		},
		write(stream) {
			this.super$write(stream);
			stream.f(this.growth);
		},
		read(stream, revision) {
			this.super$read(stream,revision);
			this.growth=stream.f();
		}
	});
	building.block = sporeFarm;
	return building;
};
sporeFarm.update = true;
sporeFarm.solid = false;
