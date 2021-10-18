package unity.ui.dialogs;

import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class TagsDialog extends BaseDialog{
    private Table content;
    private final Seq<TagElem> elems = new Seq<>();

    public ObjectMap<Object, ObjectSet<String>> tags = new ObjectMap<>();
    public Object bound;

    public TagsDialog(){
        super("@dialog.cinematic.tag");

        cont.pane(t -> content = t).growY().width(500f);

        addCloseButton();
        buttons.button("@add", Icon.add, () -> add(new TagElem(lastName()))).size(210f, 64f);
        buttons.button("Remove", Icon.trash, () -> {
            var tags = this.tags;
            var bound = this.bound;

            hide();
            tags.remove(bound);
        }).size(210f, 64f);

        shown(() -> {
            var set = tags.get(bound, ObjectSet::new);
            for(var tag : set){
                add(new TagElem(tag));
            }
        });

        hidden(() -> {
            refresh();
            tags = null;
            bound = null;
        });
    }

    private String lastName(){
        refresh();

        int i = 0;
        for(var set : tags.values()){
            for(var tag : set){
                if(tag.startsWith("tag") && Character.isDigit(tag.codePointAt("tag".length()))){
                    int index = Character.digit(tag.charAt("tag".length()), 10);
                    if(index > i) i = index;
                }
            }
        }

        return "tag" + (i + 1);
    }

    private void add(TagElem elem){
        elems.add(elem);
        content.add(elem).growX().fillY().row();

        refresh();
    }

    private void refresh(){
        if(bound == null) return;

        var set = tags.get(bound, ObjectSet::new);
        set.clear();
        elems.each(e -> set.add(e.tag));
    }

    @Override
    public TagsDialog show(){
        if(bound != null){
            super.show();
            return this;
        }else{
            throw new IllegalArgumentException("Use #show(ObjectMap, Object)");
        }
    }

    public TagsDialog show(ObjectMap<Object, ObjectSet<String>> tags, Object bound){
        this.tags = tags;
        this.bound = bound;
        content.clear();
        return show();
    }

    private class TagElem extends Table{
        private String tag;

        private TagElem(String init){
            tag = init;

            background(Tex.button);

            add("Tag: ").padLeft(4f);
            field(init, str -> {
                tag = str;
                refresh();
            }).valid(str -> {
                if(elems.contains(e -> e.tag.equals(tag))) return false;
                for(var set : tags.values()){
                    if(set.contains(tag)) return false;
                }

                return true;
            }).growX().padRight(8f);

            button(Icon.trash, Styles.emptyi, this::remove).padRight(8f);
        }

        @Override
        public boolean remove(){
            var cell = content.getCell(this);

            boolean succeed = super.remove();
            if(succeed && cell != null){
                elems.remove(this);
                content.getCells().remove(cell);
                content.invalidate();
            }

            refresh();
            return succeed;
        }
    }
}
