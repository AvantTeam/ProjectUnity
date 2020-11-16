package unity.entities.bullet;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.*;
import unity.entities.BData;
import unity.graphics.UnityPal;

import static mindustry.Vars.world;

public class CContinuousLaserBulletType extends ContinuousLaserBulletType{
    public CContinuousLaserBulletType(float damage){
        super(damage);
    }

    public CContinuousLaserBulletType(){
        this(0f);
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof BData && b.owner instanceof Unit){
            BData tempData = (BData) b.data;
            Unit tempUnit = (Unit) b.owner;
            if(tempData.isP && tempUnit.isValid()){
                tempData.t.update(tempUnit.x, tempUnit.y);
                float sx = tempUnit.aimX();
                float sy = tempUnit.aimY();
                if(Mathf.within(sx, sy, tempUnit.x, tempUnit.y, 280f)){
                    float dstDamage = Mathf.dst(sx, sy, tempData.v.x, tempData.v.y) / Time.delta;
                    int size = 4;
                    Units.nearbyEnemies(b.team, sx - size, sy - size, size * 2f, size * 2f, e -> {
                        if(Mathf.within(sx, sy, e.x, e.y, e.hitSize) && dstDamage > 2f && e.isValid()){
                            e.damage(dstDamage * 8f);
                            //
                        }
                    });
                    Building build = world.buildWorld(sx, sy);
                    if(build != null && build.team != b.team && dstDamage > 2f){
                        build.damage(dstDamage * 5f);
                        //
                    }
                }
                tempData.v.set(sx, sy);
            }
        }else super.update(b);
    }

    @Override
    public void init(Bullet b){
        if(b.owner instanceof Unit){
            Unit temp = (Unit) b.owner;
            if(temp.controller() instanceof Player) b.data = new BData(true, 4, temp.aimX(), temp.aimY());
            else if(temp.isValid()) b.data = new BData(false, 4);
        }
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof BData && b.owner instanceof Unit){
            BData tempData = (BData) b.data;
            Unit tempUnit = (Unit) b.owner;
            if(tempData.isP && Mathf.within(tempUnit.aimX(), tempUnit.aimY(), tempUnit.x, tempUnit.y, 280f)) tempData.t.draw(UnityPal.scarColor, 3f);
        }else super.draw(b);
    }
}
