package unity.entities.bullet.laser;

import arc.graphics.*;
import unity.graphics.*;

public class WavefrontLaser extends AcceleratingLaserBulletType{
    public WavefrontLaser(float damage){
        super(damage);
        lifetime = 90f;
        collisionWidth = 22f;
        width = 50f;
        maxLength = 450f;
        accel = 40f;
        laserSpeed = 40f;
        colors = new Color[]{UnityPal.advanceDark.cpy().mul(0.9f, 1f, 1f, 0.4f), UnityPal.advanceDark, UnityPal.advance, Color.white};
    }
}
