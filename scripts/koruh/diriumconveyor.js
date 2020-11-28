const diriumconv = extendContent(Conveyor, "dirium-conveyor", {
    load(){
        this.super$load();
        diriumconv.realSpeed = diriumconv.speed;
    }
});

diriumconv.buildType = () => extendContent(Conveyor.ConveyorBuild, diriumconv, {
    draw(){
        diriumconv.speed = diriumconv.realSpeed / 2;
        this.super$draw();
        diriumconv.speed = diriumconv.realSpeed;
    }
})
