package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import unity.graphics.*;

import static arc.Core.*;

public class WindTurbine extends TorqueGenerator{
    public final TextureRegion[] overlayRegions = new TextureRegion[2], baseRegions = new TextureRegion[4], rotorRegions = new TextureRegion[2];
    public TextureRegion topRegion, movingRegion, bottomRegion, mbaseRegion;

    public WindTurbine(String name){
        super(name);
        
        solid = true;
    }

    @Override
    public void load(){
        super.load();
        
        topRegion = atlas.find(name + "-top");
        movingRegion = atlas.find(name + "-moving");
        bottomRegion = atlas.find(name + "-bottom");
        mbaseRegion = atlas.find(name + "-mbase");
        
        for(int i = 0; i < 4; i++) baseRegions[i] = atlas.find(name + "-base" + (i + 1));
        for(int i = 0; i < 2; i++){
            overlayRegions[i] = atlas.find(name + "-overlay" + (i + 1));
            rotorRegions[i] = atlas.find(name + "-rotor" + (i + 1));
        }
    }

    public class WindTurbineBuild extends TorqueGeneratorBuild{
        @Override
        protected float generateTorque(){
            float x = Time.time * 0.001f;
            float mul = 0.4f * Math.max(
                0f,
                Mathf.sin(x) + 0.5f * Mathf.sin(2f * x + 50f) + 0.2f * Mathf.sin(7f * x + 90f) + 0.1f * Mathf.sin(23f * x + 10f) + 0.55f
            ) + 0.15f;
            
            return mul;
        }

        @Override
        public void draw(){
            float shaftRotog = torque().getRotation();
            int variant = (rotation + 1) % 4 >= 2 ? 1 : 0;
            float shaftRot = variant == 1 ? 360f - shaftRotog : shaftRotog;
            
            Draw.rect(bottomRegion, x, y);
            Draw.rect(baseRegions[rotation], x, y);
            Draw.rect(mbaseRegion, x, y, rotdeg());

            UnityDrawf.drawRotRect(movingRegion, x, y, 24f, 3.5f, 24f, rotdeg(), shaftRot, shaftRot + 180f);
            Draw.rect(rotorRegions[1], x, y, shaftRotog);
            Draw.rect(rotorRegions[0], x, y, shaftRotog * 2f);

            Draw.rect(topRegion, x, y);
            drawTeamTop();
        }
    }
}
