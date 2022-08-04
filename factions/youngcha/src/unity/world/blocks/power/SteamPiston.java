package unity.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;
import unity.world.blocks.*;
import unity.world.blocks.power.FlyWheel.*;
import unity.world.graph.*;

import static mindustry.Vars.tilesize;

public class SteamPiston extends GenericGraphBlock{
    public float minTemp = HeatGraphNode.celsiusZero + 100;
    public float maxTemp = HeatGraphNode.celsiusZero + 400;
    TextureRegion[] sprite = new TextureRegion[4];
    TextureRegion base, liquid, arm, pivot, heat;
    int smokeTimer = timers++;

    public SteamPiston(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        base = Core.atlas.find(name + "-base");
        liquid = Core.atlas.find(name + "-liquid");
        arm = Core.atlas.find(name + "-arm");
        pivot = Core.atlas.find(name + "-pivot");
        for(int i = 0; i < 4; i++){
            sprite[i] = Core.atlas.find(name + "-rot" + (i + 1));
        }
    }


    public class SteamPistonBuild extends GenericGraphBuild{
        FlyWheelBuild flywheel = null;
        Vec2 flywheelDir = new Vec2();
        float pushForce = 0;
        float rWater = 0;

        public void setFlywheel(FlyWheelBuild flywheel){
            this.flywheel = flywheel;
            if(flywheel != null){
                flywheelDir.set(flywheel.x - x, flywheel.y - y).nor();
            }
        }

        @Override
        public BlockStatus status(){
            if(!enabled) return BlockStatus.logicDisable;

            if(efficiency <= 0 || !productionValid()) return BlockStatus.noInput;

            if(flywheel == null) return BlockStatus.noOutput;

            return BlockStatus.active;
        }

        @Override
        public void updateEfficiencyMultiplier(){
            var temp = heatNode().getTemp();
            if(temp > minTemp) optionalEfficiency = Mathf.clamp(Mathf.map(temp, minTemp, maxTemp, 0, 1));
            else optionalEfficiency = 0;
            if(potentialEfficiency > 0) efficiency = optionalEfficiency;
            else efficiency = 0;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(flywheel == null) return;
            var heatNode = heatNode();
            if(optionalEfficiency > 0){
                boolean pulling = flywheelDir.dot(flywheel.attachY - y, -(flywheel.attachX - x)) > 0;
                if(pulling){
                    pushForce = 0;
                    if(timer(smokeTimer, 5) && liquids.currentAmount() > 1){
                        float rand = Mathf.random() > 0.5f ? -1 : 1;
                        Fx.fuelburn.at(x + flywheelDir.y * tilesize * rand, y - flywheelDir.x * tilesize * rand);
                    }
                }else{
                    if(rWater <= 0 && potentialEfficiency > 0){
                        consume();
                        heatNode.addHeatEnergy(-optionalEfficiency * 150);
                        rWater += 10;
                    }
                    if(rWater > 0){
                        rWater -= optionalEfficiency * delta();
                        pushForce += (timeScale() * optionalEfficiency - pushForce) * 0.1f * delta();
                    }else pushForce = 0;
                }
            }
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();
            if(flywheel != null){
                flywheel.connected.remove(this);
                setFlywheel(null);
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            arc.util.Log.info("onProximityUpdate");
            tryConnect();
        }

        public void tryConnect(){
            var fb = front();
            if(flywheel != null){
                flywheel.connected.remove(this);
                setFlywheel(null);
            }
            if(fb instanceof FlyWheelBuild fwb && (fb.x == x || fb.y == y)){
                setFlywheel(fwb);
                flywheel.connected.add(this);
            }
        }

        @Override
        public void draw(){
            float temp = heatNode().getTemp();
            Draw.rect(base, x, y, 0);
            Drawf.liquid(liquid, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            Draw.rect(sprite[rotation], x, y, 0);
            drawTeamTop();

            if(flywheel != null){
                float r = tilesize;
                float yd = flywheelDir.dot(flywheel.attachX - x, flywheel.attachY - y);
                float xd = flywheelDir.dot(flywheel.attachY - y, -(flywheel.attachX - x));
                boolean left = xd > 0;
                xd = Math.abs(xd);
                float d = Math.max(0, yd - Mathf.sqrt(Math.max(0, r * r - xd * xd)));
                float px = x + flywheelDir.x * d;
                float py = y + flywheelDir.y * d;
                Draw.z(Layer.blockOver + 0.1f);
                Lines.stroke(4);
                Lines.line(arm, x + flywheelDir.x * 10f, y + flywheelDir.y * 10f, px, py, false);
                Lines.stroke(3);
                Lines.line(arm, flywheel.attachX, flywheel.attachY, px, py, false);
                Draw.rect(pivot, px, py, 0);
                Draw.rect(pivot, flywheel.attachX, flywheel.attachY, 0);
            }
        }
    }
}
