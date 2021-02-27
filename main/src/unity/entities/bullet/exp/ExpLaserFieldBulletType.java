package unity.entities.bullet.exp;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import unity.content.UnityFx;
import unity.entities.comp.ExpBuildc;

public class ExpLaserFieldBulletType extends ExpLaserBulletType {
    public BulletType distField;
    public float basicFieldRadius;

    public ExpLaserFieldBulletType(float length, float damage){
        super(length, damage);
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        setDamage(b);

        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), getLength(b));
        b.data = target;

        if(target instanceof Hitboxc hit){
            hit.collision(b, hit.x(), hit.y());
            b.collision(hit, hit.x(), hit.y());
            if(b.owner instanceof ExpBuildc exp){
                if(exp.levelf() < 1 && Core.settings.getBool("hitexpeffect")){
                    for(int i = 0; i < Math.ceil(hitUnitExpGain); i++) UnityFx.expGain.at(hit.x(), hit.y(), 0f, b.owner);
                }
                Sounds.spark.at(hit.x(), hit.y(),0.4f);
                Sounds.spray.at(hit.x(), hit.y(),0.4f);

                distField.create(b, b.team, hit.x(), hit.y(), 0f, 1f, 1f);

                exp.incExp(hitUnitExpGain);
            }
        }else if(target instanceof Building tile && tile.collide(b)){
            tile.collision(b);
            hit(b, tile.x, tile.y);
            if(b.owner instanceof ExpBuildc exp){
                if(exp.levelf() < 1 && Core.settings.getBool("hitexpeffect")){
                    for(int i = 0; i < Math.ceil(hitBuildingExpGain); i++) UnityFx.expGain.at(tile.x, tile.y, 0f, b.owner);
                }
                Sounds.spark.at(tile.x(), tile.y(),0.4f);
                Sounds.spray.at(tile.x(), tile.y(),0.4f);

                distField.create(b, b.team, tile.x(), tile.y(), 0f, -1f, 1f, 1f, basicFieldRadius / 2);

                exp.incExp(hitBuildingExpGain);
            }
        }
        else {
            b.data = new Vec2().trns(b.rotation(), getLength(b)).add(b.x, b.y);
            float tx = ((Vec2) b.data).x;
            float ty = ((Vec2) b.data).y;

            Sounds.spark.at(tx, ty,0.4f);
            Sounds.spray.at(tx, ty,0.4f);
            distField.create(b.owner, b.team, tx, ty, 0f, -1f, 1f, 0.25f, new Float[]{basicFieldRadius / 5f, 2f});

            final Bullet tmpB = b;
            float tempRadius = basicFieldRadius;
            tmpB.data = new Vec2().trns(tmpB.rotation(), getLength(tmpB)).add(tmpB.x, tmpB.y);
            for(int i = 0; i < 5; i++) Time.run(0.1f * 60 * i + 1, () -> {
                float ttx = tx + Mathf.range(8) * Vars.tilesize;
                float tty = ty + Mathf.range(8) * Vars.tilesize;
                Sounds.spark.at(ttx, tty,0.4f);
                Sounds.spray.at(ttx, tty,0.4f);
                distField.create(tmpB.owner, tmpB.team, ttx, tty, 0f, -1f, 1f, 0.25f, new Float[]{tempRadius / 5f, 2f});
            });
        }
    }
}
