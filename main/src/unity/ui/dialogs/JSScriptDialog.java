package unity.ui.dialogs;

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
            var lines = t.label(() -> linesStr(
                area.getFirstLineShowing(),
                area.getLinesShowing()
            )).width(40f).growY().get();
            lines.setAlignment(Align.right);
            lines.setStyle(UnityStyles.codeLabel);

            area = t.area("", UnityStyles.codeArea, this::set).grow().get();
        }).grow().pad(20f);

        hidden(this::broadcast);
        shown(() -> area.setText(startup));
    }

    public void broadcast(){
        listener.get(content);
    }

    private void set(String content){
        this.content = content.replace("\r", "\n");
    }

    /**
     * @param first First line showing.
     * @param len All lines showing.
     * @return The lines label consisting of all line numbers.
     * @author sk7725
     */
    private String linesStr(int first, int len){
        StringBuilder str = new StringBuilder();

        for(var i = 0; i < len; i++){
            str.append(i + first + 1);
            if(i < len - 1) str.append("\n");
        }

        return str.toString();
    }
}
