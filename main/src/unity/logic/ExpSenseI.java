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
        int i = cont.ordinal();
        Object b = exec.obj(type);

        if(b instanceof ExpBuildc build){
            switch(i){
                case 0: {
                    exec.setnum(res, build.exp());
                    break;
                }

                case 1: {
                    exec.setnum(res, build.level());
                    break;
                }

                case 2: {
                    exec.setnum(res, ((Expc)build.block()).maxExp());
                    break;
                }

                case 3: {
                    exec.setnum(res, build.maxLevel());
                    break;
                }
            }
        }else{
            exec.setnum(res, 0d);
        }
    }
}
