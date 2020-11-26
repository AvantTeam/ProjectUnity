//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.
//type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.
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

const orblib = this.global.unity.exporb;
const Integer = java.lang.Integer;

module.exports = {
    extend(type, build, name, obj, objb){
        if(obj == undefined) obj = {};
        if(objb == undefined) objb = {};
        obj = Object.assign({
            //start
            unit: Units.dagger,
            craftTime: 100,

            //exp stuff cuz why not
            enableExp: false,
            expCapacity: 60,
            orbMultiplier: 1,
            orbRefund: 0.6,
            exp0Color: Color.valueOf("84ff00"),
            expMaxColor: Color.valueOf("90ff00"),
            //exp used every craft
            expUse: 10,
            //whether to ignore the amount the exp needed, and give consequences via lackingExp(lacking amount) instead
            ignoreExp: false,

            setBars() {
                this.super$setBars();
                if(!this.enableExp) return;
                this.bars.add("exp", func(build => {
                    return new Bar(prov(() => Core.bundle.get("explib.exp")), prov(() => Tmp.c1.set(this.exp0Color).lerp(this.expMaxColor, build.expf())), floatp(() => {
                        return build.expf();
                    }));
                }));
            },
            setStats(){
                this.super$setStats();
                if(!this.enableExp) return;
                this.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", this.expCapacity));
                this.stats.add(Stat.input, "@ [lightgray]@[]", Core.bundle.format("explib.expAmount", (this.expUse / this.craftTime) * 60), StatUnit.perSecond.localized());
            }
            //end
        }, obj);
        if(obj.enableExp){
            obj = Object.assign(obj, {
                hasExp(){
                    return true;
                }
            });
        }
        const padblock = extendContent(type, name, obj);
        padblock.update = true;
        padblock.solid = true;

        if(padblock.enableExp){
            objb = Object.assign({
                totalExp(){
                    return this._exp;
                },
                expf(){
                    return this._exp / padblock.expCapacity;
                },
                consumesOrb(){
                    return this.enabled && this._exp < padblock.expCapacity;
                },
                getOrbMuitiplier(){
                    return padblock.orbMultiplier;
                },
                incExp(amount){
                    this._exp = Math.min(this._exp + amount, padblock.expCapacity);
                    if(this._exp < 0) this._exp = 0;
                },

                onDestroyed(){
                    orblib.spreadExp(this.x, this.y, this.totalExp() * padblock.orbRefund, 3 * padblock.size);
                    this.super$onDestroyed();
                },
                consume(){
                    this.super$consume();
                    var remove = Math.min(padblock.expUse, this._exp);
                    this.incExp(-1 * remove);
                },
                consValid(){
                    return this.super$consValid() && this._exp >= padblock.expUse;
                }
            }, objb);
        }

        objb = Object.assign({
            read(stream, version) {
                this.super$read(stream, version);
                this._progress = stream.f();
                if(padblock.enableExp) this._exp = stream.i();
            },
            write(stream) {
                this.super$write(stream);
                stream.f(this._progress);
                if(padblock.enableExp) stream.i(this._exp);
            }
        }, objb);
        //Extend Building
        padblock.buildType = ent => {
            ent = extendContent(build, padblock, clone(objb));
            ent._progress = 0;
            if(padblock.enableExp) ent._exp = 0;
            return ent;
        };
        return padblock;
    }
}
