package unity.annotations;

import arc.struct.*;

import mindustry.ctype.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;

import unity.annotations.Annotations.*;
import unity.annotations.util.*;

@SuppressWarnings({"unchecked"})
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
            .addJavadoc("Modifies content fields based on its {@link $T}", cName(Faction.class))
            .addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        cName(ObjectMap.class),
                        tName(UnlockableContent.class),
                        tName(Faction.class)
                    ),
                    "map",
                    Modifier.PRIVATE,
                    Modifier.STATIC
                )
                    .addJavadoc("Maps {@link $T} with {@link $T}", cName(UnlockableContent.class), cName(Faction.class))
                    .initializer("new $T<>()", cName(ObjectMap.class))
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("map").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc("Gets a {@link $T} with the given content as a key", cName(Faction.class))
                    .returns(tName(Faction.class))
                    .addParameter(cName(UnlockableContent.class), "content")
                    .addStatement("return map.get(content)")
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("put").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc("Puts and handles this content with the given {@link $T}", cName(Faction.class))
                    .returns(TypeName.VOID)
                    .addParameter(cName(UnlockableContent.class), "content")
                    .addParameter(cName(Faction.class), "faction")
                    .addStatement("map.put(content, faction)")
                    .addStatement("content.description += $S + $S + faction.name", "\n", "Faction: ")
                .build()
            );

        MethodSpec.Builder initializer = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Initializes all content whose fields are to be modified");

        for(VariableElement e : factions){
            TypeName up = TypeName.get(e.getEnclosingElement().asType());
            String c = e.getSimpleName().toString();
            TypeName upf = cName(Faction.class);
            String f = e.getAnnotation(FactionDef.class).type().name();

            initializer.addStatement("put($T.$L, $T.$L)", up, c, upf, f);
        }

        facMeta.addMethod(initializer.build());

        write(facMeta.build());
    }
}
