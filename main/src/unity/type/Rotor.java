package unity.type;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.io.*;

//same as Weapon directory.
public class Rotor{
    public TextureRegion bladeRegion;
    public TextureRegion topRegion;

    public boolean mirror = false;

    public float x = 0f;
    public float y = 0f;
    public float scale = 1f;

    public float rotOffset = 0f;
    public float speed = 29f;

    public int bladeCount = 4;
    public String name;

    //is this not neat?
    public Rotor(String name){
        this.name = name;
    }

    public void load(){
        bladeRegion = Core.atlas.find(name + "-blade");
        topRegion = Core.atlas.find(name + "-top");
    }

    public Rotor copy(){
        Rotor out = new Rotor(name);
        JsonIO.json().copyFields(this, out);

        return out;
    }
}
