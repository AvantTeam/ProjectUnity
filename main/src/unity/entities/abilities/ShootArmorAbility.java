package unity.entities.abilities;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.abilities.*;
import mindustry.entities.comp.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.type.UnityUnitType;

public class ShootArmorAbility extends Ability{
    /** How much the shooter's amror increases */
    public float armorInc = 25f;
    /** How quickly the shield lerps */
    public float warmup = 0.06f;
    /** How much slower should the unit move (0 to 1) */
    public float speedReduction = 0.5f;
    /** How much mirrored weapons shift away while active */
    public float spread = 2f;
    /** Sprite that appears when active */
    public String armorRegion = "error";

    protected float shootHeat;
    protected Vec2 offset = new Vec2();

    public ShootArmorAbility(){};

    public ShootArmorAbility(float armorInc, float warmup, float spread, float speedReduction, String armorRegion){
        this.armorInc = armorInc;
        this.warmup = warmup;
        this.spread = spread;
        this.speedReduction = speedReduction;
        this.armorRegion = armorRegion;
    }

    @Override
    public void update(Unit unit){
        shootHeat = Mathf.lerpDelta(shootHeat, unit.isShooting() ? 1f : 0f, warmup);
        unit.armor = unit.type.armor + armorInc * shootHeat;

        float scl = 1f - shootHeat * speedReduction * Time.delta;
        unit.vel.scl(scl);

        for(int i = 0; i < unit.mounts.length; i++){
            Weapon w = unit.mounts[i].weapon;
            if(w.mirror){
                if(unit.type instanceof UnityUnitType type){
                    float x = type.weaponXs.items[i];
                    if(x > 0){
                        w.x = x + spread * shootHeat;
                    }else if(x < 0){
                        w.x = x - spread * shootHeat;
                    }
                }else{
                    throw new IllegalStateException("Unit type for '" + unit.type.localizedName + "' is not an instance of 'UnityUnitType'!");
                }
            }
        }
    }

    @Override
    public void draw(Unit unit){
        TextureRegion region = Core.atlas.find(armorRegion);
        if(shootHeat >= 0.01f && Core.atlas.isFound(region)){
            Draw.draw(Draw.z(), () -> {
                Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
                if(mech != null){
                    offset.trns(mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f / Mathf.PI, 1) * unit.type.mechSideSway, 0f, unit.elevation));
                    offset.add(Tmp.v1.trns(mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f / Mathf.PI, 1) * unit.type.mechFrontSway, 0f, unit.elevation)));
                }else{
                    offset.set(0f, 0f);
                }

                Drawf.construct(unit.x + offset.x, unit.y + offset.y, region, unit.team.color, unit.rotation - 90f, shootHeat, shootHeat, Time.time * 2 + unit.id());
            });
        }
    };
}