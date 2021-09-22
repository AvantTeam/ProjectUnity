package unity.type;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.entities.bullet.*;

public class TentacleType implements Cloneable{
    public String name;

    public float x, y, rotationOffset, segmentLength;
    public float speed, accel, rotationSpeed, drag = 0.06f, angleLimit = 65f, firstSegmentAngleLimit = 35f;
    public int segments = 10;
    public boolean automatic = true;
    public boolean mirror = true, top = false, flipSprite;

    public float swayScl = 110f, swayMag = 0.6f, swaySegmentOffset = 1.5f, swayOffset = 0f;

    public BulletType bullet;
    public Sound shootSound;
    public float tentacleDamage = -1f;
    public float startVelocity = 2f;
    public float bulletDuration = -1f;
    public boolean continuous = false;
    public float reload = 60f, range = 220f, shootCone = 15f;

    public TextureRegion region, tipRegion;

    public TentacleType(String name){
        this.name = name;
    }

    public float range(){
        return ((segmentLength * segments) - 5f) + (bullet != null ? (bullet.range() * 0.75f) : 0f);
    }

    public static void set(Seq<TentacleType> seq){
        Seq<TentacleType> mapped = new Seq<>();
        for(TentacleType t : seq){
            mapped.add(t);

            if(t.mirror){
                TentacleType copy = t.copy();
                copy.rotationOffset *= -1f;
                copy.x *= -1f;
                copy.flipSprite = !copy.flipSprite;
                mapped.add(copy);
            }
        }
        seq.set(mapped);
    }

    public void load(){
        region = Core.atlas.find(name);
        tipRegion = Core.atlas.find(name + "-tip", Core.atlas.find(name));
    }

    public TentacleType copy(){
        try{
            return (TentacleType)clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }
}
