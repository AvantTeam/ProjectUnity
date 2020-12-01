const orblib = this.global.unity.exporb;
const expColor = Color.valueOf("84ff00");
const expMaxColor = Color.valueOf("90ff00");

const exptank = extendContent(Block, "exp-tank", {
    load(){
        this.super$load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.expRegion = Core.atlas.find(this.name + "-exp");
    },
    hasExp(){
        return true;
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", this.expCapacity));
    },
    setBars() {
        this.super$setBars();
        this.bars.add("exp", func(build => {
            return new Bar(prov(() => Core.bundle.get("explib.exp")), prov(() => Tmp.c1.set(expColor).lerp(expMaxColor, build.expf())), floatp(() => {
                return build.expf();
            }));
        }));
    },
    expCapacityf(){
        return this.expCapacity;
    }
});
exptank.update = true;
exptank.sync = true;
exptank.solid = true;
exptank.expCapacity = 600;
exptank.buildType = () => extend(Building, {
    _exp: 0,
    totalExp(){
        return this._exp;
    },
    expf(){
        return this._exp / exptank.expCapacity;
    },
    draw(){
        Draw.rect(exptank.baseRegion, this.x, this.y);
        Draw.color(expColor, Color.white, Mathf.absin(Time.time, 20, 0.6));
        Draw.alpha(this._exp / exptank.expCapacity);
        Draw.rect(exptank.expRegion, this.x, this.y);
        Draw.color();
        Draw.rect(exptank.topRegion, this.x, this.y);
    },
    consumesOrb(){
        return this.enabled && this._exp < exptank.expCapacity;
    },
    getOrbMuitiplier(){
        return 1;
    },
    incExp(amount){
        this._exp = Math.min(this._exp + amount, exptank.expCapacity);
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
        orblib.spreadExp(this.x, this.y, this.totalExp() * 0.8, 3 * exptank.size);
        this.super$onDestroyed();
    },
    drawLight(){
        Drawf.light(this.team, this, 25 + 25 * this.expf(), expColor, 0.5 * this.expf());
    }
});
