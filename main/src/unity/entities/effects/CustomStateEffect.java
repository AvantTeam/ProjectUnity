package unity.entities.effects;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;

public class CustomStateEffect extends Effect{
    Prov<? extends EffectState> stateProvider;

    public CustomStateEffect(Prov<? extends EffectState> prov, float lifetime, Cons<EffectContainer> container){
        this(prov, lifetime, 50f, container);
    }

    public CustomStateEffect(Prov<? extends EffectState> prov, float lifetime, float clip, Cons<EffectContainer> container){
        super(lifetime, clip, container);
        this.stateProvider = prov;
    }

    @Override
    public void at(Position pos){
        create(pos.getX(), pos.getY(), 0, Color.white, null);
    }

    @Override
    public void at(Position pos, boolean parentize){
        create(pos.getX(), pos.getY(), 0, Color.white, parentize ? pos : null);
    }

    @Override
    public void at(Position pos, float rotation){
        create(pos.getX(), pos.getY(), rotation, Color.white, null);
    }

    @Override
    public void at(float x, float y){
        create(x, y, 0, Color.white, null);
    }

    @Override
    public void at(float x, float y, float rotation){
        create(x, y, rotation, Color.white, null);
    }

    @Override
    public void at(float x, float y, float rotation, Color color){
        create(x, y, rotation, color, null);
    }

    @Override
    public void at(float x, float y, Color color){
        create(x, y, 0, color, null);
    }

    @Override
    public void at(float x, float y, float rotation, Color color, Object data){
        create(x, y, rotation, color, data);
    }

    @Override
    public void at(float x, float y, float rotation, Object data){
        create(x, y, rotation, Color.white, data);
    }

    void create(float x, float y, float rotation, Color color, Object data){
        if(Vars.headless || !Core.settings.getBool("effects")) return;

        if(Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(x, y, clip))){
            var entity = stateProvider.get();
            entity.effect = this;
            entity.rotation = baseRotation + rotation;
            entity.data = data;
            entity.lifetime = lifetime;
            entity.set(x, y);
            entity.color.set(color);
            if(followParent && data instanceof Posc p){
                entity.parent = p;
                entity.rotWithParent = rotWithParent;
            }
            entity.add();
        }
    }
}
