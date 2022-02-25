package unity.ai.kami;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import unity.ai.kami.KamiPattern.*;
import unity.ai.kami.KamiPattern.StagePattern.*;
import unity.content.*;
import unity.entities.bullet.kami.*;
import unity.gen.*;

import static unity.ai.kami.KamiBulletPresets.*;
import static unity.ai.kami.KamiPattern.PatternType.*;

public class KamiPatterns{
    public final static int[] zero = {0};
    public static KamiPattern basicPattern1, basicPattern2, expandPattern, flowerPattern, flowerPattern2, hyperSpeedPattern;
    public static void load(){
        basicPattern1 = new KamiPattern(20f * 60f){
            {
                //just in case;
                type = PatternType.permanent;
            }
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

        expandPattern = new StagePattern(-4, bossBasic, new Stage(2.5f * 60f, 2, (ai, data) -> {
            Unit u = ai.unit;
            if(data.time < 1.75f * 60f && ai.shoot(2, 8f)){
                int amount = (ai.difficulty * 2) + (int)(ai.reloads[0]) + 1;
                float spread = 18f / (1f + ai.difficulty / 3f);
                Angles.shotgun(amount, spread, ai.reloads[1] + Mathf.sin(ai.reloads[3], 3.5f, spread / 1.5f) * ai.reloads[4], f -> {
                    Vec2 v = Tmp.v1.trns(f, 16f);
                    KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x + v.x, u.y + v.y, f);
                    b.width = b.length = 14f;
                    b.lifetime = 8f * 60f;
                    b.vel.scl(2.75f + ai.difficulty / 6f);
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
                    float ang = (i * (360f / amount)) + ai.reloads[1] + Mathf.sin(ai.reloads[3], 4f, 180f / amount) * ai.reloads[5];
                    KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang);
                    b.width = b.length = 16f;
                    b.lifetime = 9f * 60f;
                    b.vel.scl(2f + ai.difficulty / 8f);
                }

                ai.reloads[0] += 1f + ai.difficulty * 0.5f;
                ai.reloads[3] += 1f;
            }
        }, (ai, data) -> ai.reloads[5] *= -1f)){
            @Override
            public void init(KamiAI ai){
                super.init(ai);
                ai.reloads[4] = ai.reloads[5] = 1f;
            }
        };

        flowerPattern = new StagePattern(-3, new Stage(4.5f * 60f, 2, (ai, d) -> {
            Unit u = ai.unit;
            if(d.time < 170f && ai.shoot(2, 7f)){
                int petals = Mathf.clamp(3 + ai.difficulty, 0, 8),
                amount = 4 + ai.difficulty, len = ai.difficulty;
                float spacing = ((180f / petals) / (120f / 7f));
                for(int i = 0; i < petals; i++){
                    float ang = i * 360f / petals + ai.reloads[0];
                    int[] sign = ai.reloads[1] <= 0f ? zero : Mathf.signs;
                    for(int s : sign){
                        shootLine(3f - len / 16f, 5f + len / 4f, amount, v -> {
                            KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang + ai.reloads[1] * s);
                            b.width = b.length = 12f;
                            b.lifetime = 8f * 60f;
                            b.vel.scl(v);
                        });
                    }
                }
                ai.reloads[1] += spacing;
            }
        }, (ai, d) -> {
            ai.reloads[0] = ai.targetAngle();
            ai.reloads[1] = 0f;
        }), new Stage(4.5f * 60f, (ai, d) -> {
            Unit u = ai.unit;
            if(d.time < 190f && ai.shoot(1, 15f)){
                int diff = 3 + ai.difficulty / 3, amount = 4 + ai.difficulty;
                for(int i = 0; i < diff; i++){
                    float ang = i * 360f / diff + ai.reloads[0];
                    for(int s : Mathf.signs){
                        shootLine(3f, 5.5f, 4 + amount, (v, j) -> {
                            KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang + ai.reloads[2] * s);
                            b.width = b.length = 12f;
                            b.lifetime = 7f * 60f;
                            b.vel.scl(v);
                            b.turn = (0.2f + (1f - j / amount) * 0.3f) * s * (1f - ai.reloads[3]);
                        });
                    }
                }
                ai.reloads[2] += 15f;
                ai.reloads[3] += 1f / 20f;
            }
        }, (ai, d) -> {
            ai.reloads[0] = ai.targetAngle();
            ai.reloads[1] = ai.reloads[2] = ai.reloads[3] = 0f;
        }));

        flowerPattern2 = new KamiPattern(35f * 60f){
            {
                type = bossBasic;
            }

            @Override
            public void init(KamiAI ai){
                ai.reloads[4] = 12f * 60f;
                ai.reloads[3] = 1;
            }

            @Override
            public void update(KamiAI ai){
                Unit u = ai.unit;
                int petals = 4 + ai.difficulty / 3;
                if(ai.burst(0, 280f, 3, 35f, () -> {
                    ai.reloads[3] *= -1;
                    ai.reloads[2] = 0f;
                    ai.reloads[8] = 0f;
                })){
                    float x = u.x, y = u.y, side = ai.reloads[3], d = ai.reloads[8];
                    for(int i = 0; i < petals; i++){
                        float ang = i * 360f / petals + ai.reloads[2];
                        petal(ai, 180f / petals, 40f, 9 + ai.difficulty, f -> f * (2f - f), (angle, delay) -> {
                            KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, x, y, ang + angle);
                            b.width = b.length = 13f;
                            b.lifetime = 12f * 60f;
                            b.time = delay;
                            b.vel.scl(5f);
                            b.bdata = KamiBulletDatas.stopChangeDirection;
                            b.fdata = 4f * 60f - d;
                            b.fdata2 = ang + 160f * side;
                        });
                    }
                    ai.reloads[2] += ((360f / 3) / petals) * ai.reloads[3];
                    ai.reloads[8] += 35f;
                }
                int d = ai.difficulty;
                int bursts = 12 + d * 2;
                float spacing = Math.max(7f - d, 3f);
                if(ai.burst(4, 115f, bursts, spacing, () -> {
                    ai.reloads[6] = 1f;
                    ai.reloads[7] = ai.targetAngle();
                })){
                    for(int s : Mathf.signs){
                        float ang = ai.reloads[7] + ai.reloads[6] * 90f * s;
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 16f;
                        b.lifetime = 7f * 60f;
                        b.vel.scl(4f);
                    }
                    ai.reloads[6] -= 1f / bursts;
                }
            }
        };

        hyperSpeedPattern = new KamiPattern(120f * 60){
            {
                type = advance;
                data = HyperSpeedData::new;
            }

            @Override
            public void draw(KamiAI ai){
                super.draw(ai);
                HyperSpeedData d = (HyperSpeedData)ai.patternData;
                if(d != null && !d.nextPosition.isEmpty()){
                    Color c = Tmp.c1.set(Draw.getColor());
                    FloatSeq f = d.nextPosition;
                    float time = 0f;
                    float size = (ai.unit.hitSize / 1.7f) * 2f;
                    for(int i = 0; i < f.size - 3; i += 3){
                        float x = f.get(i), y = f.get(i + 1), x2 = f.get(i + 3), y2 = f.get(i + 4);
                        float ang = Angles.angle(x, y, x2, y2);
                        if(f.get(i + 2) > 0f) time += f.get(i + 2);
                        float fout = (time - 6f) / 60f;
                        float fout2 = time / 66f;
                        if(fout > 0.0001f && fout <= 1f){
                            Draw.color(c);
                            float fin = 1f - fout;
                            Vec2 v = Tmp.v1.trns(ang + 90f, fout * 70f + size / 2f);
                            Lines.stroke(fin * 2f);
                            for(int s : Mathf.signs){
                                Lines.line(x + v.x * s, y + v.y * s, x2 + v.x * s, y2 + v.y * s, false);
                            }
                        }
                        if(fout2 > 0.0001f){
                            Tmp.c2.set(c).a(0.5f * Mathf.clamp(fout2));
                            Draw.color(Tmp.c2);
                            Lines.stroke(size);
                            Lines.line(x, y, x2, y2, false);
                        }
                    }
                }
            }

            @Override
            public void update(KamiAI ai){
                HyperSpeedData d = (HyperSpeedData)ai.patternData;
                FloatSeq pos = d.nextPosition;
                Unit u = ai.unit;
                Vec2 v = Tmp.v1;
                if(pos.isEmpty()){
                    ai.updateFollowing();
                }
                if(ai.shoot(0, 7f * 60f)){
                    d.index = 0;
                    pos.add(u.x, u.y, 66f);
                    v.trns(ai.targetAngle(), u.dst(ai.target) * 2f).add(u.x, u.y);
                    pos.add(v.x, v.y, 7f);
                    float lx = v.x;
                    float ly = v.y;
                    for(int i = 0; i < 12 + ai.difficulty * 2; i++){
                        float ang = Angles.angle(lx, ly, ai.x, ai.y) + Mathf.range(70f);
                        v.trns(ang, KamiAI.barrierRange + 40f).add(ai.x, ai.y);
                        pos.add(v.x, v.y, 7f);
                        lx = v.x;
                        ly = v.y;
                    }
                }
                if(!pos.isEmpty() && ai.reloads[2] <= 0f){
                    float fin = 1f - pos.get(d.index + 2) / 6f;
                    float lastTime = pos.get(d.index + 2);
                    pos.items[d.index + 2] -= Time.delta;
                    boolean s = pos.get(d.index + 2) < 7 && lastTime >= 7f;
                    if(s){
                        KamiLaser l = ((NewKamiLaserBulletType)UnityBullets.kamiLaser2).createL(u, u.team, u.x, u.y, u.x, u.y, u);
                        l.lifetime = 16f;
                        l.width = u.hitSize / 1.7f;
                        l.bdata = KamiBulletDatas.hyperSpeedLaser1;
                        l.intervalCollision = false;
                        l.ellipseCollision = false;
                        d.dashBullet = l;

                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, 0f);
                        b.bdata = KamiBulletDatas.positionLock;
                        b.width = b.length = (u.hitSize / 1.5f);
                        b.lifetime = 6f;
                        b.vel.setZero();
                    }
                    if(fin > 0f){
                        float x = pos.get(d.index), nx = pos.get(d.index + 3);
                        float y = pos.get(d.index + 1), ny = pos.get(d.index + 4);
                        v.set(x, y).lerp(nx, ny, Mathf.clamp(fin));
                        u.set(v);
                        u.rotation = Angles.angle(x, y, nx, ny);
                        if(fin >= 1f){
                            d.index += 3;
                            if(d.dashBullet != null){
                                d.dashBullet.set(u);
                            }
                            d.dashBullet = null;
                            if(d.index >= pos.size - 3){
                                d.index = 0;
                                ai.reloads[1] += 1;
                                ai.reloads[2] = 40f;
                                Tmp.v1.set(nx, ny).sub(x, y).setLength(5f);
                                u.vel.add(Tmp.v1);
                            }
                        }
                    }
                }
                if(ai.reloads[2] > 0f){
                    ai.reloads[2] -= Time.delta;
                    if(ai.reloads[2] <= 0f){
                        pos.clear();
                        if(ai.reloads[1] >= 5){
                            ai.patternTime = 0f;
                        }
                    }
                }
            }
        };
    }

    private static class HyperSpeedData extends PatternData{
        FloatSeq nextPosition = new FloatSeq();
        int index = 0;
        Bullet dashBullet;
    }
}
