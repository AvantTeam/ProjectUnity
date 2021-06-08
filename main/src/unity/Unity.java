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

    private final ContentList[] content = {
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

        try{
            Class<? extends DevBuild> impl = (Class<? extends DevBuild>)Class.forName("unity.mod.DevBuildImpl");
            dev = impl.getDeclaredConstructor().newInstance();

            print("Dev build class implementation found and instantiated.");
        }catch(Throwable e){
            print("Dev build class implementation not found; defaulting to regular user implementation.");
            dev = new DevBuild(){};
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

        music = new MusicHandler();
        tap = new TapHandler();
        antiCheat = new AntiCheat();

        if(Core.app != null){
            ApplicationListener listener = Core.app.getListeners().first();
            if(listener instanceof ApplicationCore core){
                core.add(music);
                core.add(antiCheat);
            }else{
                Core.app.addListener(music);
                Core.app.addListener(antiCheat);
            }
        }

        if(Core.settings != null){
            Core.settings.getBoolOnce("unity-install", () -> Events.on(ClientLoadEvent.class, e ->
                Time.runTask(5f, CreditsDialog::showList)
            ));
        }

        Events.on(ClientLoadEvent.class, e -> UnitySettings.init());
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
        checker.get(Vars.content.getBy(ContentType.item));
        checker.get(Vars.content.getBy(ContentType.liquid));
        checker.get(Vars.content.getBy(ContentType.planet));
        checker.get(Vars.content.getBy(ContentType.sector));
        checker.get(Vars.content.getBy(ContentType.status));
        checker.get(Vars.content.getBy(ContentType.unit));
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
