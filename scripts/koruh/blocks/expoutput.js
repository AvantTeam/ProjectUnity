const orblib = this.global.unity.exporb;

const exphub = extendContent(Block, "exp-output", {
    load(){
        this.super$load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.output, "[#84ff00]In progress...[]", Core.bundle.format("explib.expAmount", exphub.unloadAmount * 10 * (60 / exphub.unloadTime)), StatUnit.perSecond.localized());
    },
    noOrbCollision(){
        return true;
    }
});
exphub.update = true;
exphub.solid = true;
exphub.timers = 1;
exphub.unloadAmount = 2;
exphub.unloadTime = 60;
exphub.buildType = () => extend(Building, {/*
    _join: [false, false, false, false],
    draw(){
        this.super$draw();
        for(var i=0; i<4; i++){
            if(this._join[i]) Draw.rect(exphub.sideRegion[i], this.x, this.y);
        }
        if(!this.consValid()) return;
        Draw.blend(Blending.additive);
        Draw.color(Color.white);
        Draw.alpha(Mathf.absin(Time.time, 20, 0.4));
        Draw.rect(exphub.topRegion, this.x, this.y);
        for(var i=0; i<4; i++){
            if(this._join[i]) Draw.rect(exphub.topRegion2, this.x, this.y, i*90);
        }
        Draw.blend();
        Draw.reset();
    },
    updateTile(){
        this.super$updateTile();
        if(this.consValid() && this.enabled && this.timer.get(0, 60)){
            for(var i=0; i<4; i++){
                if(this._join[i]) this.checkUnload(i);
            }
        }
    },
    checkUnload(dir){
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
    },
    onProximityUpdate(){
        this.super$onProximityUpdate();
        for(var i=0; i<4; i++){
            var build = this.nearby(i);
            this._join[i] = (build != null && build.isValid() && build.incExp && build.interactable(this.team));
        }
    }*/
});

// orblib.spewExp(this.x, this.y, 100, 6);
