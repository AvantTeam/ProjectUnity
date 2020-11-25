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

const craftFx = new Effect(10, e => {
      Draw.color(Pal.accent, Color.gray, e.fin());
      Lines.stroke(1);
      Lines.spikes(e.x, e.y, e.fin() * 4, 1.5, 6);
});

const steelsmelter = extendContent(GenericSmelter, "steel-smelter", {});

steelsmelter.craftEffect = craftFx;

steelsmelter.buildType = () => extendContent(GenericSmelter.SmelterBuild, steelsmelter, {
    draw(){
        Draw.rect(steelsmelter.region, this.x, this.y);
        if(this.warmup > 0){
            Draw.color(1, 1, 1, this.warmup * Mathf.absin(Time.time(), 8, 0.6));
            Draw.rect(solidifier.topRegion, this.x, this.y);
            Draw.color();
        }
    }
});


const lavaColor = Color.valueOf("ff2a00");
const lavaColor2 = Color.valueOf("ffcc00");
const meltFx = new Effect(60, e => {
    Draw.color(Color.gray, Color.clear, e.fin());
    Angles.randLenVectors(e.id, 15, 4.5 + e.fin() * 16, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 0.2 + e.fin() * 5);
    });

    Draw.color(lavaColor, lavaColor2, e.fout());

    Angles.randLenVectors(e.id + 1, 8, 3 + e.fin() * 7, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 0.2 + e.fout() * 1.6);
    });
});
const smokePersistFx = new Effect(60, e => {
    Draw.color(Color.gray, Color.clear, e.fin());
    Angles.randLenVectors(e.id, 1, 4 + e.fin() * 4, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 0.2 + e.fin() * 4);
    });
});
const liquifyColor = Color.valueOf("ff9f11");
const liquifyFx = new Effect(300, e => {
      Draw.color(Color.white, liquifyColor, Math.min(1, e.fin() * 3));
      Draw.alpha(e.fout());
      Draw.rect(lavasmelter.region, e.x, e.y, 8 + Math.min(2, 4*e.fin()), 8 + Math.min(2, 4*e.fin()));
});
liquifyFx.layer = Layer.blockUnder;

const lavasmelter = clib.extend(GenericSmelter, GenericSmelter.SmelterBuild, "lava-smelter", {
    expUse: 4,
    expCapacity: 30,
    ignoreExp: true,

    init(){
        this.super$init();
        this.lava = this.consumes.get(ConsumeType.liquid).liquid;
    }
}, {
    draw(){
        Draw.rect(lavasmelter.region, this.x, this.y);
        if(this.warmup > 0){
            Draw.color(lavasmelter.lava.color, this.liquids.get(lavasmelter.lava) / lavasmelter.liquidCapacity);
            Draw.rect(solidifier.topRegion, this.x, this.y);
            Draw.color();
        }
    },
    lackingExp(amount){
        if(!Vars.net.active()) this.melt();
        else if(!Vars.net.client()) this.configureAny(null);
    },
    configured(unit, value){
        //print("ow");
        if(unit == null) this.melt();
    },
    melt(){
        Puddles.deposit(this.tile, lavasmelter.lava, this.liquids.get(lavasmelter.lava) * 10);
        if(!Vars.headless){
            meltFx.at(this.x, this.y);
            liquifyFx.at(this.x, this.y);
            Sounds.splash.at(this.x, this.y, Mathf.random() * 0.2 + 0.3);
            this.keepsmoke(this.x, this.y);
        }
        this.tile.remove();
        this.remove();
    },
    keepsmoke(x, y){
        for(var i=0; i<10; i++){
            Time.run(Mathf.random() * 250 + 30, () => {
                smokePersistFx.at(x, y);
            });
        }
    }
});

lavasmelter.saveConfig = false;

lavasmelter.liquidCapacity = 20;
lavasmelter.flameColor = lavaColor;
lavasmelter.updateEffect = Fx.fuelburn;
lavasmelter.craftEffect = craftFx;

const diriumColor = Color.valueOf("96f7c3");
const diriumFx = new Effect(10, e => {
      Draw.color(Color.white, diriumColor, e.fin());
      Lines.stroke(1);
      Lines.spikes(e.x, e.y, e.fin() * 4, 1.5, 6);
});

const diriumcrucible = clib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "dirium-crucible", {
    expUse: 30,
    expCapacity: 120,

    load(){
        this.super$load();
        this.expRegion = Core.atlas.find(this.name + "-top");
        this.topRegion = solidifier.topRegion;
    }
}, {
    drawLight(){
        Drawf.light(this.team, this, 25 + 25 * this.expf(), expColor, 0.8 * this.expf());
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
