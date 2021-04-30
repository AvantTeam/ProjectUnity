package unity.tools;

public class Generators{
    public static final Generator[] generators = {
        new IconGenerator(),
        new LoadOutlineGenerator(),
        new RotorBlurringGenerator()
    };

    public static void generate(){
        for(Generator generator : generators){
            generator.generate();
        }
    }
}
