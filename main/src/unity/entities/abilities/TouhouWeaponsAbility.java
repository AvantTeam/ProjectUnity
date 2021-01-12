package unity.entities.abilities;

import arc.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.entities.*;
import unity.type.*;

import java.util.*;

public class TouhouWeaponsAbility extends Ability{
    public TouhouWeaponState[] stateType;
    public float powerLimit = 700f;
    protected float focusLerp = 0f;
    protected float power = 0f;
    protected int changed = 0;
    protected float[] reloads;

    public TouhouWeaponsAbility(TouhouWeaponState... states){
        stateType = new TouhouWeaponState[states.length];
        System.arraycopy(states, 0, stateType, 0, states.length);
        int tmpSizeB = 0;
        for(TouhouWeaponState a : stateType){
            int tmpSize = a.types.length;
            tmpSizeB = Math.max(tmpSizeB, tmpSize);
        }
        reloads = new float[tmpSizeB];
        Arrays.fill(reloads, 0f);
    }

    @Override
    public Ability copy(){
        TouhouWeaponsAbility tmp = (TouhouWeaponsAbility)super.copy();
        tmp.reloads = Arrays.copyOf(reloads, reloads.length);
        return tmp;
    }

    @Override
    public void update(Unit unit){
        int tc = Mathf.round((power / powerLimit) * (stateType.length - 1));
        if(changed != tc) Arrays.fill(reloads, 0f);
        if(unit.isShooting()){
            stateType[tc].update(unit, this);

            //for testing
            addPower(Time.delta * 0.3f);
        }
        //TODO sync
        if(!Vars.headless && (Core.input.keyDown(KeyCode.shiftLeft) && unit.controller() == Vars.player)){
            focusLerp = Mathf.lerpDelta(focusLerp, 1f, 0.3f);
        }else{
            focusLerp = Mathf.lerpDelta(focusLerp, 0f, 0.3f);
        }
        changed = tc;
    }

    @Override
    public void draw(Unit unit){
        stateType[changed].draw(unit, this);
    }

    public void addPower(float amount){
        power = Math.min(powerLimit, power + amount);
    }

    public static class TouhouWeaponState{
        TouhouWeapon[] types;

        public TouhouWeaponState(TouhouWeapon... types){
            this.types = new TouhouWeapon[types.length];
            System.arraycopy(types, 0, this.types, 0, types.length);
        }

        public void draw(Unit unit, TouhouWeaponsAbility source){
            if(!(unit.type instanceof UnityUnitType)) return;
            TextureRegion region = ((UnityUnitType)unit.type).abilityRegions[AbilityTextures.shooter.ordinal()];
            for(TouhouWeapon t : types){
                if(!t.shouldDraw) continue;
                if(t.mirror){
                    for(int i : Mathf.signs){
                        Tmp.v2.set(t.unfocusedPosition).lerp(t.focusedPosition, source.focusLerp).scl(i, 1f).rotate(unit.rotation - 90f).add(unit);
                        Draw.rect(region, Tmp.v2.x, Tmp.v2.y, unit.rotation - 90f);
                    }
                }else{
                    Tmp.v2.set(t.unfocusedPosition).lerp(t.focusedPosition, source.focusLerp).rotate(unit.rotation - 90f).add(unit);
                    Draw.rect(region, Tmp.v2.x, Tmp.v2.y, unit.rotation - 90f);
                }
            }
        }

        public void update(Unit unit, TouhouWeaponsAbility source){
            int index = 0;
            boolean focused = source.focusLerp >= 0.5f;
            for(TouhouWeapon t : types){
                source.reloads[index] += Time.delta * unit.reloadMultiplier();
                float freload = focused ? t.reload : t.unfReload;
                if(source.reloads[index] >= freload){
                    int fshots = focused ? t.shots : t.unfocusedShots;
                    float fspacing = focused ? t.spacing : t.unfocusedSpacing;
                    float foffset = focused ? t.offsetAngle : t.unfocusedOffset;
                    float fdelay = focused ? t.shotDelay : t.unfShotDelay;
                    BulletType ftype = focused || t.unfBullet == null ? t.bullet : t.unfBullet;
                    if(t.mirror){
                        for(int i : Mathf.signs){
                            Tmp.v1.set(t.unfocusedPosition).lerp(t.focusedPosition, source.focusLerp).scl(i, 1f).rotate(unit.rotation - 90f).add(unit);
                            //t.bullet.create(unit, Tmp.v1.x, Tmp.v1.y, unit.rotation);
                            for(int j = 0; j < fshots; j++){
                                float angle = (j * fspacing - (fshots - 1) * fspacing / 2f) + unit.rotation + (foffset * i);
                                if(fdelay < 0.01f){
                                    ftype.create(unit, Tmp.v1.x, Tmp.v1.y, angle);
                                }else{
                                    Time.run(fdelay * j, new BulletShotRunnable(unit, ftype, Tmp.v1, angle));
                                }
                            }
                        }
                    }else{
                        Tmp.v1.set(t.unfocusedPosition).lerp(t.focusedPosition, source.focusLerp).rotate(unit.rotation - 90f).add(unit);
                        for(int j = 0; j < fshots; j++){
                            float angle = (j * fspacing - (fshots - 1) * fspacing / 2f) + unit.rotation;
                            if(fdelay < 0.01f){
                                ftype.create(unit, Tmp.v1.x, Tmp.v1.y, angle);
                            }else{
                                Time.run(fdelay * j, new BulletShotRunnable(unit, ftype, Tmp.v1, angle));
                            }
                        }
                        //index++;
                    }
                    source.reloads[index] = 0f;
                }
                index++;
            }
        }
    }

    public static class TouhouWeapon{
        public int shots = 1;
        public int unfocusedShots = 1;
        public float spacing = 0f;
        public float unfocusedSpacing = 0f;
        public float offsetAngle = 0f;
        public float unfocusedOffset = 0f;
        public float reload = 9f;
        public float unfReload = 9f;
        public float shotDelay = 0f;
        public float unfShotDelay = 0f;
        public BulletType bullet;
        public BulletType unfBullet;
        public Vec2 unfocusedPosition = new Vec2();
        public Vec2 focusedPosition = new Vec2();
        public boolean mirror = false;
        public boolean shouldDraw = true;

        public TouhouWeapon(){

        }

        public TouhouWeapon(TouhouWeapon other){
            shots = other.shots;
            unfocusedShots = other.unfocusedShots;
            spacing = other.spacing;
            unfocusedSpacing = other.unfocusedSpacing;
            offsetAngle = other.offsetAngle;
            unfocusedOffset = other.unfocusedOffset;
            reload = other.reload;
            unfReload = other.unfReload;
            shotDelay = other.shotDelay;
            unfShotDelay = other.unfShotDelay;
            unfocusedPosition.set(other.unfocusedPosition);
            focusedPosition.set(other.focusedPosition);
            bullet = other.bullet;
            unfBullet = other.unfBullet;
            mirror = other.mirror;
            shouldDraw = other.shouldDraw;
        }
    }

    static class BulletShotRunnable implements Runnable{
        public BulletType bt;
        public Vec3 vc = new Vec3();
        public Unit unit;

        BulletShotRunnable(Unit unit, BulletType type, Vec2 pos, float angle){
            bt = type;
            vc.set(pos, angle);
            vc.sub(unit.x, unit.y, 0f);
            this.unit = unit;
        }

        @Override
        public void run(){
            vc.add(unit.x, unit.y, 0f);
            bt.create(unit, vc.x, vc.y, vc.z);
        }
    }
}
