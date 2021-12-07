package unity.entities.bullet.anticheat;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.gen.*;
import unity.entities.effects.*;
import unity.gen.*;

public class SlowLightningBulletType extends AntiCheatBulletTypeBase{
    protected float range = 870f, nodeLength = 80f, nodeTime = 7f, splitChance = 0.06f;
    protected SlowLightningType type;

    public SlowLightningBulletType(float damage){
        super(0f, damage);
        lifetime = 160f;
        collides = false;
        hittable = absorbable = reflectable = false;
        keepVelocity = false;
        despawnEffect = hitEffect = Fx.none;
    }

    @Override
    public float range(){
        return range * 0.8f;
    }

    @Override
    public void init(){
        super.init();

        SlowLightningBulletType b = this;

        type = new SlowLightningType(){
            {
                damage = b.damage;
                lifetime = b.lifetime;
                range = b.range;
                nodeLength = b.nodeLength;
                nodeTime = b.nodeTime;
                colorFrom = Color.red;
                colorTo = Color.black;
                splitChance = b.splitChance;
                continuous = true;
                lineWidth = 3f;
            }

            @Override
            public void damageUnit(SlowLightningNode s, Unit unit){
                if(s.main.bullet != null && s.main.bullet.type == b){
                    b.hitUnitAntiCheat(s.main.bullet, unit);
                }
            }

            @Override
            public void damageBuilding(SlowLightningNode s, Building building){
                if(s.main.bullet != null && s.main.bullet.type == b){
                    b.hitBuildingAntiCheat(s.main.bullet, building);
                }
            }

            @Override
            public void hit(SlowLightningNode s, float x, float y){
                super.hit(s, x, y);
                if(s.main.bullet != null && s.main.bullet.type == b){
                    b.hit(s.main.bullet, x, y);
                }
            }
        };
    }

    @Override
    public void init(Bullet b){
        b.data = type.create(b.team, b, b.x, b.y, b.rotation(), null, b.owner instanceof Posc ? (Posc)b.owner : null, null);
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof SlowLightning){
            SlowLightning data = (SlowLightning)b.data;
            b.x = data.x;
            b.y = data.y;
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void draw(Bullet b){

    }
}
