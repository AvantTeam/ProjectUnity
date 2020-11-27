//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.
//type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.

//if damage taken is above the max damage, it will only take the max damage

const maxDamageFx = new Effect(16, e => {
    Color.color(Color.orange);
    Lines.stroke(2.5 * e.fin());
    Lines.square(e.x, e.y, e.fout() * 1.5, 45);
});

const withstandFx = new Effect(16, e => {
    Color.color(Color.orange);
    Lines.stroke(1.2 * e.rotation * e.fin());
    Lines.square(e.x, e.y, e.rotation * 4, 0);
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
    extend(type, build, name, obj, objb){
        if(obj == undefined) obj = {};
        if(objb == undefined) objb = {};
        obj = Object.assign({
            //start
            maxDamage: 30,
            maxDamageFx: maxDamageFx,
            withstandFx: withstandFx,

            setStats(){
                this.super$setStats();
                this.stats.add(Stat.abilities, "@", Core.bundle.format("stat.maxDamage", this.maxDamage));
            }
            //end
        }, obj);
        const wallblock = extendContent(type, name, obj);
        wallblock.update = true;
        wallblock.solid = true;

        objb = Object.assign({
            handleDamage(amount){
                this.super$handleDamage(Math.min(amount, wallblock.maxDamage));
                if(amount > wallblock.maxDamage) wallblock.withstandFx.at(this.x, this.y, wallblock.size);
            }
        }, objb);
        //Extend Building
        wallblock.buildType = ent => {
            ent = extendContent(build, wallblock, clone(objb));
            return ent;
        };
        return wallblock;
    }
}
