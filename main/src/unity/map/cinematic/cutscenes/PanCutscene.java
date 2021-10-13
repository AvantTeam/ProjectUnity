package unity.map.cinematic.cutscenes;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import unity.gen.*;
import unity.map.cinematic.*;

/**
 * A type of {@link Cutscene} that interpolates the camera position to the target position, and stays for a specified delay.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class PanCutscene extends Cutscene{
    public final Pos pos;
    public final Interp interp;
    public final float delay, panDuration, endDelay;

    private Vec2 initPos;
    private float initTime = -1f;

    private Cons<PanCutscene> moved;
    private Cons<PanCutscene> arrived;
    private float movedThreshold = 5f, arrivedThreshold = 8f;

    public PanCutscene(Pos pos, float delay, float panDuration, float endDelay, Interp interp){
        this.pos = pos;
        this.interp = interp;
        this.delay = delay;
        this.panDuration = panDuration;
        this.endDelay = endDelay;
    }

    public PanCutscene(Pos pos){
        this(pos, 15f, 90f, 60f, Interp.smoother);
    }

    public PanCutscene(Position pos){
        this(() -> Float2.construct(pos.getX(), pos.getY()));
    }

    public PanCutscene(Pos pos, float panDuration){
        this(pos, 15f, panDuration, 60f, Interp.smooth2);
    }

    public PanCutscene(Position pos, float panDuration){
        this(() -> Float2.construct(pos.getX(), pos.getY()), 15f, panDuration, 60f, Interp.smooth2);
    }

    public PanCutscene(Pos pos, float panDuration, Interp interp){
        this(pos, 15f, panDuration, 60f, interp);
    }

    public PanCutscene(Position pos, float panDuration, Interp interp){
        this(() -> Float2.construct(pos.getX(), pos.getY()), 15f, panDuration, 60f, interp);
    }

    public PanCutscene(Position pos, float delay, float panDuration, float endDelay, Interp interp){
        this(() -> Float2.construct(pos.getX(), pos.getY()), delay, panDuration, endDelay, interp);
    }

    public <T extends PanCutscene> T moved(Cons<T> moved, float threshold){
        this.moved = (Cons<PanCutscene>)moved;
        movedThreshold = threshold;

        return (T)this;
    }

    public <T extends PanCutscene> T arrived(Cons<T> arrived, float threshold){
        this.arrived = (Cons<PanCutscene>)arrived;
        arrivedThreshold = threshold;

        return (T)this;
    }

    @Override
    public boolean update(){
        // Time since started.
        float elapsed = Time.time - startTime();

        // If has delayed long enough within a threshold, notify that it has moved.
        if(moved != null && elapsed >= delay - movedThreshold){
            moved.get(this);
            moved = null;
        }

        // If has delayed long enough, initialize movement.
        // `initPos` being null indicates that it hasn't moved.
        if(elapsed >= delay && initPos == null){
            initPos = new Vec2(Core.camera.position);
            // Since the elapsed time isn't guaranteed to be 0, `initTime` is used to prevent camera jag when interpolating.
            initTime = Time.time;
        }

        // Calculate the elapsed time since first moving.
        float progress = Time.time - initTime;
        if(initPos != null){
            // Actually interpolate the camera position from start to end, with an interpolator.
            long pos = this.pos.get();
            Core.camera.position.set(initPos).lerp(
                Float2.x(pos), Float2.y(pos),
                interp.apply(Mathf.clamp(progress / panDuration))
            );

            // If has delayed long enough since moved, notify that it has arrived.
            if(arrived != null && progress >= panDuration - arrivedThreshold){
                arrived.get(this);
                arrived = null;
            }
        }

        // If the elapsed time is larger than the pan duration and the end delay combined, it's finished.
        return initTime > -1f && progress > panDuration + endDelay;
    }
}
