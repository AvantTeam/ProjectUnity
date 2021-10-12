package unity.map.cinematic;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import unity.gen.*;

import static mindustry.Vars.*;

/**
 * Cutscene is a chain of input-locking actions ran one after another. {@code Cutscene.root.then(...)} is typically
 * used as a startup, as {@link #then(Cutscene)} itself returns the given cutscene, allowing for chained operations.
 * @author GlennFolker
 */
public class Cutscene{
    public static Cutscene root;
    public static float stripe = Scl.scl(50f);
    private static float progress;

    private final boolean isRoot;
    private boolean acting;

    private Cutscene parent;
    private Cutscene child;

    private Cons<Cutscene> start;
    private Cons<Cutscene> end;

    private float startTime = -1f;

    public static void init(){
        new Cutscene(true);
        Core.scene.add(new Element(){
            @Override
            public void act(float delta){
                setZIndex(ui.hudGroup.getZIndex() + 1);
                root.updateRoot();
            }

            @Override
            public void draw(){
                root.drawStripes();
            }
        });
    }

    private Cutscene(boolean isRoot){
        if(!isRoot){
            this.isRoot = false;
        }else if(root == null){
            root = this;
            this.isRoot = true;

            control.input.addLock(this::acting);
        }else{
            throw new IllegalArgumentException("Root cutscene already defined!");
        }
    }

    public Cutscene(){
        this(false);
    }

    public Cutscene then(Cutscene next){
        if(child != null) throw new IllegalArgumentException("Already has a child!");

        next.parent = this;
        var cur = this;
        while(cur != null && cur != root){
            if(cur == next) throw new IllegalArgumentException("Can't invoke then() to a parent cutscene!");
            cur = cur.parent;
        }

        child = next;
        return next;
    }

    public Cutscene start(Cons<Cutscene> start){
        this.start = start;
        return this;
    }

    public Cutscene end(Cons<Cutscene> end){
        this.end = end;
        return this;
    }

    private void drawStripes(){
        Draw.color(Color.black);

        var pos = Core.scene.screenToStageCoordinates(Tmp.v1.set(0f, Core.graphics.getHeight()));
        float
            x = pos.x,
            y = pos.y,
            w = Core.graphics.getWidth(),
            h = Core.graphics.getHeight(),
            thick = stripe * progress;

        Fill.quad(
            x, y,
            x + w, y,
            x + w, y - thick,
            x, y - thick
        );
        Fill.quad(
            x, y - h,
            x + w, y - h,
            x + w, y - h + thick,
            x, y - h + thick
        );
    }

    private void updateRoot(){
        if(!isRoot) throw new IllegalStateException();

        if(child != null){
            if(control.input.locked() && !acting){
                // Assume other mods are doing their own cutscenes if input is locked but this root's cutscene isn't acting.
                // This will only work if other mods do the same thing...
                return;
            }else{
                acting = true;
            }

            if(child.startTime < 0f) child.startTime = Time.time;

            if(child.start != null){
                child.start.get(child);
                child.start = null;
            }

            if(child.update()){
                if(child.end != null){
                    child.end.get(child);
                    child.end = null;
                }

                child = child.child;
            }
        }else{
            acting = false;
        }

        progress = Mathf.approachDelta(progress, acting ? 1f : 0f, 1f / 60f);
    }

    /**
     * @return Whether this cutscene is done acting. If it's the case, the {@link #root} cutscene will erase this one
     * and move on to the next.
     */
    public boolean update(){
        return true;
    }

    public float startTime(){
        return startTime;
    }

    public boolean acting(){
        return root.acting;
    }

    public static PanCutscene pan(Pos pos){
        return pan(pos, 15f, 90f, 60f, Interp.smoother);
    }

    public static PanCutscene pan(Position pos){
        return pan(() -> Float2.construct(pos.getX(), pos.getY()));
    }

    public static PanCutscene pan(Pos pos, float panDuration){
        return pan(pos, 15f, panDuration, 60f, Interp.smooth2);
    }

    public static PanCutscene pan(Position pos, float panDuration){
        return pan(() -> Float2.construct(pos.getX(), pos.getY()), 15f, panDuration, 60f, Interp.smooth2);
    }

    public static PanCutscene pan(Pos pos, float panDuration, Interp interp){
        return pan(pos, 15f, panDuration, 60f, interp);
    }

    public static PanCutscene pan(Position pos, float panDuration, Interp interp){
        return pan(() -> Float2.construct(pos.getX(), pos.getY()), 15f, panDuration, 60f, interp);
    }

    public static PanCutscene pan(Pos pos, float delay, float panDuration, float endDelay, Interp interp){
        return new PanCutscene(pos, delay, panDuration, endDelay, interp);
    }

    public static PanCutscene pan(Position pos, float delay, float panDuration, float endDelay, Interp interp){
        return pan(() -> Float2.construct(pos.getX(), pos.getY()), delay, panDuration, endDelay, interp);
    }

    public static class PanCutscene extends Cutscene{
        public final Pos pos;
        public final Interp interp;
        public final float delay, panDuration, endDelay;

        private Vec2 initPos;
        private float initTime = -1f;

        private Cons<Cutscene> moved;
        private Cons<Cutscene> arrived;
        private float movedThreshold = 5f, arrivedThreshold = 8f;

        public PanCutscene(Pos pos, float delay, float panDuration, float endDelay, Interp interp){
            this.pos = pos;
            this.interp = interp;
            this.delay = delay;
            this.panDuration = panDuration;
            this.endDelay = endDelay;
        }

        public PanCutscene moved(Cons<Cutscene> moved, float threshold){
            this.moved = moved;
            movedThreshold = threshold;

            return this;
        }

        public PanCutscene arrived(Cons<Cutscene> arrived, float threshold){
            this.arrived = arrived;
            arrivedThreshold = threshold;

            return this;
        }

        @Override
        public boolean update(){
            float elapsed = Time.time - startTime();
            if(moved != null && elapsed >= delay - movedThreshold){
                moved.get(this);
                moved = null;
            }

            if(elapsed >= delay && initPos == null){
                initPos = new Vec2(Core.camera.position);
                initTime = Time.time;
            }

            float progress = Time.time - initTime;
            if(initPos != null){
                long pos = this.pos.get();
                Core.camera.position.set(initPos).lerp(
                    Float2.x(pos), Float2.y(pos),
                    interp.apply(Mathf.clamp(progress / panDuration))
                );

                if(arrived != null && progress >= panDuration - arrivedThreshold){
                    arrived.get(this);
                    arrived = null;
                }
            }

            return initTime > -1f && progress > panDuration + endDelay;
        }
    }

    public interface Pos{
        long get();
    }
}
