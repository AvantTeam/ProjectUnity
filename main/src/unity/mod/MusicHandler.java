package unity.mod;

import arc.*;
import arc.audio.*;
import arc.struct.Seq;
import mindustry.game.EventType.*;
import mindustry.type.*;
import unity.annotations.util.*;
import unity.gen.*;

public class MusicHandler{
    public MusicHandler(){
        Events.on(SectorLaunchEvent.class, e -> {
            Planet p = e.sector.planet;

            for(Faction fac : Faction.all){
                if(fac.equals(FactionMeta.map(p))){
                    FactionMeta.getByFaction(fac, Music.class).each(music -> {
                        Seq<Music> category = FactionMeta.getMusicCategory(music);
                        if(!category.contains(music)){
                            category.add(music);
                        }
                    });
                }else{
                    FactionMeta.getByFaction(fac, Music.class).each(music -> {
                        Seq<Music> category = FactionMeta.getMusicCategory(music);
                        if(category.contains(music)){
                            category.remove(music);
                        }
                    });
                }
            }
        });
    }
}
