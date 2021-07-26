package unity.async;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import mindustry.*;
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

@SuppressWarnings("unchecked")
public class ContentScoreProcess implements AsyncProcess{
    private static final int iterationError = 6;
    private boolean init = false, processing, finished = false;
    private final Seq<ContentScore> unloaded = new Seq<>();
    private final IntSet[] unloadedSet = new IntSet[ContentType.all.length];
    private ContentScore[][] items;

    public ContentScoreProcess(){
        Events.on(EventType.ContentInitEvent.class, event -> init = true);
    }

    @Override
    public void process(){
        if(init){
            processing = true;
            Core.app.post(() -> Unity.print("Content Score Begin"));
            for(int i = 0; i < unloadedSet.length; i++){
                unloadedSet[i] = new IntSet(204);
            }
            items = new ContentScore[ContentType.all.length][0];
            for(ContentType type : ContentType.all){
                items[type.ordinal()] = new ContentScore[Vars.content.getBy(type).size];
                for(Content content : Vars.content.getBy(type)){
                    switch(type){
                        case block -> handleBlock((Block)content);
                        case item -> handleItem((Item)content, false);
                        case liquid -> handleLiquid((Liquid)content, true);
                        case bullet -> bulletScore((BulletType)content);
                        case sector, ammo, weather, planet, loadout_UNUSED, effect_UNUSED, mech_UNUSED, typeid_UNUSED, error -> unloadedSet[type.ordinal()] = null;
                        default -> {
                            ContentScore cs = new ContentScore(content);
                            cs.loaded = false;
                            //unloaded.add(cs);
                            addUnloaded(cs);
                            addContent(cs);
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
                    .append("\n")
                    .append("Sources: ");
                    if(score.origins != null){
                        for(Content c : score.origins){
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

    public float getScore(Content content){
        if(!finished) return 0f;
        return get(content).score;
    }

    public float getFractScore(Content content){
        if(!finished) return 0f;
        return get(content).fractScore();
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
        float energyScore = Mathf.sqr(item.charge + item.explosiveness + item.flammability);
        float score = (float)Math.pow(Math.max(item.hardness + 1, 1), 1.5) * Math.max(item.cost + energyScore, 0.1f) * 1.5f;

        if(contains(item)){
            ContentScore s = get(item);
            if(ore && s.artificial){
                s.score = score;
                s.artificial = false;
                s.loaded = true;
            }
            return;
        }
        //updateSize(item);

        if(ore){
            ContentScore c = new ContentScore(item);
            c.score = score;
            c.outputScore = score;
            c.loaded = true;
            c.artificial = false;
            addContent(c);
        }else{
            ContentScore c = new ContentScore(item);
            c.outputScore = score;
            c.artificial = true;
            addUnloaded(c);
            addContent(c);
        }
    }

    private void handleLiquid(Liquid liquid, boolean artificial){
        float score = Mathf.sqr(liquid.flammability + liquid.explosiveness + Math.abs((liquid.temperature - 0.5f) * 2f)) + liquid.heatCapacity;

        if(contains(liquid)){
            ContentScore s = get(liquid);
            if(!artificial && s.artificial){
                s.score = score;
                s.artificial = false;
                s.loaded = true;
            }
            return;
        }
        //updateSize(liquid);
        ContentScore c = new ContentScore(liquid);
        c.score = artificial ? 0f : score;
        c.outputScore = score;
        c.artificial = artificial;
        c.loaded = !artificial;
        addContent(c);
        if(artificial) addUnloaded(c);
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
            if(block.requirements.length > 0){
                for(ItemStack req : block.requirements){
                    handleItem(req.item, false);
                }
            }
            if(block instanceof UnitFactory uf){
                for(UnitPlan plan : uf.plans){
                    if(!contains(plan.unit)){
                        ContentScore cs = new ContentScore(plan.unit);
                        addContent(cs);
                    }
                    ContentScore cs = get(plan.unit);
                    cs.addOrigin(uf);
                }
            }else if(block instanceof Reconstructor re){
                for(UnitType[] types : re.upgrades){
                    UnitType type = types[1];
                    if(!contains(type)){
                        ContentScore cs = new ContentScore(type);
                        addContent(cs);
                    }
                    ContentScore cs = get(type);
                    cs.addOrigin(re);
                }
            }else if(block instanceof GenericCrafter gc){
                if(gc.outputItem != null && !Structs.contains(gc.requirements, st -> st.item == gc.outputItem.item)){
                    handleItem(gc.outputItem.item, false);
                    ContentScore cs = get(gc.outputItem.item);
                    cs.addOrigin(gc);
                }
                if(gc.outputLiquid != null){
                    handleLiquid(gc.outputLiquid.liquid, true);
                    ContentScore cs = get(gc.outputLiquid.liquid);
                    cs.addOrigin(gc);
                }
            }
            ContentScore cs = new ContentScore(block);
            addUnloaded(cs);
            addContent(cs);
        }
    }

    @Override
    public boolean shouldProcess(){
        return !processing;
    }

    float bulletScoreIndividual(BulletType b){
        float pierce = (!b.pierce || !b.collides || !b.collidesTiles) ? 1f : (b.pierceCap > 0 ? b.pierceCap / 3f : 3f),
        lightning = (b.lightningDamage > 0 ? b.lightningDamage : b.damage) * b.lightning * (b.lightningLength / 2f) * (1f - Mathf.clamp(b.lightningCone / 360f, 0f, 0.5f));
        return (b.damage + (b.splashDamage * (b.splashDamageRadius / 15f)) + lightning + b.range()) * pierce;
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
        float score, outputScore, consumeScore;
        boolean loaded = false, artificial = true;
        Content[] origins;
        short[] itemsRequired, liquidRequired;

        ContentScore(Content c){
            content = c;
        }

        <T extends Content> T as(){
            return (T)content;
        }

        void addOrigin(Content con){
            if(!artificial) return;
            if(origins == null) origins = new Content[0];
            if(!Structs.contains(origins, con)){
                origins = Arrays.copyOf(origins, origins.length + 1);
                origins[origins.length - 1] = con;
            }
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
                outputScore += block.health / (float)(block.size * block.size);
                if(content instanceof Wall wall){
                    outputScore += Math.max(wall.chanceDeflect * block.health, 0f) + Math.max(wall.lightningDamage * (wall.lightningLength / 2f) * wall.lightningChance, 0f);
                }else if(content instanceof ItemTurret iTurret){
                    float ts = 0f;
                    for(Entry<Item, BulletType> type : iTurret.ammoTypes){
                        float ts2 = (bulletScore(type.value) / get(type.key).score) * 2f;
                        ts = Math.max(ts2, ts);
                    }
                    ts /= iTurret.reloadTime;
                    ts *= (!iTurret.alternate ? iTurret.shots : 1);
                    outputScore += ts;
                }else if(content instanceof PowerTurret pTurret){
                    outputScore += (bulletScore(pTurret.shootType) / pTurret.rotateSpeed) * (!pTurret.alternate ? pTurret.shots : 1);
                }
            }
            this.outputScore = Math.max(this.outputScore, outputScore);
        }

        float fractScore(){
            float f = outputScore / score;
            return (Float.isNaN(f) || Float.isInfinite(f)) ? Float.MAX_VALUE : f;
        }

        void generateItemScore(){
            if(origins != null){
                for(Content origin : origins){
                    if(origin instanceof GenericCrafter gc){
                        score = Math.max(score, (get(origin).consumeScore / gc.outputItem.amount) * (gc.craftTime / 60f));
                    }
                }
            }else{
                score = 0f;
            }
        }

        void generateBlockScore(){
            Block block = as();
            float score = 0f;
            float conScore = 0f;
            for(ItemStack r : block.requirements){
                score += get(r.item).score + Mathf.sqrt(r.amount);
            }
            score *= block.size * block.size;
            score /= 5f;
            for(Consume cons : block.consumes.all()){
                if(cons.optional) continue;
                if(cons instanceof ConsumePower c){
                    score += c.usage;
                    conScore += c.usage;
                    continue;
                }
                if(cons instanceof ConsumeItems c){
                    for(ItemStack s : c.items){
                        float sc = get(s.item).score;
                        score += sc * s.amount;
                        conScore += sc * s.amount;
                    }
                    continue;
                }
                if(cons instanceof ConsumeItemFilter c){
                    float max = 0f;
                    if(itemsRequired != null){
                        for(short i : itemsRequired){
                            max = Math.max(max, get(i, ContentType.item).score);
                        }
                    }else{
                        for(Item item : Vars.content.items()){
                            if(c.filter.get(item)){
                                max = Math.max(max, get(item).score);
                            }
                        }
                    }
                    score += max;
                    conScore += max;
                    continue;
                }
                if(cons instanceof ConsumeLiquidBase c){
                    float amount = c.amount;
                    if(cons instanceof ConsumeLiquid cl){
                        float s = get(cl.liquid).score * amount;
                        score += s;
                        conScore += s;
                    }else if(cons instanceof ConsumeLiquidFilter clf){
                        float max = 0f;
                        if(liquidRequired != null){
                            for(short i : liquidRequired){
                                max = Math.max(max, get(i, ContentType.liquid).score);
                            }
                        }else{
                            for(Liquid l : Vars.content.liquids()){
                                if(clf.filter.get(l)){
                                    max = Math.max(max, get(l).score);
                                }
                            }
                        }
                        score += max * amount;
                        conScore += max * amount;
                    }
                }
            }
            if(block instanceof UnitFactory uf){
                for(UnitPlan plan : uf.plans){
                    for(ItemStack s : plan.requirements){
                        float sc = get(s.item).score;
                        score += sc * s.amount;
                        conScore += sc * s.amount;
                    }
                    //ContentScore cs = get(plan.unit);
                }
            }
            this.score = score;
            consumeScore = conScore;
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
                        if(itemsRequired == null){
                            ShortSeq seq = new ShortSeq(8);
                            for(Item i : Vars.content.items()){
                                if(c.filter.get(i)){
                                    f &= get(i).loaded;
                                    seq.add(i.id);
                                }
                            }
                            itemsRequired = seq.toArray();
                        }else{
                            for(short i : itemsRequired){
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
                        if(liquidRequired == null){
                            ShortSeq seq = new ShortSeq(8);
                            for(Liquid l : Vars.content.liquids()){
                                if(c.filter.get(l)){
                                    f &= get(l).loaded;
                                    seq.add(l.id);
                                }
                            }
                            liquidRequired = seq.toArray();
                        }else{
                            for(short i : liquidRequired){
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
            if(origins != null){
                for(Content c : origins){
                    if(!get(c).loaded) return false;
                }
            }
            return true;
        }

        void updateScore(){
            if(!loaded && requiredLoaded() && blockLoaded()){
                if(content instanceof Block){
                    generateBlockScore();
                }else if(content instanceof Item){
                    generateItemScore();
                }
                updateOutputScore();
                loaded = true;
            }
        }
    }
}
