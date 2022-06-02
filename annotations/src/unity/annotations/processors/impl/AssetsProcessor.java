package unity.annotations.processors.impl;

import arc.audio.*;
import arc.files.*;
import arc.util.*;
import com.squareup.javapoet.*;
import mindustry.*;
import unity.annotations.Annotations.*;
import unity.annotations.processors.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import java.util.*;

import static javax.lang.model.element.Modifier.*;

public class AssetsProcessor extends BaseProcessor{
    public static final String packageName = packageRoot + ".assets";

    public Fi assetsDir;

    @Override
    public synchronized void init(ProcessingEnvironment env){
        super.init(env);
        assetsDir = Fi.get(env.getOptions().get("assetsDirectory"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        if(++round != 1) return true;
        try{
            TypeSpec.Builder builder = TypeSpec.classBuilder("PUSounds")
                .addModifiers(PUBLIC, FINAL)
                .addMethod(
                    MethodSpec.constructorBuilder()
                        .addModifiers(PRIVATE)
                        .addStatement("throw new $T()", spec(AssertionError.class))
                    .build()
                );

            MethodSpec.Builder loader = MethodSpec.methodBuilder("load")
                .addModifiers(PUBLIC, STATIC)
                .returns(TypeName.VOID);

            Fi root = assetsDir.child("sounds");
            int offset = root.absolutePath().length() + 1;

            root.walk(f -> {
                String ext = f.extension();
                if(ext.equals("ogg") || ext.equals("mp3")){
                    String path = f.absolutePath(), name = Strings.kebabToCamel(f.nameWithoutExtension());
                    path = path.substring(offset, path.length() - 4);

                    builder.addField(spec(Sound.class), name, PUBLIC, STATIC);
                    loader.addStatement("$L = $T.tree.loadSound($S)", name, spec(Vars.class), path);
                }
            });

            write(packageName, builder.addMethod(loader.build()), null);
        }catch(Exception e){
            Log.err(e);
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        return set(fName(ModBase.class));
    }

    @Override
    public Set<String> getSupportedOptions(){
        return set("assetsDirectory");
    }
}
