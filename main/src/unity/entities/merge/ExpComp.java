package unity.entities.merge;

import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.entities.*;
import unity.gen.*;
import unity.gen.Expc.*;
import unity.type.exp.*;
import unity.util.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

@MergeComp
@SuppressWarnings({"rawtypes", "unchecked"})
class ExpComp extends Block{
    int maxLevel = 20;
    float maxExp;
    float orbRefund = 0.3f;

    Color minLevelColor = Pal.accent;
    Color maxLevelColor = Color.valueOf("fff4cc");
    Color minExpColor = Color.valueOf("84ff00");
    Color maxExpColor = Color.valueOf("90ff00");
    Color upgradeColor = Color.green;

    public Seq<ExpUpgrade> upgrades = new Seq<>();
    /** Maps {@link #upgrades} into 2D array. Do NOT modify */
    public ExpUpgrade[][] upgradesPerLevel;
    public boolean enableUpgrade;
    public boolean hasUpgradeEffect = true;
    public float sparkleChance = 0.08f;
    public Effect sparkleEffect = UnityFx.sparkleFx;
    public Effect upgradeEffect = UnityFx.upgradeBlockFx;
    public Sound upgradeSound = Sounds.message;

    /** The floating point in which exp will be multiplied on save and divided on load.
     * Use only if you increment exp in floats */
    public float ioPrecision = 1f;

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

    public Entry<Class, Field>[] listInc;
    public Entry<Class, Object[]>[] listIncMul;

    public boolean hasExp = false;

    public boolean hub = false;
    public boolean conveyor = false;
    public boolean noOrbCollision = true;

    public float orbMultiplier = 0.8f;
    public boolean condConfig;

    public ExpComp(String name){
        super(name);
        update = true;
        sync = true;
    }

    @Override
    public void init(){
        maxExp = requiredExp(maxLevel);
        setUpgrades();

        enableUpgrade = upgrades.size > 0;
        for(int i = 0; i < upgrades.size; i++){
            upgrades.get(i).index = i;
        }

        Seq<ExpUpgrade[]> upgradesPerLevel = new Seq<>(ExpUpgrade[].class);
        for(int i = 0; i <= maxLevel; i++){
            Seq<ExpUpgrade> level = new Seq<>(ExpUpgrade.class);
            for(ExpUpgrade upgrade : upgrades){
                if(upgrade.min < 0){
                    upgrade.min = maxLevel;
                }
                if(
                    upgrade.min <= i &&
                    (upgrade.max < 0 || upgrade.max >= i)
                ){
                    level.add(upgrade);
                }
            }
            upgradesPerLevel.add(level.toArray());
        }
        this.upgradesPerLevel = upgradesPerLevel.toArray();

        for(ExpFieldType type : ExpFieldType.all){
            try{
                Seq<ExpField> fields = expFields.get(type, Seq::new);
                int amount = fields.size;

                Class<?> classType = getClass();
                boolean found = false;
                while(
                    !found &&
                    Block.class.isAssignableFrom(classType)
                ){
                    try{
                        classType.getDeclaredField(type.name() + "Inc");
                    }catch(NoSuchFieldException e){
                        classType = classType.getSuperclass();
                        continue;
                    }

                    found = true;
                }

                //should not happen
                if(!found) throw new IllegalStateException("No parent block classes with exp fields detected.");

                if(type != ExpFieldType.list){
                    Field fInc = classType.getDeclaredField(type.name() + "Inc");
                    Field fIncMul = classType.getDeclaredField(type.name() + "IncMul");
                    Field fIncStart = classType.getDeclaredField(type.name() + "IncStart");

                    fInc.set(this, (Entry<Class, Field>[])new Entry[amount]);
                    fIncStart.set(this, type == ExpFieldType.bool ? new boolean[amount] : new float[amount]);
                    fIncMul.set(this, new float[amount]);

                    for(int i = 0; i < amount; i++){
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
                }else{
                    listInc = new Entry[amount];
                    listIncMul = new Entry[amount];

                    for(int i = 0; i < amount; i++){
                        ExpField field = fields.get(i);

                        listInc[i] = new Entry<>(){{
                            key = field.classType;
                            value = field.field;
                        }};

                        listIncMul[i] = new Entry<>(){{
                            key = field.intensityType;
                            value = field.intensityList;
                        }};
                    }
                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        if(enableUpgrade){
            configurable = condConfig = true;
            saveConfig = false;

            config(Integer.class, (build, value) -> {
                int i = value.intValue();
                if(i >= 0 && build instanceof ExpBuildc exp){
                    exp.upgrade(i);
                }
            });
        }
    }

    public void setUpgrades(){}

    @Override
    public void setStats(){
        stats.add(Stat.itemCapacity, "@", format("explib.lvlAmount", maxLevel));
        stats.add(Stat.itemCapacity, "@", format("explib.expAmount", requiredExp(maxLevel)));

        if(upgrades.size > 0){
            stats.add(Stat.abilities, table -> {
                table.table(t -> {
                    t.row();
                    t.add("$explib.upgrades");
                    t.row();

                    for(ExpUpgrade upgrade : upgrades){
                        if(upgrade.min > maxLevel || upgrade.hide) continue;

                        float size = 8f * 3f;

                        t.add("[green]" + Core.bundle.get("explib.level") + " " + upgrade.min + "[] ");

                        t.image(upgrade.type.icon(Cicon.small)).size(size).padRight(4).scaling(Scaling.fit);
                        t.add(upgrade.type.localizedName).left();
                        t.row();
                    }
                });
            });
        }
    }

    @Override
    public void setBars(){
        bars.add("level", b -> {
            if(b instanceof ExpBuildc build){
                return new Bar(
                    () -> Core.bundle.get("explib.level") + " " + build.level(),
                    () -> Tmp.c1.set(minLevelColor).lerp(maxLevelColor, build.levelf()),
                    () -> build.levelf()
                );
            }else{
                throw new IllegalStateException("Building type for '" + localizedName + "' is not an instance of 'ExpBuildc'!");
            }
        });

        bars.add("exp", b -> {
            if(b instanceof ExpBuildc build){
                return new Bar(
                    () -> build.exp() < maxExp
                    ?    Core.bundle.get("explib.exp")
                    :   Core.bundle.get("explib.max"),
                    () -> Tmp.c1.set(minExpColor).lerp(maxExpColor, build.expf()),
                    () -> build.expf()
                );
            }else{
                throw new IllegalStateException("Building type for '" + localizedName + "' is not an instance of 'ExpBuildc'!");
            }
        });
    }

    public String format(String format, Object... args){
        if(headless){
            return format;
        }else{
            return Core.bundle.format(format, args);
        }
    }

    public float requiredExp(int level){
        return level * level * 10f;
    }

    public <T extends Block> void addUpgrade(T type, int minLevel, boolean hidden){
        addUpgrade(type, minLevel, maxLevel, hidden);
    }

    public <T extends Block> void addUpgrade(T type, int minLevel, int maxLevel, boolean hidden){
        upgrades.add(new ExpUpgrade(type){{
            min = minLevel;
            max = maxLevel;
            hide = hidden;
        }});
    }

    public <E extends Block> void addField(ExpFieldType type, Class<E> enclosing, String field, float startFloat){
        addField(type, enclosing, field, startFloat, false, 1f, null, null);
    }

    public <E extends Block> void addField(ExpFieldType type, Class<E> enclosing, String field, float startFloat, float intensity){
        addField(type, enclosing, field, startFloat, false, intensity, null, null);
    }

    public <E extends Block> void addField(ExpFieldType type, Class<E> enclosing, String field, boolean startBool){
        addField(type, enclosing, field, 0f, startBool, 1f, null, null);
    }

    public <E extends Block> void addField(ExpFieldType type, Class<E> enclosing, String field, boolean startBool, float intensity){
        addField(type, enclosing, field, 0f, startBool, intensity, null, null);
    }

    public <E extends Block, I> void addField(ExpFieldType type, Class<E> enclosing, String field, Class<I> intensityType, I[] intensity){
        addField(type, enclosing, field, 0f, false, 0f, intensityType, intensity);
    }

    public <E extends Block, I> void addField(ExpFieldType type, Class<E> enclosing, String field, float startFloat, boolean startBool, float intensity, Class<I> intensityType, I[] intensityList){
        try{
            //had to do this because java is drunk
            float f = startFloat;
            boolean b = startBool;
            float i = intensity;
            I[] il = intensityList;

            expFields.get(type, Seq::new).add(new ExpField(type, enclosing, intensityType, enclosing.getDeclaredField(field)){{
                startFloat = f;
                startBool = b;
                intensity = i;
                intensityList = il;
            }});
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void setExpStats(ExpBuildc e){
        int level = e.level();
        try{
            linearExp(level);
            expExp(level);
            rootExp(level);
            boolExp(level);
            listExp(level);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void linearExp(int level) throws Exception{
        for(int i = 0; i < linearInc.length; i++){
            linearInc[i].value.set(linearInc[i].key.cast(this), Math.max(linearIncStart[i] + linearIncMul[i] * level, 0f));
        }
    }

    public void expExp(int level) throws Exception{
        for(int i = 0; i < expInc.length; i++){
            expInc[i].value.set(expInc[i].key.cast(this), Math.max(expIncStart[i] * Mathf.pow(expIncMul[i], level), 0f));
        }
    }

    public void rootExp(int level) throws Exception{
        for(int i = 0; i < rootInc.length; i++){
            rootInc[i].value.set(rootInc[i].key.cast(this), Math.max(rootIncStart[i] + Mathf.sqrt(rootIncMul[i] * level), 0f));
        }
    }

    public void boolExp(int level) throws Exception{
        for(int i = 0; i < boolInc.length; i++){
            boolInc[i].value.set(boolInc[i].key.cast(this), (boolIncStart[i]) ? (level < boolIncMul[i]) : (level >= boolIncMul[i]));
        }
    }

    public void listExp(int level) throws Exception{
        for(int i = 0; i < listInc.length; i++){
            listInc[i].value.set(listInc[i].key.cast(this), listIncMul[i].key.cast(listIncMul[i].value[level]));
        }
    }

    public class ExpBuildComp extends Building{
        float exp;
        boolean checked;

        public float expf(){
            int level = level();
            if(level >= maxLevel()) return 1f;

            float last = requiredExp(level);
            float next = requiredExp(level + 1);

            return (exp - last) / (next - last);
        }

        public int level(){
            return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1f)), maxLevel());
        }

        public float levelf(){
            return level() / (float)(maxLevel());
        }

        public void incExp(float exp){
            int before = level();
            this.exp += exp;
            int after = level();

            if(this.exp > maxExp) this.exp = maxExp;
            if(this.exp < 0f) this.exp = 0f;

            if(after > before) upgradeDefault();
        }

        public void upgradeDefault(){
            upgradeSound.at(this);
            upgradeEffect.at(this, size);

            if(enableUpgrade){
                if(
                    !Structs.eq(currentUpgrades(level() - 1), currentUpgrades(level())) &&
                    currentUpgrades(level()).length > 0
                ){
                    checked = false;
                }

                if(!headless && control.input.frag.config.getSelectedTile() == this){
                    control.input.frag.config.hideConfig();
                }
            }
        }

        public void sparkle(){
            sparkleEffect.at(x, y, size, upgradeColor);
        }

        public void upgrade(int index){
            var upgrade = upgrades.get(index);
            if(level() >= upgrade.min && level() <= upgrade.max){
                upgradeBlock(upgrade.type);
            }
        }

        private void upgradeBlock(Block block){
            Tile tile = this.tile;
            int[] links = power() == null ? new int[0] : power().links.toArray();

            if(block.size > size){
                tile = Utils.getBestTile(self(), block.size, size);
            }
            if(tile == null) return;

            tile.setBlock(block, team(), rotation());
            upgradeSound.at(this);
            if(Mathf.chance(sparkleChance)){
                sparkleEffect.at(tile.drawx(), tile.drawy(), block.size, upgradeColor);
            }
            upgradeEffect.at(tile.drawx(), tile.drawy(), block.size, upgradeColor);

            Building build = tile.build;
            Core.app.post(() -> {
                if(build != null && build.isValid() && build.power != null && links.length > 0){
                    for(int link : links){
                        try{
                            Tile powtile = world.tile(link);

                            if(powtile.block() instanceof PowerNode){
                                powtile.build.configure(Integer.valueOf(link));
                            }
                        }catch(Throwable ignored){}
                    }
                }
            });
        }

        @Override
        @Replace
        public boolean shouldShowConfigure(Player player){
            if(enableUpgrade){
                return super.shouldShowConfigure(player) && currentUpgrades(level()).length > 0;
            }
            return false;
        }

        @Override
        public void buildConfiguration(Table table){
            if(!enableUpgrade){
                return;
            }

            checked = true;

            int level = level();
            if(!condConfig){
                upgradeTable(table, level);
            }else{
                if(currentUpgrades(level).length == 0){
                    return;
                }

                table.table(t -> upgradeTable(t, level));
                table.row();
                table.image().pad(2f).width(130f).height(4f).color(upgradeColor);
                table.row();
            }
        }

        private void upgradeTable(Table table, int level){
            var upgrades = currentUpgrades(level);
            if(upgrades.length == 0) return;

            int[] i = {0};
            for(; i[0] < upgrades.length; i[0]++){
                var block = upgrades[i[0]].type;
                table.table(t -> {
                    t.background(Tex.button);
                    t.image(block.icon(Cicon.medium)).size(38).padRight(2);

                    t.table(info -> {
                        info.left();
                        info.add("[green]" + block.localizedName + "[]\n" + Core.bundle.get("explib.level.short") +
                            " [" + ((upgrades[i[0]].min == level)
                                ? "green" : "accent") +
                            "]" +
                        level + "[]/" + upgrades[i[0]].min);
                    }).fillX().growX();

                    infoButton(t, block);
                    if(upgrades[i[0]].min == level){
                        Styles.emptyi.imageUpColor = upgradeColor;
                    }

                    upgradeButton(t, upgrades[i[0]].index, level);
                    if(upgrades[i[0]].min == level){
                        Styles.emptyi.imageUpColor = Color.white;
                    }
                }).height(50).growX();

                if(i[0] < upgrades.length - 1) table.row();
            }
        }

        private void infoButton(Table table, Block block){
            table.button(Icon.infoCircle, Styles.emptyi, () -> {
                ui.content.show(block);
            }).size(40);
        }

        private void upgradeButton(Table table, int index, int level){
            Integer i = Integer.valueOf(index);
            table.button(Icon.up, Styles.emptyi, () -> {
                control.input.frag.config.hideConfig();
                configure(i);
            }).size(40);
        }

        public ExpUpgrade[] currentUpgrades(int level){
            return upgradesPerLevel[level];
        }

        public float spreadAmount(){
            return 3f * size;
        }

        public boolean consumesOrb(){
            return exp < maxExp;
        }

        public int maxLevel(){
            return maxLevel;
        }

        @Override
        @MethodPriority(-1)
        public void update(){
            setExpStats(self());
        }

        @Override
        public void killed(){
            ExpOrbs.spreadExp(x(), y(), exp * orbRefund, spreadAmount());
        }

        @Override
        public void updateTile(){
            if(enableUpgrade && !checked && Mathf.chanceDelta(sparkleChance)){
                sparkle();
            }
        }

        @Override
        @Replace
        public Cursor getCursor(){
            Cursor cursor = block().configurable && team() == player.team() ? SystemCursor.hand : SystemCursor.arrow;
            if(!enableUpgrade){
                return cursor;
            }else{
                return currentUpgrades(level()).length > 0 ? SystemCursor.hand : SystemCursor.arrow;
            }
        }

        @Override
        @Replace
        public boolean configTapped(){
            return
                super.configTapped() &&
                (
                    !enableUpgrade ||
                    condConfig ||
                    !upgrades.isEmpty()
                );
        }

        @Override
        public void write(Writes write){
            write.i((int)(exp * ioPrecision));
        }

        @Override
        public void read(Reads read, byte revision){
            exp = read.i() / ioPrecision;
        }
    }
}
