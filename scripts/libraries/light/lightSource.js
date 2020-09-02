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
      lightColor: Color.white,
      //whether to scale lightStrength with the input power status
      scaleStatus: true,
      //whether to display angle configuration
      angleConfig: false,
      //defaults
      update: true,
      rotate: true,
      category: Category.logic
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
    if(lightblock.angleConfig) lightblock.rotate = false;
    if(lightblock.size%2 == 0) print("[scarlet]Even - sized light blocks are not supported! Continue anyways?[]");

		//lightblock.hasLevelFunction = (typeof objb["levelUp"] === "function");
		lightblock.hasCustomUpdate = (typeof objb["customUpdate"] === "function");

		objb = Object.assign(objb, {
      //angle strength lengthleft color
      _lightData: [0, 0, lightblock.lightLength, lightblock.lightColor],
      //array of tiles
      _ls: [],
      //array of _lightData for each _ls
      _lsData: [],
      //only used in angleConfig
      _angle: 0,
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
        return (lightblock.rotate)?this.rotation:this._angle;
      },
      setAngle(a){
        this._angle = a;
      },

      lightData(){
        return this._lightData;
      },
      setLightData(d){
        this._lightData = d;
      },
      setPower(a){
        this._lightData[1] = a;
      },

      lightPower(){
        if(!this._lightInit) return this.targetStrength();
        return this._lightData[0];
      },

      targetStrength(){
        if(!this.cons.valid()) return 0;
        return (lightblock.scaleStatus)?lightblock.lightStrength * this.power.status:lightblock.lightStrength;
      },

			updateTile(){
        this.setPower(this.targetStrength());
        if(!this.initDone()) this.lightMarchStart(lightblock.lightLength, lightblock.maxLightLength);

				if(lightblock.hasCustomUpdate) this.customUpdate();
				else this.super$updateTile();
			},

			read(stream, version){
				this.super$read(stream, version);
				this._angle = stream.b();
			},
			write(stream){
				this.super$write(stream);
				stream.b(this._angle);
			},

      lightMarchStart(length, maxLength){
        //idk
        this.setInit(true);
      }
		});
		//Extend Building
		print("Prep Building: " + Object.keys(objb));
		lightblock.entityType = ent => {
			ent = extendContent(build, lightblock, clone(objb));
			ent._angle = 0;
			return ent;
		};

		return lightblock;
	}
}
