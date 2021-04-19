package unity.world.blocks.power;

import arc.struct.*;
import arc.scene.ui.layout.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.ItemStack;
import mindustry.world.*;
import mindustry.world.blocks.power.*;

public class PowerPlant extends PowerGenerator {
    public int steps = 60;

    public PowerPlant(String name){
        super(name);
        hasItems = true;
        acceptsItems = true;
        itemCapacity = 200;
    }

    public class PowerPlantBuilding extends Building{
        public Seq<ItemStack> itemSeq = new Seq<ItemStack>();

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
            /*thirds = (int) steps / 3;
            for(int i = 0; i < steps; i++){
                if()
                itemSeq.add();
            }*/

            return super.init(tile, team, shouldAdd, rotation);
        }

        /*@Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);

            table.pane(e -> {
                for(ItemStack items : itemSeq){

                }
            });
        }*/
    }
}
