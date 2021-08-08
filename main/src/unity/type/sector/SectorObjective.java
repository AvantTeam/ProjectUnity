package unity.type.sector;

import arc.*;
import arc.func.*;
import arc.struct.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public abstract class SectorObjective{
    private static final ObjectSet<String> usedNames = new ObjectSet<>();
    public final String name;

    private final Cons<SectorObjective> executor;
    public final ScriptedSector sector;

    public final int executions;
    protected int execution;

    public Seq<SectorObjective> dependencies = new Seq<>();

    private boolean finalized;

    private Cons<SectorObjective> init = objective -> {};
    private Cons<SectorObjective> update = objective -> {};
    private Cons<SectorObjective> draw = objective -> {};

    public <T extends SectorObjective> SectorObjective(ScriptedSector sector, String name, int executions, Cons<T> executor){
        if(usedNames.add(name)){
            this.name = name;
        }else{
            throw new IllegalArgumentException("Sector objective with the name '" + name + "' already exists!");
        }

        this.sector = sector;
        this.executor = (Cons<SectorObjective>)executor;
        this.executions = executions;
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

    public int getExecution(){
        return execution;
    }
}
