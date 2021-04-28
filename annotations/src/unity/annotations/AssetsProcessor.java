package unity.annotations;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.SoundLoader.*;
import arc.audio.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.PropertiesUtils;

import com.squareup.javapoet.*;
import mindustry.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

/** @author GlennFolker */
@SupportedAnnotationTypes("java.lang.Override")
public class AssetsProcessor extends BaseProcessor{
    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        if(round == 1){
            processSounds();
        }else{
            processObjects();
        }
    }

    void processSounds() throws Exception{
        TypeSpec.Builder soundSpec = TypeSpec.classBuilder("UnitySounds").addModifiers(Modifier.PUBLIC)
            .addJavadoc("Unity's {@link $T} effects", cName(Sound.class))
            .addMethod(
                MethodSpec.methodBuilder("loadSound").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Loads a {@link $T}" + lnew(), cName(Sound.class))
                            .add("@param soundName The {@link $T} name" + lnew(), cName(Sound.class))
                            .add("@return The {@link $T}", cName(Sound.class))
                        .build()
                    )
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
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Disposes a {@link $T}" + lnew(), cName(Sound.class))
                            .add("@param soundName The {@link $T} name" + lnew(), cName(Sound.class))
                            .add("@return {@code null}")
                        .build()
                    )
                    .returns(cName(Sound.class))
                    .addParameter(cName(String.class), "soundName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + soundName", cName(String.class), "sounds/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .beginControlFlow("if($T.assets.isLoaded(path, $T.class))", cName(Core.class), cName(Sound.class))
                            .addStatement("$T.assets.unload(path)", cName(Core.class))
                        .endControlFlow()
                    .endControlFlow()
                    .addCode(lnew())
                    .addStatement("return null")
                .build()
            );
        MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Loads all {@link $T}s", cName(Sound.class))
            .returns(TypeName.VOID);

        MethodSpec.Builder dispose = MethodSpec.methodBuilder("dispose").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Disposes all {@link $T}s", cName(Sound.class))
            .returns(TypeName.VOID);

        String dir = "main/assets/sounds/";
        rootDir.child(dir).walk(path -> {
            String p = path.absolutePath();
            String name = p.substring(p.lastIndexOf(dir) + dir.length(), p.length());
            String fname = path.nameWithoutExtension();
            int ex = 4;

            soundSpec.addField(
                FieldSpec.builder(
                    cName(Sound.class),
                    Strings.kebabToCamel(fname),
                    Modifier.PUBLIC, Modifier.STATIC
                )
                .build()
            );

            String stripped = name.substring(0, name.length() - ex);
            load.addStatement("$L = loadSound($S)", Strings.kebabToCamel(fname), stripped);
            dispose.addStatement("$L = disposeSound($S)", Strings.kebabToCamel(fname), stripped);
        });

        soundSpec
            .addMethod(load.build())
            .addMethod(dispose.build());

        write(soundSpec.build());
    }

    void processObjects() throws Exception{
        TypeElement wavefrontObject = elementUtils.getTypeElement("unity.util.WavefrontObject");

        TypeSpec.Builder objSpec = TypeSpec.classBuilder("UnityObjs").addModifiers(Modifier.PUBLIC)
            .addJavadoc("Unity's wavefront objects")
            .addMethod(
                MethodSpec.methodBuilder("loadObject").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Loads a {@link $T}" + lnew(), tName(wavefrontObject))
                            .add("@param objName The {@link $T} name" + lnew(), tName(wavefrontObject))
                            .add("@return The {@link $T}", tName(wavefrontObject))
                        .build()
                    )
                    .returns(tName(wavefrontObject))
                    .addParameter(cName(String.class), "objName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + objName", cName(String.class), "objects/")
                        .addStatement("$T path = name + $S", cName(String.class), ".obj")
                        .addCode(lnew())
                        .addStatement("var object = new $T()", tName(wavefrontObject))
                        .addCode(lnew())
                        .addStatement("$T<?> desc = $T.assets.load(path, $T.class, new $T(object))", cName(AssetDescriptor.class), cName(Core.class), tName(wavefrontObject), tName(elementUtils.getTypeElement("unity.util.WavefrontObjectLoader.WavefrontObjectParameters")))
                        .addStatement("desc.errored = $T::printStackTrace", cName(Throwable.class))
                        .addCode(lnew())
                        .addStatement("return object")
                    .nextControlFlow("else")
                        .addStatement("return new $T()", tName(wavefrontObject))
                    .endControlFlow()
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("disposeObject").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Disposes a {@link $T}" + lnew(), tName(wavefrontObject))
                            .add("@param objName The {@link $T} name" + lnew(), tName(wavefrontObject))
                            .add("@return {@code null}")
                        .build()
                    )
                    .returns(tName(wavefrontObject))
                    .addParameter(cName(String.class), "objName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + objName", cName(String.class), "objects/")
                        .addStatement("$T path = name + $S", cName(String.class), ".ogg")
                        .addCode(lnew())
                        .beginControlFlow("if($T.assets.isLoaded(path, $T.class))", cName(Core.class), tName(wavefrontObject))
                            .addStatement("$T.assets.unload(path)", cName(Core.class))
                        .endControlFlow()
                    .endControlFlow()
                    .addCode(lnew())
                    .addStatement("return null")
                .build()
            );
        MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Loads all {@link $T}s", tName(wavefrontObject))
            .returns(TypeName.VOID);

        MethodSpec.Builder dispose = MethodSpec.methodBuilder("dispose").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Disposes all {@link $T}s", tName(wavefrontObject))
            .returns(TypeName.VOID);

        ObjectMap<String, String> objProp = new ObjectMap<>();
        PropertiesUtils.load(objProp, rootDir.child("main/assets/objects/objects.properties").reader());

        String dir = "main/assets/objects/";
        boolean[] first = {true};
        rootDir.child(dir).walk(path -> {
            if(!path.name().endsWith(".obj")) return;

            String p = path.absolutePath();
            String name = p.substring(p.lastIndexOf(dir) + dir.length(), p.length());
            String fname = path.nameWithoutExtension();
            int ex = 4;

            objSpec.addField(
                FieldSpec.builder(
                    tName(wavefrontObject),
                    Strings.kebabToCamel(fname),
                    Modifier.PUBLIC, Modifier.STATIC
                )
                .build()
            );

            if(!first[0]){
                load.addStatement(lnew());
            }
            String stripped = name.substring(0, name.length() - ex);
            load.addStatement("$L = loadObject($S)", Strings.kebabToCamel(fname), stripped);

            Seq<String> props = objProp.keys().toSeq().select(prop -> prop.split("\\.")[1].equals(stripped));
            for(String prop : props){
                String field = prop.split("\\.")[2];
                String val = objProp.get(prop);

                if(!val.startsWith("[")){
                    load.addStatement("$L.$L = $L", Strings.kebabToCamel(fname), field, val);
                }else{
                    Seq<String> rawargs = Seq.with(val.substring(1, val.length() - 1).split("\\s*,\\s*"));
                    String format = rawargs.remove(0);

                    Seq<Object> args = rawargs.map(elementUtils::getTypeElement);
                    args.insert(0, Strings.kebabToCamel(fname));
                    args.insert(1, field);

                    load.addStatement("$L.$L = " + format, args.toArray());
                }
            }

            first[0] = false;
            dispose.addStatement("$L = disposeObject($S)", Strings.kebabToCamel(fname), stripped);
        });

        objSpec
            .addMethod(load.build())
            .addMethod(dispose.build());

        write(objSpec.build());
    }
}
