//Core functions

const tV = new Vec2();

const collidedBlocks = new IntSet();

const rect = new Rect();
const hitrect = new Rect();

module.exports = {
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
	}
};