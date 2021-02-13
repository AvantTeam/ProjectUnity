package unity.entities.units;

import mindustry.gen.*;
import unity.net.*;

public class KamiUnit extends UnitEntity{
    @Override
    public void add(){
        if(!added){
            //UnityCall.bossMusic(this, "kami"); later.
            super.add();
        }
    }
}
