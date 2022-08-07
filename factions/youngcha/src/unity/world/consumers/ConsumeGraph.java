package unity.world.consumers;

import mindustry.world.consumers.*;

//dummy consumer for efficiency tweaks show status
public class ConsumeGraph extends Consume{
    @Override
    public boolean ignore(){
        return true;
    }
}
