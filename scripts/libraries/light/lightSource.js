//light source, does all the calculations at placement and when any block has been placed.
//calc method: first raytrace, then if updated check if block placement area(x, y) intersects with each straight line from the source(aka the theta is the same).

const clone = obj => {
	if(obj === null || typeof(obj) !== 'object')
	return obj;

	var copy = obj.constructor();

	for(var attr in obj){
		if(obj.hasOwnProperty(attr)){
			copy[attr] = obj[attr];
		}
	};

	return copy;
}

module.exports = {
	extend(type, build, name, obj, objb){
		if(obj == undefined) obj = {};
		if(objb == undefined) objb = {};
		obj = Object.assign({
			//start
      //The strength of light, used by consumers
			lightStrength: 60,
      //The distance light does before dissapating
      lightLength: 50,
      //The absolute distance a light can reach, regardless of lightRepeaters
      maxLightLength: 500,
      //the max reflections this light has, everything that affects this light is consided a reflection
      maxReflections: 50,
      lightColor: Color.white,
      //whether to scale lightStrength with the input power status
      scaleStatus: true,
      //whether to display angle configuration
      angleConfig: false,
      //defaults
      dirs: [[1,0], [1,1], [0,1], [-1,1], [-1,0], [-1,-1], [0,-1], [1,-1]],
      update: true,
      rotate: true
			//end
		}, obj, {
			//start
			setBars(){
				this.super$setBars();

				this.bars.add("light", func(build => {
					return new Bar(
						prov(() => Core.bundle.format("lightlib.light", build.lightPower())),
						prov(() => this.lightColor),
						floatp(() => {
							return build.lightPower() / this.lightStrength;
						})
					);
				}));
			}
			//end
		});
		print("Created Block: " + Object.keys(obj));
		const lightblock = extendContent(type, name, obj);
		lightblock.configurable = lightblock.angleConfig;
    lightblock.reflowTimer = lightblock.timers++;
    if(lightblock.angleConfig) lightblock.rotate = false;
    if(lightblock.size%2 == 0) print("[scarlet]Even - sized light blocks are not supported! Continue anyways?[]");

		//lightblock.hasLevelFunction = (typeof objb["levelUp"] === "function");
		lightblock.hasCustomUpdate = (typeof objb["customUpdate"] === "function");
    lightblock.hasCustomRW = (typeof objb["customRead"] === "function");

		objb = Object.assign(objb, {
      //angle strengthPercentage lengthleft color
      _lightData: [0, 100, lightblock.lightLength, lightblock.lightColor],
      //array of tiles
      _ls: [],
      //array of _lightData for each _ls
      _lsData: [],
      _lCons: [],
      //only used in angleConfig
      _angle: 0,
      _strength: 0,
      _lightInit: false,

      setInit(a){
        this._lightInit = a;
      },
      initDone(){
        return this._lightInit;
      },

      getAngleDeg(){
        return (lightblock.rotate)?this.rotDeg():this._angle*45;
      },
      getAngle(){
        return (lightblock.rotate)?this.rotation*2:this._angle;
      },
      setAngle(a){
        this._angle = a;
        this.lightMarchStart(lightblock.lightLength, lightblock.maxLightLength);
      },

      lightData(){
        return this._lightData;
      },
      setLightData(d){
        this._lightData = d;
      },
      setPower(a){
        this._strength = a;
      },

      lightPower(){
        if(!this._lightInit) return this.targetStrength();
        return this._strength;
      },

      targetStrength(){
        if(!this.cons.valid()) return 0;
        return (lightblock.scaleStatus)?lightblock.lightStrength * this.power.status:lightblock.lightStrength;
      },

			updateTile(){
        this.setPower(this.targetStrength());
        if(!this.initDone()) this.lightMarchStart(lightblock.lightLength, lightblock.maxLightLength);
        else if(this.timer.get(lightblock.reflowTimer, 30) && this.lightPower() > 1) this.lightMarchStart(lightblock.lightLength, lightblock.maxLightLength);

				if(lightblock.hasCustomUpdate) this.customUpdate();
				else this.super$updateTile();
			},

			read(stream, version){
				this.super$read(stream, version);
				this._angle = stream.b();
        if(lightblock.hasCustomRW) this.customRead(stream, version);
			},
			write(stream){
				this.super$write(stream);
				stream.b(this._angle);
        if(lightblock.hasCustomRW) this.customWrite(stream);
			},

      drawLight(){
        //TODO make light draw on beams
        Drawf.light(this.team, this.x, this.y, (this.lightPower()*0.1 + 60)*this.power.status, lightblock.lightColor, 0.8);
      },

      drawLightLasers(){
        if(this == null || !this.isAdded() || this.lightPower() <= 1) return;
        Draw.z(Layer.effect - 1);
        Draw.blend(Blending.additive);

        //var now = null;
        //var next = null;
        for(var i=0; i<this._ls.length; i++){
          if(this._lsData[i] == null) continue;
          //print("Drawing Data: "+this._lsData[i]);
          var a = this._lsData[i][1]/100*(this.lightPower()/lightblock.lightStrength);
          Draw.color(this._lsData[i][3], a);
          if(Core.settings.getBool("bloom")) Draw.z((a>0.99)?(Layer.effect - 1):(Layer.bullet - 2));
          Lines.stroke(1 + Math.min(this.lightPower()/1000, 10));
          //now = this._ls[i];
          //next = this._ls[i+1];
          if(i == this._ls.length - 1 || this._ls[i+1] == null){
            //I'm sorry. okay?
            Draw.alpha(a);
            Lines.lineAngle(this._ls[i].worldx(), this._ls[i].worldy(), this._lsData[i][0]*45, this._lsData[i][2]*Vars.tilesize);
            Draw.alpha(a*0.5);
            Lines.lineAngle(this._ls[i].worldx(), this._ls[i].worldy(), this._lsData[i][2]*Vars.tilesize, this._lsData[i][0]*45, 4);
            Draw.alpha(a*0.25);
            Lines.lineAngle(this._ls[i].worldx(), this._ls[i].worldy(), this._lsData[i][2]*Vars.tilesize + 4, this._lsData[i][0]*45, 2);
            Draw.alpha(a*0.125);
            Lines.lineAngle(this._ls[i].worldx(), this._ls[i].worldy(), this._lsData[i][2]*Vars.tilesize + 6, this._lsData[i][0]*45, 2);
          }
          else{
            if(this._lsData[i+1] == null){
              //obstructed
              Lines.line(this._ls[i].worldx(), this._ls[i].worldy(), this._ls[i+1].worldx() - 4 * lightblock.dirs[this._lsData[i][0]][0], this._ls[i+1].worldy() - 4 * lightblock.dirs[this._lsData[i][0]][1]);
            }
            else{
              //light go brrrrrrrr
              Lines.line(this._ls[i].worldx(), this._ls[i].worldy(), this._ls[i+1].worldx(), this._ls[i+1].worldy());
            }
          }
        }
        Draw.blend();
        Draw.color();
      },

      lightMarchStart(length, maxLength){
        //idk
        this._lightData[0] = this.getAngle();
        this._lightData[1] = 100;
        //TODO make it more efficient
        for(var i=0; i<this._lCons.length; i++){
          this._lCons[i].removeSource(this);
        }
        this._ls = [];
        this._lsData = [];
        this._lCons = [];
        this._ls.push(this.tile);
        this._lsData.push([this.getAngle(), 100, this._lightData[2], this._lightData[3]]);
        this.pointMarch(this.tile, this.lightData(), length, maxLength, 0, this);
        this.setInit(true);
        //print(this._ls.toString());
        //print(this._lsData.toString());
      },
      pointMarch(tile, ld, length, maxLength, num, source){
        if(length <= 0 || maxLength <= 0 || num > lightblock.maxReflections || ld[1]*source.lightPower() < 1) return;
        const dir = lightblock.dirs[ld[0]];
        var next = null;
        var next2 = null;
        var furthest = null;
        var i = 0;
        var hit = Vars.world.raycast(tile.x, tile.y, tile.x + length*dir[0], tile.y + length*dir[1], (x, y)=>{
          furthest = Vars.world.tile(x, y);
          if(furthest == tile || furthest == null) return false;
          i++;
          if(!furthest.solid()) return false;
          if(furthest.bc() == null) return true;
          if(furthest.bc().block.lightReflector){
            //print("Light reflector!");
            var tr = furthest.bc().calcReflection(ld[0]);
            if(tr >= 0) next = [tr, ld[1], ld[2] - i, ld[3]];
          }
          else if(furthest.bc().block.lightDivisor){
            var tr = furthest.bc().calcReflection(ld[0]);
            if(tr >= 0){
              next = [ld[0], ld[1] / 2, ld[2] - i, ld[3]];
              next2 = [tr, ld[1] / 2, ld[2] - i, ld[3]];
            }
          }
          else if(furthest.bc().block.lightRepeater){
            var tl = furthest.bc().calcLight(ld, i);
            if(tl == null) return true;
            next = [tl[0], tl[1], tl[2], tl[3]];
          }
          else if(furthest.bc().block.consumesLight){
            furthest.bc().addSource([source, ld[1]]);
            this._lCons.push(furthest.bc());
          }
          return true;
        });
        if(!hit) return;
        if(next == null){
          //the block hit is solid or a consumer
          this._ls.push(furthest);
          this._lsData.push(null);//obstructor
        }
        else if(next2 == null){
          //the block hit reflecc
          this._ls.push(furthest);
          this._lsData.push(next);//mirror
          this.pointMarch(furthest, next, ld[2]-i, maxLength-i, ++num, source);
        }
        else{
          //the light go S P L I T
          //TODO
          this._ls.push(furthest);
          this._lsData.push(next);//mirror
          this.pointMarch(furthest, next, ld[2]-i, maxLength-i, ++num, source);
          this._ls.push(null);//cheaty yep
          this._lsData.push(null);
          this._ls.push(furthest);
          this._lsData.push(next2);//mirror mirror on the wall
          this.pointMarch(furthest, next2, ld[2]-i, maxLength-i, ++num, source);
        }
      }
		});
		//Extend Building
		print("Prep Building: " + Object.keys(objb));
		lightblock.entityType = ent => {
			ent = extendContent(build, lightblock, clone(objb));
			ent._angle = 0;
      ent._lightInit = false;
      Events.run(Trigger.draw, () => {
        if(ent != null) ent.drawLightLasers();
      });
			return ent;

		};

		return lightblock;
	}
}
