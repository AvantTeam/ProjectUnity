package unity.parts.stats;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.*;
import unity.mod.*;
import unity.parts.*;
import unity.parts.PartType.*;
import unity.util.*;

public class WeaponMountStat extends PartStat{
    Weapon baseWeapon;

    public WeaponMountStat(Weapon w){
        super("weapon");
        baseWeapon = w.copy();
    }

    @Override
    public void merge(ValueMap id, Part part){
        var weaponSeq = id.<Seq<ValueMap>>getObject("weapons",Seq::new);
        ValueMap weapon = new ValueMap();
        weapon.put("pos", new Vec2(part.cx(), part.cy()));
        Weapon copy = baseWeapon.copy();
        copy.x = part.cx() * PartType.partSize;
        copy.y = part.cy() * PartType.partSize;
        weapon.put("weapon", copy);
        weaponSeq.add(weapon);
    }

    @Override
    public void mergePost(ValueMap id, Part part){

    }

    @Override
    public void display(Table e){
        e.row();
        e.table(t -> weapons(baseWeapon).display(t));
    }

    public static StatValue weapons(Weapon weapon){
        if(weapon.region == null){
            weapon.load(); // o-o
        }
        return table -> {
            TextureRegion region = !weapon.name.equals("") && weapon.outlineRegion.found() ? weapon.outlineRegion : weapon.region;

            table.image(region).size(60).scaling(Scaling.bounded).right().top();

            table.table(Tex.underline, w -> {
                w.left().defaults().padRight(3).left();

                addStats(weapon, w);
            }).padTop(-9).left();
            table.row();
        };
    }

    public static void addStats(Weapon u, Table t){
        if(u.inaccuracy > 0){
            t.row();
            t.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int)u.inaccuracy + " " + StatUnit.degrees.localized());
        }
        t.row();
        t.add("[lightgray]" + Stat.reload.localized() + ": " + (u.mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / u.reload * u.shoot.shots, 2) + " " + StatUnit.perSecond.localized());

        //It was YoungchaUnitTypes.modularUnitSmall. hacky TODO
        StatValues.ammo(ObjectMap.of(FactionRegistry.contents(Faction.youngcha, UnitType.class).get(0), u.bullet)).display(t);
    }
}
