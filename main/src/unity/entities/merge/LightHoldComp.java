package unity.entities.merge;

import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.modules.*;

@MergeComp
class LightHoldComp extends Block{
    float lightRange;
    boolean holdsLight;
    boolean isSource;
    boolean reflect;

    public LightHoldComp(String name){
        super(name);
    }

    public class LightHoldBuildComp extends Building{
        Light source;
        LightModule light;

        float mirrorAngle = 90;

        public boolean acceptLight(Light source){
            if(holdsLight){
                this.source = source;
                light.graph.add(source);

                return true;
            }

            return false;
        }

        @Override
        public void created(){
            if(!initialized){
                light = new LightModule();

                if(isSource && holdsLight){
                    source = Light.create();
                    source.maxRange = lightRange;
                    source.set(x, y);
                    source.rotation = mirrorAngle;
                    light.graph.add(source);
                }
            }
        }

        @Override
        public void update(){
            if(source != null && !source.isAdded()){
                source = null;
            }else{
                source.strength = efficiency();
            }

            if(light != null){
                light.graph.update();
            }
        }

        public boolean reflect(){
            return reflect;
        }
    }
}
