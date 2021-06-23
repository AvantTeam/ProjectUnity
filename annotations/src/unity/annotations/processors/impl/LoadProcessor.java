package unity.annotations.processors.impl;

import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import com.squareup.javapoet.*;
import unity.annotations.Annotations.*;
import unity.annotations.processors.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

/** @author GlennFolker */
@SupportedAnnotationTypes("unity.annotations.Annotations.LoadRegs")
public class LoadProcessor extends BaseProcessor{
    ObjectSet<String> genericRegs = new ObjectSet<>();

    {
        rounds = 1;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        for(Element e : roundEnv.getElementsAnnotatedWith(LoadRegs.class)){
            LoadRegs regs = annotation(e, LoadRegs.class);

            genericRegs.addAll(regs.value());
            if(regs.outline()){
                genericRegs.addAll(Seq.with(regs.value()).map(n -> n + "-outline"));
            }
        }

        processGenerics();
    }

    void processGenerics() throws Exception{
        TypeSpec.Builder spec = TypeSpec.classBuilder("Regions").addModifiers(Modifier.PUBLIC)
            .addJavadoc("Generic texture regions");

        MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Loads the texture regions");

        for(String reg : genericRegs){
            String name = Strings.kebabToCamel(reg) + "Region";
            FieldSpec.Builder var = FieldSpec.builder(
                cName(TextureRegion.class),
                name,
                Modifier.PUBLIC, Modifier.STATIC
            );

            spec.addField(var.build());
            load.addStatement("$L = $T.atlas.find($S)", name, cName(Core.class), "unity-" + reg);
        }

        spec.addMethod(load.build());

        write(spec.build());
    }
}
