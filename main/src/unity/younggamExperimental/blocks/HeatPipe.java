package unity.younggamExperimental.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.util.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class HeatPipe extends GraphBlock{
    final static Color baseColor = Color.valueOf("6e7080");
    final static int[] shift = new int[]{0, 3, 2, 1};
    TextureRegion[] heatRegions, regions;//bottom

    public HeatPipe(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        heatRegions = Funcs.getRegions(heatRegion, 8, 2);
        regions = Funcs.getRegions(atlas.find(name + "-tiles"), 8, 2);
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        float scl = tilesize * req.animScale;
        Draw.rect(region, req.drawx(), req.drawy(), scl, scl, req.rotation * 90f);
    }

    public class HeatPipeBuild extends GraphBuild{
        int spriteIndex;//basespriteindex

        @Override
        public void created(){
            rotation = 0;
            super.created();
        }

        @Override
        public void onNeighboursChanged(){
            spriteIndex = 0;
            //unity.Unity.print("each");
            heat().eachNeighbourValue(n -> {
                //unity.Unity.print(n.parent.build, n.portIndex);
                spriteIndex += 1 << shift[n];
            });
            //unity.Unity.print(spriteIndex);
        }

        @Override
        public void unitOn(Unit unit){
            if(timer(dumpTime, 20f)){
                float intensity = Mathf.clamp(Mathf.map(heat().getTemp(), 400f, 1000f, 0f, 1f));
                unit.apply(StatusEffects.burning, intensity * 20f + 5f);
                unit.damage(intensity * 10f);
            }
        }

        @Override
        public void draw(){
            float temp = heat().getTemp();
            Draw.rect(regions[spriteIndex], x, y);
            if(temp < 273f || temp > 498f){
                Draw.color(Funcs.tempColor(temp).add(baseColor));
                Draw.rect(heatRegions[spriteIndex], x, y);
                Draw.color();
            }
            drawTeamTop();
        }
    }
}
