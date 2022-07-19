package unity.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import unity.mod.*;
import unity.util.*;

import java.util.*;

import static mindustry.Vars.iconMed;

public class PUUI{
    public int one = 1;


    //blockfrag editing
    ObjectSet<Faction> included = new ObjectSet<>();
    ObjectMap<Faction, Float> lastClicked = new ObjectMap<>();
    String searchStr = "", prevStr = "";
    boolean collapsed = true;
    boolean wasRefreshed = false;

    Cons<Table> advancedOptions = table -> {
        table.clearChildren();
        if(!collapsed){
            TextField textField = new TextField("", Styles.nodeField);
            textField.update(() -> {
                searchStr = textField.getText();
                if(prevStr.equals(searchStr)){
                    return;
                }
                prevStr = searchStr;
                reloadBlocks();
            });
            textField.setMessageText("search blocks.");
            table.add(textField).growX().left().pad(0);
        }
    };

    public void init(){
        if(Vars.headless){
            return;
        }
        Events.on(SaveLoadEvent.class, e -> {
            included.clear();
            for(Faction f : Faction.all){
                included.add(f);
            }
        });
        Events.run(Trigger.update, () -> {
            if(Vars.ui.hudfrag.blockfrag == null){
                return;
            }
            Table top = ReflectUtils.get(Vars.ui.hudfrag.blockfrag, ReflectUtils.findf(Vars.ui.hudfrag.blockfrag.getClass(), "topTable"));
            if(top != null){
                ///needs to be triggered in this manner, otherwise hovered blocks will not display stats.
                var method = ReflectUtils.findm(Vars.ui.hudfrag.blockfrag.getClass(), "hasInfoBox");
                top.visible(() -> ReflectUtils.invoke(Vars.ui.hudfrag.blockfrag, method) == Boolean.TRUE || one == 1);
                if(top.find("faction table") == null){
                    build(top);
                }
            }
        });
    }

    private void build(Table parent){
        if(!wasRefreshed){
            included.clear();
            for(Faction f : Faction.all){
                included.add(f);
            }
        }
        Table advTable = new Table();
        parent.row();
        parent.image().growX().padTop(5).padLeft(0).padRight(0).height(3).color(Pal.gray); //top divider
        parent.row();
        parent.table(table -> {
            var pane = table.add(new ScrollPane(
            new Table(inner -> {
                inner.left();
                ///add the faction buttons
                for(Faction f : Faction.all){
                    var factionCheck = inner.button(new TextureRegionDrawable(f.icon), Styles.emptyi, () -> {
                        if(Time.globalTime - lastClicked.get(f, 0f) < 30){ //half a sec
                            included.clear();
                            included.add(f);
                        }else{
                            if(included.contains(f)){
                                included.remove(f);
                            }else{
                                included.add(f);
                            }
                        }
                        lastClicked.put(f, Time.globalTime);
                        reloadBlocks();
                    }).tooltip(f.localizedName).size(46f).get();
                    factionCheck.resizeImage(iconMed);

                    factionCheck.update(() -> {
                        factionCheck.getStyle().imageUpColor = included.contains(f) ? f.color : Color.gray;
                        factionCheck.setChecked(included.contains(f));
                    });
                }
            })
            )).left().get();
            pane.name = "faction table";
            pane.setScrollingDisabledY(true);
            ///needs to be done so click doesn't remove scroll focus from the game zoom
            pane.update(() -> {
                if(pane.hasScroll()){
                    Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                    if(result == null || !result.isDescendantOf(pane)){
                        Core.scene.setScrollFocus(null);
                    }
                }
            });
            table.button(Icon.up, Styles.emptyi, () -> {
                collapsed = !collapsed;
                advancedOptions.get(advTable);
                reloadBlocks();
            }).update(b -> {
                b.setChecked(collapsed);
                b.getStyle().imageUp = collapsed ? Icon.up : Icon.down;
            })
            .size(46f).growX().right();
        }).pad(0).margin(0).left().expandX().maxWidth(282);
        parent.row();
        parent.add(advTable).growX().pad(0).margin(0);
    }

    public void reloadBlocks(){
        Table table = ReflectUtils.get(Vars.ui.hudfrag.blockfrag, ReflectUtils.findf(Vars.ui.hudfrag.blockfrag.getClass(), "toggler"));
        for(Block b : Vars.content.blocks()){
            var faction = FactionRegistry.faction(b);
            if(faction == null){
                b.placeablePlayer = included.contains(Faction.vanilla);
            }else{
                b.placeablePlayer = included.contains(faction);
            }
            if(!collapsed){
                b.placeablePlayer &= searchStr.equals("") || b.localizedName.toLowerCase(Locale.ROOT).contains(searchStr.toLowerCase(Locale.ROOT));
            }
        }
        //fuckery (updates the blocks)
        var b = (ImageButton)table.find("category-" + Vars.ui.hudfrag.blockfrag.currentCategory.name());
        if(b == null){
            b = (ImageButton)table.find("category-" + Category.distribution.name());
        }
        if(b != null){
            b.fireClick();
        }else{
            ReflectUtils.invoke(Vars.ui.hudfrag.blockfrag.getClass(), "rebuild", null);
        }
    }
}
