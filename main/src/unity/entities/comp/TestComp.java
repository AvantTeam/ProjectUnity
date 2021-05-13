package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, Testc.class})
@EntityComponent
abstract class TestComp implements Unitc{
    @Insert(value = "update()", after = false)
    private void updatePre(){}

    @Insert("update()")
    private void updatePost(){}

    @Insert(value = "update()", block = Weaponsc.class, after = false)
    private void updatePreWeapons(){}

    @Insert(value = "update()", block = Weaponsc.class, after = false)
    @MethodPriority(-5)
    private void updatePreWeaponsB(){}

    @Insert(value = "update()", block = Weaponsc.class)
    private void updatePostWeapons(){}

    @Insert(value = "update()", block = Weaponsc.class)
    @MethodPriority(-5)
    private void updatePostWeaponsB(){}

    @Override
    @BypassGroupCheck
    public void add(){
        destroy();
    }

    @Override
    @BypassGroupCheck
    public void remove(){
        destroy();
    }
}
