package unity.entities.units;

import mindustry.gen.*;
import unity.*;

import static mindustry.Vars.*;

public class KamiUnit extends UnitEntity{
    @Override
    public void add(){
        if(!added){
            Unity.musicHandler.play("kami", () -> 
                !dead &&
                player.within(this, 1500f) &&
                (state.isPlaying() || state.isPaused())
            );

            super.add();
        }
    }
}
