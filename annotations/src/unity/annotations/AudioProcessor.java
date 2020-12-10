package unity.annotations;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.SoundLoader.*;
import arc.audio.*;
import mindustry.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;

@SupportedAnnotationTypes({
    "unity.annotations.Annotations.TriggerProcess"
})
public class AudioProcessor extends BaseProcessor{
    {
        rounds = 1;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        processSounds();
        processMusics();
    }

    protected void processSounds() throws Exception{
        TypeSpec.Builder sounds = TypeSpec.classBuilder("UnitySounds").addModifiers(Modifier.PUBLIC)
            .addJavadoc("Unity's {@link $T} effects", cName(Sound.class))
            .addMethod(
                MethodSpec.methodBuilder("loadSound").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc("Loads a {@link $T}", cName(Sound.class))
                    .returns(cName(Sound.class))
                    .addParameter(cName(String.class), "soundName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + soundName", cName(String.class), "sounds/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .addStatement("var sound = new $T()", cName(Sound.class))
                        .addCode(lnew())
                        .addStatement("$T<?> desc = $T.assets.load(path, $T.class, new $T(sound))", cName(AssetDescriptor.class), cName(Core.class), cName(Sound.class), cName(SoundParameter.class))
                        .addStatement("desc.errored = $T::printStackTrace", cName(Throwable.class))
                        .addCode(lnew())
                        .addStatement("return sound")
                    .nextControlFlow("else")
                        .addStatement("return new $T()", cName(Sound.class))
                    .endControlFlow()
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("disposeSound").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc("Disposes a {@link $T}", cName(Sound.class))
                    .returns(TypeName.VOID)
                    .addParameter(cName(String.class), "soundName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + soundName", cName(String.class), "sounds/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .beginControlFlow("if($T.assets.isLoaded(path, $T.class))", cName(Core.class), cName(Sound.class))
                            .addStatement("$T.assets.unload(path)", cName(Core.class))
                        .endControlFlow()
                    .endControlFlow()
                .build()
            );
        MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID);
        MethodSpec.Builder dispose = MethodSpec.methodBuilder("dispose").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID);

        rootDir.child("main/assets/sounds").walk(path -> {
            String name = path.nameWithoutExtension();

            sounds.addField(
                FieldSpec.builder(
                    tName(Sound.class),
                    name,
                    Modifier.PUBLIC, Modifier.STATIC
                )
                .build()
            );

            load.addStatement("$L = loadSound($S)", name, name);

            dispose
                .addStatement("disposeSound($S)", name)
                .addStatement("$L = null", name);
        });

        sounds
            .addMethod(load.build())
            .addMethod(dispose.build());
        write(sounds.build());
    }

    protected void processMusics() throws Exception{

    }
}
