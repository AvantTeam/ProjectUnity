package unity.entities.bullet.exp;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.*;
import unity.gen.Expc.*;
import unity.world.blocks.exp.ExpTurret;

import static unity.content.UnityBullets.smallDistField;

public class ExpLaserFieldBulletType extends ExpLaserBulletType{
    public BulletType distField;
    public BulletType smallDistField;

    public ExpLaserFieldBulletType(float length, float damage){
        super(length, damage);
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        setDamage(b);

        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), getLength(b));

        if(target instanceof Unit unit){
            unit.collision(b, unit.x, unit.y);
            b.collision(unit, unit.x, unit.y);

            Sounds.spark.at(unit.x, unit.y,0.4f);
            Sounds.spray.at(unit.x, unit.y,0.4f);
            distField.create(b.owner, b.team, unit.x, unit.y, 0f);

            if(b.owner instanceof ExpBuildc exp){
                if(exp.levelf() < 1 && Core.settings.getBool("hitexpeffect")){
                    for(int i = 0; i < Math.ceil(expGain); i++) UnityFx.expGain.at(unit.x, unit.y, 0f, b.owner);
                }
                exp.incExp(expGain);
            }
        }else if(target instanceof Building tile && tile.collide(b)){
            tile.collision(b);
            hit(b, tile.x, tile.y);

            Sounds.spark.at(tile.x, tile.y,0.4f);
            Sounds.spray.at(tile.x, tile.y,0.4f);
            distField.create(b.owner, b.team, tile.x, tile.y, 0f);

            if(b.owner instanceof ExpBuildc exp){
                if(exp.levelf() < 1 && Core.settings.getBool("hitexpeffect")){
                    for(int i = 0; i < Math.ceil(buildingExpGain); i++) UnityFx.expGain.at(tile.x, tile.y, 0f, b.owner);
                }
                exp.incExp(buildingExpGain);
            }
        }
        else{
            Vec2 vec = new Vec2().trns(b.rotation(), getLength(b)).add(b.x, b.y);
            for(int i = 0; i < 5; i++) Time.run(0.1f * 60 * i + 1, () ->{
                float tx = vec.x + Mathf.range(8) * Vars.tilesize;
                float ty = vec.y + Mathf.range(8) * Vars.tilesize;
                Sounds.spark.at(tx, ty,0.4f);
                Sounds.spray.at(tx, ty,0.4f);
                smallDistField.create(b.owner, b.team, tx, ty, 0f);
            });
        }
    }
}
