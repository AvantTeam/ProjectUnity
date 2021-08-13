package unity.cinematic;

import arc.util.serialization.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class BuildStoryNode extends StoryNode<Building>{
    public Building bound;

    @Override
    public Building bound(){
        return bound;
    }

    @Override
    public void write(Json json){
        super.write(json);
        json.writeValue("bound", bound == null ? -1 : bound.pos());
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        super.read(json, jsonData);

        int pos = jsonData.getInt("bound");
        if(pos != -1) bound = world.build(pos);
    }
}
