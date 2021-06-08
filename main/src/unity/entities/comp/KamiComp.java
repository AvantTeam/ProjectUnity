package unity.entities.comp;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.ai.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, Bossc.class, Factionc.class, Kamic.class})
@EntityComponent
abstract class KamiComp implements Unitc, Factionc{
    transient Bullet laser;
    @SyncField(true) float laserRotation = 0f;

    @ReadOnly transient KamiAI trueController;

    @Override
    public void update(){
        if(laser != null){
            laser.rotation(laserRotation);
        }

        if(trueController.unit == self()){
            trueController.updateUnit();
        }
    }

    @Override
    public void draw(){
        if(trueController != null){
            float z = Draw.z();
            Draw.z(Layer.flyingUnit);
            trueController.draw();
            Draw.z(z);
            Draw.reset();
        }
    }

    @Override
    public void add(){
        trueController = new KamiAI();
        trueController.unit(self());
    }

    @Override
    public void damage(float amount){
        if(trueController.unit == self() && !trueController.waiting){
            trueController.stageDamage += amount;
        }
    }

    @Override
    public Faction faction(){
        return Faction.koruh;
    }
}
