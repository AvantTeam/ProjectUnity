package unity;

import arc.*;
import arc.assets.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.*;
import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.ai.kami.*;
import unity.content.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;
import unity.sync.*;
import unity.ui.*;
import unity.ui.dialogs.*;
import unity.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class Unity extends Mod implements ApplicationListener{
    public static MusicHandler musicHandler;
    public static TapHandler tapHandler;
    public static UnityAntiCheat antiCheat;
    public static UnitySettings unitySettings = new UnitySettings();

    public final ContentList[] unityContent = new ContentList[]{
        new UnityItems(),
        new UnityStatusEffects(),
        new UnityWeathers(),
        new UnityLiquids(),
        new UnityBullets(),
        new UnityUnitTypes(),
        new UnityBlocks(),
        new UnityPlanets(),
        new UnitySectorPresets(),
        new UnityTechTree(),
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

        register.get("skip", Strings.join(
            "() => {",
                "for(let i = Vars.state.wave; i < Vars.state.rules.winWave; i++){",
                    "Vars.logic.runWave();",
                "}",
            "}"
        ));
    }

    @Override
    public void loadContent(){
        for(ContentList list : unityContent){
            list.load();
            print("Loaded content list: " + list.getClass().getSimpleName());
        }

        FactionMeta.init();
        UnityEntityMapping.init();

        for(Faction faction : Faction.all) {
            print(Strings.format("Faction @ has @ contents.", faction.name, FactionMeta.getByFaction(faction, Content.class).size));
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
