package unity;

import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.world.blocks.environment.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.ai.kami.*;
import unity.cinematic.*;
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

@SuppressWarnings("unchecked")
public class Unity extends Mod implements ApplicationListener{
    public static MusicHandler music;
    public static TapHandler tap;
    public static AntiCheat antiCheat;
    public static DevBuild dev;

    private static final ContentList[] content = {
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

        if(Core.assets != null){
            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(tree));
        }

        KamiPatterns.load();
        KamiBulletDatas.load();

        Events.on(ContentInitEvent.class, e -> {
            Regions.load();
            KamiRegions.load();
        });

        Events.on(FileTreeInitEvent.class, e -> {
            UnityObjs.load();
            UnitySounds.load();
            UnityShaders.load();
            UnityFonts.load();
            UnityStyles.load();
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

        new TestType("test");

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
