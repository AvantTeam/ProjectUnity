package unity.annotations;

import arc.files.*;
import arc.util.*;

import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.tools.*;

import com.squareup.javapoet.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public abstract class BaseProcessor extends AbstractProcessor{
    public static final String packageName = "unity.gen";

    public static Elements elementUtils;
    public static Filer filer;
    public static Messager messager;
    public static Fi rootDir;

    protected int round;
    protected int rounds;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv){
        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        if(round++ >= rounds) return false;
        if(rootDir == null){
            try{
                String path = Fi.get(filer.getResource(StandardLocation.CLASS_OUTPUT, "no", "no")
                .toUri().toURL().toString().substring(OS.isWindows ? 6 : "file:".length()))
                .parent().parent().parent().parent().parent().parent().parent().toString().replace("%20", " ");

                rootDir = Fi.get(path);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        try{
            process(roundEnv);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return true;
    }

    public abstract void process(RoundEnvironment roundEnv) throws Exception;

    public void write(TypeSpec spec) throws IOException{
        JavaFile.builder(packageName, spec)
        .indent("    ")
            .skipJavaLangImports(true)
        .build().writeTo(filer);
    }

    public TypeName tName(Class<?> type){
        return ClassName.get(type).box();
    }

    public TypeName tName(Element e){
        return TypeName.get(e.asType());
    }

    public ClassName cName(Class<?> type){
        return ClassName.get(type);
    }

    public TypeVariableName tvName(String name, TypeName... bounds){
        return TypeVariableName.get(name, bounds);
    }

    public String lnew(){
        return Character.toString('\n');
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }
}
