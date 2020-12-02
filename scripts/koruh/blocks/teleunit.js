const diriumColor = Color.valueOf("96f7c3");
const diriumColor2 = Color.valueOf("ccffe4");

const tpCoolDown = new StatusEffect("tpcooldown"); //empty effect for tp cooldown
tpCoolDown.color = diriumColor2;
tpCoolDown.effect = Fx.none;

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

const tpIn = new Effect(50, e => {
    if(!(e.data instanceof UnitType)) return;
    var region = e.data.icon(Cicon.full);
    Draw.color();
    Draw.mixcol(diriumColor, 1);
    Draw.rect(region, e.x, e.y, region.width * Draw.scl * e.fout(), region.height * Draw.scl * e.fout(), e.rotation);
    Draw.mixcol();
});

const tpFlash = new Effect(30, e => {
    if(!(e.data instanceof Unit) || e.data.dead) return;
    var region = e.data.type.icon(Cicon.full);
    Draw.mixcol(diriumColor2, 1);
    Draw.alpha(e.fout());
    Draw.rect(region, e.data.x, e.data.y, e.data.rotation - 90);
    Draw.mixcol();
    Draw.color();
});
tpFlash.layer = Layer.flyingUnit + 1;

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
teleunit.solid = false;
//teleunit.consumesTap = true;
teleunit.ambientSound = Sounds.techloop;
teleunit.ambientSoundVolume = 0.02;
teleunit.configurable = true;

//WHEN PORTING TO JAVA, PLZ MAKE IT TELEPORT PAYLOADS
//teleunit.outputsPayload = true;
//teleunit.outputFacing = false;

//const d4x = [1, 0, -1, 0], d4y = [0, 1, 0, -1];

teleunit.buildType = prov(() => extend(Building, {
    _warmup: 0,
    _warmup2: 0,
    updateTile(){
        this._warmup = Mathf.lerpDelta(this._warmup, this.consValid() ? 1 : 0, 0.05);
        this._warmup2 = Mathf.lerpDelta(this._warmup2, this.consValid() && this.enabled ? 1 : 0, 0.05);
    },
    draw() {
        this.super$draw();
        Draw.color(Color.white);
        Draw.alpha(0.45 + Mathf.absin(Time.time, 7, 0.26));
        Draw.rect(teleunit.topRegion, this.x, this.y);
        if(this._warmup >= 0.001){
            Draw.z(Layer.bullet);
            Draw.color(diriumColor, this.team.color, Mathf.absin(Time.time, 19, 1));
            Lines.stroke((Mathf.absin(Time.time, 62, 0.5) + 0.5) * this._warmup);
            Lines.square(this.x, this.y, 10.5, 45);
            if(this._warmup2 >= 0.001){
                Lines.stroke((Mathf.absin(Time.time, 62, 1) + 1) * this._warmup2);
                Lines.square(this.x, this.y, 8.5, Time.time / 2);
                Lines.square(this.x, this.y, 8.5, -1 * Time.time / 2);
            }
        }
        Draw.reset();
    },
    drawSelect(){
        Draw.color(this.consValid() ? (this.inRange(Vars.player) ? diriumColor : Pal.accent) : Pal.darkMetal);
        var length = Vars.tilesize * teleunit.size / 2 + 3 + Mathf.absin(Time.time, 5, 2);

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
            if(teleunit.buildId[tid] == undefined || teleunit.buildId[tid] == null || !teleunit.buildId[tid] || (!teleunit.buildId[tid].enabled && teleunit.buildId[tid] != this)) continue;
            barr.push(teleunit.buildId[tid]);
        }
        barr.sort(function(a, b) {
            return a.pos() - b.pos();
        });
        return barr;
    },
    inRange(player){
        return player.unit() != null && !player.unit().dead && Math.abs(player.unit().x - this.x) <= 2.5 * Vars.tilesize && Math.abs(player.unit().y - this.y) <= 2.5 * Vars.tilesize;
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
        if(unit != null && unit.isPlayer() && !(unit instanceof BlockUnitc)) this.tpPlayer(unit.getPlayer());
    },
    tpPlayer(player){
        this.tpUnit(player.unit(), player == Vars.player);
        if(Vars.player != null && player == Vars.player) Core.camera.position.set(player);
    },
    tpUnit(unit, isPlayer){
        var barr = this.getDestList();
        if(barr.length <= 0) return;
        var index = barr.indexOf(this);
        if(index < 0){
            print("Error! Origin pad not in list!");
        }
        index++;
        if(index >= barr.length) index = 0;
        var dest = barr[index];
        if(!Vars.headless) tpIn.at(unit.x, unit.y, unit.rotation - 90, Color.white, unit.type);
        unit.set(dest.x, dest.y);
        unit.snapInterpolation();
        unit.set(dest.x, dest.y);//for good measure

        if(!Vars.headless) this.effects(dest, unit.hitSize * 1.7, isPlayer, unit);
    },
    effects(dest, hitSize, isPlayer, unit){
        //TODO: EoD-style total unit effect
        if(isPlayer){
            Sounds.plasmadrop.at(dest.x, dest.y, Mathf.random() * 0.2 + 1);
            Sounds.lasercharge2.at(this.x, this.y, Mathf.random() * 0.2 + 0.7);
        }
        else{
            Sounds.plasmadrop.at(this.x, this.y, Mathf.random() * 0.2 + 1);
            Sounds.lasercharge2.at(dest.x, dest.y, Mathf.random() * 0.2 + 0.7);
        }
        tpOut.at(dest.x, dest.y, hitSize);
        tpFlash.at(dest.x, dest.y, 0, Color.white, unit);
    },
    unitOn(unit){
        if(!this.consValid()) return;
        if(unit.hasEffect(tpCoolDown) || unit.isPlayer()) return;
        this.tpUnit(unit, false);
        unit.apply(tpCoolDown, 120);
    },
    consValid(){
        //suppress considering enabled
        return this.power.status > 0.98;
    }
}));
