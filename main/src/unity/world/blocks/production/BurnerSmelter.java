package unity.world.blocks.production;

import arc.math.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import unity.content.*;

import static arc.Core.bundle;

public class BurnerSmelter extends StemGenericSmelter{
    public Item input;
    public float minEfficiency = 0.6f, boostScale = 1.25f, boostConstant = -0.75f;

    public BurnerSmelter(String name){
        super(name);
        
        preserveUpdate = false;
    }

    @Override
    public void init(){
        if(!consumes.has(ConsumeType.item)) consumes.add(new ConsumeItemFilter(item -> getItemEfficiency(item) > minEfficiency)).update(false).optional(true, false);
        if(input == null) input = UnityItems.stone;
        
        super.init();
    }

    @Override
    public void setBars(){
        super.setBars();
        
        bars.add("efficiency", (BurnerSmelterBuild build) -> new Bar(() -> bundle.format("bar.efficiency", (int)(100 * build.productionEfficiency)), () -> Pal.lighterOrange, () -> build.productionEfficiency));
    }

    @Override
    public void setStats(){
        stats.add(Stat.input, input);
        
        super.setStats();
    }

    protected float getItemEfficiency(Item item){
        return item.flammability;
    }

    public class BurnerSmelterBuild extends StemSmelterBuild{
        public float itemDuration, productionEfficiency;

        @Override
        public void updateTile(){
            if(items.has(input) && itemDuration > 0f){
                progress += getProgressIncrease(craftTime) * productionEfficiency;
                itemDuration -= delta();
                
                totalProgress += delta();
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);
                
                if(Mathf.chanceDelta(updateEffectChance)) updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4f));
            }else{
                if(itemDuration <= 0f){
                    productionEfficiency = 0f;
                    
                    if(items.has(input) && consValid()){
                        int temp = items.nextIndex(-1);
                        
                        if(temp == input.id) temp = items.nextIndex(temp);
                        if(temp != input.id){
                            Item item = items.takeIndex(temp);
                            productionEfficiency = getItemEfficiency(item) * boostScale + boostConstant;
                            
                            items.remove(item, 1);
                            itemDuration = craftTime;
                        }
                    }
                }else{
                    itemDuration -= delta();
                }
                
                warmup = Mathf.lerp(warmup, 0f, 0.02f);
            }
            if(progress >= 1f){
                items.remove(input, 1);
                
                if(outputLiquid != null) handleLiquid(this, outputLiquid.liquid, outputLiquid.amount);
                
                craftEffect.at(x, y);
                progress = 0f;
            }
            
            if(outputLiquid != null) dumpLiquid(outputLiquid.liquid);
            
            super.updateTile();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return (block.consumes.itemFilters.get(item.id) || item == input) && items.get(item) < getMaximumAccepted(item);
        }
    }
}
