package unity.assets.list;

import arc.*;
import arc.util.*;
import arc.util.Log.*;
import unity.assets.loaders.*;
import unity.assets.loaders.ModelLoader.*;
import unity.assets.type.g3d.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

public class UnityModels{
    public static Model
    //turrets
    wavefront, cube,
    //planet composites
    megalithRing;

    public static void load(){
        wavefront = load("wavefront");
        cube = load("cube");
        megalithRing = load("megalithring");
    }

    public static void dispose(){
        wavefront.dispose();
        wavefront = null;
        cube.dispose();
        cube = null;
        megalithRing.dispose();
        megalithRing = null;
    }

    protected static Model load(String modelName){
        if(headless) return new Model();

        var name = "models/" + modelName;
        var path = tree.get(name + ".g3db").exists() ? name + ".g3db" : name + ".g3dj";
        var model = new Model();

        try{
            var loader = (ModelLoader)Core.assets.getLoader(Model.class, path);
            loader.load(Core.assets, path, tree.get(path), new ModelParameter(model));
        }catch(Throwable t){
            print(LogLevel.err, "", Strings.getStackTrace(Strings.getFinalCause(t)));
        }

        return model;
    }
}
