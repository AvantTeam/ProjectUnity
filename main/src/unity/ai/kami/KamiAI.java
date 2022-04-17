package unity.ai.kami;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.ai.kami.KamiPattern.*;
import unity.util.*;

import java.util.*;

public class KamiAI implements UnitController{
    public static final float minRange = 350f, barrierRange = 800f;
    protected static boolean allPatterns = true;
    private static final Vec2 vec = new Vec2();
    private static KamiPattern testPattern;
    private static final int[] limit = new int[PatternType.values().length];

    public Unit unit;
    public Unit target;
    public KamiPattern pattern;
    public PatternData patternData;
    public float[] reloads = new float[16];
    public int difficulty = 0, stages = 0;
    public float x, y;
    public float patternTime, waitTime = 2f * 60f;
    public Rand rand = new Rand();

    protected Seq<KamiDelay> delays = new Seq<>();
    protected Seq<KamiPattern> patterns = new Seq<>();

    static{
        KamiPatterns.load();
        //testPattern = KamiPatterns.hyperSpeedPattern;
    }

    public void draw(){
        float z = Draw.z();
        Draw.z(Layer.flyingUnit);
        Lines.stroke(3f + Mathf.absin(12f, 1f));
        Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time));
        Draw.blend(Blending.additive);
        Lines.circle(x, y, barrierRange);
        if(pattern != null && waitTime <= 0f){
            pattern.draw(this);
        }
        Draw.blend();
        Draw.reset();
        Draw.z(z);
    }

    public void updateFollowing(){
        float range = pattern != null && pattern.followTarget ? pattern.followRange : minRange;
        vec.trns(target.angleTo(unit), range).add(target).sub(unit).scl(0.05f * Time.delta);
        unit.move(vec);
        unit.lookAt(target);
    }

    @Override
    public void updateUnit(){
        if(target != null && Units.invalidateTarget(target, unit.team, unit.x, unit.y)){
            target = null;
        }
        if(target == null){
            Player player = Utils.bestEntity(Groups.player, p -> p.unit() != null && p.unit().isValid(),
            p -> -p.dst(unit));
            target = player.unit();
        }
        if((waitTime > 0f || (pattern != null && pattern.followTarget)) && target != null){
            float speed = patternTime <= 0f ? Mathf.clamp(waitTime / 40f) : 1f;
            float range = pattern != null && pattern.followTarget ? pattern.followRange : minRange;
            vec.trns(target.angleTo(unit), range).add(target).sub(unit).scl(0.05f * speed * Time.delta);
            unit.move(vec);
            unit.lookAt(target);
            if(patternTime <= 0f){
                vec.set(x, y).lerpDelta(target.x, target.y, 0.1f * speed);
                x = vec.x;
                y = vec.y;
            }
        }
        if(target != null && waitTime <= 0f){
            if(pattern == null){
                reset();
            }
            if(pattern != null){
                if(pattern.lootAtTarget){
                    unit.lookAt(target);
                }
                pattern.update(this);

                delays.removeAll(k -> {
                    k.delay -= Time.delta;
                    boolean b = k.delay <= 0f;
                    if(b){
                        k.run.run();
                        Pools.free(k);
                    }
                    return b;
                });

                patternTime -= Time.delta;
                if(patternTime <= 0f){
                    waitTime = pattern.waitTime;
                    pattern.end(this);
                    pattern = null;
                    patternData = null;
                }
            }
        }
        waitTime = Math.max(0f, waitTime - Time.delta);

        updateBarrier();
    }

    public float pTime(){
        return pattern == null ? 0f : pattern.time - patternTime;
    }

    void reset(){
        Arrays.fill(reloads, 0f);
        for(KamiDelay delay : delays){
            Pools.free(delay);
        }
        delays.clear();
        if(testPattern == null){
            if(patterns.isEmpty()){
                Arrays.fill(limit, 0);
                for(KamiPattern p : KamiPattern.all){
                    if(allPatterns || p.type.able.get(this)) patterns.add(p);
                }
                patterns.shuffle();
                if(!allPatterns) patterns.removeAll(p -> limit[p.type.ordinal()]++ >= p.type.limit);
                patterns.sort(p -> p.type.priority);
                /*
                StringBuilder sp = new StringBuilder();
                for(KamiPattern p : patterns){
                    sp.append(p.toString()).append("\n");
                }
                Unity.print(sp.toString());
                */
            }
            pattern = patterns.first();
            patterns.remove(0);
        }else{
            pattern = testPattern;
        }
        if(pattern.data != null) patternData = pattern.data.get();
        pattern.init(this);
        patternTime = pattern.time;

        stages++;
    }

    void updateBarrier(){
        for(Player p : Groups.player){
            Unit u = p.unit();
            if(p.unit().isValid() && !Mathf.within(x, y, u.x, u.y, barrierRange)){
                vec.set(u).sub(x, y).limit(barrierRange).add(x, y);
                u.set(vec);
            }
        }
    }

    public boolean burst(int i, float time, int bursts, float burstSpacing, Runnable begin){
        boolean s = shoot(i, burstSpacing);
        if(s){
            if(reloads[i + 1] <= 0f){
                begin.run();
            }
            reloads[i + 1] += 1f;
            if(reloads[i + 1] >= bursts){
                reloads[i] += time;
                reloads[i + 1] = 0f;
            }
        }
        return s;
    }

    public boolean burst(int i, float time, int bursts, float burstSpacing){
        boolean s = shoot(i, burstSpacing);
        if(s){
            reloads[i + 1] += 1f;
            if(reloads[i + 1] >= bursts){
                reloads[i] += time;
            }
        }
        return s;
    }

    public boolean shoot(int i, float time){
        boolean s = reloads[i] <= 0f;
        if(s) reloads[i] += time;
        reloads[i] -= Time.delta;
        return s;
    }

    public float targetAngle(){
        return unit.angleTo(target);
    }

    public void run(float delay, Runnable run){
        if(delay <= 0f){
            run.run();
            return;
        }
        KamiDelay k = Pools.obtain(KamiDelay.class, KamiDelay::new);
        k.delay = delay;
        k.run = run;
        delays.add(k);
    }

    @Override
    public void unit(Unit unit){
        this.unit = unit;
        x = unit.x;
        y = unit.y;
        rand.setSeed(unit.id * 9999L);
    }

    @Override
    public Unit unit(){
        return unit;
    }

    static class KamiDelay implements Poolable{
        Runnable run;
        float delay;

        @Override
        public void reset(){
            run = null;
            delay = 0f;
        }
    }
}
