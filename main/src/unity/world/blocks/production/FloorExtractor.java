package unity.world.blocks.production;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class FloorExtractor extends GenericCrafter{
    private static final Seq<Tile> source = new Seq<>();

    public IntFloatMap sources = new IntFloatMap();

    public FloorExtractor(String name){
        super(name);
    }

    public void setup(Object... arr) {
        if(arr.length % 2 > 0) throw new IllegalArgumentException("map must be [Block, float, Block, float, ...]");
        for(int i = 0; i < arr.length; i += 2){
            Block block = (Block)arr[i];
            float val = (float)arr[i + 1];

            sources.put(block.id, val);
        }
    }

    @Override
    public void setStats(){
        super.setStats();

        Seq<Block> blocks = new Seq<>();
        for(int id : sources.keys().toArray().items){
            blocks.add(content.getByID(ContentType.block, id));
        }

        blocks.sort(block -> sources.get(block.id, 0f));

        for(Block block : blocks){
            stats.add(Stat.tiles, table -> table.stack(
                new Image(block.uiIcon)
                    .setScaling(Scaling.fit), new Table(cont ->
                        cont.top()
                            .right()
                            .add("[accent]" + (int)((sources.get(block.id)) * 100f) + "%")
                            .style(Styles.outlineLabel)
                    )
                )
            );
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        Item item = outputItem != null ? outputItem.item : null;

        float width = drawPlaceText(Core.bundle.formatFloat("bar.extractspeed", 60f / craftTime * (count(tile) / size), 2), x, y, valid);
        float dx = x * tilesize + offset - width / 2f - 4f;
        float dy = y * tilesize + offset + size * tilesize / 2f + 5f;

        if(item != null){
            float s = iconSmall / 4f;

            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(item.uiIcon, dx, dy - 1, s, s);

            Draw.reset();
            Draw.rect(item.uiIcon, dx, dy, s, s);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        return super.canPlaceOn(tile, team) && count(tile) > 0f;
    }

    public float count(Tile tile){
        return tile == null ? 0f : tile.getLinkedTilesAs(this, source).sumf(t -> sources.get(t.floorID(), sources.get(t.overlayID(), 0f)));
    }

    public class FloorExtractorBuild extends GenericCrafterBuild{
        @Override
        public float getProgressIncrease(float baseTime){
            float incr = super.getProgressIncrease(baseTime);
            return incr * (count(tile) / size);
        }
    }
}
