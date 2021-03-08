package unity.entities.units;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.ai.*;

import java.nio.*;

public class KamiUnit extends UnitEntity{
    public Bullet laser;
    public float laserRotation = 0f;
    private NewKamiAI trueController;

    private float laserRotationLast;
    private float laserRotationTarget;

    @Override
    public void update(){
        super.update();
        if(laser != null){
            laser.rotation(laserRotation);
        }
        if(trueController.unit == this){
            trueController.updateUnit();
        }
    }

    @Override
    public float clipSize(){
        return super.clipSize() * 3f;
    }

    @Override
    public void draw(){
        super.draw();
        if(trueController != null){
            float z = Draw.z();
            Draw.z(Layer.flyingUnit);
            trueController.draw();
            Draw.z(z);
            Draw.reset();
        }
    }

    @Override
    public void add(){
        if(isAdded()) return;
        super.add();
        trueController = new NewKamiAI();
        trueController.unit(this);
    }

    @Override
    public void damage(float amount){
        super.damage(amount);
        if(trueController.unit == this && !trueController.waiting) trueController.stageDamage += amount;
    }

    @Override
    public void snapInterpolation(){
        super.snapInterpolation();
        laserRotationLast = laserRotation;
        laserRotationTarget = laserRotation;
    }

    @Override
    public void snapSync(){
        super.snapSync();
        laserRotationLast = laserRotation;
        laserRotationTarget = laserRotation;
    }

    @Override
    public void writeSync(Writes write){
        super.writeSync(write);
        write.f(laserRotation);
    }

    @Override
    public void readSync(Reads read){
        super.readSync(read);
        laserRotationLast = laserRotation;
        laserRotationTarget = read.f();
    }

    @Override
    public void writeSyncManual(FloatBuffer buffer){
        super.writeSyncManual(buffer);
        buffer.put(laserRotation);
    }

    @Override
    public void readSyncManual(FloatBuffer buffer){
        super.readSyncManual(buffer);
        laserRotationLast = laserRotation;
        laserRotationTarget = buffer.get();
    }

    @Override
    public void interpolate(){
        super.interpolate();

        if(lastUpdated != 0 && updateSpacing != 0) {
            float timeSinceUpdate = Time.timeSinceMillis(lastUpdated);
            float alpha = Math.min(timeSinceUpdate / updateSpacing, 2f);
            laserRotation = Mathf.slerp(laserRotationLast, laserRotationTarget, alpha);
        }else if(lastUpdated != 0){
            laserRotation = laserRotationTarget;
        }
    }
}
