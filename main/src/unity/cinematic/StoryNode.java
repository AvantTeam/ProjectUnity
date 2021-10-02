package unity.cinematic;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.ctype.*;
import unity.gen.*;
import unity.map.*;
import unity.type.sector.*;
import unity.ui.dialogs.canvas.CinematicCanvas.*;

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

    public Seq<SectorObjectiveModel> objectiveModels = new Seq<>();
    public Seq<SectorObjective> objectives = new Seq<>();

    @Override
    public void write(Json json){
        json.writeValue("sector", sector.name);
        json.writeValue("parents", parents.map(n -> n.name), Seq.class, String.class);
        json.writeValue("name", name);
        json.writeValue("position", Float2.construct(position.x, position.y));
        json.writeValue("objectiveModels", objectiveModels, Seq.class, SectorObjectiveModel.class);
    }

    @Override
    public void read(Json json, JsonValue data){
        sector = content.getByName(ContentType.sector, data.getString("sector"));
        name = data.getString("name");

        parentAliases.clear();
        var jParents = json.readValue(Seq.class, String.class, data.require("parents"));
        if(jParents != null) parentAliases.addAll(jParents);

        if(data.has("position")){
            long pos = data.getLong("position");
            position.set(Float2.x(pos), Float2.y(pos));
        }

        objectiveModels.clear();
        var jObjects = json.readValue(Seq.class, SectorObjectiveModel.class, data.require("objectiveModels"));
        if(jObjects != null) objectiveModels.addAll(jObjects);
    }

    public void createObjectives(){
        objectives.clear();
        objectiveModels.each(m -> objectives.add(m.create(this)));
    }

    public void init(){
        parents.clear();
        for(var p : parentAliases){
            var other = sector.storyNodes.find(e -> e.name.equals(p));
            if(other != null) parent(other);
        }

        objectives.each(SectorObjective::init);
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
}
