package unity.gensrc.entities;

import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.gen.entities.*;
import unity.mod.*;

@EntityComponent
@EntityDef({MonolithSoulc.class, Unitc.class, Factionc.class})
abstract class MonolithSoulComp implements Unitc, Factionc{
    @Import float x, y, health, maxHealth;
    @Import UnitType type;

    @ReadOnly transient boolean corporeal;
    @ReadOnly transient float joinTime;
    @ReadOnly transient Teamc joinTarget;
    @ReadOnly transient float ringRotation;
    @ReadOnly transient Seq<Tile> forms = new Seq<>(5);
    @ReadOnly transient float formProgress;

    @Override
    public Faction faction(){
        return FactionRegistry.faction(type);
    }

    @Override
    public void add(){
        health = Math.min(maxHealth / 2f, health);
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
        SoulHolder soul = SoulHolder.toSoul(other);
        return
            soul != null && other.isAdded() && soul.acceptSoul(1) >= 1 &&
            within(other, type.range + (other instanceof Unit unit
                ? (unit.hitSize / 2f) : other instanceof Building build
                ? (build.hitSize() / 2f) : 0f
            ));
    }

    boolean formValid(Tile tile){
        if(tile.synthetic() || Mathf.dst(x, y, tile.getX(), tile.getY()) > type.range) return false;
        return FactionRegistry.faction(tile.solid() ? tile.block() : tile.floor()) == Faction.monolith;
    }

    boolean joining(){
        return joinTarget != null && joinTarget.isAdded();
    }

    boolean forming(){
        return forms.any();
    }

    float lifeDelta(){
        return (forms.size - 2.5f) * 0.18f;
    }
}
