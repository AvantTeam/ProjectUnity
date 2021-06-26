package unity;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.scene.*;
import arc.struct.*;
import arc.util.*;
import arc.util.async.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.world.blocks.environment.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.ai.kami.*;
import unity.async.*;
import unity.cinematic.*;
import unity.content.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;
import unity.sync.*;
import unity.ui.*;
import unity.ui.dialogs.*;
import unity.util.*;
import younggamExperimental.Parts;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class Unity extends Mod implements ApplicationListener{
    public static MusicHandler music;
    public static TapHandler tap;
    public static AntiCheat antiCheat;
    public static DevBuild dev;

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
        new OverWriter()
    };

    private final AsyncExecutor exec = new AsyncExecutor();
    private static LoadedMod unity;

    public Unity(){
        ContributorList.init();

        Core.app.addListener(this);
        Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));

        KamiPatterns.load();
        KamiBulletDatas.load();

        Events.on(ContentInitEvent.class, e -> {
            if(!headless){
                Regions.load(); //load existing regions

                //TODO use runtime annotations for outline color
                Color outlineColor = Color.valueOf("404049");
                for(var field : Regions.class.getDeclaredFields()){
                    if(!TextureRegion.class.isAssignableFrom(field.getType()) || !field.getName().endsWith("OutlineRegion")) continue;

                    var f = ReflectUtils.findField(Regions.class, field.getName().replaceFirst("Outline", ""), false);
                    TextureRegion raw = ReflectUtils.getField(null, f);

                    if(raw instanceof AtlasRegion at && at.found()){
                        PixmapRegion sprite = Core.atlas.getPixmap(at);
                        Texture out = new Texture(Pixmaps.outline(sprite, outlineColor, 4));

                        Core.atlas.addRegion(at.name + "-outline", out, 0, 0, out.width, out.height);
                    }
                }

                Regions.load(); //load generated regions
                KamiRegions.load();
            }

            UnityFonts.load();
            UnityStyles.load();
        });

        Events.on(FileTreeInitEvent.class, e -> {
            UnityObjs.load();
            UnitySounds.load();
            UnityShaders.load();
        });

        Events.on(DisposeEvent.class, e ->
            UnityShaders.dispose()
        );

        Events.on(ClientLoadEvent.class, e -> {
            addCredits();

            UnitySettings.init();
            SpeechDialog.init();

            Core.settings.getBoolOnce("unity-install", () -> Time.runTask(5f, CreditsDialog::showList));
        });

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

        asyncCore.processes.add(new LightProcess());
    }

    @Override
    public void update(){
        unity = mods.locateMod("unity");
        if(unity != null){
            Core.app.removeListener(this);
            Events.fire(new UnityModLoadEvent());
        }
    }

    @Override
    public void init(){
        music.setup();
        antiCheat.setup();
        dev.setup();

        UnityCall.init();
        BlockMovement.init();

        if(!headless){
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            unity.meta.displayName = stringf.get(unity.meta.name + ".name");
            unity.meta.description = stringf.get(unity.meta.name + ".description");

            for(AtlasRegion region : Core.atlas.getRegions()){
                //assume `unity-` is always the PU sprites
                if(!region.name.startsWith("unity-")) continue;

                exec.submit(() -> {
                    try{
                        GraphicUtils.antialias(region);
                    }catch(Throwable t){
                        Log.err(Strings.format("Failed to antialias @", region.name), t);
                    }
                });
            }
        }

        dev.initScripts();
    }

    @Override
    public void loadContent(){
        for(ContentList list : content){
            list.load();
            print("Loaded content list: " + list.getClass().getSimpleName());
        }

        FactionMeta.init();
        UnityEntityMapping.init();

        for(Faction faction : Faction.all){
            var array = FactionMeta.getByFaction(faction, Object.class);
            print(Strings.format("Faction @ has @ contents.", faction.name, array.size));
        }

        Seq<Class<?>> ignored = Seq.with(Floor.class, Prop.class);
        Cons<Seq<? extends Content>> checker = list -> {
            for(var cont : list){
                if(
                    !(cont instanceof UnlockableContent ucont) ||
                    (cont.minfo.mod == null || !cont.minfo.mod.name.equals("unity"))
                ) continue;

                if(Core.bundle.getOrNull(ucont.getContentType() + "." + ucont.name + ".name") == null){
                    print(Strings.format("@ has no bundle entry for name", ucont));
                }

                if(!ignored.contains(c -> c.isAssignableFrom(ucont.getClass())) && Core.bundle.getOrNull(ucont.getContentType() + "." + ucont.name + ".description") == null){
                    print(Strings.format("@ has no bundle entry for description", ucont));
                }
            }
        };

        checker.get(Vars.content.blocks());
        checker.get(Vars.content.items());
        checker.get(Vars.content.liquids());
        checker.get(Vars.content.planets());
        checker.get(Vars.content.sectors());
        checker.get(Vars.content.statusEffects());
        checker.get(Vars.content.units());
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
            Log.err("Couldn't create Unity's credits button", t);
        }
    }

    //TODO support for LogLevel too
    public static void print(Object... args){
        StringBuilder builder = new StringBuilder();
        if(args == null){
            builder.append("null");
        }else{
            for(int i = 0; i < args.length; i++){
                builder.append(args[i]);
                if(i < args.length - 1) builder.append(", ");
            }
        }

        Log.info("&lm&fb[unity]&fr @", builder.toString());
    }

    public static LoadedMod mod(){
        return unity;
    }

    public static class UnityModLoadEvent{}
}
