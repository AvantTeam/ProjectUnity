package unity.ai.kami;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import unity.ai.kami.KamiPattern.*;
import unity.ai.kami.KamiPattern.StagePattern.*;
import unity.content.*;
import unity.gen.*;

public class KamiPatterns{
    public static KamiPattern basicPattern1, basicPattern2, expandPattern;
    public static void load(){
        basicPattern1 = new KamiPattern(20f * 60f){
            @Override
            public void update(KamiAI ai){
                Unit u = ai.unit;
                int diff = 6 + Mathf.clamp(ai.difficulty / 2, 0, 6), diff2 = 16 + Mathf.clamp(ai.difficulty * 2, 0, 16);
                float turn = Mathf.sin(ai.patternTime, 90f, 0.75f);
                if(ai.shoot(0, 15f)){
                    for(int i = 0; i < diff; i++){
                        float ang = (i * (360f / diff)) + ai.reloads[1];
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 4f;
                        b.turn = turn;
                        b.lifetime = 5f * 60f;
                        b.vel.scl(4f);
                    }
                    ai.reloads[1] += 180f / diff;
                }
                if(ai.shoot(2, 40f)){
                    for(int i = 0; i < diff2; i++){
                        float ang = (i * (360f / diff2)) + ai.reloads[3];
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 10f;
                        b.lifetime = 5f * 60f;
                        b.vel.scl(5f);
                    }
                    ai.reloads[3] += 180f / diff2;
                }
            }
        };
        basicPattern2 = new KamiPattern(20f * 60f){
            @Override
            public void init(KamiAI ai){
                ai.reloads[0] = 1f;
                ai.reloads[4] = 1f;
            }

            @Override
            public void update(KamiAI ai){
                Unit u = ai.unit;
                int diff = 8 + ai.difficulty / 2;
                if(ai.reloads[3] < 2f * 60f && ai.shoot(1, 5f)){
                    for(int i = 0; i < diff; i++){
                        float ang = (i * (360f / diff)) + ai.reloads[2];
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 6f;
                        b.turn = 0.25f * ai.reloads[0];
                        b.lifetime = 6f * 60f;
                        b.vel.scl(4f);
                    }
                    ai.reloads[0] *= -1f;
                    ai.reloads[2] += (40f / diff) * ai.reloads[4];
                }
                ai.reloads[3] += Time.delta;
                if(ai.reloads[3] > 3.5f * 60f){
                    ai.reloads[2] = 0f;
                    ai.reloads[3] -= 3.5f * 60f;
                    ai.reloads[4] *= -1f;
                }
            }
        };

        expandPattern = new StagePattern(-4, new Stage(2.5f * 60f, 2, (ai, data) -> {
            Unit u = ai.unit;
            if(data.time < 1.75f * 60f && ai.shoot(2, 5f)){
                int amount = (ai.difficulty * 2) + (int)(ai.reloads[0]) + 1;
                float spread = 18f / (1f + ai.difficulty / 3f);
                Angles.shotgun(amount, spread, ai.reloads[1] + Mathf.sin(ai.reloads[3], 3.5f, spread / 1.5f) * ai.reloads[4], f -> {
                    Vec2 v = Tmp.v1.trns(f, 16f);
                    KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x + v.x, u.y + v.y, f);
                    b.width = b.length = 14f;
                    b.lifetime = 7f * 60f;
                    b.vel.scl(4.5f + ai.difficulty / 6f);
                });

                ai.reloads[0] += 1f;
                ai.reloads[3] += 1f;
            }
        }, (ai, data) -> {
            ai.reloads[0] = ai.reloads[3] = 0f;
            ai.reloads[1] = ai.targetAngle();
            ai.reloads[4] *= -1f;
        }), new Stage(150f, (ai, data) -> {
            Unit u = ai.unit;
            if(data.time < 120f && ai.shoot(2, 5f)){
                int amount = ai.difficulty * 4 + (int)(ai.reloads[0]) + 8;
                for(int i = 0; i < amount; i++){
                    float ang = (i * (360f / amount)) + ai.reloads[1] + Mathf.sin(ai.reloads[3], 3f, 180f / amount) * ai.reloads[5];
                    KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang);
                    b.width = b.length = 16f;
                    b.lifetime = 8f * 60f;
                    b.vel.scl(3f + ai.difficulty / 8f);
                }

                ai.reloads[0] += 1f + ai.difficulty * 0.5f;
                ai.reloads[3] += 1f;
            }
        }, (ai, data) -> {
            ai.reloads[5] *= -1f;
        })){
            @Override
            public void init(KamiAI ai){
                super.init(ai);
                ai.reloads[4] = ai.reloads[5] = 1f;
            }
        };
    }
}
