package unity.editor;

import arc.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.ui.*;
import mindustry.world.*;
import unity.map.cinematic.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * An editor listener to setup based story nodes in maps.
 * @author GlennFolker
 */
public class CinematicEditor extends EditorListener{
    public final Seq<StoryNode> nodes = new Seq<>();

    public TagElem elem;
    public final ObjectMap<Object, ObjectSet<String>> tags = new ObjectMap<>();

    @Override
    protected void registerEvents(){
        super.registerEvents();

        ui.hudGroup.fill(t -> {
            elem = new TagElem();
            elem.visible = false;
            elem.update(() -> {
                Vec2 vec;
                if(elem.bound instanceof Building build){
                    vec = Tmp.v1.set(build.getX(), build.getY() + build.block.size * tilesize / 2f);
                }else if(elem.bound instanceof Tile tile){
                    vec = Tmp.v1.set(tile.worldx(), tile.worldy() + tile.block().size * tilesize / 2f);
                }else{
                    hideTag();
                    return;
                }

                vec = Core.input.mouseScreen(vec.x, vec.y);
                elem.setPosition(vec.x, vec.y, Align.topRight);
            });

            t.addChild(elem);
        });
    }

    @Override
    public void update(){
        // Press F4 to show cinematic dialog.
        if(!cinematicDialog.isShown() && Core.input.keyTap(KeyCode.f4)){
            cinematicDialog.show();
        }

        // Alt + Right Click to edit tile/building tags.
        if(Core.scene.getScrollFocus() == null && Core.input.alt() && Core.input.keyTap(KeyCode.mouseRight)){
            var pos = Core.input.mouseWorld();
            var tile = world.tileWorld(pos.x, pos.y);

            if(tile != null){
                showTag(tile.build == null ? tile : tile.build);
            }else{
                hideTag();
            }
        }
    }

    @Override
    public void draw(){
        Draw.draw(Layer.blockOver, () -> {
            for(var obj : tags.keys()){
                var data = Tmp.v31;
                if(obj instanceof Building b){
                    data.set(b.getX(), b.getY(), b.block.size * tilesize);
                }else if(obj instanceof Tile tile){
                    data.set(tile.worldx(), tile.worldy(), tile.block().size * tilesize);
                }else{
                    continue;
                }

                float x = data.x, y = data.y, rad = data.z;
                Lines.stroke(3f, Pal.darkerMetal);
                Lines.rect(x, y, rad, rad);

                Lines.stroke(1.5f, elem.bound == obj ? Pal.accent : Pal.place);
                Lines.rect(x, y, rad, rad);
            }
        });
    }

    private String lastName(){
        int i = 0;
        for(var set : tags.values()){
            for(var tag : set){
                if(tag.startsWith("tag") && Character.isDigit(tag.codePointAt("tag".length()))){
                    int index = Character.digit(tag.charAt("tag".length()), 10);
                    if(index > i) i = index;
                }
            }
        }

        return "tag" + (i + 1);
    }

    protected void showTag(Object target){
        if(elem.bound() == target){
            hideTag();
            return;
        }

        showTag();
        if(!tags.containsKey(target)) tags.get(target, ObjectSet::new).add(lastName());
        elem.set(target);
    }

    protected void hideTag(){
        if(!elem.visible) return;
        elem.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.run(() -> elem.set(null)), Actions.visible(false));
    }

    protected void showTag(){
        if(elem.visible) return;

        elem.visible = true;
        elem.clearActions();
        elem.pack();
        elem.setTransform(true);
        elem.actions(Actions.scaleTo(0f, 1f), Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));
    }

    @Override
    public void begin(){
        nodes.set(sector().cinematic.nodes);
        sector().cinematic.nodes.clear();

        tags.clear();
        tags.putAll(sector().cinematic.objectToTag);
        sector().cinematic.clearTags();

        cinematicDialog.begin();
    }

    @Override
    public void end(){
        try{
            apply();
        }catch(Exception e){
            ui.showException("Failed to parse story nodes", e);
        }finally{
            cinematicDialog.end();
            nodes.each(node -> node.elem = null);
            nodes.clear();

            elem.visible = false;
            elem.set(null);
        }
    }

    public void apply() throws Exception{
        sector().cinematic.setNodes(nodes);
        sector().cinematic.setTags(tags);

        editor.tags.put("nodes", JsonIO.json.toJson(sector().cinematic.saveNodes(), StringMap.class, String.class));
        editor.tags.put("object-tags", JsonIO.json.toJson(sector().cinematic.saveTags(), Seq.class, String.class));
    }

    public class TagElem extends Table{
        private Object bound;
        private Table content;
        private final Seq<String> def = new Seq<>();

        public TagElem(){
            background(Tex.button);
            margin(0f);

            setSize(400f, 300f);
            add("Tags").padTop(8f);
            row().image(Tex.whiteui).color(Pal.accent).width(3f).growX().pad(4f);
            row().pane(t -> content = t).grow().pad(4f).padTop(8f);

            row().button(Icon.trash, Styles.emptyi, () -> ui.showConfirm("@dialog.cinematic.tag-delete.title", "@dialog.cinematic.tag-delete.content", () -> {
                tags.remove(bound);
                hideTag();
            })).left().pad(4f);

            update(() -> {
                if(bound != null){
                    var set = tags.get(bound);
                    if(set == null) return;

                    set.clear();
                    set.addAll(def);
                }
            });
        }

        public Object bound(){
            return bound;
        }

        public void set(Object bound){
            this.bound = bound;

            content.clear();
            def.clear();
            if(bound != null){
                var set = tags.get(bound, ObjectSet::new);
                def.setSize(set.size);

                int i = 0;
                for(var tag : set){
                    int index = i++;

                    content.add("Tag: ");
                    content.field(tag, str -> def.set(index, str)).grow();
                    content.row();
                }
            }
        }
    }
}
