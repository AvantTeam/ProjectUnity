package unity.cinematic;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import unity.mod.*;

import static mindustry.Vars.*;

public class SpeechDialog extends Table{
    public SpeechUpdater update;

    protected boolean shown;
    protected Cons<Trigger> updater;

    public SpeechDialog(String title, String content, TextureRegion image){
        this(() -> title, content, () -> image, Pal.accent);
    }

    public SpeechDialog(Prov<CharSequence> title, String content, Prov<TextureRegion> image, Color color){
        defaults().pad(6f);
        background(Tex.wavepane);

        left().top().image(image).size(iconXLarge).fill().name("image");
        table(t -> {
            t.add(new Label(title){{
                setWrap(true);
                background(Styles.black3);
            }})
                .update(l -> l.setColor(color)).name("title")
                .grow();

            t.row()
                .image(Tex.whiteui, color).name("separator")
                .growX()
                .height(3f)
                .pad(6f);

            t.row()
                .labelWrap(update = new SpeechUpdater(content, 1f){{
                    background(Styles.black3);
                }}).name("content")
                .grow();
        }).grow();

        visible = false;
        updater = Triggers.cons(update::update);
    }

    public void show(){
        if(!shown){
            shown = true;
            update.reset();
            Triggers.listen(Trigger.update, updater);

            actions(
                Actions.sizeBy(1f, 0f, 0f),
                Actions.visible(true),
                Actions.sizeBy(1f, 1f, 0.12f, Interp.pow3Out)
            );

            placement().row().add(left()).width(65f * 5 + 4f).pad(0f);
        }
    }

    public void hide(){
        if(shown){
            shown = false;
            Triggers.detach(Trigger.update, updater);

            actions(
                Actions.sizeBy(1f, 0f, 0.12f, Interp.pow3In),
                Actions.visible(false),
                Actions.remove()
            );
        }
    }

    public static Table placement(){
        return ui.hudGroup
            .<Table>find("overlaymarker")
            .<Stack>find("waves/editor")
            .<Table>find("waves")
            .<Table>find(e -> e instanceof Table t && t.find("status") != null)
            .find("status");
    }

    public class SpeechUpdater implements Prov<CharSequence>{
        public final String content;
        public final float speed;

        protected float last = Time.time;
        protected int index = 0;
        protected boolean done;

        public SpeechUpdater(String content, float speed){
            this.content = content;
            this.speed = speed;
        }

        public void update(){
            if(index < content.length()){
                float elapsed = Time.time - last;
                if(elapsed >= speed){
                    Sounds.click.play();
                    index = speed <= 0f ? content.length() : Math.min(index + (int)(elapsed / speed), content.length());

                    last = Time.time;
                }
            }

            if(index >= content.length() && !done){
                done = true;
                Time.run(5f * Time.toSeconds, SpeechDialog.this::hide);
            }
        }

        public void reset(){
            last = Time.time;
            index = 0;
            done = false;
        }

        @Override
        public String get(){
            return content.substring(0, index);
        }
    }
}
