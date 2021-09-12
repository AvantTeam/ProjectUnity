package unity.world;

import arc.struct.*;
import mindustry.core.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;
import unity.world.meta.*;

import static mindustry.Vars.*;

/**
 * A representation of a light slot. Can accept light if said light is within the slot's boundaries
 * @author GlennFolker
 */
public class LightAcceptor{
    public final LightAcceptorType type;
    public final LightHoldBuildc hold;

    public final StemData data = new StemData();
    public Seq<Light> sources = new Seq<>(2);

    public LightAcceptor(LightAcceptorType type, LightHoldBuildc hold){
        this.type = type;
        this.hold = hold;
    }

    public float status(){
        return type.required <= 0f ? 1f : (sources.sumf(Light::endStrength) / type.required);
    }

    public boolean fulfilled(){
        return !requires() || sources.sumf(Light::endStrength) >= type.required;
    }

    public boolean requires(){
        return type.required > 0f;
    }

    public boolean accepts(Light light, int x, int y){
        int dx = World.toTile((x * tilesize) - (hold.x() - hold.block().size * tilesize / 2f + tilesize / 2f)),
            dy = -World.toTile((y * tilesize) - (hold.y() + hold.block().size * tilesize / 2f - tilesize / 2f));

        return
            dx >= type.x && dx < type.x + type.width &&
            dy >= type.y && dy < type.y + type.height;
    }

    public void add(Light light){
        sources.add(light);
    }

    public void remove(Light light){
        sources.remove(light);
    }

    public void draw(){
        type.draw.get(hold, this);
    }

    public void update(){
        type.update.get(hold, this);
    }
}
