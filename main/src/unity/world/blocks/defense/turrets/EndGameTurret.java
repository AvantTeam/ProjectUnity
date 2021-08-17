package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.*;
import unity.*;
import unity.content.*;
import unity.content.effects.*;
import unity.entities.*;
import unity.entities.effects.*;
import unity.gen.*;
import unity.mod.*;
import unity.util.*;

public class EndGameTurret extends PowerTurret{
    private static int shouldLaser = 0;
    protected static float damageFull;
    protected static float damageB;
    protected static int totalFrags;
    private final static float[] ringProgresses = {0.013f, 0.035f, 0.024f};
    private final static int[] ringDirections = {1, -1, 1};
    private final static Seq<Entityc> entitySeq = new Seq<>(512);

    protected int eyeTime = timers++;
    protected int bulletTime = timers++;

    public TextureRegion
    
    baseLightsRegion, bottomLightsRegion, eyeMainRegion,

    ringABottomRegion, ringAEyesRegion, ringARegion, ringALightsRegion,
    
    ringBBottomRegion, ringBEyesRegion, ringBRegion, ringBLightsRegion,

    ringCRegion, ringCLightsRegion;

    public EndGameTurret(String name){
        super(name);

        health = 68000;
        powerUse = 320f;
        reloadTime = 300f;
        absorbLasers = true;
        shootShake = 2.2f;
        outlineIcon = false;
        noUpdateDisabled = false;
        loopSound = UnitySounds.endgameActive;
        shootSound = UnitySounds.endgameShoot;
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
        protected float charge = 0f;
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
        //private final Seq<DeadUnitEntry> tmpArray = new Seq<>();

        @Override
        protected void effects(){
            shootSound.at(x, y);
        }

        @Override
        public void damage(float damage){
            if(verify()) return;
            if(damage > 10000) charge += Mathf.clamp(damage - 10000f, 0f, 2000000f) / 150f;
            if(charge > 15) charge = 15f;
            
            float trueAmount = Mathf.clamp(damage / resist, 0f, 410f);
            super.damage(trueAmount);
            
            resist += 0.125f + (Mathf.clamp(damage - 520f, 0f, 2147483647f) / 70f);
            if(Float.isNaN(resist)) resist = Float.MAX_VALUE;
            resistTime = 0f;
        }

        @Override
        protected float baseReloadSpeed(){
            return Mathf.clamp(efficiency() + charge, 0f, 1.2f);
        }

        float trueEfficiency(){
            return Mathf.clamp(efficiency() + charge);
        }

        float deltaB(){
            return (delta() * baseReloadSpeed());
        }

        @Override
        public boolean consValid(){
            boolean valid = false;
            if(block.consumes.hasPower()){
                valid = block.consumes.getPower().valid(this);
            }
            valid |= charge > 0.001f;
            if(block.consumes.has(ConsumeType.item)){
                valid &= block.consumes.getItem().valid(this);
            }
            return valid;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            charge = read.f();
            resist = read.f();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(charge);
            write.f(resist);
        }

        @Override
        public void draw(){
            float oz = Draw.z();
            Draw.rect(baseRegion, x, y);
            
            Draw.z(oz + 0.01f);
            Draw.rect(ringABottomRegion, x, y, ringProgress[0]);
            Draw.rect(ringBBottomRegion, x, y, ringProgress[1]);
            
            Draw.z(oz + 0.02f);
            Drawf.spinSprite(ringARegion, x, y, ringProgress[0]);
            Drawf.spinSprite(ringBRegion, x, y, ringProgress[1]);
            Drawf.spinSprite(ringCRegion, x, y, ringProgress[2]);

            Draw.blend(Blending.additive);
            
            Draw.z(oz + 0.005f);
            Draw.color(1f, Utils.offsetSin(0f, 5f), Utils.offsetSin(90f, 5f), eyesAlpha);
            Draw.rect(bottomLightsRegion, x, y);
            Draw.color(1f, Utils.offsetSin(0f, 5f), Utils.offsetSin(90f, 5f), lightsAlpha * Utils.offsetSin(0f, 12f));
            Draw.rect(baseLightsRegion, x, y);
            //Draw.z(oz + 0.015f);

            TextureRegion[] regions = {ringAEyesRegion, ringBEyesRegion, eyeMainRegion};
            TextureRegion[] regionsB = {ringALightsRegion, ringBLightsRegion, ringCLightsRegion};
            float[] trnsScl = {1f, 0.9f, 2f};

            for(int i = 0; i < 3; i++){
                int h = i + 1;
                Draw.z(oz + 0.015f);
                Draw.color(1f, Utils.offsetSin(10f * h, 5f), Utils.offsetSin(90f + (10f * h), 5f), eyesAlpha);
                Draw.rect(regions[i], x + (eyeOffset.x * trnsScl[i]), y + (eyeOffset.y * trnsScl[i]), ringProgress[i]);
                
                Draw.z(oz + 0.025f);
                Draw.color(1f, Utils.offsetSin(10f * h, 5f), Utils.offsetSin(90f + (10f * h), 5f), lightsAlpha * Utils.offsetSin(5 * h, 12f));
                Draw.rect(regionsB[i], x, y, ringProgress[i]);
            }

            Draw.blend();
            Draw.z(oz);
            //Draw.z(oz + 0.005f);
        }

        @Override
        public boolean shouldActiveSound(){
            return trueEfficiency() >= 0.0001;
        }

        void killUnits(){
            entitySeq.clear();
            Units.nearbyEnemies(team, x - range, y - range, range * 2f, range * 2f, e -> {
                if(Mathf.within(x, y, e.x, e.y, range) && !e.dead){
                    Object[] data = {new Vec2(x + eyeOffset.x, y + eyeOffset.y), e, 1f};
                    UnityFx.endgameLaser.at(x, y, 0, data);
                    entitySeq.add(e);
                }
            });
            entitySeq.each(e -> {
                AntiCheat.annihilateEntity(e, true, false);
                Unit u = (Unit)e;
                SpecialFx.endgameVapourize.at(u.x, u.y, angleTo(u), new Object[]{this, u});
            });
            entitySeq.clear();
        }

        void killTiles(){
            entitySeq.clear();
            damageB = 0f;
            shouldLaser = 0;
            Utils.trueEachBlock(x, y, range + 5f, build -> build.team != team, building -> {
                if(!building.dead && building != this){
                    if(building.block.size >= 3) UnityFx.vapourizeTile.at(building.x, building.y, building.block.size, building);
                    if((shouldLaser % 5) == 0 || building.block.size >= 5){
                        Object[] data = {new Vec2(x + (eyeOffset.x * 2f), y + (eyeOffset.y * 2f)), building, 1f};
                        UnityFx.endgameLaser.at(x, y, 0, data);
                    }
                    //building.kill();
                    entitySeq.add(building);
                    shouldLaser++;
                }
            });
            entitySeq.each(e -> {
                damageB = Math.max(((Posc)e).dst2(this), damageB);
                AntiCheat.annihilateEntity(e, true, false);
            });
            damageB = Mathf.sqrt(damageB) * 2f;
            SpecialFx.endgameVapourize.at(x, y, damageB, new Object[]{this, entitySeq.toArray(Building.class), hitSize() / 4f});
            entitySeq.clear();
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
            Utils.trueEachBlock(ux, uy, rnge, b -> b.team() != team && !b.dead(), building -> {
                building.damage(490f);
                Object[] data = {new Vec2(ux, uy), building, 0.525f};
                UnityFx.endgameLaser.at(x, y, 0, data);
            });
            
            Units.nearbyEnemies(team, ux - rnge, uy - range, range * 2f, range * 2f, e -> {
                if(e.within(ux, uy, rnge + e.hitSize) && !e.dead){
                    e.damage(490f * threatLevel);
                    if(e.dead){
                        SpecialFx.endgameVapourize.at(e.x, e.y, angleTo(e), new Object[]{this, e});
                        //annihilate(e);
                        AntiCheat.annihilateEntity(e, true, false);
                    }
                    UnityFx.endgameLaser.at(x, y, 0, new Object[]{new Vec2(ux, uy), e, 0.525f});
                }
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
                e.damage(350f * threatLevel);
                if(e.dead()){
                    //annihilate(targets[index]);
                    if(e instanceof Unit ut) SpecialFx.endgameVapourize.at(ut.x, ut.y, angleTo(ut), new Object[]{this, ut});
                    if(e instanceof Building build) UnityFx.vapourizeTile.at(build.x, build.y, build.block.size);
                    AntiCheat.annihilateEntity(e, true, false);
                }
                Object[] data = {eyesVecArray[index], e, 0.625f};
                
                UnityFx.endgameLaser.at(x, y, 0, data);
                UnitySounds.endgameSmallShoot.at(x, y);
            }
        }

        void updateThreats(){
            threatLevel = 1f;
            Units.nearbyEnemies(team, x - range, y - range, range * 2, range * 2, e -> {
                if(within(e, range) && e.isAdded()){
                    threatLevel += Math.max(((e.maxHealth() + e.type.dpsEstimate) - 450f) / 1300f, 0f);
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
                Seq<Healthc> nTargets = Utils.nearbyEnemySorted(team, x, y, range, 8f);
                if(!nTargets.isEmpty()){
                    for(int i = 0; i < targets.length; i++){
                        targets[i] = nTargets.get(i % nTargets.size);
                    }
                }
            }
        }

        void updateEyesOffset(){
            for(int i = 0; i < 16; i++){
                float angleC = ((360f / 8f) * (i % 8f));
                if(i >= 8){
                    Tmp.v1.trns(angleC + 22.5f + ringProgress[1], 25.75f);
                }else{
                    Tmp.v1.trns(angleC + ringProgress[0], 36.75f);
                }
                eyesVecArray[i].set(Tmp.v1.x, Tmp.v1.y).add(x, y);
            }
        }

        void updateAntiBullets(){
            entitySeq.clear();
            if(trueEfficiency() > 0.0001f && timer.get(bulletTime, 4f / Math.max(trueEfficiency(), 0.001f))){
                damageFull = 0f;
                Groups.bullet.intersect(x - range, y - range, range * 2f, range * 2f, b -> {
                    if(within(b, range) && b.team != team){
                        damageFull += Utils.getBulletDamage(b.type);
                        BulletType current = b.type;
                        totalFrags = 1;
                        
                        for(int i = 0; i < 16; i++){
                            if(current.fragBullet == null) break;

                            BulletType frag = current.fragBullet;
                            totalFrags *= current.fragBullets;
                            damageFull += Utils.getBulletDamage(frag) * totalFrags;

                            current = frag;
                        }
                    }
                });
                Groups.bullet.intersect(x - range, y - range, range * 2f, range * 2f, b -> {
                    if(within(b, range) && b.team != team){
                        damageB = Utils.getBulletDamage(b.type);
                        BulletType current = b.type;
                        totalFrags = 1;
                        for(int i = 0; i < 16; i++){
                            if(current.fragBullet == null) break;
                            BulletType frag = current.fragBullet;
                            totalFrags *= current.fragBullets;
                            damageB += Utils.getBulletDamage(frag) * totalFrags;

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
            return (health < lastHealth - 860f) || Float.isNaN(health);
        }

        void updateEyes(){
            updateEyesOffset();
            eyeOffsetB.lerpDelta(eyeTargetOffset, 0.12f);

            eyeOffset.set(eyeOffsetB);
            eyeOffset.add(Mathf.range(reload / reloadTime) / 2, Mathf.range(reload / reloadTime) / 2);
            eyeOffset.limit(2);
            if(((target != null && !isControlled()) || (isControlled() && unit.isShooting())) && consValid() && trueEfficiency() >= 0.0001f){
                eyeReloads[0] += deltaB();
                eyeReloads[1] += deltaB();
            }

            if(consValid() && trueEfficiency() > 0.0001){
                updateEyesTargeting();
            }

            if(eyeReloads[0] >= 15f){
                eyeReloads[0] = 0f;
                if(!isControlled()){
                    if(targets[eyeSequenceA] != null) eyeShoot(eyeSequenceA);
                }else{
                    if(unit.isShooting()) playerShoot(eyeSequenceA);
                }
                eyeSequenceA = (eyeSequenceA + 1) % 8;
            }
            if(eyeReloads[1] >= 5f){
                eyeReloads[1] = 0f;
                if(!isControlled()){
                    if(targets[eyeSequenceB] != null) eyeShoot(eyeSequenceB + 8);
                }else{
                    if(unit.isShooting()) playerShoot(eyeSequenceB + 8);
                }
                eyeSequenceB = (eyeSequenceB + 1) % 8;
            }
        }

        @Override
        public void updateTile(){
            enabled = true;
            lastHealth = health;
            charge = Math.max(0f, charge - (Time.delta / 20f));
            
            if(resistTime >= 15){
                resist = Math.max(1f, resist - Time.delta);
            }else{
                resistTime += Time.delta;
            }
            updateEyes();
            //super.updateTile();
            if(trueEfficiency() > 0.0001f){
                float value = eyesAlpha > trueEfficiency() ? 1f : trueEfficiency();
                eyesAlpha = Mathf.lerpDelta(eyesAlpha, trueEfficiency(), 0.06f * value);
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
            }else if(target != null && trueEfficiency() > 0.0001f){
                eyeTargetOffset.trns(angleTo(targetPos.x, targetPos.y), dst(targetPos.x, targetPos.y) / (range / 3f));
            }
            eyeTargetOffset.limit(2f);
            
            if(((target != null && !isControlled()) || (isControlled() && unit.isShooting())) && trueEfficiency() > 0.0001f){
                eyeResetTime = 0f;
                float value = lightsAlpha > trueEfficiency() ? 1f : trueEfficiency();
                lightsAlpha = Mathf.lerpDelta(lightsAlpha, trueEfficiency(), 0.07f * value);
                
                for(int i = 0; i < 3; i++){
                    ringProgress[i] = Mathf.lerpDelta(ringProgress[i], 360f * (float)ringDirections[i], ringProgresses[i] * trueEfficiency());
                }

                float chance = (((reload / reloadTime) * 0.90f) + (1f - 0.90f)) * trueEfficiency();
                float randomAngle = Mathf.random(360f);
                Tmp.v1.trns(randomAngle, 18.5f);
                Tmp.v1.add(x, y);
                
                if(Mathf.chanceDelta(0.75 * chance)){
                    SlowLightning l = ExtraEffect.createSlowLightning(Tmp.v1.x, Tmp.v1.y, randomAngle, 110f);
                    l.team = team;
                    l.colorFrom = Color.red;
                    l.colorTo = Color.black;
                    l.splitChance = 0.045f;
                    //l.damage = 520f * power.status;
                    l.liveDamage = () -> 520f * trueEfficiency();
                    l.range = 790f;
                    l.influence = targetPos;
                    l.add();
                }

            }else{
                if(eyeResetTime >= 60f){
                    lightsAlpha = Mathf.lerpDelta(lightsAlpha, 0f, 0.07f);
                    for(int i = 0; i < 3; i++){
                        ringProgress[i] = Mathf.lerpDelta(ringProgress[i], 0f, ringProgresses[i] * trueEfficiency());
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
            
            ShootFx.endGameShoot.at(x, y);
            UnitySounds.endgameShoot.at(x, y, 1f, 1.5f);
        }

        @Override
        public boolean collision(Bullet other){
            float amount = other.owner != null && !within((Posc)other.owner, range) ? 0f : other.damage() * other.type.buildingDamageMultiplier;
            damage(amount);
            if(other.owner != null && !within((Posc)other.owner, range)){
                Healthc en = (Healthc)other.owner;
                en.damage(0.5f * en.maxHealth() * Math.max(resist / 10f, 1f));
            }

            return true;
            //return super.collision(other);
        }

        @Override
        public void add(){
            if(isAdded()) return;
            for(int i = 0; i < 16; i++){
                eyesVecArray[i] = new Vec2();
                targets[i] = null;
            }
            
            super.add();
            Unity.antiCheat.addBuilding(this);
        }

        @Override
        public void remove(){
            if(!isAdded()) return;
            if(lastHealth <= 0){
                Unity.antiCheat.removeBuilding(this);
            }
            
            super.remove();
        }
    }
}
