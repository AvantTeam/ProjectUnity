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
    private final static Vec2 tVec = new Vec2(), tVec2 = new Vec2();

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
                    if(ai.reloads[3] >= 60f * 1.5f){
                        ai.moveAround(40f);
                        ai.reloads[3] = 0f;
                    }
                    ai.reloads[3] += Time.delta;
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
            time = 25f * 60f;
            maxDamage = 1000f;
            drawer = utsuhoDrawer;
            init = ai -> ai.kami().laserRotation = -1f;
            stages = new KamiPatternStage[]{
                new KamiPatternStage(2f * 60f, ai -> {
                    int diff = 6 + Mathf.clamp(ai.difficulty, 0, 2);
                    float angleRand = Mathf.random(0f, 360f);
                    float offset = Mathf.random(25f);
                    for(int i = 0; i < diff; i++){
                        float angle = (i * (360f / diff)) + angleRand;
                        float randV = Mathf.range(0.1f);
                        Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle, 0.3f + randV, 1f / 0.3f);
                        b.hitSize = 150f;
                        if(ai.difficulty > 2){
                            Time.run(2f * 60f, () -> {
                                if(!ai.unit.dead && !ai.reseting && !ai.spell){
                                    Bullet c = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle + offset, 0.3f + randV, 1f / 0.3f);
                                    c.hitSize = 150f;
                                }
                            });
                        }
                    }
                }, null),
                new KamiPatternStage(4f * 60f, ai -> {
                        ai.kami().laserRotation *= -1f;
                        ai.reloads[1] = 0f;
                    }, ai -> {
                    if(ai.reloads[0] >= 3f){
                        for(int i = 0; i < 3; i++){
                            float angle = (i * (360f / 3f)) + (ai.reloads[1] * ai.kami().laserRotation * 5f);
                            int diff = Mathf.clamp(ai.difficulty + 2, 1, 5);
                            for(int j = 0; j < diff; j++){
                                Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.getX(), ai.getY(), angle + Mathf.range(4f));
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

        //Explosion Sign "Mega Flare"
        addPattern(new KamiPattern(){{
            time = 25f * 60f;
            maxDamage = 1000f;
            drawer = utsuhoDrawer;
            stages = new KamiPatternStage[]{
                new KamiPatternStage(25f * 60f, ai -> {
                    float diffReload = 25 - Mathf.clamp(ai.difficulty * 5f, 0f, 15f);
                    int diff = 10 + Mathf.clamp(ai.difficulty * 2, 0, 8);
                    if(ai.reloads[0] >= diffReload){
                        tVec2.trns(ai.relativeRotation, 270f).add(ai.targetPos);
                        tVec.trns(ai.relativeRotation - 90f, Mathf.range(150f * 2f), Mathf.range(20f)).add(tVec2);
                        Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, tVec.x, tVec.y, ai.relativeRotation + 180f);
                        b.vel.scl(0.8f);
                        KamiBulletDatas.get(b, KamiBulletDatas.expandShrink);
                        float offAng = Mathf.range(30f);
                        for(int i = 0; i < diff; i++){
                            float angle = (i * (360f / diff)) + offAng;
                            Bullet c = UnityBullets.kamiBullet1.create(ai.unit, tVec.x, tVec.y, angle);
                            c.hitSize = 7f;
                        }
                        ai.reloads[0] = 0f;
                    }
                    if(ai.reloads[1] >= 2f * 60f){
                        ai.moveAround(1.5f * 60f);
                        ai.reloads[1] = 0f;
                    }
                    ai.reloads[1] += Time.delta;
                    ai.reloads[0] += Time.delta;
                })
            };
        }}, 1);

        //Blazing Star "Fixed Star"/Blazing Star "Planetary Revolution"
        addPattern(new KamiPattern(){{
            time = 25f * 60f;
            maxDamage = 1000f;
            drawer = utsuhoDrawer;
            init = ai -> {
                ai.reloads[1] = -2f * 60f;
                //ai.reloads[2] = -1f;
                int diff = 3 + Mathf.clamp(Mathf.ceil(ai.difficulty * 0.4f), 0, 2);
                for(int i = 0; i < diff * 2; i++){
                    Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), 0f);
                    b.vel.scl(0f);
                    b.lifetime = time;
                    b.hitSize = 180f;
                    KamiBulletDatas.get(b, KamiBulletDatas.nonDespawnable);
                    ai.bullets[i] = b;
                }
            };
            stages = new KamiPatternStage[]{
                new KamiPatternStage(25f * 60f, ai -> {
                    float reloadDiff = 35f - Mathf.clamp(ai.difficulty * 3, 0, 10);
                    float rotateDiff = 0.15f + Mathf.clamp(ai.difficulty / 5f, 0f, 0.2f);
                    int diff = 15 + Mathf.clamp(ai.difficulty * 5, 0, 15);
                    int diffA = 3 + Mathf.clamp(Mathf.ceil(ai.difficulty * 0.4f), 0, 2);

                    if(ai.reloads[1] >= reloadDiff){
                        for(int i = 0; i < diff; i++){
                            float angle = i * (360f / diff) + (ai.reloads[2]);
                            Bullet b = UnityBullets.kamiBullet1.create(ai.unit, ai.unit.team, ai.getX(), ai.getY(), angle);
                            b.vel.scl(0.75f);
                            b.lifetime /= 0.75f;
                            KamiBulletDatas.get(b, KamiBulletDatas.turnRegular).attribute(-0.3f);
                        }
                        ai.reloads[2] += 3;
                        ai.reloads[1] = 0f;
                    }
                    float offset = (180f / diffA);
                    for(int i = 0; i < diffA; i++){
                        float angle = i * (360f / diffA);
                        for(int j = 0; j < 2; j++){
                            tVec.trns((angle + (offset * j)) + ai.reloads[4 + j], ai.reloads[0] * (j + 0.6f)).add(ai);
                            ai.bullets[(i * 2) + j].set(tVec);
                        }
                    }
                    ai.reloads[4] += Time.delta * rotateDiff;
                    ai.reloads[5] += Time.delta * rotateDiff * (ai.difficulty >= 2 ? -1f : 1f);
                    ai.reloads[1] += Time.delta;
                    ai.reloads[0] = Mathf.clamp(ai.reloads[0] + (Time.delta * 1.4f), 0f, 360f);
                })
            };
        }}, 1);
    }

    public static class KamiPattern{
        public Cons<NewKamiAI> drawer, effects, init, start;
        public KamiPatternStage[] stages;
        public int type = -1;
        public float time = 60f, maxDamage = Float.MAX_VALUE, waitTime = 2f * 60f;

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

        public void start(NewKamiAI ai){
            if(start != null) start.get(ai);
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
