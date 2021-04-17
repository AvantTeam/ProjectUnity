package unity.entities;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.ReloadTurret.*;
import mindustry.world.blocks.logic.LogicBlock.*;
import mindustry.world.blocks.logic.MemoryBlock.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.power.ImpactReactor.*;
import mindustry.world.blocks.power.PowerGenerator.*;

import static mindustry.Vars.*;

public class Emp{
    static IntSet collided = new IntSet(409);
    static ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    static Seq<Building> last = new Seq<>(), next = new Seq<>();
    static boolean hit = false;
    /** Only use after hitTile is used. */
    public static boolean hitPowerGrid, hitDisconnect;

    /**
     * @param validRange if a power block is within this range, the emp will start.
     * @param amount The amount of power batteries lose.
     * @param logicIntensity how much it corrupts a logic block by altering a instructions value.
     * @param logicInstructions how many instructions it corrupts.
     * @param disconnectRange range where power nodes disconnect and logic blocks corrupt.
     * @param scanRange range to scan for power generators.
     * @param scans how much iterating through connections before terminating.
     * */
    public static void hitTile(float x, float y, Team team, float validRange, float duration, float amount, float logicIntensity, int logicInstructions, float disconnectRange, float scanRange, int scans){
        hit = false;
        hitPowerGrid = hitDisconnect = false;
        if(validRange > 0f){
            indexer.eachBlock(null, x, y, validRange, b -> b.team != team && !collided.contains(b.pos()) && b.block.hasPower, building -> {
                if(building.power != null){
                    if(graphs.add(building.power.graph)){
                        building.power.graph.useBatteries(amount);
                        handleBuilding(building, duration);
                        //next.add(building.pos());
                        last.add(building);
                        collided.add(building.pos());
                        for(int i = 0; i < scans; i++){
                            for(Building b : last){
                                if(b.power != null){
                                    IntSeq links = b.power.links;
                                    Point2[] nearby = Edges.getEdges(b.block.size);
                                    for(Point2 point : nearby){
                                        Building other = world.build(b.tile.x + point.x, b.tile.y + point.y);

                                        if(other != null && other.block != null && other.block.hasPower && other.within(x, y, scanRange) && collided.add(other.pos())){
                                            next.add(other);
                                            handleBuilding(other, duration);
                                        }
                                    }
                                    for(int j = 0; j < links.size; j++){
                                        int pos = links.get(j);

                                        Building other = world.build(pos);
                                        if(other != null && other.within(x, y, scanRange) && collided.add(other.pos())){
                                            next.add(other);
                                            handleBuilding(other, duration);
                                        }
                                    }
                                }
                            }
                            last.set(next);
                            next.clear();
                        }
                    }
                    hitPowerGrid = true;
                    hit = true;
                }
            });
        }
        last.clear();
        graphs.clear();

        if(disconnectRange > 0f && (hit || (logicIntensity > 0f && logicInstructions > 0))){
            indexer.eachBlock(null, x, y, disconnectRange, b -> b.team != team, building -> {
                if(((building.block.hasPower || building.block.outputsPower) && building.power != null && hit)){
                    for(int i = 0; i < building.power.links.size; i++){
                        int p = building.power.links.get(i);
                        Building s = world.build(p);
                        if(s != null && s.power != null){
                            s.power.links.removeValue(building.pos());
                            last.add(s);
                        }
                    }
                    building.power.links.clear();
                    PowerGraph origin = new PowerGraph();
                    origin.reflow(building);
                    graphs.add(origin);
                    for(Building build : last){
                        if(!graphs.contains(build.power.graph)){
                            PowerGraph n = new PowerGraph();
                            n.reflow(build);
                            graphs.add(n);
                        }
                    }
                    last.clear();
                    graphs.clear();

                    hitDisconnect = true;
                }
                if(building instanceof MemoryBuild mb && logicIntensity > 0f && logicInstructions > 0){
                    for(int i = 0; i < logicInstructions; i++){
                        int index = Mathf.random(0, mb.memory.length - 1);
                        mb.memory[index] += Mathf.range(logicIntensity);
                    }
                    hitDisconnect = true;
                }
                //kills your processor and your processor.
                if(building instanceof LogicBuild lb && logicIntensity > 0f && logicInstructions > 0){
                    hitDisconnect = true;
                    StringBuilder build = new StringBuilder();
                    String[] lines = lb.code.split("\n");

                    for(int i = 0; i < logicInstructions; i++){
                        StringBuilder builder = new StringBuilder();
                        int index = Mathf.random(0, lines.length - 1);
                        String[] line = lines[index].split("\\s+");
                        String type = line[0];
                        switch(type){
                            case "set" -> {
                                if(Strings.canParseFloat(line[2])){
                                    float par = Strings.parseFloat(line[2], 0f);
                                    par += Mathf.range(logicIntensity);
                                    line[2] = par + "";
                                }else if(logicIntensity > 256f && Mathf.chance((logicIntensity - 256f) / 64f)){
                                    line[2] = Mathf.random(Float.MAX_VALUE) + "";
                                }
                            }
                            case "op" -> {
                                String a = line[3], b = line[4];
                                if(Strings.canParseFloat(a)){
                                    float par = Strings.parseFloat(a);
                                    par += Mathf.range(logicIntensity);
                                    line[3] = par + "";
                                }else if(logicIntensity > 256f && Mathf.chance((logicIntensity - 256f) / 64f)){
                                    line[3] = Mathf.random(Float.MAX_VALUE) + "";
                                }
                                if(Strings.canParseFloat(b)){
                                    float par = Strings.parseFloat(b);
                                    par += Mathf.range(logicIntensity);
                                    line[4] = par + "";
                                }else if(logicIntensity > 256f && Mathf.chance((logicIntensity - 256f) / 64f)){
                                    line[4] = Mathf.random(Float.MAX_VALUE) + "";
                                }
                                if(logicIntensity > 128f && Mathf.chance((logicIntensity - 128f) / 64f)){
                                    LogicOp[] ops = LogicOp.values();
                                    line[1] = ops[Mathf.random(0, ops.length - 1)].name();
                                }
                            }
                            case "control" -> {
                                String conType = line[1];
                                if(conType.equals("enabled")){
                                    line[3] = "0";
                                }
                            }
                            case "draw" -> {
                                if(!line[1].equals("color")){
                                    for(int j = 2; j < 8; j++){
                                        String a = line[j];
                                        if(Strings.canParseFloat(a)){
                                            float par = Strings.parseFloat(a);
                                            float l = par;
                                            par += Mathf.range(logicIntensity);
                                            if(par < 0f) par += l;
                                            par = Math.max(0f, par);
                                            line[j] = par + "";
                                        }else if(logicIntensity > 256f && Mathf.chance((logicIntensity - 256f) / 64f)){
                                            line[j] = Mathf.random(100f) + "";
                                        }
                                    }
                                }else{
                                    for(int j = 2; j < 6; j++){
                                        String a = line[j];
                                        if(Strings.canParseFloat(a)){
                                            float par = Strings.parseFloat(a);
                                            par += Mathf.range(logicIntensity);
                                            par = Mathf.mod(par, 255f);
                                            line[j] = par + "";
                                        }else if(logicIntensity > 256f && Mathf.chance((logicIntensity - 256f) / 64f)){
                                            line[j] = Mathf.random(255f) + "";
                                        }
                                    }
                                }
                            }
                            case "jump" -> {
                                if(Strings.canParseInt(line[1]) && Mathf.round((logicIntensity - 30f) / 5f) >= 1){
                                    int par = Strings.parseInt(line[1]);
                                    par += Mathf.range(Mathf.round((logicIntensity - 30f) / 5f));
                                    par = Mathf.clamp(par, 0, lines.length - 1);
                                    line[1] = par + "";
                                }
                            }
                            default -> {
                                for(int j = 1; j < line.length; j++){
                                    if(Strings.canParseFloat(line[j])){
                                        float par = Strings.parseFloat(line[j]);
                                        par += Mathf.range(logicIntensity);
                                        line[j] = par + "";
                                    }
                                }
                            }
                        }
                        for(int j = 0; j < line.length; j++){
                            String s = line[j];
                            builder.append(s);
                            if(j < line.length - 1) builder.append(" ");
                        }
                        lines[index] = builder.toString();
                    }

                    for(int i = 0; i < lines.length; i++){
                        build.append(lines[i]);
                        if(i < lines.length - 1) build.append("\n");
                    }

                    lb.code = build.toString();
                    lb.updateCode(lb.code);
                }
            });
        }
        graphs.clear();
        last.clear();
        next.clear();
        collided.clear();
    }

    public static void handleBuilding(Building build, float duration){
        if(!build.block.hasPower) return;
        if(build instanceof GeneratorBuild gb){
            gb.productionEfficiency = 0f;
            if(build instanceof ImpactReactorBuild irb){
                ImpactReactor r = (ImpactReactor)build.block;
                irb.warmup = Mathf.clamp(irb.warmup - (duration * r.warmupSpeed));
            }
        }
        if(build.block.consumes.hasPower() && build instanceof ReloadTurretBuild rtb){
            rtb.reload = 0f;
        }
        build.enabled = false;
        build.enabledControlTime = Math.max(duration, build.enabledControlTime);
    }
}
