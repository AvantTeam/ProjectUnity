package unity.ui.dialogs;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Links.*;
import mindustry.ui.dialogs.*;
import unity.mod.*;
import unity.mod.ContributorList.*;

public class CreditsDialog extends BaseDialog{
    static Func<String, String> stringf = value -> Core.bundle.get("mod." + value);
    
    public CreditsDialog(){
        super("@credits");

        shown(() -> {
            Core.app.post(this::setup);
        });

        shown(this::setup);
        onResize(this::setup);
    }
    
    void setup(){
        cont.clear();
        buttons.clear();

        float h = Core.graphics.isPortrait() ? 90f : 80f;
        float w = Core.graphics.isPortrait() ? 330f : 600f;

        Table in = new Table();
        ScrollPane pane = new ScrollPane(in);

        for(LinkEntry link : ModLinks.getLinks()){
            Table table = new Table(Tex.underline);
            table.margin(0);
            
            table.table(img -> {
                img.image().height(h - 5).width(40f).color(link.color);
                img.row();
                img.image().height(5).width(40f).color(link.color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
            }).expandY();

            table.table(i -> {
                i.background(Tex.buttonEdge3);
                i.image(link.icon);
            }).size(h - 5, h);

            table.table(inset -> {
                inset.add("[accent]" + link.title).growX().left();
                inset.row();
                inset.labelWrap(link.description).width(w - 100f).color(Color.lightGray).growX();
            }).padLeft(8);

            table.button(Icon.link, () -> {
                if(!Core.app.openURI(link.link)){
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(link.link);
                }
            }).size(h - 5, h);

            in.add(table).size(w, h).padTop(5).row();
        }

        shown(() -> Time.run(1f, () -> Core.scene.setScrollFocus(pane)));

        cont.add(pane).growX();
        
        addCloseButton();

        buttons.button("@credits", CreditsDialog::showList).size(200f, 64f);

        if(Core.graphics.isPortrait()){
            for(Cell<?> cell : buttons.getCells()){
                cell.width(140f);
            }
        }
    }

    public static void showList(){
        BaseDialog dialog = new BaseDialog("@credits");
        
        dialog.cont.table(t -> {
            t.add("@mod.credits.text").fillX().pad(3f).wrap().get().setAlignment(Align.center);
            t.row();

            t.add("@mod.credits.bottom-text").fillX().pad(3f).wrap().get().setAlignment(Align.center);
            t.row();
        }).pad(3f);
        
        dialog.cont.row();
        
        dialog.cont.pane(b -> {
            for(ContributionType type : ContributionType.all){
                Seq<String> list = ContributorList.getBy(type);
                if(list.size <= 0) continue;

                b.table(t -> {
                    t.add(stringf.get(type.name())).pad(3f).center();
                    t.row();
                    
                    t.pane(p -> {
                        for(String c : list){
                            p.add("[lightgray]" + c).left().pad(3f).padLeft(6f).padRight(6f);
                            p.row();
                        }
                    });
                }).pad(6f).top();
            }
        }).fillX();

        dialog.addCloseButton();
        dialog.show();
    }
    
    static class ModLinks{
        private static LinkEntry[] links;
        
        private static void init(){
            links = new LinkEntry[]{
                new LinkEntry("avant-discord", "https://discord.gg/V6ygvgGVqE", Icon.discord, Color.valueOf("7289da")),
                new LinkEntry("changelog", "https://github.com/AvantTeam/ProjectUnity/releases", Icon.list, Pal.accent.cpy()),
                new LinkEntry("avant-github", "https://github.com/AvantTeam/ProjectUnity", Icon.github, Color.valueOf("24292e")),
                new LinkEntry("bug", "https://github.com/AvantTeam/ProjectUnity/issues/new", Icon.wrench, Color.valueOf("ec7458"))
            };
        }
        
        public static LinkEntry[] getLinks(){
            if(links == null) init();
            
            return links;
        }
    }
}