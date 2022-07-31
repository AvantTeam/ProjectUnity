package unity.parts.types;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.world.*;
import unity.content.*;
import unity.gen.entities.*;
import unity.parts.*;
import unity.parts.stats.*;
import unity.util.*;

public class ModularUnitMovementType extends ModularUnitPartType{
    TextureRegion base, moving;

    public ModularUnitMovementType(String name){
        super(name);
        open = true;
    }

    @Override
    public void load(){
        super.load();
        base = getPartSprite("unity-part-" + name + "-base");
        moving = getPartSprite("unity-part-" + name + "-moving");
    }

    public void wheel(float wheelStrength, float nominalWeight, float maxSpeed){
        stats.add(new WheelStat(wheelStrength, nominalWeight, maxSpeed));
    }

    //eh, this is ugly too.
    @Override
    public void draw(DrawTransform transform, Part part, Modularc parent){
        super.draw(transform, part, parent);
        Vec2 pos = new Vec2(part.cx() * partSize, part.cy() * partSize);
        transform.transform(pos);
        var rollDistance = parent != null ? parent.driveDist() : 0;
        float ang = rollDistance * 16;

        transform.drawRect(base, part.cx() * partSize, part.cy() * partSize);
        if(h == 1){
            for(int i = 0; i < 4; i++){
                DrawUtils.drawRotRect(moving, pos.x, pos.y, w * partSize, moving.height * Draw.scl, moving.height * Draw.scl, transform.getRotation() - 90, ang + i * 90, ang + i * 90 + 90);
            }
        }else{
            float treadLength = h * partSize - partSize + partSize * Mathf.pi;
            float offset = (rollDistance * Draw.scl) % (moving.height * Draw.scl);
            for(float i = 0; i < treadLength; i += moving.height * Draw.scl){
                DrawUtils.drawTread(moving, pos.x, pos.y, w * partSize, (h + 0.5f) * partSize, partSize * 0.5f, transform.getRotation() - 90, i + offset, i + offset + moving.height * Draw.scl);
            }
        }
        //dust fx.
        if(parent == null) return;
        var vel = parent.vel();
        var velLen = vel.len();
        if(velLen > 0.01f && parent.elevation() < 0.01 && Mathf.random() <= 0.1 * Time.delta && part.type == this){
            float dustVel = 0;
            if(vel.len2() > 0.1f) dustVel = velLen - parent.statSpeed();
            pos.set(part.cx() * partSize, part.ay() * partSize);
            transform.transform(pos);

            Vec2 nv = vel.cpy().nor().scl(dustVel * 40);
            nv.x += Mathf.range(3);
            nv.y += Mathf.range(3);
            Tile t = Vars.world.tileWorld(pos.x, pos.y);
            if(t != null) YoungchaFx.dust.at(pos.x, pos.y, 0, t.floor().mapColor, nv);
        }
    }
}
