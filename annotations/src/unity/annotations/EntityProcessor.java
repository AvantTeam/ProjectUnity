package unity.annotations;

import arc.struct.*;
import unity.annotations.Annotations.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;
import com.sun.source.tree.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.EntityComponent",
    "unity.annotations.Annotations.EntityInterface",
    "unity.annotations.Annotations.EntityDef"
})
public class EntityProcessor extends BaseProcessor{
    Seq<TypeElement> comps = new Seq<>();
    Seq<TypeElement> inters = new Seq<>();
    Seq<VariableElement> defs = new Seq<>();

    StringMap varInitializers = new StringMap();
    StringMap methodBlocks = new StringMap();
    ObjectMap<String, Seq<String>> imports = new ObjectMap<>();

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        comps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityComponent.class));
        inters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityInterface.class));
        defs.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(EntityDef.class));

        if(round == 1){
            for(TypeElement comp : comps){
                EntityComponent compAnno = annotation(comp, EntityComponent.class);
                TypeSpec.Builder inter = null;

                boolean write = compAnno.write();
                if(write){
                    inter = TypeSpec.interfaceBuilder(interfaceName(comp))
                        .addModifiers(Modifier.PUBLIC)
                        .addJavadoc("Interface for $L", comp.getQualifiedName().toString());
                }

                if(write && comp.getEnclosingElement() instanceof TypeElement){
                    inter.addModifiers(Modifier.STATIC);
                }

                imports.put(interfaceName(comp), getImports(comp));

                ObjectSet<String> preserved = new ObjectSet<>();
                for(ExecutableElement m : methods(comp).select(me -> !isConstructor(me))){
                    BlockTree tree = treeUtils.getTree(m).getBody();
                    if(tree != null){
                        methodBlocks.put(descString(m), tree.toString()
                            .replaceAll("this\\.<(.*)>self\\(\\)", "this")
                            .replaceAll("self\\(\\)(?!\\s+instanceof)", "this")
                            .replaceAll(" yield ", "")
                            .replaceAll("\\/\\*missing\\*\\/", "var")
                        );
                    }

                    if(is(m, Modifier.PRIVATE, Modifier.STATIC)) continue;

                    String name = m.getSimpleName().toString();
                    preserved.add(m.toString());

                    if(write && annotation(m, Override.class) == null){
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

                for(VariableElement var : vars(comp)){
                    String name = var.getSimpleName().toString();

                    VariableTree tree = (VariableTree)treeUtils.getTree(var);
                    if(tree.getInitializer() != null){
                        varInitializers.put(descString(var), tree.getInitializer().toString());
                    }

                    if(write && !preserved.contains(name + "()")){
                        inter.addMethod(
                            MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addAnnotation(Getter.class)
                                .returns(tName(var))
                            .build()
                        );
                    }

                    if(
                        write &&
                        !preserved.contains(name + "(" + var.asType().toString() + ")") &&
                        annotation(var, ReadOnly.class) == null
                    ){
                        inter.addMethod(
                            MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addParameter(
                                    ParameterSpec.builder(tName(var), name)
                                    .build()
                                )
                                .addAnnotation(Setter.class)
                                .returns(TypeName.VOID)
                            .build()
                        );
                    }
                }

                if(write) write(inter.build());
            }
        }else if(round == 2){

        }
    }

    String interfaceName(TypeElement type){
        String name = type.getSimpleName().toString();
        if(!name.endsWith("Comp")){
            throw new IllegalStateException("All types annotated with @EntityComp must have 'Comp' as the name's suffix");
        }

        return name.substring(0, name.length() - 4) + "c";
    }

    Seq<String> getImports(Element e){
        return Seq.with(treeUtils.getPath(e).getCompilationUnit().getImports()).map(Object::toString);
    }
}
