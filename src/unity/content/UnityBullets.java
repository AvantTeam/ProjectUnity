package unity.content;

import arc.util.Tmp;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.entities.Damage;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.ctype.ContentList;
import mindustry.game.Team;
import unity.blocks.ExpBlockModule;

import static arc.Core.*;

public class UnityBullets implements ContentList{
	public static BulletType laser, coalBlaze, pyraBlaze;

	@Override
	public void load(){
		laser = new BulletType(0.01f, 30f){
			float length = 150f;
			float width = 0.7f;
			Color levelColor;
			TextureRegion laserRegion, laserEndRegion;

			@Override
			public void load(){
				laserRegion = atlas.find("laser");
				laserEndRegion = atlas.find("laser-end");
			}

			@Override
			public void init(Bullet b){
				if (b == null) return;
				Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
				b.data = target;
				ExpBlockModule.ExpBuildModule exp = UnityBlocks.laserExp.forBars.get(b.owner.id());
				int lvl = UnityBlocks.laserExp.getLevel(exp.totalExp());
				b.damage(damage + lvl * 10f);
				levelColor = Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, lvl / 10f);
				if (target instanceof Hitboxc){
					Hitboxc hit = (Hitboxc) target;
					hit.collision(b, hit.x(), hit.y());
					b.collision(hit, hit.x(), hit.y());
					exp.incExp(2);
				}else if (target instanceof Building){
					Building tile = (Building) target;
					if (tile.collide(b)){
						tile.collision(b);
						hit(b, tile.x, tile.y);
						exp.incExp(2);
					}
				}else b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
			}

			@Override
			public float range(){ return length; }

			@Override
			public void draw(Bullet b){
				if (b.data instanceof Position){
					Tmp.v1.set((Position) b.data);
					Draw.color(levelColor);
					Drawf.laser(b.team, laserRegion, laserEndRegion, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * b.fout());
					Draw.reset();
					Drawf.light(Team.derelict, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15f * b.fout() + 5f,
						levelColor, 0.6f);
				}
			}

			{
				lifetime = 18f;
				despawnEffect = Fx.none;
				pierce = true;
				hitSize = 0f;
				status = StatusEffects.shocked;
				statusDuration = 3 * 60f;
				hittable = false;
				hitEffect = Fx.hitLiquid;
			}
		};
		coalBlaze = new BulletType(3.35f, 32f){
			@Override
			public void hit(Bullet b, float x, float y){
				super.hit(b, x, y);
				((ExpBlockModule.ExpBuildModule) b.data).incExp(Mathf.random(1));
			}

			@Override
			public void init(Bullet b){ b.data = UnityBlocks.infernoExp.forBars.get(b.owner.id()); }

			{
				ammoMultiplier = 3;
				hitSize = 7f;
				lifetime = 24f;
				pierce = true;
				statusDuration = 60 * 4f;
				shootEffect = UnityFx.shootSmallBlaze;
				hitEffect = Fx.hitFlameSmall;
				despawnEffect = Fx.none;
				status = StatusEffects.burning;
				keepVelocity = true;
				hittable = true;
			}
		};
		pyraBlaze = new BulletType(3.35f, 46f){
			@Override
			public void hit(Bullet b, float x, float y){
				super.hit(b, x, y);
				((ExpBlockModule.ExpBuildModule) b.data).incExp(Mathf.random(1));
			}

			@Override
			public void init(Bullet b){ b.data = UnityBlocks.infernoExp.forBars.get(b.owner.id()); }

			{
				ammoMultiplier = 3;
				hitSize = 7f;
				lifetime = 24f;
				pierce = true;
				statusDuration = 60 * 4f;
				shootEffect = UnityFx.shootPyraBlaze;
				hitEffect = Fx.hitFlameSmall;
				despawnEffect = Fx.none;
				status = StatusEffects.burning;
				keepVelocity = false;
				hittable = false;
			}
		};
	}
}
