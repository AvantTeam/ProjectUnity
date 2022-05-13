package unity.entities.merge;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.*;

/**
 * A component that defines a type of block that holds light lasers. These lasers acts like some type of consumer;
 * affects {@link Building#efficiency()} and {@link Building#canConsume()}.
 * @author GlennFolker
 */
@SuppressWarnings("unused")
@MergeComponent
abstract class LightHoldComp extends Block implements Stemc{
    Seq<LightAcceptorType> acceptors = new Seq<>();

    public LightHoldComp(String name){
        super(name);
        update = true;
        sync = true;
        destructible = true;
    }

    public float getRotation(Building build){
        return 0f;
    }

    public abstract class LightHoldBuildComp extends Building implements StemBuildc{
        transient LightAcceptor[] slots;

        /** If true, lights pointing to this block will re-trigger {@link #interact(Light)} */
        @ReadOnly transient boolean needsReinteract;

        @Override
        public void created(){
            int len = acceptors.size;

            slots = new LightAcceptor[len];
            for(int i = 0; i < len; i++){
                slots[i] = acceptors.get(i).create(self());
            }
        }

        public boolean acceptLight(Light light, int x, int y){
            return Structs.contains(slots, e -> e.accepts(light, x, y));
        }

        public void add(Light light, int x, int y){
            for(var slot : slots){
                if(slot.accepts(light, x, y)) slot.add(light);
            }
        }

        public void remove(Light light){
            for(var slot : slots){
                slot.remove(light);
            }
        }

        /**
         * Called synchronously when a light ray touches this building. Used typically for querying new children
         * for this light ray
         */
        public void interact(Light light){
            needsReinteract = false;
        }

        public float lightStatus(){
            if(slots.length <= 0) return 1f;

            float val = 0f;
            for(var slot : slots){
                val += Mathf.clamp(slot.status());
            }

            return Mathf.clamp(val / slots.length);
        }

        public boolean requiresLight(){
            return !Structs.contains(slots, e -> !e.requires());
        }

        @Override
        public void draw(){
            for(var slot : slots){
                slot.draw();
            }
        }

        @Override
        public void updateTile(){
            for(var slot : slots){
                slot.update();
            }
        }

        @Override
        @Replace
        public float efficiency(){
            return super.efficiency() * (requiresLight() ? Math.min(lightStatus(), 1f) : 1f);
        }

        @Override
        @Replace
        public boolean canConsume(){
            return super.canConsume() && (!requiresLight() || !Structs.contains(slots, e -> !e.fulfilled()));
        }
    }
}
