package unity.annotations;

import arc.files.*;
import arc.struct.*;
import arc.util.*;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.*;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;

import com.squareup.javapoet.*;
import com.sun.source.util.*;
import com.sun.tools.javac.code.AnnoConstruct;
import com.sun.tools.javac.code.Attribute.*;
import com.sun.tools.javac.model.*;
import com.sun.tools.javac.processing.*;

import unity.annotations.Annotations.AnnotationProxyMaker;

import java.lang.Class;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public abstract class BaseProcessor extends AbstractProcessor{
    public static final String packageName = "unity.gen";

    public static JavacElements elementUtils;
    public static Trees treeUtils;
    public static Types typeUtils;
    public static Filer filer;
    public static Messager messager;
    public static Fi rootDir;

    protected int round;
    protected int rounds = 1;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv){
        super.init(processingEnv);

        JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment)processingEnv;

        elementUtils = javacProcessingEnv.getElementUtils();
        treeUtils = Trees.instance(javacProcessingEnv);
        typeUtils = javacProcessingEnv.getTypeUtils();
        filer = javacProcessingEnv.getFiler();
        messager = javacProcessingEnv.getMessager();
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
            Log.err(e);
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

    public Element toEl(TypeMirror t){
        return typeUtils.asElement(t);
    }

    public Seq<Element> elements(Runnable run){
        try{
            run.run();
        }catch(MirroredTypesException ex){
            return Seq.with(ex.getTypeMirrors()).map(this::toEl);
        }

        return Seq.with();
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

    public ClassName cName(String canonical){
        Matcher matcher = Pattern.compile("\\.[A-Z]").matcher(canonical);
        matcher.find();
        int offset = matcher.start();

        String pkgName = canonical.substring(0, offset);
        Seq<String> simpleNames = Seq.with(canonical.substring(offset + 1).split("\\."));
        simpleNames.reverse();
        String simpleName = simpleNames.pop();
        simpleNames.reverse();

        return ClassName.get(pkgName, simpleName, simpleNames.toArray());
    }

    public ClassName cName(Element e){
        return cName(stripTV(e.asType().toString()));
    }

    public TypeVariableName tvName(String name, TypeName... bounds){
        return TypeVariableName.get(name, bounds);
    }

    public String stripTV(String canonical){
        return canonical.replaceAll("\\<[A-Z]+\\>", "");
    }

    public ClassName withoutTV(TypeElement t){
        return cName(stripTV(t.getQualifiedName().toString()));
    }

    public String lnew(){
        return Character.toString('\n');
    }

    Seq<VariableElement> vars(TypeElement t){
        return Seq.with(t.getEnclosedElements()).select(e -> e instanceof VariableElement).map(e -> (VariableElement)e);
    }

    Seq<ExecutableElement> methods(TypeElement t){
        return Seq.with(t.getEnclosedElements()).select(e -> e instanceof ExecutableElement).map(e -> (ExecutableElement)e);
    }

    Seq<TypeElement> types(TypeElement t){
        return Seq.with(t.getEnclosedElements()).select(e -> e instanceof TypeElement).map(e -> (TypeElement)e);
    }

    public String descString(VariableElement v){
        return v.getEnclosingElement().toString() + "#" + v.getSimpleName().toString();
    }

    public String descString(ExecutableElement m){
        return m.getEnclosingElement().toString() + "#" + m.getSimpleName().toString();
    }

    public boolean is(Element e, Modifier... modifiers){
        for(Modifier m : modifiers){
            if(e.getModifiers().contains(m)){
                return true;
            }
        }
        return false;
    }

    public boolean isConstructor(ExecutableElement e){
        return e.getSimpleName().toString().equals("<init>");
    }

    public String docs(Element e){
        return elementUtils.getDocComment(e) == null ? "" : elementUtils.getDocComment(e);
    }

    ExecutableElement method(TypeElement type, String name, TypeMirror retType, List<? extends VariableElement> params){
        return methods(type).find(m -> {
            List<? extends VariableElement> realParams = m.getParameters();

            return
                m.getSimpleName().toString().equals(name) &&
                typeUtils.isSameType(m.getReturnType(), retType) &&
                realParams.equals(params);
        });
    }

    VariableElement field(TypeElement type, String name, TypeMirror ftype){
        return vars(type).find(f ->
            f.getSimpleName().toString().equals(name) &&
            typeUtils.isSameType(f.asType(), ftype)
        );
    }

    public <A extends Annotation> A annotation(Element e, Class<A> annotation){
        try{
            Method m = AnnoConstruct.class.getDeclaredMethod("getAttribute", Class.class);
            m.setAccessible(true);
            Compound compound = (Compound)m.invoke(e, annotation);
            return compound == null ? null : AnnotationProxyMaker.generateAnnotation(compound, annotation);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }
}
