package unity.async;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import mindustry.async.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import unity.*;

import java.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class ContentScoreProcess implements AsyncProcess{
    private static final int iterationError = 5, skipIteration = 7;
    private boolean init = false, processing, finished = false;
    private final Seq<ContentScore> unloaded = new Seq<>();
    private final IntSet[] unloadedSet = new IntSet[ContentType.all.length];
    private Seq<Content>[][] origins;
    private ContentScore[][] items;

    public ContentScoreProcess(){
        Events.on(EventType.ContentInitEvent.class, event -> init = true);
    }

    @Override
    public void process(){
        if(init){
            processing = true;
            Core.app.post(() -> Unity.print("Content Score Begin"));
            items = new ContentScore[ContentType.all.length][0];
            origins = new Seq[ContentType.all.length][0];
            for(ContentType type : ContentType.all){
                switch(type){
                    case sector, ammo, weather, planet, loadout_UNUSED, effect_UNUSED, mech_UNUSED, typeid_UNUSED, error -> {}
                    case block -> {
                        origins[type.ordinal()] = new Seq[content.getBy(type).size];
                        items[type.ordinal()] = new ContentScore[content.getBy(type).size];
                        for(Content c : content.getBy(type)){
                            if(c instanceof Block b && b.synthetic()) addContent(new ContentScore(c));
                        }
                    }
                    case bullet -> {
                        origins[type.ordinal()] = new Seq[content.getBy(type).size];
                        items[type.ordinal()] = new ContentScore[content.getBy(type).size];
                        for(Content c : content.getBy(type)){
                            bulletScore((BulletType)c);
                        }
                    }
                    default -> {
                        origins[type.ordinal()] = new Seq[content.getBy(type).size];
                        items[type.ordinal()] = new ContentScore[content.getBy(type).size];
                        for(Content c : content.getBy(type)){
                            addContent(new ContentScore(c));
                        }
                    }
                }
            }
            for(ContentType type : ContentType.all){
                //items[type.ordinal()] = new ContentScore[content.getBy(type).size];
                for(Content content : content.getBy(type)){
                    switch(type){
                        case block -> handleBlock((Block)content);
                        case item -> handleItem((Item)content, false);
                        case liquid -> handleLiquid((Liquid)content, true);
                        case bullet, sector, ammo, weather, planet, loadout_UNUSED, effect_UNUSED, mech_UNUSED, typeid_UNUSED, error -> {}
                        default -> {
                            ContentScore cs = get(content);
                            cs.loaded = false;
                            addUnloaded(cs);
                        }
                    }
                }
            }
            int l = 0;
            int e = 0;
            int er = 0;
            while(unloaded.size > 0 && e < iterationError){
                if(l == unloaded.size){
                    e++;
                    er++;
                }else{
                    e = 0;
                }
                l = unloaded.size;
                unloaded.removeAll(cs -> {
                    cs.updateScore();
                    return cs.loaded;
                });
            }
            int er2 = er;

            init = false;
            finished = true;
            Core.app.post(() -> Unity.print("Content Score Complete: " + er2));
            StringBuilder out = new StringBuilder(64);
            int s = 0;
            int v;
            for(ContentScore[] i : items){
                v = 0;
                out.append(ContentType.all[s].name()).append(":\n");
                for(ContentScore score : i){
                    if(score == null) continue;
                    out.append(score.content.toString()).append(": ")
                    .append("Score: ").append(score.loaded ? score.score : "unloaded")
                    .append(", Output Score: ").append(score.loaded ? score.outputScore : "unloaded")
                    .append(", Artificial: ").append(score.artificial)
                    .append("\n")
                    .append("Sources: ");
                    if(score.origins() != null){
                        for(Content c : score.origins()){
                            out.append(c.toString()).append(":").append(get(c).loaded).append(",");
                        }
                    }else{
                        out.append("No Origins");
                    }
                    out.append("\n");
                    v++;
                }
                out.append("Contents: ").append(v).append("\n\n");
                s++;
            }
            String out2 = out.toString();
            Core.app.post(() -> Unity.print(out2));
            processing = false;
        }
    }

    Seq<Liquid> liquids(){
        return content.liquids();
    }

    Seq<Item> items(){
        return content.items();
    }

    public float getScore(Content content){
        if(!finished) return 0f;
        return get(content).score;
    }

    public float getFractScore(Content content){
        if(!finished) return 0f;
        return get(content).fractScore();
    }

    private Seq<Content> getOrigins(Content content){
        return origins[content.getContentType().ordinal()][content.id];
    }

    private void addOrigin(Content content, Content other){
        if(origins[content.getContentType().ordinal()][content.id] == null){
            origins[content.getContentType().ordinal()][content.id] = new Seq<>();
        }
        Seq<Content> origins = getOrigins(content);
        if(!origins.contains(other)){
            origins.add(other);
        }
    }

    private ContentScore get(int id, ContentType type){
        return items[type.ordinal()][id];
    }

    private ContentScore get(Content content){
        return get(content.id, content.getContentType());
    }

    private void updateSize(Content content){
        int a = content.id + 1, i = content.getContentType().ordinal();
        if(a >= items[i].length){
            items[i] = Arrays.copyOf(items[i], a);
        }
    }

    private void addUnloaded(ContentScore content){
        int key = content.content.id;
        if(unloadedSet[content.content.getContentType().ordinal()] == null) unloadedSet[content.content.getContentType().ordinal()] = new IntSet(204);
        if(unloadedSet[content.content.getContentType().ordinal()].add(key)){
            unloaded.add(content);
        }
    }

    private void addContent(ContentScore content){
        updateSize(content.content);
        items[content.content.getContentType().ordinal()][content.content.id] = content;
    }

    private boolean contains(Content c){
        if(c.id >= items[c.getContentType().ordinal()].length) return false;
        return items[c.getContentType().ordinal()][c.id] != null;
    }

    private void handleItem(Item item, boolean ore){
        float energyScore = Mathf.sqr(item.charge + item.explosiveness + item.flammability + item.radioactivity);
        float score = (float)Math.pow(Math.max(item.hardness + 1, 1), 1.5) * Math.max(item.cost + energyScore, 0.1f) * 1.5f;

        ContentScore c = get(item);
        if(c.artificial){
            if(ore){
                c.score = score;
                c.outputScore = score;
                c.loaded = true;
                c.artificial = false;
                if(getOrigins(item) != null) getOrigins(item).clear();
            }else{
                c.outputScore = score;
                c.artificial = true;
                addUnloaded(c);
            }
        }
    }

    private void handleLiquid(Liquid liquid, boolean artificial){
        float score = Mathf.sqr(liquid.flammability + liquid.explosiveness + Math.abs((liquid.temperature - 0.5f) * 2f)) + liquid.heatCapacity;

        ContentScore c = get(liquid);

        if(c.artificial){
            if(!artificial){
                c.score = score;
                c.outputScore = score;
                c.loaded = true;
                c.artificial = false;
            }else{
                c.outputScore = score;
                c.artificial = true;
                addUnloaded(c);
            }
        }
        addContent(c);
    }

    private void handleBlock(Block block){
        //updateSize(block);

        if(block instanceof Floor f){
            if(f.itemDrop != null){
                handleItem(f.itemDrop, true);
            }
            if(f.liquidDrop != null){
                handleLiquid(f.liquidDrop, false);
            }
            return;
        }
        if(block.synthetic()){
            if(block instanceof UnitFactory uf){
                for(UnitPlan plan : uf.plans){
                    addOrigin(plan.unit, block);
                }
            }else if(block instanceof Reconstructor re){
                for(UnitType[] types : re.upgrades){
                    UnitType type = types[1];
                    addOrigin(type, block);
                    addOrigin(type, types[0]);
                }
            }else if(block instanceof GenericCrafter gc){
                if(gc.outputItem != null && !Structs.contains(gc.requirements, st -> st.item == gc.outputItem.item)){
                    addOrigin(gc.outputItem.item, block);
                }
                if(gc.outputLiquid != null){
                    addOrigin(gc.outputLiquid.liquid, block);
                }
            }
            ContentScore cs = get(block);
            addUnloaded(cs);
        }
    }

    float getItemRequirementsScore(ItemStack[] stacks, float loss){
        float s = 0f;
        for(ItemStack stack : stacks){
            if(get(stack.item).loaded) s += get(stack.item).score * (loss == 1f ? stack.amount : (float)Math.pow(stack.amount, 1f / loss));
        }
        return s;
    }

    @Override
    public boolean shouldProcess(){
        return !processing;
    }

    float bulletScoreIndividual(BulletType b){
        float pierce = (!b.pierce || !b.collides || !b.collidesTiles) ? 1f : (b.pierceCap > 0 ? b.pierceCap / 3f : 3f),
        lightning = (b.lightningDamage > 0 ? b.lightningDamage : b.damage) * b.lightning * (b.lightningLength / 2f) * (1f - Mathf.clamp(b.lightningCone / 360f, 0f, 0.5f)),
        damage = b.damage + (b.splashDamage * (b.splashDamageRadius / 15f)),
        heal = (b.healPercent / 100f) * damage;
        return (damage + heal + lightning + b.range()) * pierce;
    }

    float bulletScore(BulletType b){
        if(contains(b)){
            return get(b).outputScore;
        }
        float sum = bulletScoreIndividual(b);
        BulletType frag = b.fragBullet, last = b;
        float frags = b.fragBullets * (1f - Mathf.clamp((b.fragCone / 360f), 0f, 0.5f));
        while(frag != null){
            float p = ((!last.pierce || !last.collides || !last.collidesTiles) ? 1f : (last.pierceCap > 0 ? last.pierceCap : 10f));
            sum += bulletScoreIndividual(frag) * p * frags;
            frags *= frag.fragBullets * (1f - Mathf.clamp((frag.fragCone / 360f), 0f, 0.5f));
            last = frag;
            frag = frag.fragBullet;
        }
        ContentScore cs = new ContentScore(b);
        cs.outputScore = sum;
        cs.loaded = true;
        addContent(cs);
        return sum;
    }

    private class ContentScore{
        Content content;
        int loadedc = 0, loadedLast = 0, skip;
        float score, outputScore, liquidScore, itemScore, powerScore;
        boolean loaded = false, artificial = true;
        short[] itemFilter, liquidFilter;

        ContentScore(Content c){
            content = c;
        }

        <T extends Content> T as(){
            return (T)content;
        }

        float consumeScore(){
            return liquidScore + itemScore + powerScore;
        }

        float energyScore(){
            if(content instanceof Item i){
                return Mathf.sqr(i.flammability + i.radioactivity + i.explosiveness + i.charge);
            }else if(content instanceof Liquid l){
                return Mathf.sqr(l.explosiveness + l.flammability + (Math.abs(l.temperature - 0.5f) * 2f));
            }
            return 0f;
        }

        void updateOutputScore(){
            float outputScore = 0f;
            if(content instanceof UnitType type){
                float size = ((type.hitSize * type.hitSize) / Mathf.PI) / 2f;
                outputScore += ((type.health * (type.health / (type.health - type.armor))) / size) * ((type.speed / 2f) + 1f);
                for(Weapon w : type.weapons){
                    outputScore += (bulletScore(w.bullet) / w.reload) * w.shots * (w.continuous ? w.bullet.lifetime / 5f : 1f);
                }
                //outputScore /= (type.hitSize / 3f);
            }else if(content instanceof Block block){
                outputScore += ((block.health / (float)(block.size * block.size)) + (block.absorbLasers ? 350f : 0f)) / 2f;
                if(content instanceof Wall wall){
                    outputScore += Math.max(wall.chanceDeflect * block.health, 0f) + Math.max(wall.lightningDamage * (wall.lightningLength / 2f) * wall.lightningChance, 0f);
                }else if(content instanceof ItemTurret iTurret){
                    float ts = 0f;
                    for(Entry<Item, BulletType> type : iTurret.ammoTypes){
                        float ts2 = (bulletScore(type.value) / Math.max(get(type.key).score, 0.5f));
                        ts = Math.max(ts2, ts);
                    }
                    ts /= iTurret.reloadTime;
                    ts *= (!iTurret.alternate ? iTurret.shots : 1);
                    outputScore += ts;
                }else if(content instanceof PowerTurret pTurret){
                    outputScore += (bulletScore(pTurret.shootType) / pTurret.reloadTime) * (!pTurret.alternate ? pTurret.shots : 1);
                }
            }
            this.outputScore = Math.max(this.outputScore, outputScore);
        }

        float fractScore(){
            float f = outputScore / score;
            return (Float.isNaN(f) || Float.isInfinite(f)) ? Float.MAX_VALUE : f;
        }

        void generateUnitScore(){
            if(origins() != null){
                float max = 0f;
                for(Content origin : origins()){
                    if(origin instanceof UnitFactory uf){
                        for(UnitPlan plan : uf.plans){
                            if(plan.unit == content){
                                float time = ((plan.time / 60f) / 10f) + (1f - (1f / 10f));
                                max = Math.max(((getItemRequirementsScore(plan.requirements, 2.5f) + get(uf).consumeScore()) / 15f) * time, max);
                                break;
                            }
                        }
                    }else if(origin instanceof Reconstructor re){
                        float time = ((re.constructTime / 60f) / 10f) + (1f - (1f / 10f));
                        float score = (float)Math.pow(get(re).consumeScore() / 10f, 1f / 2.5f) * time;
                        for(UnitType[] types : re.upgrades){
                            if(types[1] == content){
                                score += get(types[0]).score;
                                break;
                            }
                        }
                        max = Math.max(max, score);
                    }
                }
                score = max;
            }
        }

        void generateItemScore(){
            if(!artificial) return;
            if(origins() != null){
                for(Content origin : origins()){
                    if(get(origin).loaded && origin instanceof GenericCrafter gc){
                        if(content instanceof Item){
                            ContentScore other = get(origin);
                            float s = other.consumeScore();
                            score = Math.max(score, (s / gc.outputItem.amount) + (gc.craftTime / 60f));
                        }else{
                            ContentScore og = get(origin);
                            score = Math.max(score, (og.liquidScore + og.powerScore + (og.itemScore / (gc.craftTime / 60f))) / gc.outputLiquid.amount);
                        }
                    }
                }
            }else{
                score = 0f;
            }
        }

        void generateBlockScore(){
            Block block = as();
            float score = 0f;
            for(ItemStack r : block.requirements){
                //score += get(r.item).score + Mathf.sqrt(r.amount);
                score += get(r.item).score * (float)Math.pow(r.amount / (float)(block.size * block.size), 1f / 1.4f);
            }
            score *= (block.requirements.length / 35f) + (1f - (1f / 35f)) + block.buildCostMultiplier;
            //score *= block.size * block.size;
            //score /= 2f;
            for(Consume cons : block.consumes.all()){
                if(cons.optional) continue;
                if(cons instanceof ConsumePower c){
                    score += c.usage;
                    //conScore += c.usage;
                    powerScore += c.usage;
                    continue;
                }
                if(cons instanceof ConsumeItems c){
                    for(ItemStack s : c.items){
                        float sc = get(s.item).score;
                        score += sc * s.amount;
                        //conScore += sc * s.amount;
                        itemScore += sc * s.amount;
                    }
                    continue;
                }
                if(cons instanceof ConsumeItemFilter c){
                    float max = 0f;
                    if(itemFilter != null){
                        for(short i : itemFilter){
                            //shortSeq.add(i, (short)1);
                            float maxL = get(i, ContentType.item).score;
                            max = Math.max(max, maxL);
                        }
                    }else{
                        for(Item item : items()){
                            if(c.filter.get(item)){
                                //shortSeq.add(item.id, (short)1);
                                float maxL = get(item).score;
                                max = Math.max(max, maxL);
                            }
                        }
                    }
                    score += max;
                    //conScore += max;
                    itemScore += max;
                    continue;
                }
                if(cons instanceof ConsumeLiquidBase c){
                    float amount = c.amount;
                    if(cons instanceof ConsumeLiquid cl){
                        float s = get(cl.liquid).score * amount;
                        score += s;
                        //conScore += s;
                        liquidScore += s;
                    }else if(cons instanceof ConsumeLiquidFilter clf){
                        float max = 0f;
                        if(liquidFilter != null){
                            for(short i : liquidFilter){
                                max = Math.max(max, get(i, ContentType.liquid).score);
                            }
                        }else{
                            for(Liquid l : liquids()){
                                if(clf.filter.get(l)){
                                    max = Math.max(max, get(l).score);
                                }
                            }
                        }
                        score += max * amount;
                        //conScore += max * amount;
                        liquidScore += max * amount;
                    }
                }
            }
            if(block instanceof UnitFactory uf){
                float max = 0f;
                for(UnitPlan plan : uf.plans){
                    float ts = 0f;
                    for(ItemStack s : plan.requirements){
                        float sc = get(s.item).score;
                        ts += sc * s.amount;
                        //score += sc * s.amount;
                        //conScore += sc * s.amount;
                    }
                    max = Math.max(max, ts);
                    //ContentScore cs = get(plan.unit);
                }
                itemScore += max;
                //conScore += max;
                score += max;
            }
            this.score = score;
            //consumeScore = conScore;
        }

        boolean blockLoaded(){
            if(!(content instanceof Block) || loaded) return true;
            boolean f = true;
            Block block = as();

            for(ItemStack r : block.requirements){
                f = get(r.item).loaded;
                if(!f) break;
            }

            if(f){
                for(Consume cons : block.consumes.all()){
                    if(cons instanceof ConsumeItems c){
                        for(ItemStack i : c.items){
                            f &= get(i.item).loaded;
                        }
                        continue;
                    }
                    if(cons instanceof ConsumeItemFilter c){
                        if(itemFilter == null){
                            ShortSeq seq = new ShortSeq(8);
                            for(Item i : items()){
                                if(c.filter.get(i)){
                                    f &= get(i).loaded;
                                    seq.add(i.id);
                                }
                            }
                            itemFilter = seq.toArray();
                        }else{
                            for(short i : itemFilter){
                                f &= get(i, ContentType.item).loaded;
                            }
                        }
                        continue;
                    }
                    if(cons instanceof ConsumeLiquid c){
                        f &= get(c.liquid).loaded;
                        continue;
                    }
                    if(cons instanceof ConsumeLiquidFilter c){
                        if(liquidFilter == null){
                            ShortSeq seq = new ShortSeq(8);
                            for(Liquid l : liquids()){
                                if(c.filter.get(l)){
                                    f &= get(l).loaded;
                                    seq.add(l.id);
                                }
                            }
                            liquidFilter = seq.toArray();
                        }else{
                            for(short i : liquidFilter){
                                f &= get(i, ContentType.liquid).loaded;
                            }
                        }
                    }
                }
                if(f){
                    if(block instanceof ItemTurret iTurret){
                        for(Entry<Item, BulletType> entry : iTurret.ammoTypes){
                            f = get(entry.key).loaded;
                            if(!f) break;
                        }
                    }else if(block instanceof UnitFactory uFac){
                        for(UnitPlan plan : uFac.plans){
                            for(ItemStack stack : plan.requirements){
                                f = get(stack.item).loaded;
                                if(!f) break;
                            }
                        }
                    }
                }
            }

            return f;
        }

        boolean requiredLoaded(){
            if(origins() != null){
                boolean b = true;
                loadedLast = loadedc;
                loadedc = 0;
                int i = 0;
                for(Content c : origins()){
                    loadedc |= Mathf.num(get(c).loaded) << i;
                    b &= get(c).loaded;
                    i++;
                }
                if(loadedc == loadedLast && loadedc != 0){
                    skip++;
                }else{
                    skip = 0;
                }
                if(skip >= skipIteration){
                    return true;
                }
                return b;
            }
            //return true;
            return !artificial;
        }

        Seq<Content> origins(){
            if(!artificial) return null;
            return getOrigins(content);
        }

        void updateScore(){
            if(!loaded && requiredLoaded() && blockLoaded()){
                if(content instanceof UnitType){
                    generateUnitScore();
                }else if(content instanceof Block){
                    generateBlockScore();
                }else if(content instanceof Item || content instanceof Liquid){
                    generateItemScore();
                }
                updateOutputScore();
                loaded = true;
            }
        }
    }
}
