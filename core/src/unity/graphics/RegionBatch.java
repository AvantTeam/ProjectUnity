package unity.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;

/** Used for certain shaders. */
public class RegionBatch extends SpriteBatch{
    public float u, v, u2, v2;
    //public float width, height;

    public static RegionBatch batch;

    public static void init(){
        batch = new RegionBatch();
    }

    @Override
    protected void draw(Texture texture, float[] spriteVertices, int offset, int count){
        u = v = 0f;
        u2 = v2 = 1f;
        //width = 1f;
        //height = 1f;
        super.draw(texture, spriteVertices, offset, count);
    }

    @Override
    protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation){
        super.draw(region, x, y, originX, originY, width, height, rotation);
        u = region.u;
        v = region.v;
        u2 = region.u2;
        v2 = region.v2;
        //this.width = region.width;
        //this.height = region.height;
    }

    public Texture getTexture(){
        return lastTexture;
    }
}
