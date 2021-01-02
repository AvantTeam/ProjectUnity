package younggamExperimental;

import arc.scene.ui.layout.*;
import mindustry.type.*;
import younggamExperimental.blocks.ModularTurret.*;

//I'm sad.
public class TurretBaseUpdater{
    ModularTurretBuild build;
    PartInfo basePart;
    float reload, reloadTime;

    public static void attachBaseUpdate(){
        //TODO
    }

    float reloadMultiplier(){
        return 1f;
    }

    PartInfo getBasePart(){
        return basePart;
    }

    void updateShooting(){
        var hgraph = build.heat();
        float temp = hgraph.getTemp();
        if(reload >= reloadTime){
            //TODO
        }else reload += build.delta();//TODO
    }

    boolean canShoot(){
        return true;
    }

    void onShoot(){}

    void applyStats(StatContainer total){
        //TODO
    }

    //useAmmo(){
        //TODO
    //}

    //peekAmmo(){
        //TODO
    //}

    void processConfig(){
        //TODO
    }

    //boolean hasAmmo(){
        //TODO
    //}

    boolean acceptItem(Item item){
        //TODO
        return false;
    }

    void displayAmmoStats(Table table){
        //TODO
    }

    void draw(float x, float y){}
}
