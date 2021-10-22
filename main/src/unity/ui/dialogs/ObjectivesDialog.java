package unity.ui.dialogs;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;
import static unity.Unity.*;
import static unity.map.objectives.ObjectiveModel.*;

@SuppressWarnings("unchecked")
public class ObjectivesDialog extends BaseDialog{
    private StoryNode node;
    private final Seq<ObjectiveModel> models = new Seq<>();

    private Table content;
    private final ObjectMap<Class<? extends Objective>, Seq<Field>> fields = new ObjectMap<>();

    public ObjectivesDialog(){
        super("@root.cinematic.objectives");

        addCloseButton();

        cont.pane(Styles.nonePane, t -> content = t).growY().width(750f);
        buttons.button("@add", Icon.add, () -> {
            ObjectiveModel model = new ObjectiveModel();
            model.name = lastName();

            models.add(model);
            add(model);
        }).size(210f, 64f);

        shown(() -> {
            content.clearChildren();

            models.set(node.objectiveModels);
            models.each(this::add);
        });

        hidden(() -> {
            node.objectiveModels.set(models);
            node = null;
        });
    }

    private String lastName(){
        int i = 0;
        for(ObjectiveModel m : models){
            if(m.name.startsWith("objective") && Character.isDigit(m.name.codePointAt("objective".length()))){
                int index = Character.digit(m.name.charAt("objective".length()), 10);
                if(index > i) i = index;
            }
        }

        return "objective" + (i + 1);
    }

    private Seq<Field> getFields(Class<? extends Objective> type){
        return fields.get(type, () -> {
            Seq<Field> all = new Seq<>();

            Class<?> current = type;
            while(true){
                for(Field f : current.getDeclaredFields()){
                    int mod = f.getModifiers();
                    if(Modifier.isStatic(mod) || Modifier.isTransient(mod)) continue;
                    all.add(f);
                }

                if(current == Objective.class){
                    break;
                }else{
                    current = current.getSuperclass();
                }
            }

            return all;
        });
    }

    @Override
    public ObjectivesDialog show(){
        if(node != null){
            super.show();
            return this;
        }else{
            throw new IllegalArgumentException("Use #show(StoryNode)");
        }
    }

    public ObjectivesDialog show(StoryNode node){
        this.node = node;
        return show();
    }

    private void add(ObjectiveModel model){
        content.add(new ObjectiveElem(model)).growX().fillY().padTop(8f).padBottom(8f).row();
    }

    private class ObjectiveElem extends Table{
        private final ObjectiveModel model;
        private Table fields;

        private ObjectiveElem(ObjectiveModel model){
            this.model = model;

            background(Tex.whiteui);
            update(() -> setColor(model.type == null ? Pal.darkerMetal : data(model.type).color));
            margin(0f);

            table(t -> {
                t.add("Name: ").style(Styles.outlineLabel).padLeft(8f);

                TextField field = t.field(model.name, Styles.defaultField, str -> model.name = str).get();
                field.setValidator(str -> !models.contains(m -> m.name.equals(str)));
                field.getStyle().font = Fonts.outline;

                t.add().growX();
                t.button(Icon.trash, Styles.logici, () -> ui.showConfirm("@root.cinematic.objectives.delete.title", "@root.cinematic.objectives.delete.content", this::remove)).padRight(8f);
            }).growX().fillY().pad(4f);

            row().table(Styles.black5, t -> {
                t.defaults().pad(4f);

                t.table(ts -> {
                    Label typeSelect = ts.label(() -> "Type: " + (model.type == null ? "..." : model.type.getSimpleName())).fillX().padLeft(8f).get();
                    typeSelect.setAlignment(Align.left);

                    ts.add().growX();

                    ts.button(Icon.downOpen, Styles.logici, () -> {
                        BaseDialog dialog = new BaseDialog("@root.cinematic.objectives.select");
                        dialog.addCloseButton();

                        dialog.cont.pane(Styles.nonePane, s -> {
                            for(Class<? extends Objective> type : datas.keys()){
                                ObjectiveData data = data(type);

                                s.button(type.getSimpleName(), data.icon.get(), Styles.defaultt, () -> {
                                    model.set(model.type == type ? null : type);
                                    rebuild();
                                })
                                    .size(350f, 64f).color(data.color).pad(4f).row();
                            }
                        }).width(500f).growY();

                        dialog.show();
                    }).padRight(8f);

                    ts.button(Icon.pencil, Styles.logici, () -> {
                        jsEditDialog.listener = str -> model.init = str;
                        jsEditDialog.area.setText(model.init);

                        if(jsEditDialog.area.getText().isEmpty()){
                            jsEditDialog.area.setText("""
                            function(fields){
                                
                            }
                            """
                            );
                        }

                        jsEditDialog.show();
                    });
                }).growX().fillY();

                t.row().table(c -> {
                    fields = c;
                    rebuild();
                }).growX().fillY().padTop(8f);
            }).grow().pad(8f).padTop(16f);
        }

        private void rebuild(){
            fields.clearChildren();
            fields.defaults().pad(4f);

            if(model.type == null){
                fields.add("...", Styles.outlineLabel).grow();
            }else{
                fields.table(t -> {
                    t.add("Fields", Styles.outlineLabel).get().setAlignment(Align.left);
                    t.row().image(Tex.whiteui).growX().height(3f).color(Tmp.c1.set(color).mul(0.5f)).padTop(8f);
                }).growX().fillY().padLeft(8f).padRight(8f).padBottom(8f);

                fields.row().table(t -> {
                    for(Field f : getFields(model.type)){
                        t.add(f.getName(), Styles.outlineLabel).growX();
                        t.add(" | ", Styles.outlineLabel);
                        t.add(f.getGenericType().getTypeName(), Styles.outlineLabel).growX();
                        t.row();
                    }
                }).grow().padLeft(8f).padRight(8f).padTop(8f);
            }
        }

        @Override
        public boolean remove(){
            Cell<ObjectiveElem> cell = (Cell<ObjectiveElem>)content.getCell(this);

            boolean succeed = super.remove();
            if(succeed && cell != null){
                models.remove(model);

                content.getCells().remove(cell);
                content.invalidate();
            }

            return succeed;
        }
    }
}
