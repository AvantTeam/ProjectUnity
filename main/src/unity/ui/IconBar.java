package unity.ui;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.ui.*;

public class IconBar extends Element{
    private final Prov<IconBarStat> barStats;
    private final float prefHeight;

    public IconBar(float prefHeight, Prov<IconBarStat> barStats){
        this.prefHeight = prefHeight;
        this.barStats = barStats;
    }

    @Override
    public void draw(){
        Font font = Fonts.outline;
        var lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        float xStart = 20f;
        var data = barStats.get();
        float maxVal = Math.max(data.defaultMax, data.value);
        float minVal = Math.min(data.defaultMin, data.value);
        for(var i : data.values){
            maxVal = Math.max(maxVal, i);
            minVal = Math.min(minVal, i);
        }
        float dPos = Mathf.map(data.value, minVal, maxVal, xStart, width - xStart);
        Lines.stroke(3f);
        Draw.color(Color.gray);
        float realX = x + xStart;
        float realY = y + height * 0.5f;
        Lines.rect(realX, realY - 8f, width - xStart, 16f);
        Draw.color(data.color);
        Fill.rect(realX, realY, 26f, 26f, 45f);
        Fill.rect(realX + dPos * 0.5f, realY, dPos, 16f);
        float d = maxVal - minVal;

        float stepSize = Mathf.pow(10f, Mathf.floor(Mathf.log(10f, 2f * d)));
        if(d <= stepSize) stepSize *= 0.5f;
        if(d <= stepSize) stepSize *= 0.4f;
        if(d <= stepSize) stepSize *= 0.5f;
        Draw.color(Color.white);

        for(int i = 0, len = data.values.length; i < len; i++){
            dPos = Mathf.map(data.values[i], minVal, maxVal, realX, x + width);
            Lines.line(dPos, realY, dPos, realY + 12f);
            Draw.rect(data.icons[i], dPos, y + height * 0.75f);
        }
        Draw.color(Color.lightGray);
        float stsrt = (Mathf.floor(minVal / stepSize) + 1f) * stepSize;
        for(float i = stsrt; i < maxVal; i += stepSize){
            dPos = Mathf.map(i, minVal, maxVal, realX, x + width);
            Lines.line(dPos, realY, dPos, realY - 12f);
            String text;
            if(i >= 1000f) text = Strings.fixed(i / 1000f, 1) + "K'C";
            else text = Strings.fixed(i, 0) + "'C";
            lay.setText(font, text);
            font.setColor(Color.white);
            font.draw(text, dPos - lay.width / 2f, y + height * 0.25f + lay.height / 2f + 1f);
        }
        Pools.free(lay);
    }

    @Override
    public float getPrefHeight(){
        return prefHeight;
    }

    @Override
    public float getPrefWidth(){
        return 180f;
    }

    public static class IconBarStat{
        public float defaultMax, defaultMin;
        final TextureRegion[] icons;
        final float[] values;
        final Color color;
        final float value;
        private int i;

        public IconBarStat(float value, float defaultMax, float defaultMin, Color color, int size){
            this.value = value;
            this.defaultMax = defaultMax;
            this.defaultMin = defaultMin;
            this.color = color;
            icons = new TextureRegion[size];
            values = new float[size];
        }

        public void push(float value, TextureRegion icon){
            values[i] = value;
            icons[i++] = icon;
        }
    }
}
