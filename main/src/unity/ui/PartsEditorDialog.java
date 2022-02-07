package unity.ui;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Strings;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.ItemDisplay;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.meta.Stat;
import unity.parts.*;

import java.util.Base64;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.tilesize;

public class PartsEditorDialog extends BaseDialog{
    public ModularConstructBuilder builder;
    Cons<byte[]> consumer;
    public PartsEditorElement editorElement;
    ModularPartType hoveredPart = null;
    public Cons2<ModularConstructBuilder, Table> infoViewer;

    public ObjectMap<String,Seq<ModularPartType>> avalibleParts = new ObjectMap<>();
    boolean info = false;

    //part select
    Cons<Table> partSelectBuilder = table -> {
        table.clearChildren();
        table.top();
        table.add(Core.bundle.get("ui.parts.select")).growX().left().color(Pal.gray);

        for(var category: avalibleParts){
            table.row();
            table.add(Core.bundle.get("ui.parts.category."+category.key)).growX().left().color(Pal.accent);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();
            table.table(list -> {
                int i = 0;
                for(var part : category.value){
                    if(i!=0 && i%5==0){
                        list.row();
                    }
                    list.left();
                    ImageButton partbutton = list.button(new TextureRegionDrawable(part.icon), Styles.selecti, () -> {
                        if(editorElement.selected == part){
                            editorElement.deselect();
                        }else{
                            editorElement.select(part);
                        }
                    }).pad(3).size(46f).name("part-" + part.name).get();
                    partbutton.resizeImage(iconMed);
                    //on hover display stats in box below
                    partbutton.hovered(() -> {
                        hoveredPart = part;
                    });
                    // unselect if another one got selected
                    partbutton.update(() -> {
                        partbutton.setChecked(editorElement.selected == part);
                        //possibly set gray if disallowed.
                        partbutton.forEach(elem -> elem.setColor(Color.white));
                        if(builder.root==null && !part.root){
                            partbutton.forEach(elem -> elem.setColor(Color.darkGray));
                        }
                    });
                    //deselect hover
                    partbutton.exited(() -> {
                        if(hoveredPart == part){
                            hoveredPart = null;
                        }
                    });
                    i++;
                }
            }).growX().left().padBottom(10);
        }
    };
    //part select & info container
    Cons<Table> leftSideBuilder = table -> {
        table.clearChildren();
        //Middle content
        Table content = new Table();
        //top side, tabs
        Table tabs = new Table();
        ImageButton partsSelectMenuButton = new ImageButton(Icon.box, Styles.clearPartiali);
        partsSelectMenuButton.clicked(()->{
            partSelectBuilder.get(content);
            builder.onChange = ()->{};
        });
        tabs.add(partsSelectMenuButton).size(64).pad(8);

        ImageButton infoMenuButton = new ImageButton(Icon.info, Styles.clearPartiali);
        infoMenuButton.clicked(()->{
            infoViewer.get(builder,content);
            builder.onChange = ()->{
                infoViewer.get(builder,content);
            };
        });
        tabs.add(infoMenuButton).size(64);

        //middle
        ScrollPane scrollPane = new ScrollPane(content,Styles.defaultPane);
        partSelectBuilder.get(content);

        //bottom info
        Table stats = new Table();
        stats.update(()->{
            stats.clear();
            stats.top().left().margin(5);
            ModularPartType selected = null;
            if(editorElement.selected!=null){
                selected = editorElement.selected;
            }
            if(hoveredPart!=null){
                selected = hoveredPart;
            }
            if(selected!=null){
                selected.display(stats);
            }
        });

        table.align(Align.top);
        table.add(tabs).align(Align.left);
        table.row();
        table.add(scrollPane).
            align(Align.top).growX().left().padBottom(10).growY().minWidth(280).get()
            .setScrollingDisabled(true, false);
        table.row();
        table.add(stats);

    };
    Table selectSide;

    public PartsEditorDialog(){
        super("parts");
        this.builder= new ModularConstructBuilder(3,3);
        editorElement = new PartsEditorElement(this.builder);
        clearChildren();
        buttons.defaults().size(160f, 64f);
        buttons.button(Icon.flipX,Styles.clearTogglei,()->{
            editorElement.mirror = !editorElement.mirror;
        }).update(i->{i.setChecked(editorElement.mirror);}).tooltip("mirror").width(64);
        buttons.button(Icon.file,()->{
            builder.clear();
        }).tooltip("clear").width(64);
        buttons.button(Icon.copy,()->{
            Core.app.setClipboardText(Base64.getEncoder().encodeToString(builder.exportCompressed()));
        }).tooltip("copy").width(64);
        buttons.button(Icon.paste,()->{
            ModularConstructBuilder test = new ModularConstructBuilder(3,3);
            test.set(Base64.getDecoder().decode(Core.app.getClipboardText().trim()));
            builder.clear();
            builder.paste(test);
        }).tooltip("paste").width(64);
        buttons.button("@back", Icon.left, this::hide).name("back");

        ///
        selectSide = new Table();


        Table editorSide = new Table();
        editorSide.add(editorElement).grow().name("editor");

        editorSide.row();

        editorSide.add(buttons).growX().name("canvas");

        add(selectSide).align(Align.top).growY();
        add(editorSide);

        hidden(() -> consumer.get(builder.export()));
    }

    public void show(byte[] data, Cons<byte[]> modified,Cons2<ModularConstructBuilder, Table> viewer){
        this.builder.set(data);
        leftSideBuilder.get(selectSide);
        editorElement.setBuilder(this.builder);
        this.consumer = modified::get;
        this.infoViewer = viewer;
        show();

        //todo: temp
        avalibleParts.clear();
        for(var part: ModularPartType.partMap){
            if(!avalibleParts.containsKey(part.value.category)){
                avalibleParts.put(part.value.category, new Seq<>());
            }
            avalibleParts.get(part.value.category).add(part.value);
        }
    }


    public static Cons2<ModularConstructBuilder, Table> unitInfoViewer = (construct,table)->{
        table.clearChildren();
        var statmap = new ModularUnitStatMap();
        var itemcost = construct.itemRequirements();
        ModularConstructBuilder.getStats(construct.parts, statmap);
        table.top();
        table.add(Core.bundle.get("ui.parts.info")).growX().left().color(Pal.gray);

        /// cost
        table.row();
        table.add("[lightgray]" + Stat.buildCost.localized() + ":[] ").left().top();
        table.row();
        table.table(req -> {
            req.top().left();
            for(ItemStack stack : itemcost){
                req.add(new ItemDisplay(stack.item, stack.amount, false)).padRight(5);
            }
        }).growX().left().margin(3);
        table.row();
        table.add("[lightgray]" + Stat.health.localized() + ":[accent] "+ statmap.getValue("health")).left().top();
        table.row();
        float eff = statmap.getValue("power")/statmap.getValue("powerusage");
        String color = "[green]";
        if(eff<0.7){
            color = "[red]";
        }else if(eff<1){
            color = "[yellow]";
        }


        table.add("[lightgray]" + Stat.powerUse.localized() + ": "+color+statmap.getValue("powerusage") + "/"+ statmap.getValue("power")).left().top();
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.efficiency") + ": "+color+Strings.fixed(Mathf.clamp(eff)*100,1)+"%").left().top();
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.weight") + ":[accent] "+ statmap.getValue("mass")).left().top();

        float mass = statmap.getValue("mass");
        float wcap = statmap.getValue("wheel","weight capacity");
        float speed = eff *  Mathf.clamp(wcap/mass,0,1) * statmap.getValue("wheel","nominal speed");
        table.row();
        table.add("[lightgray]" + Stat.speed.localized()  + ":[accent] "+ Core.bundle.format("ui.parts.stat.speed",Strings.fixed(speed * 60f / tilesize,1))).left().top();
    };



}
