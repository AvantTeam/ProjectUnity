//factions!
var faclist = global.unity.factionContent;

module.exports = {
    contCache: {},
    getFaction(cont){
        if(this.contCache[cont.name]) return this.contCache[cont.name];
        else this.contCache[cont.name] = this.getFactionRaw(cont);
        return this.contCache[cont.name];
    },
    getFactionRaw(cont){
        //O(n) eww
        var facs = Object.keys(faclist);
        for(var i=0; i<facs.length; i++){
            var index = faclist[facs[i]].indexOf(cont);
            if(index > -1) return facs[i];
        }
        return "global";
    }
}
