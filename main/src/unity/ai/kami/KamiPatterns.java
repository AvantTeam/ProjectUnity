package unity.ai.kami;

import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import unity.ai.*;
import unity.ai.kami.KamiBulletDatas.*;
import unity.content.*;

import java.util.*;

public class KamiPatterns{
    public static KamiPattern[] minorPatterns, majorPatterns, finalPatterns;
    private final static Color tCol = new Color();
    private final static Vec2 tVec = new Vec2();

    public static void addPattern(KamiPattern pattern, int type){
        if(type == 0){
            if(minorPatterns != null) minorPatterns = Arrays.copyOf(minorPatterns, minorPatterns.length + 1);
            if(minorPatterns == null) minorPatterns = new KamiPattern[1];
            minorPatterns[minorPatterns.length - 1] = pattern;
        }
        if(type == 1){
            if(majorPatterns != null) majorPatterns = Arrays.copyOf(majorPatterns, majorPatterns.length + 1);
            if(majorPatterns == null) majorPatterns = new KamiPattern[1];
            majorPatterns[majorPatterns.length - 1] = pattern;
        }
        if(type == 2){
            if(finalPatterns != null) finalPatterns = Arrays.copyOf(finalPatterns, finalPatterns.length + 1);
            if(finalPatterns == null) finalPatterns = new KamiPattern[1];
            finalPatterns[finalPatterns.length - 1] = pattern;
        }
        pattern.type = type;
    }

    public static void load(){
        addPattern(new KamiPattern(){{
            time = 30f * 60f;
            maxDamage = 2000f;
            stages = new KamiPatternStage[]{
                new KamiPatternStage(70f * 60f, ai -> {
                    int diff = 6 + (ai.difficulty * 2);
                    if(ai.reloads[0] >= 6f){
                        for(int i = 0; i < diff; i++){
                            float angle = (i * (360f / diff)) + ai.reloads[1];
                            UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle);
                        }
                        ai.reloads[0] = 0f;
                        ai.reloads[1] += (ai.reloads[2] % 16f >= 8f) ? 8f : -8f;
                        ai.reloads[2] += 1;
                    }
                    ai.reloads[0] += Time.delta;
                })
            };
        }}, 0);

        //Utsuho non spell pattern 3
        addPattern(new KamiPattern(){{
            time = 30f * 60f;
            maxDamage = 2000f;
            stages = new KamiPatternStage[]{
                new KamiPatternStage(5f * 60f, ai -> {
                    if(ai.reloads[0] >= 4f){
                        int diff = 10 + Mathf.clamp(ai.difficulty * 2, 0, 10);
                        for(int i = 0; i < diff; i++){
                            float angle = (i * (360f / diff));
                            for(int s : Mathf.signs){
                                Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle);
                                b.hitSize = 20f;
                                b.lifetime /= 1.5f;
                                b.vel.scl(0.5f);
                                KamiBulletData data = KamiBulletDatas.get(b, KamiBulletDatas.accelTurn);
                                data.attribute = s * 1.2f;
                            }
                        }
                        ai.reloads[0] = 0f;
                    }
                    ai.reloads[0] += Time.delta;
                }),
                new KamiPatternStage(60f, ai -> ai.moveAround(60f), null)
            };
        }}, 0);

        //Utsuho
        Cons<NewKamiAI> utsuhoDrawer = ai -> {
            for(int i = 0; i < 3; i++){
                Draw.color(tCol.set(Color.red).shiftHue((i * (360f / 3f)) + (Time.time * 3f)).a(Mathf.clamp(ai.drawIn * 1.5f)));
                tVec.trns((i * (360f / 3f)) + 90f, ai.drawIn * (70f + Mathf.sin(60f, 15f))).add(ai.getX(), ai.getY());
                Draw.rect(KamiRegions.okuu[i], tVec.x, tVec.y, (i * (360f / 3f)) + 90f);
            }
        };

        //Atomic Fire "Nuclear Fusion"
        addPattern(new KamiPattern(){{
            time = 10f * 60f;
            maxDamage = 1000f;
            drawer = utsuhoDrawer;
            init = ai -> ai.kami().laserRotation = -1f;
            stages = new KamiPatternStage[]{
                new KamiPatternStage(2f * 60f, ai -> {
                    int diff = 6 + Mathf.clamp(ai.difficulty, 0, 2);
                    float angleRand = Mathf.random(0f, 360f);
                    float offset = 180f / diff;
                    for(int i = 0; i < diff; i++){
                        float angle = (i * (360f / diff)) + angleRand;
                        Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle, 0.3f + Mathf.range(0.1f), 1f / 0.3f);
                        b.hitSize = 110f;
                        if(ai.difficulty > 2){
                            Time.run(2f * 60f, () -> {
                                if(!ai.unit.dead && !ai.reseting){
                                    Bullet c = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle + offset, 0.3f + Mathf.range(0.1f), 1f / 0.3f);
                                    c.hitSize = 110f;
                                }
                            });
                        }
                    }
                }, null),
                new KamiPatternStage(4f * 60f, ai -> {
                        ai.kami().laserRotation *= -1f;
                        ai.reloads[1] = 0f;
                    }, ai -> {
                    if(ai.reloads[0] >= 2f){
                        for(int i = 0; i < 3; i++){
                            float angle = (i * (360f / 3f)) + (ai.reloads[1] * ai.kami().laserRotation * 5f);
                            int diff = Mathf.clamp(ai.difficulty + 1, 1, 4);
                            for(int j = 0; j < diff; j++){
                                Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.getX(), ai.getY(), angle + Mathf.range(2f));
                                b.vel.scl(Mathf.random(0.8f, 1.2f) * 0.7f);
                                b.lifetime *= 1f / 0.7f;
                            }
                        }
                        ai.reloads[1] += 2f;
                        ai.reloads[0] = 0f;
                    }
                    ai.reloads[0] += Time.delta;
                })
            };
        }}, 1);
    }

    public static class KamiPattern{
        public Cons<NewKamiAI> drawer, effects, init;
        public KamiPatternStage[] stages;
        public int type = -1;
        public float time = 60f, maxDamage = Float.MAX_VALUE;

        public void update(NewKamiAI ai){
            if(effects != null) effects.get(ai);
            if(stages[ai.stage % stages.length].stage != null) stages[ai.stage % stages.length].stage.get(ai);
            ai.stageTime += Time.delta;
            if(ai.stageTime >= stages[ai.stage % stages.length].time){
                ai.stage++;
                ai.stageTime = 0f;
                if(stages[ai.stage % stages.length].init != null) stages[ai.stage % stages.length].init.get(ai);
            }
        }

        public void draw(NewKamiAI ai){
            if(drawer != null) drawer.get(ai);
        }

        public void init(NewKamiAI ai){
            if(init != null) init.get(ai);
            if(stages[0].init != null) stages[0].init.get(ai);
        }
    }

    public static class KamiPatternStage{
        Cons<NewKamiAI> stage, init;
        float time;

        public KamiPatternStage(float time, Cons<NewKamiAI> init, Cons<NewKamiAI> stage){
            this.stage = stage;
            this.init = init;
            this.time = time;
        }

        public KamiPatternStage(float time, Cons<NewKamiAI> stage){
            this(time, null, stage);
        }
    }
}
