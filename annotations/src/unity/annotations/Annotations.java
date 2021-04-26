package unity.annotations;

import arc.audio.*;
import arc.func.*;
import mindustry.gen.*;
import mindustry.world.*;

import com.sun.tools.javac.code.*;
import com.sun.tools.javac.code.Attribute.Array;
import com.sun.tools.javac.code.Attribute.Enum;
import com.sun.tools.javac.code.Attribute.Error;
import com.sun.tools.javac.code.Attribute.Visitor;
import com.sun.tools.javac.code.Attribute.*;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Symbol.*;
import com.sun.tools.javac.code.Type.ArrayType;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.*;
import sun.reflect.annotation.*;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.lang.Class;
import java.lang.annotation.*;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.*;

public class Annotations{
    //region definitions

    /** Indicates that this content belongs to a specific faction */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionDef{
        /** @return The faction */
        String value();
    }

    /** Indicates that this content implements an exp mechanism */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpDef{
        /** @return The exp type */
        Class<?> value();
    }

    /** Indicates that this content's entity type inherits interfaces */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityDef{
        /** @return The base class for the generated entity class */
        Class<?> base();

        /** @return The interfaces that will be inherited by the generated entity class */
        Class<?>[] value();

        /** @return Whether the class can serialize itself */
        boolean serialize() default true;

        /** @return Whether the class can write/read to/from save files */
        boolean genio() default true;

        /** @return Whether the class is poolable */
        boolean pooled() default false;
    }

    /** Indicates that this content's entity will be the one that is pointed */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityPoint{
        /** @return The entity type */
        Class<?> value();
    }

    /** Indicates that this music belongs to a specific faction in a specific category */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MusicDef{
        /** @return The faction */
        String facType();

        /**
         * The category of this {@link Music}.<br>
         * <br>
         * Reserved keywords are {@code "ambient"}, {@code "dark"}, and {@code "boss"}
         * @return The music category.
         */
        String category() default "ambient";
    }

    /** Whether this class is the base class for faction enum. Only one type may use this */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionBase{}

    /** Whether this class is the base class for exp types. Only one type may use this */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpBase{}

    /** Works somewhat like {@code Object.assign(...)} for Block and Building */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Merge{
        /** @return The base class */
        Class<?> base() default Block.class;

        /** @return The merged classes */
        Class<?>[] value();
    }

    /** Notifies that this class is a component class; an interface will be generated out of this */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MergeComp{}

    /** The generated interface from {@link Annotations.MergeComp} */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MergeInterface{
        Class<?> buildType() default Building.class;
    }

    /** Indicates that this class is an entity component */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityComponent{
        /** @return Whether the component should be interpreted into interfaces */
        boolean write() default true;

        /** @return Whether the component should generate a base class for itself */
        boolean base() default false;
    }

    /** All entity components will inherit from this */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityBaseComponent{}

    /** Whether this interface wraps an entity component */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityInterface{}

    //end region
    //region utilities

    /** Indicates that a field will be interpolated when synced. */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SyncField{
        /** If true, the field will be linearly interpolated. If false, it will be interpolated as an angle. */
        boolean value();

        /** If true, the field is clamped to 0-1. */
        boolean clamped() default false;
    }

    /** Indicates that a field will not be read from the server when syncing the local player state. */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SyncLocal{}

    /** Indicates that the field annotated with this came from another component class */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Import{}

    /** Getter method, do not use directly */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Getter{}

    /** Setter method, do not use directly */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Setter{}

    /** Whether the field returned by this getter is meant to be read-only */
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReadOnly{}

    /** Whether this getter must be implemented by the type's subtypes */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MustInherit{}

    /** Whether this method replaces the actual method in the base class */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Replace{}

    /** Whether this method is implemented in annotation-processing time */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface InternalImpl{}

    /** Used for method appender sorting */
    public @interface MethodPriority{
        /** @return The priority */
        int value();
    }

    /** Indicates that the following field returned by this getter is gonna be initialized */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Initialize{
        /** @return The code that is gonna be used to evaluate the initializer */
        String value();

        /** @return Class arguments to be parsed into {@link #eval()}. */
        Class<?>[] args() default {};
    }

    /** Loads texture regions but does not assign them to their acquirers */
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadRegs{
        /** @return The regions' name */
        String[] value();

        /** @return Whether it should load the outlined region as well */
        boolean outline() default false;
    }

    //end region

    //anuke's implementation of annotation proxy maker, to replace the broken one from oracle
    //thanks, anuke
    //damn you, oracle
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class AnnotationProxyMaker{
        private final Compound anno;
        private final Class<? extends Annotation> annoType;

        private AnnotationProxyMaker(Compound var1, Class<? extends Annotation> var2){
            this.anno = var1;
            this.annoType = var2;
        }

        public static <A extends Annotation> A generateAnnotation(Compound var0, Class<A> var1){
            AnnotationProxyMaker var2 = new AnnotationProxyMaker(var0, var1);
            return (A)var1.cast(var2.generateAnnotation());
        }

        private Annotation generateAnnotation(){
            return AnnotationParser.annotationForMap(this.annoType, this.getAllReflectedValues());
        }

        private Map<String, Object> getAllReflectedValues(){
            LinkedHashMap var1 = new LinkedHashMap();
            Iterator var2 = this.getAllValues().entrySet().iterator();

            while(var2.hasNext()){
                Entry var3 = (Entry)var2.next();
                MethodSymbol var4 = (MethodSymbol)var3.getKey();
                Object var5 = this.generateValue(var4, (Attribute)var3.getValue());
                if(var5 != null){
                    var1.put(var4.name.toString(), var5);
                }
            }

            return var1;
        }

        private Map<MethodSymbol, Attribute> getAllValues(){
            LinkedHashMap map = new LinkedHashMap();
            ClassSymbol cl = (ClassSymbol)this.anno.type.tsym;

            try{
                Class entryClass = Class.forName("com.sun.tools.javac.code.Scope$Entry");
                Object members = cl.members();
                Field field = members.getClass().getField("elems");
                Object elems = field.get(members);
                Field siblingField = entryClass.getField("sibling");
                Field symField = entryClass.getField("sym");
                for(Object currEntry = elems; currEntry != null; currEntry = siblingField.get(currEntry)){
                    handleSymbol((Symbol)symField.get(currEntry), map);
                }

            }catch(Throwable e){
                try{
                    Class lookupClass = Class.forName("com.sun.tools.javac.code.Scope$LookupKind");
                    Field nonRecField = lookupClass.getField("NON_RECURSIVE");
                    Object nonRec = nonRecField.get(null);
                    Scope scope = cl.members();
                    Method getSyms = scope.getClass().getMethod("getSymbols", lookupClass);
                    Iterable<Symbol> it = (Iterable<Symbol>)getSyms.invoke(scope, nonRec);
                    Iterator<Symbol> i = it.iterator();
                    while(i.hasNext()){
                        handleSymbol(i.next(), map);
                    }
                }catch(Throwable death){
                    throw new RuntimeException(death);
                }
            }

            for(Pair var7 : this.anno.values){
                map.put(var7.fst, var7.snd);
            }

            return map;
        }

        private void handleSymbol(Symbol sym, LinkedHashMap map){
            if(sym.getKind() == ElementKind.METHOD){
                MethodSymbol var4 = (MethodSymbol)sym;
                Attribute var5 = var4.getDefaultValue();
                if(var5 != null){
                    map.put(var4, var5);
                }
            }
        }

        private Object generateValue(MethodSymbol var1, Attribute var2){
            AnnotationProxyMaker.ValueVisitor var3 = new AnnotationProxyMaker.ValueVisitor(var1);
            return var3.getValue(var2);
        }

        private class ValueVisitor implements Visitor{
            private MethodSymbol meth;
            private Class<?> returnClass;
            private Object value;

            ValueVisitor(MethodSymbol var2){
                this.meth = var2;
            }

            Object getValue(Attribute var1){
                Method var2;
                try{
                    var2 = AnnotationProxyMaker.this.annoType.getMethod(this.meth.name.toString());
                }catch(NoSuchMethodException var4){
                    return null;
                }

                this.returnClass = var2.getReturnType();
                var1.accept(this);
                if(!(this.value instanceof ExceptionProxy) && !AnnotationType.invocationHandlerReturnType(this.returnClass).isInstance(this.value)){
                    this.typeMismatch(var2, var1);
                }

                return this.value;
            }

            public void visitConstant(Constant var1){
                this.value = var1.getValue();
            }

            public void visitClass(com.sun.tools.javac.code.Attribute.Class var1){
                this.value = mirrorProxy(var1.classType);
            }

            public void visitArray(Array var1){
                Name var2 = ((ArrayType)var1.type).elemtype.tsym.getQualifiedName();
                int var6;
                if(var2.equals(var2.table.names.java_lang_Class)){
                    ListBuffer var14 = new ListBuffer();
                    Attribute[] var15 = var1.values;
                    int var16 = var15.length;

                    for(var6 = 0; var6 < var16; ++var6){
                        Attribute var7 = var15[var6];
                        Type var8 = var7 instanceof UnresolvedClass ? ((UnresolvedClass)var7).classType : ((com.sun.tools.javac.code.Attribute.Class)var7).classType;
                        var14.append(var8);
                    }

                    this.value = mirrorProxy(var14.toList());
                }else{
                    int var3 = var1.values.length;
                    Class var4 = this.returnClass;
                    this.returnClass = this.returnClass.getComponentType();

                    try{
                        Object var5 = java.lang.reflect.Array.newInstance(this.returnClass, var3);

                        for(var6 = 0; var6 < var3; ++var6){
                            var1.values[var6].accept(this);
                            if(this.value == null || this.value instanceof ExceptionProxy){
                                return;
                            }

                            try{
                                java.lang.reflect.Array.set(var5, var6, this.value);
                            }catch(IllegalArgumentException var12){
                                this.value = null;
                                return;
                            }
                        }

                        this.value = var5;
                    }finally{
                        this.returnClass = var4;
                    }
                }
            }

            public void visitEnum(Enum var1){
                if(this.returnClass.isEnum()){
                    String var2 = var1.value.toString();

                    try{
                        this.value = java.lang.Enum.valueOf((Class)this.returnClass, var2);
                    }catch(IllegalArgumentException var4){
                        this.value = proxify(() -> new EnumConstantNotPresentException((Class)this.returnClass, var2));
                    }
                }else{
                    this.value = null;
                }
            }

            public void visitCompound(Compound var1){
                try{
                    Class var2 = this.returnClass.asSubclass(Annotation.class);
                    this.value = AnnotationProxyMaker.generateAnnotation(var1, var2);
                }catch(ClassCastException var3){
                    this.value = null;
                }

            }

            public void visitError(Error var1){
                if(var1 instanceof UnresolvedClass){
                    this.value = mirrorProxy(((UnresolvedClass)var1).classType);
                }else{
                    this.value = null;
                }
            }

            private void typeMismatch(Method var1, final Attribute var2){
                this.value = proxify(() -> new AnnotationTypeMismatchException(var1, var2.type.toString()));
            }
        }

        private static Object mirrorProxy(Type t){
            return proxify(() -> new MirroredTypeException(t));
        }

        private static Object mirrorProxy(List<Type> t){
            return proxify(() -> new MirroredTypesException(t));
        }

        private static <T extends Throwable> Object proxify(Prov<T> prov){
            try{
                return new ExceptionProxy(){
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected RuntimeException generateException(){
                        return (RuntimeException)prov.get();
                    }
                };
            }catch(Throwable t){
                throw new RuntimeException(t);
            }
        }
    }
}
