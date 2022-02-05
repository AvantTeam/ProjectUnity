package unity.world.blocks.units;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import unity.content.*;
import unity.gen.*;
import unity.type.*;

public class TerraCore extends Block{
    UnityUnitType type = (UnityUnitType)UnityUnitTypes.terra;

    public TerraCore(String name){
        super(name);
        update = true;
        configurable = true;
        hasItems = true;
        itemCapacity = 150;
        separateItemCapacity = true;
        highUnloadPriority = true;
    }

    public class TerraCoreBuild extends Building{
        Worldc unit;

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.units, Styles.clearTransi, () -> {
                Unit u = type.create(team);
                if(u instanceof Worldc){
                    u.x = x;
                    u.y = y;
                    u.rotation = 90f;
                    unit = (Worldc)u;
                    u.add();
                    ((Worldc)u).setup();
                }
            }).size(50f);
        }

        @Override
        public void draw(){
            if(unit == null){
                float z = Draw.z();
                Draw.z(Layer.debris);
                Draw.color(Color.white, 0.2f);

                Draw.rect(type.fullIcon, x, y, 0f);

                Draw.z(z);
                Draw.reset();
            }
            super.draw();
        }

        @Override
        public void updateTile(){
            if(unit != null){
                Item item = unit.item();
                if(items.get(item) < itemCapacity){
                    int amount = acceptStack(unit.item(), unit.stack().amount, unit);
                    if(amount > 0){
                        handleStack(item, amount, unit);
                        unit.stack().amount -= amount;
                    }
                }
            }
        }
    }
}
