package unity.ai;

import mindustry.ai.types.*;

/** @author GlennFolker */
@Deprecated
public class CopterAI extends FlyingAI{
    @Override
    public void circleAttack(float attackLength){
        moveTo(target, unit.range() * 0.8f);
        unit.lookAt(target);
    }
}
