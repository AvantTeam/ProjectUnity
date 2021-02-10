package unity;

import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.content.*;
import unity.gen.*;
import unity.mod.*;
import unity.ui.dialogs.*;
import unity.util.*;

import static mindustry.Vars.*;

public class Unity extends Mod{
    public static final String githubURL = "https://github.com/AvantTeam/ProjectUnity";
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
            
            Events.on(ClientLoadEvent.class, e -> {
                new CreditsDialog().show(); //currently for testing.
                addCredits();
            });
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

    private void addCredits(){ //TODO make it actually work
        CreditsDialog credits = new CreditsDialog();
        Cell menuc = ((Table)((Group)ui.menuGroup.getChildren().get(0)).getChildren().get(1)).getCells().get(1);
        Table buttons = ((Table)menuc.get());
        
        buttons.row();
        
        if(mobile){
            //TODO button for mobile
        }else{
            buttons.button(
                "Project Unity",
                new TextureRegionDrawable(Core.atlas.find("unity-icon-ammo-normal")),
                Styles.clearToggleMenut,
                credits::show
            ).marginLeft(11f);
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
