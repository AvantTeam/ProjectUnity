package unity.entities.units;

import arc.util.io.*;
import mindustry.gen.*;

import java.nio.*;

public class KamiUnit extends UnitEntity{
    public Bullet laser;
    public float laserRotation = 0f;

    @Override
    public void update(){
        super.update();
        if(laser != null){
            laser.rotation(laserRotation);
        }
    }

    @Override
    public void writeSync(Writes write){
        super.writeSync(write);
        write.f(laserRotation);
    }

    @Override
    public void readSync(Reads read){
        super.readSync(read);
        laserRotation = read.f();
    }

    @Override
    public void writeSyncManual(FloatBuffer buffer){
        super.writeSyncManual(buffer);
        buffer.put(laserRotation);
    }

    @Override
    public void readSyncManual(FloatBuffer buffer){
        super.readSyncManual(buffer);
        laserRotation = buffer.get();
    }
}
