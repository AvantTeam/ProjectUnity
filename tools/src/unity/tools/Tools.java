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
import java.util.concurrent.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/** Main entry point of the tools module. This must only affect the main project's asset directory. */
public class Tools{
    public static Unity unity;
    public static LoadedMod mod;
    public static ModMeta meta;

    public static final Fi
    assetsDir,
    spritesDir, spritesGenDir;

    public static GenAtlas atlas;

    static{
        assetsDir = new Fi(Paths.get("").toFile());
        spritesDir = assetsDir.child("sprites");
        spritesGenDir = spritesDir.child("gen");
    }

    public static void main(String[] args){
        ArcNativesLoader.load();
        Log.logger = new NoopLogHandler();

        headless = true;
        Core.app = new NoopApplication();
        Core.files = new NoopFiles();
        Core.assets = new AssetManager(tree = new FileTree());
        Core.settings = new Settings();
        Core.atlas = atlas = new GenAtlas();

        asyncCore = new AsyncCore();
        mods = new Mods();

        content = new ContentLoader();
        content.createBaseContent();

        unity = new Unity();

        meta = new ModMeta(){{ name = "unity"; }};
        mod = new LoadedMod(null, null, unity, Tools.class.getClassLoader(), meta);

        Reflect.<Seq<LoadedMod>>get(Mods.class, mods, "mods").add(mod);
        Reflect.<ObjectMap<Class<?>, ModMeta>>get(Mods.class, mods, "metas").put(Unity.class, meta);

        content.setCurrentMod(mod);
        unity.loadContent();
        content.setCurrentMod(null);

        Log.logger = new DefaultLogHandler();
        loadLogger();

        clear(spritesGenDir);
        addRegions();

        atlas.dispose();
    }

    private static void addRegions(){
        print("Adding regions...");
        Time.mark();

        var exec = Executors.newCachedThreadPool();

        spritesDir.walk(path -> {
            if(!path.extEquals("png")) return;
            exec.submit(() -> atlas.addRegion(path));
        });

        try{
            exec.shutdown();

            boolean executed = exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            if(!executed) throw new IllegalStateException("Very strange things happened.");
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }

        print("Total time to add regions: " + Time.elapsed() + "ms");
    }

    public static void clear(Fi file){
        file.mkdirs();
        for(var child : file.list()){
            child.deleteDirectory();
        }
    }
}
