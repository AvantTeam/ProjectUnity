package unity.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.entities.*;
import unity.gen.*;
import unity.graphics.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public abstract class GenericTractorBeamTurret<T extends Teamc> extends BaseTurret{
    public final int timerTarget = timers++;
    public float retargetTime = 5f;

    public TextureRegion baseRegion;
    public float shootCone = 6f;
    public float shootLength = -1f;

    public float powerUse = 1f;
    public float powerUseThreshold = 0f;

    public Sound shootSound = Sounds.tractorbeam;
    public float shootSoundVolume = 0.9f;

    public Color laserColor = UnityPal.monolith;
    public float laserWidth = 0.4f;
    public TextureRegion laser;
    public TextureRegion laserEnd;

    protected GenericTractorBeamTurret(String name){
        super(name);

        rotateSpeed = 20f;
        hasItems = hasLiquids = false;
        hasPower = consumesPower = true;
        acceptCoolant = false;
    }

    @Override
    public void init(){
        consumes.powerCond(powerUse, (GenericTractorBeamTurretBuild build) -> build.target != null);

        super.init();
        if(shootLength < 0) shootLength = size * tilesize / 2f;
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("block-" + size, "unity-block-" + size);
        laser = Core.atlas.find("laser");
        laserEnd = Core.atlas.find("laser-end");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public abstract class GenericTractorBeamTurretBuild extends BaseTurretBuild implements ExtensionHolder, ControlBlock{
        public BlockUnitc unit;
        protected Extension ext;

        public T target;
        public Vec2 targetPos = new Vec2();
        public float strength;

        @Override
        public void created(){
            super.created();
            ext = Extension.create();
            ext.holder = this;
            ext.set(x, y);
            ext.add();

            unit = UnitTypes.block.create(team).as();
            unit.tile(this);
        }

        @Override
        public Unit unit(){
            return unit.as();
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.shoot && !unit.isPlayer()){
                targetPos.set(World.unconv((float)p1), World.unconv((float)p2));
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp && !unit.isPlayer()){
                if(p1 instanceof Posc pos){
                    targetPos.set(pos.x(), pos.y());
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            ext.remove();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(timer(timerTarget, retargetTime)){
                findTarget();
            }

            boolean shoot = false;
            if(target != null && target.within(this, range) && target.team() != team && efficiency() > powerUseThreshold){
                if(!headless){
                    control.sound.loop(shootSound, this, shootSoundVolume);
                }

                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                shoot = Angles.within(rotation, dest, shootCone);
            }

            if(shoot && target != null && efficiency() > powerUseThreshold){
                apply();

                targetPos.set(target.x(), target.y());
                strength = Mathf.lerpDelta(strength, efficiency(), 0.1f);
            }else{
                strength = Mathf.lerpDelta(strength, 0f, 0.1f);
            }
        }

        protected abstract void findTarget();

        protected abstract void apply();

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);
        }

        @Override
        public float clipSizeExt(){
            if(Float.isNaN(targetPos.x) || Float.isNaN(targetPos.y)) return 0f;
            return dst(targetPos.x, targetPos.y) * 2f;
        }

        @Override
        public void drawExt(){
            if(strength > 0.1f){
                Draw.z(Layer.bullet);
                float ang = angleTo(targetPos.x, targetPos.y);

                Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

                Drawf.laser(team, laser, laserEnd,
                x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
                    targetPos.x, targetPos.y, strength * efficiency() * laserWidth);

                Draw.mixcol();
            }
        }
    }
}
