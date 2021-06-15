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
    }

    public class LightRouterBuild extends LightHoldBuild{
        public ObjectMap<Light, Seq<Light>> routes = new ObjectMap<>();

        @Override
        public boolean acceptLight(Light light){
            return true;
        }

        @Override
        public void addSource(Light light){
            super.addSource(light);
            addRoute(light);
        }

        @Override
        public void removeSource(Light light){
            super.removeSource(light);
            removeRoute(light);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
        }

        protected void removeRoute(Light light){
            if(routes.containsKey(light)){
                var r = routes.get(light);
                r.each(Light::remove);
                r.clear();

                routes.remove(light);
            }
        }

        protected void addRoute(Light light){
            if(!routes.containsKey(light)){
                Seq<Light> r = new Seq<>(3);
                for(int i = 0; i < 3; i++){
                    Light l = Light.create();
                    l.relX = light.endX() - x;
                    l.relY = light.endY() - y;
                    l.set(this);
                    l.source = this;
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

            var it = routes.entries();
            while(it.hasNext()){
                var route = it.next();

                var origin = route.key;
                var r = route.value;

                if(origin == null || !origin.isAdded()){
                    r.each(Light::remove);
                    r.clear();

                    it.remove();
                    continue;
                }

                assert r.size == 3;
                for(int i = 0; i < 3; i++){
                    Light l = r.get(i);
                    l.set(this);
                    l.source = this;
                    l.rotation = origin.rotation + 90f * (i - 1);
                    l.strength = origin.endStrength() / 3f;
                }
            }
        }
    }
}
