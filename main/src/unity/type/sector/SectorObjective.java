package unity.type.sector;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import rhino.*;
import unity.type.sector.SectorObjectiveModel.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public abstract class SectorObjective{
    /** The name of this objective. It is your responsibility to differ the name from other objectives. */
    public final String name;

    public final Cons<SectorObjective> executor;
    public final ScriptedSector sector;

    public final int executions;
    protected int execution;

    public Seq<SectorObjective> dependencies = new Seq<>();

    private boolean finalized;

    public Cons<SectorObjective> init = objective -> {};
    public Cons<SectorObjective> update = objective -> {};
    public Cons<SectorObjective> draw = objective -> {};

    public <T extends SectorObjective> SectorObjective(ScriptedSector sector, String name, int executions, Cons<T> executor){
        this.name = name;
        this.sector = sector;
        this.executor = (Cons<SectorObjective>)executor;
        this.executions = executions;
    }

    protected static Function compile(Context context, Scriptable scope, String name, String sourceName, String source){
        return context.compileFunction(scope, source, name + "-" + sourceName, 1);
    }

    protected Function compile(Context context, Scriptable scope, String sourceName, String source){
        return compile(context, scope, name, sourceName, source);
    }

    public void ext(FieldTranslator f){
        ImporterTopLevel scope = Reflect.get(Scripts.class, mods.getScripts(), "scope"); //TODO don't use top-level scope for safety reasons
        var context = Context.enter();
        Object[] args = {null};

        if(f.has("init")){
            var initFunc = compile(context, scope, "init", f.val("init"));
            init = obj -> {
                args[0] = obj;
                initFunc.call(context, scope, scope, args);
            };
        }

        if(f.has("update")){
            var initFunc = compile(context, scope, "update", f.val("update"));
            update = obj -> {
                args[0] = obj;
                initFunc.call(context, scope, scope, args);
            };
        }

        if(f.has("draw")){
            var initFunc = compile(context, scope, "draw", f.val("draw"));
            draw = obj -> {
                args[0] = obj;
                initFunc.call(context, scope, scope, args);
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
        return !isExecuted() && !completed() && dependencyCompleted();
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

    public SectorObjective dependencies(SectorObjective... dependencies){
        this.dependencies.addAll(dependencies);
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
        return !isExecuted() && completed() && dependencyCompleted();
    }

    public boolean dependencyCompleted(){
        return dependencies.find(obj -> !obj.isExecuted()) == null;
    }
}
