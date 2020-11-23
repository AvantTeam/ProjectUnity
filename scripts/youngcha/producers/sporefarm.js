

//tile.floor().liquidDrop == Liquids.water

const frames = 5;
const sporeFarm = extendContent(Block, "spore-farm", {
	_timerid:0,
	_dumptimerid:0,
	load(){
		this.super$load();
		this.plantSprite = [Core.atlas.find(this.name+"-spore1"),Core.atlas.find(this.name+"-spore2"),Core.atlas.find(this.name+"-spore3"),Core.atlas.find(this.name+"-spore4"),Core.atlas.find(this.name+"-spore5")];
		this.bottom  = [Core.atlas.find(this.name+"-ground1"),Core.atlas.find(this.name+"-ground2"),Core.atlas.find(this.name+"-ground3"),Core.atlas.find(this.name+"-ground4"),Core.atlas.find(this.name+"-ground5")];
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
		randomChk() {
			let ctile = Vars.world.tile(Math.floor(this.tile.x+Mathf.range(3)),Math.floor(this.tile.y+Mathf.range(3)));
			return ctile && ctile.floor().liquidDrop == Liquids.water;
		},
		attemptGrow(){
			let ctile = Vars.world.tile(Math.floor(this.tile.x+Mathf.range(2)+0.5),Math.floor(this.tile.y+Mathf.range(2)+0.5));
			if(!ctile || ctile.bc()||ctile.solid()){return false;}
			Call.setTile(ctile, sporeFarm, this.team, 0);
			return true;
		},
		updateTile(){
			
			if(this.timer.get(gtimer, (60+this.delay)*5.0 )){
				if(this.delay==-1){
					this.delay =  (this.tile.x*89+this.tile.y*13)%21;
				}else{		
					this.growth+=this.randomChk()?(this.growth>frames-2?0.1:0.45):-0.1;
					if(this.growth>=frames){
						this.growth=frames-1;
						if(!this.attemptGrow() && this.items.total()<1){
							this.offload(Items.sporePod);
						}
					}
					if(this.growth<0){
						this.growth=0;
						this.damage(10);
					}
				}
			}
			if(this.timer.get(dtimer,15)){
				this.dump(Items.sporePod);
			}
		},
		draw() {
			let rrot =  (this.tile.x*89+this.tile.y*13)%4;
			Draw.rect(sporeFarm.bottom[Math.floor(this.growth)], this.x, this.y, rrot*90.0);
			Draw.rect(sporeFarm.plantSprite[Math.floor(this.growth)], this.x, this.y, 0);
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
