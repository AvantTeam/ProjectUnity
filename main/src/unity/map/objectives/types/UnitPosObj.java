package unity.map.objectives.types;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import rhino.*;
import unity.gen.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;
import unity.map.objectives.ObjectiveModel.*;
import unity.util.*;

import static java.lang.String.*;
import static mindustry.Vars.*;

/**
 * An objective that will complete once {@link #count} amount of units are within a certain {@link #pos}ition in a
 * {@link #radius}. If {@link #continuous}, then units outside of the boundary will be released and the count will be
 * decremented.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class UnitPosObj extends Objective{
    public int count;
    public boolean continuous;

    public Cons2<UnitPosObj, Unit> spotted = (objective, unit) -> {};
    public Cons2<UnitPosObj, Unit> released = (objective, unit) -> {};
    public Boolf2<UnitPosObj, Unit> valid = (objective, unit) -> true;

    public Team team = state.rules.defaultTeam;
    public Vec2 pos = new Vec2();
    public float radius = 4f * tilesize;

    public final transient IntSet within = new IntSet();
    private static final IntSet tmp = new IntSet();

    public UnitPosObj(StoryNode node, String name, Cons<UnitPosObj> executor){
        super(node, name, executor);
    }

    public static void setup(){
        ObjectiveModel.setup(UnitPosObj.class, Color.sky, () -> Icon.units, (node, f) -> {
            String exec = f.get("executor", "function(objective){}");
            Function func = JSBridge.compileFunc(JSBridge.unityScope, f.name() + "-executor.js", exec, 1);

            Object[] args = {null};
            UnitPosObj obj = new UnitPosObj(node, f.name(), e -> {
                args[0] = e;
                func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, args);
            });
            obj.ext(f);

            return obj;
        });
    }

    @Override
    public void ext(FieldTranslator f){
        super.ext(f);

        count = (int)num(f.get("count"));
        continuous = f.get("continuous");
        team = f.get("team", state.rules.defaultTeam);
        pos = f.get("pos", pos);
        radius = num(f.get("radius", radius));

        Context c = JSBridge.context;
        ImporterTopLevel s = JSBridge.unityScope;
        Object[] args = {null, null};

        if(f.has("spotted")){
            Function spottedFunc = JSBridge.compileFunc(s, name + "-spotted.js", f.get("spotted"));
            spotted = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                spottedFunc.call(c, s, s, args);
            };
        }

        if(f.has("released")){
            Function releasedFunc = JSBridge.compileFunc(s, name + "-released.js", f.get("released"));
            released = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                releasedFunc.call(c, s, s, args);
            };
        }

        if(f.has("valid")){
            Func<Object[], Boolean> validFunc = JSBridge.requireType(JSBridge.compileFunc(s, name + "-valid.js", f.get("valid")), c, s, boolean.class);
            valid = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                return validFunc.get(args);
            };
        }
    }

    @Override
    public void update(){
        super.update();

        float sqrRad = radius * radius;

        tmp.clear();
        Groups.unit.each(this::valid, u -> {
            if(within.contains(u.id)){
                if(continuous) tmp.add(u.id);
                return;
            }

            u.hitbox(Tmp.r1);
            if(
                Intersector.overlaps(Tmp.cr1.set(pos, radius), Tmp.r1) ||
                Intersector.intersectSegmentCircle(
                    Tmp.v1.set(u.lastX, u.lastY),
                    Tmp.v2.set(u.x, u.y),
                    pos, sqrRad
                )
            ){
                spotted.get(this, u);
                within.add(u.id);
            }
        });

        if(continuous){
            for(var it = tmp.iterator(); it.hasNext;){
                int id = it.next();
                Unit u = Groups.unit.getByID(id);
                if(u == null){
                    within.remove(id);
                    continue;
                }

                u.hitbox(Tmp.r1);
                if(!valid(u) || !Intersector.overlaps(Tmp.cr1.set(pos, radius), Tmp.r1)){
                    within.remove(id);
                    released.get(this, u);
                }
            }
        }

        completed = within.size >= count;
    }

    @Override
    public void reset(){
        super.reset();
        within.clear();
    }

    @Override
    public void save(StringMap map){
        super.save(map);
        map.put("pos", valueOf(Float2.construct(pos.x, pos.y)));
        map.put("radius", valueOf(radius));
        map.put("team", valueOf(team.id));
        map.put("count", valueOf(count));
        map.put("continuous", valueOf(continuous));

        Seq<Unit> units = new Seq<>(within.size);
        for(var it = within.iterator(); it.hasNext;){
            var unit = Groups.unit.getByID(it.next());
            if(unit != null && valid(unit)) units.add(unit);
        }

        map.put("within", JsonIO.json.toJson(units.map(u -> valueOf(Float2.construct(u.x, u.y))), Seq.class, String.class));
    }

    @Override
    public void load(StringMap map){
        super.load(map);
        if(map.containsKey("pos")){
            long saved = map.getLong("pos");
            pos.set(Float2.x(saved), Float2.y(saved));
        }

        radius = map.getFloat("radius", radius);
        team = Team.get(map.getInt("team", team.id));
        count = map.getInt("count", count);
        continuous = !map.containsKey("continuous") ? continuous : map.getBool("continuous");

        within.clear();
        Seq<String> units = JsonIO.json.fromJson(Seq.class, String.class, map.get("within", "[]"));
        for(String str : units){
            long pos = Long.parseLong(str);
            var unit = Groups.unit.find(u ->
                u != null &&
                Mathf.equal(u.x, Float2.x(pos)) &&
                Mathf.equal(u.y, Float2.y(pos))
            );

            if(unit != null) within.add(unit.id);
        }
    }

    public boolean valid(Unit unit){
        return unit.team == team && unit.isValid() && valid.get(this, unit);
    }
}
