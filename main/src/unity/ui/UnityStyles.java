package unity.ui;

import arc.graphics.*;
import arc.scene.ui.Label.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.gen.Tex.*;

public class UnityStyles{
    public static TextButtonStyle creditst;
    public static LabelStyle speecht, speechtitlet;
    public static LabelStyle codeLabel;
    public static TextFieldStyle codeArea;

    public static void load(){
        if(headless) return;

        creditst = new TextButtonStyle(){{
            font = Fonts.def;
            fontColor = Color.white;

            up = atlas.drawable("unity-credits-banner-up");
            down = atlas.drawable("unity-credits-banner-down");
            over = atlas.drawable("unity-credits-banner-over");
        }};

        speecht = new LabelStyle(){{
            fontColor = Color.white;
        }};

        speechtitlet = new LabelStyle(){{
            fontColor = Color.white;
        }};

        codeLabel = new LabelStyle(){{
            fontColor = Color.white;
        }};

        codeArea = new TextFieldStyle(){{
            fontColor = Color.white;
            disabledFontColor = Color.gray;
            disabledBackground = underlineDisabled;
            selection = Tex.selection;
            cursor = Tex.cursor;
            messageFont = Fonts.def;
            messageFontColor = Color.gray;
        }};
    }
}
