package unity.type.sector;

import arc.struct.*;
import arc.util.Log.*;
import arc.util.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.io.*;
import unity.cinematic.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

@SuppressWarnings("unchecked")
public class SectorObjectiveModel implements JsonSerializable{
    public static ObjectMap<Class<? extends SectorObjective>, ObjectiveConstructor> constructors = new ObjectMap<>();

    /** The objective type that is going to be instantiated. Do not modify directly, use {@link #set(Class)} instead. */
    public Class<? extends SectorObjective> type;

    /** The field meta data of this objective. Do not modify directly, use {@link #set(Class)} instead. */
    public OrderedMap<String, FieldMetadata> fields = new OrderedMap<>();
    /**
     * The fields that is going to be used in objective instantiation. Modify the contents of this map, with the following
     * rule:
     * <ul>
     *     <li>{@link Seq}'s or array type's elements must be {@link String}s.</li>
     *     <li>{@link ObjectMap}s must always be {@link StringMap}s.</li>
     *     <li>Other types must be {@link String}s.</li>
     * </ul>
     * These will be parsed separately in each {@link SectorObjective} implementations.
     */
    public ObjectMap<String, Object> setFields = new ObjectMap<>();

    private final FieldTranslator translator = new FieldTranslator();

    public void set(Class<? extends SectorObjective> type){
        this.type = type;

        fields.clear();
        if(type != null) fields.putAll(JsonIO.json.getFields(type));

        setFields.clear();
    }

    @Override
    public void write(Json json){
        json.writeValue("type", type == null ? "null" : type.getName());
        json.writeValue("setFields", setFields, ObjectMap.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        try{
            var typeName = jsonData.getString("type");
            if(!typeName.equals("null")){
                set((Class<? extends SectorObjective>)Class.forName(typeName, true, mods.mainLoader()));
            }

            setFields.clear();
            setFields.putAll(json.readValue(ObjectMap.class, jsonData.get("setFields")));
        }catch(Exception e){
            print(LogLevel.err, "", Strings.getStackTrace(Strings.getFinalCause(e)));
        }
    }

    public <T extends SectorObjective> T create(StoryNode node){
        if(type == null) throw new IllegalArgumentException("type is null");

        var constructor = constructors.get(type);
        if(constructor == null) throw new IllegalArgumentException("No constructor setup for " + type.getSimpleName());

        translator.fields = setFields;
        var obj = (T)constructor.get(node, translator);
        translator.fields = null;

        return obj;
    }

    public interface ObjectiveConstructor{
        SectorObjective get(StoryNode node, FieldTranslator fields);
    }

    public static class FieldTranslator{
        private ObjectMap<String, Object> fields;

        public String val(String key){
            return val(key, "");
        }

        public String val(String key, String def){
            return (String)fields.get(key, def);
        }

        public String valReq(String key){
            var val = val(key, null);
            if(val == null) throw new IllegalArgumentException("'" + key + "' not found");

            return val;
        }

        public Seq<String> arr(String key){
            return (Seq<String>)fields.get(key);
        }

        public Seq<String> arrReq(String key){
            var arr = (Seq<String>)fields.get(key);
            if(arr == null) throw new IllegalArgumentException("'" + key + "' not found");

            return arr;
        }

        public StringMap map(String key){
            return (StringMap)fields.get(key);
        }

        public StringMap mapReq(String key){
            var map = (StringMap)fields.get(key);
            if(map == null) throw new IllegalArgumentException("'" + key + "' not found");

            return map;
        }

        public boolean has(String key){
            return fields.containsKey(key);
        }
    }
}
