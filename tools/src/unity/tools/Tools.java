package unity.tools;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.graphics.g2d.*;
import arc.mock.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import arc.util.async.*;
import mindustry.async.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import unity.*;
import unity.ai.kami.*;
import unity.gen.*;
import unity.tools.GenAtlas.*;

import java.nio.file.*;
import java.util.concurrent.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * Main entry point of the tools module. This must only affect the main project's asset directory.
 * @author GlennFolker
 */
public final class Tools{
    public static Unity unity;
    public static LoadedMod mod;
    public static ModMeta meta;

    public static final Fi
    assetsDir,
    spritesDir, spritesGenDir;

    public static GenAtlas atlas;

    private static final ObjectSet<GenRegion> replaced = new ObjectSet<>();
    private static final IntSet[] initialized = new IntSet[ContentType.all.length];
    private static final IntSet[] loaded = new IntSet[ContentType.all.length];

    static{
        assetsDir = new Fi(Paths.get("").toFile());
        spritesDir = assetsDir.child("sprites");
        spritesGenDir = spritesDir.child("gen");

        for(var type : ContentType.all){
            int i = type.ordinal();
            synchronized(initialized){ initialized[i] = new IntSet(); }
            synchronized(loaded){ loaded[i] = new IntSet(); }
        }
    }

    private Tools(){}

    public static void main(String[] args){
        Log.logger = new NoopLogHandler();

        headless = true;
        Core.app = new MockApplication(){
            @Override
            public void post(Runnable runnable){
                runnable.run();
            }
        };
        Core.files = new MockFiles();
        Core.assets = new AssetManager(tree = new FileTree());
        Core.settings = new Settings();
        Core.atlas = atlas = new GenAtlas();

        asyncCore = new AsyncCore();
        state = new GameState();
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

        atlas.clear = atlas.find("clear");
        Regions.load();
        KamiRegions.load();

        Processors.process();

        atlas.dispose();

        if(args.length >= 1){
            synchronized(replaced){
                var list = Fi.get(args[0]);
                list.writeString(replaced.asArray().toString("\n", reg ->
                    "sprites/" + reg.relativePath + "/" +
                    reg.name.replaceFirst("unity-", "") + ".png"
                ));
            }
        }
    }

    private static void addRegions(){
        print("Adding regions...");
        Time.mark();

        var exec = Executors.newCachedThreadPool();

        spritesDir.walk(path -> {
            if(!path.extEquals("png")) return;
            exec.submit(() -> atlas.addRegion(path));
        });

        Threads.await(exec);
        print("Total time to add regions: " + Time.elapsed() + "ms");
    }

    @SuppressWarnings("all")
    public static boolean init(Content content){
        synchronized(initialized){
            boolean should = initialized[content.getContentType().ordinal()].add(content.id);
            if(should) content.init();

            return should;
        }
    }

    @SuppressWarnings("all")
    public static boolean load(Content content){
        synchronized(loaded){
            boolean should = loaded[content.getContentType().ordinal()].add(content.id);
            if(should) content.load();

            return should;
        }
    }

    public static void replace(GenRegion region){
        synchronized(replaced){
            replaced.add(region);
        }
    }

    public static GenRegion conv(TextureRegion region){
        return (GenRegion)region;
    }

    public static void clear(Fi file){
        file.mkdirs();
        for(var child : file.list()){
            child.deleteDirectory();
        }
    }
}
