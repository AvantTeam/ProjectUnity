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
		const t = this;
		
		const Room = (x, y, radius) => {
			this.x = x;
			this.y = y;
			this.radius = radius;
			
			this.connected = new ObjectSet();
			
			this.connect = to => {
				if(this.connected.contains(to)) return;
				
				this.connected.add(to);
				
				var nscl = t.rand.random(20, 60);
				var stroke = t.rand.random(4, 12);
				
				this.brush(t.pathfind(x, y, to.x, to.y, tile => (tile.solid() ? 5 : 0) + t.noise(tile.x, tile.y, 1, 1, 1 / nscl) * 60, Astar.manhattan), stroke);
			};
		};
		
		this.cells(4);
		this.distort(10, 12);
		
		var constraint = 1.3;
		var radius = width / 2 / Mathf.sqrt3;
		var rooms = this.rand.random(2, 5);
		var array = new Seq(Room);
		
		for(var i = 0; i < rooms; i++){
			Tmp.v1.trns(this.rand.random(360), this.rand.random(radius / constraint));
			
			var rx = width / 2 + Tmp.v1.x;
			var ry = height / 2 + Tmp.v1.y;
			var maxrad = radius - Tmp.v1.len();
			var rrad = Math.min(this.rand.random(9, maxrad / 2), 30);
			
			array.add(new Room(Math.floor(rx), Math.floor(ry), Math.floor(rrad)));
		};
		
		var spawn = null;
		var enemies = new Seq(Room);
		var enemySpawns = this.rand.chance(0.3) ? 2 : 1;
		var offset = this.rand.nextInt(360);
		var length = width / 2.55 - this.rand.random(13, 23);
		var angleStep = 5;
		var waterCheckRad = 5;
		
		for(var i = 0; i < 360; i += angleStep){
			var angle = offset + i;
			var cx = Math.floor(width / 2 + Angles.trnsx(angle, length));
			var cy = Math.floor(height / 2 + Angles.trnsy(angle, length));
			
			var waterTiles = 0;
			
			for(var rx = -waterCheckRad; rx <= waterCheckRad; rx++){
				for(var ry = -waterCheckRad; ry <= waterCheckRad; ry++){
					var tile = this.tiles.get(cx + rx, cy + ry);
					if(tile == null || tile.floor().liquidDrop != null){
						waterTiles++;
					};
				};
			};
			
			if(waterTiles <= 4 || i + angleStep >= 360){
				spawn = new Room(cx, cy, this.rand.random(8, 15))
				array.add(spawn);
				
				for(var j = 0; j < enemySpawns; j++){
					var enemyOffset = this.rand.range(60);
					Tmp.v1.set(cx - width / 2, cy - height / 2).rotate(180 + enemyOffset).add(width / 2, height / 2);
					var espawn = new Room(Tmp.v1.x, Tmp.v1.y, this.rand.random(8, 15));
					array.add(espawn);
					enemies.add(espawn);
				};
				
				break;
			};
		};
		
		array.each(room => this.erase(room.x, room.y, room.radius));
		
		var connections = this.rand.random(Math.max(rooms - 1, 1), rooms + 3);
		
		for(var i = 0; i < connections; i++){
			array.random(this.rand).connect(array.random(this.rand));
		};
		
		array.each(room => spawn.connect(room));
		
		this.cells(1);
		this.distort(10, 6);
		
		this.inverseFloodFill(this.tiles.getn(spawn.x, spawn.y));
		
		var ores = Seq.with(Blocks.oreCopper, Blocks.oreLead);
		
		var poles = Math.abs(this.sector.tile.v.y);
		var nmag = 0.5;
		var scl = 1;
		var addscl = 1.3;
		
		if(this.simplex.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.25 * addscl){
			ores.add(Blocks.oreCoal);
		};
		
		if(this.simplex.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x + 1, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.5 * addscl){
			ores.add(Blocks.oreTitanium);
		};
		
		if(this.simplex.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x + 2, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.6 * addscl){
			ores.add(Vars.content.getByName(ContentType.block, "unity-monolite"));
		};
		
		if(this.simplex.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x + 3, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.7 * addscl){
			ores.add(Blocks.oreThorium);
		};
		
		var frequencies = new FloatSeq();
		for(var i = 0; i < ores.size; i++){
			frequencies.add(this.rand.random(-0.09, 0.01) - i * 0.01);
		};
		
		this.pass((x, y) => {
			if(this.floor.asFloor().isLiquid) return;
			
			var offsetX = x - 4;
			var offsetY = y + 23;
			for(var i = ores.size - 1; i >= 0; i--){
				var entry = ores.get(i);
				var freq = frequencies.get(i);
				
				if(
					Math.abs(0.5 - this.noise(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22 + i * 0.01 &&
					Math.abs(0.5 - this.noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37 + freq
				){
					this.ore = entry;
					break;
				};
			};
		});
		
		enemies.each(espawn => this.tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));
		
		this.trimDark();
		
		this.median(2);
		
		this.tech();
		
		Schematics.placeLaunchLoadout(spawn.x, spawn.y);
		
		var difficulty = this.sector.baseCoverage;
		
		if(this.sector.hasEnemyBase()){
			this.basegen.generate(tiles, enemies.map(r => this.tiles.getn(r.x, r.y)), this.tiles.get(spawn.x, spawn.y), Vars.state.rules.waveTeam, this.sector, difficulty);
			
			Vars.state.rules.attackMode = true;
		}else{
			Vars.state.rules.winWave = 15 * Math.floor(Math.max(difficulty * 5, 1));
		};
		
		Vars.state.rules.waves = true;
		
		Vars.state.rules.spawns = Vars.defaultWaves.get();
		
		var waveScaling = 1 + difficulty * 2;
		
		for(var group of Vars.state.rules.spawns){
			group.unitAmount *= waveScaling;
			
			if(group.unitScaling != SpawnGroup.never){
				group.unitScaling /= waveScaling;
			};
		};
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
