package unity.editor;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.io.*;
import mindustry.ui.*;
import unity.cinematic.*;
import unity.type.sector.*;
import unity.type.sector.objectives.*;
import unity.util.*;

import static mindustry.Vars.*;

/**
 * An editor listener to setup {@link BlockStoryNode}s.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class CinematicEditor extends EditorListener{
    public final Seq<BlockStoryNode> nodes = new Seq<>();
    public Building selected;

    public Table container;
    private TextField name;

    private Table tagsPane;
    private final Seq<Table> tags = new Seq<>();
    private Table objectivesPane;
    private final OrderedMap<SectorObjectiveModel, Table> objectives = new OrderedMap<>();

    private final Seq<Class<? extends SectorObjective>> registered = new Seq<>();

    private boolean lock;

    public CinematicEditor(){
        JsonIO.json.addClassTag(BlockStoryNode.class.getName(), BlockStoryNode.class);

        registered.add(ResourceAmountObjective.class);
    }

    @Override
    protected void registerEvents(){
        super.registerEvents();

        container = new Table(Styles.black5);
        container.setClip(true);
        container.setTransform(true);

        var content = container.pane(table -> {
            table.setClip(true);
            table.setTransform(true);

            table.table(t -> {
                t.add("Name:").fill();
                name = t.field("", s -> current(node -> node.name = s)).fillY().growX().get();
            }).align(Align.topLeft).fillY().growX().pad(6f);

            table.row().table(Styles.black3, tag -> {
                tag.add("Tags").pad(4f);
                tag.row().image(Tex.whiteui, Pal.accent)
                    .height(4f)
                    .growX()
                    .pad(4f);

                var scroll = tag.row().pane(Styles.defaultPane, t -> {
                    tagsPane = t.table(Styles.black3).fill().get().top();
                    tagsPane.defaults().pad(6f);
                }).update(p -> {
                    if(p.hasScroll()){
                        Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                        if(result == null || !result.isDescendantOf(p)){
                            Core.scene.setScrollFocus(null);
                        }
                    }
                }).top().maxHeight(300f).grow().pad(6f).get();
                scroll.setScrollingDisabled(true, false);
                scroll.setOverscroll(false, false);

                tag.row()
                    .button(Icon.add, Styles.defaulti, () -> addTag(null, null))
                    .align(Align.left).pad(6f).size(40f);
            }).fillY().growX().pad(6f);

            table.row().table(Styles.black3, model -> {
                model.add("Objectives").pad(4f);
                model.row().image(Tex.whiteui, Pal.accent)
                    .height(4f)
                    .growX()
                    .pad(4f);

                var scroll = model.row().pane(Styles.defaultPane, t -> {
                    objectivesPane = t.table(Styles.black3).fill().get().top();
                    objectivesPane.defaults().pad(6f);
                }).update(p -> {
                    if(p.hasScroll()){
                        Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                        if(result == null || !result.isDescendantOf(p)){
                            Core.scene.setScrollFocus(null);
                        }
                    }
                }).top().maxHeight(600f).grow().pad(6f).get();
                scroll.setScrollingDisabled(true, false);
                scroll.setOverscroll(false, false);

                model.row()
                    .button(Icon.add, Styles.defaulti, () -> addObjective(new SectorObjectiveModel()))
                    .align(Align.left).pad(6f).size(40f);
            }).fillY().growX().pad(6f);

            table.row().button(Icon.right, Styles.defaulti, this::hide)
                .align(Align.bottomRight).pad(6f).size(40f);
        }).update(p -> {
            if(p.hasScroll()){
                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                if(result == null || !result.isDescendantOf(p)){
                    Core.scene.setScrollFocus(null);
                }
            }
        }).size(600f, 400f).get();
        content.setScrollingDisabled(true, false);
        content.setOverscroll(false, false);

        ui.hudGroup.addChild(container);
    }

    @Override
    public void begin(){
        container.setScale(0f, 1f);
        container.visible = false;

        try{
            nodes.addAll(JsonIO.json.fromJson(Seq.class, editor.tags.get("storyNodes", "[]")));
        }catch(Throwable t){
            ui.showException("Could not read existing story nodes", t);
        }
    }

    @Override
    public void end(){
        try{
            editor.tags.put("storyNodes", JsonIO.json.toJson(nodes, Seq.class));
        }catch(Throwable t){
            ui.showException("Could not write story nodes", t);
        }
        nodes.clear();
    }

    @Override
    public void update(){
        if(Core.input.keyDown(Binding.control) && Core.input.keyTap(KeyCode.altLeft)){
            var pos = Core.input.mouseWorld();
            var build = world.buildWorld(pos.x, pos.y);

            manualSave();
            selected = build;
            if(selected != null){
                Sounds.click.play();
                
                show();
                updateTable();
            }else{
                hide();
            }
        }

        if(selected != null) updateTablePosition();

        nodes.removeAll(node -> node.bound == null || world.build(node.bound.pos()) == null);
    }

    @Override
    public void draw(){
        Draw.draw(Layer.blockOver, () -> {
            for(var node : nodes){
                var b = node.bound;

                Draw.color(b == selected ? Color.cyan : Color.blue);
                Lines.stroke(1f);
                Lines.square(b.x, b.y, (float)(b.block.size * tilesize) / 2f + 1f);
            }

            Draw.reset();
        });
    }

    protected void updateTablePosition(){
        if(!container.visible || selected == null) return;

        var v = Core.input.mouseScreen(selected.x - selected.block.size * tilesize / 2f, selected.y + selected.block.size * tilesize / 2f);
        container.pack();
        container.setPosition(v.x, v.y, Align.topRight);
        container.setOrigin(Align.topRight);
    }

    protected void manualSave(){
        if(selected == null) return;

        var node = current();
        if(node == null) return;

        node.tags.clear();
        for(var table : tags){
            var key = table.<TextField>find("key").getText();
            var value = table.<TextField>find("value").getText();

            if(key.isEmpty()) continue;

            node.tags.put(key, value);
        }

        node.objectives.clear();
        node.objectives.addAll(objectives.keys());
    }

    protected void updateTable(){
        if(selected == null) return;

        var node = current();
        if(node == null){
            node = new BlockStoryNode();
            node.bound = selected;

            nodes.add(node);
        }

        name.setText(node.name);

        tags.each(e -> {
            Table parent = (Table)e.parent;

            var cell = parent.getCell(e);
            if(cell != null){
                e.remove();

                parent.getCells().remove(cell);
                parent.invalidateHierarchy();
            }
        });
        tags.clear();
        for(var entry : node.tags.entries()){
            addTag(entry.key, entry.value);
        }

        objectives.each((key, value) -> {
            Table parent = (Table)value.parent;

            var cell = parent.getCell(value);
            if(cell != null){
                value.remove();

                parent.getCells().remove(cell);
                parent.invalidateHierarchy();
            }
        });
        objectives.clear();
        for(var objective : node.objectives){
            addObjective(objective);
        }
    }

    protected void addTag(String key, String value){
        var cont = new Table();
        cont.defaults().pad(4f);

        cont.button(Icon.trash, Styles.defaulti, () -> {
            tags.remove(cont);

            var cell = tagsPane.getCell(cont);
            if(cell != null){
                cont.remove();
                tagsPane.getCells().remove(cell);
                tagsPane.invalidateHierarchy();

                manualSave();
            }
        }).size(40f);

        cont.add("Key:");
        cont.field(key, t -> manualSave())
            .name("key").fill();

        cont.add("Value:");
        cont.field(value, t -> manualSave())
            .name("value").fillY().growX();

        tagsPane.row().add(cont).fillY().growX();
        tags.add(cont);
    }

    protected void addObjective(SectorObjectiveModel model){
        var cont = objectives.get(model, Table::new);
        cont.clear();
        cont.defaults().pad(4f);

        cont.button(Icon.trash, Styles.defaulti, () -> {
            objectives.remove(model);

            var cell = objectivesPane.getCell(cont);
            if(cell != null){
                cont.remove();
                objectivesPane.getCells().remove(cell);
                objectivesPane.invalidateHierarchy();

                manualSave();
            }
        }).size(40f).name("trash");
        cont.add("Type:").fill().name("type name");
        cont.field("", type -> {
            var t = registered.find(c -> c.getSimpleName().equals(type));
            if(t != null){
                model.set(t);
                updateFields(model);

                manualSave();
            }
        }).fillY().growX().name("type field");

        cont.row().table(Styles.black3, t -> {}).grow().name("fields");

        objectivesPane.row().add(cont).fillY().growX();
        objectives.put(model, cont);
    }

    protected void updateFields(SectorObjectiveModel model){
        var cont = objectives.get(model);
        if(cont != null){
            cont = cont.find("fields");
            cont.clear();

            for(var entry : model.fields.entries()){
                cont.add(entry.key);
                var meta = entry.value;
                var type = meta.field.getType();

                if((type.isArray() || Seq.class.isAssignableFrom(type)) && simple(meta.elementType)){
                    cont.row().table(Styles.black3, t -> {
                        Table[] table = {null};
                        Seq<String> values = (Seq<String>)model.setFields.get(entry.key, Seq::new);
                        Seq<Table> items = new Seq<>();

                        t.pane(c -> {
                            table[0] = t.table(Styles.black3).fill().get().top();
                            table[0].defaults().pad(6f);
                        }).update(p -> {
                            if(p.hasScroll()){
                                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                                if(result == null || !result.isDescendantOf(p)){
                                    Core.scene.setScrollFocus(null);
                                }
                            }
                        }).top().maxHeight(100f).grow().pad(4f).get();

                        Cons<String> add = element -> items.add(table[0].row().table(f -> {
                            f.button(Icon.trash, () -> {
                                var cell = table[0].getCell(f);
                                if(cell != null){
                                    values.remove(items.indexOf(f));
                                    items.remove(f);

                                    f.remove();
                                    table[0].getCells().remove(cell);
                                    table[0].invalidateHierarchy();

                                    manualSave();
                                }
                            }).size(40f).align(Align.left);

                            f.label(() -> String.valueOf(items.indexOf(f)));
                            f.field(element, str -> values.set(items.indexOf(f), str)).fillY().growX();
                        }).fillY().growX().get());
                        values.each(add);

                        t.row().button(Icon.add, Styles.defaulti, () -> {
                            values.add("");
                            add.get("");
                        }).align(Align.left).size(40f).pad(6f);
                    }).fillY().growX().pad(6f);
                }else if(ObjectMap.class.isAssignableFrom(type) && simple(meta.keyType) && simple(meta.elementType)){

                }else if(simple(type)){
                    cont.field((String)model.setFields.get(entry.key, () -> ""), str -> model.setFields.put(entry.key, str));
                }
                cont.row();
            }
        }
    }

    protected boolean simple(Class<?> type){
        return type != null && Reflect.isWrapper(ReflectUtils.box(type));
    }

    protected void show(){
        if(container.visible || lock) return;

        lock = true;

        container.visible = true;
        container.setScale(0f, 1f);
        container.actions(
            Actions.scaleTo(1f, 1f, 0.12f, Interp.pow3Out),
            Actions.run(() -> lock = false)
        );
    }

    protected void hide(){
        if(!container.visible || lock) return;

        lock = true;
        container.actions(
            Actions.scaleTo(0f, 1f, 0.12f, Interp.pow3Out),
            Actions.run(() -> lock = false),
            Actions.visible(false)
        );
    }

    public BlockStoryNode current(){
        return nodes.find(n -> n.bound == selected);
    }

    public void current(Cons<BlockStoryNode> cons){
        var node = current();
        if(node != null) cons.get(node);
    }
}
