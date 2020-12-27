package unity.younggamExperimental;

import mindustry.type.*;

public class CrucibleRecipe{
    public static final CrucibleRecipe[] all = new CrucibleRecipe[5];
    private static byte total;

    public final MeltInfo melt;
    public final InputRecipe[] input;
    public final float alloySpeed;

    public CrucibleRecipe(MeltInfo melt, float alloySpeed, InputRecipe... input){
        this.melt = melt;
        this.alloySpeed = alloySpeed;
        this.input = input;
        all[total++] = this;
    }

    public static byte total(){
        return total;
    }

    public static class InputRecipe{
        public final MeltInfo material;
        public final float amount;
        public final boolean needsLiquid;

        public InputRecipe(MeltInfo material, float amount, boolean needsLiquid){
            this.material = material;
            this.amount = amount;
            this.needsLiquid = needsLiquid;
        }

        public InputRecipe(MeltInfo material, float amount){
            this(material, amount, true);
        }
    }
}
