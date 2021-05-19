package unity.annotations;

import arc.struct.*;
import com.squareup.javapoet.*;
import com.sun.source.tree.*;
import unity.annotations.Annotations.*;
import unity.annotations.TypeIOResolver.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.Merge",
    "unity.annotations.Annotations.MergeComponent",
    "unity.annotations.Annotations.MergeInterface"
})
public class MergeProcessor extends BaseProcessor{
    Seq<TypeElement> comps = new Seq<>();
    Seq<TypeElement> inters = new Seq<>();
    Seq<Element> defs = new Seq<>();
    ObjectMap<TypeElement, ObjectMap<String, Seq<ExecutableElement>>> inserters = new ObjectMap<>();
    StringMap varInitializers = new StringMap();
    StringMap methodBlocks = new StringMap();
    ObjectMap<String, Seq<String>> imports = new ObjectMap<>();
    ObjectMap<TypeElement, Seq<TypeElement>> componentDependencies = new ObjectMap<>();
    ClassSerializer serializer;

    {
        rounds = 1;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        comps = comps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(MergeComponent.class)).flatMap(t -> Seq.with(t).and(types(t)));
        inters = inters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(MergeInterface.class)).flatMap(t -> Seq.with(t).and(types(t)));
        defs.addAll(roundEnv.getElementsAnnotatedWith(Merge.class));

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

        if(round == 1){
            serializer = TypeIOResolver.resolve(this);

            for(TypeElement comp : comps.select(t -> t.getEnclosingElement() instanceof PackageElement)){
                TypeSpec.Builder builder = toInterface(comp, getDependencies(comp))
                    .addAnnotation(cName(EntityInterface.class))
                    .addAnnotation(
                        AnnotationSpec.builder(cName(SuppressWarnings.class))
                            .addMember("value", "$S", "all")
                            .build()
                    );

                for(TypeElement subtype : types(comp)){
                    TypeSpec.Builder sBuilder = toInterface(subtype, getDependencies(subtype));
                    builder.addType(sBuilder.build());
                }

                write(builder.build());
            }
        }
    }

    TypeSpec.Builder toInterface(TypeElement comp, Seq<TypeElement> depends){
        TypeSpec.Builder inter = TypeSpec.interfaceBuilder(interfaceName(comp)).addModifiers(Modifier.PUBLIC);

        if(comp.getEnclosingElement() instanceof TypeElement){
            inter.addModifiers(Modifier.STATIC);
        }

        for(TypeElement extraInterface : Seq.with(comp.getInterfaces()).map(BaseProcessor::toEl).<TypeElement>as().select(i -> !isCompInterface(i))){
            inter.addSuperinterface(cName(extraInterface));
        }

        for(TypeElement type : depends){
            inter.addSuperinterface(cName(type));
        }

        for(ExecutableElement m : methods(comp)){
            if(is(m, Modifier.ABSTRACT, Modifier.NATIVE)) continue;

            methodBlocks.put(descString(m), treeUtils.getTree(m).getBody().toString()
                .replaceAll("this\\.<(.*)>self\\(\\)", "this")
                .replaceAll("self\\(\\)(?!\\s+instanceof)", "this")
                .replaceAll(" yield ", "")
                .replaceAll("\\/\\*missing\\*\\/", "var")
            );
        }

        for(VariableElement var : vars(comp)){
            VariableTree tree = (VariableTree)treeUtils.getTree(var);
            if(tree.getInitializer() != null){
                varInitializers.put(descString(var), tree.getInitializer().toString());
            }
        }

        imports.put(interfaceName(comp), getImports(comp));
        ObjectSet<String> preserved = new ObjectSet<>();

        for(ExecutableElement m : methods(comp).select(me -> !isConstructor(me) && !is(me, Modifier.PRIVATE, Modifier.STATIC))){
            String name = simpleName(m);
            preserved.add(m.toString());

            if(annotation(m, Override.class) == null){
                inter.addMethod(
                    MethodSpec.methodBuilder(name)
                        .addTypeVariables(Seq.with(m.getTypeParameters()).map(TypeVariableName::get))
                        .addExceptions(Seq.with(m.getThrownTypes()).map(TypeName::get))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameters(Seq.with(m.getParameters()).map(ParameterSpec::get))
                        .returns(TypeName.get(m.getReturnType()))
                        .build()
                );
            }
        }

        for(VariableElement var : vars(comp).select(v -> !is(v, Modifier.STATIC) && !is(v, Modifier.PRIVATE) && annotation(v, Import.class) == null)){
            String name = simpleName(var);

            if(!preserved.contains(name + "()")){
                inter.addMethod(
                    MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(tName(var))
                        .build()
                );
            }

            if(
                !is(var, Modifier.FINAL) &&
                    !preserved.contains(name + "(" + var.asType().toString() + ")") &&
                    annotation(var, ReadOnly.class) == null
            ){
                inter.addMethod(
                    MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(tName(var), name)
                        .returns(TypeName.VOID)
                        .build()
                );
            }
        }

        return inter;
    }

    boolean isCompInterface(TypeElement type){
        return toComp(type) != null;
    }

    String interfaceName(TypeElement type){
        return baseName(type) + "c";
    }

    String baseName(TypeElement type){
        String name = simpleName(type);
        if(!name.endsWith("Comp")){
            throw new IllegalStateException("All types annotated with @EntityComp must have 'Comp' as the name's suffix");
        }

        return name.substring(0, name.length() - 4);
    }

    TypeElement toComp(TypeElement inter){
        String name = simpleName(inter);
        if(!name.endsWith("c")) return null;

        String compName = name.substring(0, name.length() - 1) + "Comp";
        return comps.find(t -> simpleName(t).equals(compName));
    }

    Seq<TypeElement> getDependencies(TypeElement component){
        if(!componentDependencies.containsKey(component)){
            ObjectSet<TypeElement> out = new ObjectSet<>();

            out.addAll(Seq.with(component.getInterfaces())
                .map(BaseProcessor::toEl)
                .<TypeElement>as()
                .map(t -> inters.find(i -> simpleName(t).equals(simpleName(i))))
                .select(Objects::nonNull)
                .map(this::toComp)
            );

            out.remove(component);

            ObjectSet<TypeElement> result = new ObjectSet<>();
            for(TypeElement type : out){
                result.add(type);
                result.addAll(getDependencies(type));
            }

            out.remove(component);
            componentDependencies.put(component, result.asArray());
        }

        return componentDependencies.get(component);
    }
}
