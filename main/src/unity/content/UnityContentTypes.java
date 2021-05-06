package unity.content;

import arc.struct.*;
import mindustry.core.*;
import mindustry.ctype.*;
import unity.*;
import unity.util.*;

import java.lang.reflect.*;
import java.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class UnityContentTypes implements ContentList{
    public static ContentType
    test;

    @Override
    public void load(){
        Unity.print("ContentTypes: " + Arrays.toString(ContentType.all));

        var constructor = ReflectUtils.findConstructor(ContentType.class, true, String.class, int.class);

        Seq<ContentType> toAdd = new Seq<>(ContentType.class);
        toAdd.addAll(ContentType.all);

        toAdd.add(
        test = ReflectUtils.addEnumEntry(
            ContentType.class, constructor,
            "unity-test", ReflectUtils.emptyObjects
        ));

        // ContentType.all
        var all = ReflectUtils.findField(ContentType.class, "all", true);

        ReflectUtils.revokeModifier(all, Modifier.FINAL);
        ReflectUtils.setField(null, all, toAdd.toArray());
        ReflectUtils.invokeModifier(all, Modifier.FINAL);

        Unity.print("ContentTypes: " + Arrays.toString(ContentType.all));

        // Vars.content.contentNameMap
        var contentNameMapf = ReflectUtils.findField(ContentLoader.class, "contentNameMap", true);
        ObjectMap<String, MappableContent>[] contentNameMap = ReflectUtils.getField(content, contentNameMapf);

        Seq<ObjectMap<String, MappableContent>> contentNameMapAll = Seq.of(true, ContentType.all.length, (Class<ObjectMap<String, MappableContent>>)contentNameMap.getClass().getComponentType());
        contentNameMapAll.addAll(contentNameMap);
        for(ContentType type : ContentType.all){
            if(contentNameMapAll.size <= type.ordinal()){
                contentNameMapAll.add(new ObjectMap<>());
            }
        }

        ReflectUtils.setField(content, contentNameMapf, contentNameMapAll.toArray());

        // Vars.content.contentMap
        var contentMapf = ReflectUtils.findField(ContentLoader.class, "contentMap", true);
        Seq<Content>[] contentMap = ReflectUtils.getField(content, contentMapf);

        Seq<Seq<Content>> contentMapAll = Seq.of(true, ContentType.all.length, (Class<Seq<Content>>)contentMap.getClass().getComponentType());
        contentMapAll.addAll(contentMap);
        for(ContentType type : ContentType.all){
            if(contentMapAll.size <= type.ordinal()){
                contentMapAll.add(new Seq<>());
            }
        }

        ReflectUtils.setField(content, contentMapf, contentMapAll.toArray());
    }
}
