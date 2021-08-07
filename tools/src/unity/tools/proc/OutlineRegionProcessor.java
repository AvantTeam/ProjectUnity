package unity.tools.proc;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import unity.gen.*;
import unity.gen.Regions.*;
import unity.tools.*;
import unity.tools.GenAtlas.*;

import java.lang.invoke.*;
import java.util.concurrent.*;

public class OutlineRegionProcessor implements Processor{
    @Override
    public void process(ExecutorService exec){
        for(var field : Regions.class.getFields()){
            Outline anno = field.getAnnotation(Outline.class);
            if(anno == null) continue;

            var name = field.getName();

            GenRegion rawRegion = Reflect.get(Regions.class, name.replace("OutlineRegion", "Region"));

            submit(exec, "Regions.java", () -> {
                var color = Color.valueOf(anno.color());
                int rad = anno.radius();

                var region = new PixmapRegion(rawRegion.pixmap());
                var out = Pixmaps.outline(region, color, rad);

                var outlineRegion = new GenRegion(rawRegion.name + "-outline", out);
                outlineRegion.relativePath = rawRegion.relativePath;
                outlineRegion.save();

                var handle = MethodHandles.publicLookup().unreflectVarHandle(field);
                handle.setVolatile(outlineRegion);
            });
        }
    }
}
