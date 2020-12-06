const orblib = this.global.unity.exporb;
const expColor = Color.valueOf("84ff00");
const expMaxColor = Color.valueOf("90ff00");
const d4x = [1, 0, -1, 0], d4y = [0, 1, 0, -1];

const exphub = extendContent(Block, "exp-output", {
    load(){
        this.super$load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", this.expCapacity));
        this.stats.add(Stat.output, "@", Core.bundle.format("explib.hubPercent", exphub.unloadAmount * 100));
    },
    setBars() {
        this.super$setBars();
        this.bars.add("exp", func(build => {
            return new Bar(prov(() => Core.bundle.get("explib.exp")), prov(() => Tmp.c1.set(expColor).lerp(expMaxColor, build.expf())), floatp(() => {
                return build.expf();
            }));
        }));
    },
    noOrbCollision(){
        return true;
    },
    expCapacityf(){
        return this.expCapacity;
    }
});
exphub.update = true;
exphub.solid = true;
exphub.timers = 1;
exphub.unloadAmount = 0.4;
exphub.unloadTime = 20;
exphub.expCapacity = 100;

exphub.buildType = () => extend(Building, {
    _join: [false, false, false, false],
    _conv: -1,
    _exp: 0,
    _warmup: 0,
    totalExp(){
        return this._exp;
    },
    expf(){
        return this._exp / exphub.expCapacity;
    },
    incExp(amount){
        this._exp = Math.min(this._exp + amount, exphub.expCapacity);
        if(this._exp < 0) this._exp = 0;
    },
    isFull(){
        return !this.enabled || !this.consValid() || this._exp >= exphub.expCapacity;
    },
    getPercent(){
      return exphub.unloadAmount;
    },
    read(stream, version) {
        this.super$read(stream, version);
        this._exp = stream.i();
    },
    write(stream) {
        this.super$write(stream);
        stream.i(this._exp);
    },
    onProximityUpdate(){
        this.super$onProximityUpdate();
        this._conv = -1;
        for(var i=0; i<4; i++){
            var build = this.nearby(i);
            if(build != null && build.isHubbable && build.interactable(this.team) && (build.getHub() == null || build.getHub() == this)){
                build.setHub(this);
                this._join[i] = true;
            }
            else this._join[i] = false;

            if(build != null && build.isValid() && build.interactable(this.team) && (build.block instanceof Conveyor)) this._conv = i;
        }
    },
    draw(){
        this.super$draw();

        if(!this.consValid()) return;
        Draw.blend(Blending.additive);
        Draw.color(Color.white);
        Draw.alpha(Mathf.absin(Time.time, 20, 0.4));
        Draw.rect(exphub.topRegion, this.x, this.y);
        Draw.blend();
        if(this._warmup > 0.001){
            Draw.color(expColor);
            Draw.z(Layer.effect);
            Lines.stroke(this._warmup * Mathf.absin(Time.time, 20, 1.2));
            for(var i=0; i<4; i++){
                if(this._join[i]) Lines.lineAngleCenter(this.x + 4*d4x[i], this.y + 4*d4y[i], i * 90 + 90, 8);
            }
        }
        Draw.reset();
    },
    updateTile(){
        this.super$updateTile();
        this._warmup = Mathf.lerpDelta(this._warmup, this.consValid() ? 1 : 0, 0.05);
        if(this._conv != -1 && this.consValid() && this.enabled && this._exp >= 10 && this.timer.get(0, 20)){
            orblib.spewExp(this.x, this.y, 1, this._conv * 90, 6);
            this._exp -= 10;
        }
    }
    /*checkUnload(dir){
        var build = this.nearby(dir);
        if(build == null || !build.isValid() || !build.incExp){
            this._join[dir] = false;
            return;
        }
        for(var j=0; j<2; j++){
            if(build.totalExp() >= orblib.expAmount){
                build.incExp(-1 * orblib.expAmount);
                orblib.spewExp(this.x, this.y, 1, dir * 90 + 180, 8);
            }
            else break;
        }
    }*/
});

// orblib.spewExp(this.x, this.y, 100, 6);
