const steelconv = extendContent(Conveyor, "steel-conveyor", {
    load(){
        this.super$load();
        steelconv.realSpeed = steelconv.speed;
    },
    expConveyor(){
        return true;
    }
});

steelconv.buildType = () => extendContent(Conveyor.ConveyorBuild, steelconv, {
    draw(){
        steelconv.speed = steelconv.realSpeed * 1.9;
        this.super$draw();
        steelconv.speed = steelconv.realSpeed;
    }
})


const diriumconv = extendContent(Conveyor, "dirium-conveyor", {
    load(){
        this.super$load();
        diriumconv.realSpeed = diriumconv.speed;
    },
    expConveyor(){
        return true;
    }
});

diriumconv.buildType = () => extendContent(Conveyor.ConveyorBuild, diriumconv, {
    draw(){
        diriumconv.speed = diriumconv.realSpeed * 1.3;
        this.super$draw();
        diriumconv.speed = diriumconv.realSpeed;
    }
})
