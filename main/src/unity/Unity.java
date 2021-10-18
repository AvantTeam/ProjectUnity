package unity;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.freetype.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import arc.util.serialization.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.input.*;
import mindustry.mod.*;
import mindustry.world.blocks.environment.*;
import unity.ai.kami.*;
import unity.annotations.Annotations.*;
import unity.assets.list.*;
import unity.assets.loaders.*;
import unity.assets.type.g3d.*;
import unity.async.*;
import unity.content.*;
import unity.editor.*;
import unity.gen.*;
import unity.map.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;
import unity.mod.*;
import unity.sync.*;
import unity.ui.*;
import unity.ui.dialogs.*;
import unity.util.*;
import younggamExperimental.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class Unity extends Mod{
    /** Whether the mod is in an asset-processing context. */
    public static boolean tools = false;

    /** Abstract music handler; will be overridden in the separate music mod. */
    public static MusicHandler music;
    /** Answers listeners for tapping. {@link Binding#boost} for desktop, screen taps for mobile. */ //TODO static-ify
    public static TapHandler tap;
    /** Shared anti-cheat utilities, heavily based around content scoring system. */ //TODO why isn't this static h
    public static AntiCheat antiCheat;
    /** Abstract developer build specification; dev builds allow users to have various developer accessibility. */
    public static DevBuild dev;

    /** Editor listener that revolves around cinematic and sector objectives. */
    public static CinematicEditor cinematicEditor;

    /** Credits dialog. Hey we made this mod we deserve it. */
    public static CreditsDialog creditsDialog;
    /** JS scripts dialog for editing JS scripts in-game. */
    public static ScriptsEditorDialog jsEditDialog;
    /** JS scripts dictionary dialog, for creating a collection of named JS scripts. */
    public static ScriptsDictionaryDialog jsDictDialog;
    /** Cinematic dialog, for editing {@link StoryNode}s bound to a {@link ScriptedSector}. */
    public static CinematicDialog cinematicDialog;
    /** Used to edit object "tags" that is bound to certain objects. */
    public static TagsDialog tagsDialog;
    /** Heavily relies on {@link #cinematicDialog}, this dialog edits {@link ObjectiveModel} bound to a {@link StoryNode}. */
    public static ObjectivesDialog objectivesDialog;

    /** Asynchronous process revolving around {@link Light} processing. */
    public static LightProcess lights;
    /** Asynchronous process revolving around content scoring system. */
    public static ContentScoreProcess scoring;

    /** All Unity's defined non-anonymous classes; the elements of this array will be generated. */
    @ListClasses
    public static final Seq<String> classes = Seq.with();
    /** All Unity's defined packages; the elements of this array will be generated. */
    @ListPackages
    public static final Seq<String> packages = Seq.with("java.lang", "java.util", "java.io", "rhino");

    public Unity(){
        this(false);
    }

    public Unity(boolean tools){
        Unity.tools = tools;

        // Setup several asset loader bindings to clients.
        if(!headless){
            Core.assets.setLoader(Model.class, ".g3dj", new ModelLoader(tree, new JsonReader()));
            Core.assets.setLoader(Model.class, ".g3db", new ModelLoader(tree, new UBJsonReader()));

            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));

            // Differ the extension to use different asset loader, as mods have to use Vars.tree.
            var fontSuff = ".gen_pu";
            Core.assets.setLoader(FreeTypeFontGenerator.class, fontSuff, new FreeTypeFontGeneratorLoader(tree){
                @Override
                public FreeTypeFontGenerator load(AssetManager assetManager, String fileName, Fi file, FreeTypeFontGeneratorParameters parameter){
                    return new FreeTypeFontGenerator(tree.get(file.pathWithoutExtension()));
                }
            });

            Core.assets.setLoader(Font.class, "-pu", new FreetypeFontLoader(tree){
                @Override
                public Font loadSync(AssetManager manager, String fileName, Fi file, FreeTypeFontLoaderParameter parameter){
                    if(parameter == null) throw new IllegalArgumentException("parameter is null");

                    var generator = manager.get(parameter.fontFileName + fontSuff, FreeTypeFontGenerator.class);
                    return generator.generateFont(parameter.fontParameters);
                }

                @Override
                @SuppressWarnings("rawtypes")
                public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, FreeTypeFontLoaderParameter parameter){
                    return Seq.with(new AssetDescriptor<>(parameter.fontFileName + fontSuff, FreeTypeFontGenerator.class));
                }
            });
        }

        // Load several assets revolving around textures and fonts.
        // Uses ContentInitEvent as it is practically equivalent to Content#load().
        Events.on(ContentInitEvent.class, e -> {
            if(!headless){
                Regions.load();
                KamiRegions.load();
            }

            UnityFonts.load();
            UnityStyles.load();
        });

        // Load all assets once they're added into Vars.tree
        Events.on(FileTreeInitEvent.class, e -> Core.app.post(() -> {
            UnityShaders.load();
            UnityObjs.load();
            UnityModels.load();
            UnitySounds.load();
        }));

        // These are irrelevant in servers.
        Events.on(ClientLoadEvent.class, e -> {
            creditsDialog = new CreditsDialog();
            jsEditDialog = new ScriptsEditorDialog();
            jsDictDialog = new ScriptsDictionaryDialog();
            cinematicDialog = new CinematicDialog();
            tagsDialog = new TagsDialog();
            objectivesDialog = new ObjectivesDialog();

            addCredits();

            UnitySettings.init();
            Speeches.init();
            Cutscene.init();

            // Recalibrate 3D camera transform before drawing.
            Triggers.listen(Trigger.preDraw, () -> {
                var cam = Core.camera;
                var cam3D = Models.camera;

                cam3D.position.set(cam.position.x, cam.position.y, 50f);
                cam3D.resize(cam.width, cam.height);
                cam3D.update();
            });

            // Localize mod display name and description.
            var mod = mods.getMod(Unity.class);

            Func<String, String> stringf = value -> Core.bundle.get("mod." + mod.name + "." + value);
            mod.meta.displayName = stringf.get("name");
            mod.meta.description = stringf.get("description");

            Core.settings.getBoolOnce("unity-install", () -> Time.runTask(5f, CreditsDialog::showList));
        });

        Utils.init();

        KamiPatterns.load();
        KamiBulletDatas.load();

        try{
            var impl = (Class<? extends DevBuild>)Class.forName("unity.mod.DevBuildImpl");
            dev = impl.getDeclaredConstructor().newInstance();

            print("Dev build class implementation found and instantiated.");
        }catch(Throwable e){
            print("Dev build class implementation not found; defaulting to regular user implementation.");
            dev = new DevBuild(){};
        }

        if(dev.isDev()) Log.level = LogLevel.debug;

        music = new MusicHandler(){};
        tap = new TapHandler();
        antiCheat = new AntiCheat();

        cinematicEditor = new CinematicEditor();

        asyncCore.processes.add(
            lights = new LightProcess(),
            scoring = new ContentScoreProcess()
        );

        Core.app.post(() -> {
            JSBridge.init();
            JSBridge.importDefaults(JSBridge.unityScope);
        });
    }

    @Override
    public void init(){
        music.setup();
        antiCheat.setup();
        dev.setup();

        UnityCall.init();
        BlockMovement.init();

        dev.init();
    }

    @Override
    public void loadContent(){
        Faction.init();

        // I don't see a reason to use ContentList[] here, creates unnecessary array and iteration and takes more lines of codes.
        UnityItems.load();
        UnityStatusEffects.load();
        UnityWeathers.load();
        UnityLiquids.load();
        UnityBullets.load();
        UnityWeaponTemplates.load();
        UnityUnitTypes.load();
        UnityBlocks.load();
        UnityPlanets.load();
        UnitySectorPresets.load();
        UnityTechTree.load();
        UnityParts.load();
        Overwriter.load();

        FactionMeta.init();
        UnityEntityMapping.init();

        logContent();
    }

    public void logContent(){
        for(var faction : Faction.all){
            var array = FactionMeta.getByFaction(faction, Object.class);
            print(LogLevel.debug, "", Strings.format("Faction @ has @ contents.", faction, array.size));
        }

        Seq<Class<?>> ignored = Seq.with(Floor.class, Prop.class);
        for(var content : content.getContentMap()){
            content.each(c -> {
                if(
                    (c.minfo.mod == null || c.minfo.mod.main != this) ||
                    !(c instanceof UnlockableContent cont)
                ) return;

                if(Core.bundle.getOrNull(cont.getContentType() + "." + cont.name + ".name") == null){
                    print(LogLevel.debug, "", Strings.format("@ has no bundle entry for name", cont));
                }

                if(!ignored.contains(t -> t.isAssignableFrom(cont.getClass())) && Core.bundle.getOrNull(cont.getContentType() + "." + cont.name + ".description") == null){
                    print(LogLevel.debug, "", Strings.format("@ has no bundle entry for description", cont));
                }
            });
        }
    }

    protected void addCredits(){
        try{
            var group = (Group)ui.menuGroup.getChildren().first();

            if(mobile){
                //TODO button for mobile
            }else{
                group.fill(c ->
                    c.bottom().left()
                        .button("", UnityStyles.creditst, creditsDialog::show)
                        .size(84, 45)
                        .name("unity credits")
                );
            }
        }catch(Throwable t){
            print(LogLevel.err, "Couldn't create Unity's credits button", Strings.getFinalCause(t));
        }
    }

    public static void print(Object... args){
        print(LogLevel.info, " ", args);
    }

    public static void print(LogLevel level, Object... args){
        print(level, " ", args);
    }

    public static void print(LogLevel level, String separator, Object... args){
        var builder = new StringBuilder();
        if(args == null){
            builder.append("null");
        }else{
            for(int i = 0; i < args.length; i++){
                builder.append(args[i]);
                if(i < args.length - 1) builder.append(separator);
            }
        }

        Log.log(level, "&lm&fb[unity]&fr @", builder.toString());
    }
}
