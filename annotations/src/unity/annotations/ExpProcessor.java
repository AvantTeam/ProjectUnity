package unity.annotations;

import arc.struct.*;
import mindustry.ctype.*;
import unity.annotations.Annotations.*;
import unity.annotations.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.ExpDef"
})
public class ExpProcessor extends BaseProcessor{
    Seq<VariableElement> exps = new Seq<>();

    {
        rounds = 1;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        exps = (Seq<VariableElement>)Seq.with(roundEnv.getElementsAnnotatedWith(ExpDef.class));

        TypeSpec.Builder expMeta = TypeSpec.classBuilder("ExpMeta").addModifiers(Modifier.PUBLIC)
        .addJavadoc("Manages exp mechanism")
        .addField(
            FieldSpec.builder(
                ParameterizedTypeName.get(
                    cName(ObjectMap.class),
                    cName(UnlockableContent.class),
                    ParameterizedTypeName.get(
                        cName(ExpType.class),
                        tvName("?")
                    )
                ),
                "map",
                Modifier.PRIVATE, Modifier.STATIC
            )
                .addJavadoc("Maps {@link $T} with {@link $T}", cName(UnlockableContent.class), cName(ExpType.class))
                .initializer("new $T<>()", cName(ObjectMap.class))
            .build()
        )
        .addMethod(
            MethodSpec.methodBuilder("put").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc(
                    CodeBlock.builder()
                        .add("Puts the map with a content as a key and an exp type as a value" + lnew())
                        .add("@param content The content" + lnew())
                        .add("@param exp The exp type")
                    .build()
                )
                .returns(TypeName.VOID)
                .addTypeVariable(tvName("T", cName(UnlockableContent.class)))
                .addParameter(cName(UnlockableContent.class), "content")
                .addParameter(
                    ParameterizedTypeName.get(
                        cName(ExpType.class),
                        tvName("T")
                    ),
                    "exp"
                )
                .addStatement("map.put(content, exp)")
                .addStatement("exp.init()")
            .build()
        );

        MethodSpec.Builder init = MethodSpec.methodBuilder("init").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addJavadoc("Initializes and maps the exp content with the exp types");

        for(VariableElement e : exps){
            TypeName up = tName(e.getEnclosingElement());
            String c = e.getSimpleName().toString();

            init.addStatement("put($T.$L, new $T<>($T.$L))", up, c, cName(ExpType.class), up, c);
        }

        expMeta.addMethod(init.build());

        write(expMeta.build());
    }
}
