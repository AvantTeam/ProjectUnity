package unity.world.blocks.production;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DeepBeamDrill extends BeamDrill{
    public float oreLoss = 0.666f;
    public float wallLoss = 0.9f;
    public float hardnessDrillMultiplier = 1f;

    private static Item returnItem;
    private static float returnItemAmount;
    private final static int[] returnLen = new int[16];
    private final static boolean[] returnFound = new boolean[16];
    private final static FloatSeq returnOreData = new FloatSeq(), oreDataTmp = new FloatSeq();
    private final static ObjectFloatMap<Item> oreCount = new ObjectFloatMap<>();
    private final static Seq<Item> itemArray = new Seq<>();

    public DeepBeamDrill(String name){
        super(name);
        range = 7;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        //super.drawPlace(x, y, rotation, valid);
        countOre(x, y, rotation, null);
        boolean found = false;
        for(int i = 0; i < size; i++){
            nearbySide(x, y, rotation, i, Tmp.p1);
            int len = returnLen[i];
            found |= returnFound[i];
            Drawf.dashLine(returnFound[i] ? Pal.placing : Pal.remove,
                    Tmp.p1.x * tilesize,
                    Tmp.p1.y * tilesize,
                    (Tmp.p1.x + Geometry.d4x(rotation)*len) * tilesize,
                    (Tmp.p1.y + Geometry.d4y(rotation)*len) * tilesize);
            if(len < range - 1){
                int r = range - 1;
                Drawf.dashLine(Pal.lightishGray,
                        (Tmp.p1.x + Geometry.d4x(rotation)*len) * tilesize,
                        (Tmp.p1.y + Geometry.d4y(rotation)*len) * tilesize,
                        (Tmp.p1.x + Geometry.d4x(rotation)*r) * tilesize,
                        (Tmp.p1.y + Geometry.d4y(rotation)*r) * tilesize);
            }
        }
        Item item = returnItem;
        if(returnItem != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", (60f / getDrillTime(item) * returnItemAmount), 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(item.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(item.fullIcon, dx, dy, s, s);
        }else if(found){
            drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, false);
        }
        FloatSeq seq = returnOreData;
        for(int i = 0; i < seq.size; i += 3){
            if(seq.items[i + 2] > 0){
                //drawPlaceText(Strings.fixed(seq.items[i + 2] * 100, 2) + "%", (int)seq.items[i], (int)seq.items[i + 1], true);
                drawTextSmall(Strings.fixed(seq.items[i + 2] * 100, 1) + "%", (int)seq.items[i], (int)seq.items[i + 1]);
            }else{
                float tx = seq.items[i] * tilesize, ty = seq.items[i + 1] * tilesize;
                Lines.stroke(3f, Pal.gray);
                Lines.lineAngleCenter(tx, ty, -45f, 3f, true);
                Lines.lineAngleCenter(tx, ty, 45f, 3f, true);
                Lines.stroke(1f, Pal.remove);
                Lines.lineAngleCenter(tx, ty, -45f, 3f, true);
                Lines.lineAngleCenter(tx, ty, 45f, 3f, true);
            }
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        for(int i = 0; i < size; i++){
            nearbySide(tile.x, tile.y, rotation, i, Tmp.p1);
            for(int j = 0; j < range; j++){
                Tile other = world.tile(Tmp.p1.x + Geometry.d4x(rotation)*j, Tmp.p1.y + Geometry.d4y(rotation)*j);
                if(other != null && other.solid()){
                    Item drop = other.wallDrop();
                    if(drop != null && drop.hardness <= tier){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    void drawTextSmall(String text, int x, int y){
        if(renderer.pixelator.enabled()) return;
        Color color = Pal.accent;
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.5f / 4f / Scl.scl(1f), 0.75f / 4f / Scl.scl(1f));
        layout.setText(font, text);

        font.setColor(color);
        float dx = x * tilesize, dy = y * tilesize - 3f;
        font.draw(text, dx, dy + layout.height + 1, Align.center);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);
    }

    public float getDrillTime(Item item){
        return drillTime + hardnessDrillMultiplier * item.hardness;
    }

    protected void countOre(int x, int y, int rotation, float[][] ranges){
        returnItem = null;
        returnItemAmount = 0f;
        returnOreData.clear();
        oreDataTmp.clear();
        oreCount.clear();
        itemArray.clear();

        boolean place = ranges == null;

        for(int i = 0; i < size; i++){
            float efficiency = 1f;
            boolean lenb = true;
            nearbySide(x, y, rotation, i, Tmp.p1);
            returnLen[i] = range - 1;
            returnFound[i] = false;
            for(int j = 0; j < range; j++){
                int rx = Tmp.p1.x + Geometry.d4x(rotation)*j, ry = Tmp.p1.y + Geometry.d4y(rotation)*j;
                Tile other = world.tile(rx, ry);
                if(!place){
                    ranges[i][j] = 0f;
                }
                if(other != null && other.solid()){
                    Item drop = other.wallDrop();
                    if(drop != null){
                        if(place){
                            oreDataTmp.add(other.x, other.y, drop.hardness <= tier ? efficiency : -1f, drop.id);
                            returnFound[i] = true;
                        }else{
                            if(j > 0){
                                int back = j - 1;
                                while(back >= 0 && ranges[i][back] == 0){
                                    ranges[i][back] = -1f;
                                    back--;
                                }
                            }
                            ranges[i][j] = drop.hardness <= tier ? efficiency : -1f;
                        }
                        if(drop.hardness <= tier){
                            if(!itemArray.contains(drop)){
                                itemArray.add(drop);
                            }
                            oreCount.increment(drop, 0, efficiency);
                            efficiency *= oreLoss;
                            if(lenb){
                                returnLen[i] = j;
                                lenb = false;
                            }
                        }else{
                            if(place && !itemArray.contains(drop)){
                                itemArray.add(drop);
                            }
                            efficiency *= wallLoss;
                        }
                    }else{
                        efficiency *= wallLoss;
                    }
                }
            }
        }
        if(itemArray.size == 0){
            return;
        }

        itemArray.sort((i1, i2) -> {
            int type = Boolean.compare(!i1.lowPriority, !i2.lowPriority);
            if(type != 0) return type;
            int amounts = Float.compare(oreCount.get(i1, 0), oreCount.get(i2, 0));
            if(amounts != 0) return amounts;
            return Integer.compare(i1.id, i2.id);
        });
        returnItem = itemArray.peek();
        returnItemAmount = oreCount.get(returnItem, 0);

        float[] items = oreDataTmp.items;
        for(int i = 0; i < oreDataTmp.size; i += 4){
            if(returnItem.id == items[i + 3] || (place && items[i + 2] == -1f)) returnOreData.add(items[i], items[i + 1], items[i + 2]);
        }
        if(returnItem.hardness > tier) returnItem = null;
    }

    public class DeepBeamDrillBuild extends BeamDrillBuild{
        public float dominantItems;
        public Item dominantItem;
        float[][] ranges = new float[size][range];
        Seq<Tile> oreDatas = new Seq<>();

        @Override
        public void updateTile(){
            if(lasers[0] == null) updateLasers();
            warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), 1f / 60f);
            for(Tile t : oreDatas){
                if(t.wallDrop() != dominantItem){
                    onProximityUpdate();
                    //Log.warn("This is not suppose to happen");
                    break;
                }
            }

            if(dominantItem == null){
                lastDrillSpeed = 0f;
                return;
            }
            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            float delay = getDrillTime(dominantItem);

            float multiplier = Mathf.lerp(1f, optionalBoostIntensity, optionalEfficiency);
            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, 0.1f);
            lastDrillSpeed = (dominantItems * multiplier * edelta()) / delay;

            time += edelta() * multiplier * dominantItems;

            if(time >= delay && items.total() < itemCapacity){
                offload(dominantItem);
                time %= delay;
            }
        }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);
            Draw.rect(topRegion, x, y, rotdeg());

            if(isPayload() && dominantItem == null) return;

            int ddx = Geometry.d4x(rotation), ddy = Geometry.d4y(rotation);
            for(int i = 0; i < size; i++){
                Color color = Tmp.c2.set(sparkColor).lerp(boostHeatColor, boostWarmup);
                Draw.z(Layer.power - 1);
                Point2 pos = lasers[i];
                float lsx = (pos.x - ddx/2f) * tilesize, lsy = (pos.y - ddy/2f) * tilesize;
                float width = (laserWidth + Mathf.absin(Time.time + i * 5 + id * 9, glowScl, pulseIntensity)) * warmup;

                float ex = -1f, ey = -1f;
                boolean found = true;

                //Draw.mixcol(glowColor, Mathf.absin(Time.time + i*5 + id*9, glowScl, glowIntensity));
                for(int s = 0; s < 2; s++){
                    float lx = lsx, ly = lsy, w = width * (s == 0 ? 1 : 0.5f);
                    Draw.color(s == 0 ? color : Color.white);
                    if(ranges[i][0] != 0f) Fill.square(lsx, lsy, w, 45f);

                    for(int j = 0; j < ranges[i].length; j++){
                        if(ranges[i][j] == -1f) continue;
                        if(ranges[i][j] == 0f) break;
                        float x = (ddx * j + pos.x) * tilesize, y = (ddy * j + pos.y) * tilesize;
                        float wr = (ranges[i][j] + 0.5f) / 1.5f;
                        Lines.stroke(w * 1.25f * wr);
                        Lines.line(lx, ly, x, y, false);
                        Fill.square(x, y, w * wr, 45f);
                        lx = x;
                        ly = y;
                        if(found){
                            found = false;
                            ex = x;
                            ey = y;
                        }
                    }
                }
                Draw.color();
                Draw.mixcol();

                if(!found){
                    Draw.z(Layer.effect);
                    Lines.stroke(warmup);
                    rand.setState(i * 2912L, id * 8912L + 1);
                    for(int j = 0; j < sparks; j++){
                        float fin = (Time.time / sparkLife + rand.random(sparkRecurrence + 1f)) % sparkRecurrence;
                        Tmp.v1.set(sparkRange * rand.random(0.9f, 1.1f) * fin, 0).rotate(rotdeg() + rand.range(sparkSpread));

                        Draw.color(color, dominantItem.color, Mathf.clamp(fin));
                        float px = Tmp.v1.x, py = Tmp.v1.y;
                        if(fin <= 1f) Lines.lineAngle(ex + px, ey + py, Angles.angle(px, py), Mathf.slope(fin) * sparkSize);
                    }
                }
            }
        }

        @Override
        public void pickedUp(){
            dominantItem = null;
            oreDatas.clear();
            //Log.warn("pickedUp");
        }

        @Override
        public void onProximityUpdate(){
            noSleep();

            updateLasers();
            countOre(tileX(), tileY(), rotation, ranges);
            lastItem = returnItem;
            dominantItem = returnItem;
            dominantItems = returnItemAmount;

            oreDatas.clear();
            float[] data = returnOreData.items;
            for(int i = 0; i < returnOreData.size; i += 3){
                int pos = Point2.pack((int)data[i], (int)data[i + 1]);
                Tile t = world.tile(pos);
                oreDatas.add(t);
            }
            //Log.warn("onProximityUpdate");
        }

        void updateLasers(){
            for(int i = 0; i < size; i++){
                if(lasers[i] == null) lasers[i] = new Point2();
                nearbySide(tileX(), tileY(), rotation, i, lasers[i]);
            }
        }
    }
}
