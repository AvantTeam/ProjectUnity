package unity.ai.kami;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.ai.kami.KamiPattern.*;
import unity.util.*;

import java.util.*;

public class KamiAI implements UnitController{
    public static final float minRange = 350f, barrierRange = 800f;
    private static final Vec2 vec = new Vec2();

    public Unit unit;
    public Unit target;
    public KamiPattern pattern;
    public PatternData patternData;
    public float[] reloads = new float[16];
    public int difficulty = 0;
    public float x, y;
    public float patternTime, waitTime = 2f * 60f;

    protected IntSeq patternSeq = new IntSeq(false, 16);

    static{
        KamiPatterns.load();
    }

    public void draw(){
        float z = Draw.z();
        Draw.z(Layer.flyingUnit);
        Lines.stroke(3f + Mathf.absin(12f, 1f));
        Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time));
        Lines.circle(x, y, barrierRange);
        if(pattern != null && waitTime <= 0f){
            pattern.draw(this);
        }
        Draw.z(z);
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
            vec.trns(target.angleTo(unit), range).add(target).sub(unit).scl(0.05f * speed);
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

    void reset(){
        Arrays.fill(reloads, 0f);
        if(patternSeq.isEmpty()){
            for(KamiPattern p : KamiPattern.all){
                patternSeq.add(p.id);
            }
        }
        int id = Mathf.random(0, patternSeq.size - 1);
        pattern = KamiPattern.all.get(patternSeq.get(id));
        if(pattern.data != null) patternData = pattern.data.get();
        pattern.init(this);
        patternTime = pattern.time;
        patternSeq.removeIndex(id);
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

    public boolean shoot(int i, float time){
        boolean s = reloads[i] <= 0f;
        if(s) reloads[i] += time;
        reloads[i] -= Time.delta;
        return s;
    }

    public float targetAngle(){
        return unit.angleTo(target);
    }

    @Override
    public void unit(Unit unit){
        this.unit = unit;
        x = unit.x;
        y = unit.y;
    }

    @Override
    public Unit unit(){
        return unit;
    }
}
