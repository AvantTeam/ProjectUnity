package unity;

import arc.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
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
                dialog.cont.add(stringf.get("welcome-text"));
                dialog.cont.row();

                dialog.cont.add(stringf.get("credits")).fillX().wrap().get().setAlignment(Align.center);
                dialog.cont.row();

                for(ContributionType type : ContributionType.all){
                    if(type == ContributionType.translator) continue;

                    Seq<String> list = ContributorList.getBy(type);
                    if(list.size > 0){
                        dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
                        dialog.cont.row();
                        dialog.cont.add(stringf.get(type.name()));
                        dialog.cont.row();

                        dialog.cont.pane(new Table(){{
                            int i = 0;
                            left();

                            for(String c : list){
                                add(c + "[]").left().pad(3f).padLeft(6f).padRight(6f);

                                if(i++ % 3 == 0){
                                    row();
                                }
                            }
                        }});
                    }
                }

                Seq<String> list = ContributorList.getBy(ContributionType.translator);
                if(list.size > 0){
                    for(Language lang : Language.all){
                        Seq<String> trnsList = ContributorList.getBy(lang);
                        if(trnsList.size < 1) continue;

                        dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
                        dialog.cont.row();
                        dialog.cont.add(stringf.get("translator"));
                        dialog.cont.row();

                        dialog.cont.pane(new Table(){{
                            int i = 0;
                            left();

                            add(stringf.get("language-" + lang.name())).pad(3f).padLeft(6f).padRight(6f);
                            for(String c : trnsList){
                                add(c + "[]");

                                if(i++ % 3 == 0){
                                    row();
                                }
                            }

                            row();
                        }});
                    }
                }

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