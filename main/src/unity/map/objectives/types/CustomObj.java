package unity.map.objectives.types;

import arc.func.*;
import mindustry.gen.*;
import rhino.*;
import unity.graphics.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;
import unity.util.*;

public class CustomObj extends Objective{
    public final Boolf<CustomObj> completer;

    public CustomObj(StoryNode node, String name, Boolf<CustomObj> completer, Cons<CustomObj> executor){
        super(node, name, executor);
        this.completer = completer;
    }

    public static void setup(){
        ObjectiveModel.setup(CustomObj.class, UnityPal.scarColor, () -> Icon.pencil, (node, f) -> {
            Context c = JSBridge.context;
            ImporterTopLevel s = JSBridge.unityScope;

            String exec = f.get("executor", "function(objective){}");
            Function func = JSBridge.compileFunc(s, f.name() + "-executor.js", exec, 1);

            String completerFunc = f.get("completer");
            Func<Object[], Boolean> completer = JSBridge.requireType(JSBridge.compileFunc(s, f.name() + "-completer.js", completerFunc, 1), c, s, boolean.class);

            Object[] args = {null};
            CustomObj obj = new CustomObj(node, f.name(), e -> {
                args[0] = e;
                return completer.get(args);
            }, e -> {
                args[0] = e;
                func.call(c, s, s, args);
            });
            obj.ext(f);

            return obj;
        });
    }

    @Override
    public void update(){
        super.update();
        completed = completer.get(this);
    }
}
