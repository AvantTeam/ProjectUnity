package unity.annotations.util;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class ExpType<T extends UnlockableContent>{
    private static final Cons2<Rect, Float> drawSpark = (rect, rot) -> {
        float x = rect.x, y = rect.y, w = rect.width, h = rect.height;

        Drawf.tri(x, y, w, h, rot);
        Drawf.tri(x, y, w, h, rot + 90f);
        Drawf.tri(x, y, w, h, rot + 180f);
        Drawf.tri(x, y, w, h, rot + 270f);
    };

    private static int tmp;

    public static final Effect

    upgradeBlockEffect = new Effect(90f, e -> {
        Draw.color(Color.white, Color.green, e.fin());
        Lines.stroke(e.fout() * 6f * e.rotation);
        Lines.square(e.x, e.y, e.fin() * 4f * e.rotation + 2f * e.rotation, 0f);

        tmp = 1;
        Angles.randLenVectors(e.id, e.id % 3 + 7, e.rotation * 4f + 4f + 8f * e.finpow(), (x, y) -> {
            Tmp.r1.set(e.x + x, e.y + y, e.fout() * 5f, e.fout() * 3.5f);
            drawSpark.get(Tmp.r1, (float)(e.id * tmp));

            tmp++;
        });
    }),

    sparkleEffect = new Effect(15f, e -> {
        Draw.color(Color.white, e.color, e.fin());

        tmp = 1;
        Angles.randLenVectors(e.id, e.id % 3 + 1, e.rotation * 4 + 4, (x, y) -> {
            Tmp.r1.set(e.x + x, e.y + y, e.fout() * 4f, 0.5f + e.fout() * 2.2f);
            drawSpark.get(Tmp.r1, (float)(e.id * tmp));

            tmp++;
        });
    });

    public final T base;

    public int maxLevel = 20;
    public int fakeMaxLevel = 59999;
    public float maxExp = getRequiredExp(maxLevel);

    public Color level0Color = Pal.accent;
    public Color levelMaxColor = Color.valueOf("fff4cc");
    public Color exp0Color = Color.valueOf("84ff00");
    public Color expMaxColor = Color.valueOf("90ff00");

    //expFields: [],
    public boolean hasLevelEffect = true;
    public Effect levelUpEffect = Fx.upgradeCore;
    public Sound levelUpSound = Sounds.message;

    public Seq<ExpUpgrade> upgrades = new Seq<>();
    public Color upgradeColor = Color.green;
    public Effect upgradeEffect = upgradeBlockEffect;
    public Effect upgradeSparkleFx = sparkleEffect;
    public Sound upgradeSound = Sounds.place;
    public float sparkleChance = 0.08f;

    public boolean useStringSync = false;

    public float rwPrecision = 1f;
    public boolean consumesExp = true;
    public float orbMultiplier = 0.8f;
    public float orbRefund = 0.3f;

    public float innateExp = 0f;

    public ExpType(T base){
        this.base = base;
    }

    public void init(){
        setStats();
        setBars();
    }

    public void setStats(){
        base.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.lvlAmount", fakeMaxLevel));
        base.stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", getRequiredExp(fakeMaxLevel)));

        StatValue statTable = table -> table.table(tab -> {
            tab.row();
            tab.add("$explib.upgrades");
            tab.row();

            for(ExpUpgrade upgrade : upgrades){
                if(upgrade.min > fakeMaxLevel) continue;

                float size = 8f * 3f;

                tab.add("[green]" + Core.bundle.get("explib.level") + " " + upgrade.min + "[] ");

                tab.image(upgrade.content.icon(Cicon.small)).size(size).padRight(4f).scaling(Scaling.fit);
                tab.add(upgrade.content.localizedName).left();
                tab.row();
            }
        });

        base.stats.add(Stat.abilities, statTable);
    }

    public void setBars(){
        if(base instanceof Block block){
            block.bars.add("level", build -> {
                if(build instanceof ExpEntity exp){
                    return new Bar(
                        () -> Core.bundle.get("explib.level") + " " + getLevel(exp.totalExp()),
                        () -> Tmp.c1.set(level0Color).lerp(levelMaxColor, Mathf.clamp(getLevel(exp.totalExp()) / fakeMaxLevel)),
                        () -> Mathf.clamp(getLevel(exp.totalExp()) / fakeMaxLevel)
                    );
                }else{
                    throw new IllegalArgumentException("Building type does not implement ExpEntity!");
                }
            });
    
            block.bars.add("exp", build -> {
                if(build instanceof ExpEntity exp){
                    return new Bar(
                        () -> (exp.totalExp() < maxExp) ? Core.bundle.get("explib.exp") : Core.bundle.get("explib.max"),
                        () -> Tmp.c1.set(exp0Color).lerp(expMaxColor, getLevelf(exp.totalExp())),
                        () -> getLevelf(exp.totalExp())
                    );
                }else{
                    throw new IllegalArgumentException("Building type does not implement ExpEntity!");
                }
            });
        }
    }

    public float getLevel(float exp) {
        return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1f)), maxLevel);
    }

    public float getLevelf(float exp) {
        float lvl = getLevel(exp);
        if(lvl >= maxLevel) return 1f;

        float last = getRequiredExp(lvl);
        float next = getRequiredExp(lvl + 1f);

        return (exp - last) / (next - last);
    }

    public float getRequiredExp(float lvl){
        return lvl * lvl * 10f;
    }

    public class ExpUpgrade{
        public T content;
        public float min;
    }
}
