const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");
const graphLib = require("libraries/graphlib");

const partinfo = [{
        name: "Pivot",
        desc: "",
        category: "Blade",
        tx: 4,
        ty: 0,
        tw: 1,
        th: 1,
        cannotPlace: true,
        prePlace: {
            x: 0,
            y: 0,
        },
        isRoot: true,
        cost: [],
        connectOut: [1, 0, 0, 0],
        connectIn: [0, 0, 0, 0],
        stats: {
            mass: {
                name: "stat.unity.blademass",
                value: 1,
            },
            collides: {
                name: "stat.unity.collides",
                value: false,
            },
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
        }

    },
    {
        name: "Blade",
        desc: "Slices and knocks back enemies",
        category: "Blade",
        tx: 0,
        ty: 0,
        tw: 1,
        th: 1,
        cost: [{
                name: "unity-nickel",
                amount: 3
            },
            {
                name: "titanium",
                amount: 5
            },

        ],
        connectOut: [1, 0, 0, 0],
        connectIn: [0, 0, 1, 0],
        stats: {
            mass: {
                name: "stat.unity.blademass",
                value: 2,
            },
            collides: {
                name: "stat.unity.collides",
                value: true,
            },
            hp: {
                name: "stat.unity.hpinc",
                value: 80,
            },
            damage: {
                name: "stat.unity.bladedamage",
                value: 5,
            },
        }
    },
    {
        name: "Serrated blade",
        desc: "A heavy reinforced blade.",
        category: "Blade",
        tx: 2,
        ty: 0,
        tw: 2,
        th: 1,
        cost: [{
                name: "unity-nickel",
                amount: 8
            },
            {
                name: "lead",
                amount: 5
            }
        ],
        connectOut: [1, 0, 0, 0, 0, 0],
        connectIn: [0, 0, 0, 1, 0, 0],
        stats: {
            mass: {
                name: "stat.unity.blademass",
                value: 6,
            },
            collides: {
                name: "stat.unity.collides",
                value: true,
            },
            hp: {
                name: "stat.unity.hpinc",
                value: 120,
            },
            damage: {
                name: "stat.unity.bladedamage",
                value: 12,
            },
        }
    },
    {
        name: "Rod",
        desc: "Supporting structure, does not collide",
        category: "Blade",
        tx: 1,
        ty: 0,
        tw: 1,
        th: 1,
        cost: [{
            name: "titanium",
            amount: 3
        }],
        connectOut: [1, 0, 0, 0],
        connectIn: [0, 0, 1, 0],
        stats: {
            mass: {
                name: "stat.unity.blademass",
                value: 1,
            },
            collides: {
                name: "stat.unity.collides",
                value: false,
            },
            hp: {
                name: "stat.unity.hpinc",
                value: 40,
            },
        }
    },

];
let blankobj = graphLib.init();
graphLib.addGraph(blankobj, rotL.baseTypes.torqueConnector);
Object.assign(blankobj.build, modturretlib.dcopy2(modturretlib.ModularBuild));
Object.assign(blankobj.block, modturretlib.dcopy2(modturretlib.ModularBlock));

const collidedBlocks = new IntSet();
const knockbackmult = 10.0;
const chopperTurret = graphLib.finaliseExtend(Block, Building, "chopper", blankobj, {

    load() {
        this.super$load();
        this.topsprite = Core.atlas.find(this.name + "-top");
        this.base = [Core.atlas.find(this.name + "-base1"), Core.atlas.find(this.name + "-base2"), Core.atlas.find(this.name + "-base3"), Core.atlas.find(this.name + "-base4")];
        this.partsAtlas = Core.atlas.find(this.name + "-parts");
        partinfo[0].sprite = Core.atlas.find(this.name + "-rod");
        partinfo[3].sprite = Core.atlas.find(this.name + "-rod");
        partinfo[1].sprite = Core.atlas.find(this.name + "-blade1");
        partinfo[1].sprite2 = Core.atlas.find(this.name + "-blade2");
        partinfo[2].sprite = Core.atlas.find(this.name + "-sblade");
        this.setConfigs();

    },
}, {
    aniprog: 0,
    anitime: 0,
    anispeed: 0,
    speedDmgMul: 0,
    knockbackTorque: 0,
    ////stats
    inertia: 5,
    originalmaxhp: 0,
    hitSegments: [],
    detectrect: null,
    bladeRadius: 0,
    getSpdDmgMul() {
        return this.speedDmgMul;
    },
    getPartsConfig() {
        return partinfo;
    },
    getPartsAtlas() {
        return chopperTurret.partsAtlas;
    },
    resetStats() {
        this.inertia = 5;
        this.hitSegments = [];
        if(this.originalmaxhp) {
            this.maxHealth = this.originalmaxhp;
        }
    },
    applyStats(total) {
        this.inertia = 5 + total.inertia;
        this.originalmaxhp = this.maxHealth;
        this.maxHealth = this.originalmaxhp + total.hpinc;
		if(!total.segments) {
			total.segments = [];
		}
        this.hitSegments = total.segments;
        var r = 0;
        for(var i = 0; i < this.hitSegments.length; i++) {
            r = Math.max(r, this.hitSegments[i].end * 8);
        }
        this.detectrect = new Rect();
        this.detectrect.setPosition(this.x-r, this.y-r).setSize(r * 2, r * 2);
        this.bladeRadius = r;
    },
    getHitDamage(rx, ry, rot) {
        var dist = Mathf.dst(rx, ry);
        var drx = Mathf.cosDeg(rot);
        var dry = Mathf.sinDeg(rot);
        if(rx * drx / dist + ry * dry / dist < Mathf.cosDeg(Mathf.clamp(this.speedDmgMul * 10, 0, 180))) { //quick 'within angle' calculation using dot product
            return 0;
        }
        for(var i = 0; i < this.hitSegments.length; i++) {
            var seg = this.hitSegments[i];
            if(seg.start * 8 + 4 < dist && seg.end * 8 + 4 > dist) {
                return seg.damage * Mathf.clamp(dist * 0.1);
            }
        }
        return 0;
    },
    onIntCollider(cx, cy, rot) {
        var tile = Vars.world.build(cx, cy);
        var collide = tile != null && collidedBlocks.add(tile.pos());

        if(collide && tile.team != this.team) {
            let k = this.getHitDamage((cx - this.tileX()) * 8.0, (cy - this.tileY()) * 8.0, rot);
            tile.damage(k);
            this.knockbackTorque += k * knockbackmult;
        }
    },
    damageChk(rot) {
        var drx = Mathf.cosDeg(rot);
        var dry = Mathf.sinDeg(rot);
        var that = this;
        collidedBlocks.clear();
        Vars.world.raycastEachWorld(this.x, this.y, this.x + drx * this.bladeRadius, this.y + dry * this.bladeRadius, (cx, cy) => {
            this.onIntCollider(cx, cy, rot);
            return false;
        });
        Units.nearbyEnemies(this.team, this.detectrect, cons(
            (unit) => {
                if(!unit.checkTarget(false /*collides air*/ , true /*collides grnd*/ )) return;
                let k = that.getHitDamage(unit.x - this.x, unit.y - this.y, rot);
                if(k > 0) {
                    unit.damage(k);
                    unit.impulse(-dry * k * 10.0, drx * k * 10.0);
                    that.knockbackTorque += k * knockbackmult;
                }

            }
        ));
    },
    accumStats(total, part, x, y, grid) {
        if(!total.inertia) {
            total.inertia = 0;
        }
        if(!total.hpinc) {
            total.hpinc = 0;
        }
        total.inertia += part.stats["mass"].value * x;
        total.hpinc += part.stats["hp"].value;
        if(part.stats["collides"].value) {
            if(!total.segments) {
                total.segments = [];
                total.segments.push({
                    start: x, //inclusive
                    end: x + part.tw, //exclusive
                    damage: part.stats["damage"].value
                });
            }
            else {
                var dmg = part.stats["damage"].value;
                var appended = false;
                for(var i = 0; i < total.segments.length; i++) {
                    if(total.segments[i].damage == dmg && total.segments[i].end == x) {
                        total.segments[i].end += part.tw;
                        appended = true;
                        break;
                    }
                }
                if(!appended) {
                    total.segments.push({
                        start: x, //inclusive
                        end: x + part.tw, //exclusive
                        damage: part.stats["damage"].value
                    });
                }
            }
        }
    },
    updatePre() {
        var tgraph = this.getGraphConnector("torque graph");
        tgraph.setInertia(this.inertia);
        tgraph.setFriction(0.03);
        tgraph.setForce(-this.knockbackTorque);
        this.knockbackTorque = 0;
        this.anitime += Time.delta;
        var prog = this.getPaidRatio();
        if(this.aniprog < prog) {
            this.anispeed = (prog - this.aniprog) * 0.1;
            this.aniprog += this.anispeed;
        }
        else {
            this.aniprog = prog;
            this.anispeed = 0;
        }
        this.speedDmgMul = tgraph.getNetwork().lastVelocity;
    },
    updatePost() {
        var tgraph = this.getGraphConnector("torque graph");
        if(this.getPaidRatio() >= 1 && this.speedDmgMul > 0.8) {
            this.damageChk(tgraph.getRotation());
        }
    },
    drawPartBuffer(part, x, y, grid) {
        Draw.rect(part.sprite, (x + part.tw * 0.5) * 32, (y + part.th * 0.5) * 32, part.tw * 32, part.th * 32);
        if((!grid[x + 1] || !grid[x + 1][y]) && part.sprite2) {
            Draw.rect(part.sprite2, (x + part.tw * 0.5 + 1) * 32, (y + part.th * 0.5) * 32, part.tw * 32, part.th * 32);
        }
    },

    draw() {
        let tgraph = this.getGraphConnector("torque graph");
        Draw.rect(chopperTurret.base[this.rotation], this.x, this.y, 0);
        let blades = this.getBufferRegion();
        if(blades) {
            Draw.z(Layer.turret);
            if(this.getPaidRatio() < 1) {
                blades.setU2(Mathf.map(this.aniprog, 0, 1, blades.u, blades.u2));
            }
            var that = this;
            if(this.getPaidRatio() < 1) {
                modturretlib.drawConstruct(blades, this.aniprog, Pal.accent, 1.0, this.anitime * 0.5, Layer.turret, function(tex) {
                    Draw.rect(tex, that.x + tex.width * 0.125, that.y, tex.width * 0.25, tex.height * 0.25, 0, tex.height * 0.5 * 0.25, tgraph.getRotation());
                });
            }
            else {
                Draw.rect(blades, that.x + blades.width * 0.125, that.y, blades.width * 0.25, blades.height * 0.25, 0, blades.height * 0.5 * 0.25, tgraph.getRotation());
            }
            Draw.rect(chopperTurret.topsprite, this.x, this.y, 0);
        }
        this.drawTeamTop();
    }

});

chopperTurret.rotate = true;
chopperTurret.update = true;
chopperTurret.solid = true;
chopperTurret.configurable = true;
chopperTurret.acceptsItems = true;
chopperTurret.setGridWidth(7);
chopperTurret.setGridHeight(1);
chopperTurret.getGraphConnectorBlock("torque graph").setAccept([1, 0, 0, 0]);
chopperTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.03);
chopperTurret.getGraphConnectorBlock("torque graph").setBaseInertia(5);