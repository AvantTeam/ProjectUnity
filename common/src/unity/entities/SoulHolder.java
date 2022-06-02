package unity.entities;

import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.mod.*;

/**
 * An entities attribute of {@linkplain Faction#monolith monolith} soul holders.
 * @author GlennFolker
 */
public interface SoulHolder{
    int souls();
    int maxSouls();

    default boolean canJoin(){
        return souls() < maxSouls();
    }

    default boolean hasSouls(){
        return souls() > 0;
    }

    default int acceptSoul(Entityc other){
        SoulHolder soul = toSoul(other);
        return soul != null ? acceptSoul(soul) : 0;
    }

    default int acceptSoul(SoulHolder other){
        return acceptSoul(other.souls());
    }

    default int acceptSoul(int amount){
        return Math.min(maxSouls() - souls(), amount);
    }

    int transferSoul(int amount);

    int withdrawSoul(int amount);

    default void transferredSoul(){}

    default float soulf(){
        return souls() / (float)maxSouls();
    }

    /** Spreads the nesting souls in this soul holder, typically at death. Called server-side */
    default void spreadSouls(){
        /*boolean transferred = false;

        float start = Mathf.random(360f);
        for(int i = 0; i < souls(); i++){
            MonolithSoul soul = MonolithSoul.create(team());

            Tmp.v1.trns(Mathf.random(360f), Mathf.random(hitSize()));
            soul.set(x() + Tmp.v1.x, y() + Tmp.v1.y);

            Tmp.v1.trns(start + 360f / souls() * i, Mathf.random(6f, 12f));
            soul.rotation = Tmp.v1.angle();
            soul.vel.set(Tmp.v1.x, Tmp.v1.y);
            //soul.healAmount = maxHealth() / 10f / souls();

            transferred = apply(soul, i, transferred);
            soul.add();
        }*/
    }

    //boolean apply(MonolithSoul soul, int index, boolean transferred);

    static boolean isSoul(Object e){
        return toSoul(e) != null;
    }

    static SoulHolder toSoul(Object e){
        if(e instanceof UnitController cont) e = cont.unit();
        if(e instanceof BlockUnitc unit) e = unit.tile();
        if(e instanceof SoulHolder soul) return soul;
        return null;
    }
}
