package unity.type.sector;

import arc.*;
import arc.func.*;
import unity.cinematic.*;
import unity.type.sector.SectorObjectiveModel.*;
import unity.util.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public abstract class SectorObjective{
    /** The name of this objective. It is your responsibility to differ the name from other objectiveModels. */
    public final String name;

    public final Cons<SectorObjective> executor;
    public final StoryNode<?> node;

    public final int executions;
    protected int execution;

    private boolean finalized;

    public Cons<SectorObjective> init = objective -> {};
    public Cons<SectorObjective> update = objective -> {};
    public Cons<SectorObjective> draw = objective -> {};

    public <T extends SectorObjective> SectorObjective(StoryNode<?> node, String name, int executions, Cons<T> executor){
        this.name = name;
        this.node = node;
        this.executor = (Cons<SectorObjective>)executor;
        this.executions = executions;
    }

    public void ext(FieldTranslator f){
        Object[] args = {null};

        if(f.has("init")){
            var initFunc = JSBridge.compileFunc(JSBridge.unityScope, name + "-init.js", f.val("init"));
            init = obj -> {
                args[0] = obj;
                initFunc.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, args);
            };
        }

        if(f.has("update")){
            var initFunc = JSBridge.compileFunc(JSBridge.unityScope, name + "-update.js", f.val("update"));
            update = obj -> {
                args[0] = obj;
                initFunc.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, args);
            };
        }

        if(f.has("draw")){
            var initFunc = JSBridge.compileFunc(JSBridge.unityScope, name + "-draw.js", f.val("draw"));
            draw = obj -> {
                args[0] = obj;
                initFunc.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, args);
            };
        }
    }

    public void init(){
        init.get(this);
    }

    //using 'do' because method name clash
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
        return !isExecuted() && !completed();
    }

    public boolean shouldDraw(){
        return shouldUpdate();
    }

    public <T extends SectorObjective> SectorObjective init(Cons<T> init){
        this.init = (Cons<SectorObjective>)init;
        return this;
    }

    public <T extends SectorObjective> SectorObjective update(Cons<T> update){
        this.update = (Cons<SectorObjective>)update;
        return this;
    }

    public <T extends SectorObjective> SectorObjective draw(Cons<T> draw){
        this.draw = (Cons<SectorObjective>)draw;
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
        return !isExecuted() && completed();
    }
}
