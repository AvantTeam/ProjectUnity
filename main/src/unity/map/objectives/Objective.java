package unity.map.objectives;

import arc.*;
import arc.func.*;
import arc.struct.*;
import unity.map.cinematic.*;
import unity.map.objectives.ObjectiveModel.*;
import unity.util.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public abstract class Objective{
    /** The name of this objective. It is your responsibility to differ the name from other objectiveModels. */
    public final String name;

    public final Cons<Objective> executor;
    public final StoryNode node;

    public final int executions;
    protected int execution;

    private boolean finalized;

    public Cons<Objective> init = objective -> {};
    public Cons<Objective> update = objective -> {};
    public Cons<Objective> draw = objective -> {};

    public Seq<Objective> dependencies = new Seq<>();
    private final Seq<String> depAliases = new Seq<>();

    public <T extends Objective> Objective(StoryNode node, String name, int executions, Cons<T> executor){
        this.name = name;
        this.node = node;
        this.executor = (Cons<Objective>)executor;
        this.executions = executions;
    }

    public void resolveDependencies(){
        dependencies.clear();
        for(var dep : depAliases){
            int separator = dep.indexOf("\n");
            if(separator == -1) continue;

            var otherNode = dep.substring(0, separator);
            var otherDep = dep.substring(separator + 1);

            var n = node.sector.storyNodes.find(e -> e.name.equals(otherNode));
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

        if(f.has("dependencies")){
            depAliases.clear();
            depAliases.addAll(f.<Iterable<String>>get("dependencies"));
        }
    }

    public void init(){
        init.get(this);
    }

    // Using 'do' because method name clash.
    public void doFinalize(){
        finalized = true;
    }

    public boolean isFinalized(){
        return finalized;
    }

    public void update(){
        update.get(this);
    }

    public void draw(){
        draw.get(this);
    }

    public boolean shouldUpdate(){
        return !isExecuted() && !completed() && dependencyFinished();
    }

    public boolean shouldDraw(){
        return shouldUpdate();
    }

    public <T extends Objective> Objective init(Cons<T> init){
        this.init = (Cons<Objective>)init;
        return this;
    }

    public <T extends Objective> Objective update(Cons<T> update){
        this.update = (Cons<Objective>)update;
        return this;
    }

    public <T extends Objective> Objective draw(Cons<T> draw){
        this.draw = (Cons<Objective>)draw;
        return this;
    }

    protected String saveEntry(){
        return "unity.sector-objective-" + name + "-" + control.saves.getCurrent().getName();
    }

    protected void set(String key, Object value){
        Core.settings.put(saveEntry() + "." + key, value);
    }

    protected <T> T get(String key){
        return get(key, null);
    }

    protected <T> T get(String key, T def){
        return (T)Core.settings.get(saveEntry() + "." + key, def);
    }

    public void save(){
        set("execution", execution);
        set("finalized", finalized);
    }

    public void load(){
        execution = get("execution", 0);
        finalized = get("finalized", false);
    }

    public void execute(){
        execution++;
        executor.get(this);
    }

    public abstract boolean completed();

    public boolean isExecuted(){
        return execution >= executions;
    }

    public void stop(){
        execution = executions;
    }

    public boolean qualified(){
        return !isExecuted() && completed() && dependencyFinished();
    }

    public boolean dependencyFinished(){
        if(dependencies.isEmpty()) return true;

        for(var dep : dependencies) if(!dep.isExecuted()) return false;
        return true;
    }
}
