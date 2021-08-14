package unity.cinematic;

import arc.struct.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.ctype.*;
import unity.type.sector.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public abstract class StoryNode<T> implements JsonSerializable{
    public Seq<StoryNode<?>> parents = new Seq<>();
    public Seq<String> parentStrings = new Seq<>();

    public ScriptedSector sector;

    public String name;
    public StringMap tags = new StringMap();

    public Seq<SectorObjectiveModel> objectiveModels = new Seq<>();
    public Seq<SectorObjective> objectives = new Seq<>();

    public abstract T bound();

    @Override
    public void write(Json json){
        json.writeValue("sector", sector.name);
        json.writeValue("parents", parentStrings, Seq.class, String.class);
        json.writeValue("name", name);
        json.writeValue("tags", tags);
        json.writeValue("objectiveModels", objectiveModels, Seq.class, SectorObjectiveModel.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        sector = content.getByName(ContentType.sector, jsonData.getString("sector"));
        name = jsonData.getString("name");

        parentStrings.clear();
        var readParents = json.readValue(Seq.class, String.class, jsonData.get("parents"));
        if(readParents != null) parentStrings.addAll(readParents);

        tags.clear();
        var readTags = json.readValue(StringMap.class, jsonData.get("tags"));
        if(readTags != null) tags.putAll(readTags);

        objectiveModels.clear();
        var readObjects = json.readValue(Seq.class, SectorObjectiveModel.class, jsonData.get("objectiveModels"));
        if(readObjects != null) objectiveModels.addAll(readObjects);
    }

    public void createObjectives(){
        objectives.clear();
        objectiveModels.each(m -> objectives.add(m.create(this)));
    }

    public void init(){
        parents.clear();
        for(var p : parentStrings){
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
        for(var o : objectives){
            if(o.shouldDraw()) o.draw();
        }
    }

    public void parent(StoryNode<?> other){
        if(!parents.contains(other) && !other.parents.contains(this)) parents.add(other);
    }
}
