//Core functions

const tV = new Vec2();

const collidedBlocks = new IntSet(127);

const rect = new Rect();
const rectAlt = new Rect();
const hitrect = new Rect();

module.exports = {
	//recoded drawer found in UnitType, but without the applyColor function. doesnt draw outlines, only used for certain effects.
	simpleUnitDrawer(unit, drawLegs){
		var type = unit.type;

		if(drawLegs){
			//TODO draw legs
			if(unit instanceof Mechc){

			};
		};

		Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90);

		for(var i = 0; i < unit.mounts.length; i++){
			var mount = unit.mounts[i];
			var weapon = mount.weapon;

			var rotation = unit.rotation - 90;
			var weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0);
			var recoil = -((mount.reload) / weapon.reload * weapon.recoil);
			var wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil);
			var wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);

			Draw.rect(weapon.region,
			wx, wy,
			weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite),
			weapon.region.height * Draw.scl,
			weaponRotation);
		};
	},
	
	//consTile: (Tile, Build, Distance %, Angle Distance %)
	//consUnit: (Unit, Distance %, Angle Distance %)
	castCone(wx, wy, range, angle, cone, consTile, consUnit){
		collidedBlocks.clear();
        var expand = 3;
        
        if(consTile != null){
            rect.setCentered(wx, wy, expand);
            for(var i = 0; i < 3; i++){
                var angleC = (((-3 + i * 3) / 3) * cone) + angle;
                tV.trns(angleC, range).add(wx, wy);
                
                rectAlt.setCentered(tV.x, tV.y, expand);
                rect.merge(rectAlt);
            };
            var tx = Mathf.round(rect.x / Vars.tilesize);
            var ty = Mathf.round(rect.y / Vars.tilesize);
            var tw = tx + Mathf.round(rect.width / Vars.tilesize);
            var th = ty + Mathf.round(rect.height / Vars.tilesize);
            
            for(var x = tx; x <= tw; x++){
                yGroup:
                for(var y = ty; y <= th; y++){
                    if(!Mathf.within(x * Vars.tilesize, y * Vars.tilesize, wx, wy, range) || !Angles.within(Angles.angle(wx, wy, x * Vars.tilesize, y * Vars.tilesize), angle, cone)) continue yGroup;
					var other = Vars.world.tile(x, y);
					if(other == null) continue yGroup;
					if(!collidedBlocks.contains(other.pos())){
						var dst = 1 - (Mathf.dst(x * Vars.tilesize, y * Vars.tilesize, wx, wy) / range);
						var anDst = 1 - (Angles.angleDist(Angles.angle(wx, wy, x * Vars.tilesize, y * Vars.tilesize), angle) / cone);
						consTile(other, other.build != null ? other.build : null, dst, anDst);
						collidedBlocks.add(other.pos());
					};
                };
            };
        };
		if(consUnit != null){
			Groups.unit.intersect(wx - range, wy - range, range * 2, range * 2, e => {
				if(!Mathf.within(e.x, e.y, wx, wy, range) || !Angles.within(Angles.angle(wx, wy, e.x, e.y), angle, cone)) return;
				
				var dst = 1 - (Mathf.dst(e.x, e.y, wx, wy) / range);
				var anDst = 1 - (Angles.angleDist(Angles.angle(wx, wy, e.x, e.y), angle) / cone);
				consUnit(e, dst, anDst);
			});
		};
	},

	trueEachBlock(wx, wy, range, conss){
		collidedBlocks.clear();
		var tx = Vars.world.toTile(wx);
		var ty = Vars.world.toTile(wy);

		var tileRange = Mathf.floorPositive(range / Vars.tilesize + 1);
		var isCons = (conss instanceof Cons);

		for(var x = -tileRange + tx; x <= tileRange + tx; x++){
			yGroup:
			for(var y = -tileRange + ty; y <= tileRange + ty; y++){
				if(!Mathf.within(x * Vars.tilesize, y * Vars.tilesize, wx, wy, range)) continue yGroup;
				var other = Vars.world.build(x, y);
				if(other == null) continue yGroup;
				if(!collidedBlocks.contains(other.pos())){
					if(isCons){
						conss.get(other);
					}else{
						conss(other);
					};
					collidedBlocks.add(other.pos());
				};
			};
		};
	},
	/*
	targets anything thats not in the array.
	picks a random target if all potential targets is in the array.
	*/
	targetUnique(team, x, y, radius, targetSeq){
		var result = null;
		var cdist = (radius * radius) + 1;

		Units.nearbyEnemies(team, x - radius, y - radius, radius * 2, radius * 2, e => {
			if(!e.within(x, y, radius)) return;

			var dst = e.dst2(x, y);

			if(Mathf.dst2(x, y, e.x, e.y) < cdist && !targetSeq.contains(e)){
				result = e;
				cdist = dst;
			};
		});
		if(result == null) result = targetSeq.random();

		return result;
	},
	collideLineDamageOnly(team, damage, x, y, angle, length, bulletType){
		collidedBlocks.clear();
		tV.trns(angle, length);
		/*var collider = new Intc2(){
			get(cx, cy){
				var tile = world.build(cx, cy);
				if(tile != null && !collidedBlocks.contains(tile.pos()) && tile.team != team){
					//tile.collision(hitter);
					tile.damage(damage);
					collidedBlocks.add(tile.pos());
					//hitter.type.hit(hitter, tile.x, tile.y);
				}
			}
		};*/
		if(bulletType.collidesGround){
			Vars.world.raycastEachWorld(x, y, x + tV.x, y + tV.y, (cx, cy) => {
				//collider.get(cx, cy);
				var tile = Vars.world.build(cx, cy);
				if(tile != null && !collidedBlocks.contains(tile.pos()) && tile.team != team){
					//tile.collision(hitter);
					tile.damage(damage);
					collidedBlocks.add(tile.pos());
					//hitter.type.hit(hitter, tile.x, tile.y);
				};
				return false;
			});
		};

		rect.setPosition(x, y).setSize(tV.x, tV.y);

		var x2 = tV.x + x;
		var y2 = tV.y + y;

		if(rect.width < 0){
			rect.x += rect.width;
			rect.width *= -1;
		};
		if(rect.height < 0){
			rect.y += rect.height;
			rect.height *= -1;
		};

		const expand = 3;

		rect.y -= expand;
		rect.x -= expand;
		rect.width += expand * 2;
		rect.height += expand * 2;

		Units.nearbyEnemies(team, rect, e => {
			if(!e.checkTarget(bulletType.collidesAir, bulletType.collidesGround)) return;

			e.hitbox(hitrect);

			var vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

			if(vec != null){
				//effect.at(vec.x, vec.y);
				//e.collision(hitter, vec.x, vec.y);
				//hitter.collision(e, vec.x, vec.y);
				e.damage(damage);
			};
		});
	},

	chanceMultiple(chance, runF){
		var intC = Mathf.ceil(chance);
		var tmp = chance;
		for(var i = 0; i < intC; i++){
			if(tmp >= 1){
				runF.run();
				tmp -= 1;
			}else if(tmp > 0){
				if(Mathf.chance(tmp)){
					runF.run();
				}
			};
		};
	},

	customValue(method){
		return extend(StatValue, {
			display: method
		});
	}
};