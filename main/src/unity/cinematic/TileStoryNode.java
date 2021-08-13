package unity.cinematic;

import arc.util.serialization.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class TileStoryNode extends StoryNode<Tile>{
    public Tile bound;

    @Override
    public Tile bound(){
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
        if(pos != -1) bound = world.tile(pos);
    }
}
