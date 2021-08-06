package unity;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.mod.*;
import mindustry.world.blocks.environment.*;
import unity.ai.kami.*;
import unity.assets.list.*;
import unity.assets.loaders.*;
import unity.assets.type.g3d.*;
import unity.async.*;
import unity.cinematic.*;
import unity.content.*;
import unity.gen.*;
import unity.gen.Regions.*;
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
            Core.assets.setLoader(Model.class, new ModelLoader(tree));
            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));
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
        });

        Events.on(ClientLoadEvent.class, e -> {
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

        music = new MusicHandler(){};
        tap = new TapHandler();
        antiCheat = new AntiCheat();
        model = new Models();

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

        //this internal implementation is used for loading Regions' outlines and such
        new UnlockableContent("internal-implementation"){
            @Override
            public void loadIcon(){
                fullIcon = Core.atlas.find("clear");
                uiIcon = Core.atlas.find("clear");
            }

            @Override
            public boolean isHidden(){
                return false;
            }

            @Override
            public boolean logicVisible(){
                return false;
            }

            @Override
            public void unlock(){
                //does nothing
            }

            @Override
            public void quietUnlock(){
                //does nothing
            }

            @Override
            public void createIcons(MultiPacker packer){
                Regions.load(); //load shadowed regions first

                var color = new Color();
                for(var field : Regions.class.getDeclaredFields()){
                    if(!TextureRegion.class.isAssignableFrom(field.getType()) || !field.getName().endsWith("OutlineRegion")) continue;

                    var raw = ReflectUtils.findField(Regions.class, field.getName().replace("Outline", ""), false);
                    AtlasRegion region = ReflectUtils.getField(null, raw);

                    if(region.found()){
                        var meta = raw.getAnnotation(Outline.class);
                        var outlineColor = Color.valueOf(color, meta == null ? "464649" : meta.color());
                        var outlineRadius = meta == null ? 4 : meta.radius();

                        var pix = GraphicUtils.get(packer, region);
                        var out = Pixmaps.outline(pix, outlineColor, outlineRadius);

                        if(Core.settings.getBool("linear")){
                            Pixmaps.bleed(out);
                        }

                        packer.add(PageType.main, region.name + "-outline", out);
                    }
                }
            }

            @Override
            public ContentType getContentType(){
                return ContentType.typeid_UNUSED;
            }
        };
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
            CreditsDialog credits = new CreditsDialog();
            Group group = (Group)ui.menuGroup.getChildren().first();

            if(mobile){
                //TODO button for mobile
            }else{
                group.fill(c ->
                    c.bottom().left()
                        .button("", UnityStyles.creditst, credits::show)
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

    public static void enforce(boolean cond, String message){
        if(!cond) throw new IllegalArgumentException(message);
    }
}
