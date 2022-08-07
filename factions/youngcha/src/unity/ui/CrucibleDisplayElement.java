package unity.ui;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import unity.world.graph.CrucibleGraph.*;
import unity.world.meta.CrucibleRecipes.*;

public class CrucibleDisplayElement extends Element{
    private static final Rect scissor = new Rect();
    public final OrderedMap<CrucibleIngredient, CrucibleFluid> fluids;
    int columns;

    public CrucibleDisplayElement(OrderedMap<CrucibleIngredient, CrucibleFluid> fluids, int columns){
        this.fluids = fluids;
        this.columns = columns;
    }

    @Override
    public void draw(){
        var font = Fonts.outline;
        var lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        Drawable bar = Tex.bar;
        Drawable top = Tex.barTop;
        int i = 0;
        float cWidth = width / columns;
        for(var fluid : fluids){
            arc.util.Log.info(fluid.key.name);
            if(fluid.value.total() < 0.0001){
                continue;
            }
            float xPos = x + (i % columns) * cWidth;
            float yPos = y + (i / columns) * 32;

            Draw.color(fluid.key.color, 0.2f);
            bar.draw(xPos, y + (i / columns) * 32, cWidth, 32);
            Draw.color(fluid.key.color);
            if(ScissorStack.push(scissor.set(xPos, y + (i / columns) * 32, cWidth * fluid.value.meltedRatio(), 32))){
                top.draw(xPos, y + (i / columns) * 32, cWidth, 32);
                ScissorStack.pop();
            }
            Draw.color(Pal.darkerGray);
            Draw.rect(fluid.key.icon, xPos + 16, yPos + 14, 24, 24);
            Draw.color();
            Draw.rect(fluid.key.icon, xPos + 16, yPos + 18, 24, 24);

            String text = Strings.fixed(fluid.value.total(), 1);
            lay.setText(font, text);
            font.setColor(Color.white);
            font.draw(text, xPos + cWidth * 0.5f - lay.width / 2.0f + 8, yPos + lay.height / 2.0f + 16);
            i++;
        }
        Draw.color();
        Pools.free(lay);
    }

    public void rectCorner(float x, float y, float w, float h){
        Fill.rect((x + w * 0.5f), (y + h * 0.5f), w, h);
    }

    public void rectCorner(TextureRegion tr, float x, float y, float w, float h){
        Draw.rect(tr, (x + w * 0.5f), (y + h * 0.5f), w, h);
    }

    @Override
    public float getMinHeight(){
        float notEmpty = 0;
        for(var fluid : fluids){
            notEmpty += fluid.value.total() > 0 ? 1 : 0;
        }
        return Mathf.ceil(notEmpty / columns) * 32;
    }
}
