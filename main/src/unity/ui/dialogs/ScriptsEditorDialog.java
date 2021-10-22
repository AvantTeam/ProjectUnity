package unity.ui.dialogs;

import arc.func.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.ui.dialogs.*;
import unity.ui.*;

public class ScriptsEditorDialog extends BaseDialog{
    public Cons<String> listener = str -> {};

    public TextArea area;

    public ScriptsEditorDialog(){
        super("@root.editscript");

        addCloseButton();

        cont.label(() -> linesStr(
            area.getFirstLineShowing(),
            area.getLinesShowing(),
            area.getCursorLine()
        )).growY()
            .padRight(2f).style(UnityStyles.codeLabel)
            .get().setAlignment(Align.right);

        area = cont.area("", UnityStyles.codeArea, str -> listener.get(str.replace("\r", "\n"))).grow().get();
        area.setFocusTraversal(false);

        hidden(() -> listener.get(area.getText()));
    }

    /**
     * @return The lines label consisting of all line numbers.
     * @author sk7725
     */
    private String linesStr(int first, int len, int now){
        StringBuilder str = new StringBuilder("[lightgray]");
        for(int i = 0; i < len; i++){
            if(i > 0) str.append("\n");

            if(i + first == now) str.append("[accent]");
            str.append(i + first + 1);

            if(i + first == now) str.append("[]");
        }

        return str + "[]";
    }
}
