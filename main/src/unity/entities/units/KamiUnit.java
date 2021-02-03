package unity.entities.units;

import mindustry.gen.*;
import unity.*;

public class KamiUnit extends UnitEntity{
    @Override
    public void add(){
        if(!added){
            Unity.musicHandler.play("kami");
            super.add();
        }
    }

    @Override
    public void remove(){
        if(added){
            Unity.musicHandler.stop("kami");
            super.remove();
        }
    }
}
