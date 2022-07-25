package unity.parts;


import arc.struct.*;
import unity.util.*;

public class ModularPartStatMap{
    public ValueMap stats = new ValueMap();

    public ValueMap getOrCreate(String name){
        if(!stats.has(name)){
            stats.put(name, new ValueMap());
        }
        return stats.getValueMap(name);
    }

    public boolean has(String name){
        return stats.has(name);
    }

    public float getValue(String name){
        return stats.getValueMap(name).getFloat("value");
    }

    public float getValue(String name, String subfield){
        return stats.getValueMap(name).getFloat(subfield, 0);
    }

    public void getStats(ModularPart[][] parts){
        //need to find the root;
        ModularPart root = null;
        OrderedSet<ModularPart> partsList = new OrderedSet<>();
        for(int i = 0; i < parts.length; i++){
            for(int j = 0; j < parts[0].length; j++){
                if(parts[i][j] != null && !partsList.contains(parts[i][j])){
                    partsList.add(parts[i][j]);
                    if(parts[i][j].type.root){
                        root = parts[i][j];
                    }
                }
            }
        }
        if(root == null){
            return;
        }
        ///temp
        var partSeq = partsList.orderedItems();
        for(int i = 0; i < partSeq.size; i++){
            partSeq.get(i).type.appendStats(this, partSeq.get(i), parts);
        }
        for(int i = 0; i < partSeq.size; i++){
            partSeq.get(i).type.appendStatsPost(this, partSeq.get(i), parts);
        }
    }
}
