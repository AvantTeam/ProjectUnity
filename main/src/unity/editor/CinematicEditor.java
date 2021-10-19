package unity.editor;

import arc.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
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
    public final ObjectMap<Object, ObjectSet<String>> tags = new ObjectMap<>();

    @Override
    public void update(){
        // Press F4 to show cinematic dialog.
        if(!cinematicDialog.isShown() && Core.input.keyTap(KeyCode.f4)){
            cinematicDialog.show();
        }

        // Alt + Right Click to edit tile/building tags.
        if(Core.scene.getScrollFocus() == null && Core.input.alt() && Core.input.keyTap(KeyCode.mouseRight)){
            Vec2 pos = Core.input.mouseWorld();
            Tile tile = world.tileWorld(pos.x, pos.y);

            if(tile != null){
                if(tile.build != null){
                    new Dialog(""){{
                        clear();

                        addCloseButton();
                        buttons.row().button("@building", () -> {
                            showTag(tile.build);
                            hide();
                        }).size(210f, 64f);
                        buttons.row().button("@tile", () -> {
                            showTag(tile);
                            hide();
                        }).size(210f, 64f);
                        add(buttons).grow();
                    }}.show();
                }else{
                    showTag(tile);
                }
            }
        }
    }

    @Override
    public void draw(){
        Draw.draw(Layer.blockOver, () -> {
            for(var obj : tags.keys()){
                Vec3 data = Tmp.v31;
                if(obj instanceof Building b){
                    data.set(b.getX(), b.getY(), b.block.size * tilesize / 2f);
                }else if(obj instanceof Tile tile){
                    data.set(tile.worldx(), tile.worldy(), tile.floor().size * tilesize / 2f);
                }else{
                    continue;
                }

                Lines.stroke(1f, Pal.place);
                Lines.square(data.x, data.y, data.z);
            }
        });
    }

    protected void showTag(Object target){
        if(!tags.containsKey(target)) tags.put(target, new ObjectSet<>());
        tagsDialog.show(tags, target);
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
        }
    }

    public void apply() throws Exception{
        var core = sector().cinematic;
        core.nodes.clear();
        core.clearTags();

        core.setNodes(nodes);
        core.setTags(tags);

        editor.tags.put("nodes", JsonIO.json.toJson(core.nodes, Seq.class, StoryNode.class));
        editor.tags.put("object-tags", JsonIO.json.toJson(core.saveTags(), Seq.class, String.class));
    }
}
