package unity.tools;

import arc.util.*;
import unity.*;

public class Generators{
    public static final Generator[] generators = {
        new IconGenerator(),
        new LoadOutlineGenerator(),
        new RotorBlurringGenerator()
    };

    public static void generate(){
        for(Generator generator : generators){
            String name = generator.getClass().getSimpleName();

            Unity.print(Strings.format("Executing '@'...", name));
            Time.mark();

            generator.generate();

            Unity.print(Strings.format("'@' executed for @ms", name, Time.elapsed()));
        }
    }
}
