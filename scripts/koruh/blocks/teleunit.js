const diriumColor = Color.valueOf("96f7c3");

const tpOut = new Effect(30, e => {
    Draw.color(diriumColor);
    Lines.stroke(3*e.fout());
    Lines.square(e.x, e.y, e.finpow() * e.rotation, 45);
    Lines.stroke(5*e.fout());
    Lines.square(e.x, e.y, e.fin() * e.rotation, 45);
    Angles.randLenVectors(e.id, 10, e.fin() * (e.rotation + 10), (x, y) => {
        Fill.square(e.x + x, e.y + y, e.fout() * 4, 100*Mathf.randomSeed(e.id+1)*e.fin());
    });
});

const tpIn = new Effect(15, e => {
    Draw.color(diriumColor);
    Lines.stroke(3*e.fin());
    Lines.square(e.x, e.y, e.fout() *  e.rotation * 0.8, 45);
});

const teleunit = extendContent(Block, "teleunit", {
    load() {
        this.super$load();
        this.lightRegion = Core.atlas.find(this.name + "-lights");
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.arrowRegion = Core.atlas.find("transfer-arrow");
    },
    init(){
        this.super$init();
        this.buildId = [];
    }
});
teleunit.update = true;
teleunit.solid = true;
//teleunit.consumesTap = true;
teleunit.ambientSound = Sounds.techloop;
teleunit.ambientSoundVolume = 0.02;
teleunit.configurable = true;

teleunit.buildType = prov(() => extend(Building, {
    _warmup: 0,
    updateTile(){
        this._warmup = Mathf.lerpDelta(this._warmup, this.consValid() && this.enabled ? 1 : 0, 0.05);
    },
    draw() {
        this.super$draw();
        Draw.color(Color.white);
        Draw.alpha(0.45 + Mathf.absin(Time.time(), 7, 0.26));
        Draw.rect(teleunit.topRegion, this.x, this.y);
        if(this._warmup >= 0.001){
          Draw.z(Layer.bullet);
          Draw.color(diriumColor, this.team.color, Mathf.absin(Time.time(), 19, 1));
          Lines.stroke((Mathf.absin(Time.time(), 62, 0.5) + 0.5) * this._warmup);
          Lines.square(this.x, this.y, 10.5, 45);
          Lines.stroke((Mathf.absin(Time.time(), 62, 1) + 1) * this._warmup);
          Lines.square(this.x, this.y, 8.5, Time.time() / 2);
          Lines.square(this.x, this.y, 8.5, -1 * Time.time() / 2);
        }
        Draw.reset();
    },
    drawSelect(){
        Draw.color(this.consValid() && this.enabled ? Pal.accent : Pal.darkMetal);
        var length = Vars.tilesize * teleunit.size / 2 + 3 + Mathf.absin(Time.time(), 5, 2);

        Draw.rect(teleunit.arrowRegion, this.x + length, this.y, (0 + 2) * 90);
        Draw.rect(teleunit.arrowRegion, this.x, this.y + length, (1 + 2) * 90);
        Draw.rect(teleunit.arrowRegion, this.x + -1 * length, this.y, (2 + 2) * 90);
        Draw.rect(teleunit.arrowRegion, this.x, this.y + -1 * length, (3 + 2) * 90);

        Draw.color();
    },
    shouldAmbientSound(){
        return this.consValid();
    },
    created(){
        this.super$created();
        teleunit.buildId[this.id] = this;
    },
    onRemoved(){
        teleunit.buildId[this.id] = null;
        this.super$onRemoved();
    },
    getDestList(){
        var str = this.power.graph.toString();

        str = str.substring(str.indexOf("consumers={")+11,str.indexOf("batteries")-3);
        var arr = str.split(", ");
        //print("Arr: "+arr);
        //print("BId: "+teleunit.buildId);
        var barr = [];
        for(var i=0; i<arr.length;i++){
            var tid = arr[i].split("#")[1];
            //print("tid: "+tid);
            if(teleunit.buildId[tid] == undefined || teleunit.buildId[tid] == null || !teleunit.buildId[tid]) continue;
            barr.push(teleunit.buildId[tid]);
        }
        barr.sort(function(a, b) {
            return a.pos() - b.pos();
        });
        return barr;
    },
    inRange(player){
        return this.enabled && player.unit() != null && !player.unit().dead && Math.abs(player.unit().x - this.x) <= 1.8 * Vars.tilesize && Math.abs(player.unit().y - this.y) <= 1.7 * Vars.tilesize;
    },
    shouldShowConfigure(player){
        return this.consValid() && this.inRange(Vars.player);
    },
    configTapped(){
        if(!this.consValid() || !this.inRange(Vars.player)) return false;
        this.configure(null);
        Sounds.click.at(this);
        return false;
    },
    configured(unit, value){
        if(unit != null && unit.isPlayer()) this.tpPlayer(unit.getPlayer());
    },
    tpPlayer(player){
        var barr = this.getDestList();
        if(barr.length <= 0) return;
        //print(barr);
        var index = barr.indexOf(this);
        if(index < 0){
            print("Error! Origin pad not in list!");
        }
        index++;
        if(index >= barr.length) index = 0;
        var dest = barr[index];
        player.unit().set(dest.x, dest.y);
        player.unit().snapInterpolation();
        if(Vars.player != null && player == Vars.player) Core.camera.position.set(player);
        if(!Vars.headless) this.effects(dest, player.unit().hitSize * 1.7);
    },
    effects(dest, hitSize){
        Sounds.plasmadrop.at(this.x, this.y, Mathf.random() * 0.2 + 1);
        Sounds.plasmadrop.at(dest.x, dest.y, Mathf.random() * 0.2 + 0.7);
        tpOut.at(dest.x, dest.y, hitSize);
        tpIn.at(this.x, this.y, hitSize);
    }
}));
