package unity.tools.proc;

import unity.tools.*;
import unity.type.*;

import java.util.concurrent.*;

import static mindustry.Vars.*;

public class UnitProcessor implements Processor{
    @Override
    public void process(ExecutorService exec){
        content.units().each(type -> type instanceof UnityUnitType, (UnityUnitType type) -> exec.submit(() -> {

        }));
    }
}
