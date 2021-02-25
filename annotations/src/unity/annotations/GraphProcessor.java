package unity.annotations;

import arc.struct.*;
import unity.annotations.Annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

import com.squareup.javapoet.*;
import com.sun.source.tree.*;

import java.util.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.GraphComp",
    "unity.annotations.Annotations.GraphInterface",
    "unity.annotations.Annotations.GraphDef"
})
public class GraphProcessor extends BaseProcessor{
    Seq<TypeElement> allComps = new Seq<>();
    Seq<VariableElement> allDefs = new Seq<>();
    Seq<TypeElement> allInters = new Seq<>();
    ObjectMap<TypeElement, TypeElement> interToComp = new ObjectMap<>();

    StringMap varInits = new StringMap();
    StringMap methodBlocks = new StringMap();

    TypeElement base;

    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        allComps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(GraphComp.class));
        allDefs.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(GraphDef.class));
        allInters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(GraphInterface.class));

        if(round == 1){
            Seq<TypeElement> base = allComps.select(t -> t.getAnnotation(GraphComp.class).base());
            if(base.size > 1){
                throw new IllegalArgumentException("Only 1 class may be annotated with based @GraphComp");
            }

            this.base = base.first();
            for(TypeElement t : allComps){
                TypeSpec.Builder builder = classToInterface(t)
                    .addAnnotation(GraphInterface.class)
                    .addModifiers(Modifier.PUBLIC);
                types(t).each(type -> builder.addType(classToInterface(type)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .build())
                );

                write(builder.build());
            }
        }else if(round == 2){
            for(TypeElement t : allInters){
                interToComp.put(t, allComps.find(c -> 
                    c.getQualifiedName().toString().equals(
                        elementUtils.getDocComment(t).split("\\s")[3]
                    )
                ));
            }

            for(VariableElement v : allDefs){
                //GraphDef def = v.getAnnotation(GraphDef.class);
                Seq<TypeElement> inters = Seq.with(elementUtils.getTypeElement("unity.gen.Heatg"));

                StringBuilder name = new StringBuilder();
                for(TypeElement t : inters){
                    String raw = t.getSimpleName().toString();
                    name.append(raw.endsWith("g") ? raw.substring(0, raw.length() - 1) : raw);
                }
                name.append("Block");

                TypeSpec.Builder builder = TypeSpec.classBuilder(name.toString())
                    .addModifiers(Modifier.PUBLIC);

                for(TypeElement inter : inters){
                    builder.addSuperinterface(inter.asType());

                    TypeElement comp = interToComp.get(inter);
                    for(VariableElement var : vars(comp)){
                        FieldSpec.Builder field = FieldSpec.builder(tName(var), var.getSimpleName().toString(), Modifier.PUBLIC);
                        if(varInits.containsKey(descString(var))){
                            field.initializer(varInits.get(descString(var)));
                        }

                        builder.addField(field.build());
                    }

                    for(ExecutableElement m : methods(comp)){
                        if(isConstructor(m)) continue;
                        ExecutableElement up = method(comp, m.getSimpleName().toString(), m.getReturnType(), m.getParameters());

                        String label = ((TypeElement)up.getEnclosingElement()).getSimpleName().toString();
                        label = label.substring(0, label.length() - 5).toLowerCase();

                        builder.addMethod(
                            MethodSpec.methodBuilder(m.getSimpleName().toString())
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeName.get(m.getReturnType()))
                                .addParameters(Seq.with(m.getParameters()).map(ParameterSpec::get))
                                .beginControlFlow("$L:", label)
                                    .addCode(
                                        methodBlocks.get(descString(up))
                                        .replace("return;", "break " + label + ";")
                                    )
                                    .addCode(lnew())
                                .endControlFlow()
                            .build()
                        );
                    }

                    for(ExecutableElement m : methods(inter)){
                        if(m.getReturnType().getKind() != TypeKind.VOID){
                            VariableElement field = field(comp, m.getSimpleName().toString(), m.getReturnType());
                            builder.addMethod(
                                MethodSpec.methodBuilder(m.getSimpleName().toString())
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(tName(field))
                                    .addStatement("return $L", m.getSimpleName().toString())
                                .build()
                            );
                        }else if(
                            m.getParameters().size() == 1 &&
                            m.getSimpleName().toString().equals(
                                m.getParameters().get(0).getSimpleName().toString()
                            )
                        ){
                            ExecutableElement getter = methods(inter).find(me -> 
                                me.getReturnType().getKind() != TypeKind.VOID &&
                                me.getParameters().size() == 0 &&
                                me.getSimpleName().toString().equals(
                                    m.getSimpleName().toString()
                                )
                            );

                            builder.addMethod(
                                MethodSpec.methodBuilder(m.getSimpleName().toString())
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.VOID)
                                    .addParameter(
                                        ParameterSpec.builder(
                                            TypeName.get(getter.getReturnType()),
                                            m.getSimpleName().toString()
                                        )
                                        .build()
                                    )
                                    .addStatement("this.$L = $L", m.getSimpleName().toString(), m.getSimpleName().toString())
                                .build()
                            );
                        }
                    }
                }

                write(builder.build());
            }
        }
    }

    String interfaceName(TypeElement comp){
        String compName = comp.getSimpleName().toString();

        if(compName.endsWith("Graph")){
            return compName.substring(0, compName.length() - 5) + "g";
        }else if(compName.endsWith("GraphBuild")){
            return compName.substring(0, compName.length() - 10) + "gb";
        }else{
            throw new IllegalStateException("All graph components name must end with 'Graph'");
        }
    }

    TypeSpec.Builder classToInterface(TypeElement t){
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(interfaceName(t))
            .addJavadoc("Interface for $L", t.getQualifiedName().toString());

        for(TypeMirror s : t.getInterfaces()){
            builder.addSuperinterface(s);
        }

        for(VariableElement v : vars(t)){
            VariableTree tree = (VariableTree)treeUtils.getTree(v);

            if(tree.getInitializer() != null){
                varInits.put(descString(v), tree.getInitializer().toString());
            }
        }

        ObjectSet<String> preserved = new ObjectSet<>();
        for(ExecutableElement m : methods(t)){
            if(is(m, Modifier.ABSTRACT, Modifier.NATIVE) || isConstructor(m)) continue;
            preserved.add(m.toString());

            MethodTree tree = treeUtils.getTree(m);
            methodBlocks.put(descString(m), tree.getBody().toString()
                .replaceAll("this\\.\\<.+\\>self\\(\\)", "this") //this.<...>self() -> this
                .replaceAll("self()", "this") //self() -> this
                .replaceAll("\\/\\* missing \\*\\/", "var") //notype -> var
            );
        }

        for(VariableElement v : vars(t)){
            if(is(v, Modifier.PRIVATE, Modifier.STATIC)) continue;

            if(!preserved.contains(v.getSimpleName().toString() + "()")){
                builder.addMethod(
                    MethodSpec.methodBuilder(v.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addJavadoc(docs(v))
                        .returns(tName(v))
                    .build()
                );
            }

            if(
                !preserved.contains(v.getSimpleName().toString() + "(" + v.asType().toString() + ")") &&
                v.getAnnotation(ReadOnly.class) == null
            ){
                builder.addMethod(
                    MethodSpec.methodBuilder(v.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(TypeName.VOID)
                        .addParameter(
                            ParameterSpec.builder(tName(v), v.getSimpleName().toString())
                            .build()
                        )
                    .build()
                );
            }
        }

        return builder;
    }
}
