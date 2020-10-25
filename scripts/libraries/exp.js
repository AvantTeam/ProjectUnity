//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.
//type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.
const clone = obj => {
    if(obj === null || typeof(obj) !== 'object') return obj;
    var copy = obj.constructor();
    for(var attr in obj) {
        if(obj.hasOwnProperty(attr)) {
            copy[attr] = obj[attr];
        }
    };
    return copy;
}

function arrayEqual(a, b){
  if (a === b) return true;
  if (a == null || b == null) return false;
  if (a.length !== b.length) return false;

  for (var i = 0; i < a.length; ++i) {
    if (a[i] !== b[i]) return false;
  }
  return true;
}

function drawSpark(x, y, w, h, r){
    Drawf.tri(x, y, w, h, r);
    Drawf.tri(x, y, w, h, r+180);
    Drawf.tri(x, y, w, h, r+90);
    Drawf.tri(x, y, w, h, r+270);
}

const sparkleFx = new Effect(15, e => {
    Draw.color(Color.white, e.color, e.fin());
    var i=1;
    Angles.randLenVectors(e.id, e.id % 3 + 1, e.rotation * 4 + 4, (x,y) => {
      drawSpark(e.x + x, e.y + y, e.fout() * 4, 0.5 + e.fout() * 2.2, e.id * i);
      i++;
    });
});

const upgradeBlockFx = new Effect(90, e => {
    Draw.color(Color.white, Color.green, e.fin());
    Lines.stroke(e.fout()*6*e.rotation);
    Lines.square(e.x, e.y, e.fin()*4*e.rotation+2*e.rotation, 0);

    var i = 1;
    Angles.randLenVectors(e.id, e.id % 3 + 7, e.rotation * 4 + 4 + 8*e.finpow(), (x,y) => {
      drawSpark(e.x + x, e.y + y, e.fout() * 5, e.fout() * 3.5, e.id * i);
      i++;
    });
});

module.exports = {
    upgradeFx: upgradeBlockFx,
    sparkleFx: sparkleFx,
    extend(type, build, name, obj, objb){
        if(obj == undefined) obj = {};
        if(objb == undefined) objb = {};
        obj = Object.assign({
            //start
            maxLevel: 20,
            level0Color: Pal.accent,
            levelMaxColor: Color.valueOf("fff4cc"),
            exp0Color: Color.valueOf("84ff00"),
            expMaxColor: Color.valueOf("90ff00"),
            expFields: [],
            //type, field, start, intensity
            hasLevelEffect: true,
            levelUpFx: Fx.upgradeCore,
            levelUpSound: Sounds.message,
            upgrades: [],
            //block, min, max
            upgradeColor: Color.green,
            upgradeFx: upgradeBlockFx,
            upgradeSparkleFx: sparkleFx,
            upgradeSound: Sounds.place,
            sparkleChance: 0.08,

            //below are legacy arrays
            linearInc: [],
            linearIncStart: [],
            linearIncMul: [],
            expInc: [],
            expIncStart: [],
            expIncMul: [],
            rootInc: [],
            rootIncMul: [],
            rootIncStart: [],
            boolInc: [],
            boolIncStart: [],
            boolIncMul: [],
            listInc: [],
            listIncStart: [],
            listIncMul: [],
            hasLevelFunction: false,
            hasCustomUpdate: false,
            forStats: new ObjectMap(),
            caches: [],
            enableUpgrade: false,
            condConfig: false,
            upBlock: [],
            upMinLevel: [],
            upMaxLevel: [],
            upAuto: []
            //end
        }, obj, {
            //start
            getLevel(exp) {
                return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1)), this.maxLevel);
            },
            getRequiredEXP(lvl) {
                return lvl * lvl * 10;
            },
            getLvlf(exp) {
                var lvl = this.getLevel(exp);
                if(lvl >= this.maxLevel) return 1;
                var last = this.getRequiredEXP(lvl);
                var next = this.getRequiredEXP(lvl + 1);
                return (exp - last) / (next - last);
            },
            setEXPStats(build) {
                var exp = build.totalExp();
                var lvl = this.getLevel(exp);
                if(this.linearInc.length == 1) {
                    this[this.linearInc[0]] = Math.max(this.linearIncStart[0] + this.linearIncMul[0] * lvl, 0);
                } else if(this.linearInc.length > 0) {
                    this.linearEXP(tile, lvl);
                };
                if(this.expInc.length == 1) {
                    this[this.expInc[0]] = Math.max(this.expIncStart[0] + Mathf.pow(this.expIncMul[0], lvl), 0);
                } else if(this.expInc.length > 0) {
                    this.expEXP(tile, lvl);
                };
                if(this.rootInc.length == 1) {
                    this[this.rootInc[0]] = Math.max(this.rootIncStart[0] + Mathf.sqrt(this.rootIncMul[0] * lvl), 0);
                } else if(this.rootInc.length > 0) {
                    this.rootEXP(tile, lvl);
                };
                if(this.boolInc.length == 1) {
                    this[this.boolInc[0]] = (this.boolIncStart[0]) ? (lvl < this.boolIncMul[0]) : (lvl >= this.boolIncMul[0]);
                } else if(this.boolInc.length > 0) {
                    this.boolEXP(tile, lvl);
                };
                if(this.listInc.length > 0) {
                    this.listEXP(tile, lvl);
                };
            },
            linearEXP(tile, lvl) {
                for(var i = 0; i < this.linearInc.length; i++) {
                    this[this.linearInc[i]] = Math.max(this.linearIncStart[i] + this.linearIncMul[i] * lvl, 0);
                };
            },
            expEXP(tile, lvl) {
                for(var i = 0; i < this.expInc.length; i++) {
                    this[this.expInc[i]] = Math.max(this.expIncStart[i] + Mathf.pow(this.expIncMul[i], lvl), 0);
                };
            },
            rootEXP(tile, lvl) {
                for(var i = 0; i < this.rootInc.length; i++) {
                    this[this.rootInc[i]] = Math.max(this.rootIncStart[i] + Mathf.sqrt(this.rootIncMul[i] * lvl), 0);
                };
            },
            boolEXP(tile, lvl) {
                for(var i = 0; i < this.boolInc.length; i++) {
                    this[this.boolInc[i]] = (this.boolIncStart[i]) ? (lvl < this.boolIncMul[i]) : (lvl >= this.boolIncMul[i]);
                };
            },
            listEXP(tile, lvl) {
                for(var i = 0; i < this.listInc.length; i++) {
                    this[this.listInc[i]] = this.listIncMul[i][Math.min(lvl, this.listIncMul[i].length - 1)];
                };
            },
            setBars() {
                this.super$setBars();
                this.bars.add("level", func(build => {
                    return new Bar(prov(() => Core.bundle.get("explib.level") + " " + this.getLevel(build.totalExp())), prov(() => Tmp.c1.set(this.level0Color).lerp(this.levelMaxColor, this.getLevel(build.totalExp()) / this.maxLevel)), floatp(() => {
                        return this.getLevel(build.totalExp()) / this.maxLevel;
                    }));
                }));
                this.bars.add("exp", func(build => {
                    return new Bar(prov(() => (build.totalExp() < this.maxExp) ? Core.bundle.get("explib.exp") : Core.bundle.get("explib.max")), prov(() => Tmp.c1.set(this.exp0Color).lerp(this.expMaxColor, this.getLvlf(build.totalExp()))), floatp(() => {
                        return this.getLvlf(build.totalExp());
                    }));
                }));
            },
            isNumerator(stat) {
                return stat == Stat.inaccuracy || stat == Stat.shootRange;
            },
            setStats() {
                this.forStats.put("range", Stat.shootRange);
                this.forStats.put("inaccuracy", Stat.inaccuracy);
                this.forStats.put("reloadTime", Stat.reload);
                this.forStats.put("targetAir", Stat.targetsAir);
                this.forStats.put("targetGround", Stat.targetsGround);
                this.super$setStats();
                for(var i = 0; i < this.linearInc.length; i++) {
                    var temp = this.forStats.get(this.linearInc[i]);
                    if(temp) {
                        //fuck
                        if(this.isNumerator(temp) == true) this.stats.add(temp, Core.bundle.get("explib.linear.numer"), this.linearIncMul[i] > 0 ? "+" : "", (100 * this.linearIncMul[i] / this.linearIncStart[i]).toFixed(2));
                        else this.stats.add(temp, Core.bundle.get("explib.linear.denomin"), String(this.linearIncStart[i]), this.linearIncMul[i] > 0 ? "+" : "", String(this.linearIncStart[i]), this.linearIncMul[i]);
                    }
                };
                for(var i = 0; i < this.expInc.length; i++) {
                    var temp = this.forStats.get(this.expInc[i]);
                    if(temp) {
                        if(this.isNumerator(temp) == true) this.stats.add(temp, Core.bundle.get("explib.expo.numer"), this.expIncMul[i], String(this.expIncStart[i]));
                        else this.stats.add(temp, Core.bundle.get("explib.expo.denomin"), String(this.expIncStart[i]), String(this.expIncStart[i]), this.expIncMul[i]);
                    }
                };
                for(var i = 0; i < this.rootInc.length; i++) {
                    var temp = this.forStats.get(this.rootInc[i]);
                    if(temp) {
                        if(this.isNumerator(temp) == true) this.stats.add(temp, Core.bundle.get("explib.root.numer"), this.rootIncMul[i], String(this.rootIncStart[i]));
                        else this.stats.add(temp, Core.bundle.get("explib.root.denomin"), String(this.rootIncStart[i]), String(this.rootIncStart[i]), this.rootIncMul[i]);
                    }
                };
                for(var i = 0; i < this.boolInc.length; i++) {
                    var temp = this.forStats.get(this.boolInc[i]);
                    if(temp) this.stats.add(temp, Core.bundle.get("explib.bool"), String(this.boolIncMul[i]), !this.boolIncStart[i]);
                };
            }
            //end
        });
        const expblock = extendContent(type, name, obj);
        expblock.maxExp = expblock.getRequiredEXP(expblock.maxLevel);
        for(var i = 0; i < expblock.expFields.length; i++) {
            var tobj = expblock.expFields[i];
            if(tobj.type == undefined) tobj.type = "linear";
            expblock[tobj.type + "Inc"].push(tobj.field);
            expblock[tobj.type + "IncStart"].push((tobj.start == undefined) ? ((expblock[tobj.field] == undefined || expblock[tobj.field] == null) ? 0 : expblock[tobj.field]) : tobj.start);
            expblock[tobj.type + "IncMul"].push((tobj.intensity == undefined) ? 1 : tobj.intensity);
            if(tobj.cacheValue) expblock.caches.push(tobj.field);
        };
        expblock.hasLevelFunction = (typeof objb["levelUp"] === "function");
        expblock.hasCustomUpdate = (typeof objb["customUpdate"] === "function");
        expblock.hasCustomRW = (typeof objb["customRead"] === "function");
        expblock.hasCache = (expblock.caches.length > 0);

        expblock.enableUpgrade = (expblock.upgrades.length > 0);
        /*for(var i = 0; i < expblock.upgrades.length; i++) {
            var tobj = expblock.upgrades[i];
            expblock.upBlock.push((tobj.block == undefined) ? Blocks.router : tobj.block);
            expblock.upMinLevel.push((tobj.min == undefined) ? expblock.maxLevel : tobj.min);
            expblock.upMaxLevel.push((tobj.max == undefined) ? expblock.maxLevel + 1 : tobj.max);
            expblock.upAuto.push((tobj.autoUpgrade == undefined) ? false : tobj.autoUpgrade);
        };*/
        expblock.upPerLevel = [];
        for(var i=0; i<expblock.maxLevel; i++){
            expblock.upPerLevel.push([]);
            for(var j=0; j<expblock.upgrades.length; j++){
                if(expblock.upgrades[j].min == undefined) expblock.upgrades[j].min = expblock.maxLevel;
                if(expblock.upgrades[j].min <= i && (expblock.upgrades[j].max == undefined || expblock.upgrades[j].max >= i)) expblock.upPerLevel[i].push(expblock.upgrades[j]);
            }
        }

        if(expblock.enableUpgrade){
            expblock.condConfig = expblock.configurable;
            expblock.configurable = true;
            //expblock.hasLevelEffect = true;
        }

        objb = Object.assign(objb, {
            totalExp() {
                return this._exp;
            },
            totalLevel() {
                return expblock.getLevel(this._exp);
            },
            expf() {
                return expblock.getLvlf(this._exp);
            },
            levelf() {
                return this._exp / expblock.maxExp;
            },
            setExp(a) {
                this._exp = a;
            },
            incExp(a) {
                if(this._exp >= expblock.maxExp) return;
                this._exp += a;
                if(this._exp > expblock.maxExp) this._exp = expblock.maxExp;
                this._changedVal = true;
                if(!expblock.hasLevelEffect) return;
                var clvl = expblock.getLevel(this._exp);
                if(expblock.getLevel(this._exp - a) != clvl) {
                    if(expblock.enableUpgrade){
                        if(!arrayEqual(this.currentUpgrades(clvl), this.currentUpgrades(clvl - 1))) this._checked = false;
                    }
                    expblock.levelUpFx.at(this.x, this.y, expblock.size);
                    expblock.levelUpSound.at(this.x, this.y);
                    if(expblock.hasLevelFunction) this.levelUp(clvl);
                };
            },

            updateCaches(){
                for(var i=0; i<expblock.caches.length; i++){
                    this["_cache_" + expblock.caches[i]] = expblock[expblock.caches[i]];
                }
            },
            getCache(fieldName){
                return this["_cache_" + fieldName];
            },

            updateTile() {
                expblock.setEXPStats(this);
                if(this._changedVal && expblock.hasCache) this.updateCaches();
                if(expblock.enableUpgrade && !this._checked && Mathf.chance(expblock.sparkleChance)) this.sparkle();
                if(expblock.hasCustomUpdate) this.customUpdate();
                else this.super$updateTile();
            },
            read(stream, version) {
                this.super$read(stream, version);
                this._exp = stream.i();
                if(expblock.hasCustomRW) this.customRead(stream, version);
                if(expblock.hasCache){
                    expblock.setEXPStats(this);
                    this.updateCaches();
                }
            },
            write(stream) {
                this.super$write(stream);
                stream.i(this._exp);
                if(expblock.hasCustomRW) this.customWrite(stream);
            },

            currentUpgrades(lvl){
                return expblock.upPerLevel[lvl];
            },
            sparkle(){
                expblock.upgradeSparkleFx.at(this.x, this.y, expblock.size, expblock.upgradeColor);
            },
            upgradeBlock(block, expected){
                if(expected != this.totalLevel()){
                    //invalid
                    Vars.control.input.frag.config.hideConfig();
                    return;
                }
                var tile = this.tile;
                if(block.size > expblock.size) tile = this.getBestTile(block.size);
                if(tile == null) return;
                Vars.control.input.frag.config.hideConfig();
                //TODO: sync
                expblock.upgradeFx.at(this.x, this.y, block.size, expblock.upgradeColor);
                expblock.upgradeSound.at(this.x, this.y);
                tile.setBlock(block, this.team, this.rotation);
            },
            getBestTile(size){
                //TODO:
                return this.tile;
            },

            makeUpgradeButton(t, iblock, lvl){
                t.button(Icon.upgrade, Styles.cleari, () => {
                    this.upgradeBlock(iblock, lvl);
                }).size(40);
            },
            upgradeTable(table, lvl){
                var arr = this.currentUpgrades(lvl);
                if(arr.length == 0) return;
                for(var i=0; i<arr.length; i++){
                    var block = arr[i].block;
                    table.table(cons(t => {
                        t.background(Tex.button);
                        t.image(block.icon(Cicon.medium)).size(38).padRight(2);
                        t.table(cons(info => {
                            info.left();
                            info.add("[green]"+block.localizedName+"[]\n"+Core.bundle.get("explib.level.short")+" ["+((arr[i].min == lvl)?"green":"accent")+"]"+lvl+"[]/"+arr[i].min);
                        })).width(80);
                        t.button(Icon.infoCircle, Styles.cleari, () => {
                            Vars.ui.content.show(block);
                        }).size(40);
                        if(arr[i].min == lvl) Styles.cleari.imageUpColor = expblock.upgradeColor;
                        this.makeUpgradeButton(t, arr[i].block, lvl);
                        if(arr[i].min == lvl) Styles.cleari.imageUpColor = Color.white;
                    })).width(220).height(50);

                    if(i < arr.length - 1) table.row();
                }
            },
            buildConfiguration(table){
                if(!expblock.enableUpgrade){
                    this.super$buildConfiguration(table);
                    return;
                }
                var lvl = this.totalLevel();
                this._checked = true;
                if(!expblock.condConfig) this.upgradeTable(table, lvl);
                else{
                    if(this.currentUpgrades(lvl).length == 0){
                        this.super$buildConfiguration(table);
                        return;
                    }
                    table.table(cons(table2 => {
                        this.upgradeTable(table2, lvl);
                    }));
                    table.row();
                    table.image().pad(2).width(130).height(4).color(expblock.upgradeColor);
                    table.row();
                    table.table(cons(table3 => {
                        this.super$buildConfiguration(table3);
                    }));
                }
            },
            configTapped(){
                if(!this.super$configTapped()) return false;
                if(!expblock.enableUpgrade) return true;
                if(expblock.condConfig) return true;
                if(expblock.upPerLevel[this.totalLevel()].length > 0) return true;
                return false;
            }
        });
        //Extend Building
        expblock.buildType = ent => {
            ent = extendContent(build, expblock, clone(objb));
            ent._exp = 0;
            ent._changedVal = false;
            ent._checked = true;
            return ent;
        };
        return expblock;
    }
}
