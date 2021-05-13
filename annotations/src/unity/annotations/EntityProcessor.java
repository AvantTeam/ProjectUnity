package unity.annotations;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.annotations.TypeIOResolver.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

import com.squareup.javapoet.*;
import com.sun.source.tree.*;

import static javax.lang.model.type.TypeKind.*;

/**
 * @author Anuke
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.EntityComponent",
    "unity.annotations.Annotations.EntityBaseComponent",
    "unity.annotations.Annotations.EntityDef",
    "unity.annotations.Annotations.EntityPoint"
})
public class EntityProcessor extends BaseProcessor{
    Seq<TypeElement> comps = new Seq<>();
    Seq<TypeElement> baseComps = new Seq<>();
    Seq<Element> pointers = new Seq<>();
    ObjectMap<String, TypeElement> compNames = new ObjectMap<>();
    Seq<TypeSpec.Builder> baseClasses = new Seq<>();
    ObjectMap<TypeElement, ObjectSet<TypeElement>> baseClassDeps = new ObjectMap<>();
    ObjectMap<TypeElement, Seq<TypeElement>> componentDependencies = new ObjectMap<>();
    ObjectMap<TypeElement, ObjectMap<String, Seq<ExecutableElement>>> inserters = new ObjectMap<>();
    Seq<TypeElement> inters = new Seq<>();
    Seq<Element> defs = new Seq<>();
    Seq<EntityDefinition> definitions = new Seq<>();

    StringMap varInitializers = new StringMap();
    StringMap methodBlocks = new StringMap();
    ObjectMap<String, Seq<String>> imports = new ObjectMap<>();
    ObjectMap<TypeElement, String> groups;
    ClassSerializer serializer;

    {
        rounds = 4;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        comps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityComponent.class));
        baseComps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityBaseComponent.class));
        inters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityInterface.class));
        defs.addAll(roundEnv.getElementsAnnotatedWith(EntityDef.class));
        pointers.addAll(roundEnv.getElementsAnnotatedWith(EntityPoint.class));

        for(ExecutableElement e : (Set<ExecutableElement>)roundEnv.getElementsAnnotatedWith(Insert.class)){
            if(!e.getParameters().isEmpty()) throw new IllegalStateException("All @Insert methods must not have parameters");

            Insert ann = annotation(e, Insert.class);
            inserters
                .get(comps.find(c -> simpleName(c).equals(simpleName(e.getEnclosingElement()))), ObjectMap::new)
                .get(ann.value(), Seq::new)
                .add(e);
        }

        if(round == 1){
            serializer = TypeIOResolver.resolve(this);
            groups = ObjectMap.of(
                toComp(Entityc.class), "all",
                toComp(Playerc.class), "player",
                toComp(Bulletc.class), "bullet",
                toComp(Unitc.class), "unit",
                toComp(Buildingc.class), "build",
                toComp(Syncc.class), "sync",
                toComp(Drawc.class), "draw",
                toComp(Firec.class), "fire",
                toComp(Puddlec.class), "puddle"
            );

            for(TypeElement inter : (List<TypeElement>)((PackageElement)elementUtils.getPackageElement("mindustry.gen")).getEnclosedElements()){
                if(
                    simpleName(inter).endsWith("c") &&
                    inter.getKind() == ElementKind.INTERFACE
                ){
                    inters.add(inter);
                }
            }

            for(TypeElement comp : comps){
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
                compNames.put(simpleName(comp), comp);

                Seq<TypeElement> depends = getDependencies(comp);

                EntityComponent compAnno = annotation(comp, EntityComponent.class);
                if(compAnno.write()){
                    TypeSpec.Builder inter = TypeSpec.interfaceBuilder(interfaceName(comp))
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(cName(EntityInterface.class))
                        .addAnnotation(
                            AnnotationSpec.builder(cName(SuppressWarnings.class))
                                .addMember("value", "$S", "all")
                            .build()
                        );

                    for(TypeElement type : depends){
                        inter.addSuperinterface(procName(type, this::interfaceName));
                    }

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

                    write(inter.build(), getImports(comp));

                    if(compAnno.base()){
                        Seq<TypeElement> deps = depends.copy().and(comp);
                        baseClassDeps.get(comp, ObjectSet::new).addAll(deps);

                        if(annotation(comp, EntityDef.class) == null){
                            TypeSpec.Builder base = TypeSpec.classBuilder(baseName(comp)).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

                            for(TypeElement dep : deps){
                                for(VariableElement var : vars(dep).select(v -> !is(v, Modifier.PRIVATE) && !is(v, Modifier.STATIC) && annotation(v, Import.class) == null && annotation(v, ReadOnly.class) == null)){
                                    FieldSpec.Builder field = FieldSpec.builder(tName(var), simpleName(var), Modifier.PUBLIC);

                                    if(is(var, Modifier.TRANSIENT)) field.addModifiers(Modifier.TRANSIENT);
                                    field.addAnnotations(Seq.with(var.getAnnotationMirrors()).map(AnnotationSpec::get));

                                    if(varInitializers.containsKey(descString(var))){
                                        field.initializer(varInitializers.get(descString(var)));
                                    }

                                    base.addField(field.build());
                                }

                                base.addSuperinterface(procName(dep, this::interfaceName));
                            }

                            baseClasses.add(base);
                        }
                    }
                }else if(compAnno.base()){
                    Seq<TypeElement> deps = depends.copy().and(comp);
                    baseClassDeps.get(comp, ObjectSet::new).addAll(deps);
                }
            }
        }else if(round == 2){
            ObjectMap<String, Element> usedNames = new ObjectMap<>();
            ObjectMap<Element, ObjectSet<String>> extraNames = new ObjectMap<>();

            for(Element def : defs){
                EntityDef ann = annotation(def, EntityDef.class);

                Seq<TypeElement> defComps = elements(ann::value)
                    .<TypeElement>as()
                    .map(t -> inters.find(i -> simpleName(i).equals(simpleName(t))))
                    .select(Objects::nonNull)
                    .map(this::toComp);

                if(defComps.isEmpty()) continue;

                ObjectMap<String, Seq<ExecutableElement>> methods = new ObjectMap<>();
                ObjectMap<FieldSpec, VariableElement> specVariables = new ObjectMap<>();
                ObjectSet<String> usedFields = new ObjectSet<>();

                Seq<TypeElement> baseClasses = defComps.select(s -> annotation(s, EntityComponent.class).base());
                if(baseClasses.size > 2){
                    throw new IllegalStateException("No entity may have more than 2 base classes.");
                }

                TypeElement baseClassType = baseClasses.any() ? baseClasses.first() : null;
                TypeName baseClass = baseClasses.any()
                ?   procName(baseClassType, this::baseName)
                :   null;

                boolean typeIsBase = baseClassType != null && annotation(def, EntityComponent.class) != null && annotation(def, EntityComponent.class).base();

                if(def instanceof TypeElement && !simpleName(def).endsWith("Comp")){
                    throw new IllegalStateException("All entity def names must end with 'Comp'");
                }

                String name = def instanceof TypeElement ?
                    simpleName(def).replace("Comp", "") :
                    createName(defComps);

                defComps.addAll(defComps.copy().flatMap(this::getDependencies)).distinct();
                Seq<TypeElement> empty = Seq.with();
                Seq<TypeElement> excludeGroups = Seq.with(defComps)
                    .flatMap(t -> annotation(t, ExcludeGroups.class) != null ? elements(annotation(t, ExcludeGroups.class)::value).as() : empty)
                    .distinct()
                    .map(this::toComp);

                Seq<String> defGroups = groups.values().toSeq().select(val -> {
                    TypeElement type = groups.findKey(val, false);
                    return
                        defComps.contains(type) &&
                        !excludeGroups.contains(type);
                    }
                );

                if(!typeIsBase && baseClass != null && name.equals(baseName(baseClassType))){
                    name += "Entity";
                }

                if(usedNames.containsKey(name)){
                    extraNames.get(usedNames.get(name), ObjectSet::new).add(simpleName(def));
                    continue;
                }

                TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(
                        AnnotationSpec.builder(SuppressWarnings.class)
                            .addMember("value", "$S", "all")
                        .build()
                    );

                builder.addMethod(
                    MethodSpec.methodBuilder("serialize").addModifiers(Modifier.PUBLIC)
                        .addAnnotation(cName(Override.class))
                        .returns(TypeName.BOOLEAN)
                        .addStatement("return " + ann.serialize())
                    .build()
                );

                Seq<VariableElement> syncedFields = new Seq<>();
                Seq<VariableElement> allFields = new Seq<>();
                Seq<FieldSpec> allFieldSpecs = new Seq<>();

                boolean isSync = defComps.contains(s -> simpleName(s).contains("Sync"));

                ObjectMap<String, Seq<ExecutableElement>> ins = new ObjectMap<>();

                for(TypeElement comp : defComps){
                    ObjectMap<String, Seq<ExecutableElement>> insComp = inserters.get(comp, ObjectMap::new);
                    for(String s : insComp.keys()){
                        ins.get(s, Seq::new).addAll(insComp.get(s));
                    }

                    boolean isShadowed = baseClass != null && !typeIsBase && baseClassDeps.get(baseClassType, ObjectSet::new).contains(comp);

                    Seq<VariableElement> fields = vars(comp).select(v -> annotation(v, Import.class) == null);
                    for(VariableElement field : fields){
                        if(!usedFields.add(simpleName(field))){
                            throw new IllegalStateException("Field '" + simpleName(field) + "' of component '" + simpleName(comp) + "' redefines a field in entity '" + simpleName(def) + "'");
                        }

                        FieldSpec.Builder fbuilder = FieldSpec.builder(tName(field), simpleName(field));

                        if(is(field, Modifier.STATIC)){
                            fbuilder.addModifiers(Modifier.STATIC);
                            if(is(field, Modifier.FINAL)) fbuilder.addModifiers(Modifier.FINAL);
                        }

                        if(is(field, Modifier.TRANSIENT)){
                            fbuilder.addModifiers(Modifier.TRANSIENT);
                        }

                        if(varInitializers.containsKey(descString(field))){
                            fbuilder.initializer(varInitializers.get(descString(field)));
                        }

                        if(is(field, Modifier.PRIVATE)){
                            fbuilder.addModifiers(Modifier.PRIVATE);
                        }else{
                            fbuilder.addModifiers(annotation(field, ReadOnly.class) != null ? Modifier.PROTECTED : Modifier.PUBLIC);
                        }

                        fbuilder.addAnnotations(Seq.with(field.getAnnotationMirrors()).map(AnnotationSpec::get));
                        FieldSpec spec = fbuilder.build();

                        boolean isVisible = !is(field, Modifier.STATIC) && !is(field, Modifier.PRIVATE) && annotation(field, ReadOnly.class) == null;

                        if(!isShadowed || !isVisible){
                            builder.addField(spec);
                        }

                        specVariables.put(spec, field);

                        allFieldSpecs.add(spec);
                        allFields.add(field);

                        if(annotation(field, SyncField.class) != null && isSync){
                            if(field.asType().getKind() != FLOAT) throw new IllegalStateException("All SyncFields must be of type float");

                            syncedFields.add(field);
                            builder.addField(FieldSpec.builder(TypeName.FLOAT, simpleName(field) + "_TARGET_").addModifiers(Modifier.TRANSIENT, Modifier.PRIVATE).build());
                            builder.addField(FieldSpec.builder(TypeName.FLOAT, simpleName(field) + "_LAST_").addModifiers(Modifier.TRANSIENT, Modifier.PRIVATE).build());
                        }
                    }

                    for(ExecutableElement elem : methods(comp).select(m -> !isConstructor(m))){
                        methods.get(elem.toString(), Seq::new).add(elem);
                    }
                }

                syncedFields.sortComparing(BaseProcessor::simpleName);

                builder.addMethod(
                    MethodSpec.methodBuilder("toString")
                        .addAnnotation(cName(Override.class))
                        .returns(String.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return $S + $L", name + "#", "id")
                    .build()
                );

                EntityIO io = new EntityIO(simpleName(def), builder, serializer);
                boolean hasIO = ann.genio() && (defComps.contains(s -> simpleName(s).contains("Sync")) || ann.serialize());

                for(Entry<String, Seq<ExecutableElement>> entry : methods){
                    if(entry.value.contains(m -> annotation(m, Replace.class) != null)){
                        if(entry.value.first().getReturnType().getKind() == VOID){
                            entry.value = entry.value.select(m -> annotation(m, Replace.class) != null);
                        }else{
                            if(entry.value.count(m -> annotation(m, Replace.class) != null) > 1){
                                throw new IllegalStateException("Type " + simpleName(def) + " has multiple components replacing non-void method " + entry.key + ".");
                            }
    
                            ExecutableElement base = entry.value.find(m -> annotation(m, Replace.class) != null);
                            entry.value.clear();
                            entry.value.add(base);
                        }
                    }

                    if(entry.value.count(m -> !is(m, Modifier.NATIVE, Modifier.ABSTRACT) && m.getReturnType().getKind() != VOID) > 1){
                        throw new IllegalStateException("Type " + simpleName(def) + " has multiple components implementing non-void method " + entry.key + ".");
                    }

                    entry.value.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

                    ExecutableElement first = entry.value.first();

                    if(annotation(first, InternalImpl.class) != null){
                        continue;
                    }

                    boolean isPrivate = is(first, Modifier.PRIVATE);
                    MethodSpec.Builder mbuilder = MethodSpec.methodBuilder(simpleName(first)).addModifiers(isPrivate ? Modifier.PRIVATE : Modifier.PUBLIC);
                    if(!isPrivate) mbuilder.addAnnotation(cName(Override.class));

                    if(is(first, Modifier.STATIC)) mbuilder.addModifiers(Modifier.STATIC);
                    mbuilder.addTypeVariables(Seq.with(first.getTypeParameters()).map(TypeVariableName::get));
                    mbuilder.returns(TypeName.get(first.getReturnType()));
                    mbuilder.addExceptions(Seq.with(first.getThrownTypes()).map(TypeName::get));

                    for(VariableElement var : first.getParameters()){
                        mbuilder.addParameter(tName(var), simpleName(var));
                    }

                    boolean writeBlock = first.getReturnType().getKind() == VOID && entry.value.size > 1;

                    if((is(entry.value.first(), Modifier.ABSTRACT) || is(entry.value.first(), Modifier.NATIVE)) && entry.value.size == 1 && annotation(entry.value.first(), InternalImpl.class) == null){
                        throw new IllegalStateException(simpleName(entry.value.first().getEnclosingElement()) + "#" + entry.value.first() + " is an abstract method and must be implemented in some component");
                    }

                    Seq<ExecutableElement> inserts = ins.get(entry.key, Seq::new);
                    if(first.getReturnType().getKind() != VOID && !inserts.isEmpty()){
                        throw new IllegalStateException("Method " + entry.key + " is not void, therefore no methods can @Insert to it");
                    }

                    if(simpleName(first).equals("add") || simpleName(first).equals("remove")){
                        Seq<ExecutableElement> bypass = entry.value.select(m -> annotation(m, BypassGroupCheck.class) != null);
                        entry.value.removeAll(bypass);

                        boolean firstc = append(mbuilder, bypass, inserts, writeBlock, first);
                        if(!firstc){
                            mbuilder.addCode(lnew());
                        }

                        mbuilder.addStatement("if($Ladded) return", simpleName(first).equals("add") ? "" : "!");

                        for(String group : defGroups){
                            mbuilder.addStatement("Groups.$L.$L(this)", group, simpleName(first));
                        }
                        mbuilder.addCode(lnew());
                    }

                    Seq<ExecutableElement> noComp = inserts.select(e -> typeUtils.isSameType(
                        elements(annotation(e, Insert.class)::block).first().asType(),
                        elementUtils.getTypeElement("java.lang.Void").asType()
                    ));

                    Seq<ExecutableElement> noCompBefore = noComp.select(e -> !annotation(e, Insert.class).after());
                    noCompBefore.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

                    Seq<ExecutableElement> noCompAfter = noComp.select(e -> annotation(e, Insert.class).after());
                    noCompAfter.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

                    inserts = inserts.select(e -> !noComp.contains(e));

                    for(ExecutableElement e : noCompBefore){
                        mbuilder.addStatement("this.$L()", simpleName(e));
                    }

                    if(hasIO){
                        if(simpleName(first).equals("read") || simpleName(first).equals("write")){
                            io.write(mbuilder, simpleName(first).equals("write"), allFields);
                        }

                        if(simpleName(first).equals("readSync") || simpleName(first).equals("writeSync")){
                            io.writeSync(mbuilder, simpleName(first).equals("writeSync"), syncedFields, allFields);
                        }

                        if(simpleName(first).equals("readSyncManual") || simpleName(first).equals("writeSyncManual")){
                            io.writeSyncManual(mbuilder, simpleName(first).equals("writeSyncManual"), syncedFields);
                        }

                        if(simpleName(first).equals("interpolate")){
                            io.writeInterpolate(mbuilder, syncedFields);
                        }

                        if(simpleName(first).equals("snapSync")){
                            mbuilder.addStatement("updateSpacing = 16");
                            mbuilder.addStatement("lastUpdated = $T.millis()", Time.class);
                            for(VariableElement field : syncedFields){
                                mbuilder.addStatement("$L = $L", simpleName(field) + "_LAST_", simpleName(field) + "_TARGET_");
                                mbuilder.addStatement("$L = $L", simpleName(field), simpleName(field) + "_TARGET_");
                            }
                        }

                        if(simpleName(first).equals("snapInterpolation")){
                            mbuilder.addStatement("updateSpacing = 16");
                            mbuilder.addStatement("lastUpdated = $T.millis()", Time.class);
                            for(VariableElement field : syncedFields){
                                mbuilder.addStatement("$L = $L", simpleName(field) + "_LAST_", simpleName(field));
                                mbuilder.addStatement("$L = $L", simpleName(field) + "_TARGET_", simpleName(field));
                            }
                        }
                    }

                    boolean firstc = append(mbuilder, entry.value, inserts, writeBlock, first);

                    if(!firstc && !noCompAfter.isEmpty()) mbuilder.addCode(lnew());
                    for(ExecutableElement e : noCompAfter){
                        mbuilder.addStatement("this.$L()", simpleName(e));
                    }

                    if(simpleName(first).equals("remove") && ann.pooled()){
                        mbuilder.addStatement("mindustry.gen.Groups.queueFree(($T)this)", Poolable.class);
                    }

                    builder.addMethod(mbuilder.build());
                }

                if(ann.pooled()){
                    builder.addSuperinterface(Poolable.class);

                    MethodSpec.Builder resetBuilder = MethodSpec.methodBuilder("reset")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(cName(Override.class));

                    for(FieldSpec spec : allFieldSpecs){
                        VariableElement variable = specVariables.get(spec);
                        if(variable != null && is(variable, Modifier.STATIC, Modifier.FINAL)) continue;
                        String desc = descString(variable);

                        if(spec.type.isPrimitive()){
                            resetBuilder.addStatement("$L = $L", spec.name, variable != null && varInitializers.containsKey(desc) ? varInitializers.get(desc) : getDefault(spec.type.toString()));
                        }else{
                            if(!varInitializers.containsKey(desc)){
                                resetBuilder.addStatement("$L = null", spec.name);
                            }
                        }
                    }

                    builder.addMethod(resetBuilder.build());
                }

                builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PROTECTED).build());

                builder.addMethod(
                    MethodSpec.methodBuilder("create").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ClassName.get(packageName, name))
                        .addStatement(ann.pooled() ? "return arc.util.pooling.Pools.obtain($L.class, " + name + "::new)" : "return new $L()", name)
                    .build()
                );

                definitions.add(new EntityDefinition(packageName + "." + name, builder, def, typeIsBase ? null : baseClass, defComps, defGroups, allFieldSpecs));
            }
        }else if(round == 3){
            TypeSpec.Builder map = TypeSpec.classBuilder("UnityEntityMapping").addModifiers(Modifier.PUBLIC)
                .addField(
                    FieldSpec.builder(ParameterizedTypeName.get(
                        cName(ObjectIntMap.class),
                        ParameterizedTypeName.get(
                            cName(Class.class),
                            WildcardTypeName.subtypeOf(cName(Entityc.class))
                        )
                    ), "ids")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE)
                        .initializer("new $T<>()", cName(ObjectIntMap.class))
                    .build()
                )
                .addField(
                    FieldSpec.builder(TypeName.INT, "last")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE)
                    .build()
                )
                .addMethod(
                    MethodSpec.methodBuilder("register")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addTypeVariable(tvName("T", cName(Entityc.class)))
                        .addParameter(
                            ParameterizedTypeName.get(cName(Class.class), tvName("T")),
                            "type"
                        )
                        .addParameter(
                            ParameterizedTypeName.get(cName(Prov.class), tvName("T")),
                            "prov"
                        )
                        .beginControlFlow("synchronized($T.class)", ClassName.get(packageName, "UnityEntityMapping"))
                            .addStatement("if(ids.containsKey(type) || $T.nameMap.containsKey(type.getSimpleName())) return", cName(EntityMapping.class))
                            .addCode(lnew())
                            .beginControlFlow("for(; last < $T.idMap.length; last++)", cName(EntityMapping.class))
                                .beginControlFlow("if($T.idMap[last] == null)", cName(EntityMapping.class))
                                    .addStatement("$T.idMap[last] = prov", cName(EntityMapping.class))
                                    .addStatement("ids.put(type, last)")
                                    .addCode(lnew())
                                    .addStatement("$T.nameMap.put(type.getSimpleName(), prov)", cName(EntityMapping.class))
                                    .addStatement("$T.nameMap.put($T.camelToKebab(type.getSimpleName()), prov)", cName(EntityMapping.class), cName(Strings.class))
                                    .addCode(lnew())
                                    .addStatement("break")
                                .endControlFlow()
                            .endControlFlow()
                        .endControlFlow()
                    .build()
                )
                .addMethod(
                    MethodSpec.methodBuilder("register")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addTypeVariable(tvName("T", cName(Entityc.class)))
                        .addParameter(cName(String.class), "name")
                        .addParameter(
                            ParameterizedTypeName.get(cName(Class.class), tvName("T")),
                            "type"
                        )
                        .addParameter(
                            ParameterizedTypeName.get(cName(Prov.class), tvName("T")),
                            "prov"
                        )
                        .addStatement("register(type, prov)")
                        .addStatement("$T.nameMap.put(name.replaceFirst($S, $S), prov)", cName(EntityMapping.class), "unity-", "")
                    .build()
                )
                .addMethod(
                    MethodSpec.methodBuilder("register")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addTypeVariable(tvName("T", cName(Unit.class)))
                        .addParameter(cName(UnitType.class), "unit")
                        .addParameter(
                            ParameterizedTypeName.get(cName(Class.class), tvName("T")),
                            "type"
                        )
                        .addParameter(
                            ParameterizedTypeName.get(cName(Prov.class), tvName("T")),
                            "prov"
                        )
                        .addStatement("register(unit.name, type, prov)")
                        .addStatement("unit.constructor = prov")
                    .build()
                )
                .addMethod(
                    MethodSpec.methodBuilder("classId")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addTypeVariable(tvName("T", cName(Entityc.class)))
                        .addParameter(
                            ParameterizedTypeName.get(cName(Class.class), tvName("T")),
                            "type"
                        )
                        .returns(TypeName.INT)
                        .addStatement("return ids.get(type, -1)")
                    .build()
                );

            MethodSpec.Builder init = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID);

            for(EntityDefinition def : definitions){
                ClassName type = ClassName.get(packageName, def.name);

                def.builder.addMethod(
                    MethodSpec.methodBuilder("classId").addModifiers(Modifier.PUBLIC)
                        .addAnnotation(cName(Override.class))
                        .returns(TypeName.INT)
                        .addStatement("return $T.classId($T.class)", ClassName.get(packageName, "UnityEntityMapping"), type)
                    .build()
                );

                if(def.naming instanceof VariableElement){
                    TypeMirror up = def.naming.getEnclosingElement().asType();
                    String c = simpleName(def.naming);
                    init.addStatement("register($T.$L, $T.class, $T::create)", TypeName.get(up), c, type, type);
                }else{
                    init.addStatement("register($T.class, $T::create)", type, type);
                }
            }

            ObjectSet<String> usedNames = new ObjectSet<>();
            for(Element e : pointers){
                EntityPoint point = annotation(e, EntityPoint.class);
                boolean isUnit = e instanceof VariableElement;

                TypeElement type = (TypeElement)toEl(isUnit ? elements(point::value).first().asType() : e.asType());
                ExecutableElement create = method(type, "create", type.asType(), Collections.emptyList());
                String constructor = create == null ? "new" : "create";

                if(isUnit){
                    TypeMirror up = e.getEnclosingElement().asType();
                    String c = simpleName(e);
                    init.addStatement("register($T.$L, $T.class, $T::$L)", TypeName.get(up), c, cName(type), cName(type), constructor);

                    usedNames.add(simpleName(type));
                }else if(!usedNames.contains(simpleName(type))){
                    init.addStatement("register($T.class, $T::$L)", cName(type), cName(type), constructor);

                    usedNames.add(simpleName(type));
                }
            }

            write(map
                .addMethod(init.build())
                .build()
            );
        }else if(round == 4){
            for(TypeSpec.Builder b : baseClasses){
                TypeSpec spec = b.build();
                write(spec, imports.get(spec.name));
            }

            for(EntityDefinition def : definitions){
                ObjectSet<String> methodNames = def.components.flatMap(type -> methods(type).map(BaseProcessor::simpleString)).asSet();

                if(def.extend != null){
                    def.builder.superclass(def.extend);
                }

                for(TypeElement comp : def.components){
                    TypeElement inter = inters.find(i -> simpleName(i).equals(interfaceName(comp)));
                    if(inter == null){
                        throw new IllegalStateException("Failed to generate interface for " + simpleName(comp));
                    }

                    def.builder.addSuperinterface(cName(inter));

                    for(ExecutableElement method : methods(inter)){
                        String var = simpleName(method);
                        FieldSpec field = Seq.with(def.fieldSpecs).find(f -> f.name.equals(var));

                        if(field == null || methodNames.contains(simpleString(method))) continue;

                        if(method.getReturnType().getKind() != VOID){
                            def.builder.addMethod(
                                MethodSpec.methodBuilder(var).addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.get(method.getReturnType()))
                                    .addAnnotation(cName(Override.class))
                                    .addStatement("return $L", var)
                                .build()
                            );
                        }

                        if(method.getReturnType().getKind() == VOID && !Seq.with(field.annotations).contains(f -> f.type.toString().equals("@unity.annotations.Annotations.ReadOnly"))){
                            def.builder.addMethod(
                                MethodSpec.methodBuilder(var).addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.VOID)
                                    .addAnnotation(cName(Override.class))
                                    .addParameter(field.type, var)
                                    .addStatement("this.$L = $L", var, var)
                                .build()
                            );
                        }
                    }
                }

                write(def.builder.build(), def.components.flatMap(comp -> imports.get(interfaceName(comp))));
            }
        }
    }

    boolean append(MethodSpec.Builder mbuilder, Seq<ExecutableElement> values, Seq<ExecutableElement> inserts, boolean writeBlock, ExecutableElement first){
        boolean firstc = true;
        for(ExecutableElement elem : values){
            String descStr = descString(elem);
            String blockName = simpleName(elem.getEnclosingElement()).toLowerCase().replace("comp", "");

            Seq<ExecutableElement> insertComp = inserts.select(e ->
                simpleName(toComp((TypeElement)elements(annotation(e, Insert.class)::block).first()))
                    .toLowerCase().replace("comp", "")
                    .equals(blockName)
            );

            if(is(elem, Modifier.ABSTRACT) || is(elem, Modifier.NATIVE) || (!methodBlocks.containsKey(descStr) && insertComp.isEmpty())) continue;
            if(!firstc) mbuilder.addCode(lnew());
            firstc = false;

            Seq<ExecutableElement> compBefore = insertComp.select(e -> !annotation(e, Insert.class).after());
            compBefore.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

            Seq<ExecutableElement> compAfter = insertComp.select(e -> annotation(e, Insert.class).after());
            compAfter.sort(Structs.comps(Structs.comparingFloat(m -> annotation(m, MethodPriority.class) != null ? annotation(m, MethodPriority.class).value() : 0), Structs.comparing(BaseProcessor::simpleName)));

            String str = methodBlocks.get(descStr);
            str = str.substring(1, str.length() - 1).trim().replace("\n    ", "\n").trim();
            str += '\n';

            for(ExecutableElement e : compBefore){
                mbuilder.addStatement("this.$L()", simpleName(e));
            }

            if(writeBlock){
                str = str.replace("return;", "break " + blockName + ";");

                if(str
                    .replaceAll("\\s+", "")
                    .replace("\n", "")
                    .isEmpty()
                ) continue;

                mbuilder.beginControlFlow("$L:", blockName);
            }

            mbuilder.addCode(str);

            if(writeBlock) mbuilder.endControlFlow();

            for(ExecutableElement e : compAfter){
                mbuilder.addStatement("this.$L()", simpleName(e));
            }
        }

        return firstc;
    }

    Seq<TypeElement> getDependencies(TypeElement component){
        if(!componentDependencies.containsKey(component)){
            ObjectSet<TypeElement> out = new ObjectSet<>();

            out.addAll(Seq.with(component.getInterfaces())
                .map(BaseProcessor::toEl)
                .<TypeElement>as()
                .map(t -> inters.find(i -> simpleName(t).equals(simpleName(i))))
                .select(t -> t != null)
                .map(this::toComp)
            );

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

    TypeName procName(TypeElement comp, Func<TypeElement, String> name){
        return ClassName.get(
            comp.getEnclosingElement().toString().contains("fetched") ? "mindustry.gen" : packageName,
            name.get(comp)
        );
    }

    TypeElement toComp(TypeElement inter){
        String name = simpleName(inter);
        if(!name.endsWith("c")) return null;

        String compName = name.substring(0, name.length() - 1) + "Comp";
        return comps.find(t -> simpleName(t).equals(compName));
    }

    TypeElement toComp(Class<?> inter){
        return toComp(elementUtils.getTypeElement(inter.getCanonicalName()));
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

    String createName(Seq<TypeElement> comps){
        Seq<TypeElement> rev = comps.copy();
        rev.reverse();

        return rev.toString("", s -> simpleName(s).replace("Comp", ""));
    }

    Seq<String> getImports(Element e){
        return Seq.with(treeUtils.getPath(e).getCompilationUnit().getImports()).map(Object::toString);
    }

    class EntityDefinition{
        final Seq<String> groups;
        final Seq<TypeElement> components;
        final Seq<FieldSpec> fieldSpecs;
        final TypeSpec.Builder builder;
        final Element naming;
        final String name;
        final TypeName extend;

        public EntityDefinition(String name, TypeSpec.Builder builder, Element naming, TypeName extend, Seq<TypeElement> components, Seq<String> groups, Seq<FieldSpec> fieldSpec){
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
            return "Definition{" +
            "groups=" + groups +
            "components=" + components +
            ", base=" + naming +
            '}';
        }
    }

    static class EntityIO{
        final String name;
        final TypeSpec.Builder type;
        final ClassSerializer serializer;

        MethodSpec.Builder method;
        boolean write;

        EntityIO(String name, TypeSpec.Builder type, ClassSerializer serializer){
            this.name = name;
            this.type = type;
            this.serializer = serializer;
        }

        Seq<VariableElement> sel(Seq<VariableElement> fields){
            return fields.select(f ->
                !f.getModifiers().contains(Modifier.TRANSIENT) &&
                !f.getModifiers().contains(Modifier.STATIC) &&
                !f.getModifiers().contains(Modifier.FINAL)
            );
        }

        void write(MethodSpec.Builder method, boolean write, Seq<VariableElement> fields){
            this.method = method;
            this.write = write;

            for(VariableElement e : sel(fields)){
                io(e.asType().toString(), "this." + simpleName(e) + (write ? "" : " = "));
            }
        }

        void writeSync(MethodSpec.Builder method, boolean write, Seq<VariableElement> syncFields, Seq<VariableElement> allFields){
            this.method = method;
            this.write = write;

            if(write){
                for(VariableElement e : sel(allFields)){
                    io(e.asType().toString(), "this." + simpleName(e));
                }
            }else{
                st("if(lastUpdated != 0) updateSpacing = $T.timeSinceMillis(lastUpdated)", Time.class);
                st("lastUpdated = $T.millis()", Time.class);
                st("boolean islocal = isLocal()");

                for(VariableElement e : sel(allFields)){
                    boolean sf = annotation(e, SyncField.class) != null;
                    boolean sl = annotation(e, SyncLocal.class) != null;

                    if(sl) cont("if(!islocal)");

                    if(sf){
                        st(simpleName(e) + "_LAST_" + " = this." + simpleName(e));
                    }

                    io(e.asType().toString(), "this." + (sf ? simpleName(e) + "_TARGET_" : simpleName(e)) + " = ");

                    if(sl){
                        ncont("else" );

                        io(e.asType().toString(), "");

                        if(sf){
                            st(simpleName(e) + "_LAST_" + " = this." + simpleName(e));
                            st(simpleName(e) + "_TARGET_" + " = this." + simpleName(e));
                        }

                        econt();
                    }
                }

                st("afterSync()");
            }
        }

        void writeSyncManual(MethodSpec.Builder method, boolean write, Seq<VariableElement> syncFields) throws Exception{
            this.method = method;
            this.write = write;

            if(write){
                for(VariableElement field : syncFields){
                    st("buffer.put(this.$L)", simpleName(field));
                }
            }else{
                st("if(lastUpdated != 0) updateSpacing = $T.timeSinceMillis(lastUpdated)", Time.class);
                st("lastUpdated = $T.millis()", Time.class);

                for(VariableElement field : syncFields){
                    st("this.$L = this.$L", simpleName(field) + "_LAST_", simpleName(field));
                    st("this.$L = buffer.get()", simpleName(field) + "_TARGET_");
                }
            }
        }

        void writeInterpolate(MethodSpec.Builder method, Seq<VariableElement> fields) throws Exception{
            this.method = method;

            cont("if(lastUpdated != 0 && updateSpacing != 0)");

            st("float timeSinceUpdate = Time.timeSinceMillis(lastUpdated)");
            st("float alpha = Math.min(timeSinceUpdate / updateSpacing, 2f)");

            for(VariableElement field : fields){
                String name = simpleName(field);
                String targetName = name + "_TARGET_";
                String lastName = name + "_LAST_";
                st("$L = $L($T.$L($L, $L, alpha))", name, annotation(field, SyncField.class).clamped() ? "arc.math.Mathf.clamp" : "", cName(Mathf.class), annotation(field, SyncField.class).value() ? "lerp" : "slerp", lastName, targetName);
            }

            ncont("else if(lastUpdated != 0)");

            for(VariableElement field : fields){
                st("$L = $L", simpleName(field), simpleName(field) + "_TARGET_");
            }

            econt();
        }

        void io(String type, String field){
            type = type.replace("mindustry.gen.", "").replace("unity.gen.", "");

            if(isPrimitive(type)){
                s(type.equals("boolean") ? "bool" : type.charAt(0) + "", field);
            }else if(instanceOf(type, "mindustry.ctype.Content")){
                if(write){
                    s("s", field + ".id");
                }else{
                    st(field + "$T.content.getByID($T.$L, read.s())", cName(Vars.class), cName(ContentType.class), simpleName(type).toLowerCase().replace("type", ""));
                }
            }else if(serializer.writers.containsKey(type) && write){
                st("$L(write, $L)", serializer.writers.get(type), field);
            }else if(serializer.mutatorReaders.containsKey(type) && !write && !field.replace(" = ", "").contains(" ") && !field.isEmpty()){
                st("$L$L(read, $L)", field, serializer.mutatorReaders.get(type), field.replace(" = ", ""));
            }else if(serializer.readers.containsKey(type) && !write){
                st("$L$L(read)", field, serializer.readers.get(type));
            }else if(type.endsWith("[]")){
                String rawType = type.substring(0, type.length() - 2);
    
                if(write){
                    s("i", field + ".length");
                    cont("for(int INDEX = 0; INDEX < $L.length; INDEX ++)", field);
                    io(rawType, field + "[INDEX]");
                }else{
                    String fieldName = field.replace(" = ", "").replace("this.", "");
                    String lenf = fieldName + "_LENGTH";
                    s("i", "int " + lenf + " = ");
                    if(!field.isEmpty()){
                        st("$Lnew $L[$L]", field, type.replace("[]", ""), lenf);
                    }
                    cont("for(int INDEX = 0; INDEX < $L; INDEX ++)", lenf);
                    io(rawType, field.replace(" = ", "[INDEX] = "));
                }
    
                econt();
            }else if(type.startsWith("arc.struct") && type.contains("<")){ //it's some type of data structure
                String struct = type.substring(0, type.indexOf("<"));
                String generic = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
    
                if(struct.equals("arc.struct.Queue") || struct.equals("arc.struct.Seq")){
                    if(write){
                        s("i", field + ".size");
                        cont("for(int INDEX = 0; INDEX < $L.size; INDEX ++)", field);
                        io(generic, field + ".get(INDEX)");
                    }else{
                        String fieldName = field.replace(" = ", "").replace("this.", "");
                        String lenf = fieldName + "_LENGTH";
                        s("i", "int " + lenf + " = ");
                        if(!field.isEmpty()){
                            st("$L.clear()", field.replace(" = ", ""));
                        }
                        cont("for(int INDEX = 0; INDEX < $L; INDEX ++)", lenf);
                        io(generic, field.replace(" = ", "_ITEM = ").replace("this.", generic + " "));
                        if(!field.isEmpty()){
                            String temp = field.replace(" = ", "_ITEM").replace("this.", "");
                            st("if($L != null) $L.add($L)", temp, field.replace(" = ", ""), temp);
                        }
                    }
    
                    econt();
                }else{
                    Log.warn("Missing serialization code for collection '@' in '@'", type, name);
                }
            }else{
                Log.warn("Missing serialization code for type '@' in '@'", type, name);
            }
        }

        void cont(String text, Object... fmt){
            method.beginControlFlow(text, fmt);
        }
    
        void econt(){
            method.endControlFlow();
        }
    
        void ncont(String text, Object... fmt){
            method.nextControlFlow(text, fmt);
        }
    
        void st(String text, Object... args){
            method.addStatement(text, args);
        }
    
        void s(String type, String field){
            if(write){
                method.addStatement("write.$L($L)", type, field);
            }else{
                method.addStatement("$Lread.$L()", field, type);
            }
        }
    }
}
