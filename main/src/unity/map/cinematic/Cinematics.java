package unity.map.cinematic;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.serialization.Json.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.*;
import unity.map.*;
import unity.mod.*;

import static mindustry.Vars.*;

/**
 * Main cinematic module bound to a {@link ScriptedSector}. Handles all contained cinematic nodes and tagged objects.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class Cinematics{
    public final Boolp validator;
    /** The root (no parents) nodes of the cinematic core. */
    public final Seq<StoryNode> nodes = new Seq<>();

    private boolean bound = false;
    public final ObjectMap<String, Object> tagToObject = new ObjectMap<>();
    public final ObjectMap<Object, ObjectSet<String>> objectToTag = new ObjectMap<>();
    private final Cons<Trigger> updater = Triggers.cons(this::update);
    private final Cons<Trigger> drawer = Triggers.cons(this::draw);

    public Cinematics(Boolp validator){
        this.validator = validator;
    }

    public void bind(){
        bound = true;
        Triggers.listen(Trigger.update, updater);
        Triggers.listen(Trigger.drawOver, drawer);

        nodes.each(StoryNode::init);
    }

    public void detach(){
        bound = false;
        Triggers.detach(Trigger.update, updater);
        Triggers.detach(Trigger.drawOver, drawer);

        nodes.each(StoryNode::reset);
    }

    public void update(){
        if(bound && !valid()){
            detach();
            return;
        }

        nodes.each(StoryNode::update);
    }

    public boolean valid(){
        return validator.get();
    }

    public void save(StringMap map){
        map.put("nodes", JsonIO.json.toJson(saveNodes(), StringMap.class, String.class));
        map.put("object-tags", JsonIO.json.toJson(saveTags(), Seq.class, String.class));
    }

    public StringMap saveNodes(){
        StringMap map = new StringMap();
        for(var node : nodes){
            StringMap child = new StringMap();
            node.save(child);

            map.put(node.name, JsonIO.json.toJson(child, StringMap.class, String.class));
        }

        return map;
    }

    public Seq<String> saveTags(){
        Seq<String> tagArray = new Seq<>();
        StringMap valueMap = StringMap.of("type", 0, "value", null, "tags", "[]");

        for(var e : objectToTag.entries()){
            if(e.value.isEmpty()) continue;

            Object obj = e.key;
            Class<?> type = obj.getClass();
            if(type.isAnonymousClass()) type = type.getSuperclass();

            if(obj instanceof JsonSerializable || JsonIO.json.getSerializer(type) != null){
                valueMap.put("type", "0");
                valueMap.put("value", JsonIO.json.toJson(obj));
            }else if(obj instanceof Building build){
                valueMap.put("type", "1");
                valueMap.put("value", String.valueOf(build.pos()));
            }else if(obj instanceof Tile tile){
                valueMap.put("type", "2");
                valueMap.put("value", String.valueOf(tile.pos()));
            }else if(obj instanceof Posc pos){
                valueMap.put("type", "3");
                valueMap.put("value", JsonIO.json.toJson(new float[]{pos.getX(), pos.getY()}, float[].class, float.class));
            }else{
                throw new IllegalArgumentException("Un-serializable tagged object: " + obj);
            }

            valueMap.put("tags", JsonIO.json.toJson(e.value, ObjectSet.class, String.class));
            tagArray.add(JsonIO.json.toJson(valueMap, StringMap.class, String.class));
        }

        return tagArray;
    }

    public void load(StringMap map){
        loadNodes(JsonIO.json.fromJson(StringMap.class, String.class, map.get("nodes", "{}")));
        loadTags(JsonIO.json.fromJson(Seq.class, String.class, map.get("object-tags", "[]")));
    }

    public void loadNodes(StringMap map){
        for(var e : map.entries()){
            StoryNode node = nodes.find(n -> n.name.equals(e.key));
            if(node == null) throw new IllegalStateException("Node '" + e.key + "' not found!");

            node.load(JsonIO.json.fromJson(StringMap.class, String.class, e.value));
        }
    }

    public void loadTags(Seq<String> array){
        objectToTag.clear();
        tagToObject.clear();
        for(var e : array){
            StringMap valueMap = JsonIO.json.fromJson(StringMap.class, String.class, e);
            ObjectSet<String> tagsArray = JsonIO.json.fromJson(ObjectSet.class, String.class, valueMap.get("tags", "[]"));

            int type = valueMap.getInt("type");
            Object obj = switch(type){
                case 0 -> JsonIO.json.fromJson(Object.class, valueMap.get("value"));
                case 1 -> world.build(valueMap.getInt("value"));
                case 2 -> world.tile(valueMap.getInt("value"));
                case 3 -> {
                    float[] pos = JsonIO.json.fromJson(float[].class, valueMap.get("value"));
                    yield Groups.all.find(ent -> ent instanceof Posc p && Mathf.equal(p.getX(), pos[0]) && Mathf.equal(p.getY(), pos[1]));
                }
                default -> throw new IllegalArgumentException("Unknown tagged object type: " + type);
            };

            for(var tag : tagsArray){
                tag(obj, tag);
            }
        }
    }

    public void tag(Object object, String tag){
        if(object == null) throw new IllegalArgumentException("Object to be tagged cannot be null!");
        if(byTag(tag) != null && byTag(tag) != object) throw new IllegalArgumentException("'" + tag + "' tag is already taken!");

        objectToTag.get(object, ObjectSet::new).add(tag);
        tagToObject.put(tag, object);
    }

    public void untag(Object object, String tag){
        if(tagToObject.get(tag) == object) tagToObject.remove(tag);

        var set = objectToTag.get(object);
        if(set != null) set.remove(tag);
    }

    public ObjectSet<String> toTag(Object object){
        return objectToTag.get(object);
    }

    public Object byTag(String tag){
        return tagToObject.get(tag);
    }

    public void draw(){
        nodes.each(StoryNode::draw);
    }

    public boolean bound(){
        return bound;
    }

    public void setNodes(Seq<StoryNode> nodes){
        this.nodes.set(nodes.select(node -> node.parent == null));
        this.nodes.each(StoryNode::createObjectives);
    }

    public void setTags(ObjectMap<Object, ObjectSet<String>> tags){
        objectToTag.clear();
        tagToObject.clear();
        for(var e : tags.entries()){
            if(e.value.isEmpty()) continue;
            for(var str : e.value){
                tag(e.key, str);
            }
        }
    }

    public void clearTags(){
        objectToTag.clear();
        tagToObject.clear();
    }
}
