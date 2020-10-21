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

    private Seq<String> authors = new Seq<>();

    public Unity(){
        Events.on(ClientLoadEvent.class, e -> {
            LoadedMod mod = Vars.mods.locateMod("unity");
            Vars.tree.addFile("authors", mod.root.child("main").child("assets").child("authors"));

            authors = Seq.with(Vars.tree.get("authors").readString("UTF-8").split("\n"));

            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog(stringf.get("welcome-title"));
                dialog.addCloseButton();
                dialog.cont.add("Project Unity v" + mod.meta.version).fillX().wrap().get().setAlignment(Align.center);
                dialog.cont.row();
                dialog.cont.add(stringf.get("welcome-text"));
                dialog.cont.row();

                dialog.cont.add(stringf.get("credits")).fillX().wrap().get().setAlignment(Align.center);
                dialog.cont.row();

                if(!authors.isEmpty()){
                    dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
                    dialog.cont.row();
                    dialog.cont.add(stringf.get("authors"));
                    dialog.cont.row();

                    dialog.cont.pane(new Table(){{
                        int i = 0;
                        left();

                        for(String c : authors){
                            add(c + "[]").left().pad(3f).padLeft(6f).padRight(6f);

                            if(i++ % 3 == 0){
                                row();
                            }
                        }
                    }});

                    dialog.show();
                }
            });
        });
    }

    @Override
    public void init(){
        Vars.enableConsole = true;

        if(!Vars.headless){
            LoadedMod mod = Vars.mods.locateMod("unity");
            String change = "mod." + mod.meta.name + ".";

            mod.meta.displayName = Core.bundle.get(change + "name");
            mod.meta.description = Core.bundle.get(change + "description");
        }
    }

    @Override
    public void loadContent(){
        for(ContentList list : unityContent){
            list.load();
        }
    }
}