

//tile.floor().liquidDrop == Liquids.water


const sporeFarm = extendContent(Block, "spore-farm", {
	_timerid:0,
	_dumptimerid:0,
	load(){
		this.super$load();
		this.plantSprite = [Core.atlas.find(this.name+"-spore1"),Core.atlas.find(this.name+"-spore2"),Core.atlas.find(this.name+"-spore3")];
		this.bottom  = [Core.atlas.find(this.name+"-ground1"),Core.atlas.find(this.name+"-ground2"),Core.atlas.find(this.name+"-ground3")];
		this._timerid = this.timers++;
		this._dumptimerid = this.timers++;
	},
	getTimerId(){
		return this._timerid;
	},
	getDTimerId(){
		return this._dumptimerid;
	}
});
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
			let ctile = Vars.world.tile(Math.floor(this.tile.x+Mathf.range(2)),Math.floor(this.tile.y+Mathf.range(2)));
			if(!ctile || ctile.bc()){return false;}
			Call.setTile(ctile, sporeFarm, this.team, 0);
			return true;
		},
		updateTile(){
			
			if(this.timer.get(this.block.getTimerId(), (60+this.delay)*5.0 )){
				if(this.delay==-1){
					this.delay =  (this.tile.x*89+this.tile.y*13)%21;
				}else{		
					this.growth+=this.randomChk()?(this.growth>1.8?0.1:0.3):-0.1;
					if(this.growth>=3){
						this.growth=2;
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
			if(this.timer.get(this.block.getDTimerId(),15)){
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
