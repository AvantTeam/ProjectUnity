let msgIndex = 0;
const msgCount = 60; //it's gonna be a long time writing this to the bundles

const megalithGen = extend(PlanetGenerator, {
    rawHeight(pos){
        pos = Tmp.v33.set(pos).scl(this.scl);
        
        return (Mathf.pow(this.simplex.octaveNoise3D(7, 0.5, 1 / 3, pos.x, pos.y, pos.z), 2.3) + this.waterOffset) / (1 + this.waterOffset);
    },

    getHeight(pos){
        let height = this.rawHeight(pos);
        
        return Math.max(height, this.water);
    },

    getColor(pos){
        let block = this.getBlock(pos);

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
        let height = this.rawHeight(pos);

        Tmp.v31.set(pos);

        pos = Tmp.v33.set(pos).scl(this.scl);

        let rad = this.scl;
        let temp = Mathf.clamp(Math.abs(pos.y * 2) / rad);
        let tnoise = this.simplex.octaveNoise3D(7, 0.56, 1 / 3, pos.x, pos.y + 999, pos.z);

        temp = Mathf.lerp(temp, tnoise, 0.5);
        height *= 1.2;
        height = Mathf.clamp(height);

        return this.arr[Mathf.clamp(Math.floor((temp * this.arr.length)), 0, this.arr[0].length - 1)][Mathf.clamp(Math.floor((height * this.arr[0].length)), 0, this.arr[0].length - 1)];
    },
    
    octNoise(x, y, octaves, falloff, scl){
        let v = this.sector.rect.project(x, y).scl(5);
        
        return this.simplex.octaveNoise3D(octaves, falloff, 1 / scl, v.x, v.y, v.z);
    },
    
    generate(tiles, sec){
        this.tiles = tiles;
        this.sector = sec;
        this.rand.setSeed(sec.id);

        let gen = new TileGen();
        this.tiles.each((x, y) => {
            gen.reset();
            let position = this.sector.rect.project(x / tiles.width, y / tiles.height);

            this.genTile(position, gen);
            this.tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
        });

        this.width = this.tiles.width;
        this.height = this.tiles.height;

        const t = this;

        const room = {
            x: 0,
            y: 0,
            radius: 0,

            connected: new ObjectSet(),

            connect(to){
                if(this.connected.contains(to)) return;

                this.connected.add(to);

                let nscl = t.rand.random(20, 60);
                let stroke = t.rand.random(4, 12);

                t.brush(t.pathfind(this.x, this.y, to.x, to.y, tile => (tile.solid() ? 5 : 0) + t.octNoise(tile.x, tile.y, 1, 1, 1 / nscl) * 48, Astar.manhattan), stroke);
            }
        };

        const newRoom = (x, y, rad) => {
            const r = Object.create(room);
            r.x = x;
            r.y = y;
            r.radius = rad;
            
            return r;
        };

        this.cells(4);
        this.distort(10, 12);

        let scl = Math.min(this.width, this.height);
        let roomPos = scl / 3 / Mathf.sqrt3;
        let roomCount = this.rand.random(2, 4);
        let rooms = new Seq();

        for(let i = 0; i < roomCount; i++){
            Tmp.v1.trns(this.rand.random(360), this.rand.random(roomPos));

            let angle = Tmp.v1.angle();
            let tx = this.width / 2 + Tmp.v1.x;
            let ty = this.height / 2 + Tmp.v2.y;
            let rad = Math.min(this.rand.random(6, (roomPos - Tmp.v1.len()) / 2), 24);
            let subRooms = this.rand.random(0, Math.floor(rad / 6));

            let r = newRoom(Math.floor(tx), Math.floor(ty), Math.floor(rad));
            rooms.add(r);

            for(let j = 0; j < subRooms; j++){
                let cone = this.rand.random(45);

                Tmp.v2.trns(this.rand.random(Math.floor(angle - cone), Math.floor(angle + cone)), this.rand.random(rad));

                let subAngle = Tmp.v2.angle();
                let tx2 = tx + Tmp.v2.x;
                let ty2 = ty + Tmp.v2.y;
                let rad2 = rad / 2.5;

                let r2 = newRoom(Math.floor(tx2), Math.floor(ty2), Math.floor(rad2));
                r.connect(r2);
            };
        };
        
        let spawn = null;
        let enemies = new Seq();
        let enemySpawns = this.rand.chance(0.3) ? 3 : 2;
        let angleStep = 5;
        let offset = scl / 2 - this.rand.random(17, 48);
        let offsetAngle = this.rand.nextInt(360);
        let waterCheck = 5;

        for(let i = 0; i < 360; i += angleStep){
            let angle = offsetAngle + i;

            let tx = Math.floor(this.width / 2 + Angles.trnsx(angle, offset));
            let ty = Math.floor(this.height / 2 + Angles.trnsx(angle, offset));

            let waterTiles = 0;

            for(let rx = -waterCheck; rx <= waterCheck; rx++){
                for(let ry = -waterCheck; ry <= waterCheck; ry++){
                    let tile = this.tiles.get(tx + rx, ty + ry);

                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles++;
                    };
                };
            };

            if(waterTiles <= 4 || i + angleStep >= 360){
                spawn = newRoom(tx, ty, this.rand.random(12, 16));
                rooms.add(spawn);

                for(let j = 0; j < enemySpawns; j++){
                    let enemyOffset = this.rand.range(60);

                    Tmp.v1.set(tx - this.width / 2, ty - this.height / 2).rotate(180 + enemyOffset).add(this.width / 2, this.height / 2);

                    let espawn = newRoom(Tmp.v1.x, Tmp.v1.y, this.rand.random(12, 16));

                    rooms.add(espawn);
                    enemies.add(espawn);
                };

                break;
            };
        };
        
        rooms.each(r => this.erase(r.x, r.y, r.radius));

        let connections = this.rand.random(Math.max(roomCount - 1, 1), roomCount + 3);
        for(let i = 0; i < connections; i++){
            rooms.random(this.rand).connect(rooms.random(this.rand));
        };

        rooms.each(r => spawn.connect(r));

        this.cells(1);
        this.distort(10, 6);
        this.inverseFloodFill(this.tiles.getn(spawn.x, spawn.y));
        
        let ores = Seq.with(Blocks.oreCopper, Blocks.oreLead, Vars.content.getByName(ContentType.block, "unity-monolite"));

        let poles = Math.abs(this.sector.tile.v.y);
        let nmag = 0.5;
        let addscl = 1.3;

        if(this.simplex.octaveNoise3D(2, 0.5, 1, this.sector.tile.v.x, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.25 * addscl){
            ores.add(Blocks.oreCoal);
        };

        if(this.simplex.octaveNoise3D(2, 0.5, 1, this.sector.tile.v.x + 1, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.5 * addscl){
            ores.add(Blocks.oreTitanium);
        };

        if(this.simplex.octaveNoise3D(2, 0.5, 1, this.sector.tile.v.x + 3, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.7 * addscl){
            ores.add(Blocks.oreThorium);
        };

        let frequencies = new FloatSeq();
        for(let i = 0; i < ores.size; i++){
            frequencies.add(this.rand.random(-0.09, 0.01) - i * 0.01);
        };
        
        this.trimDark();
        this.median(2);
        this.tech();

        this.pass((x, y) => {
            if(this.floor.asFloor().isLiquid) return;

            let offsetX = x - 4;
            let offsetY = y + 23;
            for(let i = ores.size - 1; i >= 0; i--){
                let entry = ores.get(i);
                let freq = frequencies.get(i);

                if(
                    Math.abs(0.5 - this.octNoise(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22 + i * 0.01 &&
                    Math.abs(0.5 - this.octNoise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37 + freq
                ){
                    this.ore = entry;

                    break;
                };
            };
        });

        let difficulty = this.sector.threat;
        this.ints.clear();
        this.ints.ensureCapacity(this.width * this.height / 4);

        /*let hasMessage = false;

        rooms.each(r => {
            if(r == spawn || hasMessage) return;

            let angleStep2 = 10;
            let offset = this.rand.random(360);

            for(let i = 0; i < 360; i += angleStep2){
                let angle = offset + i;

                Tmp.v1.trns(angle, r.radius - this.rand.random(r.radius / 2)).add(r.x, r.y);
                let tile = this.tiles.get(Tmp.v1.x, Tmp.v1.y);

                if(this.rand.chance(0.1) || i + angleStep2 >= 360){
                    hasMessage = true;

                    const newBuild = prov(() => {
                        const build = extend(MessageBlock.MessageBuild, Blocks.message, {});
                        build.message.append(this.nextMessage());

                        return build;
                    });

                    if(tile == null){
                        tile = new Tile(Tmp.v1.x, Tmp.v1.y);
                        this.tiles.set(Tmp.v1.x, Tmp.v1.y, tile);
                    };

                    tile.setFloor(Blocks.metalFloor.asFloor());
                    tile.setBlock(Blocks.message, Team.sharded, 0, newBuild);
                };
            };
        });*/

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn => this.tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        if(this.sector.hasEnemyBase()){
            this.basegen.generate(tiles, enemies.map(r => this.tiles.getn(r.x, r.y)), this.tiles.get(spawn.x, spawn.y), Vars.state.rules.waveTeam, this.sector, difficulty);

            Vars.state.rules.attackMode = true;
        }else{
            Vars.state.rules.winWave = 15 * Math.floor(Math.max(difficulty * 5, 1));
        };

        let waveTimeDec = 0.3;

        Vars.state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60 * 60 * 1, Math.max(difficulty - waveTimeDec, 0) / 0.8);
        Vars.state.rules.waves = this.sector.info.waves = true;
        Vars.state.rules.enemyCoreBuildRadius = 600;

        Vars.state.rules.spawns = Waves.generate(difficulty, new Rand(), Vars.state.rules.attackMode);
    },

    nextMessage(){
        msgIndex++;
        if(msgIndex > msgCount) return "...";

        return Core.bundle.get("lore.unity.megalith-" + msgIndex);
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
