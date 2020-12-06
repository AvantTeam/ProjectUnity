package unity.entities.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import unity.content.UnityFx;
import unity.util.Funcs;

//I doubt that is this way appropriate?
public class VapourizeEffectState extends EffectState{
    protected Entityc influence;

    public VapourizeEffectState(){
        lifetime = 40f;
    }

    public VapourizeEffectState(float x, float y, Unit parent, Entityc influence){
        this();
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.influence = influence;
    }

    //TODO is this omittable?
    @Override
    public void add(){
        if(this.added) return;
        Groups.all.add(this);
        Groups.draw.add(this);
        this.added = true;
    }

    @Override
    public void update(){
        if(!(parent instanceof Hitboxc hit) || !(influence instanceof Posc temp)) return;
        float hitSize = hit.hitSize();
        if(Mathf.chanceDelta(0.2f * (1f - fin()) * hitSize / 10f)){
            Tmp.v1.trns(Angles.angle(x, y, temp.x(), temp.y()) + 180f, 65f + Mathf.range(0.3f));
            Tmp.v1.add(parent);
            Tmp.v2.trns(Mathf.random(360f), Mathf.random(hitSize / 1.25f));
            UnityFx.vaporation.at(parent.x(), parent.y(), 0f, new Position[]{parent, Tmp.v1.cpy(), Tmp.v2.cpy()});
        }
        super.update();
    }

    @Override
    public float clipSize(){
        if(parent instanceof Hitboxc hit) return hit.hitSize() * 2f;
        else return super.clipSize();
    }

    @Override
    public void draw(){
        if(!(parent instanceof Unit unit)) return;
        float oz = Draw.z();
        float slope = (0.4f - Math.abs(fin() - 0.5f)) * 2f;
        Draw.z(Layer.flyingUnit + 0.01f);
        Tmp.c1.set(Color.black);
        Tmp.c1.a = slope * 0.25f;
        Draw.color(Tmp.c1);
        Funcs.simpleUnitDrawer(unit, false);
        Draw.z(oz);
    }

    //TODO is this omittable?
    @Override
    public void remove(){
        if(!added) return;
        Groups.all.remove(this);
        Groups.draw.remove(this);
        added = false;
    }
}
