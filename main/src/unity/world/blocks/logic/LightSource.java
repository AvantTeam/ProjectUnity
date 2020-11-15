package unity.world.blocks.logic;

import java.util.ArrayList;
import arc.Events;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.Eachable;
import arc.util.io.*;
import arc.math.geom.*;
import arc.scene.ui.layout.Table;
import mindustry.gen.*;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.Tile;
import mindustry.world.meta.*;
import mindustry.world.blocks.production.GenericCrafter;
import unity.world.blocks.LightData;
import unity.world.blocks.LightRepeaterBuildBase;
import unity.world.blocks.logic.LightGenerator.LightGeneratorBuild;
import unity.world.blocks.logic.LightReflector.LightReflectorBuild;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LightSource extends GenericCrafter{
    public int lightStrength = 60, lightLength = 50, maxLightLength = 5000, maxReflections = 128, lightInterval = 20;
    protected final int reflowTimer = timers++;
    protected Color lightColor = Color.white;
    protected boolean scaleStatus = true;
    public final boolean hasCustomUpdate, angleConfig;
    public TextureRegion baseRegion, topRegion, lightRegion, liquidRegion;

    public LightSource(String name, boolean hasCustomUpdate, boolean angleConfig){
        super(name);
        this.hasCustomUpdate = hasCustomUpdate;
        this.angleConfig = angleConfig;
        update = true;
        rotate = true;
        if(angleConfig){
            rotate = false;
            configurable = true;
            saveConfig = true;
            lastConfig = 0;
            config(Integer.class, (LightSourceBuild build, Integer value) -> {
                build.addAngle(value);
            });
        }
    }

    public LightSource(String name, boolean angleConfig){
        this(name, false, angleConfig);
    }

    public LightSource(String name){
        this(name, false);
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("light", (LightSourceBuild build) -> new Bar(() -> bundle.format("lightlib.light", build.getStrength()),
        () -> lightColor, () -> build.getStrength() / lightStrength));
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, "@ @", bundle.format("lightlib.light", lightStrength), StatUnit.perSecond.localized());
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        final float scl = tilesize * req.animScale * size;
        Draw.rect(baseRegion, req.drawx(), req.drawy(), scl, scl);
        if(configurable){
            if(req.config != null) drawRequestConfig(req, list);
        }else Draw.rect(topRegion, req.drawx(), req.drawy(), scl, scl, req.rotation * 90f);
    }

    @Override
    public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list){
        final float scl = tilesize * req.animScale * size;
        Draw.rect(topRegion, req.drawx(), req.drawy(), scl, scl, req.config == null ? 0 : ((int) req.config) * 45f);
    }

    public class LightSourceBuild extends GenericCrafterBuild{
        protected Tile furthest;
        protected float strength = 0f;
        protected boolean lightInit = false;
        protected int angle = 0, loops;
        protected final ArrayList<Tile> ls = new ArrayList<>();
        protected final ArrayList<LightData> lsData = new ArrayList<>();
        protected final ArrayList<LightGeneratorBuild> lCons = new ArrayList<>();
        protected LightData lightData = new LightData(lightLength, lightColor);

        {
            Events.run(Trigger.draw, () -> {
                if(this != null) drawLightLasers();
            });
        }

        protected void setInit(boolean a){
            lightInit = a;
        }

        protected boolean initDone(){
            return lightInit;
        }

        public float getAngleDeg(){
            return rotate ? rotdeg() : angle * 45f;
        }

        protected int getAngle(){
            return rotate ? rotation * 2 : angle;
        }

        protected void setAngle(int a){
            angle = a;
            lightMarchStart(lightLength, maxLightLength);
        }

        public void addAngle(int a){
            setAngle((angle + a + 8) % 8);
        }

        protected void setStrength(float a){
            strength = a;
        }

        public float getStrength(){
            if(!lightInit) return targetStrength();
            return strength;
        }

        protected float getPowerStatus(){
            if(!hasPower || power == null) return 1f;
            return power.status;
        }

        protected float targetStrength(){
            if(!cons.valid()) return 0f;
            return scaleStatus ? lightStrength * getPowerStatus() : lightStrength;
        }

        @Override
        public void updateTile(){
            setStrength(targetStrength());
            if(!initDone()) lightMarchStart(lightLength, maxLightLength);
            else if(lightInterval <= 0 || timer.get(reflowTimer, lightInterval)){
                if(getStrength() > 1f) lightMarchStart(lightLength, maxLightLength);
                else clearCons();
            }
            if(hasCustomUpdate) lightSourceUpdate();
            else super.updateTile();
        }

        protected void lightSourceUpdate(){

        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(angle);
            lightSourceWrite(write);
        }

        protected void lightSourceWrite(Writes write){

        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read);
            angle = read.b();
            lightSourceRead(read, revision);
        }

        protected void lightSourceRead(Reads read, byte revision){

        }

        @Override
        public void drawLight(){
            Drawf.light(team, x, y, (getStrength() * 0.1f + 60f) * getPowerStatus(), lightColor, 0.8f);
        }

        protected void drawLightLasers(){
            if(!isAdded() || getStrength() <= 1) return;
            Draw.z(Layer.effect - 1f);
            Draw.blend(Blending.additive);

            final float w = 1 + Math.min(getStrength() / 1000f, 10f);
            Lines.stroke(w);
            for(int i = 0, len = ls.size(); i < len; i++){
                LightData tempData = lsData.get(i);
                if(tempData == null) continue;
                Tile tempTile = ls.get(i);

                int tempAngle = tempData.angle;
                float tempLength = tempData.length, tempX = tempTile.worldx(), tempY = tempTile.worldy();
                float a = tempData.strength / 100f * (getStrength() / lightStrength);

                Draw.color(tempData.color, a);
                if(settings.getBool("bloom")) Draw.z(a > 0.99f ? Layer.effect - 1 : Layer.bullet - 2);
                if(i == len - 1 || ls.get(i + 1) == null){
                    if(tempData.length < 1) continue;
                    Lines.line(tempX, tempY, tempX + (tempLength - 1) * tilesize * Geometry.d8(tempAngle).x, tempY + (tempLength - 1) * tilesize * Geometry.d8(tempAngle).y);
                    final float shift = (tempAngle % 2 == 0) ? w / 2f : w / 2.8285f;
                    Drawf.tri(tempX + ((tempLength - 1) * tilesize + shift) * Geometry.d8(tempAngle).x, tempY + ((tempLength - 1) * tilesize + shift) * Geometry.d8(tempAngle).y, w, tempAngle % 2 == 0 ? 8f : 11.313f, tempData.angle * 45f);
                }else{
                    Tile nextTile = ls.get(i + 1);
                    if(lsData.get(i + 1) == null) Lines.line(tempX, tempY, nextTile.worldx() - 4 * Geometry.d8(tempAngle).x, nextTile.worldy() - 4 * Geometry.d8(tempAngle).y);
                    else Lines.line(tempX, tempY, nextTile.worldx(), nextTile.worldy());
                }
            }
            Draw.blend();
            Draw.color();
        }

        protected void clearCons(){
            lCons.forEach(b -> b.removeSource(this));
        }

        protected void lightMarchStart(int length, int maxLength){
            lightData.angle = getAngle();
            lightData.strength = 100f;
            clearCons();
            ls.clear();
            lsData.clear();
            lCons.clear();
            ls.add(tile);
            lsData.add(lightData);
            pointMarch(tile, lightData, length, maxLength, 0);
            setInit(true);
        }

        protected void pointMarch(Tile tile, LightData ld, int length, int maxLength, int num){
            if(length <= 0 || maxLength <= 0 || ld.strength * getStrength() < 1f) return;

            final Point2 dir = Geometry.d8(ld.angle);
            LightData next = new LightData();
            LightData next2 = new LightData();
            furthest = null;
            loops = 0;

            boolean hit = world.raycast(tile.x, tile.y, tile.x + length * dir.x, tile.y + length * dir.y, (x, y) -> {
                furthest = world.tile(x, y);
                if(furthest == tile || furthest == null) return false;
                loops++;

                if(!furthest.solid() || (furthest.block() == block && tile == this.tile)) return false;
                Building build = furthest.bc();
                if(build == null) return true;
                if(build.block instanceof LightDivisor){
                    int tr = ((LightReflectorBuild) build).calcReflection(ld.angle);
                    if(tr >= 0){
                        next.set(ld.angle, ld.strength / 2f, ld.length - loops, ld.color);
                        next2.set(tr, ld.strength / 2f, ld.length - loops, ld.color);
                    }
                }else if(build instanceof LightReflectorBuild){
                    int tr = ((LightReflectorBuild) build).calcReflection(ld.angle);
                    if(tr >= 0) next.set(tr, ld.strength, ld.length - loops, ld.color);
                }else if(build instanceof LightRepeaterBuildBase){
                    LightData tempData = ((LightRepeaterBuildBase) build).calcLight(ld, loops);
                    if(tempData == null) return true;
                    next.set(tempData);
                }else if(build.block instanceof LightGenerator)
                    lCons.add(((LightGeneratorBuild) build).addSource(this, ld));
                return true;
            });

            if(!hit) return;
            if(!next.isIntialized() || num > maxReflections){
                ls.add(furthest);
                lsData.add(null);
            }else if(!next2.isIntialized()){
                ls.add(furthest);
                lsData.add(next);
                pointMarch(furthest, next, ld.length - loops, maxLength - loops, ++num);
            }else{
                ls.add(furthest);
                lsData.add(next);
                Tile forSafety = furthest;
                pointMarch(furthest, next, ld.length - loops, maxLength - loops, ++num);
                ls.add(null);
                lsData.add(null);
                ls.add(forSafety);
                lsData.add(next2);
                pointMarch(forSafety, next2, ld.length - loops, maxLength - loops, ++num);
            }
        }

        @Override
        public Integer config(){
            return angle;
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.leftOpen, Styles.clearTransi, 34f, () -> {
                configure(1);
            }).size(40f);
            table.button(Icon.rightOpen, Styles.clearTransi, 34f, () -> {
                configure(1);
            }).size(40f);
        }
    }
}
