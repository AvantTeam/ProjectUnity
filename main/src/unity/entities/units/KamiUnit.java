package unity.entities.units;

import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;

import java.nio.*;

public class KamiUnit extends UnitEntity{
    public Bullet laser;
    public float laserRotation = 0f;

    private float laserRotationLast;
    private float laserRotationTarget;

    @Override
    public void update(){
        super.update();
        if(laser != null){
            laser.rotation(laserRotation);
        }
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
