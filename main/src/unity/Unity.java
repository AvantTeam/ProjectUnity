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
import mindustry.*;
import mindustry.core.GameState.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.world.blocks.environment.*;
import unity.ai.kami.*;
import unity.assets.list.*;
import unity.assets.loaders.*;
import unity.assets.type.g3d.*;
import unity.async.*;
import unity.cinematic.*;
import unity.content.*;
import unity.editor.*;
import unity.gen.*;
import unity.mod.*;
import unity.sync.*;
import unity.ui.*;
import unity.ui.dialogs.*;
import unity.util.*;
import younggamExperimental.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class Unity extends Mod{
    public static MusicHandler music;
    public static TapHandler tap;
    public static AntiCheat antiCheat;
    public static DevBuild dev;
    public static Models model;

    public static CinematicEditor cinematicEditor;

    public static CreditsDialog creditsDialog;
    public static JSScriptDialog scriptsDialog;

    private static final ContentList[] content = {
        new UnityItems(),
        new UnityStatusEffects(),
        new UnityWeathers(),
        new UnityLiquids(),
        new UnityBullets(),
        new UnityWeaponTemplates(),
        new UnityUnitTypes(),
        new UnityBlocks(),
        new UnityPlanets(),
        new UnitySectorPresets(),
        new UnityTechTree(),
        new Parts(),
        new Overwriter()
    };

    public Unity(){
        if(!headless){
            Core.assets.setLoader(Model.class, ".g3dj", new ModelLoader(tree, new JsonReader()));
            Core.assets.setLoader(Model.class, ".g3db", new ModelLoader(tree, new UBJsonReader()));

            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));

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

        Events.on(ContentInitEvent.class, e -> {
            if(!headless){
                Regions.load();
                KamiRegions.load();
            }

            UnityFonts.load();
            UnityStyles.load();
        });

        Events.on(FileTreeInitEvent.class, e -> Core.app.post(() -> {
            UnityShaders.load();
            UnityObjs.load();
            UnityModels.load();
            UnitySounds.load();
        }));

        Events.on(DisposeEvent.class, e -> {
            UnityModels.dispose();
            UnityShaders.dispose();
            UnityObjs.dispose();
            UnitySounds.dispose();
        });

        Events.on(ClientLoadEvent.class, e -> {
            creditsDialog = new CreditsDialog();
            scriptsDialog = new JSScriptDialog();

            addCredits();

            UnitySettings.init();
            SpeechDialog.init();

            Triggers.listen(Trigger.preDraw, () -> {
                model.camera.position.set(Core.camera.position.x, Core.camera.position.y, 0f);
                model.camera.resize(Core.camera.width, Core.camera.height);
                model.camera.update();
            });

            var mod = mods.getMod(Unity.class);

            Func<String, String> stringf = value -> Core.bundle.get("mod." + mod.name + "." + value);
            mod.meta.displayName = stringf.get("name");
            mod.meta.description = stringf.get("description");

            Core.settings.getBoolOnce("unity-install", () -> Time.runTask(5f, CreditsDialog::showList));
        });

        Events.on(StateChangeEvent.class, e -> Core.app.post(() -> {
            if(e.from == State.menu && e.to == State.playing){
                WorldListener.instance.add();
            }else if(e.to == State.menu){
                WorldListener.instance.remove();
            }
        }));

        ContributorList.init();

        KamiPatterns.load();
        KamiBulletDatas.load();

        try{
            Class<? extends DevBuild> impl = (Class<? extends DevBuild>)Class.forName("unity.mod.DevBuildImpl");
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
        model = new Models();

        cinematicEditor = new CinematicEditor();

        asyncCore.processes.add(new LightProcess(), new ContentScoreProcess());
    }

    @Override
    public void init(){
        music.setup();
        antiCheat.setup();
        dev.setup();

        UnityCall.init();
        BlockMovement.init();

        dev.init();
        JSBridge.importDefaults(JSBridge.unityScope);
    }

    @Override
    public void loadContent(){
        Faction.init();

        for(ContentList list : content){
            list.load();
            print("Loaded content list: " + list.getClass().getSimpleName());
        }

        FactionMeta.init();
        UnityEntityMapping.init();

        logContent();
    }

    public void logContent(){
        for(Faction faction : Faction.all){
            var array = FactionMeta.getByFaction(faction, Object.class);
            print(Strings.format("Faction @ has @ contents.", faction, array.size));
        }

        Seq<Class<?>> ignored = Seq.with(Floor.class, Prop.class);
        for(var content : Vars.content.getContentMap()){
            content.each(c -> {
                if(
                    !(c instanceof UnlockableContent cont) ||
                    (c.minfo.mod == null || c.minfo.mod.main == null || c.minfo.mod.main.getClass() != Unity.class)
                ) return;

                if(Core.bundle.getOrNull(cont.getContentType() + "." + cont.name + ".name") == null){
                    print(Strings.format("@ has no bundle entry for name", cont));
                }

                if(!ignored.contains(t -> t.isAssignableFrom(cont.getClass())) && Core.bundle.getOrNull(cont.getContentType() + "." + cont.name + ".description") == null){
                    print(Strings.format("@ has no bundle entry for description", cont));
                }
            });
        }
    }

    protected void addCredits(){
        try{
            Group group = (Group)ui.menuGroup.getChildren().first();

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
        StringBuilder builder = new StringBuilder();
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
