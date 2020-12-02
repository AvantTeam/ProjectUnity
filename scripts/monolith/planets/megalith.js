const megalithGen = extend(PlanetGenerator, {
    rawHeight(pos){
        pos = Tmp.v33.set(pos).scl(this.scl);
        
        return (Mathf.pow(this.simplex.octaveNoise3D(7, 0.5, 1 / 3, pos.x, pos.y, pos.z), 2.3) + this.waterOffset) / (1 + this.waterOffset);
    },
    
    getHeight(pos){
        var height = this.rawHeight(pos);
        
        return Math.max(height, this.water);
    },
    
    getColor(pos){
        var block = this.getBlock(pos);
        
        if(typeof(block) === "undefined") return Color.white.cpy();
        
        Tmp.c1.set(block.mapColor).a = 1 - block.albedo;
        
        return Tmp.c1;
    },
    
    genTile(pos, tile){
        tile.floor = this.getBlock(pos);
        tile.block = tile.floor.asFloor().wall;
        
        if(this.rid.getValue(pos.x, pos.y, pos.z, 22) > 0.32){
            tile.block = Blocks.air;
        };
    },
    
    getBlock(pos){
        var height = this.rawHeight(pos);
        
        Tmp.v31.set(pos);
        
        pos = Tmp.v33.set(pos).scl(this.scl);
        
        var rad = this.scl;
        var temp = Mathf.clamp(Math.abs(pos.y * 2) / rad);
        var tnoise = this.simplex.octaveNoise3D(7, 0.56, 1 / 3, pos.x, pos.y + 999, pos.z);
        
        temp = Mathf.lerp(temp, tnoise, 0.5);
        height *= 1.2;
        height = Mathf.clamp(height);

        return this.arr[Mathf.clamp(Math.floor((temp * this.arr.length)), 0, this.arr[0].length - 1)][Mathf.clamp(Math.floor((height * this.arr[0].length)), 0, this.arr[0].length - 1)];
    },
    
    noise(x, y, octaves, falloff, scl, mag){
        var v = this.sector.rect.project(x, y).scl(5);
        
        return this.simplex.octaveNoise3D(octaves, falloff, 1 / scl, v.x, v.y, v.z) * mag;
    },
    
    generate(){
        throw new Error("this is called you eye sore");
    },
    
    postGenerate(tiles){
        if(this.sector.hasEnemyBase()){
            this.basegen.postGenerate();
        };
    }
});
const b = Blocks;
megalithGen.arr = [
    [b.water, b.darksandWater, b.snow, b.snow, b.snow, b.snow, b.snow, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice, b.ice, b.ice],
    [b.water, b.darksandWater, b.darksand, b.stone, b.snow, b.snow, b.snow, b.snow, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice, b.ice],
    [b.water, b.water, b.darksandWater, b.darksand, b.darksand, b.stone, b.stone, b.snow, b.snow, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice],
    [b.water, b.water, b.darksandWater, b.darksand, b.darksand, b.darksand, b.stone, b.stone, b.stone, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice],
    
    [b.deepwater, b.water, b.darksandWater, b.darksand, b.darksand, b.darksand, b.stone, b.stone, b.stone, b.basalt, b.snow, b.snow, b.iceSnow, b.iceSnow, b.ice],
    [b.deepwater, b.water, b.darksandWater, b.darksand, b.darksand, b.darksand, b.stone, b.stone, b.stone, b.basalt, b.basalt, b.snow, b.snow, b.iceSnow, b.ice],
    [b.deepwater, b.water, b.darksandWater, b.darksand, b.darksand, b.darksand, b.stone, b.stone, b.stone, b.basalt, b.basalt, b.snow, b.snow, b.iceSnow, b.ice],
    [b.deepwater, b.water, b.darksandWater, b.darksand, b.darksand, b.darksand, b.stone, b.stone, b.stone, b.basalt, b.snow, b.snow, b.iceSnow, b.iceSnow, b.ice],
    
    [b.water, b.water, b.darksandWater, b.darksand, b.darksand, b.darksand, b.stone, b.stone, b.stone, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice],
    [b.water, b.water, b.darksandWater, b.darksand, b.darksand, b.stone, b.stone, b.snow, b.snow, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice],
    [b.water, b.darksandWater, b.darksand, b.stone, b.snow, b.snow, b.snow, b.snow, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice, b.ice],
    [b.water, b.darksandWater, b.snow, b.snow, b.snow, b.snow, b.snow, b.iceSnow, b.iceSnow, b.iceSnow, b.iceSnow, b.ice, b.ice, b.ice, b.ice]
];
megalithGen.water = 2 / megalithGen.arr[0].length;;
megalithGen.simplex = new Packages.arc.util.noise.Simplex();
megalithGen.waterOffset = 0.07;
megalithGen.scl = 6;
megalithGen.rid = new Packages.arc.util.noise.RidgedPerlin(1, 2);
megalithGen.basegen = new BaseGenerator();

const megalith = new JavaAdapter(Planet, {}, "megalith", Planets.sun, 3, 1);
megalith.meshLoader = () => new HexMesh(megalith, 6);
megalith.generator = megalithGen;
megalith.atmosphereColor = Color.valueOf("0f3ad2");
megalith.startSector = 30;

module.exports = megalith;
