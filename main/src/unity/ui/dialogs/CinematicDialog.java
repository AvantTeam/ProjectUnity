package unity.ui.dialogs;

import arc.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.dialogs.*;
import unity.map.cinematic.*;
import unity.ui.dialogs.canvas.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

public class CinematicDialog extends BaseDialog{
    private final CinematicCanvas canvas;

    public CinematicDialog(){
        super("@dialog.cinematic");

        clearChildren();

        add(titleTable).growX().fillY().row();
        add(canvas = new CinematicCanvas()).grow().row();

        addCloseButton();
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

        buttons.button("@dialog.cinematic.test", Icon.downOpen, () -> {
            Exception thrown = null;
            try{
                cinematicEditor.apply();
            }catch(Exception e){
                thrown = e;
            }

            if(thrown != null){
                ui.showException("[scarlet]There were misbehaving story nodes[]", thrown);
            }else{
                ui.showInfo("No story nodes are misconstructed, safe to proceed");
            }
        }).size(210f, 64f);

        add(buttons).fillX();
    }

    @Override
    public void hide(){
        if(!isShown()) return;

        try{
            cinematicEditor.apply();
            super.hide();
        }catch(Exception e){
            ui.showException("""
                [scarlet]Couldn't exit this dialog as there were misconstructed story nodes[]
                Fix these nodes before proceeding
                """, e
            );
        }
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
        cinematicEditor.nodes.each(canvas::add);
    }

    public void end(){
        canvas.clearChildren();
    }
}
