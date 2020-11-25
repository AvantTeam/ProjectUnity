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
            expCapacity: 50,
            orbMultiplier: 1,
            orbRefund: 0.6,
            exp0Color: Color.valueOf("84ff00"),
            expMaxColor: Color.valueOf("90ff00"),
            //exp used every craft
            expUse: 5,
            //whether to ignore the amount the exp needed, and give consequences via lackingExp(lacking amount) instead
            ignoreExp: false,

            setBars() {
                this.super$setBars();
                this.bars.add("exp", func(build => {
                    return new Bar(prov(() => Core.bundle.get("explib.exp")), prov(() => Tmp.c1.set(this.exp0Color).lerp(this.expMaxColor, build.expf())), floatp(() => {
                        return build.expf();
                    }));
                }));
            },
            setStats(){
                this.super$setStats();
                this.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", this.expCapacity));
                this.stats.add(Stat.input, "@ [lightgray]@[]", Core.bundle.format("explib.expAmount", (this.expUse / this.craftTime) * 60), StatUnit.perSecond.localized());
            },
            hasExp(){
                return true;
            }
            //end
        }, obj);
        const expblock = extendContent(type, name, obj);
        expblock.update = true;
        expblock.solid = true;
        expblock.sync = true;

        objb = Object.assign({
            totalExp(){
                return this._exp;
            },
            expf(){
                return this._exp / expblock.expCapacity;
            },
            consumesOrb(){
                return this.enabled && this._exp < expblock.expCapacity;
            },
            getOrbMuitiplier(){
                return expblock.orbMultiplier;
            },
            incExp(amount){
                this._exp = Math.min(this._exp + amount, expblock.expCapacity);
                if(this._exp < 0) this._exp = 0;
            },
            read(stream, version) {
                this.super$read(stream, version);
                this._exp = stream.i();
            },
            write(stream) {
                this.super$write(stream);
                stream.i(this._exp);
            },
            onDestroyed(){
                orblib.spreadExp(this.x, this.y, this.totalExp() * expblock.orbRefund, 3 * expblock.size);
                this.super$onDestroyed();
            },
            consume(){
                this.super$consume();
                var remove = Math.min(expblock.expUse, this._exp)
                this.incExp(-1 * remove);
                if(remove < expblock.expUse - 0.2) this.lackingExp(expblock.expUse - remove);
            },
            consValid(){
                return this.super$consValid() && (expblock.ignoreExp || this._exp >= expblock.expUse);
            },
            lackingExp(amount){
                //
            }
        }, objb);
        //Extend Building
        expblock.buildType = ent => {
            ent = extendContent(build, expblock, clone(objb));
            ent._exp = 0;
            return ent;
        };
        return expblock;
    }
}
