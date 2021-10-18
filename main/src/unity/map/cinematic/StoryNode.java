package unity.map.cinematic;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.ctype.*;
import mindustry.io.*;
import unity.gen.*;
import unity.map.*;
import unity.map.objectives.*;
import unity.ui.dialogs.canvas.CinematicCanvas.*;

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

    public StringMap scripts = new StringMap();
    public Seq<ObjectiveModel> objectiveModels = new Seq<>();
    public Seq<Objective> objectives = new Seq<>();

    protected float checkComplete = -1f;
    protected boolean initialized = false, completed = false;

    @Override
    public void write(Json json){
        json.writeValue("sector", sector.name);
        json.writeValue("name", name);
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

        scripts.clear();
        scripts.putAll(json.readValue(StringMap.class, String.class, data.require("scripts"), String.class));

        if(data.has("children")){
            children.set(json.readValue(Seq.class, StoryNode.class, data.require("children")));
            children.each(c -> c.parent = this);
        }

        objectiveModels.set(json.readValue(Seq.class, ObjectiveModel.class, data.require("objectiveModels")));
    }

    public void createObjectives(){
        objectives.clear();
        objectiveModels.each(m -> objectives.add(m.create(this)));
    }

    public void init(){
        if(initialized) return;
        initialized = true;

        objectives.each(Objective::init);
    }

    public void reset(){
        initialized = false;
        children.each(StoryNode::reset);
    }

    public boolean completed(){
        // Assume checked on same frame, if that's the case then don't bother recalculating.
        if(Mathf.equal(checkComplete, Time.time)) return completed;
        checkComplete = Time.time;

        if(completed || (completed = objectives.isEmpty())) return true;

        completed = true;
        for(var o : objectives){
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

        for(var o : objectives){
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

        for(var o : objectives) if(o.shouldDraw()) o.draw();
    }

    public void child(StoryNode other){
        if(children.contains(other)) return;
        if(other.parent != null) other.parent.children.remove(other);

        other.parent = this;
        children.add(other);
    }

    public void save(StringMap map){
        var objMap = new StringMap();
        for(var obj : objectives){
            var child = new StringMap();
            obj.save(child);

            objMap.put(obj.name, JsonIO.json.toJson(child, StringMap.class, String.class));
        }

        map.put("objectives", JsonIO.json.toJson(objMap, StringMap.class, String.class));

        var childMap = new StringMap();
        for(var child : children){
            var m = new StringMap();
            child.save(m);

            childMap.put(child.name, JsonIO.json.toJson(m, StringMap.class, String.class));
        }

        map.put("children", JsonIO.json.toJson(childMap, StringMap.class, String.class));
    }

    public void load(StringMap map){
        var objMap = JsonIO.json.fromJson(StringMap.class, String.class, map.get("objectives", "{}"));
        for(var e : objMap.entries()){
            var obj = objectives.find(o -> o.name.equals(e.key));
            if(obj == null) throw new IllegalStateException("Objective '" + e.key + "' not found!");

            obj.load(JsonIO.json.fromJson(StringMap.class, String.class, e.value));
        }

        var nodes = JsonIO.json.fromJson(StringMap.class, String.class, map.get("children", "[]"));
        for(var e : nodes.entries()){
            var node = children.find(n -> n.name.equals(e.key));
            if(node == null) throw new IllegalStateException("Node '" + e.key + "' not found!");

            node.load(JsonIO.json.fromJson(StringMap.class, String.class, e.value));
        }
    }
}
