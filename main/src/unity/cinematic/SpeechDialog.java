package unity.cinematic;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Label.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.ui.*;
import unity.mod.*;

import static mindustry.Vars.*;

public class SpeechDialog{
    public static SpeechDialog dialog;
    private static boolean shown;
    private static boolean initialized;

    public final Prov<CharSequence> title;
    public final SpeechProvider content;
    public final Prov<Drawable> image;
    public final Prov<Color> color;

    protected SpeechDialog parent;
    protected SpeechDialog next;
    private boolean done;

    public static void init(){
        if(initialized) return;
        initialized = true;

        dialog = new SpeechDialog();

        Table table = placement().row()
            .table(Tex.wavepane, t -> {
                t.setClip(true);
                t.defaults().pad(3f);

                t.left().top()
                    .image().update(i -> {
                        if(dialog.next != null){
                            i.setDrawable(dialog.next.image.get());
                        }
                    })
                    .size(iconXLarge)
                    .name("image");

                t.table(Styles.black6, cont -> {
                    cont.name = "content";

                    Label title = cont.labelWrap(() -> dialog.next != null ? dialog.next.title.get() : "")
                        .fill().name("title")
                        .get();

                    LabelStyle titleSt = title.getStyle();
                    titleSt.background = Styles.black3;

                    cont.row()
                        .image(Tex.whiteui).update(i -> {
                            if(dialog.next != null){
                                i.setColor(dialog.next.color.get());
                            }
                        })
                        .fillX().height(3f).pad(6f)
                        .name("separator");

                    Label content = cont.row().labelWrap(() -> dialog.next != null ? dialog.next.content.get() : "")
                        .fillX().growY().name("content")
                        .get();

                    LabelStyle contentSt = content.getStyle();
                    contentSt.background = Styles.black3;
                    contentSt.font = Fonts.tech;
                }).fillX().growY();
            })
            .width(65f * 5)
            .fillY()
            .update(t -> {
                if(dialog.next != null && !dialog.next.done && !shown){
                    shown = true;
                    t.actions(
                        Actions.sizeBy(1f, 1f, 0.2f, Interp.pow3Out),
                        Actions.action(SpeechUpdateAction.class, SpeechUpdateAction::new).dialog(dialog.next)
                    );
                }

                if(dialog.next != null && dialog.next.done){
                    if(dialog.next.next != null){
                        SpeechDialog child = dialog.next.next;
                        dialog.next.parent = null;
                        dialog.next.next = null;

                        dialog.next = child;
                    }else if(shown){
                        shown = false;
                        t.actions(Actions.sizeBy(1f, 0f, 0.2f, Interp.pow3In));
                    }
                }
            })
            .name("speechdialog")
            .get();

        table.sizeBy(1f, 0f);
    }

    private SpeechDialog(){
        title = null;
        content = null;
        image = null;
        color = null;
    }

    protected SpeechDialog(SpeechDialog parent, Prov<CharSequence> title, String content, float speed, Prov<Drawable> image, Prov<Color> color){
        this.parent = parent;
        this.title = title;
        this.content = new SpeechProvider(content, speed);
        this.image = image;
        this.color = color;
    }

    public SpeechDialog show(Prov<CharSequence> title, String content, float speed, Prov<Drawable> image, Prov<Color> color){
        return last().next = new SpeechDialog(this, title, content, speed, image, color);
    }

    public SpeechDialog last(){
        SpeechDialog current = this;
        while(current.next != null){
            current = current.next;
        }

        return current;
    }

    public void clear(){
        if(next != null){
            next.clear();
            next = null;
        }
    }

    public static Table placement(){
        return ui.hudGroup
            .<Table>find("overlaymarker")
            .<Stack>find("waves/editor")
            .<Table>find("waves")
            .find(e -> e instanceof Table t && t.find("status") != null);
    }

    protected class SpeechProvider implements Prov<CharSequence>{
        public final String content;
        public final float speed;

        protected float last;
        protected int index = 0;

        protected Cons<Trigger> updater;

        protected SpeechProvider(String content, float speed){
            this.content = content;
            this.speed = speed;
        }

        protected void update(){
            if(index < content.length()){
                float elapsed = Time.time - last;
                if(elapsed >= speed){
                    Sounds.click.play();
                    index = speed <= 0f ? content.length() : Math.min(index + (int)(elapsed / speed), content.length());

                    last = Time.time;
                }
            }

            if(index >= content.length()){
                Triggers.detach(Trigger.update, updater);
                Time.run(Math.max(5f, content.length() / 20f) * Time.toSeconds, () -> done = true);
            }
        }

        protected void reset(){
            last = Time.time;
            index = 0;
            done = false;
        }

        @Override
        public String get(){
            return content.substring(0, index);
        }
    }

    protected static class SpeechUpdateAction extends Action{
        protected SpeechDialog dialog;

        @Override
        public boolean act(float delta){
            if(dialog != null && dialog.content != null){
                dialog.content.last = Time.time;
                dialog.content.updater = Triggers.listen(Trigger.update, dialog.content::update);
            }

            return true;
        }

        @Override
        public void reset(){
            super.reset();
            dialog = null;
        }

        protected SpeechUpdateAction dialog(SpeechDialog dialog){
            this.dialog = dialog;
            return this;
        }
    }
}
