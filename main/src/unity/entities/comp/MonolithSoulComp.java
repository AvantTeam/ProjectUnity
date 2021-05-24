package unity.entities.comp;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.gen.*;
import unity.gen.SoulHoldc.*;
import unity.mod.*;
import unity.sync.*;
import unity.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, MonolithSoulc.class, Factionc.class})
@EntityComponent
abstract class MonolithSoulComp implements Unitc, Factionc{
    static final Rect rec1 = new Rect();
    static final Rect rec2 = new Rect();

    static final Prov<UnitType> defaultType = () -> UnityUnitTypes.monolithSoul;

    float healAmount;

    @Import UnitType type;
    @Import UnitController controller;
    @Import Team team;
    @Import float x, y, rotation, health, maxHealth, hitSize;
    @Import boolean dead;
    @Import int id;

    @Override
    public Faction faction(){
        return FactionMeta.map(type);
    }

    @Override
    public void update(){
        if(controller == null){
            kill();
        }else{
            health -= maxHealth / (5f * Time.toSeconds) * Time.delta;

            if(!dead && (net.server() || !net.active())){
                boolean[] invoked = {false};
                Units.nearby(team, x, y, hitSize, unit -> {
                    if(!invoked[0] && !dead && !unit.dead && unit instanceof Monolithc soul && soul.canJoin() && isSameFaction(unit)){
                        hitbox(rec1);
                        unit.hitbox(rec2);

                        if(rec1.overlaps(rec2)){
                            invoked[0] = true;
                            invoke(unit);

                            if(isPlayer() && !unit.isPlayer()){
                                getPlayer().unit(unit);
                            }
                        }
                    }
                });

                if(!invoked[0]){
                    indexer.eachBlock(this, hitSize, b -> {
                        if(!invoked[0] && b instanceof SoulBuildc soul && soul.canJoin()){
                            hitbox(rec1);
                            b.hitbox(rec2);
                            return rec1.overlaps(rec2);
                        }else{
                            return false;
                        }
                    }, b -> {
                        if(!invoked[0]){
                            invoked[0] = true;
                            invoke(b);

                            if(isPlayer() && b instanceof SoulBuildc cont && (cont.canControl() && !cont.isControlled())){
                                getPlayer().unit(cont.unit());
                            }
                        }
                    });
                }
            }
        }

        if(Mathf.chanceDelta(0.5f)){
            UnityFx.monolithSoul.at(x, y, rotation, hitSize * 1.5f);
        }
    }

    @Override
    @Replace
    public int cap(){
        return Integer.MAX_VALUE;
    }

    public <T extends Entityc> void invoke(T ent){
        UnityCall.soulJoin(self(), ent);
    }

    @Override
    @MethodPriority(-1)
    @BreakAll
    public void hitbox(Rect rect){
        Class<?> caller = ReflectUtils.classCaller();
        if(caller != null && QuadTree.class.isAssignableFrom(caller)){
            rect.set(x, y, Float.NaN, Float.NaN);
            return;
        }
    }
}
