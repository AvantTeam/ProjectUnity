package unity.content;

import arc.graphics.g2d.*;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.graphics.*;
import unity.type.sector.*;
import unity.type.sector.SectorObjective.*;

import static mindustry.Vars.*;
import static unity.content.UnityPlanets.*;

public class UnitySectorPresets implements ContentList{
    public static SectorPreset imberlands;
    public static @FactionDef("monolith") SectorPreset accretion;

    @Override
    public void load(){
        /*imberlands = new SectorPreset("imberlands", electrode, 30){{
            alwaysUnlocked = true;
            captureWave = 15;
        }};*/
        /*accretion = new SectorPreset("accretion", megalith, 30){{
            alwaysUnlocked = true;
            captureWave = 15;
        }};*/

        //testing scripted sectors
        accretion = new ScriptedSector("accretion", megalith, 30){{
            alwaysUnlocked = true;
            addStartingItems = true;
            difficulty = 3f;
            captureWave = 15;

            float rad = 5f * tilesize;

            SectorObjective spawn;
            objectives.addAll(
                spawn =
                new UnitSpawnObjective(Team.crux, UnityUnitTypes.kami, 1, this, 1, (UnitSpawnObjective objective) -> {
                    ui.announce("[scarlet]Kami has been spawned![]");
                }),

                new UnitDeathObjective(Team.crux, UnityUnitTypes.kami, 1, this, 1, (UnitDeathObjective objective) -> {
                    ui.announce("[accent]Kami has been defeated![]");
                }).update((UnitDeathObjective objective) -> {
                    Groups.player.each(p -> {
                        Groups.bullet.intersect(p.x - rad, p.y - rad, rad * 2f, rad * 2f, b -> {
                            if(b.team != p.team() && b.within(player, rad + b.hitSize / 2f)){
                                b.absorb();
                                Fx.absorb.at(b);
                            }
                        });
                    });
                }).draw((UnitDeathObjective objective) -> {
                    float z = Draw.z();
                    Draw.z(UnityShaders.holoShield.getLayer());

                    Groups.player.each(p -> {
                        Draw.color(p.team().color, 0.3f);
                        Fill.circle(p.x, p.y, rad);
                    });
                    Draw.z(z);
                }).dependencies(spawn)
            );
        }};
    }
}
