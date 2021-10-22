package unity.map.cinematic;

import arc.flabel.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import unity.ui.*;

import static mindustry.Vars.*;

public class Speeches{
    public static Speeches root;
    private static boolean shown;
    private static boolean initialized;
    private static FLabel label;

    public final SpeechData data;

    protected boolean ended = false, done = false;
    protected Speeches child;

    public static void init(){
        if(initialized) return;
        initialized = true;

        ui.hudGroup
            .<Table>find("overlaymarker")
            .<Stack>find("waves/editor")
            .<Table>find("waves")
            .<Table>find("statustable")
        .row().table(t -> {
            root = new Speeches();

            t.setClip(true);
            t.table(Styles.black3, header -> {
                header.image().update(i -> {
                    if(root.child != null){
                        i.setDrawable(root.child.data.image.get());
                    }
                }).pad(5f).size(iconXLarge + 10f);

                header.table(title -> {
                    title.label(() -> root.child != null ? root.child.data.title.get() : "")
                        .style(UnityStyles.speechtitlet)
                        .pad(5f).growY().fillY().get().setAlignment(Align.left);

                    title.row().image(Tex.whiteui).update(i -> {
                        if(root.child != null){
                            i.setColor(root.child.data.color.get());
                        }
                    }).growX().height(3f).pad(5f);
                }).pad(5f).grow();
            }).growX().fillY();

            t.row().table(Styles.black3, cont -> {
                label = cont.add(new FLabel("")).style(UnityStyles.speecht).pad(5f).grow().get();
                label.setAlignment(Align.left);
                label.setTypingListener(new FListener(){
                    @Override
                    public void event(String event){
                        Speeches current = root.child;
                        if(current != null && current.data.listener != null) current.data.listener.event(event);
                    }

                    @Override
                    public String replaceVariable(String variable){
                        Speeches current = root.child;
                        if(current != null && current.data.listener != null){
                            return current.data.listener.replaceVariable(variable);
                        }else{
                            return FListener.super.replaceVariable(variable);
                        }
                    }

                    @Override
                    public void onChar(char ch){
                        Speeches current = root.child;
                        if(current != null){
                            if(!Character.isWhitespace(ch)) current.data.sound.play();
                            if(current.data.listener != null) current.data.listener.onChar(ch);
                        }
                    }

                    @Override
                    public void end(){
                        Speeches current = root.child;
                        if(current != null){
                            if(!current.ended){
                                current.ended = true;
                                label.actions(Actions.delay(current.data.endDelay));
                            }

                            if(current.data.listener != null) current.data.listener.end();
                        }
                    }
                });
            }).pad(5f).grow();
        }).growX().height(200f).update(t -> {
            t.setOrigin(Align.topLeft);
            if(!(state.isPlaying() || state.isPaused())){
                root.child = null;

                label.restart("");
                label.pause();

                shown = false;
            }else{
                Speeches current = root.child;
                if(current != null && !current.done && !shown){
                    shown = true;
                    t.actions(
                        Actions.run(() -> {
                            label.restart(current.data.content);
                            label.pause();
                        }),
                        Actions.scaleTo(1f, 1f, 0.2f, Interp.pow3Out),
                        Actions.run(label::resume)
                    );
                }

                if(current != null && current.done){
                    if(current.child != null){
                        Speeches child = current.child;
                        current.child = null;

                        root.child = child;

                        label.restart(child.data.content);
                        label.resume();
                    }else if(shown){
                        shown = false;
                        t.actions(Actions.scaleTo(1f, 0f, 0.2f, Interp.pow3In));
                    }
                }

                if(root.child == null){
                    t.actions(Actions.scaleTo(1f, 0f));
                }
            }
        });
    }

    private Speeches(){
        data = null;
    }

    public Speeches(SpeechData data){
        this.data = data == null ? new SpeechData() : data;
    }

    public Speeches show(SpeechData data){
        return last().child = new Speeches(data);
    }

    public Speeches last(){
        Speeches current = this;
        while(current.child != null){
            current = current.child;
        }

        return current;
    }
}
