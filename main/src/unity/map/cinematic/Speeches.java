package unity.map.cinematic;

import arc.audio.*;
import arc.flabel.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import unity.ui.*;

import static mindustry.Vars.*;

public class Speeches{
    public static Speeches root;
    private static boolean shown;
    private static boolean initialized;
    private static FLabel label;

    public Prov<CharSequence> title = () -> "";
    public CharSequence content = "";
    public Sound sound = Sounds.click;
    public FListener listener;
    public Prov<Drawable> image = () -> Tex.clear;
    public Prov<Color> color = () -> Pal.accent;
    public float endDelay = 2f;

    protected boolean ended = false, finished = false;
    protected Speeches child;

    public static void init(){
        if(initialized) return;
        initialized = true;

        ui.hudGroup
            .<Table>find("overlaymarker")
            .<Stack>find("waves/editor")
            .<Table>find("waves")
            .<Table>find("statustable")
        .row().table(Tex.buttonRight, t -> {
            root = new Speeches();
            label = new FLabel(""){
                boolean gamePaused, lastPaused;

                {
                    setWrap(true);
                    setAlignment(Align.topLeft);
                    setStyle(UnityStyles.speecht);
                    setTypingListener(new FListener(){
                        @Override
                        public void event(String event){
                            Speeches current = root.child;
                            if(current != null && current.listener != null) current.listener.event(event);
                        }

                        @Override
                        public String replaceVariable(String variable){
                            Speeches current = root.child;
                            if(current != null && current.listener != null){
                                return current.listener.replaceVariable(variable);
                            }else{
                                return FListener.super.replaceVariable(variable);
                            }
                        }

                        @Override
                        public void onChar(char ch){
                            Speeches current = root.child;
                            if(current != null){
                                if(!Character.isWhitespace(ch)) current.sound.play();
                                if(current.listener != null) current.listener.onChar(ch);
                            }
                        }

                        @Override
                        public void end(){
                            Speeches current = root.child;
                            if(current != null){
                                if(!current.ended){
                                    current.ended = true;
                                    actions(
                                        Actions.delay(current.endDelay),
                                        Actions.run(() -> current.finished = true)
                                    );
                                }

                                if(current.listener != null) current.listener.end();
                            }
                        }
                    });
                }

                @Override
                public void act(float delta){
                    if(state.isPaused()){
                        if(!gamePaused){
                            gamePaused = true;
                            lastPaused = isPaused();
                            pause();
                        }
                    }else if(gamePaused){
                        gamePaused = false;
                        if(!lastPaused) resume();
                    }

                    super.act(delta);
                }

                @Override
                public void resume(){
                    if(gamePaused){
                        lastPaused = false;
                    }else{
                        super.resume();
                    }
                }
            };

            t.margin(5f);
            t.setClip(true);
            t.table(Styles.black3, header -> {
                header.image().update(i -> {
                    if(root.child != null){
                        Drawable icon = root.child.image.get();
                        i.setDrawable(icon == Tex.clear ? Styles.black5 : icon);
                    }else{
                        i.setDrawable(Styles.black5);
                    }
                }).pad(5f).size(iconXLarge);

                header.table(title -> {
                    title.table(up -> {
                        up.label(() -> root.child != null ? root.child.title.get() : "")
                            .style(UnityStyles.speechtitlet).grow().padRight(5f)
                            .get().setAlignment(Align.left);

                        up.button(Icon.play, Styles.emptyi, label::skipToTheEnd)
                            .fill().align(Align.topRight)
                            .get().setDisabled(label::hasEnded);
                    }).pad(5f).grow();

                    title.row().image(Tex.whiteui).update(i -> {
                        if(root.child != null){
                            i.setColor(root.child.color.get());
                        }
                    }).growX().height(3f).pad(5f);
                }).pad(5f).grow();
            }).pad(5f).growX().fillY();

            t.row().table(Styles.black3, cont -> cont.add(label).pad(5f).grow()).pad(5f).grow();
        }).width(320f).minHeight(200f).fillY().align(Align.topLeft).update(t -> {
            t.setOrigin(Align.topLeft);
            if(!(state.isPlaying() || state.isPaused())){
                root.child = null;

                label.restart("");
                label.pause();

                shown = false;
            }else{
                Speeches current = root.child;
                if(current != null && !current.finished && !shown){
                    shown = true;
                    t.actions(
                        Actions.run(() -> {
                            label.restart(current.content);
                            label.pause();
                        }),
                        Actions.scaleTo(1f, 1f, 0.2f, Interp.pow3Out),
                        Actions.run(label::resume)
                    );
                }

                if(current != null && current.finished){
                    if(current.child != null){
                        Speeches child = current.child;
                        current.child = null;

                        root.child = child;

                        label.restart(child.content);
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

    public Speeches show(Cons<Speeches> cons){
        Speeches child = new Speeches();
        cons.get(child);

        return show(child);
    }

    public Speeches show(Speeches child){
        return last().child = child;
    }

    public Speeches last(){
        Speeches current = this;
        while(current.child != null){
            current = current.child;
        }

        return current;
    }

    public Speeches setTitle(CharSequence title){
        return setTitle(() -> title);
    }

    public Speeches setTitle(Prov<CharSequence> title){
        this.title = title;
        return this;
    }

    public Speeches setContent(CharSequence content){
        this.content = content;
        return this;
    }

    public Speeches setSound(Sound sound){
        this.sound = sound;
        return this;
    }

    public Speeches setListener(FListener listener){
        this.listener = listener;
        return this;
    }

    public Speeches setImage(TextureRegion region){
        var image = new TextureRegionDrawable(region);
        return setImage(() -> image);
    }

    public Speeches setImage(Drawable image){
        return setImage(() -> image);
    }

    public Speeches setImage(Prov<Drawable> image){
        this.image = image;
        return this;
    }

    public Speeches setColor(Color color){
        return setColor(() -> color);
    }

    public Speeches setColor(Prov<Color> color){
        this.color = color;
        return this;
    }

    public Speeches setDelay(float endDelay){
        this.endDelay = endDelay;
        return this;
    }
}
