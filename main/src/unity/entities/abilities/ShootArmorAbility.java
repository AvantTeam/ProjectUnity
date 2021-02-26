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

public class ShootArmorAbility extends Ability{
    /** How much the shooter's amror increases */
    public float armorInc = 25f;
    /** How quickly the shield lerps */
    public float speed = 0.06f;
    /** Sprite that appears when active */
    public String armorRegion = "error";

    protected float shootHeat;
    protected Vec2 offset = new Vec2();

    public ShootArmorAbility(){};

    public ShootArmorAbility(float armorInc, float speed, String armorRegion){
        this.armorInc = armorInc;
        this.speed = speed;
        this.armorRegion = armorRegion;
    }

    @Override
    public void update(Unit unit)   {
        shootHeat = Mathf.lerpDelta(shootHeat, unit.isShooting() ? 1f : 0f, speed);
        unit.armor = unit.type.armor + armorInc * shootHeat;
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