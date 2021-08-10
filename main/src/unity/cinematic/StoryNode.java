package unity.cinematic;

import arc.struct.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import unity.type.sector.*;

@SuppressWarnings("unchecked")
public abstract class StoryNode<T> implements JsonSerializable{
    public String name;
    public StringMap tags = new StringMap();

    public Seq<SectorObjectiveModel> objectives = new Seq<>();

    public abstract T bound();

    @Override
    public void write(Json json){
        json.writeValue("name", name);
        json.writeValue("tags", tags);
        json.writeValue("objectives", objectives, Seq.class, SectorObjectiveModel.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        name = jsonData.getString("name");

        tags.clear();
        var readTags = json.readValue(StringMap.class, jsonData.get("tags"));
        if(readTags != null) tags.putAll(readTags);

        objectives.clear();
        var readObjects = json.readValue(Seq.class, SectorObjectiveModel.class, jsonData.get("objectives"));
        if(readObjects != null) objectives.addAll(readObjects);
    }
}
