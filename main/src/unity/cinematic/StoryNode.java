package unity.cinematic;

import arc.struct.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import unity.type.sector.*;

@SuppressWarnings("unchecked")
public abstract class StoryNode<T> implements JsonSerializable{
    public String name;
    public StringMap tags = new StringMap();

    public Seq<SectorObjectiveModel> objectiveModels = new Seq<>();
    protected Seq<SectorObjective> objectives = new Seq<>();

    public abstract T bound();

    @Override
    public void write(Json json){
        json.writeValue("name", name);
        json.writeValue("tags", tags);
        json.writeValue("objectiveModels", objectiveModels, Seq.class, SectorObjectiveModel.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        name = jsonData.getString("name");

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
        objectives.each(SectorObjective::init);
    }

    public void update(){
        for(SectorObjective objective : objectives){
            if(objective.shouldUpdate()) objective.update();

            if(objective.qualified()){
                objective.execute();
            }

            if(objective.isExecuted() && !objective.isFinalized()) objective.doFinalize();
        }
    }

    public void draw(){
        for(SectorObjective objective : objectives){
            if(objective.shouldDraw()){
                objective.draw();
            }
        }
    }
}
