package unity.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.scene.*;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.*;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

/* Most code comes from MinimapRenderer.java by Anuke */
public class UnderworldMap extends Element {
    private static final float baseSize = 16f;
    private Pixmap pixmap;
    private Texture texture;
    private TextureRegion region;
    private Pixmap shadowPixmap;
    private Texture shadowTexture;
    private TextureRegion shadowRegion;
    private float mouseX = -1, mouseY = -1;

    @Override
    public float getMinWidth() {
        return world.width() * 32f;
    }

    @Override
    public float getMinHeight() {
        return world.height() * 32f;
    }

    public UnderworldMap(){
        addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Element fromActor) {
                mouseX = x;
                mouseY = y;

                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                mouseX = x;
                mouseY = y;

                return super.mouseMoved(event, x, y);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Element toActor) {
                mouseX = -1;
                mouseY = -1;

                super.exit(event, x, y, pointer, toActor);
            }
        });
    }

    public Pixmap getPixmap(){
        return pixmap;
    }

    public @Nullable
    Texture getTexture(){
        return texture;
    }

    public void reset(){
        if(pixmap != null){
            pixmap.dispose();
            texture.dispose();
        }

        if(shadowPixmap != null){
            shadowPixmap.dispose();
            shadowTexture.dispose();
        }

        pixmap = new Pixmap(world.width() * 32, world.height() * 32);
        texture = new Texture(pixmap);
        region = new TextureRegion(texture);

        shadowPixmap = new Pixmap(world.width() * 32, world.height() * 32);
        shadowTexture = new Texture(shadowPixmap);
        shadowRegion = new TextureRegion(shadowTexture);
    }

    public boolean isFree(Tile tile, int x, int y){
        Tile nearby = tile.nearby(x, y);

        if(nearby == null){
            return false;
        } else {
            return !(nearby.block() != null && nearby.block() instanceof StaticWall);
        }
    }

    public boolean checkSquare(Tile tile, int radius){
        boolean has = false;
        for(int i = -radius; i <= radius; i++){
            for(int j = -radius; j <= radius; j++) {
                if(i == 0 && j == 0) continue;

                if (isFree(tile, i, j)){
                    has = true;
                    break;
                }
            }
            if(has) break;
        }

        return has;
    }

    public void updateAll(){
        // TODO performance sucks
        for(Tile tile : world.tiles){
            if(tile != null) {
                if (!(tile.block() != null && tile.block() instanceof StaticWall) && tile.floor() != null && tile.floor().region != null) {
                    if(tile.floor().isLiquid){
                        pixmap.draw(Core.atlas.getPixmap(Blocks.darksand.variantRegions[Mathf.random(0, 2)]), tile.x * 32, (world.height() - tile.y) * 32);
                    } else {
                        pixmap.draw(Core.atlas.getPixmap(Blocks.craters.variantRegions[Mathf.random(0, 2)]), tile.x * 32, (world.height() - tile.y) * 32);
                    }
                } else {
                    if (checkSquare(tile, 2)) {
                        pixmap.draw(Core.atlas.getPixmap(Blocks.duneWall.variantRegions[Mathf.random(0, 1)]), tile.x * 32, (world.height() - tile.y) * 32);
                    } else {
                        if (checkSquare(tile, 4)) {
                            shadowPixmap.fillRect(tile.x * 32, (world.height() - tile.y) * 32, 32, 32, Color.black.rgba());
                        }

                        pixmap.fillRect(tile.x * 32, (world.height() - tile.y) * 32, 32, 32, Color.black.rgba());
                    }
                }
            }
        }

        texture.draw(pixmap);
        shadowTexture.draw(shadowPixmap);
    }

    public void rectCorner(TextureRegion tr, float w, float h){
        Draw.rect(tr,x + w*0.5f, y + h*0.5f, w, h);
    }

    public void rectCorner(TextureRegion tr, float x, float y, float w, float h){
        Draw.rect(tr,x + w*0.5f, y + h*0.5f, w, h);
    }

    @Override
    public void draw() {
        super.draw();
        Draw.color(Color.white);

        if(region != null && shadowRegion != null) {
            rectCorner(region, region.width, region.height);
            Draw.alpha(0.1f);
            for(int i = -4; i < 5; i++) {
                for(int j = -4; j < 5; j++) {
                    rectCorner(shadowRegion, (float)(x + Math.pow(i / 4f, 2) * i * 2f), (float)(y + Math.pow(j / 4f, 2) * j * 2f), shadowRegion.width, shadowRegion.height);
                }
            }
            Draw.alpha(1f);
        } else {
            reset();
            updateAll();
        }

        if(mouseX != -1 && mouseY != -1){
            Draw.color(Pal.accent);
            Draw.alpha(0.2f);
            Fill.rect(x + (int)(mouseX / 32f) * 32f + 16f, y + (int)(mouseY / 32f) * 32f + 16f, 32, 32);
            Draw.reset();
        }
    }
}
