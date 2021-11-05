package unity.async;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import mindustry.*;
import mindustry.async.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import unity.*;

import static mindustry.Vars.*;
import static mindustry.ctype.ContentType.*;

@SuppressWarnings("unchecked")
public class ContentScoreProcess implements AsyncProcess{
    static final float stackConstant = 2.5f;
    static final int maxDepth = 128;
    final Seq<ContentScore> unloaded = new Seq<>(), allScores = new Seq<>();
    final Seq<Floor> ores = new Seq<>();
    volatile boolean processing = false, finished = false;
    EnumSet<ContentType> blackListed = EnumSet.of(mech_UNUSED, weather, effect_UNUSED, sector, loadout_UNUSED, typeid_UNUSED, error, planet, ammo_UNUSED);
    ContentScore[][] scores;
    int depth;
    boolean depthLoaded;

    @Override
    public void process(){
        if(!finished){
            processing = true;
            long lt = System.nanoTime();

            ContentLoader l = content;

            Core.app.post(() -> Unity.print("Content Scoring Begin"));

            scores = new ContentScore[all.length][0];
            for(int i = 0; i < all.length; i++){
                if(blackListed.contains(all[i])) continue;
                Seq<Content> c = l.getContentMap()[i];
                scores[i] = new ContentScore[c.size];
                for(Content cs : c){
                    if(all[i] == block && (!((Block)cs).synthetic() || cs instanceof ConstructBlock)){
                        if(cs instanceof Floor && (((Floor)cs).itemDrop != null || ((Floor)cs).liquidDrop != null)){
                            ores.add(((Floor)cs));
                        }
                        continue;
                    }
                    scores[i][cs.id] = new ContentScore(cs);
                }
            }

            for(Floor ore : ores){
                if(ore.itemDrop != null){
                    ContentScore cs = get(ore.itemDrop);
                    cs.artificial = false;
                }
                if(ore.liquidDrop != null){
                    ContentScore cs = get(ore.liquidDrop);
                    cs.artificial = false;
                }
            }

            Core.app.post(() -> Unity.print("Content Score Processing Begin"));

            for(ContentScore score : allScores){
                if(score.artificial) processContent(score);
            }
            unloaded.removeAll(cs -> {
                if(!cs.loaded){
                    resetDepth();
                    cs.loadScore();
                }

                return cs.loaded;
            });
            clear();

            float time = Time.nanosToMillis(System.nanoTime() - lt);
            StringBuilder builder = new StringBuilder(64);

            for(ContentType type : all){
                if(!blackListed.contains(type)){
                    builder.append(type.toString()).append(":\n\n");
                    for(Content c : content.getContentMap()[type.ordinal()]){
                        ContentScore cs = get(c);
                        if(cs == null) continue;
                        builder.append("  ").append(cs.toString()).append("\n");
                    }
                }
            }
            builder.append("Content Processed in: ").append(time);
            String out = builder.toString();
            Core.app.post(() -> Unity.print(out));

            processing = false;
            finished = true;
        }
    }

    void clear(){
        for(ContentScore sc : allScores){
            sc.consumesScore = null;
            sc.crafterRequirements = null;
            sc.outputs = null;
        }
    }

    void resetDepth(){
        depth = 0;
        depthLoaded = true;
    }

    ContentScore get(Content content){
        return scores[content.getContentType().ordinal()][content.id];
    }

    ContentScore get(ContentType type, short id){
        return scores[type.ordinal()][id];
    }

    <T extends Content> T getc(int type, short id){
        return (T)content.getContentMap()[type].get(id);
    }

    float getItemScore(Item item){
        float energyScore = Mathf.sqr(item.charge + item.explosiveness + item.flammability + item.radioactivity);
        return (float)Math.pow(Math.max(item.hardness + 1, 1), 1.5) * Math.max(item.cost + energyScore, 0.1f) * 1.5f;
    }

    float getLiquidScore(Liquid liquid){
        return Mathf.sqr(liquid.flammability + liquid.explosiveness + Math.abs((liquid.temperature - 0.5f) * 2f)) + liquid.heatCapacity;
    }

    float getItemStackScore(ItemStack stack){
        return get(stack.item).loadScore() * Mathf.pow(stack.amount, 1f / stackConstant);
    }

    float getItemStackScore(ItemStack stack, int size){
        return get(stack.item).loadScore() * Mathf.pow(stack.amount, 1f / (stackConstant / size));
    }

    float getItemStackScore(short id, short amount){
        return get(content.getByID(item, id)).loadScore() * Mathf.pow(amount, 1f / stackConstant);
    }

    Item getMaxFilter(ShortSeq seq){
        Item tmp = null;
        float last = 0f;

        for(int i = 0; i < seq.size; i++){
            ContentScore cs = get(item, seq.get(i));
            if(cs.loadScore() > last){
                tmp = cs.as();
                last = cs.score;
            }
        }

        return tmp;
    }

    Liquid getMaxFilterLiquid(ShortSeq seq){
        Liquid tmp = null;
        float last = 0f;

        for(int i = 0; i < seq.size; i++){
            ContentScore cs = get(liquid, seq.get(i));
            if(cs.loadScore() > last){
                tmp = cs.as();
                last = cs.score;
            }
        }

        return tmp;
    }

    void processContent(ContentScore c){
        if(c.content instanceof Block){
            Block b = (Block)c.content;
            c.outputs.add(new BlockOutput(b));

            if(b.consumes.has(ConsumeType.item)){
                Consume con = b.consumes.get(ConsumeType.item);
                if(con instanceof ConsumeItemFilter){
                    ConsumeItemFilter cons = (ConsumeItemFilter)con;
                    c.itemFilter(cons.filter);
                }else if(con instanceof ConsumeItems){
                    ConsumeItems cons = (ConsumeItems)con;
                    for(ItemStack stack : cons.items){
                        c.addItemConsumes(stack.item, stack.amount);
                    }
                }
                if(c.consumesScore != null) c.consumesScore.optional |= con.optional ? 1 : 0;
            }
            if(b.consumes.has(ConsumeType.liquid)){
                Consume con = b.consumes.get(ConsumeType.liquid);
                if(con instanceof ConsumeLiquidFilter){
                    c.liquidFilter(((ConsumeLiquidFilter)con).filter, ((ConsumeLiquidFilter)con).amount);
                }else if(con instanceof ConsumeLiquid){
                    ConsumeLiquid cons = (ConsumeLiquid)con;
                    c.addLiquidConsume(cons.liquid, cons.amount);
                }
                if(c.consumesScore != null) c.consumesScore.optional |= con.optional ? 2 : 0;
            }
            if(b.consumes.has(ConsumeType.power)){
                Consume con = b.consumes.get(ConsumeType.power);
                if(con instanceof ConsumePower){
                    c.setPower(((ConsumePower)con).usage);
                }
                if(c.consumesScore != null) c.consumesScore.optional |= con.optional ? 4 : 0;
            }

            if(c.content instanceof GenericCrafter){
                GenericCrafter g = (GenericCrafter)c.content;
                float output = 0;

                if(g.outputItems != null){
                    for(ItemStack stack : g.outputItems){
                        output += stack.amount;
                    }
                }
                if(g.outputLiquid != null){
                    output += g.outputLiquid.amount;
                }

                if(g.outputItems != null){
                    for(ItemStack stack : g.outputItems){
                        ContentScore cs = get(stack.item);

                        CrafterRequirements req = new CrafterRequirements(g);
                        req.outputAmount = output;
                        req.setConsumes(c.consumesScore);

                        cs.crafterRequirements.add(req);
                    }
                }
                if(g.outputLiquid != null){
                    ContentScore cs = get(g.outputLiquid.liquid);

                    CrafterRequirements req = new CrafterRequirements(g);
                    req.outputAmount = output;
                    req.setConsumes(c.consumesScore);

                    cs.crafterRequirements.add(req);
                }
            }else if(c.content instanceof UnitFactory){
                UnitFactory f = c.as();
                for(int i = 0; i < f.plans.size; i++){
                    UnitPlan p = f.plans.get(i);
                    ContentScore cs = get(p.unit);

                    UnitRequirements ur = new UnitRequirements(f);
                    ur.time = p.time;
                    ur.setConsumes(c.consumesScore);
                    for(ItemStack stack : p.requirements){
                        ur.addItems(stack.item, (short)stack.amount);
                    }

                    cs.crafterRequirements.add(ur);
                }
            }else if(c.content instanceof Reconstructor){
                Reconstructor r = c.as();
                for(UnitType[] upgrade : r.upgrades){
                    ContentScore cs = get(upgrade[1]);

                    UnitRequirements ur = new UnitRequirements(r);
                    ur.prev = upgrade[0];
                    ur.time = r.constructTime;
                    ur.setConsumes(c.consumesScore);

                    cs.crafterRequirements.add(ur);
                }
            }
        }
    }

    @Override
    public boolean shouldProcess(){
        return !processing && !finished;
    }

    private class ContentScore{
        Content content;
        boolean loaded = false, outputLoaded = false, artificial = true, processing = false;
        float score, outputScore;

        Seq<CrafterScore> crafterRequirements = new Seq<>();
        Seq<OutputHandler> outputs = new Seq<>();
        ConsumesScore consumesScore;

        ContentScore(Content content){
            this.content = content;
            unloaded.add(this);
            allScores.add(this);
        }

        <T extends Content> T as(){
            return (T)content;
        }

        void liquidFilter(Boolf<Liquid> filter, float amount){
            if(consumesScore != null && consumesScore.liquidFilter != null) return;
            if(consumesScore == null) consumesScore = new ConsumesScore();
            consumesScore.liquidFilter = new ShortSeq();
            consumesScore.liquidAmount = amount;
            ShortSeq liquidFilter = consumesScore.liquidFilter;

            for(Liquid liquid : Vars.content.liquids()){
                if(filter.get(liquid)) liquidFilter.add(liquid.id);
            }
        }

        void itemFilter(Boolf<Item> filter){
            if(consumesScore != null && consumesScore.itemFilter != null) return;
            if(consumesScore == null) consumesScore = new ConsumesScore();
            consumesScore.itemFilter = new ShortSeq();
            ShortSeq itemFilter = consumesScore.itemFilter;

            for(Item item : Vars.content.items()){
                if(filter.get(item)) itemFilter.add(item.id);
            }
        }

        void addItemConsumes(Item item, int amount){
            if(consumesScore == null) consumesScore = new ConsumesScore();
            if(consumesScore.itemConsumes == null) consumesScore.itemConsumes = new ShortSeq();
            consumesScore.itemConsumes.add(item.id, (short)amount);
        }

        void addLiquidConsume(Liquid liquid, float amount){
            if(consumesScore == null) consumesScore = new ConsumesScore();
            consumesScore.liquid = liquid.id;
            consumesScore.liquidAmount = amount;
        }

        void setPower(float amount){
            if(consumesScore == null) consumesScore = new ConsumesScore();
            consumesScore.power = amount;
        }

        float loadOutputScore(){
            if(outputLoaded) return outputScore;
            if(!outputs.isEmpty()){
                for(OutputHandler output : outputs){
                    outputScore = Math.max(outputScore, output.calculateOutput());
                }
            }
            outputLoaded = depthLoaded;
            return outputScore;
        }

        float loadScore(){
            if(processing) return 0f;
            if(loaded) return score;

            depth++;
            if(depth > maxDepth || !depthLoaded){
                depthLoaded = false;
                loaded = false;
                return 0f;
            }

            processing = true;

            if(artificial){
                float ns = 0f;
                if(content instanceof Block){
                    Block block = (Block)content;
                    for(ItemStack stack : block.requirements){
                        ns += getItemStackScore(stack, block.size);
                    }
                    ns *= block.buildCostMultiplier;
                }else if(!crafterRequirements.isEmpty()){
                    for(CrafterScore s : crafterRequirements){
                        ns = Math.max(ns, s.calculateScore());
                    }
                }
                score = ns;
            }else{
                if(content instanceof Item){
                    score = outputScore = getItemScore((Item)content);
                }else if(content instanceof Liquid){
                    score = outputScore = getLiquidScore((Liquid)content);
                }
            }
            loadOutputScore();

            loaded = depthLoaded;
            processing = false;

            return score;
        }

        @Override
        public String toString(){
            return content.toString() + ": Score: " + score + ", Output Score: " + outputScore;
        }
    }

    private class CrafterRequirements extends CrafterScore{
        GenericCrafter crafter;
        float outputAmount = 1f;

        CrafterRequirements(GenericCrafter crafter){
            this.crafter = crafter;
        }

        @Override
        float calculateScore(){
            if(score == -1f){
                score = 0f;
                if(itemStacks != null){
                    for(int i = 0; i < itemStacks.size; i += 2){
                        score += getItemStackScore(itemStacks.get(i), itemStacks.get(i + 1));
                    }
                }
                if(liquid != null){
                    score += get(liquid).loadScore() * liquidAmount;
                }
                score += power;
                score /= outputAmount;
                score += get(crafter).loadScore() / 110f;
                score *= Mathf.sqrt(Math.max(crafter.craftTime, 0.1f) / 60f);
            }

            return score;
        }
    }

    private class UnitRequirements extends CrafterScore{
        UnitBlock block;
        UnitType prev;
        float time = 0f;

        UnitRequirements(UnitBlock block){
            this.block = block;
        }

        @Override
        float calculateScore(){
            if(score == -1f){
                score = 0f;
                if(itemStacks != null){
                    for(int i = 0; i < itemStacks.size; i += 2){
                        score += getItemStackScore(itemStacks.get(i), itemStacks.get(i + 1));
                    }
                }
                if(liquid != null){
                    score += get(liquid).loadScore() * liquidAmount;
                }
                score += power;

                if(prev != null){
                    score += get(prev).loadScore();
                }
                score *= Mathf.sqrt(Math.max(time, 0.1f) / 60f);

                score += get(block).loadScore() / 110f;
            }
            return score;
        }
    }

    abstract class CrafterScore{
        float score = -1f;
        ShortSeq itemStacks;
        Liquid liquid;
        float liquidAmount;
        float power;

        abstract float calculateScore();

        void addItems(Item item, short amount){
            if(itemStacks == null) itemStacks = new ShortSeq();
            itemStacks.add(item.id, amount);
        }

        void setConsumes(ConsumesScore cons){
            if(cons == null) return;

            if(cons.itemFilter != null && !cons.itemFilter.isEmpty()){
                Item i = getMaxFilter(cons.itemFilter);
                if(i != null){
                    itemStacks = new ShortSeq();
                    itemStacks.add(i.id, (short)1);
                }
            }else{
                itemStacks = cons.itemConsumes;
            }

            if(cons.liquidFilter != null && !cons.liquidFilter.isEmpty()){
                liquid = getMaxFilterLiquid(cons.liquidFilter);
            }else if(cons.liquid != -1){
                liquid = getc(ContentType.liquid.ordinal(), cons.liquid);
            }
            liquidAmount = cons.liquidAmount;

            power = cons.power;
        }
    }

    class BlockOutput implements OutputHandler{
        Block block;
        float score;
        boolean loaded;

        public BlockOutput(Block b){
            block = b;
        }

        @Override
        public float calculateOutput(){
            if(loaded) return this.score;
            ContentScore cs = get(block);
            float conScore = (cs.consumesScore != null ? cs.consumesScore.score() : 0f);
            float consTime = 5f;
            float score = (block.health / (float)block.size);

            if(block.hasItems){
                score += block.itemCapacity * (1f + block.baseExplosiveness);
            }
            if(block.hasLiquids){
                score += block.liquidCapacity * block.liquidPressure * (1f + block.baseExplosiveness);
            }

            if(block instanceof Wall){
                Wall w = (Wall)block;
                float ls = score;
                score /= 4f;
                if(w.chanceDeflect > 0f) score += ls * w.chanceDeflect;
                if(w.lightningChance > 0f) score += w.lightningChance * (w.lightningLength / 4f) * w.lightningDamage;
            }else if(block instanceof Turret){
                Turret t = (Turret)block;
                int shots = t.alternate ? 1 : t.shots;
                float inaccuracy = 1f - ((t.inaccuracy / 180f) / (shots * shots));

                if(block instanceof ItemTurret){
                    ItemTurret it = (ItemTurret)block;
                    float bs = 0f;
                    for(Entry<Item, BulletType> entry : it.ammoTypes){
                        bs = Math.max(bs, get(entry.value).loadOutputScore());
                    }
                    score += (bs * shots * inaccuracy) / t.reloadTime;
                }else if(block instanceof PowerTurret){
                    PowerTurret pt = (PowerTurret)block;
                    score += (get(pt.shootType).loadOutputScore() * shots * inaccuracy) / t.reloadTime;
                }
            }else if(block instanceof MendProjector){
                MendProjector mp = (MendProjector)block;
                float rr = (mp.range + mp.phaseRangeBoost) / 8f;

                score += ((((mp.healPercent + mp.phaseBoost) / 100f) * score) * rr * rr) / mp.reload;
            }else if(block instanceof OverdriveProjector){
                OverdriveProjector op = (OverdriveProjector)block;
                float rr = (op.range + op.phaseRangeBoost) / 8f;

                score += ((((op.speedBoostPhase + op.speedBoostPhase) / 100f) * score) * rr * rr) / op.reload;
            }else if(block instanceof PowerGenerator){
                float power = ((PowerGenerator)block).powerProduction;

                if(block instanceof ItemLiquidGenerator){
                    ItemLiquidGenerator ilg = (ItemLiquidGenerator)block;
                    consTime = (ilg.itemDuration + ilg.maxLiquidGenerate);
                }
                score += power;
            }else if(block instanceof GenericCrafter){
                GenericCrafter gc = (GenericCrafter)block;
                if(gc.outputItems != null){
                    for(ItemStack item : gc.outputItems){
                        score += get(item.item).loadScore();
                    }
                }
                if(gc.outputLiquid != null){
                    score += get(gc.outputLiquid.liquid).loadScore();
                }
            }
            score /= (conScore / consTime) + 1f;
            loaded = depthLoaded;
            this.score = score;

            return score;
        }
    }

    interface OutputHandler{
        float calculateOutput();
    }

    private class ConsumesScore{
        ShortSeq itemConsumes, itemFilter, liquidFilter;
        short liquid = -1;
        float liquidAmount = 0f, power, score = -1f;
        byte optional = 0;

        float score(){
            if(score == -1f){
                float is = 0f, ls = 0f;
                if(itemConsumes != null){
                    for(int i = 0; i < itemConsumes.size; i += 2){
                        is += getItemStackScore(itemConsumes.get(i), itemConsumes.get(i + 1));
                    }
                }else if(itemFilter != null){
                    is += get(getMaxFilter(itemFilter)).loadScore();
                }
                if((optional & 1) != 0){
                    is /= 10f;
                }

                if(liquid != -1){
                    ls += get(ContentType.liquid, liquid).loadScore() * liquidAmount;
                }else if(liquidFilter != null){
                    ls += get(getMaxFilterLiquid(liquidFilter)).loadScore() * liquidAmount;
                }
                if((optional & 2) != 0){
                    ls /= 10f;
                }
                float p = (optional & 4) != 0 ? power / 10f : power;

                score = is + ls + p;
            }
            return score;
        }
    }
}
