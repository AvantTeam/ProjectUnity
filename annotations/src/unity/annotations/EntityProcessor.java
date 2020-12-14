package unity.annotations;

import arc.struct.*;

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
    Seq<EntityDefinition> specs = new Seq<>();

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

                StringBuilder name = new StringBuilder();
                for(TypeElement t : inters){
                    String raw = t.getSimpleName().toString();
                    name.append(raw.endsWith("c") ? raw.substring(0, raw.length() - 1) : raw);
                }
                name.append(base.getSimpleName().toString());

                TypeSpec.Builder entity = TypeSpec.classBuilder(name.toString()).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(tName(base))
                    .addSuperinterfaces(inters.map(this::tName));

                Seq<String> resolvedGetters = new Seq<>();
                for(TypeElement type : inters){
                    Seq<ExecutableElement> getters = getGetters(type);
                    for(ExecutableElement getter : getters){
                        String n = getter.getSimpleName().toString();

                        if(resolvedGetters.contains(n)) continue;

                        FieldSpec.Builder field = FieldSpec.builder(
                            TypeName.get(getter.getReturnType()),
                            n,
                            getter.getAnnotation(ReadOnly.class) != null ? Modifier.PROTECTED : Modifier.PUBLIC
                        );
                        Initialize initializer = getter.getAnnotation(Initialize.class);
                        if(initializer != null){
                            field.initializer(initializer.eval(), (Object[])Seq.with(initializer.args()).map(this::cName).toArray());
                        }

                        MethodSpec.Builder getImpl = MethodSpec.overriding(getter)
                            .addStatement("return this.$L", n);
                        ExecutableElement setter = getSetter(type, getter);
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

                specs.add(new EntityDefinition(name.toString(), entity));
            }
        }else if(round == 2){
            // maybe not today...
            for(EntityDefinition spec : specs){
                write(spec.builder.build());
            }
        }
    }

    protected Seq<ExecutableElement> getGetters(TypeElement type){
        return getMethods(type).select(m ->
            m.getReturnType().getKind() != VOID &&
            m.getParameters().isEmpty() &&
            !m.getModifiers().contains(Modifier.DEFAULT)
        );
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
        final TypeSpec.Builder builder;

        EntityDefinition(String name, TypeSpec.Builder builder){
            this.name = name;
            this.builder = builder;
        }
    }
}
