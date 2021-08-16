package unity.logic;

import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import unity.gen.*;
import unity.gen.Expc.*;

public class ExpSenseI implements LInstruction{
    public int res, type;
    public ExpContentList cont;

    public ExpSenseI(int res, ExpContentList cont, int type){
        this.res = res;
        this.cont = cont;
        this.type = type;
    }

    @Override
    public void run(LExecutor exec){
        Object b = exec.obj(type);

        if(b instanceof ExpBuildc build){
            switch(cont){
                case totalExp -> exec.setnum(res, build.exp());
                case totalLevel -> exec.setnum(res, build.level());
                case expCapacity -> exec.setnum(res, ((Expc)build.block()).maxExp());
                case maxLevel -> exec.setnum(res, build.maxLevel());
            }
        }else{
            exec.setnum(res, 0d);
        }
    }
}
