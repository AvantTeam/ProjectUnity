package unity.world.blocks.logic;

import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.io.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import mindustry.gen.*;
import mindustry.world.*;
import unity.world.blocks.LightData;
import unity.world.blocks.LightRepeaterBuildBase;
import unity.world.blocks.logic.LightInfluencer.LightInfluencerBuild;
import mindustry.graphics.Layer;
import mindustry.ui.Styles;
import mindustry.entities.units.BuildPlan;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LightFilter extends Block{
    public static final Color[] colors = {Color.white, Color.red, Color.green, Color.blue}, ncolors = {Color.black, Color.cyan, Color.magenta, Color.yellow};
    protected TextureRegion baseRegion, lightRegion;
    public final boolean invert;

    public LightFilter(String name, boolean invert){
        super(name);
        this.invert = invert;
        update = true;
        solid = true;
        configurable = true;
        saveConfig = true;
        config(Integer.class, (LightFilterBuild build, Integer value) -> {
            build.setFilterColor(value);
            if(!headless) renderer.minimap.update(build.tile);
        });
        configClear((LightFilterBuild build) -> {
            build.setFilterColor(0);
        });
    }

    public LightFilter(String name){
        this(name, false);
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        final float scl = tilesize * req.animScale;
        Draw.rect(region, req.drawx(), req.drawy(), scl, scl);
        if(req.config != null) drawRequestConfig(req, list);
    }

    @Override
    public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list){
        if(req.config == null) return;
        final float scl = tilesize * req.animScale;
        Draw.color(colors[(int) req.config], 0.7f);
        Draw.rect(lightRegion, req.drawx(), req.drawy(), scl, scl);
        Draw.color();
    }

    @Override
    public int minimapColor(Tile tile){
        return colors[tile.<LightFilterBuild>bc().color].rgba();
    }

    @Override
    public void load(){
        super.load();
        baseRegion = atlas.find(name + "-base");
        lightRegion = atlas.find("unity-light-center");
    }

    public class LightFilterBuild extends Building implements LightRepeaterBuildBase{
        protected int color;
        protected LightInfluencerBuild cont;

        protected void setFilterColor(int c){
            color = c;
        }

        protected Color getTrueColor(boolean inverted){
            Color ret = inverted ? ncolors[color] : colors[color];
            if(cont == null) return ret;
            else{
                if(!cont.isValid()){
                    cont = null;
                    return ret;
                }else return cont.lastSumColor();
            }
        }

        @Override
        public LightData calcLight(LightData ld, int i){
            Color tempColor = ld.color.cpy().mul(getTrueColor(invert));
            int val = Mathf.floorPositive(tempColor.value() * ld.strength);
            if(val <= 0) return null;
            return new LightData(ld.angle, val, ld.length - i, tempColor);
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color(getTrueColor(false), 0.7f);
            Draw.z(Layer.effect + 2f);
            Draw.rect(lightRegion, x, y);
            Draw.color();
            Draw.reset();
        }

        @Override
        public Integer config(){
            return color;
        }

        protected void addColorButton(Table table, int i){
            ImageButton button = table.button(Tex.whiteui, Styles.clearToggleTransi, 24, () -> {
                color = i;
                configure(i);
                control.input.frag.config.hideConfig();
            }).size(40).get();
            button.update(() -> {
                button.setChecked(i == color);
            });
            button.getStyle().imageUpColor = colors[i];
        }

        @Override
        public void buildConfiguration(Table table){
            for(int i = 0; i < 4; i++) addColorButton(table, i);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(color);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            color = read.b();
        }
    }
}
