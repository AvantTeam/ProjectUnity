const expvoid = extendContent(Block, "exp-void", {
    hasExp(){
        return true;
    },
    expCapacityf(){
        return 99999999;//heh
    }
});
expvoid.update = true;
expvoid.solid = true;
expvoid.buildType = () => extend(Building, {
    totalExp(){
        return 0;
    },
    consumesOrb(){
        return this.enabled;
    },
    getOrbMuitiplier(){
        return 1;
    },
    incExp(amount){
        return;
    }
});
