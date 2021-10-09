package unity.ui.dialogs;

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

import static unity.Unity.*;
import static unity.map.objectives.ObjectiveModel.*;

public class ObjectivesDialog extends BaseDialog{
    private StoryNode node;
    private final Seq<ObjectiveModel> models = new Seq<>();

    private Table content;

    public ObjectivesDialog(){
        super("@dialog.cinematic.objectives");

        addCloseButton();

        cont.pane(Styles.nonePane, t -> content = t).growY().width(750f);
        buttons.button("@add", Icon.add, () -> {
            var model = new ObjectiveModel();

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

                var field = t.field(model.name, Styles.defaultField, str -> model.name = str).padRight(8f).get();
                field.setValidator(str -> !models.contains(m -> m.setFields.get("name", "").equals("")));
                field.getStyle().font = Fonts.outline;

                t.add().growX();
                t.button(Icon.cancel, Styles.logici, this::remove).padRight(8f);
            }).growX().fillY().pad(4f);

            row().table(Styles.black5, t -> {
                t.defaults().pad(4f);

                t.table(ts -> {
                    var typeSelect = ts.label(() -> "Type: " + (model.type == null ? "..." : model.type.getSimpleName())).width(100f).padLeft(8f).get();
                    typeSelect.setAlignment(Align.left);

                    ts.add().growX();

                    ts.button(Icon.downOpen, Styles.logici, () -> {
                        var dialog = new BaseDialog("@dialog.cinematic.objectives.select");
                        dialog.addCloseButton();

                        dialog.cont.pane(Styles.nonePane, s -> {
                            for(var type : datas.keys()){
                                var data = data(type);

                                var b = s.button(type.getSimpleName(), data.icon.get(), Styles.cleart, () -> {
                                    model.set(model.type == type ? null : type);
                                    rebuild();
                                })
                                    .size(210f, 64f)
                                    .pad(8f).get();
                                b.getStyle().fontColor.set(data.color);
                                b.getStyle().font = Fonts.outline;
                            }
                        }).width(500f).growY();

                        dialog.show();
                    }).padRight(8f);

                    ts.button(Icon.pencil, Styles.logici, () -> {
                        scriptsDialog.listener = str -> model.init = str;
                        if(scriptsDialog.area.getText().isEmpty()){
                            scriptsDialog.area.setText("""
                            function(fields){
                                
                            }
                            """
                            );
                        }

                        scriptsDialog.show();
                    }).padRight(8f);
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
                fields.add("Fields", Styles.outlineLabel).padLeft(8f);
                fields.add().growX();
                fields.row().image(Tex.whiteui).growX().height(3f).color(Tmp.c1.set(color).mul(0.5f)).padBottom(8f);

                fields.row().table(t -> {
                    for(var f : model.type.getDeclaredFields()){
                        if(f.isAnnotationPresent(Ignore.class) || Modifier.isStatic(f.getModifiers())) continue;

                        t.add(f.getName(), Styles.outlineLabel).growX();
                        t.add(" | ", Styles.outlineLabel);
                        t.add(f.getGenericType().getTypeName(), Styles.outlineLabel).growX();
                        t.row();
                    }
                }).grow().padLeft(8f).padRight(8f);
            }
        }

        @Override
        public boolean remove(){
            var cell = content.getCell(this);

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
