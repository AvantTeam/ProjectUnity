package unity.ui;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.scene.Element;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.world.Block;
import unity.gen.FactionMeta;
import unity.mod.Faction;
import unity.util.ReflectUtils;

import java.util.Locale;

import static mindustry.Vars.iconMed;

//idk
public class UnityUI{
    public PartsEditorDialog partsEditor;
    public int one = 1;


    //blockfrag editings
    ObjectSet<Faction> included = new ObjectSet<>();
    ObjectMap<Faction,Float> lastClicked = new ObjectMap<>();
    boolean includeVanilla = true;
    String searchstr="",prevstr="";
    boolean collapsed = true;

    Cons<Table> advancedOptions = table -> {
        table.clearChildren();
        if(!collapsed){
            TextField textField = new TextField("",Styles.nodeField);
            textField.update(() -> {
                searchstr = textField.getText();
                if(prevstr.equals(searchstr)){
                    return;
                }
                prevstr = searchstr;
                reloadBlocks();
            });
            textField.setMessageText("search blocks.");
            table.add(textField).growX().left().pad(0);
        }
    };
    ImageButtonStyle factiontoggle = new ImageButtonStyle(){{
        imageDownColor = Pal.accent;
        imageOverColor = Color.lightGray;
        imageUpColor = Color.white;
    }};


    public void init(){
        partsEditor = new PartsEditorDialog();

        Events.run(Trigger.update,()->{
            if(Vars.ui.hudfrag.blockfrag==null){
                return;
            }
            Table top = ReflectUtils.getFieldValue(Vars.ui.hudfrag.blockfrag,ReflectUtils.getField(Vars.ui.hudfrag.blockfrag,"topTable"));
            if(top!=null){
                ///needs to be triggered in this manner, otherwise hovered blocks will not display stats.
                var method = ReflectUtils.findMethod(Vars.ui.hudfrag.blockfrag.getClass(),"hasInfoBox",true);
                top.visible(()->ReflectUtils.invokeMethod(Vars.ui.hudfrag.blockfrag,method)==Boolean.TRUE || 1==one);
                if(top.find("faction table")!=null){
                    return;
                }
                included.clear();
                for(Faction f:Faction.all){
                    included.add(f);
                }
                Table advtable = new Table();
                top.row();
                top.image().growX().padTop(5).padLeft(0).padRight(0).height(3).color(Pal.gray); //top divider
                top.row();
                top.table(tp->{
                    var pane = tp.add(new ScrollPane(
                        new Table((tbl)->{
                            tbl.left();
                            ///add the faction buttons
                            for(Faction f:Faction.all){
                                var factionCheck =tbl.button(new TextureRegionDrawable(f.icon), Styles.emptyi,()->{
                                    if(included.contains(f)){
                                        included.remove(f);
                                    }else{
                                        if(Time.globalTime-lastClicked.get(f,0f)<30){
                                            included.clear();
                                        }
                                        included.add(f);

                                    }
                                    lastClicked.put(f,Time.globalTime);
                                    reloadBlocks();
                                }).tooltip(f.localizedName).size(46f).get();
                                factionCheck.resizeImage(iconMed);

                                factionCheck.update(()->{
                                    factionCheck.getStyle().imageUpColor = included.contains(f)? f.color : Color.gray;
                                    factionCheck.setChecked(included.contains(f));
                                });
                            }
                        })
                    )).left().growX().get();
                    pane.name = "faction table";
                    pane.setScrollingDisabledY(true);
                    ///needs to be done so click doesn't remove scroll focus from the game zoom
                    pane.update(()->{
                        if(pane.hasScroll()){
                            Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                            if(result == null || !result.isDescendantOf(pane)){
                                Core.scene.setScrollFocus(null);
                            }
                        }
                    });
                    tp.button(Icon.up,Styles.emptyi,()->{
                       collapsed=!collapsed;
                       advancedOptions.get(advtable);
                       reloadBlocks();
                   }).update(b->{b.setChecked(collapsed); b.getStyle().imageUp = collapsed?Icon.up:Icon.down; })
                    .size(46f).growX().right();
                }).pad(0).margin(0).left().growX();
                top.row();
                top.add(advtable).growX().pad(0).margin(0);
            }
        });
    }

    public void reloadBlocks(){
        Table table = ReflectUtils.getFieldValue(Vars.ui.hudfrag.blockfrag,ReflectUtils.getField(Vars.ui.hudfrag.blockfrag,"toggler"));
        for(Block b:Vars.content.blocks()){
            if(FactionMeta.map(b)==null){
                b.placeablePlayer = included.contains(Faction.vanilla);
            }else{
                b.placeablePlayer = included.contains(FactionMeta.map(b));
            }
            if(!collapsed){
                b.placeablePlayer &= searchstr.equals("") || b.localizedName.toLowerCase(Locale.ROOT).contains(searchstr.toLowerCase(Locale.ROOT));
            }
        }
        //fuckery (updates the blocks)
        var b = (ImageButton)table.find("category-"+ Vars.ui.hudfrag.blockfrag.currentCategory.name());
        b.fireClick();

    }

}
