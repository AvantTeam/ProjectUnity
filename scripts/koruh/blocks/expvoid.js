const expvoid = extendContent(Block, "exp-void", {
    hasExp(){
        return true;
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
