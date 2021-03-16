package unity.entities.effects;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.BaseTurret.*;

public class ParentEffect extends Effect{
    public ParentEffect(float life, Cons<EffectContainer> renderer){
        super(life, renderer);
    }

    @Override
    public void at(float x, float y, float rotation, Object data){
        at(x, y, rotation, Color.white, data);
    }

    @Override
    public void at(float x, float y, float rotation, Color color, Object data){
        create(this, x, y, rotation, color, data);
    }

    public static void create(Effect effect, float x, float y, float rotation, Color color, Object data){
        if(Vars.headless || effect == Fx.none) return;
        if(Core.settings.getBool("effects")){
            Rect view = Core.camera.bounds(Tmp.r1);
            Rect pos = Tmp.r2.setSize(effect.clip).setCenter(x, y);

            if(view.overlaps(pos)){
                ParentEffectState entity = createState();
                entity.effect = effect;
                entity.rotation = rotation;
                entity.originalRotation = rotation;
                entity.data = (data);
                entity.lifetime = (effect.lifetime);
                entity.set(x, y);
                entity.color.set(color);
                if(data instanceof Posc) entity.parent = ((Posc)data);
                entity.add();
            }
        }
    }

    public static ParentEffectState createState(){
        return Pools.obtain(ParentEffectState.class, ParentEffectState::new);
    }

    public static class ParentEffectState extends EffectState{
        public float originalRotation = 0f;

        @Override
        public void update(){
            super.update();

            if(parent != null){
                float rotationA = 0f;
                if(parent instanceof Rotc){
                    rotationA = ((Rotc)parent).rotation();
                }else if(parent instanceof BaseTurretBuild){
                    rotationA = ((BaseTurretBuild)parent).rotation;
                }
                rotation = rotationA - originalRotation;
                //float angle = Mathf.angle(offsetX, offsetY);
                float len = (float)Math.sqrt(offsetX * offsetX + offsetY * offsetY);
                Tmp.v1.trns(rotationA, len).add(parent);
                x = Tmp.v1.x;
                y = Tmp.v1.y;
            }
        }
    }
}
