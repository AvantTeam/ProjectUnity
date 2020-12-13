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

const Integer = java.lang.Integer;

module.exports = {
    extendBlock(name, obj, objb){
        //base block is always router, because router. (just kidding, it is because routers can be controlled)
        if(obj == undefined) obj = {};
        if(objb == undefined) objb = {};
        obj = Object.assign({
            //start
            unit: UnitTypes.dagger,
            craftTime: 100,
            setStats(){
                this.super$setStats();
                //TODO
            },
            canReplace(other){
                //TODO
                return other.alwaysReplace;
            },
            load() {
                this.super$load();
                this.arrowRegion = Core.atlas.find("transfer-arrow");
            }
            //end
        }, obj);

        const padblock = extendContent(Router, name, obj);
        padblock.update = true;
        padblock.solid = false;
        padblock.hasItems = false;
        padblock.ambientSound = Sounds.respawn;
        padblock.ambientSoundVolume = 0.08;
        padblock.configurable = true;
        //TODO: set group to null

        objb = Object.assign({
            canControl(){
                return false;
            },
            acceptItem(source, item){
                return false;
            },
            handleItem(source, item){
                return;
            },
            removeStack(item, amount){
                return 0;
            },
            inRange(player){
                return player.unit() != null && !player.unit().dead && Math.abs(player.unit().x - this.x) <= 2.5 * Vars.tilesize && Math.abs(player.unit().y - this.y) <= 2.5 * Vars.tilesize;
            },
            drawSelect(){
                Draw.color(this.consValid() ? (this.inRange(Vars.player) ? Color.orange : Pal.accent) : Pal.darkMetal);
                var length = Vars.tilesize * padblock.size / 2 + 3 + Mathf.absin(Time.time, 5, 2);

                Draw.rect(padblock.arrowRegion, this.x + length, this.y, (0 + 2) * 90);
                Draw.rect(padblock.arrowRegion, this.x, this.y + length, (1 + 2) * 90);
                Draw.rect(padblock.arrowRegion, this.x + -1 * length, this.y, (2 + 2) * 90);
                Draw.rect(padblock.arrowRegion, this.x, this.y + -1 * length, (3 + 2) * 90);

                Draw.color();
            },
            shouldShowConfigure(player){
                return this.consValid() && this.inRange(Vars.player);
            },

            getUnit(){
                //stupid rhino
                if(this.unit == null){
                    this.unit = UnitTypes.block.create(this.team);
                    this.unit.tile(this);
                }
                return this.unit;
            },

            configTapped(){
                if(!this.consValid() || !this.inRange(Vars.player)) return false;
                this.configure(null);
                //Sounds.click.at(this);
                return false;
            },
            configured(unit, value){
                if(unit != null && unit.isPlayer() && !(unit instanceof BlockUnitc)){
                    this._time = 0;
                    this._revert = unit.type == padblock.unit;
                    if(!Vars.net.client()){
                        unit.getPlayer().unit(this.getUnit());
                    }
                }
            },

            shouldAmbientSound(){
                return this.inProgress();
            },

            updateTile(){
                if(this.inProgress()){
                    this._time += this.edelta() * (this.consValid() ? 1 : 0) * Vars.state.rules.unitBuildSpeedMultiplier;
                    if(this._time > padblock.craftTime) this.finishUnit();
                }
                this._heat = Mathf.lerpDelta(this._heat, this.inProgress() ? 1 : 0, 0.1);
            },
            getResultUnit(){
                return this._revert ? UnitTypes.alpha : padblock.unit;
            },
            inProgress(){
                return this.unit != null && this.isControlled();
            },
            finishUnit(){
                var player = this.unit.getPlayer();
                if(player == null) return;
                Fx.spawn.at(this);

                if(!Vars.net.client()){
                    var unit = this.getResultUnit().create(this.team);
                    unit.set(this);
                    unit.rotation = 90;
                    unit.impulse(0, 3);
                    unit.set(this.getResultUnit(), player);
                    unit.spawnedByCore = true;
                    unit.add();
                }

                if(Vars.state.isCampaign() && player == Vars.player){
                    this.getResultUnit().unlock();
                }
                this.consume();
                this._time = 0;
                this._revert = false;
            },
            draw(){
                this.super$draw();
                if(!this.inProgress()) return;
                var progress = Mathf.clamp(this._time / padblock.craftTime);

                Draw.color(Pal.darkMetal);
                Lines.stroke(2 * this._heat);
                Fill.poly(this.x, this.y, 4, 10 * this._heat);
                Draw.reset();
                var region = this.getResultUnit().icon(Cicon.full);

                //Draw.rect(from, this.x, this.y);
                Draw.color(0, 0, 0, 0.4 * progress);
                Draw.rect("circle-shadow", this.x, this.y, region.width / 3, region.width / 3);
                Draw.color();
                Draw.draw(Layer.blockOver, () => {
                    try{
                        Drawf.construct(this.x, this.y, region, 0, progress, Vars.state.rules.unitBuildSpeedMultiplier, this._time);
                        Lines.stroke(this._heat, Pal.accentBack);
                        var pos = Mathf.sin(this._time, 6, 8);
                        Lines.lineAngleCenter(this.x + pos, this.y, 90, 16 - Math.abs(pos) * 2);
                        Draw.color();
                    }
                    catch(ignore){
                        //why.
                    }
                });

                Lines.stroke(1.5 * this._heat);
                Draw.color(Pal.accentBack);
                Lines.poly(this.x, this.y, 4, 8 * this._heat);

                var oy = -7, len = 6 * this._heat;
                Lines.stroke(5);
                Draw.color(Pal.darkMetal);
                Lines.line(this.x - len, this.y + oy, this.x + len, this.y + oy, false);

                Fill.tri(this.x + len, this.y + oy - Lines.getStroke()/2, this.x + len, this.y + oy + Lines.getStroke()/2, this.x + (len + Lines.getStroke() * this._heat), this.y + oy);
                Fill.tri(this.x + len * -1, this.y + oy - Lines.getStroke()/2, this.x + len * -1, this.y + oy + Lines.getStroke()/2, this.x + (len + Lines.getStroke() * this._heat) * -1, this.y + oy);

                Lines.stroke(3);
                Draw.color(Pal.accent);
                Lines.line(this.x - len, this.y + oy, this.x - len+ len*2*progress, this.y + oy, false);

                Fill.tri(this.x + len, this.y + oy - Lines.getStroke()/2, this.x + len, this.y + oy + Lines.getStroke()/2, this.x + (len + Lines.getStroke() * this._heat), this.y + oy);
                Fill.tri(this.x + len * -1, this.y + oy - Lines.getStroke()/2, this.x + len * -1, this.y + oy + Lines.getStroke()/2, this.x + (len + Lines.getStroke() * this._heat) * -1, this.y + oy);

                Draw.reset();
            },

            read(stream, version) {
                this.super$read(stream, version);
                this._time = stream.i();
                this._revert = stream.bool();
            },
            write(stream) {
                this.super$write(stream);
                stream.i(this._time);
                stream.bool(this._revert);
            }
        }, objb);
        //Extend Building
        padblock.buildType = ent => {
            ent = extendContent(Router.RouterBuild, padblock, clone(objb));
            ent._time = 0;
            ent._heat = 0;
            ent._revert = false;
            return ent;
        };
        return padblock;
    }
}
