package unity.world.blocks.exp;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import unity.content.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class ExpNode extends ExpTank {
    public int range = 5;
    public float reloadTime = 600f;
    public float warmupTime = 35f;

    public int minExp = 10;
    public Effect shootEffect = UnityFx.expPoof;
    public Color lightClearColor;

    private static final Seq<ExpHolder> tmps = new Seq<>();
    private static int tmpm = 0;
    private final Color tmpc = new Color();

    public ExpNode(String name){
        super(name);
        lightColor = UnityPal.exp;
    }

    @Override
    public void init(){
        super.init();
        lightRadius = range * tilesize;
        clipSize = Math.max(clipSize, (range * 2f + 2f) * tilesize);
        lightClearColor = lightColor.cpy().a(0);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, 60 / reloadTime, StatUnit.perSecond);
        stats.add(Stat.powerRange, range, StatUnit.blocks);
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("links", (ExpNodeBuild entity) -> new Bar(() -> Core.bundle.format("bar.reloading", (int)(100 * Mathf.clamp(entity.reload / reloadTime))), () -> Pal.accent, () -> Mathf.clamp(entity.reload / reloadTime)));
    }

    public class ExpNodeBuild extends ExpTankBuild{
        public float reload = 0;
        public float warmup = 0f;
        public boolean shooting = false;

        @Override
        public void updateTile(){
            if(shooting){
                warmup += Time.delta;
                if(warmup >= warmupTime){
                    shoot();
                    shooting = false;
                    reload = 0f;
                }
            }
            else reload += delta();

            if(reload >= reloadTime && !shooting && exp >= minExp){
                reload = warmup = 0f;
                shooting = true;
                shootEffect.at(this);
            }
        }

        public void shoot(){
            tmps.clear();
            tmpm = -1;
            Geometry.circle(tile.x, tile.y, range + 1, (x, y) -> {
                Building other = world.build(x, y);
                if(other != null && other.team == team && other instanceof ExpHolder exp && !exp.hubbable() && (tmpm == -1 ||exp.getExp() <= tmpm)){
                    if(exp.getExp() < tmpm) tmps.clear(); //previous blocks are all invalid
                    tmps.add(exp);
                    tmpm = exp.getExp();
                }
            });

            if(tmps.isEmpty()) return;
            int amount = Mathf.ceilPositive(exp / (float)tmps.size);
            for(ExpHolder e : tmps){
                if(exp <= 0) continue;
                int a = e.handleExp(amount);
                exp -= a;
            }
        }

        @Override
        public void draw(){
            super.draw();

            if(shooting){
                Draw.z(Layer.effect + 0.011f);
                float r = range * tilesize * fin();
                Fill.light(x, y, Lines.circleVertices(r), r, lightClearColor, tmpc.set(lightColor).a(Mathf.clamp(2f * (1 - fin()))));
            }
        }

        @Override
        public void drawLight(){
            super.drawLight();

            if(shooting){
                float r = range * tilesize * fin();
                Drawf.light(x, y, r, lightColor, Mathf.clamp(2f * (1 - fin())));
            }
        }

        public float fin(){
            return Mathf.clamp(warmup / warmupTime);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(reload);
            write.f(warmup);
            write.bool(shooting);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            reload = read.f();
            warmup = read.f();
            shooting = read.bool();
        }
    }
}
