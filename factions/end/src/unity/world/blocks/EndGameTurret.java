package unity.world.blocks;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.content.*;
import unity.graphics.*;
import unity.util.*;

/** @author EyeOfDarkness */
public class EndGameTurret extends Turret{
    static Seq<Posc> tmpTargets = new Seq<>(Posc.class);
    static float targetScore = 0f;
    static Healthc bestTarget;

    TextureRegion[] ringRegions = new TextureRegion[6];
    TextureRegion baseRegion, baseLightRegion1, baseLightRegion2;
    float[] ringReloadTimes = {15, 10, 5}, eyeMoveScl = {0.9f, 0.9f, 0.6f}, ringMoveDuration = {120f, 60f, 30f}, ringMoveSpeed = {2f, 4f, 8f};
    Vec2[] ringPos = {new Vec2(44.25f, 18f), new Vec2(30.5f, 0f), new Vec2(19.5f, 7.5f)};
    int[] ringDirection = {1, -1, 1};
    float mainRange = 750f;
    float chargeTime = 7f * 60f;

    public EndGameTurret(String name){
        super(name);
        size = 16;
        health = 89000;
        reload = 12f * 60f;
        absorbLasers = true;
        shake = 2.2f;
        noUpdateDisabled = true;
        outlineIcon = false;

        //range = 890f;
        range = 1350f;
        //short range = 600;
        coolantMultiplier = 1.2f;
        shootCone = 360f;

        //consumePower(460f);
        consumePowerDynamic((EndGameTurretBuild e) -> {
            float sum = 0f;
            if(e.shouldConsume() && e.isShootingOld()){
                sum += 480f;
            }
            if(e.totalEyeTargets > 0){
                sum += 120f;
            }
            return sum;
        });
        //place holder
        consumeItem(Items.surgeAlloy, 5);

        requirements(Category.turret, ItemStack.with(Items.copper, 1));
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        baseLightRegion1 = Core.atlas.find(name + "-base-light-0");
        baseLightRegion2 = Core.atlas.find(name + "-base-light-1");

        for(int i = 0; i < ringRegions.length; i++){
            ringRegions[i] = Core.atlas.find(name + "-" + i);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * Vars.tilesize + offset, y * Vars.tilesize + offset, mainRange, Pal.remove);
    }

    public class EndGameTurretBuild extends TurretBuild{
        float trueHealth, trueMaxHealth, excessDamage;
        float eyeAlpha, eyeRetarget, chargeTimeCounter;
        float[] ringRotations = new float[3], ringMovementTime = new float[3], targetRotations = new float[3], ringReloads = new float[3];
        int[] ringIdx = new int[3];
        int totalEyeTargets;
        Posc[] targets = new Posc[8 * 3];
        Vec2 eyeOffset = new Vec2(), visualEyeOffset = new Vec2();
        Vec2[] eyePosition = new Vec2[8 * 3];

        float sinAlpha(int i){
            float absin = Mathf.sinDeg((Time.time * 2f) + i * 25f) * 0.7f + 0.3f;
            return (absin * 0.5f) + 0.5f;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, mainRange, Pal.remove);
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);

            if(shootWarmup > 0.0001f){
                Tmp.c1.set(1, Mathf.sin(Time.time, 15, 0.2f) + 0.8f, Mathf.cos(Time.time, 15, 0.2f) + 0.8f).a(shootWarmup * sinAlpha(0));
                Draw.color(Tmp.c1);
                Draw.blend(Blending.additive);

                Draw.rect(baseLightRegion1, x, y);
            }
            if(eyeAlpha > 0.0001f){
                Tmp.c1.set(1, Mathf.sin(Time.time, 15, 0.2f) + 0.8f, Mathf.cos(Time.time, 15, 0.2f) + 0.8f).a(eyeAlpha);
                Draw.color(Tmp.c1);
                Draw.blend(Blending.additive);
                Draw.rect(baseLightRegion2, x, y);

                Rand r = MathUtils.seedr, r2 = MathUtils.seedr2;
                r.setSeed(id * 9999L);
                float amount = reloadCounter / 20f;

                Lines.stroke(1.5f);
                Draw.blend(Blending.normal);
                Draw.color(EndPal.endLight);

                for(int i = 0; i < Mathf.ceil(amount); i++){
                    float fin = Math.min(1f, amount - i);
                    float duration = r.random(20f, 35f);
                    float t = Math.max(chargeTimeCounter, 1) + r.random(duration);
                    int tseed = (int)(t / duration) + r.nextInt();
                    float fin2 = (t / duration) % 1f;

                    r2.setSeed(tseed);
                    float len = r2.random(30f, 55f);
                    float angle = r2.random(360f);
                    Vec2 v = Tmp.v1.trns(angle, len * (1f - fin2)).add(this);
                    Lines.lineAngleCenter(v.x, v.y, angle, 12f * Mathf.slope(fin2) * fin, false);
                }
            }

            if(reloadCounter > reload){
                Draw.blend(Blending.normal);
                Draw.color(Color.black);

                Rand r = MathUtils.seedr, r2 = MathUtils.seedr2;
                r.setSeed(id * 9999L + 2);
                float charge = (reloadCounter - reload) / chargeTime;
                float charge2 = charge * 7f;
                int amount = Mathf.ceil(charge2);
                for(int i = 0; i < amount; i++){
                    for(int j = 0; j < 3; j++){
                        float fin = Math.min(1f, charge2 - i);
                        float duration = r.random(50f, 80f);
                        float t = Time.time + r.random(duration);
                        //int tseed = (int)(t / duration) + r.nextInt();
                        float fin2 = (t / duration) % 1f;
                        float fin3 = Interp.pow3Out.apply(charge);

                        r2.setSeed((int)(t / duration) + r.nextInt());
                        float width = r2.random(11f, 22f);
                        float length = width * r2.random(3f, 3.8f) * Mathf.slope(fin2) * fin * fin3;

                        DrawPU.diamond(x, y, width * Mathf.curve(1f - fin2, 0.5f, 1f) * fin * fin3, length, r2.random(360f) + r2.range(15f) * fin2);
                    }
                }
            }

            for(int i = 0; i < 3; i++){
                float colS = Mathf.sin(Time.time + 5 + i * 5, 15, 0.2f) + 0.8f;
                float colC = Mathf.cos(Time.time + 5 + i * 5, 15, 0.2f) + 0.8f;

                Draw.blend(Blending.normal);
                Draw.color(Color.white);
                TextureRegion rr = ringRegions[i * 2];
                Drawf.spinSprite(rr, x, y, ringRotations[i]);

                if(shootWarmup > 0.0001f){
                    TextureRegion lr = ringRegions[i * 2 + 1];

                    float r = Mathf.mod(ringRotations[i], 90f);
                    float f = r / 90f;
                    //float f1 = Interp.pow2Out.apply(f), f2 = Interp.pow2Out.apply(1f - f);

                    Tmp.c1.set(1, colS, colC).a(shootWarmup * (1f - f) * sinAlpha(i + 1));
                    Draw.color(Tmp.c1);
                    Draw.blend(Blending.additive);
                    Draw.rect(lr, x, y, r);

                    Tmp.c1.set(1, colS, colC).a(shootWarmup * f * sinAlpha(i + 1));
                    Draw.color(Tmp.c1);
                    Draw.rect(lr, x, y, r - 90f);
                }
                if(eyeAlpha > 0.0001f){
                    //float ox = visualEyeOffset.x * eyeMoveScl[i], oy = visualEyeOffset.y * eyeMoveScl[i];
                    Draw.blend(Blending.additive);
                    for(int j = 0; j < 8; j++){
                        //Vec2 v = eyePos(i, j);
                        Vec2 v = eyePosition[i * 8 + j];
                        Tmp.c1.set(EndPal.endMid).mul(1f, colS, colC, 1f);
                        Draw.color(Tmp.c1);
                        Fill.circle(v.x, v.y, (0.8f * eyeMoveScl[i] + Mathf.absin(Time.time + i * 4f, 6f, 0.125f)) * eyeAlpha);
                    }
                }
            }

            if(eyeAlpha > 0.0001f){
                float radS = Mathf.absin(Time.time + 12f, 6f, 0.75f) * eyeAlpha;
                float colS = Mathf.sin(Time.time + 20, 15, 0.1f) + 0.9f;
                float colC = Mathf.cos(Time.time + 20, 15, 0.1f) + 0.9f;
                Vec2 v = eyePosCenter();

                Draw.blend(Blending.normal);
                Tmp.c1.set(EndPal.endLight).mul(1f, colS, colC, 1f);
                Draw.color(Tmp.c1);

                DrawPU.shiningCircle(id + 10, v.x, v.y, 6f * eyeAlpha + radS, 5, 60f, 0.5f, 90f, 25f, 4f * eyeAlpha, 0.3f);
                Draw.color(Color.white);
                Fill.circle(v.x, v.y, 4f * eyeAlpha + radS);
            }
            Draw.blend(Blending.normal);
        }

        public boolean isShootingOld(){
            return super.isShooting();
        }

        @Override
        public boolean isShooting(){
            return (isControlled() ? unit.isShooting() : logicControlled() ? logicShooting : (totalEyeTargets > 0 || target != null));
        }

        @Override
        public boolean hasAmmo(){
            return true;
        }

        float trueEfficiency(){
            return power.status;
        }

        boolean invalidateTarget(Posc target){
            return target == null || !target.isAdded() || (range != Float.MAX_VALUE && !target.within(x, y, range + (target instanceof Sized hb ? hb.hitSize() / 2f : 0f))) ||
            (target instanceof Teamc t && t.team() == team) || (target instanceof Healthc h && !h.isValid());
        }

        @Override
        public void updateTile(){
            if(!validateTarget()) target = null;

            float warmupTarget = isShooting() && canConsume() ? 1f : 0f;
            shootWarmup = Mathf.lerpDelta(shootWarmup, warmupTarget, shootWarmupSpeed);
            heat = Math.max(heat - Time.delta / cooldownTime, 0);

            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            if(isShooting() && canConsume()){
                for(int i = 0; i < ringRotations.length; i++){
                    if((ringMovementTime[i] += Time.delta) >= ringMoveDuration[i]){
                        targetRotations[i] = Mathf.mod(ringRotations[i] + Mathf.range(45f, 270f) * shootWarmup, 360f);
                        ringMovementTime[i] = 0f;
                    }
                }
            }else{
                for(int i = 0; i < targetRotations.length; i++){
                    targetRotations[i] = (int)(targetRotations[i] / 90f) * 90f;
                }
            }
            for(int i = 0; i < ringRotations.length; i++){
                float sp = ringMoveSpeed[i];
                float r = Mathf.clamp(MathUtils.angleDistSigned(ringRotations[i], targetRotations[i]) / 15, -sp, sp);
                //ringRotations[i] = Mathf.slerpDelta(ringRotations[i], targetRotations[i], 0.02f);
                ringRotations[i] -= r;
            }

            if(power.status > 0.0001f){
                eyeAlpha = Mathf.lerpDelta(eyeAlpha, 1f, 0.05f * power.status);
            }else{
                eyeAlpha = Mathf.lerpDelta(eyeAlpha, 0f, 0.05f * (1 - power.status));
            }

            if(isControlled()){
                Player con = (Player)unit.controller();
                eyeOffset.set(con.mouseX, con.mouseY).sub(x, y).scl(1f / (range / 7f)).limit(1.3f);
                targetPos.set(con.mouseX, con.mouseY);
            }else if(target != null && trueEfficiency() > 0.0001f){
                eyeOffset.set(target.x(), target.y()).sub(x, y).scl(1f / (range / 7f)).limit(1.3f);
                targetPos.set(target.x(), target.y());
            }else{
                eyeOffset.set(0f, 0f);
            }

            updateEyes();
        }

        void updateEyes(){
            visualEyeOffset.lerp(eyeOffset, 0.2f);
            float rspeed = Time.delta * baseReloadSpeed();

            boolean controlled = isControlled();

            for(int i = 0; i < targets.length; i++){
                if(targets[i] != null && (controlled || invalidateTarget(targets[i]))){
                    targets[i] = null;
                    totalEyeTargets--;
                }
            }

            if(target != null && (controlled || invalidateTarget(target))){
                target = null;
                if(totalEyeTargets > 0 && !controlled){
                    findMainTarget(targets, targets.length);
                }
            }

            if(eyePosition[0] == null){
                for(int i = 0; i < eyePosition.length; i++){
                    eyePosition[i] = new Vec2();
                }
            }
            for(int i = 0; i < eyePosition.length; i++){
                eyePosition[i].set(eyePos(i / 8, i % 8));
            }

            if(!canConsume()) return;
            if(!controlled && (totalEyeTargets < 24 || target == null) && (eyeRetarget += Time.delta) >= 15f){
                eyeRetarget();
                eyeRetarget = 0f;
            }

            for(int i = 0; i < ringReloads.length; i++){
                if(ringReloads[i] > 0f) ringReloads[i] -= rspeed;
            }

            if(((totalEyeTargets > 0 && !controlled) || (controlled && unit.isShooting()))){
                for(int i = 0; i < ringReloads.length; i++){
                    if(ringReloads[i] <= 0f){
                        eyeShoot(i, ringIdx[i], controlled);
                        ringReloads[i] = ringReloadTimes[i];
                        ringIdx[i] = Mathf.mod(ringIdx[i] + ringDirection[i], 8);
                    }
                }
            }

            if(reloadCounter < reload) reloadCounter = Math.min(reloadCounter + rspeed, reload);

            if(reloadCounter >= reload && ((controlled && unit.isShooting()) || (target != null && within(target, mainRange + (target instanceof Sized s ? s.hitSize() / 2f : 0f))))){
                reloadCounter += rspeed;
            }else if(reloadCounter > reload){
                reloadCounter = Math.max(reload, reloadCounter - Time.delta);
            }

            chargeTimeCounter += Math.min(1f, reloadCounter / reload) * Time.delta;

            if(reloadCounter > reload){
                float charge = ((reloadCounter - reload) / chargeTime);
                chargeTimeCounter -= charge * charge * 2f * Time.delta;
            }

            if(reloadCounter >= reload + chargeTime){
                reloadCounter = 0f;
                chargeTimeCounter = Mathf.random(1000000f);
            }
        }

        Vec2 eyePosCenter(){
            return Tmp.v1.set(visualEyeOffset.x, visualEyeOffset.y).scl(6f).add(this);
        }

        Vec2 eyePos(int ring, int idx){
            float ox = visualEyeOffset.x * eyeMoveScl[ring], oy = visualEyeOffset.y * eyeMoveScl[ring];

            int rot = (idx / 2);
            if(ringPos[ring].y == 0){
                return Tmp.v1.trns(ringRotations[ring] + idx * 45f, ringPos[ring].x).add(x + ox, y + oy);
            }

            float side = Mathf.signs[idx % 2] * ringPos[ring].y;
            return Tmp.v1.trns(ringRotations[ring] + rot * 90f, ringPos[ring].x, side).add(x + ox, y + oy);
        }

        void eyeShoot(int ring, int idx, boolean controlled){
            //Vec2 v = eyePos(ring, idx);
            Vec2 v = eyePosition[ring * 8 + idx];
            //float ox = visualEyeOffset.x * eyeMoveScl[ring], oy = visualEyeOffset.y * eyeMoveScl[ring];
            if(!controlled){
                Posc t = targets[ring * 8 + idx];
                if(t != null){
                    //float rot = v.angleTo(t);
                    //ringBullets[ring].create(this, team, v.x, v.y, rot);

                    eyeDamage((Healthc)t, ring, v);

                    EndFx.LaserEffectData d = new EndFx.LaserEffectData();
                    d.a = v;
                    d.b = t;
                    EndFx.endgameEyeLaser.at(v.x, v.y, ring != 2 ? 1f : 0.6f, d);
                }
            }else{
                Vec2 l = Tmp.v2.set(targetPos).sub(this).limit(range).add(this);
                Teamc t = Units.closestTarget(team, l.x, l.y, 2f);

                //ringBullets[ring].create(this, team, v.x, v.y, v.angleTo(l));

                if(t != null) eyeDamage((Healthc)t, ring, v);

                EndFx.LaserEffectData d = new EndFx.LaserEffectData();
                d.a = v;
                d.b = t == null ? new Vec2(l) : t;
                EndFx.endgameEyeLaser.at(v.x, v.y, ring != 2 ? 1f : 0.6f, d);
            }
        }

        void eyeDamage(Healthc e, int ring, Vec2 v){
            float d = ring == 2 ? 300f : 650f;
            float scl = ring == 2 ? 250f : 150f;
            float h = e.maxHealth() / 150000f;
            float scl2 = Math.max(1f, h * h * h);
            e.damagePierce((d + (e.maxHealth() / scl)) * scl2);

            if(ring != 2){
                EndFx.endRingHit.at(e.x(), e.y(), v.angleTo(e));
                Damage.damage(team, e.x(), e.y(), 110f, 150f * scl2);
            }

            //ringBullets[ring].create(this, team, e.getX(), e.getY(), v.angleTo(e));
        }

        void findMainTarget(Posc[] targets, int size){
            if(target != null) return;
            targetScore = -Float.MAX_VALUE;
            bestTarget = null;

            for(int i = 0; i < size; i++){
                Posc t = targets[i];
                if(!(t instanceof Healthc)) continue;
                float h = ((Healthc)t).maxHealth();
                float s = (h * h) + -dst2(t) / 6000f;
                if(s > targetScore || bestTarget == null){
                    targetScore = s;
                    bestTarget = (Healthc)t;
                }
            }
            target = bestTarget;
        }

        void eyeRetarget(){
            tmpTargets.clear();

            Seq<Teams.TeamData> data = Vars.state.teams.present;
            for(int i = 0; i < data.size; i++){
                if(data.items[i].team != team){
                    QuadTree<Unit> utree = data.items[i].unitTree;
                    QuadTree<Building> btree = data.items[i].buildingTree;
                    float r = range;

                    if(utree != null){
                        utree.intersect(x - r, y - r, r * 2, r * 2, u -> {
                            if(within(u, r + u.hitSize / 2f)){
                                tmpTargets.add(u);
                            }
                        });
                    }
                    if(btree != null){
                        btree.intersect(x - r, y - r, r * 2, r * 2, b -> {
                            if(within(b, r + b.hitSize() / 2f)){
                                tmpTargets.add(b);
                            }
                        });
                    }
                }
            }

            if(!tmpTargets.isEmpty()){
                findMainTarget(tmpTargets.items, tmpTargets.size);
                tmpTargets.sort((Floatf<Posc>)this::dst);
                for(int i = 0; i < targets.length; i++){
                    Posc t = tmpTargets.get(i % tmpTargets.size);
                    if(targets[i] == null){
                        targets[i] = t;
                        totalEyeTargets++;
                    }
                }
            }
            tmpTargets.clear();
        }
    }
}
