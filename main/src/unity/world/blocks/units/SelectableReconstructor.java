package unity.world.blocks.units;

import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.units.*;
import mindustry.world.meta.*;

import static arc.Core.*;

public class SelectableReconstructor extends Reconstructor{
    public Seq<UnitType[]> otherUpgrades = new Seq<>();
    protected int minTier;

    public SelectableReconstructor(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        outRegion = atlas.find("unity-factory-out-" + size);
        inRegion = atlas.find("unity-factory-in-" + size);
    }

    @Override
    public void setStats(){
        stats.add(Stat.output, table -> {
            table.row();
            table.add("[accent]T" + minTier);
        });
        super.setStats();
        stats.add(Stat.output, table -> {
            float size = 8f * 3f;
            table.row();
            table.add("[accent]T" + (minTier + 1)).row();
            otherUpgrades.each(upgrade->{
                if(upgrade[0].unlockedNow() && upgrade[1].unlockedNow()){
                    table.image(upgrade[0].uiIcon).size(size).padRight(4f).padLeft(10f).scaling(Scaling.fit).right();
                    table.add(upgrade[0].localizedName).left();
                    table.add("[lightgray] -> ");
                    table.image(upgrade[1].uiIcon).size(size).padRight(4f).scaling(Scaling.fit);
                    table.add(upgrade[1].localizedName).left();
                    table.row();
                }
            });
        });
    }

    public class SelectableReconstructorBuild extends ReconstructorBuild{
        protected int tier = minTier;

        @Override
        public void buildConfiguration(Table table){
            table.button("T" + minTier, Styles.togglet, () -> tier = minTier)
                .width(50f).height(50f)
                .update(b -> b.setChecked(tier == minTier));

            table.button("T" + (minTier + 1), Styles.togglet, () -> tier = minTier + 1)
                .width(50f).height(50f)
                .update(b -> b.setChecked(tier == minTier + 1));
        }

        @Override
        public UnitType upgrade(UnitType type){
            UnitType[] ret = null;
            if(tier == minTier) ret = upgrades.find(u -> u[0] == type);
            else if(tier == minTier + 1) ret = otherUpgrades.find(u -> u[0] == type);
            return ret == null ? null : ret[1];
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(tier);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            tier = read.b();
        }
    }
}
