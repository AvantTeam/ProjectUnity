package unity.entities.bullet;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class VelocityLaserBoltBulletType extends BasicBulletType{
    public VelocityLaserBoltBulletType(float speed, float damage){
        super(speed, damage);
        backColor = Color.valueOf("a9d8ff");
        frontColor = Color.valueOf("ffffff");
        width = 4.75f;
        height = 4f;
        hitEffect = Fx.hitLancer;
        despawnEffect = Fx.hitLancer;
        shootEffect = Fx.none;
        smokeEffect = Fx.none;
    }

    @Override
    public void load(){
        frontRegion = Core.atlas.find("circle");
    }

    @Override
    public void draw(Bullet b){
        float vel = b.vel().len() * 4f;
		
		Draw.color(backColor);
		Draw.rect(frontRegion, b.x, b.y, width, height + vel, b.rotation() - 90f);
		
		Draw.color(frontColor);
		Draw.rect(frontRegion, b.x, b.y, width * 0.625f, height * 0.625f + (vel / 1.2f), b.rotation() - 90f);
    }
}
