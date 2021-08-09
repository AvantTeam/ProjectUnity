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

    @Override
    protected void registerEvents(){
        super.registerEvents();

        cont = new Table();
        cont.setSize(200f, 600f);

        cont.add("Name:").fill();
        name = cont.field("", Styles.nodeField, s -> current(node -> node.name = s)).fillY().growX().get();

        cont.row().pane(Styles.defaultPane, t -> {
            pane = t.table(Styles.black5).fill().get();
            t.row().center().button(Icon.add, () -> addTag(null, null));
        }).grow();

        cont.row().left();
        cont.button(Icon.up, Styles.clearTransi, this::hide).fill();
        cont.button(Icon.trash, Styles.clearTransi, () -> {
            var node = current();
            if(node != null){
                nodes.remove(node);
                hide();
            }
        }).fill();

        cont.actions(Actions.scaleTo(1f, 0f));
    }

    @Override
    public void begin(){
        try{
            nodes.addAll(JsonIO.json.fromJson(Seq.class, editor.tags.get("storyNodes", "[]")));
        }catch(Throwable t){
            Log.err(t);
        }
    }

    @Override
    public void end(){
        try{
            editor.tags.put("storyNodes", JsonIO.json.toJson(nodes, Seq.class));
            nodes.clear();
        }catch(Throwable t){
            Log.err(t);
        }
    }

    @Override
    public void update(){
        if(Core.input.keyDown(Binding.control)){
            var pos = Core.input.mouseWorld();
            var build = world.buildWorld(pos.x, pos.y);

            if(Core.input.keyTap(KeyCode.altLeft)){
                manualSave();
                if(selected != build){
                    selected = build;
                    if(selected != null){
                        Sounds.click.play();

                        show();
                        updateTable();
                    }else{
                        hide();
                    }
                }else{
                    selected = null;
                    hide();
                }
            }
        }

        if(selected != null && selected.isAdded()) updateTablePosition();

        nodes.removeAll(node -> node.bound == null || !node.bound.isValid());
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

        String[] keyStr = {key};
        cont.left()
            .field(keyStr[0], t -> {
                if(!t.isEmpty()) current(node -> node.tags.remove(t));
            })
            .name("key").fill();

        cont.right()
            .field(value, null)
            .update(t -> {
                if(!t.getText().isEmpty()) current(node -> node.tags.put(keyStr[0], t.getText()));
            })
            .name("value").grow();

        pane.row().add(cont).fillY().growX();
        tags.add(cont);
    }

    protected void show(){
        cont.actions(Actions.scaleTo(1f, 1f, 0.2f, Interp.pow3Out));
    }

    protected void hide(){
        cont.actions(Actions.scaleTo(1f, 0f, 0.2f, Interp.pow3In));
    }

    public BlockStoryNode current(){
        return nodes.find(n -> n.bound == selected);
    }

    public void current(Cons<BlockStoryNode> cons){
        var node = current();
        if(node != null) cons.get(node);
    }
}
