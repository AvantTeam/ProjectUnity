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
    },
    init(){
        this.super$init();
        this.liquid1 = this.consumes.get(ConsumeType.liquid);
    }
});

solidifier.liquid2 = {
    liquid: Liquids.water,
    amount: 0.1
};

const rockFx = new Effect(10, e => {
      Draw.color(Color.orange, Color.gray, e.fin());
      Lines.stroke(1);
      Lines.spikes(e.x, e.y, e.fin() * 4, 1.5, 6);
});

solidifier.liquidCapacity = 12;
solidifier.flameColor = Color.valueOf("FFB096");
solidifier.updateEffect = Fx.fuelburn;
solidifier.craftEffect = rockFx;

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
            Draw.color(solidifier.liquid1.liquid.color, this.liquids.get(solidifier.liquid1.liquid) / solidifier.liquidCapacity);
            Draw.rect(solidifier.topRegion, this.x, this.y);
            Draw.color();
        }
    }
});


const clib = this.global.unity.expcrafter;
const diriumColor = Color.valueOf("96f7c3");
const diriumFx = new Effect(10, e => {
      Draw.color(Color.white, diriumColor, e.fin());
      Lines.stroke(1);
      Lines.spikes(e.x, e.y, e.fin() * 4, 1.5, 6);
});

const diriumcrucible = clib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "dirium-crucible", {
    expUse: 15,
    expCapacity: 100,

    load(){
        this.super$load();
        this.expRegion = Core.atlas.find(this.name + "-top");
        this.topRegion = solidifier.topRegion;
    }
}, {
    drawLight(){
        Drawf.light(this.team, this, 25 + 25 * this.expf(), expColor, 0.5 * this.expf());
    },
    draw(){
        this.super$draw();//
        if(this.warmup > 0){
            Draw.color(1, 1, 1, this.warmup * Mathf.absin(Time.time(), 8, 0.6));
            Draw.rect(solidifier.topRegion, this.x, this.y);
            Draw.blend(Blending.additive);
            Draw.color(diriumcrucible.exp0Color, this.warmup * Mathf.absin(Time.time(), 25, 0.3));
            Draw.rect(diriumcrucible.expRegion, this.x, this.y);
            Draw.blend();
        }
        Draw.color();
    }
});

diriumcrucible.craftEffect = diriumFx;
