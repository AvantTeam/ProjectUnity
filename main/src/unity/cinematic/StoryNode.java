package unity.cinematic;

import arc.struct.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;

public abstract class StoryNode<T> implements JsonSerializable{
    public String name;
    public StringMap tags = new StringMap();

    public abstract T bound();

    @Override
    public void write(Json json){
        json.writeValue("name", name);
        json.writeValue("tags", tags);
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        name = jsonData.getString("name");

        tags.clear();
        tags.putAll(json.readValue(StringMap.class, jsonData.get("tags")));
    }
}
