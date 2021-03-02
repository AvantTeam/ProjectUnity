package unity.annotations;

import arc.struct.*;
import mindustry.ctype.*;
import unity.annotations.Annotations.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.ExpBase"
})
public class ExpProcessor extends BaseProcessor{
    Seq<VariableElement> exps = new Seq<>();
    ClassName expBase;

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        if(expBase == null){
            Seq<TypeElement> seq = Seq.with(roundEnv.getElementsAnnotatedWith(ExpBase.class)).map(e -> (TypeElement) e);
            if(seq.size > 1){
                throw new IllegalArgumentException("Always one type may be annotated by 'ExpBase', no more, no less");
            }

            expBase = withoutTV(seq.first());
        }
        exps.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(ExpDef.class));

        TypeSpec.Builder expMeta = TypeSpec.classBuilder("ExpMeta").addModifiers(Modifier.PUBLIC)
            .addAnnotation(
                AnnotationSpec.builder(SuppressWarnings.class)
                    .addMember("value", "$S", "unchecked")
                .build()
            )
            .addJavadoc("Maps {@link $T} with {@link $T}, if the content implements exp mechanism", cName(UnlockableContent.class), expBase)
            .addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        cName(ObjectMap.class),
                        cName(UnlockableContent.class),
                        ParameterizedTypeName.get(
                            expBase,
                            WildcardTypeName.subtypeOf(cName(UnlockableContent.class))
                        )
                    ),
                    "map",
                    Modifier.PRIVATE, Modifier.STATIC
                )
                .addJavadoc("The map")
                .initializer("new $T<>()", cName(ObjectMap.class))
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("map").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Gets a {@link $T} with {@link $T} as the key" + lnew(), expBase, cName(UnlockableContent.class))
                            .add("@param <$T> The exp type" + lnew(), tvName("T"))
                            .add("@param content The content" + lnew())
                            .add("@return The exp type")
                        .build()
                    )
                    .addTypeVariable(tvName("T", ParameterizedTypeName.get(expBase, WildcardTypeName.subtypeOf(cName(UnlockableContent.class)))))
                    .addParameter(cName(UnlockableContent.class), "content")
                    .returns(tvName("T"))
                    .addStatement("return ($T)map.get(content)", tvName("T"))
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("put").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Puts a {@link $T} with {@link $T} as the key" + lnew(), expBase, cName(UnlockableContent.class))
                            .add("@param <$T> The exp type" + lnew(), tvName("T"))
                            .add("@param content The content" + lnew())
                            .add("@param exp The exp type")
                        .build()
                    )
                    .returns(TypeName.VOID)
                    .addTypeVariable(tvName("T", ParameterizedTypeName.get(expBase, WildcardTypeName.subtypeOf(cName(UnlockableContent.class)))))
                    .addParameter(cName(UnlockableContent.class), "content")
                    .addParameter(tvName("T"), "exp")
                    .addStatement("map.put(content, exp)")
                .build()
            );
        MethodSpec.Builder init = MethodSpec.methodBuilder("init").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Initializes all contents that implements exp mechanism")
            .returns(TypeName.VOID);

        boolean first = true;
        TypeName before = null;
        for(VariableElement e : exps){
            ExpDef def = annotation(e, ExpDef.class);
            TypeElement type = (TypeElement)elements(def::value).first();
            TypeName up = tName(e.getEnclosingElement());
            String c = e.getSimpleName().toString();

            if(type.getModifiers().contains(Modifier.ABSTRACT)){
                throw new IllegalArgumentException("ExpType for '" + up + "." + c + "' is abstract!");
            }

            init.addStatement("map.put($T.$L, new $T($T.$L))", up, c, type, up, c);
            if(!up.equals(before) && !first){
                init.addCode(lnew());
            }

            before = up;
            first = false;
        }

        expMeta.addMethod(init.build());
        write(expMeta.build());
	}
}
