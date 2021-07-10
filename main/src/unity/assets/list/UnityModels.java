package unity.assets.list;

import arc.*;
import unity.assets.loaders.*;
import unity.assets.loaders.ModelLoader.*;
import unity.assets.type.g3d.*;

import static mindustry.Vars.*;

public class UnityModels{
    public static Model
    //turrets
    wavefront, cube, intersection,
    //planet composites
    megalithRing;

    public static void load(){
        wavefront = load("wavefront");
        cube = load("cube");
        intersection = load("intersection");
        megalithRing = load("megalithring");
    }

    public static void dispose(){
        wavefront.dispose();
        wavefront = null;
        cube.dispose();
        cube = null;
        intersection.dispose();
        intersection = null;
        megalithRing.dispose();
        megalithRing = null;
    }

    protected static Model load(String modelName){
        if(headless) return new Model();

        String name = "objects/" + modelName;
        String path = tree.get(name + ".g3db").exists() ? name + ".g3db" : name + ".g3dj";
        var model = new Model();

        //AssetDescriptor<?> desc = Core.assets.load(path, Model.class, new ModelParameter(model));
        //desc.errored = Log::err;

        //i need these to be loaded before content load
        try{
            ModelLoader loader = (ModelLoader)Core.assets.getLoader(Model.class);
            loader.load(Core.assets, path, tree.get(path), new ModelParameter(model));
        }catch(Throwable t){
            t.printStackTrace();
        }

        return model;
    }
}
