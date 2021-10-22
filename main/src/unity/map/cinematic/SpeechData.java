package unity.map.cinematic;

import arc.audio.*;
import arc.flabel.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.style.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SpeechData{
    public Prov<CharSequence> title = () -> "";
    public String content = "";
    public Sound sound = Sounds.click;
    public FListener listener;
    public Prov<Drawable> image = () -> Tex.clear;
    public Prov<Color> color = () -> Pal.accent;
    public float endDelay = 120f;
}
