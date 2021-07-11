package unity.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.util.*;

/**
 * RailBulletType with modifiable speed.
 */
public class SlowRailBulletType extends BasicBulletType{
    public float trailSpacing = 5f;
    private static boolean hit = false;

    public SlowRailBulletType(float speed, float damage){
        super(speed, damage);
        collides = collidesTiles = false;
        pierce = pierceBuilding = true;
        trailEffect = TrailFx.coloredRailgunTrail;
    }

    @Override
    public void update(Bullet b){
        hit = false;
        Utils.collideLineRawEnemy(b.team, b.lastX, b.lastY, b.x, b.y, 3f, 3f, (building, direct) -> {
            if(direct && !b.collided.contains(building.id)){
                float h = building.health;
                building.collision(b);
                hitTile(b, building, h, true);
                b.collided.add(building.id);
            }
            return (hit = (building.block.absorbLasers || b.collided.size >= pierceCap));
        }, unit -> {
            if(!b.collided.contains(unit.id)){
                hitEntity(b, unit, unit.health);
                b.collided.add(unit.id);
            }
            return (hit = b.collided.size >= pierceCap);
        }, (x, y) -> {
            if(hit){
                Tmp.v1.trns(b.rotation(), Mathf.dst(b.lastX, b.lastY, x, y)).add(b.lastX, b.lastY);
                b.set(Tmp.v1);
                b.vel.setZero();
            }
            hit(b, x, y);
        }, true);
        b.fdata += b.deltaLen();
        if(b.fdata >= trailSpacing){
            trailEffect.at(b.x, b.y, b.rotation(), trailColor);
            b.fdata = 0f;
        }
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);
        float height = this.height * ((1f - shrinkY) + shrinkY * b.fout());
        float width = this.width * ((1f - shrinkX) + shrinkX * b.fout());
        Tmp.v1.trns(b.rotation(), height / 2f);
        Draw.color(backColor);
        Drawf.tri(b.x, b.y, width, speed * lifetime * 0.75f * b.fin(), b.rotation() + 180f);
        for(int s : Mathf.signs){
            Tmp.v2.trns(b.rotation() - 90f, width * s, -height);
            Draw.color(backColor);
            Fill.tri(Tmp.v1.x + b.x, Tmp.v1.y + b.y, -Tmp.v1.x + b.x, -Tmp.v1.y + b.y, Tmp.v2.x + b.x, Tmp.v2.y + b.y);
            Draw.color(frontColor);
            Fill.tri(Tmp.v1.x / 2f + b.x, Tmp.v1.y / 2f + b.y, -Tmp.v1.x / 2f + b.x, -Tmp.v1.y / 2f + b.y, Tmp.v2.x / 2f + b.x, Tmp.v2.y / 2f + b.y);
        }
    }
}
