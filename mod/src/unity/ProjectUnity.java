package unity;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import unity.content.*;
import unity.gen.assets.*;
import unity.gen.entities.*;
import unity.mod.*;

import static mindustry.Vars.*;

public class ProjectUnity extends ProjectUnityCommon{
    public ProjectUnity(){
        try{
            dev = (DevBuild)Class.forName("unity.mod.DevBuildImpl", true, mods.mainLoader()).getConstructor().newInstance();
            Log.info("Successfully instantiated developer build.");
        }catch(ClassNotFoundException | NoClassDefFoundError e){
            Log.info("Defaulting to user build.");
        }catch(Throwable t){
            Log.err("Error while trying to instantiate developer build", t);
        }finally{
            if(dev == null) dev = new DevBuild(){};
        }

        Events.on(FileTreeInitEvent.class, e -> Core.app.post(PUSounds::load));
        Events.on(ContentInitEvent.class, e -> Core.app.post(Faction::load));

        Core.app.post(dev::setup);
    }

    @Override
    public void init(){
        dev.init();
    }

    @Override
    public void loadContent(){
        EntityRegistry.register();
        Faction.init();

        PUStatusEffects.load();

        MonolithUnitTypes.load();
    }
}
