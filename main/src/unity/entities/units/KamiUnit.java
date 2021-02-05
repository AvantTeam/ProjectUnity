package unity.entities.units;

import mindustry.gen.*;
import unity.*;

public class KamiUnit extends UnitEntity{
    @Override
    public void add(){
        if(!added){
            Unity.musicHandler.play("kami", () -> !dead && added);
            super.add();
        }
    }
}
