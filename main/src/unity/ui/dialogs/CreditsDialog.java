package unity.ui.dialogs;

import arc.*;
import arc.flabel.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Links.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.*;
import unity.mod.*;
import unity.mod.ContributorList.*;

public class CreditsDialog extends BaseDialog{
    static Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

    public CreditsDialog(){
        super("@credits");

        shown(() -> Core.app.post(this::setup));

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

        for(ModLink link : ModLink.all){
            Table table = new Table(Tex.underline);
            table.margin(0);

            table.table(img -> {
                img.image().height(h - 5).width(40f).color(link.entry.color);
                img.row();
                img.image().height(5).width(40f).color(link.entry.color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
            }).expandY();

            table.table(i -> {
                i.background(Tex.buttonEdge3);
                i.image(link.entry.icon);
            }).size(h - 5, h);

            table.table(inset -> {
                inset.add("[accent]" + link.entry.title).growX().left();
                inset.row();
                inset.labelWrap(link.entry.description).width(w - 100f).color(Color.lightGray).growX();
            }).padLeft(8);

            table.button(Icon.link, () -> {
                if(!Core.app.openURI(link.entry.link)){
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(link.entry.link);
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
        TextureRegion error = Core.atlas.find("error");
        BaseDialog dialog = new BaseDialog("@credits");

        dialog.cont.table(t -> {
            t.add("@mod.credits.text").fillX().pad(3f).wrap().get().setAlignment(Align.center);
            t.row();

            t.table(tb -> {
                tb.add("@mod.credits.bottom-text-one");
                tb.image(Core.atlas.find("unity-EyeOfDarkness")).padLeft(5f).padRight(3f);
                tb.add("@mod.credits.bottom-text-two");
            }).fillX().pad(3f);
            t.row();
        }).pad(3f);

        dialog.cont.row();

        dialog.cont.pane(b -> {
            for(ContributionType type : ContributionType.all){
                Seq<String> list = ContributorList.getBy(type);
                if(list.size <= 0) continue;

                b.table(Tex.button, t -> {
                    t.add(stringf.get(type.name())).pad(3f).center();
                    t.row();

                    t.image().color(Pal.accent).fillX().growX().padBottom(5f);
                    t.row();

                    t.pane(p -> {
                        for(String c : list){
                            String noLang = c.replaceAll("\\(([^\\)]*)\\)", "").replace(" ", "");
                            p.button(bt -> {
                                TextureRegion icon = Core.atlas.find("unity-" + noLang);
                                if(icon != error){
                                    bt.image(icon).padRight(3f);
                                }

                                bt.add(new FLabel("{wave}{rainbow}[lightgray]" + c)).left().pad(3f).padLeft(6f).padRight(6f);
                            }, Styles.transt, () -> {
                                String name = noLang;
                                if(ContributorList.githubAliases.containsKey(name)){
                                    name = ContributorList.githubAliases.get(name);
                                }

                                if(c.equals("Evl")){
                                    Core.app.openURI("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                                } else {
                                    Core.app.openURI("https://github.com/" + name);
                                }
                            });

                            p.row();
                        }
                    });
                }).pad(6f).top().width(Math.max(Core.graphics.getWidth() / 5f, 320f));
            }
        }).fillX();

        dialog.addCloseButton();
        dialog.show();
    }

    enum ModLink{
        discord("avant-discord", "https://discord.gg/V6ygvgGVqE", Icon.discord, Color.valueOf("7289da")),
        changelog("changelog", "https://github.com/AvantTeam/ProjectUnity/releases", Icon.list, Pal.accent),
        github("avant-github", "https://github.com/AvantTeam/ProjectUnity", Icon.github, Color.valueOf("24292e")),
        bug("bug", "https://github.com/AvantTeam/ProjectUnity/issues/new", Icon.wrench, Color.valueOf("ec7458"));

        public static final ModLink[] all = values();

        final LinkEntry entry;

        ModLink(String name, String link, Drawable icon, Color color){
            entry = new LinkEntry(name, link, icon, color);
        }
    }
}
