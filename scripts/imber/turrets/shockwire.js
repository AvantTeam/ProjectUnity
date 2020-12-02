const shockBeam = extend(ContinuousLaserBulletType, {
    init(b){
        if(typeof(b) === "undefined") return;
        this.super$init(b);

        var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
        b.data = target;

        if(target instanceof Hitboxc){
            var hit = target;

            hit.collision(b, hit.x, hit.y);
            b.collision(hit, hit.x, hit.y);
        }else if(target instanceof Building){
            var tile = target;
            
            if(tile.collide(b)){
                tile.collision(b);
                this.hit(b, tile.x, tile.y);
            }
        }else{
            b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
        }
    },

    update(b){
        if(b.timer.get(1, 5)){
            if(b.owner.target == null || b.owner == null) return;
            Lightning.create(b.team, this.color, Mathf.random(this.damage / 1.8, this.damage / 1.2), b.x, b.y, b.angleTo(b.owner.target), Mathf.floorPositive(b.dst(b.owner.target) / Vars.tilesize + 3));
            if(b.owner.target instanceof Healthc){
                b.owner.target.damage(this.damage);
            }
        }
    },

    draw(b){
        var target = b.owner.target;
        if(target != null){
            Draw.color(this.color);
            Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, target.x, target.y, this.width * b.fout());
            Draw.reset();

            Drawf.light(b.team, b.x, b.y, b.x + target.x, b.y + target.y, 15 * b.fout(), this.lightColor, 0.6);
        }else if(b.data instanceof Position){
            var data = b.data;
            Tmp.v1.set(data);

            Draw.color(this.color);
            Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * b.fout());
            Draw.reset();

            Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15 * b.fout(), this.color, 0.6);
        }
    }
});
shockBeam.damage = 35;
shockBeam.speed = 0.0001;
shockBeam.shootEffect = Fx.none;
shockBeam.despawnEffect = Fx.none;
shockBeam.pierce = true;
shockBeam.hitSize = 0;
shockBeam.status = StatusEffects.shocked;
shockBeam.statusDuration = 3 * 60;
shockBeam.width = 0.62;
shockBeam.length = 120;
shockBeam.color = Pal.surge.cpy();
shockBeam.hittable = false;
shockBeam.hitEffect = Fx.hitLiquid;

const shockwire = extendContent(PowerTurret, "shockwire", {
    setStats(){
        this.super$setStats();

        this.stats.remove(Stat.damage);
        this.stats.add(Stat.damage, this.shootType.damage, StatUnit.none);
    }
});
shockwire.shootType = shockBeam;
shockwire.shootSound = Sounds.laserbig;
shockwire.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.4)).update(false);
