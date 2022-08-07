package unity.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.util.*;
import unity.world.meta.*;
import unity.world.meta.CrucibleRecipes.*;

import static mindustry.Vars.content;

public class CrucibleCaster extends GenericCaster{
    TextureRegion floor, platter, platterSide, liquid, castLiquid;
    TextureRegion[] base;
    public Vec2[] itemPos = {
    new Vec2(0.4f * 8, 0.4f * 8),
    new Vec2(-0.4f * 8, 0.4f * 8),
    new Vec2(-0.4f * 8, -0.4f * 8),
    new Vec2(0.4f * 8, -0.4f * 8),
    };

    public CrucibleCaster(String name){
        super(name);
        hasItems = true;

    }

    @Override
    public void load(){
        super.load();
        floor = loadTex("floor");
        platter = loadTex("platter");
        platterSide = loadTex("platterSide");
        liquid = loadTex("liquid");
        castLiquid = loadTex("cast-liquid");
        base = new TextureRegion[4];
        base[0] = loadTex("base1");
        base[1] = loadTex("base2");
        base[2] = loadTex("base3");
        base[3] = loadTex("base4");
    }

    @Override
    public boolean rotatedOutput(int x, int y){
        return false;
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class CrucibleCasterBuild extends GenericCasterBuild{
        public Item currentCast;

        @Override
        public boolean isCasting(){
            return currentCast != null;
        }

        @Override
        public void tryStartCast(){
            var crucible = crucibleNode();
            for(var fluid : crucible.fluids){
                if(fluid.key instanceof CrucibleItem item && fluid.value.melted >= 4){
                    currentCast = item.item;
                    crucible.getFluid(CrucibleRecipes.items.get(currentCast)).melted -= 4;
                    break;
                }
            }
        }

        @Override
        public void offloadCast(){
            for(int i = 0; i < 4; i++){
                offload(currentCast);
            }
        }

        @Override
        public void resetCast(){
            currentCast = null;
        }

        @Override
        public boolean canOffloadCast(){
            return items.empty();
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && canOffloadCast();
        }

        @Override
        public boolean productionValid(){
            return currentCast != null;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            dumpOutputs();
        }

        public void dumpOutputs(){
            if(timer(timerDump, dumpTime / timeScale)){
                dump(null);
            }
        }

        public void offload(Item item){
            this.produced(item, 1);
            int dump = this.cdump;
            if(!Vars.net.client() && Vars.state.isCampaign() && this.team == Vars.state.rules.defaultTeam){
                item.unlock();
            }
            Tile t = frontTile();
            var dir = Geometry.d4(rotation + 1);
            int offset = this.block.size / 2;
            Point2 origin = new Point2(t.x - dir.x * offset, t.y - dir.y * offset);
            for(int i = 0; i < this.block.size; ++i){
                Building other = Vars.world.build(origin.x + dir.x * (i + dump) % this.block.size, origin.y + dir.y * (i + dump) % this.block.size);
                if(other == null){
                    continue;
                }
                this.incrementDump(this.proximity.size);

                if(other.team == this.team && other.acceptItem(this, item) && this.canDump(other, item)){
                    other.handleItem(this, item);
                    return;
                }
            }
            this.handleItem(this, item);
        }

        public Tile frontTile(){
            int trns = this.block.size / 2 + 1;
            return Vars.world.tile(this.tile.x + Geometry.d4(this.rotation).x * trns, this.tile.y + Geometry.d4(this.rotation).y * trns);
        }


        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }


        @Override
        public void draw(){
            var crucible = crucibleNode();
            Draw.rect(floor, x, y);

            if(currentCast != null){
                //items
                //cast liquid
                float castProg = Mathf.curve(progress, 0, castTime);
                float itemSize = Vars.itemSize * castProg;
                if(progress < castTime){
                    Draw.rect(platter, x, y);
                    Draw.color(crucible.getColor(), 4 * Mathf.sqr(castProg));
                    Draw.rect(castLiquid, x, y);
                    Draw.color();
                    for(Vec2 itemPo : itemPos){
                        Draw.rect(currentCast.fullIcon, x + itemPo.x, y + itemPo.y, itemSize, itemSize);
                    }
                }else{
                    float moveProg = Mathf.curve(progress, castTime, castTime + moveTime);

                    float ang = MathUtils.interp(0, 180, moveProg);

                    if(ang < 90){
                        DrawUtils.drawRectOrtho(platter, x, y, -2, platter.width * 0.25f, platter.height * 0.25f, ang, rotdeg());
                    }else{
                        DrawUtils.drawRectOrtho(platter, x, y, 2, platter.width * 0.25f, platter.height * 0.25f, ang, rotdeg());
                    }
                    DrawUtils.drawRectOrtho(platterSide, x, y, -8, 4, 16, ang - 90, rotdeg());

                    if(ang < 90){
                        for(Vec2 itemPo : itemPos){
                            DrawUtils.drawRectOrtho(currentCast.fullIcon, x, y, itemPo.x, itemPo.y, -3, itemSize, itemSize, ang, rotdeg());
                        }
                    }
                }

            }else{
                Draw.rect(platter, x, y);
            }

            Draw.rect(base[rotation], x, y);
            Draw.color(crucible.getColor());
            Draw.rect(liquid, x, y, rotdeg());
            Draw.color();

            drawTeamTop();
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            currentCast = content.item(read.s());
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(currentCast == null ? -1 : currentCast.id);
        }
    }
}
