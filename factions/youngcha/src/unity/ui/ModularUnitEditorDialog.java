package unity.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.meta.*;
import unity.parts.*;

import java.util.*;

import static mindustry.Vars.*;

public class ModularUnitEditorDialog extends BaseDialog{
    public ModularUnitBlueprint blueprint;
    Runnable onHidden;
    public ModularUnitEditorElement editorElement;
    public Cons2<ModularUnitBlueprint, Table> infoViewer;

    public ObjectMap<String, Seq<ModularUnitPartType>> availableParts = new ObjectMap<>();
    Table selectSide;

    //part select
    void buildPartSelect(Table table){
        table.clearChildren();
        table.top();
        table.add(Core.bundle.get("ui.parts.select")).growX().left().color(Pal.gray);

        for(var category : availableParts){
            ///Title!
            table.row();
            table.add(Core.bundle.get("ui.parts.category." + category.key)).growX().left().color(Pal.accent);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();
            ///Table of the parts in the category
            table.table(list -> {
                int i = 0;
                for(var part : category.value){
                    if(i != 0 && i % 5 == 0){
                        list.row(); //row size i 5
                    }
                    list.left();
                    ImageButton partsButton = list.button(new TextureRegionDrawable(part.icon), Styles.selecti, () -> {
                        if(editorElement.selected == part){
                            editorElement.deselect();
                        }else{
                            editorElement.select(part);
                        }
                    }).pad(3).size(46f).name("part-" + part.name)
                    .tooltip(part::displayTooltip).get();
                    partsButton.resizeImage(iconMed);

                    // unselect if another one got selected
                    partsButton.update(() -> {
                        partsButton.setChecked(editorElement.selected == part);
                        //possibly set gray if disallowed.
                        if(blueprint.root == null && !part.root || blueprint.root != null && part.root){
                            partsButton.forEach(elem -> elem.setColor(Color.darkGray));
                        }else{
                            partsButton.forEach(elem -> elem.setColor(Color.white));
                        }
                    });
                    i++;
                }
            }).growX().left().padBottom(10);
        }
    }

    //part select & info container
    void buildLeftSide(Table table){
        table.clearChildren();
        //Middle content
        Table content = new Table();
        //top side, tabs
        Table tabs = new Table();
        ImageButton partsSelectMenuButton = new ImageButton(Icon.box, Styles.clearNoneTogglei);
        partsSelectMenuButton.clicked(() -> {
            buildPartSelect(content);
            blueprint.setOnChange(() -> {});
        });

        ImageButton infoMenuButton = new ImageButton(Icon.info, Styles.clearNoneTogglei);
        infoMenuButton.clicked(() -> {
            infoViewer.get(blueprint, content);
            blueprint.setOnChange(() -> infoViewer.get(blueprint, content));
        });
        var tabsButtonGroup = new ButtonGroup<ImageButton>();
        tabsButtonGroup.add(partsSelectMenuButton, infoMenuButton);
        tabs.add(partsSelectMenuButton).size(64);
        tabs.add(infoMenuButton).size(64);

        //middle
        ScrollPane scrollPane = new ScrollPane(content, Styles.defaultPane);

        //bottom info
        Table stats = new Table();
        stats.update(() -> {
            stats.clear();
            stats.top().left().margin(5);
            if(editorElement.selected != null){
                editorElement.selected.display(stats);
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

        partsSelectMenuButton.fireClick();
    }

    public ModularUnitEditorDialog(){
        super("parts");
        editorElement = new ModularUnitEditorElement();
        clearChildren();
        buttons.defaults().size(160f, 64f);
        buttons.button(Icon.flipX, Styles.clearTogglei, () -> editorElement.mirror = !editorElement.mirror)
        .update(i -> i.setChecked(editorElement.mirror)).tooltip("mirror").width(64);

        buttons.button(Icon.file, () -> {
            blueprint.clear();
            editorElement.onAction();
        }).tooltip("clear").width(64);
        buttons.button(Icon.copy, () -> Core.app.setClipboardText(Base64.getEncoder().encodeToString(blueprint.encodeCropped())))
        .tooltip("copy").width(64);

        buttons.button(Icon.paste, () -> {
            try{
                blueprint.decode(Base64.getDecoder().decode(Core.app.getClipboardText().trim().replaceAll("[\\t\\n\\r]+", "")));
                editorElement.onAction();
            }catch(Exception e){
                Vars.ui.showOkText("Uh", "Your code is poopoo", () -> {}); ///?????
            }
        }).tooltip("paste").width(64);
        if(Core.graphics.getWidth() < 750){
            buttons.row();
            buttons.table(row2 -> {
                buttons.button("@undo", Icon.undo, () -> editorElement.undo()).name("undo").width(64);
                buttons.button("@redo", Icon.redo, () -> editorElement.redo()).name("redo").width(64);
                buttons.button("@back", Icon.left, this::hide).name("back");
            }).left();
        }else{
            buttons.button("@back", Icon.left, this::hide).name("back");
        }

        ///
        selectSide = new Table();


        Table editorSide = new Table();
        editorSide.add(editorElement).grow().name("editor");

        editorSide.row();

        editorSide.add(buttons).growX().name("canvas");

        add(selectSide).align(Align.top).growY();
        add(editorSide);

        hidden(() -> onHidden.run());

        //input
        update(() -> {
            if(Core.scene != null && Core.scene.getKeyboardFocus() == this){
                if(Core.input.ctrl()){
                    if(Core.input.keyTap(KeyCode.z)){
                        editorElement.undo();
                    }else if(Core.input.keyTap(KeyCode.y)){
                        editorElement.redo();
                    }
                }
            }
        });
    }

    public void show(ModularUnitBlueprint blueprint, Runnable modified, Cons2<ModularUnitBlueprint, Table> viewer, Boolf<ModularUnitPartType> allowed){
        //todo: temp
        availableParts.clear();
        for(var part : ModularUnitPartType.partMap){
            if(!allowed.get(part.value)) continue;

            if(!availableParts.containsKey(part.value.category)){
                availableParts.put(part.value.category, new Seq<>());
            }
            availableParts.get(part.value.category).add(part.value);
        }

        show();
        this.blueprint = blueprint;
        buildLeftSide(selectSide);
        editorElement.setBlueprint(blueprint);
        this.onHidden = modified;
        this.infoViewer = viewer;
    }


    public static Cons2<ModularUnitBlueprint, Table> unitInfoViewer = (blueprint, table) -> {
        table.clearChildren();
        var statMap = new ModularUnitStatMap();
        var itemCosts = blueprint.itemRequirements();
        statMap.getStats(blueprint.partsList.orderedItems());
        table.top();
        table.add(Core.bundle.get("ui.parts.info")).growX().left().color(Pal.gray);

        /// cost
        table.row();
        table.add("[lightgray]" + Stat.buildCost.localized() + ":[] ").left().top();
        table.row();
        table.table(req -> {
            req.top().left();
            for(ItemStack stack : itemCosts){
                req.add(new ItemDisplay(stack.item, stack.amount, false)).padRight(5);
            }
        }).growX().left().margin(3);
        table.row();
        table.add("[lightgray]" + Stat.health.localized() + ":[accent] " + statMap.getValue("health")).left().top();
        table.row();
        float eff = Mathf.clamp(statMap.getValue("power") / statMap.getValue("powerusage"));
        String color = "[green]";
        if(eff < 0.7){
            color = "[red]";
        }else if(eff < 1){
            color = "[yellow]";
        }

        table.add("[lightgray]" + Stat.powerUse.localized() + ": " + color + statMap.getValue("powerusage") + "/" + statMap.getValue("power")).left().top();
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.efficiency") + ": " + color + Strings.fixed(Mathf.clamp(eff) * 100, 1) + "%").left().top();
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.weight") + ":[accent] " + statMap.getValue("mass")).left().top();

        float mass = statMap.getValue("mass");
        float wcap = statMap.getValue("wheel", "weight capacity");
        float speed = eff * Mathf.clamp(wcap / mass) * statMap.getValue("wheel", "nominal statSpeed");
        table.row();
        table.add("[lightgray]" + Stat.speed.localized() + ":[accent] " + Core.bundle.format("ui.parts.stat.speed", Strings.fixed(speed * 60f / tilesize, 1))).left().top();
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.armour-points") + ":[accent] " + statMap.getValue("armour")).left().top();
        table.row();
        table.add("[lightgray]" + Stat.armor.localized() + ":[accent] " + Strings.fixed(statMap.getValue("armour", "realValue"), 1)).left().top();

        table.row();
        int weaponslots = Math.round(statMap.getValue("weaponslots"));
        int weaponslotsused = Math.round(statMap.getValue("weaponslotuse"));
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.weapon-slots") + ": " + (weaponslotsused > weaponslots ? "[red]" : "[green]") + weaponslotsused + "/" + weaponslots).left().top().tooltip(Core.bundle.get("ui.parts.stat.weapon-slots-tooltip"));

        table.row();
        int abilityslots = Math.round(statMap.getValue("abilityslots"));
        int abilityslotsused = Math.round(statMap.getValue("abilityslotuse"));
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stat.ability-slots") + ": " + (abilityslotsused > abilityslots ? "[red]" : "[green]") + abilityslotsused + "/" + abilityslots).left().top().tooltip(Core.bundle.get("ui.parts.stat.ability-slots-tooltip"));
    };

    public void updateScrollFocus(){
        boolean[] done = {false};

        Core.app.post(() -> forEach(child -> {
            if(done[0]) return;

            if(child instanceof ScrollPane || child instanceof ModularUnitEditorElement){
                Core.scene.setScrollFocus(child);
                done[0] = true;
            }
        }));
    }

}
