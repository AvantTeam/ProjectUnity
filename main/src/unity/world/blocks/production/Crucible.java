package unity.world.blocks.production;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.gen.*;
import unity.graphics.*;
import unity.ui.dialogs.*;
import unity.util.*;
import unity.world.blocks.*;
import unity.world.graph.*;
import unity.world.meta.*;
import unity.world.modules.*;

import static arc.Core.*;

public class Crucible extends GraphBlock{
    CrucibleGraph viewPos;
    private static final long[] randomPos = new long[]{
        SVec2.construct(0f, 0f),
        SVec2.construct(-1.6f, 1.6f),
        SVec2.construct(-1.6f, -1.6f),
        SVec2.construct(1.6f, -1.6f),
        SVec2.construct(-1.6f, -1.6f),
        SVec2.construct(0f, 0f)
    };

    public TextureRegion[] liquidRegions, baseRegions, roofRegions, solidItemStrips, heatRegions;
    public TextureRegion floorRegion, solidItem;

    public Crucible(String name){
        super(name);

        configurable = solid = true;
    }

    @Override
    public void load(){
        super.load();

        liquidRegions = GraphicUtils.getRegions(liquidRegion, 12, 4);
        baseRegions = GraphicUtils.getRegions(atlas.find(name + "-base"), 12, 4);
        floorRegion = atlas.find(name + "-floor");
        roofRegions = GraphicUtils.getRegions(atlas.find(name + "-roof"), 12, 4);

        solidItem = atlas.find(name + "-solid");
        solidItemStrips = GraphicUtils.getRegions(atlas.find(name + "-solidstrip"), 6, 1);
        heatRegions = GraphicUtils.getRegions(heatRegion, 12, 4);
    }

    public class CrucibleBuild extends GraphBuild{
        final Color color = Color.clear.cpy();

        @Override
        public void buildConfiguration(Table table){
            table.button(Tex.whiteui, Styles.clearTransi, 50f, new CrucibleDialog(this)::show).size(50f).get().getStyle().imageUp = Icon.chartBar;
            table.button(Tex.whiteui, Styles.clearTransi, 50f, () -> configure(0)).size(50f).get().getStyle().imageUp = Icon.eye;
        }

        @Override
        public void configured(Unit builder, Object value){
            CrucibleGraph thisG = crucible().getNetwork();
            viewPos = viewPos == thisG ? null : thisG;
        }

        @Override
        public void drawConfigure(){}

        @Override
        public void draw(){
            GraphCrucibleModule dex = crucible();
            byte tileIndex = UnityDrawf.tileMap[dex.tilingIndex];

            if(viewPos == dex.getNetwork()){
                Draw.rect(floorRegion, x, y, 8f, 8f);
                drawContents(dex, tileIndex);

                Draw.rect(baseRegions[tileIndex], x, y, 8f, 8f, 4f, 4f, 0f);
                UnityDrawf.drawHeat(heatRegions[tileIndex], x, y, 0f, heat().getTemp());
            }else{
                Draw.rect(roofRegions[tileIndex], x, y, 8f, 8f, 4f, 4f, 0f);
            }

            drawTeamTop();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return crucible().canContainMore(1f) && MeltInfo.map.containsKey(item);
        }

        @Override
        public void handleItem(Building source, Item item){
            crucible().addItem(item);
        }

        protected void drawContents(GraphCrucibleModule crucGraph, int tIndex){
            color.set(0f, 0f, 0f);
            Seq<CrucibleData> cc = crucGraph.getContained();

            if(cc.isEmpty()) return;

            float tLiquid = 0f;
            float fraction = crucGraph.liquidCap / crucGraph.getTotalLiquidCapacity();

            for(var i : cc){
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

            if(tLiquid > 0f){
                float invt = 1f / tLiquid;

                Draw.color(color.mul(invt), Mathf.clamp(tLiquid * fraction * 2f));
                Draw.rect(liquidRegions[tIndex], x, y, 8f, 8f);
            }

            for(var i : cc){
                if(i.meltedRatio < 1f && i.volume * fraction > 0.1f){
                    Color itemCol = UnityPal.youngchaGray;

                    if(i.item != null) itemCol = i.item.color;

                    float ddd = (1f - i.meltedRatio) * i.volume * fraction;

                    if(ddd > 0.1f){
                        Draw.color(itemCol);
                        if(ddd > 1f) Draw.rect(solidItemStrips[Mathf.floor(ddd) - 1], x, y);

                        float siz = 8f * (ddd % 1f);
                        long pos = randomPos[Math.max(Mathf.floor(ddd), 5)];

                        Draw.rect(solidItem, SVec2.x(pos) + x, SVec2.y(pos) + y, siz, siz);
                    }
                }
            }

            Draw.color();
        }
    }
}
