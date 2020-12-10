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
                    .addParameter(UnlockableContent.class, "name")
                    .addStatement("return map.get(name)")
                .build()
            );
        MethodSpec.Builder initializer = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Initializes all content whose fields are to be modified");

        int i = 0;
        for(VariableElement e : factions){
            i++;

            Element up = e.getEnclosingElement();
            String c = e.getSimpleName().toString();
            Faction fac = e.getAnnotation(FactionDef.class).type();
            String cf = fac.name();

            initializer.addStatement("map.put($T.$L, $T.$L)", tName(up), c, cName(Faction.class), cf);
            initializer.addStatement("$T.$L.description += $S + $S + map.get($T.$L).name", tName(up), c, "\n", "Faction: ", tName(up), c);
            if(i < factions.size){
                initializer.addCode(lnew());
            }
        }

        facMeta.addMethod(initializer.build());

        write(facMeta.build());
    }
}
