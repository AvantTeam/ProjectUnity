package unity.ui.dialogs;

import arc.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.*;
import unity.cinematic.*;
import unity.ui.dialogs.canvas.*;

import static unity.Unity.*;

public class CinematicDialog extends BaseDialog{
    private final CinematicCanvas canvas;

    public CinematicDialog(){
        super("@dialog.cinematic");

        clearChildren();

        add(titleTable).growX().fillY().row();
        add(canvas = new CinematicCanvas()).grow().row();

        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);
        buttons.button("@add", Icon.add, () -> {
            var node = new StoryNode();
            node.sector = cinematicEditor.sector();
            node.name = lastName();
            node.position.set(canvas.stageToLocalCoordinates(Tmp.v1.set(
                Core.graphics.getWidth() / 2f,
                Core.graphics.getHeight() / 2f
            )));

            cinematicEditor.nodes.add(node);
            canvas.add(node);
        }).size(210f, 64f);

        add(buttons).fillX();
    }

    private String lastName(){
        int i = 0;
        for(var node : cinematicEditor.nodes){
            if(node.name.startsWith("node") && Character.isDigit(node.name.codePointAt(4))){
                int index = Character.digit(node.name.charAt(4), 10);
                if(index > i) i = index;
            }
        }

        return "node" + (i + 1);
    }

    public void begin(){
        canvas.clearChildren();
    }

    public void end(){
        canvas.clearChildren();
    }
}
