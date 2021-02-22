package unity.world.blocks.power;

import arc.graphics.g2d.*;
import unity.graphics.*;

import static arc.Core.atlas;

public class ElectricMotor extends TorqueGenerator{
    public final TextureRegion[] overlayRegions = new TextureRegion[2], baseRegions = new TextureRegion[2], coilRegions = new TextureRegion[2];
    public TextureRegion topRegion, movingRegion, bottomRegion, mbaseRegion;

    public ElectricMotor(String name){
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
        
        for(int i = 0; i < 2; i++){
            baseRegions[i] = atlas.find(name + "-base" + (i + 1));
            overlayRegions[i] = atlas.find(name + "-overlay" + (i + 1));
            coilRegions[i] = atlas.find(name + "-coil" + (i + 1));
        }
    }

    public class ElectricMotorBuild extends TorqueGeneratorBuild{
        @Override
        protected float generateTorque(){
            return power.graph.getSatisfaction();
        }

        @Override
        public void draw(){
            int variant = (rotation + 1) % 4 >= 2 ? 1 : 0;
            int rotVar = rotation % 2 == 1 ? 1 : 0;
            
            float shaftRot = torque().getRotation();
            
            if(variant == 1) shaftRot = 360f - shaftRot;
            
            Draw.rect(bottomRegion, x, y);
            Draw.rect(baseRegions[rotVar], x, y);
            Draw.rect(coilRegions[rotVar], x, y);
            Draw.rect(mbaseRegion, x, y, rotdeg());

            UnityDrawf.drawRotRect(movingRegion, x, y, 24f, 3.5f, 24f, rotdeg(), shaftRot, shaftRot + 180f);
            Draw.rect(overlayRegions[variant], x, y, rotdeg());
            Draw.rect(topRegion, x, y);
            
            drawTeamTop();
        }
    }
}
