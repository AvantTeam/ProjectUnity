package unity.entities.units;

import mindustry.gen.*;
import unity.net.*;

public class KamiUnit extends UnitEntity{
    @Override
    public void add(){
        if(!added){
            UnityCall.bossMusic("kami", true);
            super.add();
        }
    }

    @Override
    public void remove() {
        if(added){
            UnityCall.bossMusic("kami", false);
            super.remove();
        }
    }
}
