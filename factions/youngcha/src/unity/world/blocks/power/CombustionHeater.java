package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.graph.*;

import static arc.Core.atlas;

public class CombustionHeater extends GenericGraphBlock{
    public final TextureRegion[] baseRegions = new TextureRegion[4];
    TextureRegion heatRegion;
    public float baseTemp = 1000 + HeatGraphNode.celsiusZero;
    public float tempPerFlammability = 1750;

    public float minConsumeAmount = 0.005f;
    public float maxConsumeAmount = 0.015f;

    public @Nullable ConsumeItemFilter filterItem;

    public CombustionHeater(String name){
        super(name);
        rotate = true;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++) baseRegions[i] = atlas.find(name + "-base" + (i + 1));
        heatRegion = atlas.find(name + "-heat");
    }

    @Override
    public void init(){
        filterItem = consume(new ConsumeItemFlammable(0.1f));
        super.init();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.productionTime, "@ - @ " + StatUnit.seconds.localized(), Strings.fixed((1 / minConsumeAmount) / 60f, 1), Strings.fixed((1 / maxConsumeAmount) / 60f, 1));
    }

    public class CombustionHeaterBuild extends GenericGraphBuild{
        float generateTime, productionEfficiency, efficiencyMultiplier = 1f;

        @Override
        public void initGraph(){
            super.initGraph();
            heatNode().minGenerate = 0;
        }

        @Override
        public void updateEfficiencyMultiplier(){
            if(filterItem != null){
                float m = filterItem.efficiencyMultiplier(this);
                if(m > 0) efficiencyMultiplier = m;
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
            boolean valid = efficiency > 0;
            productionEfficiency = efficiency * efficiencyMultiplier;

            if(valid && generateTime <= 0f && items.total() > 0f){
                consume();
                Fx.generatespark.at(x + Mathf.range(3f), y + Mathf.range(3f));
                generateTime = 1f;
            }

            if(generateTime > 0f){
                float mul = Mathf.lerp(minConsumeAmount, maxConsumeAmount, Mathf.clamp(heatNode().lastEnergyInput * 0.3f, 0.1f, 1.0f));
                generateTime -= delta() * mul;
            }
            heatNode().targetTemp = baseTemp + Math.max(tempPerFlammability * (productionEfficiency - 1), -baseTemp * 0.5f);
            heatNode().efficiency = productionEfficiency;
        }

        @Override
        public boolean consumeTriggerValid(){
            return generateTime > 0;
        }

        //TODO use drawer. and drawLight
        @Override
        public void draw(){
            Draw.rect(baseRegions[rotation], x, y);
            YoungchaDrawf.drawHeat(heatRegion, x, y, rotdeg(), heatNode().getTemp());

            drawTeamTop();
        }

        @Override
        public float ambientVolume(){
            return Mathf.clamp(productionEfficiency);
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(productionEfficiency);
            write.f(generateTime);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            productionEfficiency = read.f();
            if(revision >= 1){
                generateTime = read.f();
            }
        }
    }
}