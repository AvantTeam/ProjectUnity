package unity.annotations;

import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.*;

import com.squareup.javapoet.*;

public abstract class BaseProcessor extends AbstractProcessor{
    public static final String packageName = "unity.gen";

    public static Elements elementUtils;
    public static Filer filer;
    public static Messager messager;

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
        try{
            process(roundEnv);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public abstract void process(RoundEnvironment roundEnv) throws Exception;

    public void write(TypeSpec spec){
        try{
            JavaFile.builder(packageName, spec).skipJavaLangImports(true).build().writeTo(filer);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public TypeName typeName(Class<?> type){
        return ClassName.get(type).box();
    }

    public TypeName typeName(Element e){
        return TypeName.get(e.asType());
    }
}
