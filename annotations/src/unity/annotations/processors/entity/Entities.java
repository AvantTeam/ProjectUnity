package unity.annotations.processors.entity;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import com.squareup.javapoet.*;
import unity.annotations.Annotations.*;
import unity.annotations.processors.*;
import unity.annotations.processors.util.*;
import unity.annotations.processors.util.TypeIOResolver.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import java.lang.annotation.*;
import java.util.*;

import static javax.lang.model.type.TypeKind.*;

@SuppressWarnings("unchecked")
public abstract class Entities extends BaseProcessor{
    protected Seq<TypeElement> comps = new Seq<>();
    protected Seq<TypeElement> baseComps = new Seq<>();
    protected Seq<Element> pointers = new Seq<>();
    protected Seq<TypeSpec.Builder> baseClasses = new Seq<>();
    protected ObjectMap<TypeElement, ObjectSet<TypeElement>> baseClassDeps = new ObjectMap<>();
    protected ObjectMap<TypeElement, Seq<TypeElement>> componentDependencies = new ObjectMap<>();
    protected ObjectMap<TypeElement, ObjectMap<String, Seq<ExecutableElement>>> inserters = new ObjectMap<>();
    protected ObjectMap<TypeElement, ObjectMap<String, Seq<ExecutableElement>>> wrappers = new ObjectMap<>();
    protected Seq<TypeElement> inters = new Seq<>();
    protected Seq<Element> defs = new Seq<>();
    protected Seq<EntityDefinition> definitions = new Seq<>();

    protected StringMap varInitializers = new StringMap();
    protected StringMap methodBlocks = new StringMap();
    protected ObjectMap<String, Seq<String>> imports = new ObjectMap<>();
    protected ObjectMap<TypeElement, String> groups;
    protected ClassSerializer serializer;

    protected Seq<Cons<RoundEnvironment>> tasks = new Seq<>();

    {
        rounds = 2;

        tasks.add(roundEnv -> {
            comps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(compAnno()));
            baseComps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(baseCompAnno()));
            inters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(interAnno()));
            defs.addAll(roundEnv.getElementsAnnotatedWith(EntityDef.class));
            pointers.addAll(roundEnv.getElementsAnnotatedWith(EntityPoint.class));

            for(ExecutableElement e : (Set<ExecutableElement>)roundEnv.getElementsAnnotatedWith(Insert.class)){
                if(!e.getParameters().isEmpty()) throw new IllegalStateException("All @Insert methods must not have parameters");

                TypeElement type = comps.find(c -> simpleName(c).equals(simpleName(e.getEnclosingElement())));
                if(type == null) continue;

                Insert ann = annotation(e, Insert.class);
                inserters
                    .get(type, ObjectMap::new)
                    .get(ann.value(), Seq::new)
                    .add(e);
            }

            for(ExecutableElement e : (Set<ExecutableElement>)roundEnv.getElementsAnnotatedWith(Wrap.class)){
                if(!e.getParameters().isEmpty()) throw new IllegalStateException("All @Wrap methods must not have parameters");
                if(e.getReturnType().getKind() != BOOLEAN) throw new IllegalStateException("All @Wrap methods must have boolean return type");

                TypeElement type = comps.find(c -> simpleName(c).equals(simpleName(e.getEnclosingElement())));
                if(type == null) continue;

                Wrap ann = annotation(e, Wrap.class);
                wrappers
                    .get(type, ObjectMap::new)
                    .get(ann.value(), Seq::new)
                    .add(e);
            }
        });

        tasks.add(roundEnv -> {
            serializer = TypeIOResolver.resolve(this);

        });
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        for(int i = 1; i <= tasks.size; i++){
            if(round == i){
                tasks.get(i - 1).get(roundEnv);
            }
        }
    }

    protected void begin(DefaultTask task){
        
    }

    protected abstract <T extends Annotation> Class<T> compAnno();

    protected abstract <T extends Annotation> Class<T> baseCompAnno();

    protected abstract <T extends Annotation> Class<T> interAnno();

    protected void append(MethodSpec.Builder mbuilder, Seq<ExecutableElement> values, Seq<ExecutableElement> inserts, Seq<ExecutableElement> wrappers, boolean writeBlock){
        for(ExecutableElement elem : values){
            String descStr = descString(elem);
            String blockName = simpleName(elem.getEnclosingElement()).toLowerCase().replace("comp", "");

            Seq<ExecutableElement> insertComp = inserts.select(e ->
                simpleName(toComp((TypeElement)elements(annotation(e, Insert.class)::block).first()))
                    .toLowerCase().replace("comp", "")
                    .equals(blockName)
            );

            Seq<ExecutableElement> wrapComp = wrappers.select(e ->
                simpleName(toComp((TypeElement)elements(annotation(e, Wrap.class)::block).first()))
                    .toLowerCase().replace("comp", "")
                    .equals(blockName)
            );

            if(is(elem, Modifier.ABSTRACT) || is(elem, Modifier.NATIVE) || (!methodBlocks.containsKey(descStr) && insertComp.isEmpty())) continue;

            Seq<ExecutableElement> compBefore = insertComp.select(e -> !annotation(e, Insert.class).after());
            compBefore.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

            Seq<ExecutableElement> compAfter = insertComp.select(e -> annotation(e, Insert.class).after());
            compAfter.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

            String str = methodBlocks.get(descStr)
                .replaceAll("\\s+", "")
                .replace("\n", "");

            if(!wrapComp.isEmpty()){
                StringBuilder format = new StringBuilder("if(");
                Seq<Object> args = new Seq<>();

                for(int i = 0; i < wrapComp.size; i++){
                    ExecutableElement e = wrapComp.get(i);

                    format.append("this.$L()");
                    args.add(simpleName(e));

                    if(i < wrapComp.size - 1) format.append(" && ");
                }

                format.append(")");
                mbuilder.beginControlFlow(format.toString(), args.toArray());
            }

            for(ExecutableElement e : compBefore){
                mbuilder.addStatement("this.$L()", simpleName(e));
            }

            writeBlock &= !str.isEmpty();
            if(writeBlock){
                if(annotation(elem, BreakAll.class) == null){
                    str = str.replace("return;", "break " + blockName + ";");
                }

                mbuilder.beginControlFlow("$L:", blockName);
            }

            mbuilder.addCode(str);

            if(writeBlock) mbuilder.endControlFlow();

            for(ExecutableElement e : compAfter){
                mbuilder.addStatement("this.$L()", simpleName(e));
            }

            if(!wrapComp.isEmpty()){
                mbuilder.endControlFlow();
            }
        }
    }

    protected Seq<TypeElement> getDependencies(TypeElement component){
        if(!componentDependencies.containsKey(component)){
            ObjectSet<TypeElement> out = new ObjectSet<>();

            Seq<TypeElement> list = Seq.with(component.getInterfaces())
                .map(i -> toComp(compName(simpleName(toEl(i)))))
                .select(Objects::nonNull);

            out.addAll(list);
            out.remove(component);

            ObjectSet<TypeElement> result = new ObjectSet<>();
            for(TypeElement type : out){
                result.add(type);
                result.addAll(getDependencies(type));
            }

            if(annotation(component, EntityBaseComponent.class) == null){
                result.addAll(baseComps);
            }

            out.remove(component);
            componentDependencies.put(component, result.asArray());
        }

        return componentDependencies.get(component);
    }

    protected boolean isCompInterface(TypeElement type){
        return toComp(type) != null;
    }

    protected TypeName procName(TypeElement comp, Func<TypeElement, String> name){
        return ClassName.get(
            comp.getEnclosingElement().toString().contains("fetched") ? "mindustry.gen" : packageName,
            name.get(comp)
        );
    }

    protected String compName(String interfaceName){
        return interfaceName.substring(0, interfaceName.length() - 1) + "Comp";
    }

    protected TypeElement toComp(TypeElement inter){
        String name = simpleName(inter);
        if(!name.endsWith("c")) return null;

        return toComp(compName(name));
    }

    protected TypeElement toComp(String compName){
        return comps.find(t -> simpleName(t).equals(compName));
    }

    protected TypeElement toComp(Class<?> inter){
        return toComp(toType(inter));
    }

    protected String interfaceName(TypeElement type){
        return baseName(type) + "c";
    }

    protected String baseName(TypeElement type){
        String name = simpleName(type);
        if(!name.endsWith("Comp")){
            throw new IllegalStateException("All types annotated with @EntityComponent must have 'Comp' as the name's suffix");
        }

        return name.substring(0, name.length() - 4);
    }

    protected String createName(Seq<TypeElement> comps){
        Seq<TypeElement> rev = comps.copy();
        rev.reverse();

        return rev.toString("", s -> simpleName(s).replace("Comp", ""));
    }

    protected static class EntityDefinition{
        protected final Seq<String> groups;
        protected final Seq<TypeElement> components;
        protected final Seq<FieldSpec> fieldSpecs;
        protected final TypeSpec.Builder builder;
        protected final Element naming;
        protected final String name;
        protected final TypeName extend;

        protected EntityDefinition(String name, TypeSpec.Builder builder, Element naming, TypeName extend, Seq<TypeElement> components, Seq<String> groups, Seq<FieldSpec> fieldSpec){
            this.builder = builder;
            this.name = name;
            this.naming = naming;
            this.groups = groups;
            this.components = components;
            this.extend = extend;
            this.fieldSpecs = fieldSpec;
        }

        @Override
        public String toString(){
            return
                "EntityDefinition{" +
                    "groups=" + groups +
                    "components=" + components +
                    ", base=" + naming +
                    '}';
        }
    }

    protected enum DefaultTask{
        generateInterface,
        createDef,
        writeDef
    }
}
