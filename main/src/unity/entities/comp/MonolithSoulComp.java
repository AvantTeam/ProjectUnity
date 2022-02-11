package unity.entities.comp;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.content.units.*;
import unity.entities.*;
import unity.gen.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, MonolithSoulc.class, Trailc.class, Factionc.class})
@EntityComponent
abstract class MonolithSoulComp implements Unitc, Trailc, Factionc{
    @Import UnitType type;
    @Import Team team;
    @Import float x, y, health, maxHealth;

    @ReadOnly transient boolean corporeal;
    @ReadOnly transient float joinTime;
    @ReadOnly transient Teamc joinTarget;
    @ReadOnly transient Seq<Tile> forms = new Seq<>(5);

    @Override
    public Faction faction(){
        return Faction.monolith;
    }

    @Override
    public void add(){
        health = Math.min(maxHealth / 2f, health);
    }

    @Override
    @MethodPriority(-1)
    public void update(){
        if(!corporeal){
            health = Mathf.clamp(health + lifeDelta() * Time.delta, 0f, maxHealth);
            joinTime = (joinTarget == null || !joinTarget.isAdded()) ? Mathf.lerpDelta(joinTime, 0f, 0.2f) : Mathf.approachDelta(joinTime, 1f, 0.08f);

            if(!joinValid(joinTarget)) joinTarget = null;
            forms.removeAll(t -> !formValid(t));
        }else if(health <= maxHealth * 0.5f){
            corporeal = false;
        }

        if(Mathf.equal(joinTime, 1f)){
            // join the target
        }else if(!corporeal && Mathf.equal(health, maxHealth)){
            corporeal = true;
            joinTime = 0f;
        }
    }

    void join(Teamc other){
        if(!joinValid(other)) return;

        if(forms.any()) forms.clear();
        joinTarget = other;
    }

    void form(Tile tile){
        if(forms.contains(tile)){
            forms.remove(tile);
            return;
        }

        if(!formValid(tile)) return;
        if(forms.size >= 5) forms.remove(0);

        joinTarget = null;
        forms.add(tile);
    }

    boolean joinValid(Teamc other){
        Soul soul = Soul.toSoul(other);
        return soul != null && other.isAdded() && soul.acceptSoul(this) > 1 && within(other, type.range);
    }

    boolean formValid(Tile tile){
        if(tile.synthetic() || Mathf.dst(x, y, tile.getX(), tile.getY()) > type.range) return false;
        return FactionMeta.map(tile.solid() ? tile.block() : tile.floor()) == Faction.monolith;
    }

    boolean joining(){
        return joinTarget != null && joinTarget.isAdded();
    }

    boolean forming(){
        return forms.any();
    }

    float lifeDelta(){
        return (forms.size - 2.5f) * 0.35f;
    }

    static MonolithSoul create(Team team){
        return (MonolithSoul)MonolithUnitTypes.monolithSoul.create(team);
    }
}
