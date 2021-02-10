package unity.type;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
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
@SuppressWarnings({"unchecked", "rawtypes"})
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
    public Entry<Class, Field>[] linearInc;
    public float[] linearIncStart;
    public float[] linearIncMul;

    public Entry<Class, Field>[] expInc;
    public float[] expIncStart;
    public float[] expIncMul;

    public Entry<Class, Field>[] rootInc;
    public float[] rootIncStart;
    public float[] rootIncMul;

    public Entry<Class, Field>[] boolInc;
    public boolean[] boolIncStart;
    public float[] boolIncMul;

    protected ExpType(T type){
        this.type = type;
    }

    public void init(){
        setStats();
        enableUpgrade = upgrades.size > 0;
    }

    public void setupFields(){
        for(ExpFieldType type : ExpFieldType.all){
            try{
                Seq<ExpField> fields = expFields.get(type, Seq::new);
                int amount = fields.size;

                Field fInc = ExpType.class.getDeclaredField(type.name() + "Inc");
                Field fIncStart = ExpType.class.getDeclaredField(type.name() + "IncStart");
                Field fIncMul = ExpType.class.getDeclaredField(type.name() + "IncMul");

                fInc.set(this, (Entry<Class, Field>[])new Entry[amount]);
                fIncStart.set(this, type == ExpFieldType.bool ? new boolean[amount] : new float[amount]);
                fIncMul.set(this, new float[amount]);

                for(int i = 0; i < fields.size; i++){
                    ExpField field = fields.get(i);

                    Entry<Class, Field> entry = (((Entry<Class, Field>[])fInc.get(this))[i] = new Entry<>());
                    entry.key = field.classType;
                    entry.value = field.field;

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
            expFields.get(type, Seq::new).add(new ExpField(type, enclosing, enclosing.getDeclaredField(field)){{
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
        for(int i = 0; i < linearInc.length; i++){
            linearInc[i].value.set(linearInc[i].key.cast(type), Math.max(linearIncStart[i] + linearIncMul[i] * level, 0f));
        }
    }

    public void expExp(int level) throws Exception{
        for(int i = 0; i < expInc.length; i++){
            expInc[i].value.set(expInc[i].key.cast(type), Math.max(expIncStart[i] * Mathf.pow(this.expIncMul[i], level), 0f));
        }
    }

    public void rootExp(int level) throws Exception{
        for(int i = 0; i < rootInc.length; i++) {
            rootInc[i].value.set(rootInc[i].key.cast(type), Math.max(rootIncStart[i] + Mathf.sqrt(rootIncMul[i] * level), 0f));
        }
    }

    public void boolExp(int level) throws Exception{
        for(int i = 0; i < boolInc.length; i++) {
            boolInc[i].value.set(boolInc[i].key.cast(type), (boolIncStart[i]) ? (level < boolIncMul[i]) : (level >= boolIncMul[i]));
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
        public final Class classType;
        public final Field field;

        public float startFloat = 0f;
        public boolean startBool = false;
        public float intensity = 1f;

        public ExpField(ExpFieldType type, Class classType, Field field){
            this.type = type;
            this.classType = classType;
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
