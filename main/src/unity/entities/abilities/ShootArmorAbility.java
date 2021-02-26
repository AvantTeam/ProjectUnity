package unity.entities.abilities;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.abilities.*;
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
                Drawf.construct(unit.x, unit.y, region, unit.team.color, unit.rotation - 90f, shootHeat, 1, Time.time * 2 + unit.id());
            });
        }
    };
}