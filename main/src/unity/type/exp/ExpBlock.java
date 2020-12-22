package unity.type.exp;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.world.*;
import unity.entities.comp.*;
import unity.type.*;

public class ExpBlock extends ExpType<Block>{
    public ExpBlock(Block type){
        super(type);
    }

    @Override
    public void init(){
        super.init();
        setBars();

        type.sync = true;
        if(enableUpgrade){
            type.configurable = true;
            type.saveConfig = false;

            type.config(Integer.class, (build, value) -> {
                int i = value.intValue();
                if(i > 0 && build instanceof ExpBuildc exp){
                    exp.upgrade(i);
                }
            });
        }
    }

    protected void setBars(){
        type.bars.add("level", b -> {
            if(b instanceof ExpBuildc build){
                return new Bar(
                    () -> Core.bundle.get("explib.level") + " " + build.level(),
                    () -> Tmp.c1.set(minLevelColor).lerp(maxLevelColor, Mathf.clamp(build.level() / maxLevel)),
                    () -> Mathf.clamp(build.level() / maxLevel)
                );
            }else{
                throw new IllegalStateException("Building type for '" + type.localizedName + "' is not an instance of 'ExpBuildc'!");
            }
        });

        type.bars.add("exp", b -> {
            if(b instanceof ExpBuildc build){
                return new Bar(
                    () -> build.exp() < maxExp
                    ?    Core.bundle.get("explib.exp")
                    :   Core.bundle.get("explib.max"),
                    () -> Tmp.c1.set(minExpColor).lerp(maxExpColor, build.levelf()),
                    () -> build.levelf()
                );
            }else{
                throw new IllegalStateException("Building type for '" + type.localizedName + "' is not an instance of 'ExpBuildc'!");
            }
        });
    }
}
