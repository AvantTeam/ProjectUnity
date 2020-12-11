package unity.annotations;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.SoundLoader.*;
import arc.assets.loaders.MusicLoader.*;
import arc.audio.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ctype.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;

import unity.annotations.Annotations.*;
import unity.annotations.util.*;

@SuppressWarnings({"unchecked"})
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.FactionDef",
    "unity.annotations.Annotations.MusicDef"
})
public class FactionProcessor extends BaseProcessor{
    Seq<VariableElement> factions = new Seq<>();
    Seq<VariableElement> musics = new Seq<>();

    private final ObjectMap<String, Faction> map = ObjectMap.of(
        "monolithDark1:dark", Faction.monolith,
        "monolithDark2:dark", Faction.monolith
    );

    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        if(round == 1){
            factions.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(FactionDef.class));

            processSounds();
            processMusics();
        }else if(round == 2){
            factions.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(FactionDef.class));
            musics.addAll((Set<VariableElement>)roundEnv.getElementsAnnotatedWith(MusicDef.class));

            processFactions();
        }
    }

    protected void processSounds() throws Exception{
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

        rootDir.child("main/assets/sounds").walk(path -> {
            String name = path.nameWithoutExtension();

            soundSpec.addField(
                FieldSpec.builder(
                    tName(Sound.class),
                    name,
                    Modifier.PUBLIC, Modifier.STATIC
                )
                .build()
            );

            load.addStatement("$L = loadSound($S)", name, name);
            dispose.addStatement("$L = disposeSound($S)", name, name);
        });

        soundSpec
            .addMethod(load.build())
            .addMethod(dispose.build());

        write(soundSpec.build());
    }

    protected void processMusics() throws Exception{
        TypeSpec.Builder musicSpec = TypeSpec.classBuilder("UnityMusics").addModifiers(Modifier.PUBLIC)
            .addJavadoc("Unity's {@link $T}s", cName(Music.class))
            .addMethod(
                MethodSpec.methodBuilder("loadMusic").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Loads a {@link $T}" + lnew(), cName(Music.class))
                            .add("@param musicName The {@link $T} name" + lnew(), cName(Music.class))
                            .add("@return The {@link $T}", cName(Music.class))
                        .build()
                    )
                    .returns(cName(Music.class))
                    .addParameter(cName(String.class), "musicName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + musicName", cName(String.class), "music/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .addStatement("var music = new $T()", cName(Music.class))
                        .addCode(lnew())
                        .addStatement("$T<?> desc = $T.assets.load(path, $T.class, new $T(music))", cName(AssetDescriptor.class), cName(Core.class), cName(Music.class), cName(MusicParameter.class))
                        .addStatement("desc.errored = $T::printStackTrace", cName(Throwable.class))
                        .addCode(lnew())
                        .addStatement("return music")
                    .nextControlFlow("else")
                        .addStatement("return new $T()", cName(Music.class))
                    .endControlFlow()
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("disposeMusic").addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Disposes a {@link $T}" + lnew(), cName(Music.class))
                            .add("@param musicName The {@link $T} name" + lnew(), cName(Music.class))
                            .add("@return {@code null}")
                        .build()
                    )
                    .returns(cName(Music.class))
                    .addParameter(cName(String.class), "musicName")
                    .beginControlFlow("if(!$T.headless)", cName(Vars.class))
                        .addStatement("$T name = $S + musicName", cName(String.class), "music/")
                        .addStatement("$T path = $T.tree.get(name + $S).exists() ? name + $S : name + $S", cName(String.class), cName(Vars.class), ".ogg", ".ogg", ".mp3")
                        .addCode(lnew())
                        .beginControlFlow("if($T.assets.isLoaded(path, $T.class))", cName(Core.class), cName(Music.class))
                            .addStatement("$T.assets.unload(path)", cName(Core.class))
                        .endControlFlow()
                    .endControlFlow()
                    .addCode(lnew())
                    .addStatement("return null")
                .build()
            );
        MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Loads all {@link $T}s", cName(Music.class))
            .returns(TypeName.VOID);

        MethodSpec.Builder dispose = MethodSpec.methodBuilder("dispose").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Disposes all {@link $T}s", cName(Music.class))
            .returns(TypeName.VOID);

        rootDir.child("main/assets/music").walk(path -> {
            String name = path.nameWithoutExtension();

            FieldSpec.Builder music = FieldSpec.builder(tName(Music.class), name, Modifier.PUBLIC, Modifier.STATIC);
            Seq<String[]> names = map.keys().toSeq().map(n -> n.split(":"));
            for(String[] n : names){
                if(n[0].equals(name)){
                    music.addAnnotation(
                        AnnotationSpec.builder(cName(MusicDef.class))
                            .addMember("type", "$T.$L", Faction.class, map.get(name + ":" + n[1]).name())
                            .addMember("category", "$S", n[1])
                        .build()
                    );
                }
            }

            musicSpec.addField(music.build());
            load.addStatement("$L = loadMusic($S)", name, name);
            dispose.addStatement("$L = disposeMusic($S)", name, name);
        });

        musicSpec
            .addMethod(load.build())
            .addMethod(dispose.build());

        write(musicSpec.build());
    }

    protected void processFactions() throws Exception{
        TypeSpec.Builder facMeta = TypeSpec.classBuilder("FactionMeta").addModifiers(Modifier.PUBLIC)
            .addAnnotation(
                AnnotationSpec.builder(cName(SuppressWarnings.class))
                    .addMember("value", "$S", "unchecked")
                .build()
            )
            .addJavadoc("Modifies content fields based on its {@link $T}", cName(Faction.class))
            .addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        cName(ObjectMap.class),
                        tName(Object.class),
                        tName(Faction.class)
                    ),
                    "map",
                    Modifier.PRIVATE, Modifier.STATIC
                )
                    .addJavadoc("Maps {@link $T} with {@link $T}", cName(Object.class), cName(Faction.class))
                    .initializer("new $T<>()", cName(ObjectMap.class))
                .build()
            )
            .addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        cName(ObjectMap.class),
                        tName(Music.class),
                        tName(String.class)
                    ),
                    "music", Modifier.PRIVATE, Modifier.STATIC
                )
                    .addJavadoc("Maps {@link $T} with its category", cName(Music.class))
                    .initializer("new $T<>()", cName(ObjectMap.class))
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("map").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Gets a {@link $T} with the given content as a key" + lnew(), cName(Faction.class))
                            .add("@param content The content object" + lnew())
                            .add("@return The {@link $T}", cName(Faction.class))
                        .build()
                    )
                    .returns(tName(Faction.class))
                    .addParameter(cName(Object.class), "content")
                    .addStatement("return map.get(content)")
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("put").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Puts and handles this content with the given {@link $T}" + lnew(), cName(Faction.class))
                            .add("@param content The content object" + lnew())
                            .add("@param faction The {@link $T}", cName(Faction.class))
                        .build()
                    )
                    .returns(TypeName.VOID)
                    .addParameter(cName(Object.class), "content")
                    .addParameter(cName(Faction.class), "faction")
                    .addStatement("map.put(content, faction)")
                    .beginControlFlow("if(content instanceof $T unlockable)", cName(UnlockableContent.class))
                        .addStatement("unlockable.description += $S + $S + faction.name", "\n", "[gray]Faction:[] ")
                    .endControlFlow()
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getByFaction").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Returns specific {@link $T}s with the given {@link $T}" + lnew(), cName(Object.class), cName(Faction.class))
                            .add("@param <$T> The generic type to filter" + lnew(), tvName("T"))
                            .add("@param faction The {@link $T}" + lnew(), cName(Faction.class))
                            .add("@param type The generic type class" + lnew())
                            .add("@return {@link $T} filled with the filtered objects", cName(Seq.class))
                        .build()
                    )
                    .addTypeVariable(tvName("T"))
                    .returns(ParameterizedTypeName.get(cName(Seq.class), tvName("T")))
                    .addParameter(cName(Faction.class), "faction")
                    .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(cName(Class.class), tvName("T")), "type").build())
                    .addStatement("$T<$T> contents = new $T<>()", cName(Seq.class), tvName("T"), cName(Seq.class))
                    .addStatement("map.keys().toSeq().select(o -> map.get(o).equals(faction) && type.isAssignableFrom(o.getClass())).each(o -> contents.add(($T)o))", tvName("T"))
                    .addCode(lnew())
                    .addStatement("return contents")
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getByCtype").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Returns all {@link $T}s with the given {@link $T}" + lnew(), cName(Content.class), cName(ContentType.class))
                            .add("@param <$T> The generic type to filter" + lnew(), tvName("T"))
                            .add("@param ctype The {@link $T}" + lnew(), cName(ContentType.class))
                            .add("@return {@link $T} filled with the filtered objects", cName(Seq.class))
                        .build()
                    )
                    .addTypeVariable(tvName("T", cName(Content.class)))
                    .returns(ParameterizedTypeName.get(cName(Seq.class), tvName("T")))
                    .addParameter(cName(ContentType.class), "ctype")
                    .addStatement("$T<$T> contents = new $T<>()", cName(Seq.class), tvName("T"), cName(Seq.class))
                    .beginControlFlow("for($T o : map.keys().toSeq())", cName(Object.class))
                        .beginControlFlow("if(o instanceof $T c && c.getContentType().equals(ctype))", cName(Content.class))
                            .addStatement("contents.add(($T)c)", tvName("T"))
                        .endControlFlow()
                    .endControlFlow()
                    .addCode(lnew())
                    .addStatement("return contents")
                .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getMusicCategory").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addJavadoc(
                        CodeBlock.builder()
                            .add("Gets the category of a specific {@link $T}" + lnew(), cName(Music.class))
                            .add("@param mus The {@link $T}" + lnew(), cName(Music.class))
                            .add("@return The category")
                        .build()
                    )
                    .returns(ParameterizedTypeName.get(cName(Seq.class), cName(Music.class)))
                    .addParameter(cName(Music.class), "mus")
                    .addStatement("$T category = music.get(mus)", cName(String.class))
                    .addStatement("if(category == null) return $T.control.sound.ambientMusic", cName(Vars.class))
                    .addCode(lnew())
                    .beginControlFlow("return switch(category)")
                        .addStatement("case $S -> $T.control.sound.ambientMusic", "ambient", cName(Vars.class))
                        .addStatement("case $S -> $T.control.sound.darkMusic", "dark", cName(Vars.class))
                        .addStatement("case $S -> $T.control.sound.bossMusic", "boss", cName(Vars.class))
                        .addCode(lnew())
                        .addStatement("default -> throw new $T($S + category)", cName(IllegalArgumentException.class), "Unknown category: ")
                    .endControlFlow("")
                .build()
            );

        MethodSpec.Builder initializer = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addJavadoc("Initializes all content whose fields are to be modified");

        Faction before = null;
        for(VariableElement e : factions){
            TypeName up = TypeName.get(e.getEnclosingElement().asType());
            String c = e.getSimpleName().toString();
            TypeName upf = cName(Faction.class);
            FactionDef def = e.getAnnotation(FactionDef.class);
            Faction fac = def.type();

            if(before != null && !fac.equals(before)){
                initializer.addCode(lnew());
            }
            before = fac;

            initializer.addStatement("put($T.$L, $T.$L)", up, c, upf, fac.name());
        }

        Seq<String[]> names = map.keys().toSeq().map(n -> n.split(":"));
        for(VariableElement e : musics){
            TypeName up = TypeName.get(e.getEnclosingElement().asType());
            String c = e.getSimpleName().toString();
            TypeName upf = cName(Faction.class);
            String f = e.getAnnotation(MusicDef.class).type().name();

            initializer.addCode(lnew());
            initializer.addStatement("put($T.$L, $T.$L)", up, c, upf, f);

            category:
            for(String[] n : names){
                if(n[0].equals(c)){
                    initializer.addStatement("music.put($T.$L, $S)", up, c, n[1]);
                    break category;
                }
            }
        }

        facMeta.addMethod(initializer.build());

        write(facMeta.build());
    }
}
