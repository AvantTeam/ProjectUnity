package unity.annotations;

import arc.struct.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;

import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

import com.squareup.javapoet.*;
import com.sun.source.tree.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({
    "unity.annotations.Annotations.EntityComponent",
    "unity.annotations.Annotations.EntityDef"
})
public class EntityProcessor extends BaseProcessor{
    Seq<TypeElement> comps = new Seq<>();
    ObjectMap<String, TypeElement> compNames = new ObjectMap<>();
    Seq<TypeElement> inters = new Seq<>();
    Seq<Element> defs = new Seq<>();

    StringMap varInitializers = new StringMap();
    StringMap methodBlocks = new StringMap();
    ObjectMap<String, Seq<String>> imports = new ObjectMap<>();
    ObjectMap<TypeElement, String> groups;

    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        comps.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityComponent.class));
        inters.addAll((Set<TypeElement>)roundEnv.getElementsAnnotatedWith(EntityInterface.class));
        defs.addAll(roundEnv.getElementsAnnotatedWith(EntityDef.class));

        if(round == 1){
            groups = ObjectMap.of(
                toComp(Entityc.class), "all",
                toComp(Playerc.class), "player",
                toComp(Bulletc.class), "bullet",
                toComp(Unitc.class), "unit",
                toComp(Buildingc.class), "build",
                toComp(Syncc.class), "sync",
                toComp(Drawc.class), "draw",
                toComp(Firec.class), "fire",
                toComp(Puddlec.class), "puddle",
                toComp(WeatherStatec.class), "weather"
            );

            for(TypeElement inter : (List<TypeElement>)((PackageElement)elementUtils.getPackageElement("mindustry.gen")).getEnclosedElements()){
                if(
                    inter.getSimpleName().toString().endsWith("c") &&
                    inter.getKind() == ElementKind.INTERFACE
                ){
                    inters.add(inter);
                }
            }

            for(TypeElement comp : comps){
                for(ExecutableElement m : methods(comp)){
                    if(is(m, Modifier.ABSTRACT, Modifier.NATIVE)) continue;

                    methodBlocks.put(descString(m), treeUtils.getTree(m).getBody().toString()
                        .replaceAll("this\\.<(.*)>self\\(\\)", "this")
                        .replaceAll("self\\(\\)(?!\\s+instanceof)", "this")
                        .replaceAll(" yield ", "")
                        .replaceAll("\\/\\*missing\\*\\/", "var")
                    );
                }

                for(VariableElement var : vars(comp)){
                    VariableTree tree = (VariableTree)treeUtils.getTree(var);
                    if(tree.getInitializer() != null){
                        varInitializers.put(descString(var), tree.getInitializer().toString());
                    }
                }

                imports.put(interfaceName(comp), getImports(comp));
                compNames.put(comp.getSimpleName().toString(), comp);

                EntityComponent compAnno = annotation(comp, EntityComponent.class);
                if(compAnno.write()){
                    
                }
            }
        }
    }

    TypeElement toComp(TypeElement inter){
        String name = inter.getSimpleName().toString();
        return comps.find(t -> t.getSimpleName().toString().equals(
            name.substring(0, name.length() - 1) + "Comp"
        ));
    }

    TypeElement toComp(Class<?> inter){
        return toComp(elementUtils.getTypeElement(inter.getCanonicalName()));
    }

    String interfaceName(TypeElement type){
        String name = type.getSimpleName().toString();
        if(!name.endsWith("Comp")){
            throw new IllegalStateException("All types annotated with @EntityComp must have 'Comp' as the name's suffix");
        }

        return name.substring(0, name.length() - 4) + "c";
    }

    Seq<String> getImports(Element e){
        return Seq.with(treeUtils.getPath(e).getCompilationUnit().getImports()).map(Object::toString);
    }
}
