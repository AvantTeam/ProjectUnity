const clone = obj => {
    if(obj === null || typeof(obj) !== 'object') return obj;
    var copy = obj.constructor();
    for(var attr in obj) {
        if(obj.hasOwnProperty(attr)) {
            copy[attr] = obj[attr];
        }
    };
    return copy;
}
module.exports = {
    //for merge - lib
    extendBody(type, name, obj) {
        if(obj == undefined) obj = {};
        obj = Object.assign({
            //start
            //strength of light that is needed
            lightStrength: 60,
            //range of light this block accepts
            lightFilter: Color.white,
            //whether light status exceeds 1
            scaleStatus: false,
            lightOptional: false
            //end
        }, obj, {
            //start
            setBars() {
                this.super$setBars();
                this.bars.add("light", func(build => {
                    return new Bar(prov(() => Core.bundle.format("lightlib.light", build.lightPower())), prov(() => build.lightSumColor()), floatp(() => {
                        return build.lastLightStatus();
                    }));
                }));
            },
            setStats() {
                this.super$setStats();
                this.stats.add(BlockStat.output, Core.bundle.format("bar.efficiency", 6000 / this.lightStrength));
            },
            consumesLight() {
                return true;
            }
            //end
        });
        //print("Created Block: " + Object.keys(obj));
        return obj;
    },
    extend(type, build, name, obj, objb) {
        obj = this.extendBody(type, name, obj);
        if(objb == undefined) objb = {};
        const lightblock = extendContent(type, name, obj);
        //lightblock.hasCustomUpdate = (typeof objb["customUpdate"] === "function");
        //lightblock.hasCustomRW = (typeof objb["customRead"] === "function");
        objb = clone(objb);
        objb = Object.assign(objb, {
            //angle strengthPercentage lengthleft color
            _src: [],
            _srcStr: [],
            _lastLightPower: 0,
            _lastColor: Color.black.cpy(),
            removeSource(source) {
                var index = this._src.indexOf(source);
                if(index >= 0) {
                    this._src.splice(index, 1);
                    this._srcStr.splice(index, 1);
                }
            },
            addSource(sarr) {
                var index = this._src.indexOf(sarr[0]);
                if(index >= 0) {
                    this._srcStr[index][1] += sarr[1][1];
                    this._srcStr[index][3] = this._srcStr[index][3].cpy().add(sarr[1][3].cpy().mul(sarr[1][1] / 100));
                }
                else {
                    this._src.push(sarr[0]);
                    this._srcStr.push([sarr[1][0], sarr[1][1], sarr[1][2], sarr[1][3].cpy().mul(sarr[1][1] / 100)]);
                }
                //this._cachePower += sarr[0].lightPower() * (sarr[1]/100);
            },
            validateSource() {
                for(var i = 0; i < this._src.length; i++) {
                    if(!this._src[i].isValid()) {
                        this._src.splice(i, 1);
                        this._srcStr.splice(i, 1);
                        i--;
                    }
                }
            },
            lightPower() {
                this._lastLightPower = 0;
                this.validateSource();
                for(var i = 0; i < this._src.length; i++) {
                    this._lastLightPower += this._src[i].lightPower() * (this._srcStr[i][1] / 100);
                }
                return this._lastLightPower;
            },
            lastPower() {
                return this._lastLightPower;
            },
            lightStatus() {
                var ret = this.lightPower() / lightblock.lightStrength;
                if(!lightblock.scaleStatus) ret = Math.min(ret, 1);
                return ret;
            },
            lastLightStatus() {
                var ret = this._lastLightPower / lightblock.lightStrength;
                if(!lightblock.scaleStatus) ret = Math.min(ret, 1);
                return ret;
            },
            lightSumColor() {
                this._lastColor = Color.black.cpy();
                this.validateSource();
                for(var i = 0; i < this._src.length; i++) {
                    this._lastColor.add(this._srcStr[i][3].cpy().mul( /*this._src[i].lpowerf()**/ this._srcStr[i][1] / 100));
                    if(this._lastColor.equals(Color.white)) break;
                }
                if(!this._lastColor.equals(Color.black)) this._lastColor = this._lastColor.shiftValue(1 - this._lastColor.value());
                return this._lastColor;
            },
            lastSumColor() {
                return this._lastColor;
            },
            //utility methods - use AFTER UPDATE
            updateCons() {
                this.lightPower();
            },
            consValid() {
                return this.cons.valid() && (lightblock.lightOptional || this._lastLightPower >= lightblock.lightStrength);
            },
            consOptionalValid() {
                return this.cons.optionalValid() && (!lightblock.lightOptional || this._lastLightPower >= lightblock.lightStrength);
            }
        });
        //Extend Building
        lightblock.buildType = ent => {
            ent = extendContent(build, lightblock, clone(objb));
            ent._src = [];
            ent._srcStr = [];
            ent._lastLightPower = 0;
            ent._lastColor = Color.black.cpy();
            return ent;
        };
        return lightblock;
    }
}
