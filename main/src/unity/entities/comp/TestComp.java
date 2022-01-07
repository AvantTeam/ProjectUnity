package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.annotations.Annotations.Resolve.*;
import unity.gen.*;

/**
 * A test component whose sole purpose is to test out annotation implementations. Do not use directly!
 * <p>This component will generate {@link Testc} interface of which declared methods are all methods in this comp
 * that aren't {@code protected}/{@code private} and has no {@link Override}, <i>and</i> getter/setters of which
 * requirements are:
 * <ol>
 * <li> Getter:
 *   <ol>
 *     <li> Has no {@link Import}.
 *     <li> Must have no modifiers <i>(implicitly {@code public})</i>.
 *     <li> Is not {@code private}.
 *   </ol>
 * <li> Setter:
 *   <ol>
 *     <li> Has no {@link Import}.
 *     <li> Must have no modifiers <i>(implicitly {@code public})</i>.
 *     <li> Is not {@code private}.
 *     <li> Has no {@link ReadOnly}. If this is the case, the generated field in future entity class(es) will be
 *     {@code protected}.
 *   </ol>
 * </ol>
 */
@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityDef({Unitc.class, Testc.class, Test2c.class, Test3c.class})
@EntityComponent
abstract class TestComp implements Unitc, Test4c{
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

    /**
     * Breaks the entire method block if condition is met, causing other component-specific implementations not to run.
     * Also removes {@link Unitc#update()} from the specification.
     */
    @Override
    @MethodPriority(-5)
    @BreakAll
    @Remove(Unitc.class)
    public void update(){
        if(health < maxHealth / 2f){
            destroy();
            return;
        }
    }

    @Override
    @Combine
    public float speed(){
        @Resolve(Method.add)
        float result = 0f;

        /*
        Resulting codes:

        float result = 0f;
        speed_result_RESOLVER_: {
            float test4_speed_;
            test4: {
                // ...
                test4_speed_ = // value, replacing `return` with `test4_speed_ = `.
            }

            // other components...

            result = test4_speed_; // + othercomponent_speed_...
        }
        */

        return result;
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

    /** Since {@link Unitc#clipSize()} is also annotated with {@link Replace}, use higher priority to bypass it */
    @Replace(5)
    @Override
    public float clipSize(){
        return Float.POSITIVE_INFINITY;
    }

    /** Annotate it with {@link Replace} and use priority 5 */
    @Replace(5)
    @Override
    public void yourThing(){
        Log.info("All of those, give them to me.");
    }
}
