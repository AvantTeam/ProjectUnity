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
import javax.tools.*;

import com.squareup.javapoet.*;
import com.sun.source.util.*;
import com.sun.tools.javac.code.AnnoConstruct;
import com.sun.tools.javac.code.Attribute.*;
import com.sun.tools.javac.model.*;
import com.sun.tools.javac.processing.*;

import unity.annotations.Annotations.AnnotationProxyMaker;

import java.lang.Class;

/** @author GlennFolker */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public abstract class BaseProcessor extends AbstractProcessor{
    public static final String packageName = "unity.gen";

    public static JavacElements elementUtils;
    public static Trees treeUtils;
    public static JavacTypes typeUtils;
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

    public void write(TypeSpec spec) throws Exception{
        write(spec, Seq.with());
    }

    public void write(TypeSpec spec, Seq<String> imports) throws Exception{
        try{
            JavaFile file = JavaFile.builder(packageName, spec)
                .indent("    ")
                .skipJavaLangImports(true)
            .build();

            if(imports == null || imports.isEmpty()){
                file.writeTo(filer);
            }else{
                imports.distinct();

                Seq<String> statics = imports.select(i -> i.contains("import static ")).sort();
                imports = imports.select(i -> !statics.contains(s -> s.equals(i))).sort();
                if(!statics.isEmpty()){
                    imports = statics.addAll("\n").and(imports);
                }

                String rawSource = file.toString();
                Seq<String> source = Seq.with(rawSource.split("\n", -1));
                Seq<String> result = new Seq<>();
                for(int i = 0; i < source.size; i++){
                    String s = source.get(i);

                    result.add(s);
                    if(s.startsWith("package ")){
                        source.remove(i + 1);
                        result.add("");
                        for(String im : imports){
                            result.add(im.replace("\n", ""));
                        }
                    }
                }

                String out = result.toString("\n");
                JavaFileObject object = filer.createSourceFile(file.packageName + "." + file.typeSpec.name, file.typeSpec.originatingElements.toArray(new Element[0]));
                OutputStream stream = object.openOutputStream();
                stream.write(out.getBytes());
                stream.close();
            }
        }catch(Exception e){
            if(!(e instanceof FilerException && e.getMessage().contains("Attempt to recreate a file for type"))){
                throw e;
            }
        }
    }

    public static TypeElement toEl(TypeMirror t){
        return (TypeElement)typeUtils.asElement(t);
    }

    public static Seq<Element> elements(Runnable run){
        try{
            run.run();
        }catch(MirroredTypesException ex){
            return Seq.with(ex.getTypeMirrors()).map(BaseProcessor::toEl);
        }

        return Seq.with();
    }

    public static TypeName tName(Class<?> type){
        return ClassName.get(type).box();
    }

    public static TypeName tName(Element e){
        return TypeName.get(e.asType());
    }

    public static ClassName cName(Class<?> type){
        return ClassName.get(type);
    }

    public static ClassName cName(String canonical){
        canonical = canonical.replace("<any?>", "unity.gen");

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

    public static ClassName cName(Element e){
        return cName(stripTV(e.asType().toString()));
    }

    public static TypeVariableName tvName(String name, TypeName... bounds){
        return TypeVariableName.get(name, bounds);
    }

    public static String stripTV(String canonical){
        return canonical.replaceAll("<[A-Z]+>", "");
    }

    public static String lnew(){
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
        String params = Arrays.toString(m.getParameters().toArray());
        params = params.substring(1, params.length() - 1);

        return m.getEnclosingElement().toString() + "#" + simpleName(m) + "(" + params + ")";
    }

    public boolean is(Element e, Modifier... modifiers){
        for(Modifier m : modifiers){
            if(e.getModifiers().contains(m)){
                return true;
            }
        }
        return false;
    }

    public static boolean isConstructor(ExecutableElement e){
        return
            simpleName(e).equals("<init>") ||
            simpleName(e).equals("<clinit>");
    }

    boolean hasMethod(TypeElement type, ExecutableElement method){
        for(; !(type.getSuperclass() instanceof NoType); type = toEl(type.getSuperclass())){
            if(method(type, simpleName(method), method.getReturnType(), method.getParameters()) != null){
                return true;
            }
        }
        return false;
    }

    ExecutableElement method(TypeElement type, String name, TypeMirror retType, List<? extends VariableElement> params){
        return methods(type).find(m -> {
            List<? extends VariableElement> realParams = m.getParameters();

            return
                simpleName(m).equals(name) &&
                (retType == null || typeUtils.isSameType(m.getReturnType(), retType)) &&
                paramEquals(realParams, params);
        });
    }

    boolean paramEquals(List<? extends VariableElement> first, List<? extends VariableElement> second){
        if(first.size() != second.size()) return false;

        boolean same = true;
        for(int i = 0; same && i < first.size(); i++){
            VariableElement a = first.get(i);
            VariableElement b = second.get(i);

            if(!typeUtils.isSameType(a.asType(), b.asType())) same = false;
        }

        return same;
    }

    Seq<String> getImports(Element e){
        return Seq.with(treeUtils.getPath(e).getCompilationUnit().getImports()).map(Object::toString);
    }

    public static String getDefault(String value){
        switch(value){
            case "float":
            case "double":
            case "int":
            case "long":
            case "short":
            case "char":
            case "byte":
                return "0";
            case "boolean":
                return "false";
            default:
                return "null";
        }
    }

    public static boolean instanceOf(String type, String other){
        TypeElement a = elementUtils.getTypeElement(type);
        TypeElement b = elementUtils.getTypeElement(other);
        return a != null && b != null && typeUtils.isSubtype(a.asType(), b.asType());
    }

    public static boolean isPrimitive(String type){
        return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
        || type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
    }

    public static <A extends Annotation> A annotation(Element e, Class<A> annotation){
        try{
            Method m = AnnoConstruct.class.getDeclaredMethod("getAttribute", Class.class);
            m.setAccessible(true);
            Compound compound = (Compound)m.invoke(e, annotation);
            return compound == null ? null : AnnotationProxyMaker.generateAnnotation(compound, annotation);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static TypeElement toType(Class<?> type){
        return elementUtils.getTypeElement(type.getCanonicalName());
    }

    public static String fullName(Element e){
        return e.asType().toString();
    }

    public static String simpleName(Element e){
        return simpleName(e.getSimpleName().toString());
    }

    public static String simpleName(String canonical){
        if(canonical.contains(".")){
            canonical = canonical.substring(canonical.lastIndexOf(".") + 1);
        }
        return canonical;
    }

    public static String simpleString(ExecutableElement e){
        return simpleName(e) + "(" + Seq.with(e.getParameters()).toString(", ", p -> simpleName(p.asType().toString())) + ")";
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }
}
