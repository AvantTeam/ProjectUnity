package unity.map.cinematic;

import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.util.*;

/** @author GlennFolker */
public class Cutscene{
    public static Cutscene root;

    private final boolean isRoot;

    private Cutscene child;

    private final Boolf<Cutscene> action;
    private Cons<Cutscene> start;
    private Cons<Cutscene> end;

    private float startTime = -1f;

    public static void init(){
        root = new Cutscene();
        Core.scene.add(new Element(){
            @Override
            public void act(float delta){
                root.update();
            }
        });
    }

    private Cutscene(){
        isRoot = true;
        action = null;
    }

    public Cutscene(Boolf<Cutscene> action){
        isRoot = false;
        this.action = action;
    }

    public Cutscene show(Cutscene next){
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

    /** @return Whether this cutscene is finished. */
    public boolean update(){
        if(isRoot){
            if(child != null){
                if(child.startTime < 0f) child.startTime = Time.time;

                if(child.start != null){
                    child.start.get(child);
                    child.start = null;
                }

                //TODO input lock. requires newer mindustry version
                if(child.update()){
                    if(child.end != null){
                        child.end.get(child);
                        child.end = null;
                    }

                    child = child.child;
                }
            }

            return false;
        }else{
            return action == null || action.get(this);
        }
    }

    public float startTime(){
        return startTime;
    }
}
