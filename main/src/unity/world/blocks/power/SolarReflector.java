package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.world.blocks.power.SolarCollector.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SolarReflector extends Block{
    public TextureRegion mirrorRegion, baseRegion;

    public SolarReflector(String name){
        super(name);
        
        solid = update = configurable = true;
        config(Point2.class, (SolarReflectorBuild build, Point2 point) -> build.setLink(Point2.pack(point.x + build.tileX(), point.y + build.tileY())));
        config(Integer.class, (SolarReflectorBuild build, Integer point) -> build.setLink(point));
    }

    @Override
    public void load(){
        super.load();
        
        mirrorRegion = atlas.find(name + "-mirror");
        baseRegion = atlas.find(name + "-base");
    }

    public class SolarReflectorBuild extends Building{
        float mirrorRot;
        int link = -1;
        boolean hasChanged;

        public void setLink(int s){
            if(s == link) return;
            if(link != -1){
                Building build = world.build(link);
                if(build instanceof SolarCollectorBuild b) b.removeReflector(this);
            }
            
            if(s != -1) hasChanged = true;
            link = s;
        }

        @Override
        public void updateTile(){
            mirrorRot += 0.4f;
            Building build = world.build(link);
            
            if(linkValid()){
                setLink(build.pos());
                mirrorRot = Mathf.slerpDelta(mirrorRot, tile.angleTo(build.tile), 0.05f);
                
                if(hasChanged){
                    ((SolarCollectorBuild)build).appendSolarReflector(this);
                    hasChanged = false;
                }
            }
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(mirrorRegion, x - size / 2f, y - size / 2f, mirrorRot);
            Draw.rect(mirrorRegion, x, y, mirrorRot);
        }

        @Override
        public void drawConfigure(){
            float sin = Mathf.absin(6f, 1f);
            
            if(linkValid()){
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block.size / 2f + 1f) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }
            
            Drawf.dashCircle(x, y, 100f, Pal.accent);
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
            if(this == other){
                configure(-1);
                return false;
            }
            if(link == other.pos()){
                configure(-1);
                return false;
            }else if(other instanceof SolarCollectorBuild && other.dst(tile) <= 100f && other.team == team){
                configure(other.pos());
                return false;
            }
            
            return true;
        }

        @Override
        public Point2 config(){
            return Point2.unpack(link).sub(tileX(), tileY());
        }

        boolean linkValid(){
            if(link == -1) return false;
            Building build = world.build(link);
            
            if(build instanceof SolarCollectorBuild) return build.team == team && within(build, 100f);
            
            return false;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            
            write.i(link);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            setLink(read.i());
        }

        @Override
        public void onRemoved(){
            Building build = world.build(link);
            if(build instanceof SolarCollectorBuild b) b.removeReflector(this);
        }
    }
}
