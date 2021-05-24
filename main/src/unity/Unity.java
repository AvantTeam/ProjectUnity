package unity;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.*;
import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.world.blocks.environment.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.ai.kami.*;
import unity.content.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;
import unity.sync.*;
import unity.type.*;
import unity.ui.*;
import unity.ui.dialogs.*;
import unity.util.*;
import younggamExperimental.Parts;

import static mindustry.Vars.*;

public class Unity extends Mod implements ApplicationListener{
    public static MusicHandler musicHandler;
    public static TapHandler tapHandler;
    public static UnityAntiCheat antiCheat;
    public static UnitySettings unitySettings = new UnitySettings();

    public final ContentList[] unityContent = new ContentList[]{
        new UnityContentTypes(),
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

    private static LoadedMod unity;

    public Unity(){
        ContributorList.init();
        if(Core.app != null){
            Core.app.addListener(this);
        }

        KamiPatterns.load();
        KamiBulletDatas.load();

        if(Core.assets != null){
            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));
            Core.assets.load(new UnityStyles());
        }

        if(!headless){
            Events.on(ContentInitEvent.class, e -> {
                Regions.load();
                KamiRegions.load();
            });

            Events.on(FileTreeInitEvent.class, e -> {
                UnityObjs.load();
                UnitySounds.load();
                UnityMusics.load();
                UnityShaders.load();
            });

            Events.on(ClientLoadEvent.class, e -> addCredits());
        }else{
            UnityObjs.load();
            UnitySounds.load();
            UnityMusics.load();
        }

        Events.on(DisposeEvent.class, e -> {
            UnityObjs.dispose();
            UnitySounds.dispose();
            UnityMusics.dispose();
            UnityShaders.dispose();
        });

        musicHandler = new MusicHandler();
        tapHandler = new TapHandler();
        antiCheat = new UnityAntiCheat();

        if(Core.app != null){
            ApplicationListener listener = Core.app.getListeners().first();
            if(listener instanceof ApplicationCore core){
                core.add(musicHandler);
                core.add(antiCheat);
            }else{
                Core.app.addListener(musicHandler);
                Core.app.addListener(antiCheat);
            }
        }

        if(Core.settings != null){
            Core.settings.getBoolOnce("unity-install", () -> {
                Events.on(ClientLoadEvent.class, e -> {
                    Time.runTask(5f, CreditsDialog::showList);
                });
            });
        }

        Events.on(ClientLoadEvent.class, e -> {
            unitySettings.init();
        });
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
        enableConsole = true;
        musicHandler.setup();
        antiCheat.setup();
        UnityCall.init();
        BlockMovement.init();

        if(!headless){
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            unity.meta.displayName = stringf.get(unity.meta.name + ".name");
            unity.meta.description = stringf.get(unity.meta.name + ".description");

            initScripts();
        }
    }

    private void initScripts(){
        Scripts scripts = mods.getScripts();
        Cons2<String, String> register = (var, expression) -> {
            scripts.runConsole(Strings.format("const @ = @", var, expression));
        };

        register.get(
            getClass().getSimpleName(),
            "Vars.mods.locateMod(\"unity\").main"
        );

        for(int i = 0; i < unityContent.length; i++){
            var list = unityContent[i];
            register.get(
                list.getClass().getSimpleName(),
                getClass().getSimpleName() + ".unityContent[" + i + "]"
            );
        }

        register.get("launch",
            """
            (planet, i) => {
                Vars.control.playSector(planet.sectors.get(i));
            }
            """
        );

        register.get("skip",
            """
            () => {
                for(let i = Vars.state.wave; i < Vars.state.rules.winWave; i++){
                    Vars.logic.runWave();
                }
            }
            """
        );
    }

    @Override
    public void loadContent(){
        for(ContentList list : unityContent){
            list.load();
            print("Loaded content list: " + list.getClass().getSimpleName());
        }

        FactionMeta.init();
        UnityEntityMapping.init();

        new TestType("test");

        for(Faction faction : Faction.all){
            var array = FactionMeta.getByFaction(faction, Object.class);
            print(Strings.format("Faction @ has @ contents.", faction.name, array.size));
        }

        Seq<Class<?>> ignored = Seq.with(Floor.class, Boulder.class);
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

        checker.get(content.blocks());
        checker.get(content.getBy(ContentType.item));
        checker.get(content.getBy(ContentType.liquid));
        checker.get(content.getBy(ContentType.planet));
        checker.get(content.getBy(ContentType.sector));
        checker.get(content.getBy(ContentType.status));
        checker.get(content.getBy(ContentType.unit));
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

        Log.info("[#@][unity][] @", Color.cyan, builder.toString());
    }

    public static Class<?> forName(String canonical){
        try{
            return Class.forName(canonical, true, unity.loader);
        }catch(Exception e){
            Log.err(e);
            return null;
        }
    }

    public static class UnityModLoadEvent{}
}
