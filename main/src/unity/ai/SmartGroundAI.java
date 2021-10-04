package unity.ai;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.math.geom.QuadTree.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.Wall.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.meta.*;
import unity.type.*;
import unity.util.*;

import static mindustry.Vars.*;

public class SmartGroundAI extends AIController{
    static float tmpAngle = 0f, tmpAngleB;

    Seq<WeaponMount> mainMounts = new Seq<>();
    float mainMountsRange = -1f;

    FloatSeq score = new FloatSeq(), tmpf = new FloatSeq();
    Seq<Healthc> targets = new Seq<>(), tmp = new Seq<>();
    Healthc[] targetArray;
    QuadTree<QuadTreeObject> tree = new QuadTree<>(new Rect(-finalWorldBounds, -finalWorldBounds, world.width() * tilesize + finalWorldBounds * 2f, world.height() * tilesize + finalWorldBounds * 2f));
    IntSet occupied = new IntSet();
    float retarget, aimX, aimY;
    int targetIdx;
    boolean targeting = false, set = false;

    float angDst(float dst, float width){
        return Angles.angle(dst, -width) % 180f;
    }

    void updateScoring(){
        boolean tmr = timer.get(1, 20f);
        if(tmr) tree.clear();

        UnitType t = unit.type;
        tmp.clear();
        //tree.clear();
        score.clear();
        tmpAngle = -361f;
        tmpAngleB = -361f;
        targeting = false;
        set = false;
        if(!targets.isEmpty()){
            targets.removeAll(h -> {
                boolean invalid = !h.isAdded() || Units.invalidateTarget(h, unit.team, unit.x, unit.y, t.maxRange);
                if(!invalid){
                    if(unit.within(h, mainMountsRange)){
                        if(h instanceof Unit){
                            Unit u = (Unit)h;
                            tmpf.add(u.x, u.y, u.health + u.type.dpsEstimate);
                            updateScore(unit.angleTo(u), unit.dst(u), false);
                        }else if(h instanceof Building){
                            Building b = (Building)h;
                            float sc = b.health;
                            //float sc = b instanceof TurretBuild tr && tr.hasAmmo() ? ((tr.peekAmmo().estimateDPS() / ((Turret)tr.block).reloadTime) * ((Turret)tr.block).shots) : 0f;
                            if(b instanceof TurretBuild){
                                TurretBuild tr = (TurretBuild)b;
                                Turret tt = (Turret)b.block;
                                sc += tr.hasAmmo() ? ((tr.peekAmmo().estimateDPS() / tt.reloadTime) * tt.shots) : 0f;
                            }else if(b instanceof WallBuild){
                                sc *= -1f;
                            }else if(b instanceof CoreBuild){
                                sc *= 10f;
                            }
                            tmpf.add(b.x, b.y, sc);
                            updateScore(unit.angleTo(b), unit.dst(b), false);
                        }
                        float an = unit.angleTo(h);
                        if(tmpAngleB == -361f) tmpAngleB = an;
                        tmpAngle = an;
                    }
                    //tree.insert((QuadTreeObject)h);
                    //if(tmr) tmp.add(h);
                    if(tmr) tree.insert((QuadTreeObject)h);
                }
                return invalid;
            });
            updateScore(-1f, -1f, true);

            if(!targets.isEmpty()){
                if(retarget <= 0f || targetIdx + 2 >= score.size){
                    float ls = -Float.MAX_VALUE;

                    for(int i = 0; i < score.size; i += 3){
                        float s = score.items[i + 2];
                        if(s > ls){
                            targetIdx = i;
                            ls = s;
                        }
                    }

                    retarget = 120f;
                }
                float tx = score.items[targetIdx];
                float ty = score.items[targetIdx + 1];
                for(WeaponMount m : mainMounts){
                    targeting |= m.weapon.bullet.damage >= score.items[targetIdx + 2] * 2f;

                    m.aimX = tx + unit.x;
                    m.aimY = ty + unit.y;
                    m.rotate = m.shoot = true;
                }
                aimX = tx;
                aimY = ty;
                set = true;
            }
        }
        retarget = Math.max(retarget - Time.delta, 0f);

        if(tmr){
            occupied.clear();

            for(int i = 0; i < unit.mounts.length; i++){
                WeaponMount m = unit.mounts[i];
                Weapon w = unit.mounts[i].weapon;
                if(!w.rotate || w.rotateSpeed <= 1f || m.target != null) continue;
                tmp.clear();

                float
                weaponRotation = unit.rotation + m.rotation,
                mountX = unit.x + Angles.trnsx(unit.rotation - 90, w.x, w.y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, w.x, w.y),
                range = w.bullet.range();

                Rect r = Tmp.r1.setCentered(mountX, mountY, range * 2f);

                tree.intersect(r, q -> {
                    Healthc h = (Healthc)q;
                    if(h.within(mountX, mountY, range + (((Sized)q).hitSize() / 2f))) tmp.add(h);
                });

                if(!tmp.isEmpty()){
                    tmp.sort(h -> {
                        float angScore = Mathf.clamp(Utils.angleDist(weaponRotation, h.angleTo(mountX, mountY) + 180f) / 180f);
                        angScore = angScore + (1f - angScore) * 0.5f;
                        float score = 0f;
                        float dst = h.dst(mountX, mountY);

                        if(occupied.contains(h.id())) score += 1000000f;
                        if(h instanceof Unit && angDst(dst, unit.deltaLen() * 2f) <= w.rotateSpeed) score += 1000000f;

                        return (dst * angScore) + score;
                    });
                    Healthc target = tmp.first();
                    occupied.add(target.id());
                    m.target = (Teamc)target;
                }
            }
        }
        for(WeaponMount m : unit.mounts){
            Weapon w = m.weapon;
            if(!w.rotate || w.rotateSpeed <= 1f) continue;
            float
            mountX = unit.x + Angles.trnsx(unit.rotation - 90, w.x, w.y),
            mountY = unit.y + Angles.trnsy(unit.rotation - 90, w.x, w.y);
            if(Units.invalidateTarget(m.target, unit.team, mountX, mountY, m.weapon.bullet.range())) m.target = null;
            if(m.target != null){
                if(w.predictTarget){
                    Vec2 to = Predict.intercept(unit, m.target, w.bullet.speed);
                    m.aimX = to.x;
                    m.aimY = to.y;
                }else{
                    m.aimX = m.target.x();
                    m.aimY = m.target.y();
                }
                m.rotate = m.shoot = true;
            }
        }
    }

    @Override
    public void updateTargeting(){
        if(targetArray == null || targetArray.length != unit.mounts.length){
            targetArray = new Healthc[unit.mounts.length];
        }

        UnitType t = unit.type;
        if(timer.get(0, 15f)){
            targets.clear();
            Rect r = Tmp.r1.setCentered(unit.x, unit.y, t.maxRange * 2f);

            Groups.unit.intersect(r.x, r.y, r.x + r.width, r.y + r.height, u -> {
                if(u.team != unit.team && unit.within(u, t.maxRange + (u.hitSize / 2f))){
                    targets.add(u);
                }
            });
            Vars.indexer.allBuildings(unit.x, unit.y, t.maxRange, b -> {
                if(b.team != unit.team){
                    targets.add(b);
                }
            });
            targets.sort(u -> Utils.angleDistSigned(unit.rotation, unit.angleTo(u)));
        }
        updateScoring();
    }

    void updateScore(float rotation, float dst, boolean finalize){
        float adst = angDst(dst, ((UnityUnitType)unit.type).bulletWidth / 2f);
        if(tmpAngleB != 361f && Utils.angleDist(tmpAngleB, rotation) > adst * 2f){
            finalize = true;
        }
        if(finalize || (tmpAngle != 361f && Utils.angleDist(tmpAngle, rotation) > adst)){
            float fx = 0f;
            float fy = 0f;
            float fs = 0f;
            int size = 0;
            for(int i = 0; i < tmpf.size; i += 3){
                fx += tmpf.items[i] - unit.x;
                fy += tmpf.items[i + 1] - unit.y;
                fs += tmpf.items[i + 2];
                size++;
            }
            //score.add(Angles.angle(fx, fy), fs / size);
            score.add(fx / size, fy / size, fs);
            tmpf.clear();
            tmpAngleB = rotation;
        }
    }

    @Override
    public void updateMovement(){
        if(!targeting){
            Building core = unit.closestEnemyCore();

            if((core == null || !unit.within(core, unit.type.range * 0.5f)) && command() == UnitCommand.attack){
                boolean move = true;

                if(state.rules.waves && unit.team == state.rules.defaultTeam){
                    Tile spawner = getClosestSpawner();
                    if(spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)) move = false;
                }

                if(move) pathfind(Pathfinder.fieldCore);
            }

            if(command() == UnitCommand.rally){
                Teamc target = targetFlag(unit.x, unit.y, BlockFlag.rally, false);

                if(target != null && !unit.within(target, 70f)){
                    pathfind(Pathfinder.fieldRally);
                }
            }
        }

        if(unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()){
            unit.elevation = Mathf.approachDelta(unit.elevation, 0f, unit.type.riseSpeed);
        }

        faceTarget();
    }

    @Override
    public void faceTarget(){
        if(!set){
            super.faceTarget();
        }else{
            unit.lookAt(Angles.angle(aimX, aimY));
        }
    }

    @Override
    public void init(){
        Core.app.post(() -> {
            mainMounts.clear();
            for(WeaponMount m : unit.mounts){
                if(!m.weapon.rotate || m.weapon.rotateSpeed <= 1f){
                    mainMounts.add(m);
                    if(mainMountsRange < 0 || m.weapon.bullet.range() < mainMountsRange) mainMountsRange = m.weapon.bullet.range();
                }
            }
        });
    }
}
