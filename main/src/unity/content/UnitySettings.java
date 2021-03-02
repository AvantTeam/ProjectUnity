package unity.content;

import arc.*;
import mindustry.*;

/**
  * Project Unity mod settings
  * @author sk7725
*/
public class UnitySettings{
    public void addGraphicSetting(String key){
        Vars.ui.settings.graphics.checkPref(key, Core.settings.getBool(key));
    }

    public void init(){
        boolean tmp = Core.settings.getBool("uiscalechanged", false);
        Core.settings.put("uiscalechanged", false);

        addGraphicSetting("hitexpeffect");

        Core.settings.put("uiscalechanged", tmp);
    }
}
