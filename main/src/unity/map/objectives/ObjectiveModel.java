package unity.map.objectives;

import arc.func.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.Log.*;
import arc.util.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.graphics.*;
import unity.map.cinematic.*;
import unity.map.objectives.types.*;
import unity.util.*;

import java.util.regex.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

@SuppressWarnings("unchecked")
public class ObjectiveModel implements JsonSerializable{
    public static ObjectMap<Class<? extends Objective>, ObjectiveData> datas = new ObjectMap<>();

    /** The objective type that is going to be instantiated. Do not modify directly, use {@link #set(Class)} instead. */
    public Class<? extends Objective> type;
    /** The name of this objective model. */
    public String name;
    /** The JS function to be called in instantiation. */
    public String init;

    /** Objective model fields. Elements of this map must be serializable. */
    public ObjectMap<String, Object> fields = new ObjectMap<>();

    private static final String env = "\\$\\{{2}.*\\}{2}";
    private static final Pattern replacer = Pattern.compile(env);
    private static final FieldTranslator translator = new FieldTranslator();

    static{
        ResourceAmountObj.setup();
        UnitPosObj.setup();
    }

    public void set(Class<? extends Objective> type){
        this.type = type;
        fields.clear();
    }

    @Override
    public void write(Json json){
        json.writeValue("type", type == null ? "null" : type.getName());
        json.writeValue("name", name);
        json.writeValue("init", init);
        json.writeValue("fields", fields, ObjectMap.class);
    }

    @Override
    public void read(Json json, JsonValue data){
        try{
            var typeName = data.getString("type");
            if(!typeName.equals("null")){
                set((Class<? extends Objective>)Class.forName(typeName, true, mods.mainLoader()));
            }

            name = data.getString("name");
            init = data.getString("init");

            fields.clear();
            fields.putAll(json.readValue(ObjectMap.class, data.get("fields")));
        }catch(Exception e){
            print(LogLevel.err, "", Strings.getStackTrace(Strings.getFinalCause(e)));
        }
    }

    public <T extends Objective> T create(StoryNode node){
        var data = data(type);

        fields.clear();
        if(init != null){
            var source = init;
            var matcher = replacer.matcher(init);
            while(matcher.find()){
                var occur = init.substring(matcher.start(), matcher.end());
                occur = occur.substring(3, occur.length() - 2).trim();

                var f = occur; // Finalize, for use in lambda statements.
                var script = node.scripts.getThrow(f, () -> new IllegalArgumentException("No such script: '" + f + "'"));

                // Remove new-lines, note that every script must have `;` as the statement separator!
                source = source.replaceFirst(env, script.replace("\r", "\n").replace("\n", ""));
            }

            var func = JSBridge.compileFunc(JSBridge.unityScope, name + "-init.js", source);
            func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, new Object[]{fields});
        }

        translator.fields.clear();
        translator.fields.putAll(fields);
        translator.name = name;

        var obj = (T)data.constructor.get(node, translator);
        translator.fields.clear();

        return obj;
    }

    public static void setup(Class<? extends Objective> type, Color color, Prov<Drawable> icon, ObjConstructor constructor){
        datas.put(type, new ObjectiveData(color, icon, constructor));
    }

    public static ObjectiveData data(Class<? extends Objective> type){
        if(type == null) throw new IllegalArgumentException("type can't be null");
        return datas.getThrow(type, () -> new IllegalArgumentException("No data registered for " + type.getSimpleName()));
    }

    public interface ObjConstructor{
        Objective get(StoryNode node, FieldTranslator fields);
    }

    public static class FieldTranslator{
        private String name;
        private final ObjectMap<String, Object> fields = new ObjectMap<>();

        public String name(){
            return name;
        }

        public <T> T get(String name){
            return (T)fields.getThrow(name, () -> new IllegalArgumentException("'" + name + "' not found"));
        }

        public <T> T get(String name, T def){
            return (T)fields.get(name, def);
        }

        public boolean has(String name){
            return fields.containsKey(name);
        }
    }

    public static class ObjectiveData{
        public final Color color = Pal.accent.cpy();
        public final ObjConstructor constructor;
        public final Prov<Drawable> icon;

        public ObjectiveData(Color color, Prov<Drawable> icon, ObjConstructor constructor){
            this.color.set(color);
            this.constructor = constructor;
            this.icon = icon;
        }
    }
}
