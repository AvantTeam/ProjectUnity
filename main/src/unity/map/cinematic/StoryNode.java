package unity.map.cinematic;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.ctype.*;
import mindustry.io.*;
import rhino.*;
import unity.gen.*;
import unity.map.*;
import unity.map.objectives.*;
import unity.ui.dialogs.canvas.CinematicCanvas.*;
import unity.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class StoryNode implements JsonSerializable{
    /** This node's position in the cinematic editor's dialog. */
    public final Vec2 position = new Vec2();
    /** The UI element of this node to be used in cinematic editor. */
    public NodeElem elem;

    public StoryNode parent;
    public final Seq<StoryNode> children = new Seq<>(2);

    public ScriptedSector sector;
    public String name;

    //TODO ui
    public String dataScript;
    public Object data;
    public Func3<StoryNode, Object, Json, String> dataSerializer = (node, data, json) -> json.toJson(data);
    public Func3<StoryNode, String, Json, Object> dataDeserializer = (node, data, json) -> json.fromJson(Object.class, data);

    public StringMap scripts = new StringMap();
    public Seq<ObjectiveModel> objectiveModels = new Seq<>();
    public Seq<Objective> objectives = new Seq<>();

    protected boolean initialized = false, completed = false;

    @Override
    public void write(Json json){
        json.writeValue("sector", sector.name);
        json.writeValue("name", name);
        json.writeValue("dataScript", dataScript);
        json.writeValue("position", Float2.construct(position.x, position.y));
        json.writeValue("scripts", scripts, StringMap.class, String.class);
        json.writeValue("objectiveModels", objectiveModels, Seq.class, ObjectiveModel.class);
        json.writeValue("children", children, Seq.class, StoryNode.class);
    }

    @Override
    public void read(Json json, JsonValue data){
        sector = content.getByName(ContentType.sector, data.getString("sector"));
        name = data.getString("name");

        long pos = data.getLong("position");
        position.set(Float2.x(pos), Float2.y(pos));

        dataScript = data.getString("dataScript", null);

        scripts.clear();
        scripts.putAll(json.readValue(StringMap.class, String.class, data.require("scripts"), String.class));

        children.set(json.readValue(Seq.class, StoryNode.class, data.require("children")));
        children.each(c -> c.parent = this);

        objectiveModels.set(json.readValue(Seq.class, ObjectiveModel.class, data.require("objectiveModels")));
    }

    public void createObjectives(){
        objectives.clear();
        objectiveModels.each(m -> objectives.add(m.create(this)));
    }

    public void init(){
        if(initialized) return;
        initialized = true;

        if(dataScript != null){
            Function func = JSBridge.compileFunc(JSBridge.unityScope, dataScript, name + "-metadata.js");
            data = func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, new Object[]{this});
        }

        objectives.each(Objective::init);
    }

    public void reset(){
        initialized = false;
        completed = false;
        objectives.each(Objective::reset);
        children.each(StoryNode::reset);
    }

    public boolean completed(){
        if(completed || (completed = objectives.isEmpty())) return true;

        completed = true;
        for(Objective o : objectives){
            if(!o.executed()) return completed = false;
        }

        return true;
    }

    public void update(){
        if(!initialized) init();

        if(completed()){
            children.each(StoryNode::update);
            return;
        }

        for(Objective o : objectives){
            if(o.executed()) continue;
            if(o.shouldUpdate()) o.update();
            if(o.qualified()) o.execute();
        }
    }

    public void draw(){
        if(completed()){
            children.each(StoryNode::draw);
            return;
        }

        for(Objective o : objectives) if(o.shouldDraw()) o.draw();
    }

    public void child(StoryNode other){
        if(children.contains(other)) return;
        if(other.parent != null) other.parent.children.remove(other);

        other.parent = this;
        children.add(other);
    }

    public void save(StringMap map){
        map.put("data", dataSerializer.get(this, data, JsonIO.json));
        map.put("completed", String.valueOf(completed));

        StringMap objMap = new StringMap();
        for(Objective obj : objectives){
            StringMap child = new StringMap();
            obj.save(child);

            objMap.put(obj.name, JsonIO.json.toJson(child, StringMap.class, String.class));
        }

        map.put("objectives", JsonIO.json.toJson(objMap, StringMap.class, String.class));

        StringMap childMap = new StringMap();
        for(StoryNode child : children){
            StringMap m = new StringMap();
            child.save(m);

            childMap.put(child.name, JsonIO.json.toJson(m, StringMap.class, String.class));
        }

        map.put("children", JsonIO.json.toJson(childMap, StringMap.class, String.class));
    }

    public void load(StringMap map){
        data = dataDeserializer.get(this, map.get("data", ""), JsonIO.json);
        completed = map.getBool("completed");

        StringMap objMap = JsonIO.json.fromJson(StringMap.class, String.class, map.get("objectives", "{}"));
        for(Entry<String, String> e : objMap.entries()){
            Objective obj = objectives.find(o -> o.name.equals(e.key));
            if(obj == null) throw new IllegalStateException("Objective '" + e.key + "' not found!");

            obj.load(JsonIO.json.fromJson(StringMap.class, String.class, e.value));
        }

        StringMap nodes = JsonIO.json.fromJson(StringMap.class, String.class, map.get("children", "[]"));
        for(Entry<String, String> e : nodes.entries()){
            StoryNode node = children.find(n -> n.name.equals(e.key));
            if(node == null) throw new IllegalStateException("Node '" + e.key + "' not found!");

            node.load(JsonIO.json.fromJson(StringMap.class, String.class, e.value));
        }
    }
}
