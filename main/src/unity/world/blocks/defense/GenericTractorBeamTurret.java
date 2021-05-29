package unity.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
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

    public abstract class GenericTractorBeamTurretBuild extends BaseTurretBuild implements ExtensionHolder, ControlBlock, Senseable{
        public BlockUnitc unit;
        protected Extension ext;

        public T target;
        public Vec2 targetPos = new Vec2();
        public float strength;

        public float logicControlTime = -1f;
        public boolean logicShooting = false;

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
                logicControlTime = Turret.logicControlCooldown;
                logicShooting = !Mathf.zero(p3);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp && !unit.isPlayer()){
                logicControlTime = Turret.logicControlCooldown;
                logicShooting = !Mathf.zero(p2);

                if(p1 instanceof Posc pos){
                    targetPos.set(pos.x(), pos.y());
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case rotation -> rotation;
                case shootX -> World.conv(targetPos.x);
                case shootY -> World.conv(targetPos.y);
                case shooting -> isShooting() ? 1 : 0;
                default -> super.sense(sensor);
            };
        }

        public boolean isShooting(){
            return (isControlled() ? unit.isShooting() : logicControlled() ? logicShooting : (canShoot() && target != null));
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            ext.remove();
        }

        @Override
        public void updateTile(){
            if(!validateTarget()) target = null;

            unit.health(health);
            unit.rotation(rotation);
            unit.team(team);
            unit.set(x, y);

            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            boolean shot = false;
            if(canShoot()){
                if(!logicControlled() && !isControlled() && timer(timerTarget, retargetTime)){
                    findTarget();
                }

                if(validateTarget()){
                    float targetRot = angleTo(targetPos);
                    turnToTarget(targetRot);

                    boolean shoot = true;
                    if(isControlled()){
                        targetPos.set(unit.aimX(), unit.aimX());
                        shoot = unit.isShooting();
                    }else if(logicControlled()){
                        shoot = logicShooting;
                    }else{
                        targetPos.set(target.x(), target.y());

                        if(Float.isNaN(rotation)){
                            rotation = 0;
                        }
                    }

                    if(shoot && Angles.angleDist(rotation, targetRot) < shootCone){
                        shot = updateShooting();
                    }
                }
            }

            if(shot){
                strength = Mathf.lerpDelta(strength, efficiency(), 0.1f);
            }else{
                strength = Mathf.lerpDelta(strength, 0f, 0.1f);
            }
        }

        protected boolean updateShooting(){
            if(logicControlled() || isControlled()){
                findTarget(targetPos);
            }

            if(target != null){
                control.sound.loop(shootSound, this, shootSoundVolume);

                apply();
                return true;
            }else{
                return false;
            }
        }

        protected void turnToTarget(float targetRot){
            rotation = Angles.moveToward(rotation, targetRot, rotateSpeed * edelta());
        }

        protected boolean validateTarget(){
            return !Units.invalidateTarget(target, team, x, y) || isControlled() || logicControlled();
        }

        public boolean logicControlled(){
            return logicControlTime > 0;
        }

        public boolean canShoot(){
            return efficiency() > powerUseThreshold;
        }

        protected abstract void findTarget();

        protected abstract void findTarget(Vec2 pos);

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
