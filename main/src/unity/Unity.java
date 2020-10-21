package unity;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import unity.ContributorList.*;
import unity.content.*;

public class Unity extends Mod{
    private final ContentList[] unityContent = {
        new UnityItems(),
        new UnityStatusEffects(),
        new UnityBullets(),
        new UnityUnitTypes(),
        new UnityBlocks(),
        new UnityPlanets(),
        new UnityTechTree(),
    };

    public Unity(){
        ContributorList.init();

        Events.on(ClientLoadEvent.class, e -> {
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog(stringf.get("welcome-title"));

                dialog.addCloseButton();
                dialog.cont.add("Project Unity").fillX().wrap().get().setAlignment(Align.center);
                dialog.cont.row();
                dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
                dialog.cont.row();
                dialog.cont.add(stringf.get("welcome-text"));
                dialog.cont.row();
                dialog.cont.add(" ");
                dialog.cont.row();



                dialog.cont.table(Tex.button, t -> {
                    t.pane(p -> {
                        p.center();

                        for(ContributionType type : ContributionType.all){
                            if(type == ContributionType.translator) continue;

                            Seq<String> list = ContributorList.getBy(type);
                            if(list.size <= 0) continue;

                            p.add(stringf.get(type.name()));
                            p.row();
                            p.image().color(Pal.accent).fillX().height(3f).pad(3f);
                            p.row();

                            for(String c : list){
                                p.add(c + "[]").pad(3f).padLeft(6f).padRight(6f);
                                p.row();
                            }
                            /** Spacing */
                            p.add(" ");
                            p.row();
                        }

                        p.row();
                        /** Spacing */
                        p.add(" ");
                        p.row();
                        p.add(stringf.get("translators"));
                        p.row();
                        p.image().color(Pal.accent).fillX().height(3f).pad(3f);
                        p.row();


                        Seq<String> list = ContributorList.getBy(ContributionType.translator);
                        for(Language lang : Language.all){
                            Seq<String> trnsList = ContributorList.getBy(lang);
                            if(trnsList.size < 1) continue;

                            p.add(stringf.get("language-" + lang.name()) + ":").pad(3f).padLeft(6f).padRight(6f);
                            p.row();
                            p.image().color(Color.sky).fillX().height(3f).pad(3f);
                            p.row();
                            for(String c : trnsList){
                                p.add(c + "[]");
                                p.row();
                            }
                            /** Spacing */
                            p.add(" ");
                            p.row();
                        }
                    }).pad(10f).grow();
                }).width(250f).height(300f);

                dialog.show();
            });
        });
    }

    @Override
    public void init(){
        Vars.enableConsole = true;

        if(!Vars.headless){
            LoadedMod mod = Vars.mods.locateMod("unity");
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            mod.meta.displayName = stringf.get(mod.meta.name + ".displayName");
            mod.meta.description = stringf.get(mod.meta.name + ".description");
        }
    }

    @Override
    public void loadContent(){
        for(ContentList list : unityContent){
            try{
                list.load();

                Log.info("@: Loaded content list: @", getClass().getSimpleName(), list.getClass().getSimpleName());
            }catch(Exception e){
                Log.err("Error loading @ content(s): @", getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}