package unity.type;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.io.*;

//same as Weapon directory.
public class Rotor{
    public final String name;

    public TextureRegion bladeRegion, bladeOutlineRegion, topRegion;

    public boolean mirror = false;
    public float x = 0f;
    public float y = 0f;
    public float scale = 1f;

    public float rotOffset = 0f;
    public float speed = 29f;

    public int bladeCount = 4;

    public Rotor(String name){
        this.name = name;
    }

    public void load(){
        bladeRegion = Core.atlas.find(name + "-blade");
        bladeOutlineRegion = Core.atlas.find(name + "-blade-outline");
        topRegion = Core.atlas.find(name + "-top");
    }

    public Rotor copy(){
        Rotor out = new Rotor(name);
        JsonIO.json().copyFields(this, out);

        return out;
    }
}
