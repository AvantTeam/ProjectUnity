package unity.map.objectives;

import arc.func.*;
import arc.struct.*;
import unity.map.cinematic.*;
import unity.map.objectives.ObjectiveModel.*;
import unity.util.*;

import static java.lang.String.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public abstract class Objective{
    /** The name of this objective. It is your responsibility to differ the name from other objectiveModels. */
    public final @Ignore String name;

    public final Cons<Objective> executor;
    public final @Ignore StoryNode node;

    protected @Ignore boolean
        executed = false,
        completed = false,
        finishedDep = false;

    public Cons<Objective> init = objective -> {};
    public Cons<Objective> update = objective -> {};
    public Cons<Objective> draw = objective -> {};

    public final @Ignore Seq<Objective> dependencies = new Seq<>();
    public final Seq<String> depAliases = new Seq<>();

    public <T extends Objective> Objective(StoryNode node, String name, Cons<T> executor){
        this.name = name;
        this.node = node;
        this.executor = (Cons<Objective>)executor;
    }

    public void resolveDependencies(){
        dependencies.clear();
        for(var dep : depAliases){
            int separator = dep.indexOf("\n");
            if(separator == -1) continue;

            var otherNode = dep.substring(0, separator);
            var otherDep = dep.substring(separator + 1);

            var n = node.sector.cinematic.nodes.find(e -> e.name.equals(otherNode));
            if(n != null){
                var o = n.objectives.find(e -> e.name.equals(otherDep));
                if(o != null) depend(o);
            }
        }
    }

    public void depend(Objective other){
        if(!dependencies.contains(other) && !other.dependencies.contains(this)){
            dependencies.add(other);
        }
    }

    public void ext(FieldTranslator f){
        var c = JSBridge.context;
        var s = JSBridge.unityScope;
        Object[] args = {null};

        if(f.has("init")){
            var initFunc = JSBridge.compileFunc(s, name + "-init.js", f.get("init"));
            init = obj -> {
                args[0] = obj;
                initFunc.call(c, s, s, args);
            };
        }

        if(f.has("update")){
            var updateFunc = JSBridge.compileFunc(s, name + "-update.js", f.get("update"));
            update = obj -> {
                args[0] = obj;
                updateFunc.call(c, s, s, args);
            };
        }

        if(f.has("draw")){
            var drawFunc = JSBridge.compileFunc(s, name + "-draw.js", f.get("draw"));
            draw = obj -> {
                args[0] = obj;
                drawFunc.call(c, s, s, args);
            };
        }

        if(f.has("depAliases")){
            depAliases.clear();
            depAliases.addAll(f.<Iterable<String>>get("depAliases"));
        }
    }

    public void init(){
        init.get(this);
    }

    public void update(){
        update.get(this);
    }

    public void draw(){
        draw.get(this);
    }

    public boolean shouldUpdate(){
        return !executed && !completed && dependencyFinished();
    }

    public boolean shouldDraw(){
        return shouldUpdate();
    }

    public <T extends Objective> T init(Cons<T> init){
        this.init = (Cons<Objective>)init;
        return (T)this;
    }

    public <T extends Objective> T update(Cons<T> update){
        this.update = (Cons<Objective>)update;
        return (T)this;
    }

    public <T extends Objective> T draw(Cons<T> draw){
        this.draw = (Cons<Objective>)draw;
        return (T)this;
    }

    public void save(StringMap map){
        map.put("executed", valueOf(executed));
        map.put("completed", valueOf(completed));
    }

    public void load(StringMap map){
        executed = map.getBool("executed");
        completed = map.getBool("completed");
    }

    public void execute(){
        executor.get(this);
        stop();
    }

    public boolean completed(){
        return completed;
    }

    public boolean executed(){
        return executed;
    }

    public void stop(){
        completed = true;
        executed = true;
    }

    public boolean qualified(){
        return !executed && completed && dependencyFinished();
    }

    public boolean dependencyFinished(){
        if(finishedDep || (finishedDep = dependencies.isEmpty())) return true;

        finishedDep = true;
        for(var dep : dependencies){
            if(!dep.executed){
                return finishedDep = false;
            }
        }

        return true;
    }
}
