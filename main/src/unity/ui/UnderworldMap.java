package unity.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.*;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.util.*;
import mindustry.content.Blocks;
import mindustry.graphics.Pal;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import unity.type.*;

import static mindustry.Vars.*;

/* ThePythonGuy3 */
/* Some code comes from MinimapRenderer.java by Anuke */
public class UnderworldMap extends Element {
    private static Pixmap pixmap;
    private static Texture texture;
    private static TextureRegion region;

    private static Pixmap wallPixmap;
    private static Texture wallTexture;
    private static TextureRegion wallRegion;

    private static Pixmap shadowPixmap;
    private static Texture shadowTexture;
    private static TextureRegion shadowRegion;

    private static Pixmap darknessPixmap;
    private static Texture darknessTexture;
    private static TextureRegion darknessRegion;
    private static int mouseX = -1, mouseY = -1;

    private static final Color darknessColor = Color.white.cpy().lerp(Color.black, 0.71f), realDarknessColor = new Color(0f, 0f, 0f, darknessColor.a);

    public UnderworldBlock placing;

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
                mouseX = (int)(x / 32f);
                mouseY = (int)(y / 32f);

                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                mouseX = (int)(x / 32f);
                mouseY = (int)(y / 32f);

                return super.mouseMoved(event, x, y);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Element toActor) {
                mouseX = -1;
                mouseY = -1;

                super.exit(event, x, y, pointer, toActor);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                // TODO make map storage
                Log.info("Clicked at x:" + mouseX + " y:" + mouseY);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public static void reset(){
        if(pixmap != null){
            pixmap.dispose();
            texture.dispose();
        }

        if(wallPixmap != null){
            wallPixmap.dispose();
            wallTexture.dispose();
        }

        if(shadowPixmap != null){
            shadowPixmap.dispose();
            shadowTexture.dispose();
        }

        if(darknessPixmap != null){
            darknessPixmap.dispose();
            darknessTexture.dispose();
        }

        pixmap = new Pixmap(world.width() * 32, world.height() * 32);
        texture = new Texture(pixmap);
        region = new TextureRegion(texture);

        wallPixmap = new Pixmap(world.width() * 32, world.height() * 32);
        wallTexture = new Texture(wallPixmap);
        wallRegion = new TextureRegion(wallTexture);

        shadowPixmap = new Pixmap(world.width(), world.height());
        shadowTexture = new Texture(shadowPixmap);
        shadowRegion = new TextureRegion(shadowTexture);

        darknessPixmap = new Pixmap(world.width(), world.height());
        darknessTexture = new Texture(darknessPixmap);
        darknessRegion = new TextureRegion(darknessTexture);
    }

    public static boolean isFree(Tile tile, int x, int y){
        Tile nearby = tile.nearby(x, y);

        if(nearby == null){
            return false;
        } else {
            return !(nearby.block() != null && nearby.block() instanceof StaticWall);
        }
    }

    public static boolean checkSquare(Tile tile, int radius){
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

    public static void updateAll(){
        // TODO performance sucks
        for(Tile tile : world.tiles){
            if(tile != null) {
                if (!(tile.block() != null && tile.block() instanceof StaticWall) && tile.floor() != null && tile.floor().region != null) {
                    if(tile.floor().isLiquid){
                        pixmap.draw(Core.atlas.getPixmap(Blocks.darksand.variantRegions[Mathf.random(0, 2)]), tile.x * 32, (world.height() - tile.y) * 32);
                    } else {
                        TextureRegion tex = Blocks.stone.variantRegions[Mathf.random(0, 2)];
                        if (Mathf.chance(0.2f)) {
                            tex = Blocks.craters.variantRegions[Mathf.random(0, 2)];
                        }

                        pixmap.draw(Core.atlas.getPixmap(tex), tile.x * 32, (world.height() - tile.y) * 32);
                    }
                } else {
                    wallPixmap.draw(Core.atlas.getPixmap(Blocks.duneWall.variantRegions[Mathf.random(0, 1)]), tile.x * 32, (world.height() - tile.y) * 32);
                    if (checkSquare(tile, 1)) {
                        shadowPixmap.fillRect(tile.x, world.height() - tile.y, 1, 1, realDarknessColor.rgba());
                    }

                    float dark = world.getDarkness(tile.x, tile.y);
                    dark = dark <= 0f ? 1f : 1f - Math.min((dark + 0.5f) / 4f, 1f);

                    darknessPixmap.fillRect(tile.x, world.height() - tile.y, 1, 1, new Color(0f, 0f, 0f, 1f - dark).rgba());
                }
            }
        }

        texture.draw(pixmap);
        wallTexture.draw(wallPixmap);
        shadowTexture.draw(shadowPixmap);
        darknessTexture.draw(darknessPixmap);

        shadowTexture.setFilter(Texture.TextureFilter.linear, Texture.TextureFilter.linear);
        darknessTexture.setFilter(Texture.TextureFilter.linear, Texture.TextureFilter.linear);
        
        pixmap.dispose();
        wallPixmap.dispose();
        shadowPixmap.dispose();
        darknessPixmap.dispose();
    }

    public void rectCorner(TextureRegion tr, float w, float h){
        Draw.rect(tr,x + w*0.5f, y + h*0.5f + 32f, w, h);
    }

    @Override
    public void draw() {
        super.draw();
        Draw.color(Color.white);

        if(region != null && wallRegion != null && shadowRegion != null && darknessRegion != null) {
            rectCorner(region, region.width, region.height);
            rectCorner(shadowRegion, shadowRegion.width * 32f, shadowRegion.height * 32f);
            rectCorner(wallRegion, wallRegion.width, wallRegion.height);
            rectCorner(darknessRegion, darknessRegion.width * 32f, darknessRegion.height * 32f);
        } else {
            reset();
            updateAll();
        }

        if(mouseX != -1 && mouseY != -1 && !(world.tile(mouseX, mouseY).block() instanceof StaticWall)){
            Draw.color(Pal.accent);
            Draw.alpha(0.2f);
            Fill.rect(x + mouseX * 32f + 16f, y + mouseY * 32f + 16f, 32, 32);
            Draw.reset();
        }
    }
}
