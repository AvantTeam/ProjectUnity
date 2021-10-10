package unity.map.cinematic;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.io.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.ctype.*;
import mindustry.io.*;
import unity.gen.*;
import unity.map.*;
import unity.map.objectives.*;
import unity.ui.dialogs.canvas.CinematicCanvas.*;

import java.io.*;
import java.nio.charset.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class StoryNode implements JsonSerializable{
    /** This node's position in the cinematic editor's dialog. */
    public final Vec2 position = new Vec2();
    /** The UI element of this node to be used in cinematic editor. */
    public NodeElem elem;

    private final Seq<String> parentAliases = new Seq<>();
    public Seq<StoryNode> parents = new Seq<>();

    public ScriptedSector sector;
    public String name;

    public StringMap scripts = new StringMap();
    public Seq<ObjectiveModel> objectiveModels = new Seq<>();
    public Seq<Objective> objectives = new Seq<>();

    private static final ReusableByteInStream ins = new ReusableByteInStream();
    private static final Reads read = new Reads(new DataInputStream(ins));

    private static final ReusableByteOutStream outs = new ReusableByteOutStream();
    private static final Writes write = new Writes(new DataOutputStream(outs));

    @Override
    public void write(Json json){
        json.writeValue("sector", sector.name);
        json.writeValue("name", name);
        json.writeValue("position", Float2.construct(position.x, position.y));
        json.writeValue("scripts", scripts, ObjectMap.class, String.class);
        json.writeValue("parents", parents.map(n -> n.name), Seq.class);
        json.writeValue("objectiveModels", objectiveModels, Seq.class, ObjectiveModel.class);
    }

    @Override
    public void read(Json json, JsonValue data){
        sector = content.getByName(ContentType.sector, data.getString("sector"));
        name = data.getString("name");

        long pos = data.getLong("position");
        position.set(Float2.x(pos), Float2.y(pos));

        scripts.clear();
        scripts.putAll(json.readValue(ObjectMap.class, String.class, data.require("scripts"), String.class));

        parentAliases.set(json.readValue(Seq.class, String.class, data.require("parents")));
        objectiveModels.set(json.readValue(Seq.class, ObjectiveModel.class, data.require("objectiveModels")));
    }

    public void createObjectives(){
        objectives.clear();
        objectiveModels.each(m -> objectives.add(m.create(this)));
    }

    public void init(){
        parents.clear();
        for(var p : parentAliases){
            var other = sector.nodes.find(e -> e.name.equals(p));
            if(other != null) parent(other);
        }

        objectives.each(Objective::init);
    }

    public boolean shouldUpdate(){
        if(parents.isEmpty()) return true;

        for(var p : parents) if(!p.completed()) return false;
        return true;
    }

    public boolean completed(){
        if(objectives.isEmpty()) return true;

        for(var o : objectives) if(!o.isExecuted()) return false;
        return true;
    }

    public void update(){
        for(var o : objectives){
            if(o.isFinalized()) continue;

            if(o.shouldUpdate()) o.update();
            if(o.qualified()) o.execute();
            if(o.isExecuted() && !o.isFinalized()) o.doFinalize();
        }
    }

    public void draw(){
        for(var o : objectives) if(o.shouldDraw()) o.draw();
    }

    public void parent(StoryNode other){
        if(!parents.contains(other) && !other.parents.contains(this)) parents.add(other);
    }

    public void save(Writes w){
        var map = new StringMap();
        for(var obj : objectives){
            outs.reset();

            write.b(obj.revision());
            obj.save(write);

            map.put(obj.name, new String(outs.getBytes(), 0, outs.size(), StandardCharsets.UTF_8));
        }

        w.str(JsonIO.json.toJson(map, StringMap.class, String.class));
    }

    public void load(Reads r, byte revision){
        var map = JsonIO.json.fromJson(StringMap.class, String.class, r.str());
        for(var e : map.entries()){
            var obj = objectives.find(o -> o.name.equals(e.key));
            if(obj == null) throw new IllegalStateException("'" + e.key + "' objective not found!");

            ins.setBytes(e.value.getBytes(StandardCharsets.UTF_8));

            byte rev = read.b();
            obj.load(read, rev);
        }
    }

    public byte revision(){
        return 0;
    }
}
