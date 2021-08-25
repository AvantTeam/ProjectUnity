package unity.annotations.processors.impl;

import arc.struct.*;
import arc.struct.ObjectMap.*;
import com.squareup.javapoet.*;
import unity.annotations.Annotations.*;
import unity.annotations.processors.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.*;

/**
 * @author Anuke
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.Struct",
    "unity.annotations.Annotations.StructWrap"
})
public class StructProcessor extends BaseProcessor{
    ObjectMap<Element, OrderedMap<VariableElement, SInfo>> structs = new ObjectMap<>();

    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        if(round == 1){
            Seq<TypeElement> defs = Seq.with((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(Struct.class));
            Seq<VariableElement> wrappers = Seq.with((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(StructWrap.class));

            for(TypeElement e : defs){

            }

            for(VariableElement e : wrappers){
                StructWrap anno = annotation(e, StructWrap.class);

                StructField[] vals = anno.value();
                TypeElement type = toEl(e.asType());

                OrderedMap<VariableElement, SInfo> fields = new OrderedMap<>();
                for(StructField val : vals){
                    String fname = val.name();

                    VariableElement field = vars(type).find(f -> simpleName(f).equals(fname));
                    if(field == null) throw new IllegalArgumentException(type + "#" + fname + " does not exist!");

                    TypeKind kind = field.asType().getKind();
                    int defSize = sizeOf(kind);
                    int size = val.value() <= 0 ? defSize : val.value();

                    if(defSize < size) throw new IllegalArgumentException(kind + ": Size can't be greater than " + defSize + ": " + size);
                    if(kind == TypeKind.FLOAT && size != 32) throw new IllegalArgumentException(kind + ": Size must be 32");
                    if(kind == TypeKind.BOOLEAN && size != 1) throw new IllegalArgumentException(kind + ": Size must be 1");

                    Class<?> stype = null;
                    switch(kind){
                        case BOOLEAN: stype = boolean.class; break;
                        case BYTE: stype = byte.class; break;
                        case CHAR: stype = char.class; break;
                        case SHORT: stype = short.class; break;
                        case INT: stype = int.class; break;
                        case FLOAT: stype = float.class;
                    }

                    fields.put(field, new SInfo(stype, size));
                }

                structs.put(e, fields);
            }
        }else if(round == 2){
            structs.each((e, infos) -> {
                String cname = "";
                if(e instanceof VariableElement){
                    cname = "S" + simpleName(toEl(e.asType()));
                }else if(e instanceof TypeElement){
                    cname = simpleName(e);
                    cname = cname.substring(0, cname.length() - "Struct".length());
                }

                TypeSpec.Builder builder = TypeSpec.classBuilder(cname).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

                Class<?> structType;
                int structSize = infos.values().toSeq().sum(i -> i.size);
                if(structSize <= 8){
                    structType = byte.class;
                }else if(structSize <= 16){
                    structType = short.class;
                }else if(structSize <= 32){
                    structType = int.class;
                }else if(structSize <= 64){
                    structType = long.class;
                }else{
                    throw new IllegalStateException("Struct size cannot go over 64");
                }

                int structTotalSize = structSize <= 8 ? 8 : structSize <= 16 ? 16 : structSize <= 32 ? 32 : 64;
                String structParam = cname.toLowerCase(Locale.ROOT);

                StringBuilder cons = new StringBuilder();
                MethodSpec.Builder constructor = MethodSpec.methodBuilder("construct")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(structType);

                int offset = 0;
                for(Entry<VariableElement, SInfo> entry : infos.entries()){
                    VariableElement f = entry.key;
                    SInfo info = entry.value;

                    TypeName ftype = tName(f);
                    String fname = simpleName(f);

                    constructor.addParameter(ftype, fname);

                    MethodSpec.Builder getter = MethodSpec.methodBuilder(fname)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ftype)
                        .addParameter(structType, structParam);

                    MethodSpec.Builder setter = MethodSpec.methodBuilder(fname)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(structType)
                        .addParameter(structType, structParam).addParameter(ftype, "value");

                    if(ftype == TypeName.BOOLEAN){
                        getter.addStatement("return ($L & (1L << $L)) != 0", structParam, offset);
                    }else if(ftype == TypeName.FLOAT){
                        getter.addStatement("return $T.intBitsToFloat((int)(($L >>> $L) & $L))", cName(Float.class), structParam, offset, bitString(info.size, structTotalSize));
                    }else{
                        getter.addStatement("return ($T)(($L >>> $L) & $L)", ftype, structParam, offset, bitString(info.size, structTotalSize));
                    }

                    if(ftype == TypeName.BOOLEAN){
                        cons.append(" | (").append(fname).append(" ? ").append("1L << ").append(offset).append("L : 0)");

                        setter.beginControlFlow("if(value)");
                        setter.addStatement("return ($T)(($L & ~(1L << $LL)))", structType, structParam, offset);
                        setter.nextControlFlow("else");
                        setter.addStatement("return ($T)(($L & ~(1L << $LL)) | (1L << $LL))", structType, structParam, offset, offset);
                        setter.endControlFlow();
                    }else if(ftype == TypeName.FLOAT){
                        cons.append(" | (").append("(").append(structType).append(")").append("Float.floatToIntBits(").append(fname).append(") << ").append(offset).append("L)");

                        setter.addStatement("return ($T)(($L & $L) | (($T)Float.floatToIntBits(value) << $LL))", structType, structParam, bitString(offset, info.size, structTotalSize), structType, offset);
                    }else{
                        cons.append(" | (((").append(structType).append(")").append(fname).append(" << ").append(offset).append("L)").append(" & ").append(bitString(offset, info.size, structTotalSize)).append(")");

                        setter.addStatement("return ($T)(($L & $L) | (($T)value << $LL))", structType, structParam, bitString(offset, info.size, structTotalSize), structType, offset);
                    }

                    builder.addMethod(getter.build());
                    builder.addMethod(setter.build());

                    offset += info.size;
                }

                constructor.addStatement("return ($T)($L)", structType, cons.substring(3));
                builder.addMethod(constructor.build());
            });
        }
    }

    static String bitString(int offset, int size, int totalSize){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < offset; i++) builder.append('0');
        for(int i = 0; i < size; i++) builder.append('1');
        for(int i = 0; i < totalSize - size - offset; i++) builder.append('0');
        return "0b" + builder.reverse() + "L";
    }

    static String bitString(int size, int totalSize){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++) builder.append('1');
        for(int i = 0; i < totalSize - size; i++) builder.append('0');
        return "0b" + builder.reverse() + "L";
    }

    static int sizeOf(TypeKind kind){
        switch(kind){
            case BOOLEAN: return 1;
            case BYTE:
            case CHAR: return 8;
            case SHORT: return 16;
            case INT:
            case FLOAT: return 32;
            default: throw new IllegalArgumentException("Illegal kind: " + kind + ". Must be primitives and takes less than 64 bits");
        }
    }

    static class SInfo{
        final Class<?> type;
        final int size;

        SInfo(Class<?> type, int size){
            this.type = type;
            this.size = size;
        }
    }
}
