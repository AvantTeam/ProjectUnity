package unity.world.blocks.payloads;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.payloads.PayloadBlock.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import unity.net.*;
import unity.parts.*;
import unity.util.*;
import unity.world.blocks.*;
import unity.world.graph.*;

import static mindustry.Vars.tilesize;
import static unity.world.blocks.payloads.PayloadArm.PayloadArmBuild.*;

public class PayloadArm extends GenericGraphBlock{
    public float range = 3.5f;
    public float maxSize = 8 * 8;
    public float moveTime = 60;
    public float rotateTime = 30;
    public int armJoints = 1;

    TextureRegion[] rotateIcons = new TextureRegion[4];
    TextureRegion[] armSegments;
    TextureRegion base, top, claw;

    public PayloadArm(String name){
        super(name);
        configurable = true;
        sync = true;
        rotate = true;
        solid = true;
        config(Point2[].class, (PayloadArmBuild build, Point2[] value) -> {
            build.from = value[0];
            build.to = value[1];
            build.rotateTargetBy = Math.abs(value[2].x) + Math.abs(value[2].y);
            build.recalcPositions();
            build.switchState(build.state);
        });
        config(Long.class, (PayloadArmBuild build, Long value) -> {
            int i1 = (int)(value >> 32);
            Point2 i2 = Point2.unpack((int)(value & 0xFFFFFFFFL));
            i2.sub(256, 256);
            switch(i1){
                case SEL_IN -> {
                    if(i2.equals(0, 0)){
                        break;
                    }
                    build.from.set(i2);
                    build.recalcPositions();
                    switch(build.state){
                        case MOVING_TO_PICKUP, PICKING_UP -> build.switchState(ArmState.MOVING_TO_PICKUP);
                    }
                }
                case SEL_OUT -> {
                    if(i2.equals(0, 0)){
                        break;
                    }
                    build.to.set(i2);
                    build.recalcPositions();
                    switch(build.state){
                        case MOVING_TO_TARGET, DROPPING, ROTATING_TARGET -> build.switchState(ArmState.MOVING_TO_TARGET);
                    }
                }
                case 2 -> {
                    build.rotateTargetBy++;
                    if(build.rotateTargetBy > 3){
                        build.rotateTargetBy = 0;
                    }
                    build.growAnim = 0;
                }
            }
        });
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, (range) * tilesize + 2);
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < rotateIcons.length; i++){
            rotateIcons[i] = Core.atlas.find("unity-rotate" + (i + 1));
        }
        armSegments = new TextureRegion[armJoints + 1];
        for(int i = 0; i < armSegments.length; i++){
            armSegments[i] = loadTex("seg" + (i));
        }
        base = loadTex("base");
        top = loadTex("top");
        claw = loadTex("claw");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = Vars.world.tile(x, y);
        if(tile != null){
            Lines.stroke(1.0F);
            Draw.color(Pal.placing);
            Drawf.circles((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range * 8.0F);
        }
    }

    public boolean canPickupBlock(Block b){
        return !(b instanceof ConstructBlock) && !(b instanceof CoreBlock) && b.synthetic();
    }

    enum ArmState{
        PICKING_UP, MOVING_TO_TARGET, ROTATING_TARGET, DROPPING, MOVING_TO_PICKUP
    }

    public class PayloadArmBuild extends GenericGraphBuild{
        public float progress, progressInterop;
        public float armBaseRotation, armExtend, payloadRotation;
        public float targetArmBaseRotation, targetArmExtend, targetPayloadRotation;
        Vec2[] calculatedPositions = new Vec2[2];
        public Point2 from, to;
        public int rotateTargetBy = 0;
        ArmState state = ArmState.PICKING_UP;
        public Payload carrying;

        public transient boolean selected = false;
        public transient int ioSelect = -1;
        final static int SEL_IN = 0;
        final static int SEL_OUT = 1;

        public transient float growAnim = 0;

        public transient ZipperArm arm;
        public transient float clawOpen = 0;

        @Override
        public void onPlaced(){
            super.onPlaced();
            recalcPositions();
        }

        @Override
        public void onInit(){
            arm = new ZipperArm(0, 0, 1, 1, range * tilesize + 4, armJoints);
            targetArmBaseRotation = armBaseRotation = (180 + rotdeg()) % 360;
            targetArmExtend = armExtend = tilesize;
            if(from == null){
                from = new Point2(-Geometry.d4x(rotation), -Geometry.d4y(rotation));
                to = new Point2(Geometry.d4x(rotation), Geometry.d4y(rotation));
            }
            Events.on(EventType.TapEvent.class, e -> {
                var thisBuild = PayloadArmBuild.this;
                if(Vars.net.server()){
                    return;
                }
                if(!selected){
                    if(Vars.control.input.config.getSelected() == thisBuild){
                        deselect();
                    }
                    return;
                }
                if(e.tile == null || e.tile.dst(tile) > range * tilesize){
                    selected = false;
                    deselect();
                    ioSelect = -1;
                    return;
                }
                Point2 relpt = new Point2(e.tile.x - tile.x, e.tile.y - tile.y);
                if(ioSelect != -1 && !relpt.equals(0, 0)){
                    configure(getCode(relpt));
                    ioSelect = -1;
                }else{
                    if(relpt.equals(from)){
                        ioSelect = SEL_IN;
                    }else if(relpt.equals(to)){
                        ioSelect = SEL_OUT;
                    }else if(relpt.equals(0, 0)){
                        ioSelect = 2;
                        configure(getCode(relpt));
                    }else{
                        selected = false;
                        deselect();
                    }
                }
            });
        }

        long getCode(Point2 pt){
            return ((long)ioSelect << 32) | (Point2.pack(pt.x + 256, pt.y + 256));
        }

        private void setTargetNoReset(Vec2 tar){
            targetArmBaseRotation = tar.x;
            targetArmExtend = tar.y;
        }

        private void setTarget(Vec2 tar){
            armBaseRotation = targetArmBaseRotation;
            armExtend = targetArmExtend;
            targetArmBaseRotation = tar.x;
            targetArmExtend = tar.y;
        }

        public void getRelTilePos(Vec2 in, int x, int y){
            float o = ((size + 1) % 2) * 4;
            in.set(x * tilesize - o, y * tilesize - o);
        }

        public void recalcPositions(){
            getRelTilePos(Tmp.v1, from.x, from.y);
            calculatedPositions[0] = new Vec2(Mathf.atan2(Tmp.v1.x, Tmp.v1.y) * Mathf.radiansToDegrees, Tmp.v1.len());
            getRelTilePos(Tmp.v1, to.x, to.y);
            calculatedPositions[1] = new Vec2(Mathf.atan2(Tmp.v1.x, Tmp.v1.y) * Mathf.radiansToDegrees, Tmp.v1.len());
        }

        @Override
        public void drawSelect(){
            if(!selected){
                super.drawSelect();
                float[] poss = new float[]{(tile.x + from.x) * tilesize, (tile.y + from.y) * tilesize, (tile.x + to.x) * tilesize, (tile.y + to.y) * tilesize};
                DrawUtils.selected(poss[0], poss[1], 1, Color.cyan);
                DrawUtils.selected(poss[2], poss[3], 1, Color.orange);
                float offset = Time.globalTime * 0.03f % 1.0f;
                float i1, i2, r, r2, l, l2;
                for(float i = 0; i <= 1; i += 0.1){
                    i1 = Mathf.clamp(i + offset * 0.1f);
                    i2 = Mathf.clamp(i + offset * 0.1f + 0.05f);
                    r = Mathf.lerp(calculatedPositions[0].x, calculatedPositions[1].x, i1);
                    r2 = Mathf.lerp(calculatedPositions[0].x, calculatedPositions[1].x, i2);
                    l = Mathf.lerp(calculatedPositions[0].y, calculatedPositions[1].y, i1);
                    l2 = Mathf.lerp(calculatedPositions[0].y, calculatedPositions[1].y, i2);
                    Lines.line(x + Mathf.cosDeg(r) * l, y + Mathf.sinDeg(r) * l, x + Mathf.cosDeg(r2) * l2, y + Mathf.sinDeg(r2) * l2);
                }


                Lines.stroke(1.0F);
                Draw.color(Pal.placing);
                Drawf.circles(x, y, range * 8.0F);
                Draw.color();
            }
        }


        @Override
        public void drawConfigure(){
            super.drawConfigure();
            float[] poss = new float[]{(tile.x + from.x) * tilesize, (tile.y + from.y) * tilesize, (tile.x + to.x) * tilesize, (tile.y + to.y) * tilesize};
            float s1 = Math.abs(Mathf.sin(Time.globalTime * 0.1f));
            float s2 = s1;
            if(ioSelect == 0){
                s1 = -0.5f;
            }
            if(ioSelect == 1){
                s2 = -0.5f;
            }
            growAnim += (1 - growAnim) * 0.3f;
            DrawUtils.selected(poss[0], poss[1], s1, Color.cyan);
            DrawUtils.selected(poss[2], poss[3], s2, Color.orange);
            if(rotateTargetBy != 0){
                Draw.rect(rotateIcons[rotateTargetBy - 1], poss[2], poss[3], growAnim * tilesize, growAnim * tilesize);
            }
            Lines.stroke(1.0F);
            Draw.color(Pal.placing);
            Drawf.circles(x, y, range * 8.0F);
            Draw.color();
            Drawf.shadow(x, y, 8);
            Draw.rect(rotateIcons[3], x, y, growAnim * tilesize, growAnim * tilesize, growAnim * 180);
        }

        public void switchState(ArmState state){
            if(calculatedPositions[0] == null){
                recalcPositions();
            }
            if(this.state == state){
                switch(state){
                    case MOVING_TO_TARGET -> setTargetNoReset(calculatedPositions[1]);
                    case DROPPING -> payloadRotation = targetPayloadRotation;
                    case PICKING_UP, MOVING_TO_PICKUP -> setTargetNoReset(calculatedPositions[0]);
                }
                return;
            }
            this.state = state;

            switch(state){
                case ROTATING_TARGET:
                    payloadRotation = carrying.rotation();
                    targetPayloadRotation = carrying.rotation() + rotateTargetBy * 90;
                case MOVING_TO_TARGET:
                    setTarget(calculatedPositions[1]);
                    break;
                case DROPPING:
                    payloadRotation = targetPayloadRotation;
                    break;
                case PICKING_UP:
                case MOVING_TO_PICKUP:
                    setTarget(calculatedPositions[0]);
                    break;
            }
            progress = 0;
        }

        public float getCurrentArmRotation(){
            return Mathf.lerp(armBaseRotation, targetArmBaseRotation, progressInterop);
        }

        public float getCurrentArmExtend(){
            return Mathf.lerp(armExtend, targetArmExtend, progressInterop);
        }

        public void grabBuild(BuildPayload p){
            carrying = p;
            payloadRotation = p.build.rotdeg();
            targetPayloadRotation = p.build.rotdeg();
            Fx.unitPickup.at(p.build);
            switchState(ArmState.MOVING_TO_TARGET);
        }

        public void grabUnit(UnitPayload p){
            carrying = p;
            payloadRotation = carrying.rotation();
            targetPayloadRotation = carrying.rotation();
            Fx.unitPickup.at(p.unit);
            switchState(ArmState.MOVING_TO_TARGET);
        }

        public void grabUnit(Unit unit){
            grabUnit(new UnitPayload(unit));
        }

        public void dropBlock(){
            switchState(ArmState.MOVING_TO_PICKUP);
            carrying = null;
        }

        @Override
        public BlockStatus status(){
            if(!enabled){
                return BlockStatus.logicDisable;
            }

            if(efficiency > 0){
                BlockStatus status = switch(state){
                    case PICKING_UP -> {
                        if(efficiency < 0.1) yield BlockStatus.noInput;
                        else if(carrying != null) yield BlockStatus.active;
                        else yield BlockStatus.noOutput;
                    }
                    case MOVING_TO_TARGET, ROTATING_TARGET, MOVING_TO_PICKUP -> BlockStatus.active;
                    case DROPPING -> {
                        if(carrying != null) yield BlockStatus.noOutput;
                        else yield BlockStatus.noInput;
                    }
                };
                return status;
            }

            return BlockStatus.noInput;
        }

        @Override
        public void updateEfficiencyMultiplier(){
            TorqueGraphNode tNode = torqueNode();
            efficiency = Mathf.clamp(tNode.getGraph().lastVelocity / tNode.maxSpeed);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(selected){
                if(Vars.control.input.config.getSelected() != this || !Vars.control.input.config.isShown()){
                    Vars.control.input.config.showConfig(this); // force config to be open....
                }
            }
            switch(state){
                case PICKING_UP:
                    if(carrying == null){
                        if(efficiency < 0.1){
                            break;
                        }
                        Tile t = Vars.world.tile(tile.x + from.x, tile.y + from.y);
                        if(isPayload()){
                            t = Vars.world.tile(Mathf.floor(x / tilesize) + from.x, Mathf.floor(y / tilesize) + from.y);
                        }
                        if(t != null && t.build != null){
                            if(t.build.block.outputsPayload || t.build instanceof PayloadBlockBuild){
                                //theres a payload block to recieve from...
                                //make sure its in range of the arm first.
                                if(t.build.getPayload() != null && Mathf.sqr(t.build.getPayload().size()) <= maxSize + 0.01f && t.build.getPayload().dst((tile.x + from.x) * tilesize, (tile.y + from.y) * tilesize) < 5){
                                    carrying = t.build.takePayload();
                                    payloadRotation = carrying.rotation();
                                    targetPayloadRotation = carrying.rotation();
                                    switchState(ArmState.MOVING_TO_TARGET);
                                }
                            }else if(!Vars.net.client() && t.build != this && t.build.block.size * t.build.block.size * 8 * 8 <= maxSize && canPickupBlock(t.block())){
                                ///theres a block we can grab directly...
                                Building build = t.build;
                                build.pickedUp();
                                BuildPayload bp = new BuildPayload(build);
                                UnityCalls.blockGrabbedByArm(t, bp, this);  // buildings should not modify the world on client, for that results in desyncs.
                                grabBuild(bp);
                                break;
                            }
                        }
                        //maybe theres a unit nearby owo ....
                        if(!Vars.net.client() && t != null){
                            var unit = Units.closest(this.team, t.worldx(), t.worldy(), 7, (u) -> u.isAI() && u.isGrounded());
                            if(unit == null || Mathf.sqr(unit.hitSize()) > maxSize + 0.01f){
                                break;
                            }
                            UnitPayload unitPayload = new UnitPayload(unit);
                            UnityCalls.unitGrabbedByArm(unitPayload, this);// buildings should not modify the world on client, for that results in desyncs.
                            grabUnit(unitPayload);
                        }
                        break;
                    }else{
                        switchState(ArmState.MOVING_TO_TARGET);
                    }
                    break;
                case MOVING_TO_TARGET:
                    progress += Time.delta * efficiency / moveTime;
                    if(progress >= 1){
                        progress = 1;
                        switchState(ArmState.ROTATING_TARGET);
                    }
                    break;
                case ROTATING_TARGET:
                    progress += Time.delta * efficiency / rotateTime;
                    if(carrying instanceof BuildPayload buildp){
                        if(!buildp.block().rotate){
                            switchState(ArmState.DROPPING);
                            break;
                        }
                    }
                    if(rotateTargetBy == 0){
                        switchState(ArmState.DROPPING);
                    }
                    if(progress >= 1){
                        progress = 1;
                        if(carrying instanceof BuildPayload buildp){
                            buildp.build.rotation = (buildp.build.rotation + rotateTargetBy) % 4;
                        }else if(carrying instanceof UnitPayload unitp){
                            //unitp.unit.rotation
                        }
                        switchState(ArmState.DROPPING);
                    }
                    break;
                case DROPPING:
                    if(carrying == null){
                        //uh.
                        switchState(ArmState.MOVING_TO_PICKUP);
                    }
                    Tile t = Vars.world.tile(tile.x + to.x, tile.y + to.y);
                    if(isPayload()){
                        t = Vars.world.tile(Mathf.floor(x / tilesize) + to.x, Mathf.floor(y / tilesize) + to.y);
                    }
                    if(t == null){
                        break;
                    }
                    if(t.build != null){
                        //there's a payload block to push to...
                        if(t.build.acceptPayload(this, carrying)){
                            t.build.handlePayload(this, carrying);
                            carrying = null;
                            switchState(ArmState.MOVING_TO_PICKUP);
                        }
                    }else{
                        Vec2 targetOut = new Vec2(t.worldx(), t.worldy());
                        if(carrying instanceof UnitPayload unitp){
                            carrying.set(targetOut.x, targetOut.y, carrying.rotation());
                            if(unitp.dump()){
                                Fx.unitDrop.at(targetOut.x, targetOut.y);
                                switchState(ArmState.MOVING_TO_PICKUP);
                                carrying = null;
                            }
                        }else if(carrying instanceof BuildPayload buildp){
                            if(!Vars.net.client()){
                                if(Build.validPlace(buildp.block(), buildp.build.team, t.x, t.y, buildp.build.rotation, false)){ // place on the ground
                                    UnityCalls.blockDroppedByArm(t, buildp, this);
                                    dropBlock();
                                }
                            }
                        }
                    }
                    break;
                case MOVING_TO_PICKUP:
                    progress += Time.delta * efficiency / moveTime;
                    if(progress >= 1){
                        progress = 1;
                        switchState(ArmState.PICKING_UP);
                    }
                    break;
            }
            progressInterop = MathUtils.interp(0, 1, Mathf.clamp(progress));
            arm.start.set(0, 0);
            arm.end.set(Mathf.cosDeg(getCurrentArmRotation()) * getCurrentArmExtend(), Mathf.sinDeg(getCurrentArmRotation()) * getCurrentArmExtend());
            arm.update();
            clawOpen += ((carrying == null ? 1 : 0) - clawOpen) * 0.1f;

            if(carrying != null){
                carrying.set(
                arm.end.x + x,
                arm.end.y + y,
                Mathf.lerp(payloadRotation, targetPayloadRotation, progressInterop)
                );
            }
        }


        @Override
        public void draw(){
            Draw.rect(base, x, y, this.get2SpriteRotationVert());
            Draw.z(Layer.power - 1);
            if(carrying != null){
                if(carrying instanceof BuildPayload bp && (state.equals(ArmState.ROTATING_TARGET) || bp.build instanceof ConveyorBuild)){
                    bp.drawShadow(1.0F);
                    bp.build.tile = Vars.emptyTile;
                    Draw.rect(bp.icon(), bp.x(), bp.y(), bp.build.payloadRotation + bp.build.rotdeg());
                }else{
                    carrying.draw();
                }
            }
            Draw.color();
            Draw.z(Layer.power);
            for(int i = 0; i < 4; i++){
                float ang = i * 90 + Mathf.lerp(payloadRotation, targetPayloadRotation, progressInterop);
                Draw.rect(claw, arm.end.x + x + Mathf.cosDeg(ang) * clawOpen, arm.end.y + y + Mathf.sinDeg(ang) * clawOpen, ang);
            }
            Lines.stroke(4);
            for(int i = 0; i < arm.joints; i++){
                if(i == 0){
                    Lines.line(armSegments[0], arm.start.x + x, arm.start.y + y, arm.jointPositions[0].x + x, arm.jointPositions[0].y + y, true);
                }
                if(i == arm.joints - 1){
                    Lines.line(armSegments[i + 1], arm.end.x + x, arm.end.y + y, arm.jointPositions[i].x + x, arm.jointPositions[i].y + y, false);
                }else{
                    Lines.line(armSegments[i], arm.jointPositions[i].x + x, arm.jointPositions[i].y + y, arm.jointPositions[i + 1].x + x, arm.jointPositions[i + 1].y + y, true);
                }
            }
            Drawf.spinSprite(top, x, y, getCurrentArmRotation());
            drawTeamTop();
        }

        public ArmState getState(){
            return state;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            return false;
        }

        @Override
        public boolean configTapped(){
            selected = true;
            return super.configTapped();
        }

        @Override
        public Payload getPayload(){
            return carrying;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            Payload.write(carrying, write);
            write.f(progress);
            write.i(from.pack());
            write.i(to.pack());
            write.s(rotateTargetBy);
            write.s(state.ordinal());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            carrying = Payload.read(read);
            progress = read.f();
            from = Point2.unpack(read.i());
            to = Point2.unpack(read.i());
            recalcPositions();
            rotateTargetBy = read.s();
            switchState(ArmState.values()[read.s()]);
        }

        @Override
        public Point2[] config(){
            Point2[] out = new Point2[3];
            out[0] = from.cpy();
            out[1] = to.cpy();
            out[2] = new Point2(0, rotateTargetBy);
            return out;
        }
    }
}
