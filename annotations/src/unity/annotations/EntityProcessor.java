package unity.annotations;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

import unity.annotations.Annotations.*;

import com.squareup.javapoet.*;

import static javax.lang.model.type.TypeKind.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.EntityDef"
})
public class EntityProcessor extends BaseProcessor{
    Seq<VariableElement> defs = new Seq<>();
    ObjectSet<EntityDefinition> specs = new ObjectSet<>();
    ObjectMap<EntityDefinition, Seq<VariableElement>> defMap = new ObjectMap<>();

    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        defs.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(EntityDef.class));

        if(round == 1){
            for(VariableElement e : defs){
                EntityDef def = e.getAnnotation(EntityDef.class);
                TypeElement base = (TypeElement)elements(def::base).first();
                Seq<TypeElement> inters = elements(def::def).map(el -> (TypeElement)el);
                ObjectSet<TypeElement> allInters = getInterfaces(base);

                StringBuilder name = new StringBuilder();
                for(TypeElement t : inters){
                    String raw = t.getSimpleName().toString();
                    name.append(raw.endsWith("c") ? raw.substring(0, raw.length() - 1) : raw);
                }
                name.append(base.getSimpleName().toString());

                TypeSpec.Builder entity = TypeSpec.classBuilder(name.toString()).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
                entity.superclass(tName(base));
                for(TypeElement t : inters){
                    if(allInters.contains(t)) continue;

                    entity.addSuperinterface(tName(t));
                }

                Seq<String> resolvedGetters = Seq.with("self", "as");
                for(TypeElement type : inters){
                    if(allInters.contains(type)) continue;

                    Seq<ExecutableElement> getters = getGetters(type).select(getter ->
                        !allInters.contains((TypeElement)getter.getEnclosingElement())
                    );
                    for(ExecutableElement getter : getters){
                        String n = getter.getSimpleName().toString();

                        if(getter.getAnnotation(MustInherit.class) != null){
                            resolvedGetters.add(n);
                        }
                        if(resolvedGetters.contains(n)) continue;

                        FieldSpec.Builder field = FieldSpec.builder(
                            TypeName.get(getter.getReturnType()),
                            n,
                            getter.getAnnotation(ReadOnly.class) != null ? Modifier.PROTECTED : Modifier.PUBLIC
                        );
                        Initialize initializer = getter.getAnnotation(Initialize.class);
                        if(initializer != null){
                            field.initializer(initializer.eval(), elements(initializer::args).map(this::cName).toArray(Object.class));
                        }

                        MethodSpec.Builder getImpl = MethodSpec.overriding(getter)
                            .addStatement("return this.$L", n);
                        ExecutableElement setter = getSetter((TypeElement)getter.getEnclosingElement(), getter);
                        MethodSpec.Builder setImpl;
                        if(setter != null){
                            setImpl = MethodSpec.overriding(setter)
                                .addStatement("this.$L = $L", n, setter.getParameters().get(0).getSimpleName().toString());
                        }else{
                            setImpl = MethodSpec.methodBuilder(n).addModifiers(getter.getAnnotation(ReadOnly.class) != null ? Modifier.PROTECTED : Modifier.PUBLIC)
                                .addParameter(TypeName.get(getter.getReturnType()), n)
                                .addStatement("this.$L = $L", n, n);
                        }

                        entity
                            .addField(field.build())
                            .addMethod(getImpl.build())
                            .addMethod(setImpl.build());

                        resolvedGetters.add(n);
                    }
                }

                ObjectMap<ExecutableElement, Seq<ExecutableElement>> appending = new ObjectMap<>();
                for(TypeElement type : inters){
                    appending.putAll(getAppendedMethods(base, type));
                }

                for(ExecutableElement appended : appending.keys().toSeq()){
                    Seq<ExecutableElement> appenders = appending.get(appended);
                    boolean replace = appenders.contains(app -> app.getAnnotation(Replace.class) != null);
                    boolean returns = appended.getReturnType().getKind() != VOID;

                    StringBuilder params = new StringBuilder();
                    Seq<Object> args = Seq.with(appended.getSimpleName().toString());

                    List<? extends VariableElement> parameters = appended.getParameters();
                    for(int i = 0; i < parameters.size(); i++){
                        VariableElement var = parameters.get(i);

                        params.append("$L");
                        if(i < parameters.size() - 1) params.append(", ");
                        args.add(var.getSimpleName().toString());
                    }

                    MethodSpec.Builder method = MethodSpec.overriding(appended);
                    if(!replace){
                        if(!returns){
                            method
                                .addStatement("super.$L(" + params.toString() + ")", args.toArray())
                                .addCode(lnew());

                            for(ExecutableElement appender : appenders){
                                TypeElement up = (TypeElement)appender.getEnclosingElement();
                                method.addStatement("$T.super.$L(" + params.toString() + ")", Seq.<Object>with(tName(up)).addAll(args).toArray());
                            }
                        }else if(appended.getReturnType().getKind() == BOOLEAN){
                            StringBuilder builder = new StringBuilder()
                                .append("return ");
                            Seq<Object> builderArgs = new Seq<>();

                            for(int i = 0; i < appenders.size; i++){
                                ExecutableElement appender = appenders.get(i);

                                TypeElement up = (TypeElement)appender.getEnclosingElement();
                                builder.append("$T.super.$L(" + params.toString() + ")");
                                builderArgs.addAll(Seq.<Object>with(tName(up)).addAll(args));

                                if(i < appenders.size - 1){
                                    builder.append(" && ");
                                }else{
                                    builder.append(";");
                                }
                            }

                            method.addStatement(builder.toString(), builderArgs.toArray());
                        }
                    }else{
                        Seq<ExecutableElement> replacers = appenders.select(app -> app.getAnnotation(Replace.class) != null);

                        for(ExecutableElement replacer : replacers){
                            TypeElement up = (TypeElement)replacer.getEnclosingElement();
                            Object[] mArgs = Seq.<Object>with(tName(up)).addAll(args).toArray();
                            if(!returns){
                                method.addStatement("$T.super.$L(" + params.toString() + ")", mArgs);
                            }else{
                                method.addStatement("return $T.super.$L(" + params.toString() + ")", mArgs);
                            }
                        }
                    }

                    entity.addMethod(method.build());
                }

                EntityDefinition definition = new EntityDefinition(name.toString(), base, entity);
                specs.add(definition);
                defMap.get(definition, Seq::new).add(e);
            }
        }else if(round == 2){
            TypeSpec.Builder mapper = TypeSpec.classBuilder("UnityEntityMapping").addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(cName(SuppressWarnings.class))
                        .addMember("value", "$S", "unchecked")
                    .build()
                )
                .addJavadoc("Modifies {@link $T} based on all the generated entities", cName(EntityMapping.class))
                .addMethod(
                    MethodSpec.methodBuilder("put").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                        .addJavadoc(
                            CodeBlock.builder()
                                .add("Maps {@link $T} with its entity type provider" + lnew(), cName(UnlockableContent.class))
                                .add("@param content The content")
                            .build()
                        )
                        .returns(TypeName.VOID)
                        .addParameter(cName(UnlockableContent.class), "content")
                        .addParameter(ParameterizedTypeName.get(cName(Prov.class), tvName("?")), "entity")
                        .beginControlFlow("if(content instanceof $T block)", cName(Block.class))
                            .addStatement("block.buildType = ($T<$T>)entity", cName(Prov.class), cName(Building.class))
                        .nextControlFlow("else if(content instanceof $T unit)", cName(UnitType.class))
                            .addStatement("unit.constructor = ($T<? extends $T>)entity", cName(Prov.class), cName(Unit.class))
                            .addCode(lnew())
                            .addStatement("$T.nameMap.put(unit.name.replaceFirst($S, $S), entity)", cName(EntityMapping.class), "unity-", "")
                            .addStatement("$T.nameMap.put(entity.getClass().getSimpleName(), entity)", cName(EntityMapping.class))
                            .addStatement("$T.nameMap.put($T.camelToKebab(entity.getClass().getSimpleName()), entity)", cName(EntityMapping.class), cName(Strings.class))
                        .endControlFlow()
                    .build()
                );
            MethodSpec.Builder init = MethodSpec.methodBuilder("init").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("Puts all content that has custom entities")
                .returns(TypeName.VOID);

            int classId = 0;
            for(int i = 0; i < EntityMapping.idMap.length; i++){
                if(i >= EntityMapping.idMap.length || (EntityMapping.idMap[i] == null && EntityMapping.idMap[i + 1] == null)){
                    break;
                }else{
                    classId++;
                }
            }

            boolean first = true;
            for(EntityDefinition spec : specs){
                TypeElement base = spec.base;
                ClassName specName = ClassName.get(packageName, spec.name);

                if(!typeUtils.isSubtype(base.asType(), elementUtils.getTypeElement("mindustry.gen.Buildingc").asType())){
                    classId++;

                    MethodSpec.Builder id = MethodSpec.methodBuilder("classId").addModifiers(Modifier.PUBLIC)
                        .addAnnotation(cName(Override.class))
                        .returns(TypeName.INT)
                        .addStatement("return $L", classId);
                    spec.builder.addMethod(id.build());

                    if(!first) init.addCode(lnew());
                    init.addStatement("$T.idMap[$L] = $T::new", cName(EntityMapping.class), classId, specName);
                }

                for(VariableElement e : defMap.get(spec)){
                    TypeElement up = (TypeElement)e.getEnclosingElement();
                    String c = e.getSimpleName().toString();

                    init.addStatement("put($T.$L, $T::new)", tName(up), c, specName);
                }

                first = false;

                spec.builder.addMethod(
                    MethodSpec.methodBuilder("toString").addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .returns(String.class)
                        .addStatement("return $S + id()", spec.name.toString() + "#")
                    .build()
                );
                write(spec.builder.build());
            }

            mapper.addMethod(init.build());
            write(mapper.build());
        }
    }

    protected ObjectSet<TypeElement> getInterfaces(TypeElement type){
        Seq<TypeMirror> inters = (Seq<TypeMirror>)Seq.with(type.getInterfaces());
        inters.add(type.asType());

        ObjectSet<TypeElement> all = ObjectSet.with(inters.map(this::toEl).map(e -> (TypeElement)e));
        for(TypeMirror m : type.getInterfaces()){
            if(!(m instanceof NoType)){
                all.addAll(getInterfaces((TypeElement)toEl(m)));
            }
        }

        return all;
    }

    protected ObjectMap<ExecutableElement, Seq<ExecutableElement>> getAppendedMethods(TypeElement base, TypeElement comp){
        ObjectMap<ExecutableElement, Seq<ExecutableElement>> appending = new ObjectMap<>();
        Seq<ExecutableElement> baseMethods = getMethodsRec(base);
        Seq<ExecutableElement> toAppend = getMethods(comp).select(m ->
            m.getModifiers().contains(Modifier.DEFAULT)
        );

        for(ExecutableElement e : toAppend){
            ExecutableElement append = baseMethods.find(m ->
                m.getReturnType().equals(e.getReturnType()) &&
                m.getSimpleName().toString().equals(e.getSimpleName().toString()) &&
                m.getTypeParameters().equals(e.getTypeParameters()) &&
                m.getParameters().equals(e.getParameters())
            );

            if(append != null){
                appending.get(append, Seq::new).add(e);
            }
        }

        return appending;
    }

    protected Seq<ExecutableElement> getGetters(TypeElement type){
        Seq<ExecutableElement> getters = new Seq<>();
        getInterfaces(type).each(t -> getMethods(t).each(m ->
            m.getReturnType().getKind() != VOID &&
            m.getParameters().isEmpty() &&
            !m.getModifiers().contains(Modifier.DEFAULT) &&
            m.getAnnotation(MustInherit.class) == null
        ,
        getters::add));

        return getters;
    }

    protected ExecutableElement getSetter(TypeElement type, ExecutableElement getter){
        return getMethods(type).find(m ->
            m.getSimpleName().toString().equals(getter.getSimpleName().toString()) &&
            m.getReturnType().getKind() == VOID &&
            m.getParameters().size() == 1 &&
            !m.getModifiers().contains(Modifier.DEFAULT)
        );
    }

    protected Seq<ExecutableElement> getMethods(TypeElement type){
        return Seq.with(type.getEnclosedElements()).select(el -> el instanceof ExecutableElement).map(el -> (ExecutableElement)el);
    }

    protected Seq<ExecutableElement> getMethodsRec(TypeElement type){
        Seq<ExecutableElement> methods = new Seq<>();
        getInterfaces(type).each(t -> 
            getMethods(t).each(m ->
                !methods.contains(mm -> elementUtils.overrides(mm, m, (TypeElement)mm.getEnclosingElement()))
            ,
            methods::add)
        );

        return methods;
    }

    protected ExecutableElement getMethod(TypeElement type, String name, TypeMirror retType, List<? extends VariableElement> params){
        return getMethods(type).find(m -> {
            List<? extends VariableElement> realParams = m.getParameters();

            return
                m.getSimpleName().toString().equals(name) &&
                m.getReturnType().equals(retType) &&
                realParams.equals(params);
        });
    }

    class EntityDefinition{
        final String name;
        final TypeElement base;
        final TypeSpec.Builder builder;

        EntityDefinition(String name, TypeElement base, TypeSpec.Builder builder){
            this.name = name;
            this.base = base;
            this.builder = builder;
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof EntityDefinition def
            ?   def.name.equals(name)
            :   false;
        }

        @Override
        public int hashCode(){
            return name.hashCode();
        }
    }
}
