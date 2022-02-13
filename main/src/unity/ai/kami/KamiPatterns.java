package unity.ai.kami;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.content.*;
import unity.gen.*;

public class KamiPatterns{
    public static KamiPattern basicPattern1, basicPattern2;
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
    }
}
