package unity.annotations;

import arc.struct.*;
import mindustry.io.*;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

import com.squareup.javapoet.*;

/**
 * @author Anuke
 * @author GlennFolker
 */
public class TypeIOResolver{
    public static ClassSerializer resolve(BaseProcessor proc){
        ClassSerializer out = new ClassSerializer(new ObjectMap<>(), new ObjectMap<>(), new ObjectMap<>());

        TypeElement type = BaseProcessor.elementUtils.getTypeElement(TypeIO.class.getCanonicalName());
        Seq<ExecutableElement> methods = proc.methods(type);
        for(ExecutableElement meth : methods){
            if(proc.is(meth, Modifier.PUBLIC) && proc.is(meth, Modifier.STATIC)){
                Seq<VariableElement> params = Seq.with(meth.getParameters()).as();

                if(params.size == 2 && BaseProcessor.tName(params.first()).toString().equals("arc.util.io.Writes")){
                    out.writers.put(fix(BaseProcessor.tName(params.get(1)).toString()), BaseProcessor.fullName(type) + "." + BaseProcessor.simpleName(meth));
                }else if(params.size == 1 && BaseProcessor.tName(params.first()).toString().equals("arc.util.io.Reads") && meth.getReturnType().getKind() != TypeKind.VOID){
                    out.readers.put(fix(TypeName.get(meth.getReturnType()).toString()), BaseProcessor.fullName(type) + "." + BaseProcessor.simpleName(meth));
                }else if(params.size == 2 && BaseProcessor.tName(params.first()).toString().equals("arc.util.io.Reads") && meth.getReturnType().getKind() != TypeKind.VOID && BaseProcessor.typeUtils.isSameType(meth.getReturnType(), meth.getParameters().get(1).asType())){
                    out.mutatorReaders.put(fix(TypeName.get(meth.getReturnType()).toString()), BaseProcessor.fullName(type) + "." + BaseProcessor.simpleName(meth));
                }
            }
        }

        return out;
    }

    private static String fix(String str){
        return str.replace("mindustry.gen", "").replace("unity.gen", "");
    }

    /** Information about read/write methods for class types. */
    public static class ClassSerializer{
        public final ObjectMap<String, String> writers, readers, mutatorReaders;

        public ClassSerializer(ObjectMap<String, String> writers, ObjectMap<String, String> readers, ObjectMap<String, String> mutatorReaders){
            this.writers = writers;
            this.readers = readers;
            this.mutatorReaders = mutatorReaders;
        }
    }
}
