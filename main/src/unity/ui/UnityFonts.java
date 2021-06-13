package unity.ui;

import arc.*;
import arc.freetype.FreeTypeFontGenerator.*;
import arc.freetype.FreetypeFontLoader.*;
import arc.graphics.*;
import arc.graphics.g2d.*;

import static mindustry.Vars.*;

public class UnityFonts{
    public static Font speech, speechtitle;

    public static void load(){
        if(headless) return;

        Core.assets.load("unityspeech", Font.class, new FreeTypeFontLoaderParameter("fonts/font.woff", new FreeTypeFontParameter(){{
            size = 12;
            incremental = true;

            shadowColor = Color.darkGray;
            shadowOffsetY = 1;
        }})).loaded = f -> speech = UnityStyles.speecht.font = (Font)f;

        Core.assets.load("unityspeechtitle", Font.class, new FreeTypeFontLoaderParameter("fonts/font.woff", new FreeTypeFontParameter(){{
            size = 21;
            incremental = true;

            shadowColor = Color.darkGray;
            shadowOffsetX = -1;
            shadowOffsetY = 3;
        }})).loaded = f -> speechtitle = UnityStyles.speechtitlet.font = (Font)f;
    }
}
