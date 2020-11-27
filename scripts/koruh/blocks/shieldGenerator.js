const lib = this.global.unity.exp;

const shieldBreakCircle = new Effect(40, e => {
    Draw.color(Pal.lancerLaser);
    Lines.stroke(3 * e.fout());
    Lines.circle(e.x, e.y, e.rotation + e.fin());
});

const shieldGenerator = lib.extend(ForceProjector, ForceProjector.ForceBuild, "shield-generator", {
    maxLevel: 15,
    expFields: [
        {
            type: "linear",
            field: "breakage",
            start: 400,
            intensity: 25
        }
    ],
    drawPlace(x, y, rotation, valid){
        var fin = (Time.globalTime() % 90) / 90;
        Draw.color(this.exp0Color);
        Lines.stroke(1.5 * (1 - fin));
        Lines.circle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.radius + fin * 1.5 * this.maxLevel);
        Draw.color(Pal.lancerLaser);
        Lines.stroke(1.5);
        Lines.circle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.radius);
        Draw.color();
    }
}, {
    buildingRadius: 40,
    created(){
        this.super$created();
        this.buildingRadius = shieldGenerator.radius;
    },
    customUpdate(){
        this.radscl = Mathf.lerpDelta(this.radscl, this.broken ? 0 : this.warmup, 0.05);
        this.warmup = Mathf.lerpDelta(this.warmup, this.efficiency(), 0.1);

        var scale = !this.broken ? shieldGenerator.cooldownNormal : shieldGenerator.cooldownBrokenBase;
        var cons = shieldGenerator.consumes.get(ConsumeType.liquid);

        if(this.buildup > 0){
            if(cons.valid(this)){
                cons.update(this);
                scale *= (shieldGenerator.cooldownLiquid * (1 + (this.liquids.current().temperature - 0.4) * 0.9));
            }
            this.buildup -= this.delta() * scale;
        }

        if(this.broken && this.buildup <= 0){
            this.broken = false;
        }

        if(this.buildup >= shieldGenerator.breakagem && !this.broken){
            this.broken = true;
            this.buildup = shieldGenerator.breakage;
            shieldBreakCircle.at(this.x, this.y, this.realRadius(), Pal.lancerLaser);
        }

        if(this.hit > 0){
            this.hit -= 1 / 5 * Time.delta;
        }

        const customConsumer = trait => {
            if(trait.team != this.paramEntity.team && trait.type.absorbable && Mathf.dst(this.paramEntity.x, this.paramEntity.y, trait.x, trait.y) <= this.realRadius()){
                trait.absorb();
                Fx.absorb.at(trait);
                this.paramEntity.hit = 1;
                this.paramEntity.buildup += trait.damage * this.paramEntity.warmup;
                this.incExp(cons.valid(this) ? scale / 20 : 0.1);
            }
        };
        var realRadius = this.realRadius();

        if(realRadius > 0 && !this.broken){
            this.paramEntity = this;
            Groups.bullet.intersect(this.x - realRadius, this.y - realRadius, realRadius * 2, realRadius * 2, customConsumer);
        }
    },
    draw(){
        this.super$draw();

        if(this.drawer != null){
            this.drawer.set(this.x, this.y);
        }

        if(this.buildup > 0){
            Draw.alpha(this.buildup / this.breakage * 0.6);//lower alpha color
            Draw.blend(Blending.additive);
            Draw.rect(this.topRegion, this.x, this.y);
            Draw.blend();
            Draw.reset();
        }
    },
    realRadius(){
        return this.buildingRadius * this.radscl;
    },
    drawShield(){
        if(!this.broken){
            var radius = this.realRadius();

            Draw.z(Layer.shields);
            Draw.color(Pal.lancerLaser, Color.white.cpy(), Mathf.clamp(this.hit));

            if(radius > 1){
                if(Core.settings.getBool("animatedshields")){
                    Fill.poly(this.x, this.y, 40, radius);
                } else {
                    Lines.stroke(1.5);
                    Draw.alpha(0.09 + Mathf.clamp(0.08 * this.hit));
                    Fill.circle(this.x, this.y, radius);
                    Draw.alpha(1);
                    Lines.circle(this.x, this.y, radius);
                }
            }
        }
        Draw.reset();
    },
    levelUp(int){
        shieldGenerator.consumes.power(2 + int);
        this.buildingRadius = 40 + 1.5 * int;
    },
    custonRead(read, revision){
        this.super$read(read, revision);
        this.buildingRadius = 40 + this.totalLevel() * 1.5;
    }
});

shieldGenerator.radius = 40;
