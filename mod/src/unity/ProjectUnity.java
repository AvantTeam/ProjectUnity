package unity;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import unity.content.*;
import unity.gen.assets.*;
import unity.gen.entities.*;
import unity.mod.*;
import unity.util.*;

import java.io.*;

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

        Events.on(FileTreeInitEvent.class, e -> Core.app.post(() -> {
            PUSounds.load();

            try(Reader file = tree.get("meta/classes.out").reader();
                BufferedReader reader = new BufferedReader(file)
            ){
                Seq<String> current = packages;

                String line;
                while((line = reader.readLine()) != null){
                    switch(line){
                        case "Packages:" -> current = packages;
                        case "Classes:" -> current = classes;
                        default -> current.add(line);
                    }
                }

                classes.removeAll(str -> {
                    try{
                        Class.forName(str, true, mods.mainLoader());
                        return false;
                    }catch(ClassNotFoundException | NoClassDefFoundError ex){
                        Log.warn("Class not found: '@'.", str);
                        return true;
                    }
                });
            }catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }));

        Events.on(ContentInitEvent.class, e -> Core.app.post(Faction::load));
        Core.app.post(() -> {
            dev.setup();
            JSBridge.init();
        });
    }

    @Override
    public void init(){
        JSBridge.importDefaults(JSBridge.defaultScope);
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
