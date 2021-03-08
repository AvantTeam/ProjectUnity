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
    "unity.annotations.Annotations.Merge",
    "unity.annotations.Annotations.MergeComp",
    "unity.annotations.Annotations.MergeInterface"
})
public class MergeProcessor extends BaseProcessor{
    Seq<TypeElement> comps = new Seq<>();
    Seq<TypeElement> inters = new Seq<>();
    Seq<Element> defs = new Seq<>();

    StringMap varInitializers = new StringMap();
    StringMap methodBlocks = new StringMap();
    ObjectMap<String, Seq<String>> imports = new ObjectMap<>();

    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        comps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(MergeComp.class));
        inters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(MergeInterface.class));
        defs.addAll(roundEnv.getElementsAnnotatedWith(Merge.class));

        if(round == 1){
            for(TypeElement comp : comps){
                TypeSpec.Builder builder = toInterface(comp);
                builder.addJavadoc("Interface for $L", comp.getQualifiedName().toString());

                String build = null;
                for(TypeElement inner : types(comp)){
                    TypeSpec innerSpec = toInterface(inner).build();
                    builder.addType(innerSpec);

                    if(typeUtils.isSubtype(inner.asType(), elementUtils.getTypeElement("mindustry.gen.Buildingc").asType())){
                        if(build == null){
                            build = inner.getSimpleName().toString();
                        }else{
                            throw new IllegalStateException("Multiple building type in " + comp.getQualifiedName().toString());
                        }
                    }
                }

                if(build == null){
                    builder.addAnnotation(MergeInterface.class);
                }else{
                    builder.addAnnotation(
                        AnnotationSpec.builder(MergeInterface.class)
                            .addMember("buildType", "$L.$L.class", comp.getSimpleName().toString(), build)
                        .build()
                    );
                }

                write(builder.build());
            }
        }else if(round == 2){
            for(Element e : defs){
                Merge merge = annotation(e, Merge.class);
                TypeElement base;
                if(e instanceof TypeElement){
                    base = (TypeElement)e;
                }else{
                    base = (TypeElement)elements(merge::base).first();
                }
                Seq<TypeElement> value = elements(merge::value).<TypeElement>as().map(t -> {
                    return inters.find(i -> i.getQualifiedName().toString().equals(
                        t.getSimpleName().toString()
                    ));
                });

                StringBuilder n = new StringBuilder();
                for(TypeElement t : value){
                    String raw = t.getSimpleName().toString();
                    n.append(raw);
                }
                n.append(base.getSimpleName().toString());
                String name = n.toString();

                TypeSpec.Builder type = TypeSpec.classBuilder(name).addModifiers(Modifier.PUBLIC);
                type.superclass(cName(base));
                value.each(t -> type.addSuperinterface(cName(t)));

                for(TypeElement inter : value){
                    TypeElement comp = comps.find(t -> t.getQualifiedName().toString().equals(
                        docs(inter).split("\\s+")[3]
                    ));

                    for(VariableElement v : vars(comp)){
                        String fname = v.getSimpleName().toString();

                        FieldSpec.Builder var = FieldSpec.builder(
                            tName(v),
                            fname,
                            annotation(v, ReadOnly.class) != null
                            ?   Modifier.PROTECTED
                            :   Modifier.PUBLIC
                        );

                        if(varInitializers.containsKey(descString(v))){
                            var.initializer(varInitializers.get(descString(v)));
                        }

                        type.addField(var.build());

                        Seq<ExecutableElement> methods = methods(inter);
                        ExecutableElement getter = methods.find(m ->
                            annotation(m, Getter.class) != null &&
                            m.getSimpleName().toString().equals(fname) &&
                            typeUtils.isSameType(m.getReturnType(), v.asType())
                        );
                        ExecutableElement setter = methods.find(m ->
                            annotation(m, Setter.class) != null &&
                            m.getSimpleName().toString().equals(fname) &&
                            m.getParameters().size() == 1 &&
                            typeUtils.isSameType(m.getParameters().get(0).asType(), v.asType())
                        );

                        if(getter != null){
                            type.addMethod(
                                MethodSpec.overriding(getter)
                                    .addStatement("return this.$L", fname)
                                .build()
                            );
                        }

                        if(setter != null){
                            type.addMethod(
                                MethodSpec.overriding(setter)
                                    .addStatement("this.$L = $L", fname, setter.getParameters().get(0).getSimpleName().toString())
                                .build()
                            );
                        }
                    }
                }

                ObjectMap<ExecutableElement, Seq<ExecutableElement>> appending = new ObjectMap<>();
                Seq<String> imports = new Seq<>();
                for(TypeElement inter : value){
                    appending.putAll(getAppendedMethods(base, inter));
                    imports.addAll(this.imports.get(inter.getSimpleName().toString(), Seq::with));
                }

                for(ExecutableElement appended : appending.keys().toSeq()){
                    Seq<ExecutableElement> appenders = appending.get(appended);
                    appenders.sort(m -> {
                        MethodPriority p = annotation(m, MethodPriority.class);
                        return p == null ? 0 : p.value();
                    });

                    boolean returns = appended.getReturnType().getKind() != TypeKind.VOID;
                    MethodSpec.Builder method = MethodSpec.overriding(appended);

                    StringBuilder params = new StringBuilder();
                    Seq<Object> args = Seq.with(appended.getSimpleName().toString());

                    List<? extends VariableElement> parameters = appended.getParameters();
                    for(int i = 0; i < parameters.size(); i++){
                        VariableElement var = parameters.get(i);

                        params.append("$L");
                        if(i < parameters.size() - 1) params.append(", ");
                        args.add(var.getSimpleName().toString());
                    }

                    if(!returns){
                        method.addStatement("super.$L(" + params.toString() + ")", args.toArray());
                        for(ExecutableElement app : appenders){
                            String doc = docs(app.getEnclosingElement());
                            TypeElement comp;
                            if(!doc.isEmpty()){
                                comp = comps.find(t -> t.getQualifiedName().toString().equals(
                                    doc.split("\\s+")[3]
                                ));
                            }else{
                                comp = (TypeElement)app.getEnclosingElement();
                            }

                            String label = app.getEnclosingElement().getSimpleName().toString().toLowerCase();
                            ExecutableElement up = method(comp, app.getSimpleName().toString(), app.getReturnType(), app.getParameters());

                            String body = methodBlocks.get(descString(up))
                            .replace("return;", "break " + label + ";");
                            body = body.substring(2, body.length() - 1).trim();

                            method
                                .addCode(lnew())
                                .beginControlFlow("$L:", label)
                                .addCode(body)
                                .addCode(lnew())
                            .endControlFlow();
                        }
                    }else{
                        
                    }

                    type.addMethod(method.build());
                }

                write(type.build(), imports.distinct());
            }
        }
    }

    TypeSpec.Builder toInterface(TypeElement comp){
        TypeSpec.Builder inter = TypeSpec.interfaceBuilder(comp.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC);

        if(comp.getEnclosingElement() instanceof TypeElement){
            inter.addModifiers(Modifier.STATIC);
        }

        imports.put(comp.getSimpleName().toString(), getImports(comp));

        ObjectSet<String> preserved = new ObjectSet<>();
        for(ExecutableElement m : methods(comp)){
            if(is(m, Modifier.PRIVATE, Modifier.STATIC)) continue;

            String name = m.getSimpleName().toString();
            preserved.add(m.toString());

            MethodTree tree = treeUtils.getTree(m);
            methodBlocks.put(descString(m), tree.getBody().toString());

            if(!isConstructor(m) && annotation(m, Override.class) == null){
                inter.addMethod(
                    MethodSpec.methodBuilder(name)
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

            if(!preserved.contains(name + "()")){
                inter.addMethod(
                    MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addAnnotation(Getter.class)
                        .returns(tName(var))
                    .build()
                );
            }

            if(
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

        return inter;
    }

    Seq<String> getImports(Element e){
        return Seq.with(treeUtils.getPath(e).getCompilationUnit().getImports()).map(Object::toString);
    }

    @Override
    ObjectMap<ExecutableElement, Seq<ExecutableElement>> getAppendedMethods(TypeElement base, TypeElement inter){
        ObjectMap<ExecutableElement, Seq<ExecutableElement>> appending = new ObjectMap<>();
        Seq<ExecutableElement> baseMethods = getMethodsRec(base);

        TypeElement comp = elementUtils.getTypeElement(docs(inter).split("\\s+")[3]);
        Seq<ExecutableElement> toAppend = methods(inter).and(methods(comp).select(m -> annotation(m, Override.class) != null)).select(m ->
            baseMethods.contains(e -> {
                if(e.getParameters().size() != m.getParameters().size()) return false;

                boolean same = true;
                for(int i = 0; i < e.getParameters().size(); i++){
                    if(same && !typeUtils.isSameType(e.getParameters().get(i).asType(), m.getParameters().get(i).asType())){
                        same = false;
                    }
                }

                return e.getSimpleName().toString().equals(m.getSimpleName().toString()) &&
                typeUtils.isSameType(e.getReturnType(), m.getReturnType()) &&
                same;
            })
        );

        for(ExecutableElement e : toAppend){
            ExecutableElement append = baseMethods.find(m -> {
                boolean equal = m.getParameters().size() == e.getParameters().size();
                for(int i = 0; i < m.getParameters().size(); i++){
                    if(!equal) break;
                    try{
                        VariableElement up = m.getParameters().get(i);
                        VariableElement c = e.getParameters().get(i);

                        equal = typeUtils.isSameType(up.asType(), c.asType());
                    }catch(IndexOutOfBoundsException ex){
                        return false;
                    }
                }

                return m.getSimpleName().toString().equals(e.getSimpleName().toString()) && equal;
            });

            if(append != null){
                appending.get(append, Seq::new).add(e);
            }
        }

        return appending;
    }
}
