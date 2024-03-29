package unity.ui.dialogs.canvas;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import unity.map.cinematic.*;
import unity.ui.*;

import java.util.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * The canvas where all story nodes representation gets drawn into. Based off of the old logic canvas.
 * @author GlennFolker
 * @author Anuke
 */
public class CinematicCanvas extends WidgetGroup{
    private static final Color base = Pal.accent, connection = Pal.place;

    private static TextureRegionDrawable
        acceptor,
        distributor;

    private ImageButton
        selected,
        entered;

    public CinematicCanvas(){
        if(acceptor == null) acceptor = new TextureRegionDrawable(Core.atlas.find("unity-cinematic-node-acceptor"));
        if(distributor == null) distributor = new TextureRegionDrawable(Core.atlas.find("unity-cinematic-node-distributor"));
    }

    @Override
    public void draw(){
        super.draw();

        Draw.alpha(parentAlpha);
        getChildren().each(e -> e instanceof NodeElem, NodeElem::drawConnection);

        if(selected != null){
            Vec2 dest = selected.localToStageCoordinates(Tmp.v1.set(
                selected.getWidth() / 2f,
                selected.getHeight() / 2f
            ));
            Vec2 mouse = Core.input.mouse();

            drawCurve(dest.x, dest.y, mouse.x, mouse.y);
        }
    }

    private void drawCurve(float x, float y, float x2, float y2){
        Lines.stroke(4f, Tmp.c1.set(connection).a(connection.a * parentAlpha));
        float dist = Math.abs(x - x2) / 2f;
        Lines.curve(x, y, x + dist, y, x2 - dist, y2, x2, y2, Math.max(3, (int)(Mathf.dst(x, y, x2, y2) / 5f)));

        Draw.reset();
    }

    public void add(StoryNode node){
        NodeElem elem = new NodeElem(node);
        Vec2 pos = localToStageCoordinates(node.position);
        elem.setPosition(pos.x, pos.y, Align.center);

        addChild(elem);
        elem.pack();
    }

    public class NodeElem extends Table{
        public final StoryNode node;
        private ImageButton acceptCont, distCont;

        private NodeElem(StoryNode node){
            this.node = node;
            node.elem = this;

            setClip(false);
            update(() -> node.position.set(parent.stageToLocalCoordinates(Tmp.v1.set(x, y))));

            background(Tex.whitePane);
            setColor(connection);
            margin(0f);

            connection(true);
            table(Tex.whiteui, t -> {
                t.setColor(base);

                t.margin(8f);
                t.touchable = Touchable.enabled;

                t.add("Name: ").style(Styles.outlineLabel);
                TextField field = t.field(node.name, Styles.defaultField, str -> node.name = str).padRight(8f).get();
                field.setValidator(str -> !cinematicEditor.nodes.contains(n -> n.name.equals(str)));
                field.getStyle().font = Fonts.outline;

                // Specialized button that cancels touch if dragged, to avoid hard-locks while dragging the node element.
                class DragButton extends ImageButton{
                    DragButton(Drawable drawable, ImageButtonStyle style, Runnable listener){
                        super(drawable, style);

                        resizeImage(drawable.imageSize());
                        addListener(new ClickListener(){
                            {
                                setButton(KeyCode.mouseLeft);
                            }

                            @Override
                            public void clicked(InputEvent event, float x, float y){
                                if(listener != null && !isDisabled()) listener.run();
                            }

                            @Override
                            public void touchDragged(InputEvent event, float x, float y, int pointer){
                                if(pointer == pressedPointer && !cancelled) cancel();
                            }
                        });
                    }
                }

                t.add().growX();
                t.add(new DragButton(Icon.book, Styles.logici, () -> jsDictDialog.show(node.scripts))).padRight(4f);
                t.add(new DragButton(Icon.pencil, Styles.logici, () -> objectivesDialog.show(node))).padRight(4f);
                t.add(new DragButton(Icon.trash, Styles.logici, () -> ui.showConfirm("@root.cinematic.node-delete.title", "@root.cinematic.node-delete.content", () -> {
                    remove();

                    Iterator<StoryNode> it = cinematicEditor.nodes.iterator();
                    while(it.hasNext()){
                        StoryNode e = it.next();
                        e.children.remove(node);

                        if(e == node) it.remove();
                    }
                }))).padRight(4f);

                t.addListener(new MoveDragListener(this));
            }).growX().fillY();
            connection(false);
        }

        private void connection(boolean accept){
            float size = 30f, pad = size / 2f - 3f;
            Cell<ImageButton> c = button(accept ? acceptor : distributor, Styles.colori, () -> {}).size(size);

            ImageButton button = c.get();
            if(accept){
                acceptCont = button;
                c.padLeft(-pad);
            }else{
                distCont = button;
                c.padRight(-pad);
            }

            button.userObject = this;
            button.getStyle().imageUpColor = connection;

            if(!accept){
                button.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode code){
                        if(selected == null) selected = button;
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode code){
                        ImageButton e = entered;
                        if(e != null && e.userObject instanceof NodeElem elem && elem != NodeElem.this && e == elem.acceptCont){
                            node.child(elem.node);
                        }

                        if(selected == button) selected = null;
                    }
                });
            }else{
                button.addListener(new InputListener(){
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Element from){
                        entered = button;
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Element to){
                        if(entered == button) entered = null;
                    }
                });
            }
        }

        private void drawConnection(){
            for(StoryNode child : node.children){
                NodeElem elem = child.elem;
                if(elem == null) continue;

                Vec2 from = distCont.localToStageCoordinates(Tmp.v1.set(
                    distCont.getWidth() / 2f,
                    distCont.getHeight() / 2f
                ));
                Vec2 to = elem.acceptCont.localToStageCoordinates(Tmp.v2.set(
                    elem.acceptCont.getWidth() / 2f,
                    elem.acceptCont.getHeight() / 2f
                ));

                drawCurve(from.x, from.y, to.x, to.y);
            }
        }
    }
}
