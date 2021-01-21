package mindustry.entities.abilities;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.audio.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

//Still waiting on Anuke to accept my PR.
public class ShieldMoveLightningAbility extends Ability{
    //Lightning damage
    public float damage = 35f;
    //Chance of firing every tick. Set >= 1 to always fire lightning every tick at max speed.
    public float chance = 0.15f;
    //Length of the lightning
    public int length = 12;
    //Speeds for when to start lightninging and when to stop getting faster
    public float minSpeed = 0.8f, maxSpeed = 1.2f;
    //Lightning color
    public Color color = Color.valueOf("a9d8ff");
    //Shifts where the lightning spawns along the Y axis
    public float offset = 0f;
    //Jittering heat sprite like the shield on v5 Javelin
    public String shieldRegion;
    
    public Effect shootEffect = Fx.sparkShoot;
    public Sound shootSound = Sounds.spark;
    
    ShieldMoveLightningAbility(){}
    
    public ShieldMoveLightningAbility(float damage, int length, float chance, float offset, float minSpeed, float maxSpeed, Color color, String shieldName){
        this.damage = damage;
        this.length = length;
        this.chance = chance;
        this.offset = offset;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.color = color;
        this.shieldRegion = shieldName;
    }
    
    public ShieldMoveLightningAbility(float damage, int length, float chance, float offset, float minSpeed, float maxSpeed, Color color){
        this.damage = damage;
        this.length = length;
        this.chance = chance;
        this.offset = offset;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.color = color;
        this.shieldRegion = "error";
    }
    
    @Override
    public void update(Unit unit){
        float scl = Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        if(Mathf.chance(Time.delta * chance * scl)){
            float x = unit.x + Angles.trnsx(unit.rotation, offset, 0), y = unit.y + Angles.trnsy(unit.rotation, offset, 0);
            shootEffect.at(x, y, unit.rotation, color);
            Lightning.create(unit.team, color, damage * state.rules.unitDamageMultiplier, x + unit.vel.x, y + unit.vel.y, unit.rotation, length);
            shootSound.at(unit);
        }
    }
    
    @Override
    public void draw(Unit unit){
        float scl = Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
        if(shieldRegion != "error" && scl > 0.001f){
            TextureRegion region = atlas.find(shieldRegion);
            Draw.color(color);
            Draw.alpha(scl / 2f);
            Draw.blend(Blending.additive);
            Draw.rect(region, unit.x + Mathf.range(scl / 2f), unit.y + Mathf.range(scl / 2f), unit.rotation - 90);
            Draw.blend();
        }
    }

    @Override
    public String localized(){
        return bundle.get("ability.movelightning");
    }
}