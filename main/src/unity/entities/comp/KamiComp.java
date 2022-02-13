package unity.entities.comp;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.ai.kami.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, Bossc.class, Factionc.class, Kamic.class})
@EntityComponent
abstract class KamiComp implements Unitc, Factionc{
    transient Bullet laser;
    @SyncField(true) float laserRotation = 0f;

    @ReadOnly transient KamiAI newAI;

    @Override
    public void update(){
        if(laser != null){
            laser.rotation(laserRotation);
        }
        if(newAI.unit == self()){
            newAI.updateUnit();
        }
    }

    @Override
    public void draw(){
        if(newAI != null){
            float z = Draw.z();
            Draw.z(Layer.flyingUnit);
            newAI.draw();
            Draw.z(z);
            Draw.reset();
        }
    }

    @Override
    public void add(){
        newAI = new KamiAI();
        newAI.unit(self());
    }

    @Override
    public Faction faction(){
        return Faction.koruh;
    }

    @Replace(2)
    @Override
    public float clipSize(){
        return Float.MAX_VALUE;
    }
}
