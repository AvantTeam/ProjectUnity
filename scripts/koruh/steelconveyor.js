const steelconv = extendContent(Conveyor, "steel-conveyor", {
    load(){
        this.super$load();
        steelconv.realSpeed = steelconv.speed;
    }
});

steelconv.buildType = () => extendContent(Conveyor.ConveyorBuild, steelconv, {
    draw(){
        steelconv.speed = steelconv.realSpeed / 2;
        this.super$draw();
        steelconv.speed = steelconv.realSpeed;
    }
})
