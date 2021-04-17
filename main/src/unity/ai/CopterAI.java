package unity.ai;

import mindustry.ai.types.*;

/** @author GlennFolker */
public class CopterAI extends FlyingAI{
    @Override
    protected void attack(float attackLength){
        moveTo(target, unit.range() * 0.8f);
        unit.lookAt(target);
    }
}
