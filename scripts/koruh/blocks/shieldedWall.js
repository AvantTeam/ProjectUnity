const lib = this.global.unity.exp;
const shieldBreakSquare = new Effect(40, e => {
    Draw.color(Pal.lancerLaser);
    Lines.stroke(3 * e.fout());
    Lines.poly(e.x, e.y, 4, e.rotation + e.fin(), 45);
});

const shieldedWall = lib.extend(ForceProjector, ForceProjector.ForceBuild, "shielded-wall", {
    maxLevel: 15,
    expFields: [
        {
            type: "linear",
            field: "breakage",
            start: 400,
            intensity: 25
        },
        {
            type: "exp",
            field: "health",
            start: 300,
            intensity: 1.5
        }
    ],
    drawPlace(x, y, rotation, valid){
        Draw.color(Pal.lancerLaser);
        Lines.stroke(1.5);
        Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 4, this.radius, 45);
        Draw.color();
    }
}, {
    customUpdate(){
        //necessary for realRadius
        this.radscl = Mathf.lerpDelta(this.radscl, this.broken ? 0 : this.warmup, 0.05);
        this.warmup = Mathf.lerpDelta(this.warmup, this.efficiency(), 0.1);

        //shield is self-healing by cooldown
        var scale = !this.broken ? shieldedWall.cooldownNormal : shieldedWall.cooldownBrokenBase;
        this.buildup -= this.delta() * scale;

        //when shield is revived
        if(this.broken && this.buildup <= 0){
            this.broken = false;
        }

        //when shield is destroyed
        if(this.buildup >= shieldedWall.breakage + shieldedWall.phaseShieldBoost && !this.broken){
            this.broken = true;
            this.buildup = shieldedWall.breakage;
            shieldBreakSquare.at(this.x, this.y, this.realRadius(), Pal.lancerLaser);
        }
        //when hit
        if(this.hit > 0){
            this.hit -= 1 / 5 * Time.delta;
        }

        //bullet intersect
        const customConsumer = trait => {
            if(trait.team != this.paramEntity.team && trait.type.absorbable && Intersector.isInPolygon([
              this.x + this.realRadius(), this.y + this.realRadius(),
              this.x - this.realRadius(), this.y + this.realRadius(),
              this.x + this.realRadius(), this.y - this.realRadius(),
              this.x - this.realRadius(), this.y - this.realRadius()
            ], 0, 8, trait.x, trait.y)){
                trait.absorb();
                Fx.absorb.at(trait);
                this.paramEntity.hit = 1;
                this.paramEntity.buildup += trait.damage * this.paramEntity.warmup;
                this.incExp(0.25);

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
        return (shieldedWall.radius + this.phaseHeat * shieldedWall.phaseRadiusBoost) * this.radscl;
    },
    drawShield(){
        if(!this.broken){
            var radius = this.realRadius();

            Draw.z(Layer.shields);

            Draw.color(Pal.lancerLaser, Color.white.cpy(), Mathf.clamp(this.hit));

            if(Core.settings.getBool("animatedshields")){
                if(radius <= 1){
                    Draw.reset();
                } else {
                    Fill.poly(this.x, this.y, 4, radius, 45);
                }
            } else {
                if(radius <= 1){
                    Draw.reset();
                } else {
                    Lines.stroke(1.5);
                    Draw.alpha(0.09 + Mathf.clamp(0.08 * this.hit));
                    Fill.poly(this.x, this.y, 4, radius, 45);
                    Draw.alpha(1);
                    Lines.poly(this.x, this.y, 4, radius, 45);
                    Draw.reset();
                }
            }
        }
        Draw.reset();
    }
});

shieldedWall.radius = 10;
shieldedWall.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 1 && liquid.flammability > 1000, 0.5)).boost().update(false);//???
