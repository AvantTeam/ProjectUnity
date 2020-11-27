const lib = this.global.unity.exp;
const colorOfDirium = Color.valueOf("96f7c3");
const shieldBreakCircle = new Effect(40, e => {
    Draw.color(colorOfDirium);
    Lines.stroke(3 * e.fout());
    Lines.circle(e.x, e.y, e.rotation + e.fin());
});
const deflect = new Effect(12, e => {
    Draw.color(Pal.heal);

    Lines.stroke(2 * e.fout());
    Lines.square(e.x, e.y, 8 * e.fout(), 45);
});

const deflectGenerator = lib.extend(ForceProjector, ForceProjector.ForceBuild, "deflect-generator", {
    maxLevel: 30,
    expFields: [
        {
            type: "linear",
            field: "breakage",
            start: 775,
            intensity: 35
        }
    ],
    drawPlace(x, y, rotation, valid){
        var fin = (Time.time() % 90) / 90;
        Draw.color(this.exp0Color);
        Lines.stroke(1.5 * (1 - fin));
        Lines.circle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.radius + fin * 2 * this.maxLevel);

        Draw.color(Pal.lancerLaser, colorOfDirium, fin);
        Lines.stroke(1.5);
        Lines.circle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.radius);
        Draw.color();
    },
    load(){
        this.super$load();
        this.regionAlt = Core.atlas.find(this.name + "-1");
    }
}, {
    deflectChance: 0.2,
    deflectDamage: 0,
    buildingRadius: 60,
    created(){
        this.super$created();
        this.buildingRadius = 60;
    },
    customUpdate(){

        this.radscl = Mathf.lerpDelta(this.radscl, this.broken ? 0 : this.warmup, 0.05);
        this.warmup = Mathf.lerpDelta(this.warmup, this.efficiency(), 0.1);

        var scale = !this.broken ? deflectGenerator.cooldownNormal : deflectGenerator.cooldownBrokenBase;
        var cons = deflectGenerator.consumes.get(ConsumeType.liquid);
        if(this.buildup > 0){
            if(cons.valid(this)){
                cons.update(this);
                scale *= (deflectGenerator.cooldownLiquid * (1 + (this.liquids.current().temperature - 0.4) * 0.9));
            }

            this.buildup -= this.delta() * scale;
        }

        if(this.broken && this.buildup <= 0){
            this.broken = false;
        }

        if(this.buildup >= deflectGenerator.breakage && !this.broken){
            this.broken = true;
            this.buildup = deflectGenerator.breakage;
            shieldBreakCircle.at(this.x, this.y, this.realRadius(), Pal.lancerLaser);
        }

        if(this.hit > 0){
            this.hit -= 1 / 5 * Time.delta;
        }

        const customConsumer = trait => {
            if(trait.team != this.paramEntity.team && trait.type.absorbable && Mathf.dst(this.paramEntity.x, this.paramEntity.y, trait.x, trait.y) <= this.realRadius()){
                var h = 0;
                if(Mathf.chance(this.deflectChance)){
                    h = 2;
                    trait.trns(-trait.vel.x, -trait.vel.y);

                    var penX = Math.abs(this.paramEntity.x - trait.x);
                    var penY = Math.abs(this.paramEntity.y - trait.y);

                    if(penX > penY){
                        trait.vel.x *= -1;
                    }else{
                        trait.vel.y *= -1;
                    }
                    trait.owner = this.paramEntity;
                    trait.team = this.paramEntity.team;
                    trait.time += 1;
                    trait.damage += this.deflectDamage;

                    deflect.at(trait);
                }else{
                    h = 1;
                    trait.absorb();

                    Fx.absorb.at(trait);
                }
                this.paramEntity.hit = 1;
                this.paramEntity.buildup += trait.damage * this.paramEntity.warmup / h;
                if(cons.valid(this)){
                    this.incExp((scale / 20) * h);
                } else {
                    this.incExp(0.1 * h);
                }
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

        Draw.alpha(this.levelf());
        Draw.rect(deflectGenerator.regionAlt, this.x, this.y);

        if(this.drawer != null){
            this.drawer.set(this.x, this.y);
        }

        if(this.buildup > 0){
            Draw.color();
            Draw.mixcol(colorOfDirium, this.levelf());
            Draw.alpha(this.buildup / this.breakage * 0.6);
            Draw.blend(Blending.additive);
            Draw.rect(deflectGenerator.topRegion, this.x, this.y);
            Draw.blend();
            Draw.mixcol();
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

            Draw.color(Tmp.c1.set(Pal.lancerLaser).lerp(colorOfDirium, this.levelf()), Color.white, Mathf.clamp(this.hit));
            if(radius > 4){
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
        //deflectGenerator.consumes.power(2 + this.totalLevel());이거 안됨 다른 방법을 찾아봐요
        this.buildup = 0;
        this.broken = false;
        this.buildingRadius = 60 + int * 2;
        if(this.deflectChance <= 1){ //...to 8lv
            this.deflectChance = 0.2 + int * 0.1;
        }else{//9 ~ 30, total +30 damage
            this.deflectDamage = int;
        }
    },
    customRead(read, revision){
        this.buildingRadius = 60 + this.totalLevel() * 2;
        if(this.totalLevel() <= 8){
            this.deflectChance = 0.2 + this.totalLevel() * 0.1;
        }else{
            this.deflectDamage = this.totalLevel();
        }
    },
    customWrite(write){}
});
deflectGenerator.radius = 60;
deflectGenerator.buildVisibility = BuildVisibility.shown;
