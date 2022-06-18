package unity.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

/**
 * A 1x1 {@link SteamVent}.
 * @author GlennFolker
 */
public class SmallVent extends Floor{
    public Block parent = Blocks.air;
    public Effect effect = Fx.ventSteam;
    public Color effectColor = Pal.vent;
    public float effectSpacing = 15f;

    public SmallVent(String name){
        super(name);
        variants = 3;
    }

    @Override
    public void drawBase(Tile tile){
        parent.drawBase(tile);

        Mathf.rand.setSeed(tile.pos());
        Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx() - tilesize, tile.worldy() - tilesize);
    }

    @Override
    public boolean updateRender(Tile tile){
        return tile.block() == Blocks.air;
    }

    @Override
    public void renderUpdate(UpdateRenderState state){
        if((state.data += Time.delta) >= effectSpacing){
            effect.at(state.tile.x * tilesize - tilesize, state.tile.y * tilesize - tilesize, effectColor);
            state.data %= effectSpacing;
        }
    }
}
