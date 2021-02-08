package unity.type;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
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
import unity.entities.comp.*;

import java.lang.reflect.*;

@ExpBase
public abstract class ExpType<T extends UnlockableContent>{
    public final T type;

    public int maxLevel = 20;
    public float maxExp = requiredExp(maxLevel);
    public float orbRefund = 0.3f;

    public Color minLevelColor = Pal.accent;
    public Color maxLevelColor = Color.valueOf("fff4cc");
    public Color minExpColor = Color.valueOf("84ff00");
    public Color maxExpColor = Color.valueOf("90ff00");
    public Color upgradeColor = Color.green;

    public Seq<ExpUpgrade> upgrades = new Seq<>();
    public boolean enableUpgrade;
    public boolean hasUpgradeEffect = true;
    public Effect upgradeEffect = Fx.none;
    public Sound upgradeSound = Sounds.none;

    public ObjectMap<ExpFieldType, Seq<ExpField>> expFields = new ObjectMap<>();
    public Field[] linearInc;
    public float[] linearIncStart;
    public float[] linearIncMul;

    public Field[] expInc;
    public float[] expIncStart;
    public float[] expIncMul;

    public Field[] rootInc;
    public float[] rootIncStart;
    public float[] rootIncMul;

    public Field[] boolInc;
    public boolean[] boolIncStart;
    public float[] boolIncMul;

    protected ExpType(T type){
        this.type = type;
    }

    public void init(){
        setStats();

        enableUpgrade = upgrades.size > 0;
        for(ExpFieldType type : ExpFieldType.all){
            try{
                Seq<ExpField> fields = expFields.get(type, Seq::new);
                int amount = fields.size;

                Field fInc = getClass().getDeclaredField(type.name() + "Inc");
                Field fIncStart = getClass().getDeclaredField(type.name() + "IncStart");
                Field fIncMul = getClass().getDeclaredField(type.name() + "IncMul");

                fInc.set(this, new Field[amount]);
                fIncStart.set(this, type == ExpFieldType.bool ? new boolean[amount] : new float[amount]);
                fIncMul.set(this, new float[amount]);

                for(int i = 0; i < fields.size; i++){
                    ExpField field = fields.get(i);

                    ((Field[])fInc.get(this))[i] = field.field;
                    if(type == ExpFieldType.bool){
                        ((boolean[])fIncStart.get(this))[i] = field.startBool;
                    }else{
                        ((float[])fIncStart.get(this))[i] = field.startFloat;
                    }
                    ((float[])fIncMul.get(this))[i] = field.intensity;
                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
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

    public void addUpgrade(T type, int minLevel){
        addUpgrade(type, minLevel, maxLevel);
    }

    public void addUpgrade(T type, int minLevel, int maxLevel){
        upgrades.add(new ExpUpgrade(type){{
            min = minLevel;
            max = maxLevel;
        }});
    }

    public <E extends T> void addField(ExpFieldType type, Class<E> enclosing, String field, float startFloat){
        addField(type, enclosing, field, startFloat, false, 1);
    }

    public <E extends T> void addField(ExpFieldType type, Class<E> enclosing, String field, float startFloat, float intensity){
        addField(type, enclosing, field, startFloat, false, intensity);
    }

    public <E extends T> void addField(ExpFieldType type, Class<E> enclosing, String field, boolean startBool){
        addField(type, enclosing, field, 0, startBool, 1);
    }

    public <E extends T> void addField(ExpFieldType type, Class<E> enclosing, String field, boolean startBool, float intensity){
        addField(type, enclosing, field, 0, startBool, intensity);
    }

    public <E extends T> void addField(ExpFieldType type, Class<E> enclosing, String field, float sFloat, boolean sBool, float ints){
        try{
            expFields.get(type, Seq::new).add(new ExpField(type, enclosing.getDeclaredField(field)){{
                startFloat = sFloat;
                startBool = sBool;
                intensity = ints;
            }});
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public <E extends ExpType<T>> void setExpStats(ExpEntityc<T, E> e){
        int level = e.level();
        try{
            linearExp(level);
            expExp(level);
            rootExp(level);
            boolExp(level);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void linearExp(int level) throws Exception{
        if(linearInc == null) return;
        for(int i = 0; i < linearInc.length; i++){
            linearInc[i].set(type, Math.max(linearIncStart[i] + linearIncMul[i] * level, 0f));
        }
    }

    public void expExp(int level) throws Exception{
        if(expInc == null) return;
        for(int i = 0; i < expInc.length; i++){
            expInc[i].set(type, Math.max(expIncStart[i] * Mathf.pow(this.expIncMul[i], level), 0f));
        }
    }

    public void rootExp(int level) throws Exception{
        if(rootInc == null) return;
        for(int i = 0; i < rootInc.length; i++) {
            rootInc[i].set(type, Math.max(rootIncStart[i] + Mathf.sqrt(rootIncMul[i] * level), 0f));
        }
    }

    public void boolExp(int level) throws Exception{
        if(boolInc == null) return;
        for(int i = 0; i < boolInc.length; i++) {
            boolInc[i].set(type, (boolIncStart[i]) ? (level < boolIncMul[i]) : (level >= boolIncMul[i]));
        }
    }

    public class ExpUpgrade{
        public final T type;
        public int min = 1;
        public int max = maxLevel;

        public ExpUpgrade(T type){
            this.type = type;
        }
    }

    public class ExpField{
        public final ExpFieldType type;
        public final Field field;

        public float startFloat = 0f;
        public boolean startBool = false;
        public float intensity = 1f;

        public ExpField(ExpFieldType type, Field field){
            this.type = type;
            this.field = field;
        }
    }

    public enum ExpFieldType{
        linear,
        exp,
        root,
        bool;

        public static final ExpFieldType[] all = values();
    }
}
