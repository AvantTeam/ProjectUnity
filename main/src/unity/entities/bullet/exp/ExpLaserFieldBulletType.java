package unity.entities.bullet.exp;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import unity.content.*;
import unity.gen.Expc.*;
import unity.world.blocks.exp.ExpTurret;

import static unity.content.UnityBullets.smallDistField;

public class ExpLaserFieldBulletType extends ExpLaserBulletType{
    public BulletType distField;
    public BulletType smallDistField;
    public int fields;
    public float fieldInc;

    public ExpLaserFieldBulletType(float length, float damage){
        super(length, damage);
    }

    int getFields(Bullet b){
        return fields + Mathf.floor(fieldInc * getLevel(b) * b.damageMultiplier());
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        setDamage(b);

        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), getLength(b));

        Position vec = new Vec2().trns(b.rotation(), getLength(b)).add(b.x, b.y);

        if(target instanceof Unit || (target instanceof Building tile && tile.collide(b))) {
            vec = target;

            if (target instanceof Unit unit) {
                unit.collision(b, vec.getX(), vec.getY());
                b.collision(unit, vec.getX(), vec.getY());
            } else {
                ((Building)target).collision(b);
                hit(b, vec.getX(), vec.getY());
            }

            if (b.owner instanceof ExpBuildc exp) {
                if (exp.levelf() < 1 && Core.settings.getBool("hitexpeffect")) {
                    for (int i = 0; i < Math.ceil(buildingExpGain); i++)
                        UnityFx.expGain.at(vec.getX(), vec.getY(), 0f, b.owner);
                }
                exp.incExp(buildingExpGain);
            }

            distField.create(b.owner, b.team, vec.getX(), vec.getY(), 0f);
        }
        else smallDistField.create(b.owner, b.team, vec.getX(), vec.getY(), 0f);

        Sounds.spark.at(vec.getX(), vec.getY(),0.4f);
        Sounds.spray.at(vec.getX(), vec.getY(),0.4f);
        UnityFx.chainLightning.at(b.x, b.y, 0, getColor(b), vec);

        Position finalVec = vec;
        for(int i = 0; i < getFields(b); i++) {
            Time.run(0.1f * 60 * i + 1 + UnityFx.smallChainLightning.lifetime*0.5f, () ->{
                float tx = finalVec.getX() + Mathf.range(8) * Vars.tilesize;
                float ty = finalVec.getY() + Mathf.range(8) * Vars.tilesize;
                UnityFx.smallChainLightning.at(finalVec.getX(), finalVec.getY(), 0, getColor(b), new Vec2(tx, ty));
                Sounds.spark.at(tx, ty,0.4f);
                Sounds.spray.at(tx, ty,0.4f);
                smallDistField.create(b.owner, b.team, tx, ty, 0f);
            });
        }
    }
}
