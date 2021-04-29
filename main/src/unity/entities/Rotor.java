package unity.entities;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.io.*;

/**
 * @author younggam
 * @author GlennFolker
 */
public class Rotor{
    public final String name;

    public TextureRegion bladeRegion, bladeOutlineRegion, bladeGhostRegion, bladeShadeRegion, topRegion;

    public boolean mirror = false;
    public float x = 0f;
    public float y = 0f;
    public float scale = 1f;

    public float rotOffset = 0f;
    public float speed = 29f;
    public float ghostAlpha = 0.5f;

    public int bladeCount = 4;

    public Rotor(String name){
        this.name = name;
    }

    public void load(){
        bladeRegion = Core.atlas.find(name + "-blade");
        bladeOutlineRegion = Core.atlas.find(name + "-blade-outline");
        bladeGhostRegion = Core.atlas.find(name + "-blade-ghost");
        bladeShadeRegion = Core.atlas.find(name + "-blade-shade");
        topRegion = Core.atlas.find(name + "-top");
    }

    public Rotor copy(){
        Rotor out = new Rotor(name);
        JsonIO.json.copyFields(this, out);

        return out;
    }
}
