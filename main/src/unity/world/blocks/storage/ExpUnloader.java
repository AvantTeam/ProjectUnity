package unity.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.*;
import unity.entities.bullet.ExpOrb;
import unity.world.blocks.ExpBuildBase;

import static arc.Core.*;

public class ExpUnloader extends Block implements ExpOrbHandlerBase{
    protected float unloadAmount = 2f, unloadTime = 60f;
    protected TextureRegion topRegion, topRegion2;
    protected final TextureRegion[] sideRegions = new TextureRegion[4];

    public ExpUnloader(String name){
        super(name);
        update = solid = true;
        timers++;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        topRegion2 = atlas.find(name + "-top2");
        for(int i = 0; i < 4; i++) sideRegions[i] = atlas.find(name + "-" + i);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, "@ [lightgray]@[]", bundle.format("explib.expAmount", unloadAmount * 10f * (60f / unloadTime)), StatUnit.perSecond.localized());
    }

    public class ExpUnloaderBuild extends Building{
        protected final boolean[] join = new boolean[]{false, false, false, false};

        @Override
        public void draw(){
            super.draw();
            for(int i = 0; i < 4; i++){
                if(join[i]) Draw.rect(sideRegions[i], x, y);
            }
            if(!consValid()) return;
            Draw.blend(Blending.additive);
            Draw.color(Color.white);
            Draw.alpha(Mathf.absin(Time.time(), 20f, 0.4f));
            Draw.rect(topRegion, x, y);
            for(int i = 0; i < 4; i++){
                if(join[i]) Draw.rect(topRegion2, x, y, i * 90f);
            }
            Draw.blend();
            Draw.reset();
        }

        @Override
        public void updateTile(){
            if(enabled && consValid() && timer(0, 60f)){
                for(int i = 0; i < 4; i++){
                    if(join[i]) checkUnload(i);
                }
            }
        }

        protected void checkUnload(int dir){
            Building build = nearby(dir);
            if(!(build instanceof ExpBuildBase) || !build.isValid()){
                join[dir] = false;
                return;
            }
            ExpBuildBase temp = (ExpBuildBase)build;
            for(int i = 0; i < 2; i++){
                if(temp.totalExp() >= ExpOrb.expAmount){
                    temp.incExp(-1f * ExpOrb.expAmount);
                    ExpOrb.spewExp(x, y, 1, dir * 90f + 180f);
                }else break;
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            for(int i = 0; i < 4; i++){
                Building build = nearby(i);
                join[i] = build instanceof ExpBuildBase && build.isValid() && build.interactable(team);
            }
        }
    }
}
