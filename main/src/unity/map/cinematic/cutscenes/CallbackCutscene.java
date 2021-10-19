package unity.map.cinematic.cutscenes;

import arc.util.Time;
import unity.map.cinematic.Cutscene;

public class CallbackCutscene extends Cutscene {
    public float startDelay;
    public float endDelay;
    public float duration;

    public Runnable callback;
    private boolean called;
    private boolean callOnce;

    public CallbackCutscene(float startDelay, float endDelay, float duration, boolean callOnce, Runnable callback){
        this.startDelay = startDelay;
        this.endDelay = endDelay;
        this.duration = duration;
        this.callback = callback;
        this.callOnce = callOnce;
    }

    public CallbackCutscene(float duration, boolean callOnce, Runnable callback){
        this(0f, 0f, duration, callOnce, callback);
    }

    public CallbackCutscene(float duration, Runnable callback){
        this(0f, 0f, duration, true, callback);
    }

    public CallbackCutscene(Runnable callback){
        this(0f, 0f, 60f, true, callback);
    }

    @Override
    public boolean update() {
        float elapsed = Time.time - startTime();

        if(elapsed >= startDelay && elapsed <= startDelay + duration && !called){
            callback.run();
            called = callOnce;
        }

        return elapsed >= startDelay + duration + endDelay;
    }
}
