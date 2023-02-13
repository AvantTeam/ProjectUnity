package unity.world.blocks.essence;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.mod.*;
import unity.util.*;
import unity.world.*;

import static mindustry.Vars.*;

public class EndEssenceBlock extends Block{
    public float essenceCapacity = 40f;
    public float flowSpeed = 0.25f, flowSpeedFrac = 0.05f;

    public float range = 330f;
    public int maxConnections = 6;

    private final static Seq<Building> tmpBuildings = new Seq<>();
    //private final int flowStatSize = 10;
    private float maxRange = 0f;

    public EndEssenceBlock(String name){
        super(name);
        update = true;
        configurable = true;
        swapDiagonalPlacement = true;

        config(Integer.class, (entity, value) -> {
            EndEssenceInterface as = (EndEssenceInterface)entity;
            EndEssenceModule mod = as.mod();
            Building other = world.build(value);
            boolean contains = mod.outputs.contains(value);

            if(other == null && contains){
                mod.outputs.removeValue(value);
                return;
            }

            connect(entity, other);
        });
        config(Point2[].class, (tile, value) -> {
            EndEssenceInterface as = (EndEssenceInterface)tile;
            as.mod().outputs.clear();

            for(Point2 p : value){
                int newPos = Point2.pack(p.x + tile.tileX(), p.y + tile.tileY());
                configurations.get(Integer.class).get(tile, newPos);
            }
        });
    }
    
    @Override
    public void init(){
        super.init();
        maxRange = Math.max(maxRange, range * 2);
        clipSize = Math.max(clipSize, maxRange);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(UnityStats.essenceCapacity, essenceCapacity);
        stats.add(UnityStats.essenceConnection, maxConnections);
        stats.add(UnityStats.essenceRange, range / tilesize, StatUnit.blocks);
        stats.add(UnityStats.essenceFlow, flowSpeed * 60f, StatUnit.perSecond);
        stats.add(UnityStats.essenceFlowFract, flowSpeedFrac);
    }

    @Override
    public void setBars(){
        super.setBars();

        /*
        addBar("essence", (EndEssenceBuilding e) ->
                new Bar(() -> "Essence: " + Strings.fixed(e.flowStat, 2) + "s", () -> EndPal.endMid, () -> e.module.essence / essenceCapacity));
        */
        addBar("essence", (EndEssenceBuilding e) ->
                new Bar(() -> Core.bundle.format("bar.unity-essence", Strings.fixed(e.module.essence, 2), essenceCapacity), () -> EndPal.endMid, () -> e.module.essence / essenceCapacity));
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation, boolean diagonalOn){
        Placement.calculateNodes(points, this, rotation, (p, o) -> overlaps(p.x * tilesize + offset, p.y * tilesize + offset, o.x * tilesize + offset, o.y * tilesize + offset, size));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        float wx = x * tilesize + offset;
        float wy = y * tilesize + offset;

        Drawf.circles(wx, wy, range, Pal.placing);
        //Building base = world.build(x, y);

        getPotentialLinks(wx, wy, player.team(), b -> {
            EndEssenceInterface e = (EndEssenceInterface)b;
            if(e.isOutput() && isOutputBlock()) return;
            float angle = b.angleTo(wx, wy);
            float lsize = b.block.size * tilesize / 2f + 2f;
            float sin = Mathf.absin(Time.time, 6f, 1f);
            float angle2 = angle + ((e.isOutput() || (e.mod().essence > 0 && !isOutputBlock())) ? 0f : 180f);

            Drawf.square(b.x, b.y, lsize, Pal.place);

            Tmp.v1.trns(angle, lsize + 4f + sin).add(b);

            Draw.color(Pal.gray);
            Fill.poly(Tmp.v1.x, Tmp.v1.y, 3, 4f + 3f + sin, angle2);
            Draw.color(Pal.place);
            Fill.poly(Tmp.v1.x, Tmp.v1.y, 3, 4f + sin, angle2);
        });

        Draw.reset();
    }
    
    protected boolean isOutputBlock(){
        return false;
    }

    protected static void arrow(float x, float y, float size, float angle){
        Draw.color(Pal.gray);
        Fill.poly(x, y, 3, size + 3, angle);
        Draw.color(Pal.place);
        Fill.poly(x, y, 3, size, angle);
    }

    protected void connect(Building src, Building other){
        if(other instanceof EndEssenceInterface oas){
            EndEssenceInterface as = (EndEssenceInterface)src;
            EndEssenceModule mod = as.mod(), omod = oas.mod();

            if(oas.isOutput()){
                if(!as.isOutput()) connect(other, src);
                return;
            }

            boolean contains = mod.outputs.contains(other.pos());
            int connectionO = (other.block instanceof EndEssenceBlock eb) ? eb.maxConnections : 0;
            
            /*
            if(contains){
                mod.outputs.removeValue(other.pos());
            }else if(mod.outputs.size < maxConnections && linkValid(src, other)){
                mod.outputs.add(other.pos());
                oas.mod().outputs.removeValue(src.pos());
            }
            */
            if(contains){
                mod.outputs.removeValue(other.pos());
                if(omod.outputs.size < connectionO && !as.isOutput() && !omod.outputs.contains(src.pos()) && linkValid(src, other)){
                    omod.outputs.add(src.pos());
                }
            }else if(omod.outputs.contains(src.pos())){
                omod.outputs.removeValue(src.pos());
                mod.outputs.removeValue(other.pos());
            }else if(mod.outputs.size < maxConnections && linkValid(src, other)){
                mod.outputs.add(other.pos());
                oas.mod().outputs.removeValue(src.pos());
            }
        }
    }

    protected boolean linkValid(Building src, Building other){
        return linkValid(src.x, src.y, other);
    }

    protected boolean linkValid(float x, float y, Building other){
        return other instanceof EndEssenceInterface && overlaps(x, y, other);
    }

    protected boolean overlaps(float x, float y, Building other){
        return overlaps(x, y, other.x, other.y, other.block.size);
    }

    protected boolean overlaps(float x, float y, float x2, float y2, float size){
        Tmp.cr1.set(x, y, range);
        Tmp.r1.setSize(size * tilesize).setCenter(x2, y2);
        return Intersector.overlaps(Tmp.cr1, Tmp.r1);
    }

    protected void getPotentialLinks(float x, float y, Team team, Cons<Building> cons){
        QuadTree<Building> tree = team.data().buildingTree;
        if(tree == null) return;
        tmpBuildings.clear();
        Tmp.r1.setCentered(x, y, range * 2);
        tree.intersect(Tmp.r1, e -> {
            if(linkValid(x, y, e)){
                tmpBuildings.add(e);
            }
        });
        tmpBuildings.sort((a, b) -> Float.compare(a.dst2(x, y), b.dst2(x, y)));
        int size = Math.min(tmpBuildings.size, maxConnections);
        for(int i = 0; i < size; i++){
            Building e = tmpBuildings.get(i);
            cons.get(e);
        }
    }

    public class EndEssenceBuilding extends Building implements EndEssenceInterface{
        public EndEssenceModule module = new EndEssenceModule();
        public float[] flowTimes = new float[maxConnections * 2];

        @Override
        public void placed(){
            if(module.outputs.size <= 0){
                getPotentialLinks(x, y, team, b -> {
                    //EndEssenceInterface e = (EndEssenceInterface)a;
                    //e.mod().outputs.add(b.pos());
                    if(b == this) return;
                    EndEssenceInterface e = (EndEssenceInterface)b;
                    if(e.isOutput() && isOutput()) return;
                    if(e.isOutput() || (e.mod().essence > 0 && !isOutput())){
                        e.mod().outputs.add(pos());
                    }else{
                        module.outputs.add(b.pos());
                    }
                });
            }
            super.placed();
        }

        @Override
        public void dropped(){
            module.outputs.clear();
        }

        @Override
        public EndEssenceModule mod(){
            return module;
        }

        @Override
        public boolean isOutput(){
            return false;
        }

        @Override
        public float capacity(){
            return essenceCapacity;
        }

        @Override
        public void draw(){
            super.draw();
            float z = Draw.z();

            Rand r = MathUtils.seedr;
            IntSeq is = module.outputs;
            Draw.z(Layer.power);
            Draw.color(EndPal.endMid);
            Fill.circle(x, y, (module.essence / essenceCapacity) * 4f);
            for(int i = 0; i < is.size; i++){
                Building o = world.build(is.items[i]);
                if(o != null){
                    float tm = flowTimes[i * 2 + 1] + Time.time / 20f;
                    float dst = dst(o) / 8;
                    float angle = angleTo(o);
                    float w = (2f + Mathf.absin(9f, 0.25f)) * Mathf.clamp(module.essence / 15f);
                    Lines.stroke(Math.max(w, 1f));
                    Lines.line(x, y, o.x, o.y, false);

                    r.setSeed((id + o.id) * 642L);
                    for(int j = 0; j < 3; j++){
                        float dur = r.random(10f + dst, 20f + dst);
                        float time = ((tm + r.nextFloat() * dur) / dur) % 1;
                        Tmp.v1.set(this).lerp(o, time);
                        Fill.square(Tmp.v1.x, Tmp.v1.y, w, angle + 45);
                    }
                }
            }
            Draw.color();
            Draw.z(z);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            Drawf.circles(x, y, range, Pal.place);
            Draw.reset();
        }

        @Override
        public void drawConfigure(){
            Drawf.circles(x, y, range);
            Drawf.square(x, y, block.size * tilesize / 2f + 1f, 0f, Pal.place);
            
            module.outputs.each(i -> {
                Building link = world.build(i);
                float angle = link.angleTo(this);
                float lsize = link.block.size * tilesize / 2f + 1f;
                float sin = Mathf.absin(Time.time, 6f, 1f);

                Tmp.v1.trns(angle, lsize + 4f).add(link);

                Drawf.square(link.x, link.y, lsize, Pal.place);

                arrow(Tmp.v1.x, Tmp.v1.y, 4f + sin, angle + 180);
            });
            getPotentialLinks(x, y, team, b -> {
                EndEssenceInterface e = (EndEssenceInterface)b;
                if(b != this && e.mod().outputs.contains(pos())){
                    float angle = b.angleTo(this);
                    float lsize = b.block.size * tilesize / 2f + 1f;
                    float sin = Mathf.absin(Time.time, 6f, 1f);

                    Tmp.v1.trns(angle, lsize + 4f).add(b);

                    Drawf.square(b.x, b.y, lsize, Pal.place);
                    arrow(Tmp.v1.x, Tmp.v1.y, 4f + sin, angle);
                }
            });
        }

        @Override
        public void updateTile(){
            //flowSpeed = 0f;
            //flowSpeedWarmup = Mathf.lerpDelta(flowSpeedWarmup, 0f, 0.015f);
            for(int i = 0; i < flowTimes.length; i += 2){
                //
                flowTimes[i + 1] += flowTimes[i] * 0.75f;
                flowTimes[i] = Mathf.lerpDelta(flowTimes[i], 0f, 0.015f);
            }

            if(!module.outputs.isEmpty()){
                IntSeq out = module.outputs;
                float essence = module.essence;
                float take = Math.min(essence, Math.max(flowSpeed, essence * flowSpeedFrac) * Time.delta);
                int usage = 0;
                for(int i = 0; i < out.size; i++){
                    Building b = world.build(out.items[i]);
                    if(b instanceof EndEssenceInterface){
                        usage++;
                    }else{
                        out.removeIndex(i);
                        i--;
                    }
                }
                float take2 = 0f;
                for(int i = 0; i < out.size; i++){
                    EndEssenceInterface b = (EndEssenceInterface)world.build(out.items[i]);
                    EndEssenceModule mod = b.mod();
                    float le = mod.essence;
                    float h = b.capacity() / 8f;
                    float flow = 1f - Mathf.clamp((mod.essence - h) / (b.capacity() - h)) * 0.875f;
                    mod.lastEssence = le;
                    float ne = (mod.essence = Mathf.clamp(mod.essence + (take / usage) * flow, 0f, b.capacity()));
                    take2 += ne - le;
                    flowTimes[i * 2] += Math.abs(ne - le);
                }
                module.essence -= take2;
                //flowSpeedWarmup += Math.abs(take2);
            }
            //flowTime += flowSpeedWarmup * 2;
            module.essence = Mathf.clamp(module.essence, 0f, essenceCapacity);

            module.lastEssence = module.essence;
        }

        @Override
        public Point2[] config(){
            Point2[] out = new Point2[module.outputs.size];
            for(int i = 0; i < out.length; i++){
                out[i] = Point2.unpack(module.outputs.get(i)).sub(tile.x, tile.y);
            }
            return out;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }
            if(other == this){
                deselect();
                return false;
            }
            return true;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            module.read(read);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            module.write(write);
        }
    }

    public interface EndEssenceInterface{
        EndEssenceModule mod();

        boolean isOutput();

        float capacity();
    }
}
