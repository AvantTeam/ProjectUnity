const hover_AI = () => extend(AIController, {
    updateMovement(){
        // find core
        var core = this.unit.closestEnemyCore();
        // if we have core our target core
        if(core != null && this.unit.within(core, this.unit.range() / 1.1 + core.block.size * Vars.tilesize / 2)){
            this.target = core;
        } 
        // else just mve to near enemy spawn
        if((core == null || !this.unit.within(core, this.unit.range() * 0.5)) && this.command() == UnitCommand.attack){
            // enable movement
            this.move = true;
            
            // if we have target look  at them
            if(this.target) {
                this.unit.lookAt(this.target);
            }

            // if we move
            if(this.move) {
                // we enable pathfind to move point
                this.pathfind(Pathfinder.fieldCore);
                // and if we have target in our radius
                if (this.target && this.unit.type.weapons.first().rotate) {
                    // we go way on max radius from target to this unit
                    this.moveTo(this.target, this.unit.range() * 0.8);
                    // and look at unit
                    this.unit.lookAt(this.target);
                }
            };
        }
        // if command operation wait, we wait lol
        if(this.command() == UnitCommand.rally){
            // find rally
            var target = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);
            // pathfind to target
            if(target != null && !this.unit.within(target, 70)){
                this.pathfind(Pathfinder.fieldRally);
            }
        }
        // if unit have boost
        if(this.unit.type.canBoost && !this.unit.onSolid()){
            this.unit.elevation = Mathf.approachDelta(this.unit.elevation, 0, 0.08);
        }
        // if command attack and target instanceof Teamc
        if (this.command() == UnitCommand.attack && this.target instanceof Teamc) {
            // if target not invalid
            if(!Units.invalidateTarget(this.target, this.unit, this.unit.range()) && this.unit.type.rotateShooting){
                // and we have weapons
                if(this.unit.type.hasWeapons()){
                    // rotate to target
                    this.unit.lookAt(Predict.intercept(this.unit, this.target, this.unit.type.weapons.first().bullet.speed));
                }
                // else moving on our velocity
            }else if(this.unit.moving()){
                this.unit.lookAt(this.unit.vel.angle());
            }
        }

    }
});

// export hoverbase
module.exports = {
    // extend operation
    extend(entity, name, obj, obju){
        // if not obj or obju ( object update ) made {}
        if(typeof(obj) === "undefined") obj = {};
        if(typeof(obju) === "undefined") obju = {};

        // assign functions in obj
        obj = Object.assign({
        }, obj, {
            // @Overdrive load 
            load() {
                this.super$load();
                // set to zero to not draw and get hover layer
                this.legCount = 0;
                // ground layer
                this.groundLayer = Layer.legUnit;
                // config
                this.hovering = true;
                this.allowLegStep = true;
                this.flying = false;
            },
            // @Overdrive draw
            draw(unit) {
                // mix with hit time
                Draw.mixcol(Color.white, unit.hitTime / unit.hitDuration);

                // z layer
                var z = 0.7 > 0.5 ? (this.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : this.groundLayer + Mathf.clamp(this.hitSize / 4000, 0, 0.01);

                // Draw shadow
                Draw.z(Math.min(Layer.darkness, z - 1));
                
                // shadow block
                Draw.color(Pal.shadow);
                // idk just E go brr brr
                var e = Math.max(unit.elevation, 0.3);
                Draw.rect(this.shadowRegion, unit.x + this.shadowTX * e, unit.y + this.shadowTY * e, unit.rotation - 90); 
                Draw.color();
                // Draw shadow ended

                // Draw layer
                Draw.z(z);
                // Draw region
                this.drawBody(unit);

                // UnitType methods
                this.drawOutline(unit);
                this.drawWeapons(unit);
                // Region ended

                // Draw mix ended
                Draw.mixcol();
                // Draw layer ended
            }
            //Time.time()
        });

        // create content unit
        var copter = extendContent(UnitType, name, obj);
        // set AI
        copter.defaultController = hover_AI;
        // assign obju
        obju = Object.assign(obju, {
        });
        // create constructor for unit
        copter.constructor = () => {
            // create unit extends entity
            var unit = extend(entity, obju);
            // return this
            return unit;
        };
        // return module unit
        return copter;
    }
};