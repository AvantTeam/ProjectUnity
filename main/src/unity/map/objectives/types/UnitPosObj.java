package unity.map.objectives.types;

import arc.func.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;
import unity.map.objectives.ObjectiveModel.*;
import unity.util.*;

import static mindustry.Vars.*;

/**
 * An objective that will complete once {@link #count} amount of units are within a certain {@link #pos}ition in a
 * {@link #radius}. If {@link #continuous}, then units outside of the boundary will be released and the count will be
 * decremented.
 * @author GlennFolker
 */
public class UnitPosObj extends Objective{
    public int count;
    public boolean continuous;

    public Cons2<UnitPosObj, Unit> spotted = (objective, unit) -> {};
    public Cons2<UnitPosObj, Unit> released = (objective, unit) -> {};
    public Boolf2<UnitPosObj, Unit> valid = (objective, unit) -> true;

    public Team team = state.rules.defaultTeam;
    public Vec2 pos = new Vec2();
    public float radius = 4f * tilesize;

    protected final transient IntSet within = new IntSet();
    private static final IntSet tmp = new IntSet();

    public UnitPosObj(StoryNode node, String name, Cons<UnitPosObj> executor){
        super(node, name, executor);
    }

    public static void setup(){
        ObjectiveModel.setup(UnitPosObj.class, Color.sky, () -> Icon.units, (node, f) -> {
            var exec = f.get("executor", "function(objective){}");
            var func = JSBridge.compileFunc(JSBridge.unityScope, f.name() + "-executor.js", exec, 1);

            Object[] args = {null};
            var obj = new UnitPosObj(node, f.name(), e -> {
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

        count = f.get("count");
        continuous = f.get("continuous");
        team = f.get("team", state.rules.defaultTeam);
        pos = f.get("pos", pos);
        radius = f.get("radius", radius);

        var c = JSBridge.context;
        var s = JSBridge.unityScope;
        Object[] args = {null, null};

        if(f.has("spotted")){
            var spottedFunc = JSBridge.compileFunc(s, name + "-spotted.js", f.get("spotted"));
            spotted = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                spottedFunc.call(c, s, s, args);
            };
        }

        if(f.has("released")){
            var releasedFunc = JSBridge.compileFunc(s, name + "-released.js", f.get("released"));
            released = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                releasedFunc.call(c, s, s, args);
            };
        }

        if(f.has("valid")){
            var validFunc = JSBridge.requireType(JSBridge.compileFunc(s, name + "-valid.js", f.get("valid")), c, s, boolean.class);
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
                var u = Groups.unit.getByID(id);

                u.hitbox(Tmp.r1);
                if(!valid(u) || !Intersector.overlaps(Tmp.cr1.set(pos, radius), Tmp.r1)){
                    within.remove(id);
                    released.get(this, u);
                }
            }
        }

        completed = within.size >= count;
    }

    public boolean valid(Unit unit){
        return unit.team == team && unit.isValid() && valid.get(this, unit);
    }
}
