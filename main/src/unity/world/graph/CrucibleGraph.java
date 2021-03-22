package unity.world.graph;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.world.meta.*;
import unity.world.meta.CrucibleRecipe.*;
import unity.world.blocks.GraphBlockBase.*;
import unity.world.modules.*;

public class CrucibleGraph extends BaseGraph<GraphCrucibleModule, CrucibleGraph>{
    static final float[] capacityMul = new float[]{0f, 0.1f, 0.2f, 0.5f, 1f};
    public final Color color = Color.clear.cpy();
    final Seq<CrucibleData> contains = new Seq<>();
    float totalVolume, totalCapacity, containedAmCache;
    boolean containChanged = true, crafts = true;

    @Override
    public CrucibleGraph create(){
        return new CrucibleGraph();
    }

    public float getVolumeContained(){
        if(containChanged){
            containedAmCache = 0f;
            for(int i = 0, len = contains.size; i < len; i++) containedAmCache += contains.get(i).volume;
        }
        return containedAmCache;
    }

    public boolean addItem(Item item){
        MeltInfo meltProd = MeltInfo.map.get(item);
        if(meltProd == null) return false;
        if(meltProd.additive){
            return addMeltItem(meltProd.additiveID, meltProd.additiveWeight, false);
        }else{
            return addMeltItem(meltProd, 1f, false);
        }
    }

    public CrucibleData getMeltFromID(int id){
        return contains.find(i -> i.id == id);
    }

    public boolean addMeltItem(MeltInfo meltProd, float am, boolean liquid){
        CrucibleData avalslot = null;
        int totalContained = 0;
        
        for(var i : contains){
            if(i.id == meltProd.id) avalslot = i;
            totalContained += i.volume;
        }
        
        if(totalContained + am > totalCapacity) return false;
        if(avalslot != null){
            if(liquid) addLiquidToSlot(avalslot, am);
            else addSolidToSlot(avalslot, am);

        }else{
            contains.add(new CrucibleData(meltProd.id, am, liquid ? 1f : 0f, meltProd.item));
        }
        
        containChanged = true;
        return true;
    }

    public boolean canContainMore(float amount){
        return getVolumeContained() + amount <= totalCapacity;
    }

    public float getRemainingSpace(){
        return Math.max(0, totalCapacity - getVolumeContained());
    }

    void addSolidToSlot(CrucibleData slot, float am){
        float melted = slot.meltedRatio * slot.volume;
        slot.volume += am;
        slot.meltedRatio = melted / slot.volume;
        
        if(slot.volume <= 0f || slot.meltedRatio <= 0f) slot.meltedRatio = 0f;
        containChanged = true;
    }

    public void addLiquidToSlot(CrucibleData slot, float am){
        float melted = slot.meltedRatio * slot.volume + am;
        slot.volume += am;
        slot.meltedRatio = melted / slot.volume;
        
        if(slot.volume <= 0f || slot.meltedRatio <= 0f) slot.meltedRatio = 0f;
        containChanged = true;
    }

    @Override
    void copyGraphStatsFrom(CrucibleGraph graph){}

    @Override
    void updateOnGraphChanged(){
        totalCapacity = 0f;
        crafts = false;
        
        for(var module : connected){ //building
            int bitmask = 0;
            if(!module.initialized()){
                module.tilingIndex = 0;
                return;
            }
            int directNeighbour = 0;
            for(int i = 0; i < 8; i++){
                Tile tile = module.parent.build.tile().nearby(Geometry.d8(i));
                
                if(tile == null || !(tile.build instanceof GraphBuildBase build)) continue;
                
                GraphCrucibleModule conModule = build.crucible(); //crucible building
                if(conModule == null || conModule.dead() || !canConnect(module, conModule)) continue;
                if(i % 2 == 0) directNeighbour++;
                
                bitmask += 1 << i;
            }

            module.tilingIndex = bitmask;
            module.liquidCap = (module.parent.build.block().size == 1 ? capacityMul[directNeighbour] : 1f) * module.graph.baseLiquidCapcity;
            
            totalCapacity += module.liquidCap;
            crafts |= module.graph.doesCrafting;
        }
        if(getVolumeContained() > totalCapacity){
            float decRatio = totalCapacity / getVolumeContained();
            
            for(int i = 0, len = contains.size; i < len; i++) contains.get(i).volume *= decRatio;
            containChanged = true;
        }
    }

    public float getAverageTemp(){
        float speed = 0f;
        int count = 0;
        
        for(var module : connected){ //building
            if(!module.graph.doesCrafting) continue;
            speed += module.parent.build.heat().getTemp();
            count++;
        }
        if(count == 0) return 0f;
        return speed / count;
    }

    float getAverageTempDecay(float meltPoint, float meltSpeed, float tmpDep, float coolDep){
        float speed = 0f;
        int count = 0;
        
        for(var module : connected){ //building
            if(!module.graph.doesCrafting) continue;
            
            float temp = module.parent.build.heat().getTemp();
            
            if(temp > meltPoint){
                speed += (1f + temp / meltPoint * tmpDep) * meltSpeed;
            }else{
                speed -= (1f - temp / meltPoint) * coolDep * meltSpeed;
            }    
            count++;
        }
        if(count == 0) return 0;
        
        return speed / count;
    }

    float getAverageMeltSpeed(MeltInfo m, float tmpDep, float coolDep){
        return getAverageTempDecay(m.meltPoint, m.meltSpeed, tmpDep, coolDep);
    }

    float getAverageMeltSpeedIndex(int index, float tmpDep, float coolDep){
        return getAverageMeltSpeed(MeltInfo.all[index], tmpDep, coolDep);
    }

    public void updateColor(){
        color.set(0f, 0f, 0f);
        float tLiquid = 0f;
        
        for(var i : contains){
            if(i.meltedRatio > 0f){
                float liquidVol = i.meltedRatio * i.volume;
                tLiquid += liquidVol;
                Color itemCol = UnityPal.youngchaGray;
                if(i.item != null) itemCol = i.item.color;
                color.r += itemCol.r * liquidVol;
                color.g += itemCol.g * liquidVol;
                color.b += itemCol.b * liquidVol;
            }
        }
        float invt = 1f / tLiquid;
        color.mul(invt).a(Mathf.clamp(2f * tLiquid / totalCapacity));
    }

    @Override
    void updateGraph(){
        if(contains.isEmpty()) return;
        if(!crafts){
            removeEmptyMelts();
            updateColor();
            return;
        }
        
        float capcityMul = Mathf.sqrt(totalCapacity / 15f);
        
        for(var i : contains){
            float meltMul = Time.delta / i.volume;
            
            if(i.id < MeltInfo.all.length){
                MeltInfo m = MeltInfo.all[i.id];
                i.meltedRatio += meltMul * getAverageMeltSpeed(m, 0.002f, 0.5f) * 0.4f * capcityMul;
                i.meltedRatio = Mathf.clamp(i.meltedRatio);
                
                if(m.evaporationTemp >= 0f){
                    float evap = getAverageTempDecay(m.evaporationTemp, m.evaporation, 0f, 1f);
                    if(evap > 0f){
                        i.volume -= evap;
                        containChanged = true;
                    }
                }
            }
        }
        for(var z : CrucibleRecipe.all){
            boolean valid = true;
            float maxCraftable = 9999999f;
            int len = z.input.length;
            int[] inputSlots = new int[len];
            
            for(int r = 0; r < len; r++){
                boolean found = false;
                for(var ingre : contains){
                    InputRecipe alyInput = z.input[r];
                    if(MeltInfo.all[ingre.id] == alyInput.material && (!alyInput.needsLiquid || ingre.meltedRatio > 0f)){
                        found = true;
                        inputSlots[r] = ingre.id;
                        maxCraftable = Math.min(maxCraftable, (alyInput.needsLiquid ? ingre.meltedRatio : 1f) * ingre.volume / alyInput.amount);
                        break;
                    }
                }
                if(!found){
                    valid = false;
                    break;
                }
            }
            if(valid && maxCraftable > 0f){
                float craftAm = Math.min(maxCraftable, z.alloySpeed * Time.delta * 0.2f * capcityMul);
                if(craftAm <= 0f) return;
                
                for(int r = 0; r < len; r++){
                    InputRecipe alyInput = z.input[r];
                    if(alyInput.needsLiquid){
                        addLiquidToSlot(contains.get(inputSlots[r]), -alyInput.amount * craftAm);
                    }else{
                        contains.get(inputSlots[r]).volume -= alyInput.amount * craftAm;
                        containChanged = true;
                    }
                }
                addMeltItem(z.melt, craftAm, true);
            }
        }
        removeEmptyMelts();
        updateColor();
    }

    void removeEmptyMelts(){
        contains.removeAll(i -> i.volume <= 0f);
    }

    @Override
    void killGraph(){
        for(var module : connected){ //building
            Seq<CrucibleData> nc = new Seq<>();
            float ratio = module.liquidCap / totalCapacity;
            
            for(var i : contains) nc.add(new CrucibleData(i.id, i.volume * ratio, i.meltedRatio, i.item));
            module.propsList.put(module.getPortOfNetwork(this), nc);
        }
        connected.clear();
    }

    @Override
    void updateDirect(){}

    @Override
    void addMergeStats(GraphCrucibleModule module){
        int port = module.getPortOfNetwork(this);
        totalCapacity += module.liquidCap;
        
        var cc = module.propsList.get(port);
        if(cc == null || cc.isEmpty()) return;
        MeltInfo[] melts = MeltInfo.all;
        
        for(var i : cc){
            addMeltItem(melts[i.id], i.volume * (1f - i.meltedRatio), false);
            addMeltItem(melts[i.id], i.volume * i.meltedRatio, true);
        }
    }

    @Override
    void mergeStats(CrucibleGraph graph){
        MeltInfo[] melts = MeltInfo.all;
        totalCapacity += graph.totalCapacity;
        
        for(var i : graph.contains){
            addMeltItem(melts[i.id], i.volume * (1f - i.meltedRatio), false);
            addMeltItem(melts[i.id], i.volume * i.meltedRatio, true);
        }
    }

    public Seq<CrucibleData> contains(){
        return contains;
    }

    public float totalCapacity(){
        return totalCapacity;
    }
}
