package unity.annotations;

import arc.struct.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;

import unity.annotations.Annotations.*;
import unity.annotations.util.*;

@SuppressWarnings({"unchecked"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.FactionDef"
})
public class FactionProcessor extends BaseProcessor{
    Seq<VariableElement> factions = new Seq<>();

    {
        rounds = 1;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        factions = (Seq<VariableElement>)Seq.with(roundEnv.getElementsAnnotatedWith(FactionDef.class));

        TypeSpec.Builder facMeta = TypeSpec.classBuilder("FactionMeta").addModifiers(Modifier.PUBLIC)
            .addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        ClassName.get(ObjectMap.class),
                        typeName(String.class),
                        typeName(Faction.class)
                    ),
                    "nameMap",
                    Modifier.PRIVATE,
                    Modifier.STATIC
                ).build()
            )
            .addMethod(
                MethodSpec.methodBuilder("map").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(typeName(Faction.class))
                    .addParameter(String.class, "name")
                    .addStatement("return nameMap.get(name)")
                    .build()
            );

        CodeBlock.Builder block = CodeBlock.builder();

        for(VariableElement e : factions){
            String up = e.getEnclosingElement().asType().toString();
            String c = e.getSimpleName().toString();
            Faction fac = e.getAnnotation(FactionDef.class).type();
            String upf = fac.getClass().getCanonicalName();
            String cf = fac.name();

            block.addStatement("nameMap.put($S, $L.$L)", "unity-" + c, upf, cf);
            block.addStatement("$L.$L.description += $S + nameMap.get($S).name", up, c, "\nFaction: ", "unity-" + c);
        }

        facMeta.addStaticBlock(block.build());

        write(facMeta.build());
    }
}
