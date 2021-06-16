package unity.world.blocks.light;

import arc.struct.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(LightHoldc.class)
public class LightRouter extends LightHoldBlock{
    public LightRouter(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
    }

    public class LightRouterBuild extends LightHoldBuild{
        public ObjectMap<Light, Seq<Light>> routes = new ObjectMap<>();

        @Override
        public boolean acceptLight(Light light){
            return true;
        }

        @Override
        public void added(Light light){
            addRoute(light);
        }

        @Override
        public void removed(Light light){
            removeRoute(light);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            for(Light source : routes.keys()){
                removeRoute(source);
            }
        }

        public void removeRoute(Light light){
            if(routes.containsKey(light)){
                var r = routes.remove(light);
                r.each(Light::remove);
                r.clear();
            }
        }

        protected void addRoute(Light light){
            if(!routes.containsKey(light)){
                Seq<Light> r = new Seq<>(3);
                for(int i = 0; i < 3; i++){
                    Light l = apply(light, Light.create());
                    l.rotation = light.rotation + 90f * (i - 1);
                    l.strength = light.endStrength() / 3f;
                    l.add();

                    r.add(l);
                }

                routes.put(light, r);
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
            for(var route : routes.entries()){
                var origin = route.key;
                var r = route.value;

                for(int i = 0; i < r.size; i++){
                    Light l = apply(origin, r.get(i));
                    l.rotation = origin.rotation + 90f * (i - 1);
                    l.strength = origin.endStrength() / 3f;
                }
            }
        }
    }
}
