package unity.entities.abilities;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.entities.*;
import unity.type.*;
import unity.util.*;

public class DirectionShieldAbility extends Ability{
    public int shields;
    public float[] shieldAngles;
    public float[] healths;
    public boolean[] available;
    public float maxHealth;
    public float disableRegen;
    public float shieldRegen;
    public float distanceRadius;
    public float shieldSize;
    public float shieldSpeed;
    public Interval timer = new Interval();

    public float explosiveReflectDamageMultiplier = 0.7f;
    public float explosiveDamageThreshold = 90f;

    public DirectionShieldAbility(int shields, float speed, float size, float health, float regen, float disableRegen, float distance){
        shieldSpeed = speed;
        shieldSize = size;
        maxHealth = health;
        shieldRegen = regen;
        distanceRadius = distance;
        shieldAngles = new float[shields];
        healths = new float[shields];
        available = new boolean[shields];
        this.disableRegen = disableRegen;
        this.shields = shields;

        for(int i = 0; i < shields; i++){
            shieldAngles[i] = 0f;
            healths[i] = health;
            available[i] = true;
        }
    }

    @Override
    public Ability copy(){
        DirectionShieldAbility instance = new DirectionShieldAbility(shields, shieldSpeed, shieldSize, maxHealth, shieldRegen, disableRegen, distanceRadius);
        instance.explosiveReflectDamageMultiplier = explosiveReflectDamageMultiplier;
        instance.explosiveDamageThreshold = explosiveDamageThreshold;
        return instance;
    }

    protected void updateShields(Unit unit){
        Tmp.r1.setCentered(unit.x, unit.y, shieldSize);
        Seq<ShieldNode> nodes = new Seq<>();
        for(int i = 0; i < shields; i++){
            Tmp.v1.trns(unit.rotation + shieldAngles[i], distanceRadius);
            Tmp.v1.add(unit);
            Tmp.v2.trns(unit.rotation + shieldAngles[i] + 90, shieldSize / 2f);

            ShieldNode ts = new ShieldNode();
            ts.id = i;
            for(int s : Mathf.signs){
                ts.getNodes(s).set(Tmp.v1.x + (Tmp.v2.x * s), Tmp.v1.y + (Tmp.v2.y * s));
                Tmp.r2.setCentered(Tmp.v1.x + (Tmp.v2.x * s), Tmp.v1.y + (Tmp.v2.y * s), shieldSize / 2f);
                Tmp.r1.merge(Tmp.r2);
            }
            nodes.add(ts);
        }
        if(timer.get(1.5f)){
            Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, b -> {
                if(b.team != unit.team && !(b.type instanceof ContinuousLaserBulletType || b.type instanceof LaserBulletType) && !b.type.scaleVelocity){
                    b.hitbox(Tmp.r2);
                    Tmp.r2.grow(5f);
                    nodes.each(n -> {
                        if(!available[n.id]) return;
                        Vec2 vec = Geometry.raycastRect(n.nodeA.x, n.nodeA.y, n.nodeB.x, n.nodeB.y, Tmp.r2);
                        if(vec != null){
                            float d = Funcs.getBulletDamage(b.type) * (b.damage() / (b.type.damage * b.damageMultiplier()));
                            healths[n.id] -= d;
                            b.damage(b.damage() / 1.5f);
                            float angC = unit.angleTo(b) + Mathf.range(15f);
                            if(explosiveReflectDamageMultiplier > 0f && d >= explosiveDamageThreshold){
                                for(int i = 0; i < 3; i++){
                                    float off = (i * 20f - (3 - 1) * 20f / 2f);
                                    Tmp.v4.set(n.nodeA);
                                    Tmp.v4.add(n.nodeB);
                                    Tmp.v4.scl(0.5f);
                                    UnityBullets.scarShrapnel.create(unit, unit.team, Tmp.v4.x, Tmp.v4.y, angC + off, d * explosiveReflectDamageMultiplier, 1f, 1f, null);
                                }
                            }
                            b.team(unit.team());
                            b.rotation(angC);
                            if(healths[n.id] < 0) available[n.id] = false;
                        }
                    });
                }
            });
        }
        for(int i = 0; i < shields; i++){
            if(available[i]){
                healths[i] = Math.min(healths[i] + (shieldRegen * Time.delta), maxHealth);
            }else{
                healths[i] = Math.min(healths[i] + (disableRegen * Time.delta), maxHealth);
                if(healths[i] >= maxHealth) available[i] = true;
            }
        }
    }

    @Override
    public void update(Unit unit){
        if(unit.isShooting()){
            float size = ((shieldSize * 6f) / Mathf.sqrt(distanceRadius / 3f));
            for(int i = 0; i < shields; i++){
                float ang = Mathf.mod((i * size - (shields - 1) * size / 2f), 360f);
                //float ang = (-shields + (i * shields)) * (size / shields);
                shieldAngles[i] = Mathf.slerpDelta(shieldAngles[i], ang, shieldSpeed);
            }
        }else{
            float offset = (360f / shields) / 2f;
            for(int i = 0; i < shields; i++){
                float ang = Mathf.mod((i * 360f / shields) + offset, 360f);
                shieldAngles[i] = Mathf.slerpDelta(shieldAngles[i], ang, shieldSpeed);
            }
        }
        updateShields(unit);
    }

    @Override
    public void draw(Unit unit){
        float z = Draw.z();
        if(!(unit.type instanceof UnityUnitType type)) return;
        TextureRegion region = type.abilityRegions[AbilityTextures.shield.ordinal()];
        float size = (Math.max(region.width, region.height) * Draw.scl) * 1.3f;
        for(int i = 0; i < shields; i++){
            Tmp.v3.trns(unit.rotation + shieldAngles[i], distanceRadius);
            Tmp.v3.add(unit);
            Draw.z(z - 0.01f);
            float offset = available[i] ? 2f : 1.5f;
            Draw.color(Color.white, Color.black, (1f - (Mathf.clamp(healths[i] / maxHealth))) / offset);
            Draw.rect(region, Tmp.v3.x, Tmp.v3.y, shieldAngles[i] + unit.rotation);
            Draw.z(Math.min(Layer.darkness, z - 1f));
            Draw.color(Pal.shadow);
            Draw.rect(type.softShadowRegion, Tmp.v3.x, Tmp.v3.y, size, size);
        }
        Draw.z(z);
        Draw.color();
    }

    public static class ShieldNode{
        public Vec2 nodeA = new Vec2();
        public Vec2 nodeB = new Vec2();
        public int id;

        public ShieldNode(){
        }

        public Vec2 getNodes(int sign){
            return sign <= -1 ? nodeA : nodeB;
        }
    }
}
