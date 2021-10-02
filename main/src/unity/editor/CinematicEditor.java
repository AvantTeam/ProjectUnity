package unity.editor;

import arc.struct.*;
import mindustry.io.*;
import unity.cinematic.*;
import unity.ui.dialogs.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * An editor listener to setup based story nodes in maps.
 * @author GlennFolker
 */
public class CinematicEditor extends EditorListener{
    public final Seq<StoryNode> nodes = new Seq<>();

    @Override
    protected void registerEvents(){
        super.registerEvents();
        cinematicDialog = new CinematicDialog();
    }

    @Override
    public void begin(){
        nodes.set(sector().storyNodes);
        sector().storyNodes.clear();

        cinematicDialog.begin();
    }

    @Override
    public void end(){
        editor.tags.put("storyNodes", JsonIO.json.toJson(nodes, Seq.class, StoryNode.class));

        sector().setNodes(nodes);

        cinematicDialog.end();
        nodes.each(node -> node.elem = null);
        nodes.clear();
    }
}
