package unity.tools;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.async.*;
import mindustry.core.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import unity.*;

import java.nio.file.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/** Main entry point of the tools module. This must only affect the main project's asset directory. */
public class Tools{
    public static Unity unity;

    public static final Fi
    assetsDir,
    spritesDir, spritesGenDir;

    static{
        assetsDir = new Fi(Paths.get("").toFile());
        spritesDir = assetsDir.child("sprites");
        spritesGenDir = spritesDir.child("gen");
    }

    public static void main(String[] args){
        ArcNativesLoader.load();
        Core.app = new Application(){
            final Seq<ApplicationListener> listeners = new Seq<>();

            @Override
            public Seq<ApplicationListener> getListeners(){
                return listeners;
            }

            @Override
            public ApplicationType getType(){
                return ApplicationType.headless;
            }

            @Override
            public String getClipboardText(){
                return null;
            }

            @Override
            public void setClipboardText(String text){}

            @Override
            public void post(Runnable runnable){
                runnable.run();
            }

            @Override
            public void exit(){}
        };

        headless = true;
        Core.app = new NoopApplication();
        Core.files = new NoopFiles();
        Core.assets = new AssetManager(tree = new FileTree());

        asyncCore = new AsyncCore();
        mods = new Mods();

        Log.logger = new NoopLogHandler();
        content = new ContentLoader();
        content.createBaseContent();

        unity = new Unity();

        var meta = new ModMeta(){{ name = "unity"; }};
        var mod = new LoadedMod(null, null, unity, Tools.class.getClassLoader(), meta);

        Reflect.<Seq<LoadedMod>>get(Mods.class, mods, "mods").add(mod);
        Reflect.<ObjectMap<Class<?>, ModMeta>>get(Mods.class, mods, "metas").put(Unity.class, meta);

        content.setCurrentMod(mod);
        unity.loadContent();
        content.setCurrentMod(null);
        Log.logger = new DefaultLogHandler();
    }
}
