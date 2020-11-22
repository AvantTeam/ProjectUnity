const orblib = this.global.unity.exporb;

const expfountain = extendContent(Block, "exp-fountain", {
    load(){
        this.super$load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    }
});
expfountain.update = true;
expfountain.solid = false;
expfountain.timers = 1;
expfountain.buildType = () => extend(Building, {
    draw(){
        this.super$draw();
        Draw.blend(Blending.additive);
        Draw.color(Color.yellow);
        Draw.alpha(Mathf.absin(Time.time(), 20, 0.6));
        Draw.rect(expfountain.topRegion, this.x, this.y);
        Draw.blend();
        Draw.reset();
    },
    updateTile(){
        this.super$updateTile();
        if(this.enabled && this.timer.get(0, 120)) orblib.spreadExp(this.x, this.y, 100, 6);
    }
});
