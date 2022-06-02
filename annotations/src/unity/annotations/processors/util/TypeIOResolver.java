package unity.annotations.processors.util;

import arc.struct.*;
import arc.util.io.*;
import com.sun.tools.javac.code.Symbol.*;
import com.sun.tools.javac.util.*;
import mindustry.io.*;
import unity.annotations.processors.*;

import javax.lang.model.element.*;

import static unity.annotations.processors.BaseProcessor.*;
import static javax.lang.model.element.Modifier.*;
import static javax.lang.model.type.TypeKind.*;

/**
 * @author Anuke
 * @author GlennFolker
 */
public class TypeIOResolver{
    public static ClassSerializer resolve(BaseProcessor proc){
        ClassSerializer out = new ClassSerializer(new ObjectMap<>(), new ObjectMap<>(), new ObjectMap<>(), new ObjectMap<>());

        ClassSymbol type = proc.conv(TypeIO.class); //TODO implement @TypeIOBase, should be easy enough.
        for(Element e : type.getEnclosedElements()){
            if(!(e instanceof MethodSymbol)) continue;
            MethodSymbol m = (MethodSymbol)e;

            if(is(m, PUBLIC, STATIC)){
                List<VarSymbol> params = m.params;
                int size = params.size();

                if(size == 2 && proc.same(params.get(0).type, proc.conv(Writes.class))){
                    (name(m).endsWith("Net") ? out.netWriters : out.writers).put(fixName(params.get(1).type.toString()), fName(type) + "." + name(m));
                }else if(size == 1 && proc.same(params.get(0).type, proc.conv(Reads.class)) && m.getReturnType().getKind() != VOID){
                    out.readers.put(fixName(m.getReturnType().toString()), fName(type) + "." + name(m));
                }else if(size == 2 && proc.same(params.get(0).type, proc.conv(Reads.class)) && m.getReturnType().getKind() != VOID && proc.same(m.getReturnType(), params.get(1).type)){
                    out.mutatorReaders.put(fixName(m.getReturnType().toString()), fName(type) + "." + name(m));
                }
            }
        }

        return out;
    }

    public static class ClassSerializer{
        public final ObjectMap<String, String> writers, readers, mutatorReaders, netWriters;

        public ClassSerializer(ObjectMap<String, String> writers, ObjectMap<String, String> readers, ObjectMap<String, String> mutatorReaders, ObjectMap<String, String> netWriters){
            this.writers = writers;
            this.readers = readers;
            this.mutatorReaders = mutatorReaders;
            this.netWriters = netWriters;
        }

        public String getNetWriter(String type, String fallback){
            return netWriters.get(type, writers.get(type, fallback));
        }
    }
}
