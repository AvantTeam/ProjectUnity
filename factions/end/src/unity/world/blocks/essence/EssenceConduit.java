package unity.world.blocks.essence;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.liquid.*;
import unity.content.*;
import unity.graphics.*;

public class EssenceConduit extends EndEssenceBlock implements Autotiler{
    public TextureRegion[] regions;
    public TextureRegion bottomRegion;
    public Color baseColor = EndPal.endSolidDarker;
    public int timerFlow = timers++;
    protected Block junction;

    public EssenceConduit(String name){
        super(name);
        rotate = true;
        solid = false;
        underBullets = true;
        conveyorPlacement = true;
        noUpdateDisabled = true;
        canOverdrive = false;
        essenceCapacity = 5f;
        essencePressure = 1.025f;
        //priority = TargetPriority.transport;
    }

    @Override
    public void load(){
        super.load();
        regions = new TextureRegion[5];
        for(int i = 0; i < 5; i++){
            regions[i] = Core.atlas.find(name + "-" + i);
        }
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    @Override
    public void init(){
        super.init();
        if(junction == null) junction = EndBlocks.essenceJunction;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        int[] bits = getTiling(plan, list);

        if(bits == null) return;

        Draw.alpha(0.5f);
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy(), 0f);
        Draw.scl(bits[1], bits[2]);
        Draw.color();
        Draw.rect(regions[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.scl();
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junction == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && o.rotation == req.rotation && (req.block instanceof EssenceConduit || req.block instanceof EssenceJunction));

        //return super.getReplacement(req, plans);
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof EssenceConduit &&
                Mathf.mod(req.build().rotation - req.rotation, 2) == 1 ? junction : this;
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{bottomRegion, regions[0]};
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        //return otherblock instanceof EndEssenceBlock2 && (!(otherblock instanceof EssenceConduit) || (lookingAt(tile, rotation, otherx, othery, otherblock))) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
        return otherblock instanceof EndEssenceBlock && (lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock) || !(otherblock instanceof EssenceConduit));
    }

    public class EssenceConduitBuild extends EndEssenceBuilding{
        public float smoothEss;
        public int blendbits, xscl = 1, yscl = 1;

        @Override
        public void draw(){
            //int r = this.rotation;
            //float angle = r * 90;

            Draw.z(Layer.blockUnder);
            if(smoothEss > 0.001f){
                Draw.color(baseColor, EndPal.endMid, smoothEss);
            }else{
                Draw.color(baseColor);
            }
            Fill.square(x, y, size * Vars.tilesize / 2f);

            Draw.color();
            Draw.scl(xscl, yscl);
            //drawAt(blendbits);
            int r = this.rotation;
            float angle = r * 90;
            Draw.rect(regions[blendbits], x, y, angle);
            Draw.reset();
        }

        @Override
        public void updateTile(){
            smoothEss = Mathf.lerpDelta(smoothEss, Mathf.clamp(efract()), 0.05f);

            if(essence.essence > 0.001f && timer(timerFlow, 1)){
                essenceFlowForward();
                noSleep();
            }else{
                sleep();
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            xscl = bits[1];
            yscl = bits[2];
            //blending = bits[4];
        }

        @Override
        public boolean acceptEssence(Building source){
            noSleep();
            return (tile == null || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation);
        }
    }
}
