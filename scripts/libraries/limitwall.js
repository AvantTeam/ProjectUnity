//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.
//type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.

//if damage taken is above the max damage, it will only take the max damage

const maxDamageFx = new Effect(16, e => {
    Draw.color(Color.orange);
    Lines.stroke(2.5 * e.fin());
    Lines.square(e.x, e.y, e.fout() * 1.5, 45);
});

const withstandFx = new Effect(16, e => {
    Draw.color(Color.orange);
    Lines.stroke(1.2 * e.rotation * e.fout());
    Lines.square(e.x, e.y, e.rotation * 4);
});

const diriumColor = Color.valueOf("96f7c3");

const blinkFx = new Effect(30, e => {
    Draw.color(Color.white, diriumColor, e.fin());
    Lines.stroke(3 * e.rotation * e.fout());
    Lines.square(e.x, e.y, e.rotation * 4 * e.finpow());
});

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

module.exports = {
    maxDamageFx: maxDamageFx,
    withstandFx: withstandFx,
    blinkFx: blinkFx,
    extend(type, build, name, obj, objb){
        if(obj == undefined) obj = {};
        if(objb == undefined) objb = {};
        obj = Object.assign({
            //start
            //negative to disable max damage
            maxDamage: 30,
            maxDamageFx: maxDamageFx,
            withstandFx: withstandFx,
            over9000: 90000000, //wave damage is 99999999

            //negative to disable wall blinking
            blinkFrame: -1,
            blinkFx: blinkFx,

            setStats(){
                this.super$setStats();
                if(this.maxDamage > 0 && this.blinkFrame > 0) this.stats.add(Stat.abilities, "@\n@", Core.bundle.format("stat.unity.maxDamage", this.maxDamage), Core.bundle.format("stat.unity.blinkFrame", this.blinkFrame));
                else if(this.maxDamage > 0) this.stats.add(Stat.abilities, "@", Core.bundle.format("stat.unity.maxDamage", this.maxDamage));
                else if(this.blinkFrame > 0) this.stats.add(Stat.abilities, "@", Core.bundle.format("stat.unity.blinkFrame", this.blinkFrame));
            }
            //end
        }, obj);
        const wallblock = extendContent(type, name, obj);
        wallblock.update = true;
        wallblock.solid = true;

        objb = Object.assign({
            handleDamage(amount){
                if(wallblock.blinkFrame > 0){
                    if(Time.time - this._blink >= wallblock.blinkFrame){
                        this._blink = Time.time;
                        wallblock.blinkFx.at(this.x, this.y, wallblock.size);
                    }
                    else{
                        return 0;
                    }
                }
                if(wallblock.maxDamage > 0 && amount > wallblock.maxDamage && amount < wallblock.over9000){
                    wallblock.withstandFx.at(this.x, this.y, wallblock.size);
                    return this.super$handleDamage(Math.min(amount, wallblock.maxDamage));
                }
                else{
                    return this.super$handleDamage(amount);
                }
            }
        }, objb);
        //Extend Building
        wallblock.buildType = ent => {
            ent = extendContent(build, wallblock, clone(objb));
            if(wallblock.blinkFrame > 0) ent._blink = 0;
            return ent;
        };
        return wallblock;
    }
}
