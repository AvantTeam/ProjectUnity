package unity.logic;

import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import unity.world.blocks.*;

public class ExpSenseI implements LInstruction{
    public int res, type;
    public ExpContentList cont;

    public ExpSenseI(int res, ExpContentList cont, int type){
        this.res = res;
        this.cont = cont;
        this.type = type;
    }

    @Override
    public void run(LExecutor vm){
        final int cont = this.cont.ordinal();
        Object build = vm.obj(type);
        if(!(build instanceof Building b)){
            vm.setnum(res, 0);
            return;
        }
        switch(cont){
            case 0:
                vm.setnum(res, (b instanceof ExpBuildBase expb) ? expb.totalExp() : 0);
                break;
            case 1:
                //TODO
                vm.setnum(res, (b instanceof ExpBuildFrame expb) ? expb.totalExp() : 0);
                break;
            case 2:
                vm.setnum(res, (b.block instanceof ExpBlockBase expb) ? expb.expCapacity() : 0);
                break;
            case 3:
                //TODO
                vm.setnum(res, (b.block instanceof ExpBlockFrame expb) ? expb.getMaxLevel() : 0);
                break;
        }
    }
}
