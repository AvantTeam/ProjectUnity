package unity.ui.dialogs;

import arc.func.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.ui.dialogs.*;
import unity.ui.*;

public class JSScriptDialog extends BaseDialog{
    public Cons<String> listener = str -> {};

    public TextArea area;

    public JSScriptDialog(){
        super("@dialog.editscript");

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
        area.setTextFieldListener((f, c) -> {
            if(f instanceof TextArea a && c == '\t'){
                var text = a.getText();
                int cur = a.getCursorPosition();

                int start = -1;
                for(int i = cur; i >= 0; i--){
                    char t = text.charAt(i);
                    if(t == '\n' || t == '\t'){
                        start = i;
                        break;
                    }
                }

                if(start == -1){
                    a.setText(text.substring(0, cur) + "    " + text.substring(cur));
                }else{
                    int length = 4 - ((cur - (start + 1) + 4) % 4);

                    var indent = new StringBuilder();
                    // Don't use String#repeat as it's only in Java 11+
                    for(int i = 0; i < length; i++){
                        indent.append(" ");
                    }

                    a.setText(text.substring(0, cur) + indent + text.substring(cur));
                }
            }
        });
    }

    /**
     * @return The lines label consisting of all line numbers.
     * @author sk7725
     */
    private String linesStr(int first, int len, int now){
        var str = new StringBuilder("[lightgray]");
        for(var i = 0; i < len; i++){
            if(i > 0) str.append("\n");

            if(i + first == now) str.append("[accent]");
            str.append(i + first + 1);

            if(i + first == now) str.append("[]");
        }

        return str + "[]";
    }
}
