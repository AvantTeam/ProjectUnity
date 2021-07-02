package unity.assets.list;

import arc.*;
import arc.assets.*;
import arc.util.*;
import unity.assets.loaders.ModelLoader.*;
import unity.assets.type.g3d.*;

import static mindustry.Vars.*;

public class UnityModels{
    public static Model wavefront, cube;

    public static void load(){
        wavefront = load("wavefront");
        cube = load("cube");
    }

    public static void dispose(){
        wavefront.dispose();
        wavefront = null;
    }

    protected static Model load(String modelName){
        if(headless) return new Model();

        String name = "objects/" + modelName;
        String path = tree.get(name + ".g3db").exists() ? name + ".g3db" : name + ".g3dj";
        var model = new Model();

        AssetDescriptor<?> desc = Core.assets.load(path, Model.class, new ModelParameter(model));
        desc.errored = Log::err;

        return model;
    }
}
