package unity.entities.comp;

import arc.math.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** A test component whose purpose is to test out annotation implementations. Do not use! */
@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityDef({Unitc.class, Testc.class})
@EntityComponent
abstract class TestComp implements Unitc{
    /** Import fields from another component */
    @Import float health, maxHealth;

    /** Don't generate setter for this field and make it {@code protected} */
    @ReadOnly float chance = 0.5f;

    /** Calls {@code updatePre()} at the beginning of {@link #update()} block */
    @Insert(value = "update()", after = false)
    private void updatePre(){}

    /** Calls {@code #updatePost()} at the end of {@link #update()} block */
    @Insert("update()")
    private void updatePost(){}

    /** Calls {@code updatePreWeapons()} at the beginning of {@link Weaponsc}'s {@link #update()} block */
    @Insert(value = "update()", block = Weaponsc.class, after = false)
    private void updatePreWeapons(){}

    /** Calls {@code updatePreWeaponsB()} at the beginning of {@link Weaponsc}'s {@link #update()} block, with the priority of -5 */
    @Insert(value = "update()", block = Weaponsc.class, after = false)
    @MethodPriority(-5)
    private void updatePreWeaponsB(){}

    /** Calls {@code updatePostWeapons()} at the end of {@link Weaponsc}'s {@link #update()} block */
    @Insert(value = "update()", block = Weaponsc.class)
    private void updatePostWeapons(){}

    /** Calls {@code updatePostWeaponsB()} at the end of {@link Weaponsc}'s {@link #update()} block, with the priority of -5 */
    @Insert(value = "update()", block = Weaponsc.class)
    @MethodPriority(-5)
    private void updatePostWeaponsB(){}

    /** Bypasses {@code if(added)} in {@link #add()} */
    @Override
    @BypassGroupCheck
    public void add(){
        destroy();
    }

    /** Bypasses {@code if(!added)} in {@link #remove()} */
    @Override
    @BypassGroupCheck
    public void remove(){
        destroy();
    }

    /** Breaks the entire method block if condition is met, causing other component-specific implementations not to run */
    @Override
    @MethodPriority(-5)
    @BreakAll
    public void update(){
        if(health < maxHealth / 2f){
            destroy();
            return;
        }
    }

    /** Replaces default serialization method */
    @Override
    public boolean serialize(){
        return Mathf.chance(chance);
    }

    /** Wraps {@link #update()} with {@code if(this.shouldUpdate())} */
    @Wrap(value = "update()")
    public boolean shouldUpdate(){
        return false;
    }

    /** Wraps {@link Weaponsc}'s {@link #update()} implementation with {@code if(this.shouldUpdateWeapons())} */
    @Wrap(value = "update()", block = Weaponsc.class)
    public boolean shouldUpdateWeapons(){
        return false;
    }

    /**
     * Wraps {@link Unitc}'s {@link #update()} implementation with <code>if(this.shouldUpdateUnit() && this.{@link #shouldUpdateUnitB()})</code>
     * @see #shouldUpdateUnitB()
     */
    @Wrap(value = "update()", block = Unitc.class)
    public boolean shouldUpdateUnit(){
        return false;
    }

    /**
     * Wraps {@link Unitc}'s {@link #update()} implementation with <code>if(this.{@link #shouldUpdateUnit()} && this.shouldUpdateUnitB)</code>
     * @see #shouldUpdateUnit()
     */
    @Wrap(value = "update()", block = Unitc.class)
    public boolean shouldUpdateUnitB(){
        return false;
    }
}
