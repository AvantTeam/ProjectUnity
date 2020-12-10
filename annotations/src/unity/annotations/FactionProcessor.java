package unity.annotations;

import arc.struct.*;

import mindustry.ctype.*;

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
            .addJavadoc("Modifies content fields based on its {@link $L}", Faction.class.getCanonicalName())
            .addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        ClassName.get(ObjectMap.class),
                        typeName(UnlockableContent.class),
                        typeName(Faction.class)
                    ),
                    "map",
                    Modifier.PRIVATE,
                    Modifier.STATIC
                )
                .addJavadoc("Maps {@link $L} with {@link $L}", UnlockableContent.class.getCanonicalName(), Faction.class.getCanonicalName())
                .initializer("new arc.struct.ObjectMap<>()")
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("map").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc("Gets a {@link $L} with the given content as a key", Faction.class.getCanonicalName())
                    .returns(typeName(Faction.class))
                    .addParameter(UnlockableContent.class, "name")
                    .addStatement("return map.get(name)")
                    .build()
            );
        MethodSpec.Builder initializer = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Initializes all content whose fields are to be modified");

        for(VariableElement e : factions){
            String up = e.getEnclosingElement().asType().toString();
            String c = e.getSimpleName().toString();
            Faction fac = e.getAnnotation(FactionDef.class).type();
            String upf = fac.getClass().getCanonicalName();
            String cf = fac.name();

            initializer.addStatement("map.put($L.$L, $L.$L)", up, c, upf, cf);
            initializer.addStatement("$L.$L.description += $S + map.get($L.$L).name", up, c, "\n\nFaction: ", up, c);
        }

        facMeta.addMethod(initializer.build());

        write(facMeta.build());
    }
}
