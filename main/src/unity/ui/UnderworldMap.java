package unity.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.io.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/* Most code comes from MinimapRenderer.java by Anuke */
public class UnderworldMap extends Element {
    private static final float baseSize = 16f;
    private Pixmap pixmap;
    private Texture texture;
    private TextureRegion region;

    @Override
    public float getMinWidth() {
        return world.width() * 4f;
    }

    @Override
    public float getMinHeight() {
        return world.height() * 4f;
    }

    public UnderworldMap(){
        Events.on(EventType.WorldLoadEvent.class, event -> {
            reset();
            updateAll();
        });

        Events.on(EventType.TileChangeEvent.class, event -> {
            //TODO don't update when the minimap is off?
            if(!ui.editor.isShown()){
                updateTile(event.tile);
            }
        });

        Events.on(EventType.BuildTeamChangeEvent.class, event -> updateTile(event.build.tile));
    }

    public Pixmap getPixmap(){
        return pixmap;
    }

    public @Nullable
    Texture getTexture(){
        return texture;
    }

    private int colorFor(Tile tile){
        if(tile == null) return 0;
        int bc = tile.block().minimapColor(tile);
        Color color = Tmp.c1.set(bc == 0 ? MapIO.colorFor(tile.block(), tile.floor(), tile.overlay(), tile.team()) : bc);
        color.mul(1f - Mathf.clamp(world.getDarkness(tile.x, tile.y) / 4f));

        return color.rgba();
    }

    public void reset(){

        if(pixmap != null){
            pixmap.dispose();
            texture.dispose();
        }

        pixmap = new Pixmap(world.width(), world.height());
        texture = new Texture(pixmap);
        region = new TextureRegion(texture);
    }

    public void updateTile(Tile tile){
        if(world.isGenerating() || !state.isGame()) return;

        if(tile.build != null && tile.isCenter()){
            tile.getLinkedTiles(other -> {
                if(!other.isCenter()){
                    updateTile(other);
                }
            });
        }

        int color = colorFor(tile);
        pixmap.set(tile.x, pixmap.height - 1 - tile.y, color);

        Pixmaps.drawPixel(texture, tile.x, pixmap.height - 1 - tile.y, color);
    }

    public void updateAll(){
        for(Tile tile : world.tiles){
            pixmap.set(tile.x, pixmap.height - 1 - tile.y, colorFor(tile));
        }

        texture.draw(pixmap);
    }

    public void rectCorner(TextureRegion tr, float w, float h){
        Draw.rect(tr,x + w*0.5f, y + h*0.5f, w, h);
    }

    @Override
    public void draw() {
        super.draw();
        Draw.color(Color.white);

        if(region != null) {
            rectCorner(region, region.width * 4f, region.height * 4f);
        } else {
            reset();
            updateAll();
        }
    }
}
