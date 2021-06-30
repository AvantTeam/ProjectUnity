package unity.assets.list;

import arc.*;
import arc.assets.*;
import arc.util.*;
import unity.assets.loaders.ModelLoader.*;
import unity.assets.type.g3d.*;

import static mindustry.Vars.*;

public class UnityModels{
    public static Model wavefront;

    public static void load(){
        wavefront = load("wavefront");
    }

    protected static Model load(String modelName){
         if(headless) return new Model();

        String path = "objects/" + modelName + ".g3db";
        var model = new Model();

        AssetDescriptor<?> desc = Core.assets.load(path, Model.class, new ModelParameter(model));
        desc.errored = Log::err;

        return model;
    }
}
