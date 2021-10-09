package unity.editor;

import arc.*;
import arc.input.*;
import arc.struct.*;
import mindustry.io.*;
import unity.map.cinematic.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * An editor listener to setup based story nodes in maps.
 * @author GlennFolker
 */
public class CinematicEditor extends EditorListener{
    public final Seq<StoryNode> nodes = new Seq<>();

    @Override
    public void update(){
        if(!cinematicDialog.isShown() && Core.input.keyTap(KeyCode.f4)){
            cinematicDialog.show();
        }
    }

    @Override
    public void begin(){
        nodes.set(sector().storyNodes);
        sector().storyNodes.clear();

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
        editor.tags.put("storyNodes", JsonIO.json.toJson(nodes, Seq.class, StoryNode.class));
        sector().setNodes(nodes);
    }
}
