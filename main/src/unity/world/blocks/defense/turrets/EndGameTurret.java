package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.content.*;
import unity.entities.*;
import unity.entities.effects.*;
import unity.gen.*;
import unity.util.*;

public class EndGameTurret extends PowerTurret {
    private static int shouldLaser = 0;
    private static float lowest;
    private static float dstC;
    protected static float damageFull;
    protected static float damageB;
    protected static int totalFrags;
    private final static float[] ringProgresses = {0.013f, 0.035f, 0.024f};
    private final static int[] ringDirections = {1, -1, 1};
    private final static Seq<Entityc> entitySeq = new Seq<>(512);

    protected int eyeTime = timers++;
    protected int bulletTime = timers++;

    public TextureRegion baseLightsRegion;
    public TextureRegion bottomLightsRegion;
    public TextureRegion eyeMainRegion;

    public TextureRegion ringABottomRegion;
    public TextureRegion ringAEyesRegion;
    public TextureRegion ringARegion;
    public TextureRegion ringALightsRegion;

    public TextureRegion ringBBottomRegion;
    public TextureRegion ringBEyesRegion;
    public TextureRegion ringBRegion;
    public TextureRegion ringBLightsRegion;

    public TextureRegion ringCRegion;
    public TextureRegion ringCLightsRegion;

    public EndGameTurret(String name){
        super(name);

        health = 68000;
        powerUse = 320f;
        reloadTime = 300f;
        absorbLasers = true;
        shootShake = 2.2f;
        outlineIcon = false;
        loopSound = UnitySounds.endgameActive;
        shootSound = UnitySounds.endgameShoot;
        /*shootType = new BulletType(){
            {
                damage = Float.MAX_VALUE;
            }
        };*/
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        baseLightsRegion = Core.atlas.find(name + "-base-lights");
        bottomLightsRegion = Core.atlas.find(name + "-bottom-lights");
        eyeMainRegion = Core.atlas.find(name + "-eye");

        ringABottomRegion = Core.atlas.find(name + "-ring1-bottom");
        ringAEyesRegion = Core.atlas.find(name + "-ring1-eyes");
        ringARegion = Core.atlas.find(name + "-ring1");
        ringALightsRegion = Core.atlas.find(name + "-ring1-lights");

        ringBBottomRegion = Core.atlas.find(name + "-ring2-bottom");
        ringBEyesRegion = Core.atlas.find(name + "-ring2-eyes");
        ringBRegion = Core.atlas.find(name + "-ring2");
        ringBLightsRegion = Core.atlas.find(name + "-ring2-lights");

        ringCRegion = Core.atlas.find(name + "-ring3");
        ringCLightsRegion = Core.atlas.find(name + "-ring3-lights");
    }

    public class EndGameTurretBuilding extends PowerTurretBuild{
        protected float resist = 1f;
        protected float resistTime = 10f;
        protected float threatLevel = 1f;
        protected float lastHealth = 0f;
        protected float eyeResetTime = 0f;
        protected float eyesAlpha = 0f;
        protected float lightsAlpha = 0f;
        protected float[] ringProgress = {0, 0, 0};
        protected float[] eyeReloads = {0, 0};

        protected int eyeSequenceA = 0;
        protected int eyeSequenceB = 0;

        protected Vec2 eyeOffset = new Vec2();
        protected Vec2 eyeOffsetB = new Vec2();
        protected Vec2 eyeTargetOffset = new Vec2();
        protected Vec2[] eyesVecArray = new Vec2[16];

        protected Posc[] targets = new Posc[16];

        @Override
        protected void effects(){
            shootSound.at(x, y);
        }

        @Override
        public void damage(float damage){
            if(verify()) return;
            float trueAmount = Mathf.clamp(damage / resist, 0f, 410f);
            super.damage(trueAmount);
            resist += 0.025 + Mathf.clamp(damage - 560f, 0f, 2147483647f) / 80f;
            if(Float.isNaN(resist)) resist = Float.MAX_VALUE;
            resistTime = 0f;
        }

        float deltaB(){
            return delta() * power.status;
        }

        @Override
        public void draw(){
            float oz = Draw.z();
            Draw.rect(baseRegion, x, y);
            Draw.z(oz + 0.01f);
            Draw.rect(ringABottomRegion, x, y, ringProgress[0]);
            Draw.rect(ringBBottomRegion, x, y, ringProgress[1]);
            Draw.z(oz + 0.02f);
            Draw.rect(ringARegion, x, y, ringProgress[0]);
            Draw.rect(ringBRegion, x, y, ringProgress[1]);
            Draw.rect(ringCRegion, x, y, ringProgress[2]);

            Draw.blend(Blending.additive);
            Draw.z(oz + 0.005f);
            Draw.color(1f, Funcs.offsetSin(0f, 5f), Funcs.offsetSin(90f, 5f), eyesAlpha);
            Draw.rect(bottomLightsRegion, x, y);
            Draw.color(1, Funcs.offsetSin(0f, 5f), Funcs.offsetSin(90f, 5f), lightsAlpha * Funcs.offsetSin(0f, 12f));
            Draw.rect(baseLightsRegion, x, y);
            //Draw.z(oz + 0.015f);

            TextureRegion[] regions = {ringAEyesRegion, ringBEyesRegion, eyeMainRegion};
            TextureRegion[] regionsB = {ringALightsRegion, ringBLightsRegion, ringCLightsRegion};
            float[] trnsScl = {1f, 0.9f, 2f};

            for(int i = 0; i < 3; i++){
                int h = i + 1;
                Draw.z(oz + 0.015f);
                Draw.color(1f, Funcs.offsetSin(10f * h, 5f), Funcs.offsetSin(90f + (10f * h), 5f), eyesAlpha);
                Draw.rect(regions[i], x + (eyeOffset.x * trnsScl[i]), y + (eyeOffset.y * trnsScl[i]), ringProgress[i]);
                Draw.z(oz + 0.025f);
                Draw.color(1f, Funcs.offsetSin(10f * h, 5f), Funcs.offsetSin(90f + (10f * h), 5f), lightsAlpha * Funcs.offsetSin(5 * h, 12f));
                Draw.rect(regionsB[i], x, y, ringProgress[i]);
            }

            Draw.blend();
            Draw.z(oz);
            //Draw.z(oz + 0.005f);
        }

        @Override
        public boolean shouldActiveSound(){
            return power.status >= 0.0001;
        }

        void killUnits(){
            entitySeq.clear();
            Units.nearbyEnemies(team, x - range, y - range, range * 2f, range * 2f, e -> {
                if(Mathf.within(x, y, e.x, e.y, range) && !e.dead){
                    Object[] data = {new Vec2(x + eyeOffset.x, y + eyeOffset.y), e, 1f};
                    UnityFx.vapourizeUnit.at(e.x, e.y, 0, e);
                    UnityFx.endgameLaser.at(x, y, 0, data);
                    entitySeq.add(e);
                }
            });
            entitySeq.each(Entityc::remove);
            entitySeq.clear();
        }

        void killTiles(){
            shouldLaser = 0;
            Vars.indexer.eachBlock(null, x, y, range, build -> build.team != team, building -> {
                if(!building.dead && building != this){
                    if(building.block.size >= 3) UnityFx.vapourizeTile.at(building.x, building.y, building.block.size, building);
                    if((shouldLaser % 5) == 0 || building.block.size >= 5){
                        Object[] data = {new Vec2(x + (eyeOffset.x * 2f), y + (eyeOffset.y * 2f)), building, 1f};
                        UnityFx.endgameLaser.at(x, y, 0, data);
                    }
                    building.kill();
                    shouldLaser++;
                }
            });
        }

        @Override
        public void kill(){
            if(lastHealth < 10f) super.kill();
        }

        void playerShoot(int index){
            final float rnge = 15f;
            float ux = unit.aimX();
            float uy = unit.aimY();
            if(!Mathf.within(x, y, ux, uy, range * 1.5f)) return;
            Vars.indexer.eachBlock(null, ux, uy, rnge, b -> b.team() != team && !b.dead(), building -> {
                building.damage(490f);
                Object[] data = {new Vec2(ux, uy), building, 0.525f};
                UnityFx.endgameLaser.at(x, y, 0, data);
            });
            Tmp.v1.set(eyesVecArray[index]);
            Tmp.v1.add(ux, uy);
            Tmp.v1.scl(0.5f);
            Object[] dataB = {eyesVecArray[index], new Vec2(ux, uy), 0.625f};
            UnityFx.endgameLaser.at(Tmp.v1.x, Tmp.v1.y, 0, dataB);
            UnitySounds.endgameSmallShoot.at(x, y);
        }

        void eyeShoot(int index){
            Healthc e = (Healthc)targets[index];
            if(e != null){
                e.damage(350 * threatLevel);
                if(e.dead()){
                    if(targets[index] instanceof Unit tmp) UnityFx.vapourizeUnit.at(tmp.x, tmp.y, tmp.rotation, tmp);
                    e.remove();
                }
                Object[] data = {eyesVecArray[index], e, 0.625f};
                UnityFx.endgameLaser.at(x, y, 0, data);
                //sound
                UnitySounds.endgameSmallShoot.at(x, y);
            }
        }

        void updateThreats(){
            threatLevel = 1f;
            Units.nearbyEnemies(team, x - range, y - range, range * 2, range * 2, e -> {
                if(within(e, range) && e.isAdded()){
                    threatLevel += Math.max((e.maxHealth() - 100f) / 410f, 0f);
                    if(e.realSpeed() >= 18f){
                        e.vel.setLength(0f);
                        //e.apply(UnityStatusEffects.endgameDisable);
                    }
                }
            });
        }

        void updateEyesTargeting(){
            for(int i = 0; i < 16; i++){
                if(Units.invalidateTarget(targets[i], team, x, y)){
                    targets[i] = null;
                }
            }
            updateThreats();
            if(timer.get(eyeTime, 15) && target != null && !isControlled()){
                entitySeq.clear();
                lowest = range + 999f;
                dstC = range + 999f;
                Vars.indexer.eachBlock(null, x, y, range, b -> b.team != team && !b.dead, build -> {
                    float dstD = Mathf.dst(x, y, build.x, build.y);

                    if(dstD < dstC){
                        lowest = Math.min(lowest, dstD);
                        dstC = dstD;
                        if(entitySeq.size > 16) entitySeq.remove(0);
                        entitySeq.add(build);
                    }else if(Mathf.equal(lowest, dstD, 32)){
                        if(entitySeq.size > 16) entitySeq.remove(0);
                        entitySeq.add(build);
                    }
                });
                for(int i = 0; i < 16; i++){
                    Posc tmpTarget = Funcs.targetUnique(team, x, y, range, new Seq<>(targets));
                    if(tmpTarget == null && entitySeq.size >= 1){
                        tmpTarget = (Posc)entitySeq.random();
                    }
                    targets[i] = tmpTarget;
                }
                entitySeq.clear();
            }
        }

        void updateEyesOffset(){
            for(int i = 0; i < 16; i++){
                float angleC = (360f / 8f) * (i * 8f);
                if(i >= 8){
                    Tmp.v1.trns(angleC + 22.5f + ringProgress[1], 25.75f);
                }else{
                    Tmp.v1.trns(angleC + ringProgress[0], 36.75f);
                }
                eyesVecArray[i].set(Tmp.v1).add(x, y);
            }
        }

        void updateAntiBullets(){
            entitySeq.clear();
            if(power.status > 0.0001f && timer.get(bulletTime, 4f / Math.max(power.status, 0.001f))){
                damageFull = 0f;
                Groups.bullet.intersect(x - range, y - range, range * 2f, range * 2f, b -> {
                    if(within(b, range) && b.team != team){
                        damageFull += Funcs.getBulletDamage(b.type);
                        BulletType current = b.type;
                        totalFrags = 1;
                        for(int i = 0; i < 16; i++){
                            if(current.fragBullet == null) break;

                            BulletType frag = current.fragBullet;
                            totalFrags *= current.fragBullets;
                            damageFull += Funcs.getBulletDamage(frag) * totalFrags;

                            current = frag;
                        }
                    }
                });
                Groups.bullet.intersect(x - range, y - range, range * 2f, range * 2f, b -> {
                    if(within(b, range) && b.team != team){
                        damageB = Funcs.getBulletDamage(b.type);
                        BulletType current = b.type;
                        totalFrags = 1;
                        for(int i = 0; i < 16; i++){
                            if(current.fragBullet == null) break;
                            BulletType frag = current.fragBullet;
                            totalFrags *= current.fragBullets;
                            damageB += Funcs.getBulletDamage(frag) * totalFrags;

                            current = frag;
                        }
                        if(damageB > 1600f || b.type.splashDamageRadius > 120f || damageFull + damageB > 13000f || (b.owner != null && !within((Posc)b.owner, range))){
                            entitySeq.add(b);
                            Object[] data = {new Vec2(x + (eyeOffset.x * 2f), y + (eyeOffset.y * 2f)), new Vec2(b.x, b.y), 0.625f};
                            UnityFx.endgameLaser.at(x, y, 0f, data);
                        }
                    }
                });
                if(!entitySeq.isEmpty()) UnitySounds.endgameSmallShoot.at(x, y);
                entitySeq.each(Entityc::remove);
                entitySeq.clear();
            }
        }

        boolean verify(){
            return (health < lastHealth - 860) || Float.isNaN(health);
        }

        void updateEyes(){
            updateEyesOffset();
            eyeOffsetB.lerpDelta(eyeTargetOffset, 0.12f);

            eyeOffset.set(eyeOffsetB);
            eyeOffset.add(Mathf.range(reload / reloadTime) / 2, Mathf.range(reload / reloadTime) / 2);
            eyeOffset.limit(2);
            if(((target != null && !isControlled()) || (isControlled() && unit.isShooting())) && consValid() && power.status >= 0.0001f){
                eyeReloads[0] += deltaB();
                eyeReloads[1] += deltaB();
            }

            if(consValid() && power.status > 0.0001){
                updateEyesTargeting();
            }

            if(eyeReloads[0] >= 15f){
                eyeReloads[0] = 0f;
                if(!isControlled()){
                    if(targets[eyeSequenceA] != null) eyeShoot(eyeSequenceA);
                }else{
                    if(unit.isShooting()) playerShoot(eyeSequenceA);
                }
                //eyeSequenceA = (eyeSequenceA + 1) % 8;
                eyeSequenceA += 1;
                eyeSequenceA %= 8;
            }
            if(eyeReloads[1] >= 5f){
                eyeReloads[1] = 0f;
                if(!isControlled()){
                    if(targets[eyeSequenceB] != null) eyeShoot(eyeSequenceB + 8);
                }else{
                    if(unit.isShooting()) playerShoot(eyeSequenceB + 8);
                }
                //eyeSequenceB = (eyeSequenceB + 1) % 8;
                eyeSequenceB += 1;
                eyeSequenceB %= 8;
            }
        }

        @Override
        public void updateTile(){
            lastHealth = health;
            if(resistTime >= 10){
                resist = Math.max(1, resist - Time.delta);
            }else{
                resistTime += Time.delta;
            }
            updateEyes();
            //super.updateTile();
            if(power.status > 0.0001f){
                float value = eyesAlpha > power.status ? 1f : power.status;
                eyesAlpha = Mathf.lerpDelta(eyesAlpha, power.status, 0.06f * value);
            }else{
                eyesAlpha = Mathf.lerpDelta(eyesAlpha, 0f, 0.06f);
            }
            if(consValid()){
                updateAntiBullets();
                super.updateTile();
            }
            if(isControlled()){
                Player con = (Player)unit.controller();
                eyeTargetOffset.trns(angleTo(con.mouseX, con.mouseY), dst(con.mouseX, con.mouseY) / (range / 3f));
                //eyeTargetOffset.limit(2f);
            }else if(target != null && power.status > 0.0001f){
                eyeTargetOffset.trns(angleTo(targetPos.x, targetPos.y), dst(targetPos.x, targetPos.y) / (range / 3f));
            }
            eyeTargetOffset.limit(2f);
            if(((target != null && !isControlled()) || (isControlled() && unit.isShooting())) && power.status > 0.0001f){
                eyeResetTime = 0f;
                float value = lightsAlpha > power.status ? 1f : power.status;
                lightsAlpha = Mathf.lerpDelta(lightsAlpha, power.status, 0.07f * value);
                for(int i = 0; i < 3; i++){
                    ringProgress[i] = Mathf.lerpDelta(ringProgress[i], 360f * (float)ringDirections[i], ringProgresses[i] * power.status);
                }

                float chance = (((reload / reloadTime) * 0.90f) + (1f - 0.90f)) * power.status;
                float randomAngle = Mathf.random(360f);
                Tmp.v1.trns(randomAngle, 18.5f);
                Tmp.v1.add(x, y);
                if(Mathf.chanceDelta(0.75 * chance)){
                    SlowLightning l = ExtraEffect.createSlowLightning(Tmp.v1.x, Tmp.v1.y, randomAngle, 80f);
                    l.team = team;
                    l.colorFrom = Color.red;
                    l.colorTo = Color.black;
                    l.damage = 520f * power.status;
                    l.range = 730f;
                    l.influence = targetPos;
                    l.add();
                }

            }else{
                if(eyeResetTime >= 60f){
                    lightsAlpha = Mathf.lerpDelta(lightsAlpha, 0f, 0.07f);
                    for(int i = 0; i < 3; i++){
                        ringProgress[i] = Mathf.lerpDelta(ringProgress[i], 0f, ringProgresses[i] * power.status);
                    }
                }else{
                    eyeResetTime += Time.delta;
                }
            }
        }

        @Override
        protected void shoot(BulletType type){
            consume();
            killTiles();
            killUnits();
            UnitySounds.endgameShoot.at(x, y);
        }

        @Override
        public boolean collision(Bullet other){
            float amount = other.owner != null && !within((Posc)other.owner, range) ? 0f : other.damage() * other.type.tileDamageMultiplier;
            damage(amount);
            if(other.owner != null && !within((Posc)other.owner, range)){
                Healthc en = (Healthc)other.owner;
                en.damage(0.5f * en.maxHealth() * Math.max(resist / 10f, 1f));
            }

            return super.collision(other);
        }

        @Override
        public void add(){
            if(isAdded()) return;
            for (int i = 0; i < 16; i++){
                eyesVecArray[i] = new Vec2();
                targets[i] = null;
            }
            super.add();
        }
    }
}
