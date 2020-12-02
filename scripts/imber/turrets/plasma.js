const effects = this.global.unity.effects;

const plasmaFrag = extend(BulletType, {
    init(b){
        if(b == undefined) return;
    
        b.data = new Trail(10);
    },
	
    draw(b){
        b.data.draw(this.colors, 2.8);

        Draw.color(this.colors);
        Fill.square(b.x + (b.fin() * Mathf.range(2)), b.y + (b.fin() * Mathf.range(2)), 4, 45);
    },
	
    update(b){
        this.super$update(b);
		
        b.data.update(b.x, b.y);
		
        if(Mathf.chanceDelta(0.19)){
            effects.coloredSpark1.at(b.x, b.y, Mathf.random(360), Color.valueOf("f2e87b"));
        };

        var target = Units.closestTarget(b.team, b.x, b.y, 8 * Vars.tilesize);
        if(target != null && b.timer.get(1, 12)){
            Lightning.create(b.team, Pal.surge, 23, b.x, b.y, b.angleTo(target), b.dst(target) / Vars.tilesize + 2);
        }
    }
});
plasmaFrag.speed = 4.5;
plasmaFrag.drag = 0.05;
plasmaFrag.damage = 70;
plasmaFrag.lifetime = 230;
plasmaFrag.collides = false;
plasmaFrag.colors = Pal.surge;
plasmaFrag.hitColor = Pal.surge;
plasmaFrag.shootEffect = effects.imberPlasmaFragAppear;
plasmaFrag.hitEffect = effects.imberPlasmaFragDisappear;
plasmaFrag.despawnEffect = effects.imberPlasmaFragDisappear;

const plasma = extend(BulletType, {
    init(b){
        if(typeof(b) === "undefined") return;
    
        b.data = new Trail(10);
    },

    draw(b){
        b.data.draw(this.colors[0], 13 * 0.5);

        Draw.color(this.colors[0]);
        Fill.square(b.x, b.y, 7, b.rotation() + 45);
    },
	
    update(b){
        this.super$update(b);

        b.data.update(b.x, b.y);
		
        if(Mathf.chanceDelta(0.12)){
            effects.coloredSpark1.at(b.x, b.y, Mathf.random(360), Color.valueOf("f2e87b"));
        };
    },

    despawned(b){
        this.hit(b);
    },
	
    hit(b, x, y){
        this.super$hit(b, b.x, b.y);

        effects.imberTriangleHit.at(b.x, b.y, b.rotation(), this.hitColor);
    }
});
plasma.lifetime = 80;
plasma.speed = 4;
plasma.damage = 130;
plasma.colors = [Pal.surge, Color.valueOf("f2e87b"), Color.valueOf("d89e6b"), Color.white];
plasma.hitColor = plasma.colors[1];
plasma.fragBullet = plasmaFrag;
plasma.fragBullets = 8;
plasma.fragLifeMin = 0.8;
plasma.fragLifeMax = 1.1;

const plasmaTurret = extendContent(PowerTurret, "plasma", {});
plasmaTurret.shootType = plasma;
plasmaTurret.shootSound = Sounds.shotgun;
plasmaTurret.shootEffect = effects.imberShootTriangle;
plasmaTurret.chargeBeginEffect = effects.imberChargeBeginTriangles;
plasmaTurret.chargeEffect = effects.imberChargeTriangles;
plasmaTurret.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();