package unity;

import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.util.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.net.ValidateException;
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

        if(netClient != null){
            net.handleClient(UnityInvokePacket.class, packet -> {
                UnityRemoteReadClient.readPacket(packet.reader(), packet.type);
            });
        }else{
            Log.warn("'netClient' is null");
        }

        if(netServer != null){
            net.handleServer(UnityInvokePacket.class, (con, packet) -> {
                if(con.player == null) return;
    
                try{
                    UnityRemoteReadServer.readPacket(packet.reader(), packet.type, con.player);
                }catch(ValidateException e){
                    Log.err("Validation failed for '@': @", e.player, e.getMessage());
                }catch(RuntimeException e){
                    if(e.getCause() instanceof ValidateException v){
                        Log.err("Validation failed for '@': @", v.player, v.getMessage());
                    }else{
                        throw e;
                    }
                }
            });
        }else{
            Log.warn("'netServer' is null");
        }

        if(!headless){
            LoadedMod mod = mods.locateMod("unity");
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            mod.meta.displayName = stringf.get(mod.meta.name + ".name");
            mod.meta.description = stringf.get(mod.meta.name + ".description");
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
        StringBuilder h = new StringBuilder();
        if(args == null) h.append("null");
        else{
            for(var o : args){
                h.append(o == null ? "null" : o.toString());
                h.append(", ");
            }
        }
        Log.infoTag("unity", h.toString());
    }
}
