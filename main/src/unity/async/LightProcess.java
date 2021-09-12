package unity.async;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.async.*;
import mindustry.core.*;
import mindustry.gen.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class LightProcess implements AsyncProcess{
    protected TaskQueue queue = new TaskQueue();

    public final Seq<Light> all = new Seq<>(Light.class);
    public final QuadTree<Light> quad = new QuadTree<>(new Rect());

    protected volatile boolean
        processing = false,
        end = false,
        ready = false;

    @Override
    public void begin(){
        if(end){
            queue.run();
            end = false;
        }

        all.size = 0; // avoid unnecessary iteration
        Groups.draw.each(e -> e instanceof Light, e -> {
            var l = (Light)e;

            l.snap();
            all.add(l);
        });
    }

    @Override
    public void init(){
        queue.clear();

        quad.clear();
        quad.botLeft = null;
        quad.botRight = null;
        quad.topLeft = null;
        quad.topRight = null;
        quad.leaf = true;

        quad.bounds.set(-finalWorldBounds, -finalWorldBounds, world.unitWidth() + finalWorldBounds * 2, world.unitHeight() + finalWorldBounds * 2);

        ready = true;
    }

    @Override
    public void reset(){
        queue.clear();
        quad.clear();

        ready = false;
    }

    @Override
    public void process(){
        processing = true;

        int size = all.size;
        for(int i = 0; i < size; i++){
            all.items[i].cast();
        }

        end = true;
        processing = false;
    }

    @Override
    public boolean shouldProcess(){
        return !processing && !state.isPaused();
    }

    public void quad(Cons<QuadTree<Light>> cons){
        synchronized(quad){
            cons.get(quad);
        }
    }

    public void queuePoint(Light light, @Nullable LightHoldBuildc hold){
        if(hold == null){
            queue.post(() -> {
                light.clearChildren();

                var pointed = light.pointed;
                if(pointed != null){
                    pointed.remove(light);
                    light.pointed = null;
                }
            });
        }else{
            queue.post(() -> {
                var pointed = light.pointed;
                if(light.rotationChanged || pointed != hold || hold.needsReinteract()){
                    light.clearChildren();

                    if(pointed != null) pointed.remove(light);
                    light.pointed = hold;
                    hold.add(light, World.toTile(light.endX()), World.toTile(light.endY()));

                    hold.interact(light);
                    light.rotationChanged = false;
                }
            });
        }
    }

    public void queueAdd(Light light){
        if(ready){
            queue.post(light::add);
        }else{
            light.add();
        }
    }

    public void queueRemove(Light light){
        if(ready){
            queue.post(() -> {
                if(light.pointed != null) light.pointed.remove(light);
                light.remove();
            });
        }else{
            light.remove();
        }
    }
}
