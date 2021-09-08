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
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.cinematic.*;
import unity.type.sector.*;
import unity.type.sector.objectives.*;
import unity.ui.*;
import unity.util.*;
import unity.util.func.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * An editor listener to setup tile-based story nodes in maps.
 * @author GlennFolker
 */
//TODO so many repetitions. might have to be reworked some day, preferably make interpreters return Cell<?> instead of whatever its doing
@SuppressWarnings("unchecked")
public class CinematicEditor extends EditorListener{
    public final Seq<BuildStoryNode> nodes = new Seq<>();
    public Building selected;

    public Table container;
    private TextField name;

    private Table parentsPane;
    private final Seq<Table> parents = new Seq<>();
    private Table tagsPane;
    private final Seq<Table> tags = new Seq<>();
    private Table objectivesPane;
    private final OrderedMap<SectorObjectiveModel, Table> objectives = new OrderedMap<>();

    private final ObjectMap<Class<?>, Cons3<Table, Prov<String>, Cons<String>>> interpreters = new ObjectMap<>();
    private final Seq<Class<? extends SectorObjective>> registered = new Seq<>();
    private final ObjectMap<Class<? extends SectorObjective>, Seq<Field>> ignored = new ObjectMap<>();

    private boolean lock;

    public CinematicEditor(){
        JsonIO.json.addClassTag(BuildStoryNode.class.getName(), BuildStoryNode.class);

        registered.add(ResourceAmountObjective.class);

        Cons3<Table, Prov<String>, Cons<String>> def = (cont, v, str) -> cont.field(v.get(), str).fillY().growX().pad(4f).get().setAlignment(Align.left);
        Cons3<Table, Prov<String>, Cons<String>> defInt = (cont, v, str) -> {
            var s = v.get();
            var text = cont.field("", str).fillY().growX().pad(4f).get();
            text.setFilter(TextFieldFilter.digitsOnly);
            text.setAlignment(Align.left);

            text.setText(s == null || s.isEmpty() ? "0" : s);
        };

        Cons3<Table, Prov<String>, Cons<String>> defFloat = (cont, v, str) -> {
            var s = v.get();
            var text = cont.field("", str).fillY().growX().pad(4f).get();
            text.setFilter(TextFieldFilter.floatsOnly);
            text.setAlignment(Align.left);

            text.setText(s == null || s.isEmpty() ? "0.0" : s);
        };

        interpreters.put(Byte.class, defInt);
        interpreters.put(Short.class, defInt);
        interpreters.put(Integer.class, defInt);
        interpreters.put(Long.class, defInt);
        interpreters.put(Float.class, defFloat);
        interpreters.put(Double.class, defFloat);
        interpreters.put(Character.class, def);
        interpreters.put(String.class, def);
        interpreters.put(Boolean.class, (cont, v, str) -> {
            var c = cont.check("", b -> str.get(String.valueOf(b))).left().size(40f).pad(4f).get();
            str.get(String.valueOf(c.isChecked()));
        });

        interpreters.put(Team.class, defInt);

        interpreters.put(ItemStack.class, (cont, v, str) -> cont.table(t -> {
            var s = v.get();

            String[] pair = {"copper", "0"};
            if(s != null && !s.isEmpty()){
                int separator = s.indexOf("\n");
                if(separator != -1){
                    pair[0] = s.substring(0, separator);
                    pair[1] = s.substring(separator + 1);
                }
            }

            Runnable accept = () -> str.get(pair[0] + "\n" + pair[1]);

            t.add("Item:").fillY().width(80f);
            t.field(pair[0], val -> {
                pair[0] = val;
                accept.run();
            }).fillY().growX();

            t.add("Amount:").fillY().width(80f);
            t.field(pair[1], val -> {
                pair[1] = val;
                accept.run();
            }).fillY().growX().get().setFilter(TextFieldFilter.digitsOnly);

            accept.run();
        }).fillY().growX().pad(4f));

        interpreters.put(SectorObjective.class, (cont, v, str) -> cont.table(t -> {
            var s = v.get();

            String[] pair = {"", ""};
            if(s != null && !s.isEmpty()){
                int separator = s.indexOf("\n");
                if(separator != -1){
                    pair[0] = s.substring(0, separator);
                    pair[1] = s.substring(separator + 1);
                }
            }

            Runnable accept = () -> str.get(pair[0] + "\n" + pair[1]);

            t.add("Node:").fillY().width(80f);
            t.field(pair[0], val -> {
                pair[0] = val;
                accept.run();
            }).fillY().growX();

            t.add("Name:").fillY().width(80f);
            t.field(pair[1], val -> {
                pair[1] = val;
                accept.run();
            }).fillY().growX();

            accept.run();
        }).fillY().growX().pad(4f));

        interpreters.put(Color.class, (cont, v, str) -> cont.button(Icon.pencil, () -> {
            var s = v.get();
            ui.picker.show(
                Color.valueOf(Tmp.c1, s == null || s.isEmpty() ? "ffffffff" : s),
                false,
                res -> str.get(res.toString())
            );

            str.get(s);
        }).size(40f).pad(4f));

        interpreters.put(Runnable.class, funcInterface(Runnable.class));
        interpreters.put(Cons.class, funcInterface(Cons.class));
        interpreters.put(Cons2.class, funcInterface(Cons2.class));
        interpreters.put(Func.class, funcInterface(Func.class));
        interpreters.put(Func2.class, funcInterface(Func2.class));
        interpreters.put(Func3.class, funcInterface(Func3.class));
    }

    protected Cons3<Table, Prov<String>, Cons<String>> funcInterface(Class<?> type){
        if(!type.isInterface() || type.getMethods().length != 1) throw new IllegalArgumentException("Not a functional interface: '" + type + "'");

        Method method = null;
        for(var m : type.getMethods()){
            if(Modifier.isAbstract(m.getModifiers()) || !m.isDefault()){
                if(method == null){
                    method = m;
                }else{
                    throw new IllegalArgumentException("Not a functional interface: '" + type + "'");
                }
            }
        }

        if(method == null) throw new IllegalArgumentException("Not a functional interface: '" + type + "'");

        var ret = method.getReturnType();
        var args = method.getParameters();

        var exec = new StringBuilder("/**\n")
            .append(" * Function type - ").append(type.getSimpleName()).append("\n");
        for(var arg : args){
            exec.append(" * ")
                .append(arg.getName())
                .append(" - Argument passed by ")
                .append(type.getSimpleName())
                .append("\n");
        }
        exec.append(" */\n")
            .append("function(");
        for(int i = 0; i < args.length; i++){
            if(i > 0) exec.append(", ");
            exec.append(args[i].getName());
        }
        exec.append("){\n")
            .append("    ")
            .append(ret == void.class ? "" : "return " + ReflectUtils.def(ret) + ";").append("\n")
            .append("}");

        var def = exec.toString();

        return (cont, v, str) -> {
            cont.button(Icon.pencil, () -> {
                var s = v.get();

                scriptsDialog.listener = str;
                scriptsDialog.area.setText((s == null || s.isEmpty()) ? def : s);
                scriptsDialog.show();
            }).left().size(40f).pad(4f);

            var out = v.get();
            str.get((out == null || out.isEmpty()) ? def : out);
        };
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

            table.table(Styles.black3, t -> {
                t.add("Metadata").pad(4f);
                t.row().image(Tex.whiteui, Pal.accent)
                    .height(4f).growX().pad(4f);

                t.row().table(cont -> {
                    cont.add("Name:").fill();
                    name = cont.field("", s -> current(node -> node.name = s)).fillY().growX().get();
                }).fillY().growX().pad(4f);

                t.row().table(cont -> {
                    cont.add("Parents").fillY().growX().pad(4f)
                        .get().setAlignment(Align.left);
                    cont.row().image(Tex.whiteui, Pal.accent)
                        .height(4f).growX().pad(4f);

                    var scroll = cont.row().pane(Styles.defaultPane, tt -> {
                        parentsPane = tt.table(Styles.black3).fillY().growX().get().top();
                        parentsPane.defaults().pad(6f);
                    }).update(p -> {
                        if(p.hasScroll()){
                            Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                            if(result == null || !result.isDescendantOf(p)){
                                Core.scene.setScrollFocus(null);
                            }
                        }
                    }).maxHeight(100f).grow().pad(6f).get();
                    scroll.setScrollingDisabled(true, false);
                    scroll.setOverscroll(false, false);

                    cont.row()
                        .button(Icon.add, Styles.defaulti, () -> addParent(null))
                        .left().pad(6f).size(40f);
                }).fillY().growX().pad(4f);
            }).top().fillY().growX().pad(6f);

            table.row().table(Styles.black3, tag -> {
                tag.add("Tags").pad(4f);
                tag.row().image(Tex.whiteui, Pal.accent)
                    .height(4f).growX().pad(4f);

                var scroll = tag.row().pane(Styles.defaultPane, t -> {
                    tagsPane = t.table(Styles.black3).fillY().growX().get().top();
                    tagsPane.defaults().pad(6f);
                }).update(p -> {
                    if(p.hasScroll()){
                        Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                        if(result == null || !result.isDescendantOf(p)){
                            Core.scene.setScrollFocus(null);
                        }
                    }
                }).maxHeight(300f).grow().pad(6f).get();
                scroll.setScrollingDisabled(true, false);
                scroll.setOverscroll(false, false);

                tag.row()
                    .button(Icon.add, Styles.defaulti, () -> addTag(null, null))
                    .left().pad(6f).size(40f);
            }).fillY().growX().pad(6f);

            table.row().table(Styles.black3, model -> {
                model.add("Objectives").pad(4f);
                model.row().image(Tex.whiteui, Pal.accent)
                    .height(4f).growX().pad(4f);

                var scroll = model.row().pane(Styles.defaultPane, t -> {
                    objectivesPane = t.table(Styles.black3).fillY().growX().get().top();
                    objectivesPane.defaults().pad(4f);
                }).update(p -> {
                    if(p.hasScroll()){
                        Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                        if(result == null || !result.isDescendantOf(p)){
                            Core.scene.setScrollFocus(null);
                        }
                    }
                }).maxHeight(800f).grow().pad(6f).get();
                scroll.setScrollingDisabled(true, false);
                scroll.setOverscroll(false, false);

                model.row()
                    .button(Icon.add, Styles.defaulti, () -> addObjective(new SectorObjectiveModel()))
                    .left().pad(6f).size(40f);
            }).fillY().growX().pad(6f);

            table.row().button(Icon.right, Styles.defaulti, this::hide)
                .bottom().right().pad(6f).size(40f);
        }).update(p -> {
            if(p.hasScroll()){
                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                if(result == null || !result.isDescendantOf(p)){
                    Core.scene.setScrollFocus(null);
                }
            }
        }).size(800f, 500f).get();
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
        if(selected != null) selected = world.build(selected.pos());

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
                Drawf.square(b.x, b.y, b.block.size * tilesize / 2f + 1f, 0f, b == selected ? Pal.accent : Pal.place);
            }

            Draw.reset();
        });
    }

    public void ignore(Class<? extends SectorObjective> type, Field field){
        var arr = ignored.get(type, Seq::new);
        if(!arr.contains(field)) arr.add(field);
    }

    protected void updateTablePosition(){
        if(!container.visible || selected == null) return;

        var v = Core.input.mouseScreen(selected.x - selected.block.size * tilesize / 2f, selected.y + selected.block.size * tilesize / 2f);
        container.pack();
        container.setPosition(v.x, v.y, Align.topRight);
        container.setOrigin(Align.topRight);
    }

    protected void manualSave(){
        current(node -> {
            node.tags.clear();
            for(var table : tags){
                var key = table.<TextField>find("key").getText();
                var value = table.<TextField>find("value").getText();

                if(key.isEmpty()) continue;

                node.tags.put(key, value);
            }

            node.objectiveModels.clear();
            node.objectiveModels.addAll(objectives.keys());

            node.parentStrings.clear();
            for(var parent : parents){
                node.parentStrings.add(parent.<TextField>find("node").getText());
            }
        });
    }

    protected void updateTable(){
        if(selected == null) return;

        var node = current();
        if(node == null){
            node = new BuildStoryNode();
            node.sector = sector();
            node.bound = selected;

            nodes.add(node);
        }

        name.setText(node.name);

        parents.each(e -> {
            Table parent = (Table)e.parent;

            var cell = parent.getCell(e);
            if(cell != null){
                e.remove();

                parent.getCells().remove(cell);
                parent.invalidateHierarchy();
            }
        });
        parents.clear();
        for(var parent : node.parentStrings){
            addParent(parent);
        }

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
        for(var objective : node.objectiveModels){
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

    protected void addParent(String parent){
        var cont = new Table();
        cont.defaults().pad(4f);

        cont.button(Icon.trash, Styles.defaulti, () -> {
            parents.remove(cont);

            var cell = parentsPane.getCell(cont);
            if(cell != null){
                cont.remove();
                parentsPane.getCells().remove(cell);
                parentsPane.invalidateHierarchy();

                manualSave();
            }
        }).size(40f);

        cont.add("Node:");
        cont.field(parent, t -> manualSave())
            .name("node").fillY().growX();

        parentsPane.row().add(cont).fillY().growX();
        parents.add(cont);
    }

    protected void addObjective(SectorObjectiveModel model){
        var cont = objectives.get(model, Table::new);
        cont.clear();
        cont.defaults().pad(4f);

        cont.table(t -> {
            t.button(Icon.trash, Styles.defaulti, () -> {
                objectives.remove(model);

                var cell = objectivesPane.getCell(cont);
                if(cell != null){
                    cont.remove();
                    objectivesPane.getCells().remove(cell);
                    objectivesPane.invalidateHierarchy();

                    manualSave();
                }
            }).size(40f).name("trash");

            t.add("Type:").fill().name("type name");
            t.field(model.type == null ? "" : model.type.getSimpleName(), type -> {
                var c = registered.find(cl -> cl.getSimpleName().equals(type));
                if(c != null){
                    model.set(c);
                    updateFields(model);

                    manualSave();
                }
            }).fillY().growX().name("type field");
        }).fillY().growX();

        cont.row().table(Styles.black3, t -> {}).grow().name("fields");

        objectivesPane.row().add(cont).fillY().growX();
        objectives.put(model, cont);

        updateFields(model);
    }

    protected void updateFields(SectorObjectiveModel model){
        var fieldCont = objectives.get(model);
        if(fieldCont != null){
            fieldCont = fieldCont.find("fields");
            fieldCont.clear();

            for(var entry : model.fields.entries()){
                if(ignored.get(model.type, Seq::new).contains(entry.value.field::equals) || !Modifier.isPublic(entry.value.field.getModifiers())) continue;

                var fieldName = entry.key;
                var meta = entry.value;
                var type = ReflectUtils.box(meta.field.getType());
                var elem = ReflectUtils.box(meta.elementType);
                var key = ReflectUtils.box(meta.keyType);

                boolean handled = false;

                if((type.isArray() && can(ReflectUtils.box(type.getComponentType()))) || (Seq.class.isAssignableFrom(type) && can(elem))){
                    var fType = type.isArray() ? ReflectUtils.box(type.getComponentType()) : elem;

                    handled = true;
                    fieldCont.table(cont -> {
                        var field = cont.add(fieldName).fillY().growX().pad(4f).get();
                        field.setAlignment(Align.left);
                        field.setStyle(UnityStyles.codeLabel);

                        cont.row().image(Tex.whiteui, Pal.accent)
                            .height(4f).growX().pad(4f);

                        cont.row().table(Styles.black3, t -> {
                            Table[] table = {null};
                            Seq<String> values = (Seq<String>)model.setFields.get(fieldName, Seq::new);
                            Seq<Table> items = new Seq<>();

                            t.pane(c -> {
                                table[0] = t.table(Styles.black3).fill().get().top();
                                table[0].defaults().pad(4f);
                            }).update(p -> {
                                if(p.hasScroll()){
                                    Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                                    if(result == null || !result.isDescendantOf(p)){
                                        Core.scene.setScrollFocus(null);
                                    }
                                }
                            }).maxHeight(100f).grow().pad(4f).get();

                            Cons<String> add = element -> {
                                Table[] c = {null};
                                items.add(table[0].row().table(f -> {
                                    c[0] = f;
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
                                    }).size(40f).left().pad(6f);

                                    var index = f.label(() -> String.valueOf(items.indexOf(f)))
                                        .size(30f).pad(4f)
                                        .get();
                                    index.setAlignment(Align.right);
                                    index.setStyle(UnityStyles.codeLabel);
                                }).fillY().growX().pad(4f).get());

                                interpreters.get(fType).get(c[0], () -> values.get(items.indexOf(c[0])), str -> values.set(items.indexOf(c[0]), str));
                            };
                            values.each(add);

                            t.row().button(Icon.add, Styles.defaulti, () -> {
                                values.add("");
                                add.get("");
                            }).left().size(40f).pad(6f);
                        }).fillY().growX().pad(6f);
                    }).left().fillY().growX();
                }else if(ObjectMap.class.isAssignableFrom(type) && can(key) && can(elem)){
                    handled = true;
                    //TODO handle maps
                    fieldCont.table(cont -> {}).left().fillY().growX();
                }else if(can(type)){
                    handled = true;
                    fieldCont.table(cont -> {
                        var field = cont.add(fieldName).left().fillY().width(100f).pad(4f).get();
                        field.setAlignment(Align.left);
                        field.setStyle(UnityStyles.codeLabel);

                        interpreters.get(type).get(cont, () -> (String)model.setFields.get(fieldName), str -> model.setFields.put(fieldName, str));
                    }).left().fillY().growX();
                }

                if(handled) fieldCont.row();
            }
        }
    }

    protected boolean can(Class<?> type){
        return type != null && interpreters.containsKey(type);
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

    public BuildStoryNode current(){
        return nodes.find(n -> n.bound == selected);
    }

    public void current(Cons<BuildStoryNode> cons){
        var node = current();
        if(node != null) cons.get(node);
    }
}
