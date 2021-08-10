package unity.type.sector;

import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;
import mindustry.io.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

@SuppressWarnings("unchecked")
public class SectorObjectiveModel implements JsonSerializable{
    public static ObjectMap<Class<? extends SectorObjective>, ObjectiveConstructor<? extends SectorObjective>> constructors = new ObjectMap<>();

    /** The objective type that is going to be instantiated. Do not modify directly, use {@link #set(Class)} instead. */
    public Class<? extends SectorObjective> type;

    /** The field meta data of this objective. Do not modify directly, use {@link #set(Class)} instead. */
    public OrderedMap<String, FieldMetadata> fields = new OrderedMap<>();
    /**
     * The fields that is going to be used in objective instantiation. Modify the contents of this map, with the following
     * rule:
     * <ul>
     *     <li>{@link Seq}'s elements must be {@link String}s.</li>
     *     <li>{@link ObjectMap} must always be {@link StringMap}.</li>
     *     <li>Other types must be {@link String}s.</li>
     * </ul>
     * These will be parsed separately in each {@link SectorObjective} implementations.
     */
    public ObjectMap<String, Object> setFields = new ObjectMap<>();

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

    public <T extends SectorObjective> T create(){
        if(type == null) throw new IllegalArgumentException("type is null");

        var constructor = constructors.get(type);
        if(constructor == null) throw new IllegalArgumentException("No constructor setup for " + type.getSimpleName());

        return (T)constructor.get(setFields);
    }

    public interface ObjectiveConstructor<T extends SectorObjective>{
        T get(ObjectMap<String, Object> fields);
    }
}
