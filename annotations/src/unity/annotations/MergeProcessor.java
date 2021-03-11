package unity.annotations;

import arc.func.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

import com.squareup.javapoet.*;
import com.sun.source.tree.*;

import java.util.*;
import java.util.regex.*;

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
    ObjectMap<String, ObjectMap<Seq<? extends VariableElement>, ObjectMap<ExecutableElement, String>>> constructorBlocks = new ObjectMap<>();
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

                if(types(comp).size > 1) throw new IllegalStateException("Multiple nested class in " + comp.getQualifiedName().toString());

                TypeElement build = null;
                if(!types(comp).isEmpty()){
                    TypeElement inner = types(comp).first();
                    TypeSpec.Builder innerSpec = toInterface(inner);

                    build = inner;
                    innerSpec.addSuperinterface(Buildingc.class);

                    builder.addType(innerSpec.build());
                }

                if(build == null){
                    builder.addAnnotation(MergeInterface.class);
                }else{
                    builder.addAnnotation(
                        AnnotationSpec.builder(MergeInterface.class)
                            .addMember("buildType", "$L.$L.class", interfaceName(comp), interfaceName(build))
                        .build()
                    );
                }

                builder.addAnnotation(
                    AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "all")
                    .build()
                );

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
                Seq<TypeElement> value = elements(merge::value).<TypeElement>as().map(t -> inters.find(i -> {
                    return i.getSimpleName().toString().equals(
                        t.getSimpleName().toString()
                    );
                }));

                Entry<TypeSpec.Builder, Seq<String>> block = toClass(base, value);
                Seq<String> imports = block.value;

                Func<TypeElement, TypeElement> findBuild = type -> {
                    TypeElement current = type;

                    while(
                        current != null &&
                        !typeUtils.isSameType(current.asType(), elementUtils.getTypeElement("mindustry.world.Block").asType())
                    ){
                        TypeElement[] c = {current};
                        TypeElement build = types(c[0]).find(t -> Seq.with(getInterfaces(t)).contains(t2 ->
                            t2.getQualifiedName().toString().equals("mindustry.gen.Building")
                        ));

                        if(build != null){
                            return build;
                        }else{
                            current = (TypeElement)toEl(current.getSuperclass());
                        }
                    }

                    return elementUtils.getTypeElement("mindustry.gen.Building");
                };

                Seq<TypeElement> buildingComps = value
                    .map(t -> (TypeElement)elements(annotation(t, MergeInterface.class)::buildType).first())
                    .select(t -> !t.getQualifiedName().toString().equals(
                        Building.class.getCanonicalName()
                    ));

                Entry<TypeSpec.Builder, Seq<String>> build = toClass(findBuild.get(base), buildingComps);
                block.key.addType(build.key.build());
                imports.addAll(build.value);

                block.key.addAnnotation(
                    AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "all")
                    .build()
                );

                write(block.key.build(), imports);
            }
        }
    }

    TypeSpec.Builder toInterface(TypeElement comp){
        TypeSpec.Builder inter = TypeSpec.interfaceBuilder(interfaceName(comp))
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("Interface for $L", comp.getQualifiedName().toString());

        if(comp.getEnclosingElement() instanceof TypeElement){
            inter.addModifiers(Modifier.STATIC);
        }

        imports.put(interfaceName(comp), getImports(comp));

        ObjectSet<String> preserved = new ObjectSet<>();
        for(ExecutableElement m : methods(comp).select(me -> !isConstructor(me))){
            MethodTree tree = treeUtils.getTree(m);
            methodBlocks.put(descString(m), tree.getBody().toString()
                .replaceAll("this\\.<(.*)>self\\(\\)", "this")
                .replaceAll("self\\(\\)(?!\\s+instanceof)", "this")
                .replaceAll(" yield ", "")
                .replaceAll("\\/\\*missing\\*\\/", "var")
            );

            if(is(m, Modifier.PRIVATE, Modifier.STATIC)) continue;

            String name = m.getSimpleName().toString();
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

        for(ExecutableElement cons : methods(comp).select(this::isConstructor)){
            ObjectMap<ExecutableElement, String> map = findConstructorMap(interfaceName(comp), cons.getParameters());

            MethodTree tree = treeUtils.getTree(cons);
            map.put(cons, tree.getBody().toString()
                .replaceAll("this\\.<(.*)>self\\(\\)", "this")
                .replaceAll("self\\(\\)(?!\\s+instanceof)", "this")
                .replaceAll(" yield ", "")
                .replaceAll("\\/\\*missing\\*\\/", "var")
            );
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

    Entry<TypeSpec.Builder, Seq<String>> toClass(TypeElement base, Seq<TypeElement> value){
        StringBuilder n = new StringBuilder();
        for(TypeElement t : value){
            String raw = t.getSimpleName().toString();
            raw = raw.substring(0, raw.length() - 1);

            if(raw.endsWith("Build")){
                raw = raw.substring(0, raw.length() - 5);
            }

            n.append(raw);
        }
        n.append(base.getSimpleName().toString());
        String name = n.toString();

        TypeSpec.Builder type = TypeSpec.classBuilder(name).addModifiers(Modifier.PUBLIC);
        type.superclass(cName(base));
        value.each(t -> type.addSuperinterface(cName(t)));

        for(TypeElement inter : value){
            TypeElement comp;
            if(inter.getEnclosingElement() instanceof TypeElement){
                comp = elementUtils.getTypeElement(docs(inter).split("\\s+")[3]);
            }else{
                comp = comps.find(t -> t.getQualifiedName().toString().equals(
                    docs(inter).split("\\s+")[3]
                ));
            }

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

            for(ExecutableElement m : methods(comp).select(me ->
                !is(me, Modifier.PRIVATE, Modifier.STATIC) &&
                annotation(me, Override.class) == null &&
                !isConstructor(me)
            )){
                String code = methodBlocks.get(descString(m));
                type.addMethod(
                    MethodSpec.overriding(m)
                        .addCode(code.substring(1, code.length() - 1).trim().replace("\n    ", ""))
                    .build()
                );
            }

            for(ExecutableElement m : methods(comp).select(me ->
                is(me, Modifier.PRIVATE, Modifier.STATIC) &&
                annotation(me, Override.class) == null &&
                !isConstructor(me)
            )){
                String code = methodBlocks.get(descString(m));
                type.addMethod(
                    MethodSpec.methodBuilder(m.getSimpleName().toString())
                        .addTypeVariables(Seq.with(m.getTypeParameters()).map(TypeVariableName::get))
                        .addExceptions(Seq.with(m.getThrownTypes()).map(TypeName::get))
                        .addModifiers(m.getModifiers())
                        .addParameters(Seq.with(m.getParameters()).map(ParameterSpec::get))
                        .returns(TypeName.get(m.getReturnType()))
                        .addCode(code.substring(1, code.length() - 1).trim().replace("\n    ", ""))
                    .build()
                );
            }
        }

        ObjectMap<Seq<? extends VariableElement>, ObjectMap<ExecutableElement, String>> constructors = new ObjectMap<>();
        for(String ctype : constructorBlocks.keys().toSeq()){
            if(!value.contains(t -> t.getSimpleName().toString().equals(ctype))) continue;

            ObjectMap<Seq<? extends VariableElement>, ObjectMap<ExecutableElement, String>> map = constructorBlocks.get(ctype);
            for(Entry<Seq<? extends VariableElement>, ObjectMap<ExecutableElement, String>> entry : map){
                Seq<? extends VariableElement> actualKey = entry.key;
                for(Seq<? extends VariableElement> key : constructors.keys()){
                    try{
                        boolean same = true;
                        for(int i = 0; i < key.size; i++){
                            if(same && !typeUtils.isSameType(
                                key.get(i).asType(),
                                entry.key.get(i).asType()
                            )){
                                same = false;
                            }
                        }

                        if(same){
                            actualKey = key;
                            break;
                        }
                    }catch(IndexOutOfBoundsException e){}
                }

                constructors.get(actualKey, ObjectMap::new).putAll(entry.value);
            }
        }

        for(Entry<Seq<? extends VariableElement>, ObjectMap<ExecutableElement, String>> entry : constructors){
            OrderedSet<Modifier> modifiers = new OrderedSet<>();
            for(Entry<ExecutableElement, String> method : entry.value){
                modifiers.addAll(method.key.getModifiers().toArray(new Modifier[0]));
            }

            boolean err = false;
            if(
                (modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.PROTECTED)) ||
                (modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.PRIVATE)) ||
                (modifiers.contains(Modifier.PROTECTED) && modifiers.contains(Modifier.PRIVATE))
            ){
                err = true;
            }

            if(err) throw new IllegalStateException("Inconsistent visibility modifiers in constructor with params: " + entry.key.toString());
            MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(modifiers)
                .addParameters(entry.key.map(ParameterSpec::get));

            StringBuilder params = new StringBuilder();
            for(int i = 0; i < entry.key.size; i++){
                params.append(entry.key.get(i));
                if(i < entry.key.size - 1){
                    params.append(", ");
                }
            }

            constructor.addStatement("super(" + params.toString() + ")");

            for(Entry<ExecutableElement, String> method : entry.value){
                String label = method.key.getEnclosingElement().getSimpleName().toString().toLowerCase();
                label = label.substring(0, label.length() - 4);

                String code = method.value
                .replace("return;", "break " + label + ";");
                code = code.substring(1, code.length() - 1).trim().replace("\n    ", "\n");

                Seq<String> split = Seq.with(code.split("\n"));
                if(split.first().startsWith("super(")) split.remove(0);

                constructor
                    .addCode(lnew())
                    .beginControlFlow("$L:", label)
                    .addCode(split.toString("\n"))
                    .addCode(lnew())
                .endControlFlow();
            }

            type.addMethod(constructor.build());
        }
        

        ObjectMap<ExecutableElement, Seq<ExecutableElement>> appending = new ObjectMap<>();
        Seq<String> imports = new Seq<>();
        for(TypeElement inter : value){
            appending.putAll(getAppendedMethods(base, inter));
            imports.addAll(this.imports.get(inter.getSimpleName().toString(), Seq::with));
        }

        for(ExecutableElement appended : appending.keys().toSeq()){
            Seq<ExecutableElement> appenders = appending.get(appended);
            boolean replace = false;

            if(replace = appenders.contains(app -> annotation(app, Replace.class) != null)){
                appenders = appenders.select(app -> annotation(app, Replace.class) != null);
            }

            appenders.sort(m -> {
                MethodPriority p = annotation(m, MethodPriority.class);
                return p == null ? 0 : p.value();
            });

            Seq<ExecutableElement>[] tmp = new Seq[]{appenders};
            boolean[] change = {false};

            boolean returns = appended.getReturnType().getKind() != TypeKind.VOID;
            MethodSpec.Builder method = MethodSpec.methodBuilder(appended.getSimpleName().toString())
                .addAnnotation(cName(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .addParameters(Seq.with(appended.getParameters()).map(p -> {
                    String pname = p.getSimpleName().toString();

                    try{
                        Integer.parseInt(pname.substring(3, pname.length()));

                        change[0] = true;
                        VariableElement var = Seq.with(tmp[0].first().getParameters()).find(v ->
                            typeUtils.isSameType(v.asType(), p.asType()) &&
                            v.getModifiers().equals(p.getModifiers())
                        );
                        return ParameterSpec.get(var);
                    }catch(NumberFormatException e){
                        return ParameterSpec.get(p);
                    }
                }))
                .returns(TypeName.get(appended.getReturnType()))
                .addTypeVariables(Seq.with(appended.getTypeParameters()).map(TypeVariableName::get))
                .addExceptions(Seq.with(appended.getThrownTypes()).map(TypeName::get))
                .varargs(appended.isVarArgs());

            StringBuilder params = new StringBuilder();
            Seq<Object> args = Seq.with(appended.getSimpleName().toString());

            List<? extends VariableElement> parameters = appended.getParameters();
            for(int i = 0; i < parameters.size(); i++){
                VariableElement var = parameters.get(i);

                params.append("$L");
                if(i < parameters.size() - 1) params.append(", ");
                if(change[0]){
                    VariableElement v = Seq.with(tmp[0].first().getParameters()).find(v2 ->
                        typeUtils.isSameType(v2.asType(), var.asType()) &&
                        v2.getModifiers().equals(var.getModifiers())
                    );
                    args.add(v.getSimpleName().toString());
                }else{
                    args.add(var.getSimpleName().toString());
                }
            }

            boolean superCalled = false;
            boolean first = true;
            if(!returns){
                for(int i = 0; i < appenders.size; i++){
                    ExecutableElement app = appenders.get(i);

                    MethodPriority priority = annotation(app, MethodPriority.class);
                    if(
                        !replace &&
                        !superCalled &&
                        (
                            priority != null
                            ?   priority.value() >= 0
                            :   true
                        )
                    ){
                        method.addStatement("super.$L(" + params.toString() + ")", args.toArray());
                        superCalled = true;
                        first = false;
                    }

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
                    label = label.substring(0, label.length() - 4);
                    ExecutableElement up = method(comp, app.getSimpleName().toString(), app.getReturnType(), app.getParameters());

                    String code = methodBlocks.get(descString(up))
                    .replace("return;", "break " + label + ";");
                    code = code.substring(1, code.length() - 1).trim().replace("\n    ", "\n");

                    Seq<String> arguments = new Seq<>();

                    Pattern fixer = Pattern.compile("\\\"\\$.");
                    String fixed = new String(code);

                    Matcher matcher = fixer.matcher(code);
                    while(matcher.find()){
                        String snip = matcher.group();
                        fixed = fixed.replace(snip, "$L");
                        arguments.add(snip);
                    }

                    if(first){
                        first = false;
                    }else{
                        method.addCode(lnew());
                    }

                    method
                        .beginControlFlow("$L:", label)
                        .addCode(fixed, arguments.toArray(Object.class))
                        .addCode(lnew())
                    .endControlFlow();

                    if(!replace && !superCalled && i == appenders.size - 1){
                        method.addCode(lnew());
                        method.addStatement("super.$L(" + params.toString() + ")", args.toArray());
                    }
                }
            }else{
                if(appenders.size > 1){
                    throw new IllegalStateException("Multiple non-void return type methods appending: " + appended);
                }else{
                    ExecutableElement app = appenders.first();

                    String doc = docs(app.getEnclosingElement());
                    TypeElement comp;
                    if(!doc.isEmpty()){
                        comp = comps.find(t -> t.getQualifiedName().toString().equals(
                            doc.split("\\s+")[3]
                        ));
                    }else{
                        comp = (TypeElement)app.getEnclosingElement();
                    }

                    ExecutableElement up = method(comp, app.getSimpleName().toString(), app.getReturnType(), app.getParameters());

                    String code = methodBlocks.get(descString(up));
                    code = code.substring(1, code.length() - 1).trim().replace("\n    ", "\n");

                    Seq<String> arguments = new Seq<>();

                    Pattern fixer = Pattern.compile("\\\"\\$.");
                    String fixed = new String(code);

                    Matcher matcher = fixer.matcher(code);
                    while(matcher.find()){
                        String snip = matcher.group();
                        fixed = fixed.replace(snip, "$L");
                        arguments.add(snip);
                    }

                    method
                        .addCode(fixed, arguments.toArray(Object.class))
                        .addCode(lnew());
                }
            }

            type.addMethod(method.build());
        }

        return new Entry<TypeSpec.Builder, Seq<String>>() {{
            key = type;
            value = imports;
        }};
    }

    ObjectMap<ExecutableElement, String> findConstructorMap(String comp, List<? extends VariableElement> list){
        ObjectMap<Seq<? extends VariableElement>, ObjectMap<ExecutableElement, String>> blocks = constructorBlocks.get(comp, ObjectMap::new);
        Seq<? extends VariableElement> params = blocks.keys().toSeq().find(l -> {
            try{
                boolean same = true;
                for(int i = 0; i < l.size; i++){
                    if(same && !typeUtils.isSameType(
                        l.get(i).asType(),
                        list.get(i).asType()
                    )){
                        same = false;
                    }
                }

                return same;
            }catch(IndexOutOfBoundsException e){
                return false;
            }
        });

        if(params == null){
            params = Seq.with(list);
            blocks.put(params, new ObjectMap<>());
        }

        return blocks.get(params);
    }

    String interfaceName(TypeElement type){
        String name = type.getSimpleName().toString();
        if(!name.endsWith("Comp")){
            throw new IllegalStateException("All types annotated with @MergeComp must have 'Comp' as the name's suffix");
        }

        return name.substring(0, name.length() - 4) + "c";
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
