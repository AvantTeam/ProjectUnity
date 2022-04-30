package unity.world.blocks.exp;

import arc.Core;
import mindustry.ctype.ContentType;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Stat;
import unity.graphics.UnityPal;

public class KoruhVault extends StorageBlock {
    public int expCap = 500;

    public KoruhVault(String name) {
        super(name);
        update = sync = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.expAmount", expCap));
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("exp", (KoruhCrafter.KoruhCrafterBuild entity) -> new Bar(() -> Core.bundle.get("bar.exp"), () -> UnityPal.exp, entity::expf));
    }

    public class KoruhVaultBuild extends StorageBuild implements ExpHolder{
        public int exp;

        @Override
        public int getExp(){
            return exp;
        }

        @Override
        public int handleExp(int amount){
            if(amount > 0){
                int e = Math.min(expCap - exp, amount);
                exp += e;
                return e;
            } else{
                int e = Math.min(-amount, exp);
                exp -= e;
                return -e;
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.total() < itemCapacity;
        }
    }
}
