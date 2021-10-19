package unity.map.cinematic.cutscenes;

import arc.util.Time;
import unity.map.cinematic.Cutscene;

public class CallbackCutscene extends Cutscene {
    public float delay;
    public float duration;

    public Runnable callback;
    private boolean called;

    public CallbackCutscene(float delay, float duration, Runnable callback){
        this.delay = delay;
        this.duration = duration;
        this.callback = callback;
    }

    public CallbackCutscene(float duration, Runnable callback){
        this(0f, duration, callback);
    }

    @Override
    public boolean update() {
        float elapsed = Time.time - startTime();

        if(elapsed >= delay && !called){
            callback.run();
            called = true;
        }

        return elapsed >= delay + duration;
    }
}
