package unity;

import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.struct.Seq;
import arc.util.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.content.*;
import unity.gen.*;
import unity.mod.*;
import unity.net.*;
import unity.ui.*;
import unity.ui.dialogs.*;
import unity.util.*;

import static mindustry.Vars.*;

public class Unity extends Mod{
    public static MusicHandler musicHandler;
    public static UnityAntiCheat antiCheat;

    private final ContentList[] unityContent = {
        new UnityItems(),
        new OverWriter(),
        new UnityStatusEffects(),
        new UnityLiquids(),
        new UnityBullets(),
        new UnityUnitTypes(),
        new UnityBlocks(),
        new UnityPlanets(),
        new UnitySectorPresets(),
        new UnityTechTree()
    };

    private static LoadedMod unity;

    public Unity(){
        ContributorList.init();

        if(Core.assets != null){
            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));
            Core.assets.load(new UnityStyles());
        }

        if(!headless){
            Events.on(ContentInitEvent.class, e -> {
                Regions.load();
            });

            Events.on(FileTreeInitEvent.class, e -> {
                UnityObjs.load();
                UnitySounds.load();
                UnityMusics.load();
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
        });

        musicHandler = new MusicHandler();
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
    }

    @Override
    public void init(){
        enableConsole = true;
        musicHandler.setup();
        antiCheat.setup();
        UnityCall.init();

        if(!headless){
            unity = mods.locateMod("unity");
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            unity.meta.displayName = stringf.get(unity.meta.name + ".name");
            unity.meta.description = stringf.get(unity.meta.name + ".description");

            Scripts scripts = mods.getScripts();
            scripts.runConsole("const Unity = Vars.mods.locateMod(\"unity\").main");
        }
    }

    @Override
    public void loadContent(){
        for(ContentList list : unityContent){
            list.load();

            Log.info("@: Loaded content list: @", getClass().getSimpleName(), list.getClass().getSimpleName());
        }

        FactionMeta.init();
        ExpMeta.init();
        UnityEntityMapping.init();
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

        Log.infoTag("unity", builder.toString());
    }

    public static Class<?> forName(String canonical){
        try{
            return Class.forName(canonical, true, ((Unity)unity.main).getClass().getClassLoader());
        }catch(Exception e){
            Log.err(e);
            return null;
        }
    }

    public static <T> T newInstance(Class<T> type, Object... parameters){
        try{
            Class<?>[] types = Seq.with(parameters).map(param -> {
                Class<?> ptype = param.getClass();
                return ptype.isAnonymousClass() ? ptype.getSuperclass() : ptype;
            }).toArray();

            return type.getDeclaredConstructor(types).newInstance(parameters);
        }catch(Exception e){
            Log.err(e);
            return null;
        }
    }
}
