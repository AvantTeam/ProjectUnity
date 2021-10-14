package unity.ui.dialogs;

import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

public class ScriptsDictionaryDialog extends BaseDialog{
    private StringMap dictionary;
    private final Seq<ScriptElem> elems = new Seq<>();
    private Table content;

    public ScriptsDictionaryDialog(){
        super("@dialog.cinematic.scripts");
        cont.pane(Styles.nonePane, t -> {
            content = t;
            content.defaults().pad(3f);
        }).grow();

        addCloseButton();
        buttons.button("@add", Icon.add, () -> {
            var e = new ScriptElem(lastName(), "");
            elems.add(e);

            put(e);
        }).size(210f, 64f);

        shown(() -> {
            content.clearChildren();

            elems.clear();
            for(var e : dictionary.entries()){
                var elem = new ScriptElem(e.key, e.value);
                elems.add(elem);
            }

            rebuild();
        });

        hidden(() -> {
            dictionary.clear();
            for(var e : elems){
                dictionary.put(e.name, e.script);
            }

            elems.clear();
            dictionary = null;
        });
    }

    private String lastName(){
        int i = 0;
        for(var e : elems){
            if(e.name.startsWith("script") && Character.isDigit(e.name.codePointAt("script".length()))){
                int index = Character.digit(e.name.charAt("script".length()), 10);
                if(index > i) i = index;
            }
        }

        return "script" + (i + 1);
    }

    @Override
    public ScriptsDictionaryDialog show(){
        if(dictionary != null){
            super.show();
            return this;
        }else{
            throw new IllegalArgumentException("Use #show(StringMap)");
        }
    }

    public ScriptsDictionaryDialog show(StringMap dictionary){
        this.dictionary = dictionary;
        return show();
    }

    private void rebuild(){
        content.clearChildren();
        for(var e : elems){
            put(e);
        }
    }

    private void put(ScriptElem elem){
        content.add(elem).size(300f, 64f);
        if((content.getChildren().size % 3) == 0){
            content.row();
        }
    }

    private class ScriptElem extends Table{
        private String name;
        private String script;

        public ScriptElem(String name, String script){
            this.name = name;
            this.script = script;

            background(Tex.button);
            margin(0f);

            add("Name: ").padLeft(8f);

            var field = field(name, str -> this.name = str).growX().get();
            field.setValidator(str -> !elems.contains(e -> e.name.equals(str)));

            add().growX();

            button(Icon.pencil, Styles.emptyi, () -> {
                jsEditDialog.listener = str -> this.script = str;
                jsEditDialog.area.setText(this.script);
                jsEditDialog.show();
            });

            button(Icon.trash, Styles.emptyi, () -> ui.showConfirm("@dialog.scripts-dictionary.delete.title", "@dialog.scripts-dictionary.delete.content", () -> {
                elems.remove(this);
                rebuild();
            })).padRight(8f);
        }
    }
}
