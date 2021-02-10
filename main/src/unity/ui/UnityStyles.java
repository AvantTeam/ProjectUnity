package unity.ui;

import arc.assets.*;
import arc.graphics.*;
import arc.scene.ui.TextButton.*;
import mindustry.ui.*;

import static arc.Core.*;

public class UnityStyles implements Loadable{
    public static TextButtonStyle creditst;

    @Override
    public void loadSync(){
        creditst = new TextButtonStyle(){{
            font = Fonts.def;
            fontColor = Color.white;
            up = atlas.drawable("unity-credits-banner");
            down = atlas.drawable("unity-credits-banner-down");
        }};
    }
}
