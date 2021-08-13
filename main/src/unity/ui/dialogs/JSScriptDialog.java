package unity.ui.dialogs;

import arc.*;
import arc.func.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import unity.ui.*;

public class JSScriptDialog extends BaseDialog{
    public String startup = "";
    public Cons<String> listener = str -> {};

    private String content = "";
    private TextArea area;

    public JSScriptDialog(){
        super("@editor.cinematic.editscript");

        addCloseButton();

        cont.table(Styles.black5, t -> {
            var lines = cont.label(() -> linesStr(
                area.getFirstLineShowing(),
                area.getLinesShowing(),
                area.getCursorLine()
            )).width(40f).growY().get();
            lines.setAlignment(Align.right);
            lines.setStyle(UnityStyles.codeLabel);

            area = cont.area("", UnityStyles.codeArea, this::set).grow().get();
        }).grow().pad(20f);

        hidden(() -> listener.get(content));
        shown(() -> area.setText(startup));
    }

    private void set(String content){
        this.content = content;
    }

    /**
     * @param first First line showing.
     * @param len All lines showing.
     * @param now Current line showing.
     * @return The lines label consisting of all line numbers.
     * @author sk7725
     */
    private String linesStr(int first, int len, int now){
        StringBuilder str = new StringBuilder("[lightgray]");

        for(var i = 0; i < len; i++){
            if(i + first == now) str.append("[accent]");
            str.append(i + first + 1);
            if(i + first == now) str.append("[]");
            if(i < len - 1) str.append("\n");
        }

        return str + "[]";
    }
}
