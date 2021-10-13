package unity.map.cinematic;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.ui.layout.*;
import arc.util.*;

import static mindustry.Vars.*;

/**
 * Cutscene is a chain of input-locking actions ran one after another. {@code Cutscene.root.then(...)} is typically
 * used as a startup, as {@link #then(Cutscene)} itself returns the given cutscene, allowing for chained operations.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class Cutscene{
    public static Cutscene root;
    public static float stripe = Scl.scl(56f);
    private float progress, zoomProgress;

    private final boolean isRoot;
    private boolean acting;

    protected Floatf<Cutscene> zoom = c -> Scl.scl(4f);
    protected Cutscene parent;
    protected Cutscene child;

    private Cons<Cutscene> start;
    private Cons<Cutscene> end;

    private Vec2 endPos;
    private float endTime = -1f;
    private float firstZoom = -1f, lastZoom = -1f;
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

    public <T extends Cutscene> T then(T next){
        var target = this;
        while(target.child != null){
            target = target.child;
        }

        next.parent = target;
        var cur = target;
        while(cur != null && cur != root){
            if(cur == next) throw new IllegalArgumentException("Can't invoke then() to a parent cutscene!");
            cur = cur.parent;
        }

        target.child = next;
        return next;
    }

    public <T extends Cutscene> T start(Cons<T> start){
        this.start = (Cons<Cutscene>)start;
        return (T)this;
    }

    public <T extends Cutscene> T end(Cons<T> end){
        this.end = (Cons<Cutscene>)end;
        return (T)this;
    }

    public <T extends Cutscene> T zoom(Floatf<T> zoom){
        this.zoom = (Floatf<Cutscene>)zoom;
        return (T)this;
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
                if(!acting) ui.hudGroup.actions(Actions.alpha(0f, 0.17f));
                if(firstZoom < 0f) firstZoom = renderer.getScale();
                if(lastZoom < 0f) lastZoom = renderer.getScale();
                acting = true;
            }

            endPos = null;
            endTime = -1f;

            progress = Mathf.approachDelta(progress, 1f, 1f / 60f);
            zoomProgress = Mathf.approachDelta(zoomProgress, 1f, 1f / 48f);

            float targetZoom = child.zoom();
            renderer.setScale(Mathf.lerp(lastZoom, targetZoom, zoomProgress));

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

                lastZoom = targetZoom;
                zoomProgress = 0f;
                child = child.child;
            }
        }else{
            progress = Mathf.approachDelta(progress, 0f, 1f / 60f);
            zoomProgress = Mathf.approachDelta(zoomProgress, 0f, 1f / 60f);

            if(firstZoom > 0f){
                renderer.setScale(Mathf.lerp(firstZoom, renderer.getScale(), zoomProgress));
                if(Mathf.equal(firstZoom, renderer.getScale())){
                    firstZoom = -1f;
                    lastZoom = -1f;
                    zoomProgress = 0f;
                }
            }

            if(acting){
                if(endPos == null){
                    endPos = new Vec2(Core.camera.position);
                    endTime = Time.time;
                }

                float endProgress = Time.time - endTime;

                Core.camera.position.set(endPos).lerp(player, Interp.smoother.apply(Mathf.clamp(endProgress / 52f)));
                if(endProgress >= 52f){
                    ui.hudGroup.actions(Actions.alpha(1f, 0.17f));

                    zoomProgress = 0f;
                    firstZoom = -1f;
                    lastZoom = -1f;
                    acting = false;
                }
            }
        }
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

    public float zoom(){
        return zoom.get(this);
    }

    public interface Pos{
        long get();
    }
}
