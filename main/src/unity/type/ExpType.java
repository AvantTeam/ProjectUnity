package unity.type;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.Sounds;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;

@ExpBase
public abstract class ExpType<T extends UnlockableContent>{
    public final T type;

    public int maxLevel = 20;
    public float maxExp = requiredExp(maxLevel);

    public Color minLevelColor = Pal.accent;
    public Color maxLevelColor = Color.valueOf("fff4cc");
    public Color minExpColor = Color.valueOf("84ff00");
    public Color maxExpColor = Color.valueOf("90ff00");

    public OrderedSet<ExpUpgrade> upgrades = new OrderedSet<>();
    public boolean enableUpgrade;
    public boolean hasUpgradeEffect = true;
    public Effect upgradeEffect = Fx.none;
    public Sound upgradeSound = Sounds.none;

    protected ExpType(T type){
        this.type = type;
    }

    public void init(){
        setStats();
        enableUpgrade = upgrades.size > 0;
    }

    protected void setStats(){
        type.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.lvlAmount", maxLevel));
        type.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", requiredExp(maxLevel)));

        type.stats.add(Stat.abilities, table -> {
            table.table(t -> {
                t.row();
                t.add("$explib.upgrades");
                t.row();

                for(ExpUpgrade upgrade : upgrades){
                    if(upgrade.min > maxLevel) continue;

                    float size = 8f * 3f;

                    t.add("[green]" + Core.bundle.get("explib.level") + " " + upgrade.min + "[] ");

                    t.image(upgrade.type.icon(Cicon.small)).size(size).padRight(4).scaling(Scaling.fit);
                    t.add(upgrade.type.localizedName).left();
                    t.row();
                }
            });
        });
    }

    public float requiredExp(int level){
        return level * level * 10f;
    }

    public class ExpUpgrade{
        public final T type;
        public int min = 1;

        public ExpUpgrade(T type){
            this.type = type;
        }
    }
}
