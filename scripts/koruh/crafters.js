const solidifier = extendContent(GenericSmelter, "solidifier", {
    setBars(){
        this.super$setBars();
        this.bars.add("liquid2", func(build => {
            return new Bar(prov(() => (build.liquids.get(solidifier.liquid2.liquid) <= 0.001 ? Core.bundle.get("bar.liquid") : solidifier.liquid2.liquid.localizedName)), prov(() => solidifier.liquid2.liquid.color), floatp(() => {
                return build.getLiquidf();
            }));
        }));
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.input, solidifier.liquid2.liquid, solidifier.liquid2.amount * solidifier.craftTime, true);
    }
});

solidifier.liquid2 = {
    liquid: Liquids.water,
    amount: 0.1
};

solidifier.liquidCapacity = 12;
solidifier.flameColor = Color.valueOf("FFB096");

solidifier.buildType = () => extendContent(GenericSmelter.SmelterBuild, solidifier, {
    consValid(){
        return this.super$consValid() && this.liquids.get(solidifier.liquid2.liquid) >= this.useLiquid(solidifier.liquid2);
    },
    useLiquid(l2){
        return Math.min(l2.amount * this.edelta(), solidifier.liquidCapacity);
    },
    getLiquidf(){
        return this.liquids.get(solidifier.liquid2.liquid) / solidifier.liquidCapacity;
    },
    updateTile(){
        if(this.consValid()) this.liquids.remove(solidifier.liquid2.liquid, Math.min(this.useLiquid(solidifier.liquid2), this.liquids.get(solidifier.liquid2.liquid)));
        this.super$updateTile();
    },
    acceptLiquid(source, liquid){
        return this.super$acceptLiquid(source, liquid) || liquid == solidifier.liquid2.liquid;
    },
    shouldConsume(){
        return this.super$shouldConsume() && this.liquids.get(solidifier.liquid2.liquid) >= this.useLiquid(solidifier.liquid2);
    },
    draw(){
        Draw.rect(solidifier.region, this.x, this.y);
        if(this.warmup > 0){
            Draw.color(1, 1, 1, this.warmup * Mathf.absin(Time.time(), 8, 1));
            Draw.rect(solidifier.topRegion, this.x, this.y);
            Draw.color();
        }
    }
});


//does this work? no
//const solidifier = extendContent(GenericSmelter, "solidifier", {});
//solidifier.consumes.liquid(Liquids.water, 1);
