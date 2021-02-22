package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import unity.world.blocks.*;

import static arc.Core.atlas;

public class HandCrank extends GraphBlock{
    public final TextureRegion[] shaftRegions = new TextureRegion[2];
    public TextureRegion handleRegion, baseRegion;

    public HandCrank(String name){
        super(name);
        
        rotate = configurable = true;
        config(Integer.class, (HandCrankBuild build, Integer value) -> {
            build.force = 40f;
            build.cooldown = 0f;
        });
    }

    @Override
    public void load(){
        super.load();
        
        handleRegion = atlas.find(name + "-handle");
        baseRegion = atlas.find(name + "-bottom");
        
        for(int i = 0; i < 2; i++) shaftRegions[i] = atlas.find(name + "-base" + (i + 1));
    }

    public class HandCrankBuild extends GraphBuild{
        float cooldown, force;

        @Override
        public void buildConfiguration(Table table){
            table.button(Tex.whiteui, Styles.clearTransi, 50f, () -> configure(0))
                .size(50f).disabled(b -> cooldown < 30f)
                .get().getStyle().imageUp = Icon.redo;
        }

        @Override
        public void updatePre(){
            var tGraph = torque();
            float ratio = (20f - tGraph.getNetwork().lastVelocity) / 20f;
            
            tGraph.force = ratio * force;
            cooldown += Time.delta;
            force *= 0.8f;
        }

        @Override
        public void draw(){
            int variant = rotation == 2 || rotation == 1 ? 1 : 0;
            
            Draw.rect(baseRegion, x, y);
            Draw.rect(shaftRegions[variant], x, y, rotdeg());
            Draw.rect(handleRegion, x, y, torque().getRotation());
            
            drawTeamTop();
        }
    }
}
