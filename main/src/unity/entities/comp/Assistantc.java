package unity.entities.comp;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.pooling.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

/** @author GlennFolker */
public interface Assistantc extends Unitc{
    String lastText();
    void lastText(String lastText);

    float textFadeTime();
    void textFadeTime(float textFadeTime);

    @Override
    default void writeSync(Writes write){
        write.str(lastText());
        write.f(textFadeTime());
    }

    @Override
    default void readSync(Reads read){
        lastText(read.str());
        textFadeTime(read.f());
    }

    @Override
    default void update(){
        textFadeTime(textFadeTime() - Time.delta / 300f);
    }

    @Override
    default void draw(){
        Draw.z(Layer.playerName);
        float z = Drawf.text();

        Font font = Fonts.def;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        float textHeight = 15f;
        boolean ints = font.usesIntegerPositions();

        font.setUseIntegerPositions(false);
        font.getData().setScale(0.25f / Scl.scl(1f));

        String text = lastText() != null ? Core.bundle.getOrNull(lastText()) : null;
        if(Core.settings.getBool("playerchat") && textFadeTime() > 0f && text != null){
            float width = 100f;
            float visualFadeTime = 1f - Mathf.curve(1f - textFadeTime(), 0.9f);

            font.setColor(1f, 1f, 1f, textFadeTime() <= 0f || text == null ? 1f : visualFadeTime);
            layout.setText(font, text, Color.lightGray, width, Align.bottom, true);

            Draw.color(0f, 0f, 0f, 0.3f * (textFadeTime() <= 0f || text == null ? 1f : visualFadeTime));
            Fill.rect(x(), y() + textHeight + layout.height - layout.height / 2f, layout.width + 2f, layout.height + 3f);

            font.draw(text, x() - width / 2f, y() + textHeight + layout.height, width, Align.center, true);
        }

        Draw.reset();
        Pools.free(layout);

        font.getData().setScale(1f);
        font.setColor(Color.white);
        font.setUseIntegerPositions(ints);

        Draw.z(z);
    }
}
