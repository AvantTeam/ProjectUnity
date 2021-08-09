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

import static mindustry.Vars.*;

/**
 * An editor listener to setup {@link BlockStoryNode}s.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class CinematicEditor extends EditorListener{
    public final Seq<BlockStoryNode> nodes = new Seq<>();
    public Building selected;

    public Table cont;

    private TextField name;

    private Table pane;
    private final Seq<Table> tags = new Seq<>();

    private boolean lock;

    public CinematicEditor(){
        JsonIO.json.addClassTag(BlockStoryNode.class.getName(), BlockStoryNode.class);
    }

    @Override
    protected void registerEvents(){
        super.registerEvents();

        cont = new Table(Styles.black5);
        cont.setClip(true);
        cont.setTransform(true);

        cont.top().table(Styles.black5, table -> {
            table.setClip(true);
            table.setTransform(true);

            table.right().table(t -> {
                t.left().add("Name:").fill();
                name = t.field("", Styles.nodeField, s -> current(node -> node.name = s)).fillY().growX().get();
            }).fillY().growX().pad(6f);

            var scroll = table.row().center().pane(Styles.defaultPane, t -> {
                pane = t.top().table(Styles.black5).fill().get().top();
                pane.defaults().pad(6f);
            }).update(p -> {
                if(p.hasScroll()){
                    Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                    if(result == null || !result.isDescendantOf(p)){
                        Core.scene.setScrollFocus(null);
                    }
                }
            }).grow().pad(8f).get();
            scroll.setScrollingDisabled(true, false);
            scroll.setOverscroll(false, false);

            table.row().left().table(t -> {
                t.defaults().pad(6f);

                t.left().button(Icon.up, Styles.defaulti, this::hide).size(40f);

                t.right().button(Icon.trash, Styles.defaulti, () -> current(node -> {
                    nodes.remove(node);
                    hide();
                })).size(40f);

                t.button(Icon.add, Styles.defaulti, () -> addTag(null, null)).size(40f);
            }).fillY().growX().pad(6f);
        }).size(600f, 400f);

        ui.hudGroup.addChild(cont);
    }

    @Override
    public void begin(){
        cont.setScale(0f, 1f);
        cont.visible = false;

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
        if(!cont.visible) return;

        var v = Core.input.mouseScreen(selected.x - selected.block.size * tilesize / 2f, selected.y + selected.block.size * tilesize / 2f);
        cont.pack();
        cont.setPosition(v.x, v.y, Align.topRight);
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

        tags.each(Element::remove);
        tags.clear();

        for(var entry : node.tags){
            addTag(entry.key, entry.value);
        }
    }

    protected void addTag(String key, String value){
        var cont = new Table();
        cont.defaults().pad(4f);

        String[] keyStr = {key};
        cont.center().left();
        cont.button(Icon.trash, Styles.defaulti, () -> {
            tags.remove(cont);
            cont.remove();

            manualSave();
            cont.pack();
        }).size(40f);

        cont.add("Key:");
        cont.field(keyStr[0], Styles.nodeField, t -> {
                if(!t.isEmpty()) current(node -> node.tags.remove(t));
            })
            .update(t -> keyStr[0] = t.getText())
            .name("key").fill();

        cont.add("Value:");
        cont.field(value, Styles.nodeField, null)
            .update(t -> {
                var k = keyStr[0];
                if(!t.getText().isEmpty() && k != null && !k.isEmpty()){
                    current(node -> node.tags.put(k, t.getText()));
                }
            })
            .name("value").fillY().growX();

        pane.row().add(cont).fillY().growX();
        tags.add(cont);
    }

    protected void show(){
        if(cont.visible || lock) return;

        lock = true;

        cont.visible = true;
        cont.setScale(0f, 1f);
        cont.actions(
            Actions.scaleTo(1f, 1f, 0.12f, Interp.pow3Out),
            Actions.run(() -> lock = false)
        );
    }

    protected void hide(){
        if(!cont.visible || lock) return;

        lock = true;
        cont.actions(
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
