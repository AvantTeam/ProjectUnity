package unity.ai;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.ai.kami.*;
import unity.ai.kami.KamiPatterns.*;
import unity.entities.units.*;

import java.util.*;

public class NewKamiAI implements UnitController, Position{
    private static final Vec2 tmpVec = new Vec2();
    private final Color tcolor = new Color();

    public Unit unit;
    public Unit target;
    public Vec2 targetPos = new Vec2(), movePos = new Vec2();
    public float[] reloads = new float[16];
    public int difficulty = 0;
    public float patternTime, stageTime, stageDamage, moveTime, waitTime;
    public int stage = 0;
    public float drawIn = 0f, relativeRotation = 90f;
    public boolean reseting = true, waiting = true;

    Interval timer = new Interval(2);
    protected IntSeq spellSeq = new IntSeq(), nonSpellSeq = new IntSeq();
    protected Rect barrier = new Rect();
    protected boolean barrierActive = false;

    public KamiPattern pattern;
    public boolean spell = false;

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }

    @Override
    public Unit unit(){
        return unit;
    }

    @Override
    public float getX(){
        return unit.x;
    }

    @Override
    public float getY(){
        return unit.y;
    }

    public KamiUnit kami(){
        return unit.as();
    }

    public void draw(){
        Draw.color(tcolor.set(Color.red).shiftHue(Time.time * 3f));
        Draw.blend(Blending.additive);
        if(barrierActive){
            Lines.stroke(3f);
            Lines.rect(barrier.x, barrier.y, barrier.width, barrier.height);
        }
        if(patternTime > 0f){
            Lines.stroke(3f * drawIn);
            Lines.circle(getX(), getY(), (1f - (patternTime / pattern.time)) * 240f);
        }
        Draw.z(Layer.flyingUnitLow);
        if(pattern != null) pattern.draw(this);
        Draw.blend();
    }

    @Override
    public void updateUnit(){
        if(!reseting) drawIn = Mathf.clamp(drawIn + (Time.delta / 180f));
        if(timer.get(40f) && target == null && unit != null){
            Unit next = null;
            float dst = 0f;
            for(Unit e : Groups.unit){
                if(e.controller() instanceof Player && (next == null || dst < unit.dst(e))){
                    next = e;
                    target = e;
                    targetPos.set(e);
                    dst = unit.dst(e);
                }
            }
        }
        if(target != null && !barrierActive){
            barrier.setCentered(targetPos.x, targetPos.y, 300f * 2f, 340f * 2f);
            barrierActive = true;
        }
        if(barrierActive && (timer.get(1, 2f) || Groups.unit.size() < 15)){
            for(Unit e : Groups.unit){
                if(e != unit){
                    if(e.x < barrier.x) e.x += barrier.x - e.x;
                    if(e.y < barrier.y) e.y += barrier.y - e.y;
                    if(e.x > barrier.x + barrier.width) e.x += (barrier.x + barrier.width) - e.x;
                    if(e.y > barrier.y + barrier.height) e.y += (barrier.y + barrier.height) - e.y;
                }
            }
        }
        if(target instanceof Unit e && !(e.controller() instanceof Player)) target = null;
        if(spellSeq.isEmpty()){
            for(int i = 0; i < KamiPatterns.majorPatterns.length; i++){
                spellSeq.add(i);
            }
        }
        if(nonSpellSeq.isEmpty()){
            for(int i = 0; i < KamiPatterns.minorPatterns.length; i++){
                nonSpellSeq.add(i);
            }
        }
        if(moveTime > 0){
            unit.move(movePos.x, movePos.y);
            moveTime -= Time.delta;
        }
        if(target != null) updateBulletHell();
    }

    public void moveAround(float moveTime){
        this.moveTime = moveTime;
        movePos.trns(relativeRotation + (Mathf.randomSign() * 90f) + Mathf.range(4f), 45f + Mathf.range(20f)).scl(1f / moveTime);
    }

    void updateBulletHell(){
        if(reseting){
            if(spell){
                int rand = Mathf.random(0, spellSeq.size - 1);
                int t = spellSeq.get(rand);
                pattern = KamiPatterns.majorPatterns[t];
                spellSeq.removeIndex(rand);
                if(spellSeq.isEmpty()) difficulty++;
                spell = false;
            }else{
                int rand = Mathf.random(0, nonSpellSeq.size - 1);
                int t = nonSpellSeq.get(rand);
                pattern = KamiPatterns.minorPatterns[t];
                nonSpellSeq.removeIndex(rand);
                spell = true;
            }
            waiting = true;
            waitTime = 0f;
            reseting = false;
        }
        if(waiting){
            tmpVec.trns(relativeRotation, 270f).add(targetPos).sub(unit).scl(1f / 15f);
            unit.move(tmpVec.x, tmpVec.y);
            waitTime += Time.delta;
            float maxTime = spell ? 2f * 60f : 5f * 60f;
            if(waitTime >= maxTime){
                pattern.init(this);
                waiting = false;
            }
        }
        if(pattern != null && !waiting){
            pattern.update(this);
            patternTime += Time.delta;
            if(patternTime >= pattern.time || (pattern.maxDamage != Float.MAX_VALUE && stageDamage >= pattern.maxDamage)){
                reset();
            }
        }
    }

    void reset(){
        Arrays.fill(reloads, 0f);
        stageDamage = stageTime = patternTime = moveTime = drawIn = 0f;
        kami().laserRotation = 0f;
        kami().laser = null;
        stage = 0;
        reseting = true;
    }
}
