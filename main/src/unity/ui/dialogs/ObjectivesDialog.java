package unity.ui.dialogs;

import arc.scene.actions.*;
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

import static unity.map.objectives.ObjectiveModel.*;

public class ObjectivesDialog extends BaseDialog{
    private StoryNode node;
    private final Seq<ObjectiveModel> models = new Seq<>();

    private Table content;

    public ObjectivesDialog(){
        super("@dialog.cinematic.objectives");

        addCloseButton();

        cont.pane(Styles.defaultPane, t -> content = t).growY().width(500f);
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
        content.add(new ObjectiveElem(model)).grow().padTop(8f).padBottom(8f).row();
    }

    private class ObjectiveElem extends Table{
        private final ObjectiveModel model;
        private ScrollPane selection;

        private ObjectiveElem(ObjectiveModel model){
            this.model = model;

            background(Tex.whiteui);
            update(() -> setColor(model.type == null ? Pal.darkerMetal : data(model.type).color));
            margin(0f);

            table(Styles.black9, t -> {
                var typeSelect = t.add("Type:", Styles.defaultLabel).width(100f).get();
                typeSelect.setAlignment(Align.left);

                t.table(select -> {
                    var typePref = select.label(() -> model.type == null ? "" : model.type.getSimpleName()).growX().get();
                    selection = new ScrollPane(new Table(){{
                        for(var type : datas.keys()){
                            button(type.getSimpleName(), data(type).icon.get(), () -> {
                                model.set(type);

                                if(selection.visible){
                                    selection.actions(Actions.scaleTo(1f, 0f, 0.06f), Actions.visible(false));
                                }else{
                                    selection.visible = true;
                                    selection.actions(Actions.scaleTo(1f, 1f));
                                }
                            }).growX().fillY().get().setStyle(Styles.clearPartialt);
                        }
                    }}){
                        {
                            update(() -> setPosition(typePref.x, typePref.y + typePref.getWidth(), Align.topLeft));
                        }

                        @Override
                        public float getPrefWidth(){
                            return typePref.getWidth();
                        }
                    };

                    t.addChild(selection);
                    selection.pack();
                }).width(300f).fillY();
            }).grow().pad(4f).padTop(8f);
        }

        @Override
        public boolean remove(){
            var cell = content.getCells().find(c -> c.get() == this);

            boolean succeed = super.remove();
            if(succeed){
                content.getCells().remove(cell);
                content.invalidate();
            }

            return succeed;
        }
    }
}
