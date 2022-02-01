package unity.world.blocks.defense.turrets.exp;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import unity.content.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class ExpTurret extends Turret {
    public int maxLevel = 10; //must be below 200
    public int maxExp;
    public EField<?>[] expFields;

    public @Nullable ExpTurret pregrade = null;
    public int pregradeLevel = -1;

    public float orbScale = 0.8f;
    public int expScale = 1;
    public Effect upgradeEffect = UnityFx.upgradeBlockFx;
    public Sound upgradeSound = Sounds.message;
    public Color fromColor = Pal.lancerLaser, toColor = UnityPal.expColor;

    protected @Nullable EField<Float> rangeField = null;//special field, it is special because it's the only one used for drawing stuff
    protected float rangeStart, rangeEnd;

    public ExpTurret(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        maxExp = requiredExp(maxLevel);
        if(expLevel(maxExp) < maxLevel) maxLevel++; //floating point error

        //check for range field
        for(EField<?> f : expFields){
            if(f.stat == Stat.shootRange){
                rangeField = (EField<Float>) f;
                break;
            }
        }
        if(rangeField == null){
            rangeStart = rangeEnd = range;
        }
        else{
            rangeEnd = rangeField.fromLevel(maxLevel);
            rangeStart = rangeField.fromLevel(0);
        }
        setEFields(0);

        if(pregrade != null && pregradeLevel < 0) pregradeLevel = pregrade.maxLevel;
    }

    //setStats is untouched
    @Override
    public void checkStats(){
        if(!stats.intialized){
            setStats();
            addExpStats();
            stats.intialized = true;
        }
    }

    public void addExpStats(){
        var map = stats.toMap();
        for(EField<?> f : expFields){
            if(f.stat == null) continue;
            if(map.containsKey(f.stat.category) && map.get(f.stat.category).containsKey(f.stat)) stats.remove(f.stat);
            stats.add(f.stat, f.toString());
        }

        if(pregrade != null){
            stats.add(Stat.buildCost, "[#84ff00]" + Iconc.up + Core.bundle.format("exp.upgradefrom", pregradeLevel, pregrade.localizedName) + "[]");
            stats.add(Stat.buildCost, t -> {
                t.button(Icon.info, Styles.cleari, 20f, () -> ui.content.show(pregrade)).size(26);
            });
        }

        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.lvlAmount", maxLevel));
        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.expAmount", maxExp));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);

        if(rangeStart != rangeEnd) Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, rangeEnd, UnityPal.expColor);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, rangeStart, Pal.placing);

        if(!valid && pregrade != null) drawPlaceText(Core.bundle.format("exp.pregrade", pregradeLevel, pregrade.localizedName), x, y, false);
    }

    @Override
    public boolean canReplace(Block other){
        return super.canReplace(other) || (pregrade != null && other == pregrade);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(tile == null) return false;
        if(pregrade == null) return super.canPlaceOn(tile, team, rotation);

        CoreBlock.CoreBuild core = team.core();
        //must have all requirements
        if(core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier))) return false;
        return (tile.block() == pregrade && ((ExpTurretBuild) tile.build).level() >= pregradeLevel);
    }

    @Override
    public void placeBegan(Tile tile, Block previous){
        //finish placement immediately when a block is replaced.
        if(pregrade != null && previous == pregrade){
            tile.setBlock(this, tile.team());
            UnityFx.placeShine.at(tile.drawx(), tile.drawy(), tile.block().size * tilesize, UnityPal.expColor);
            Fx.upgradeCore.at(tile, tile.block().size);
        }
        else super.placeBegan(tile, previous);
    }

    public int expLevel(int e){
        return Math.min(maxLevel, (int)(Mathf.sqrt(e / (25f * expScale))));
    }

    public float expCap(int l){
        if(l < 0) return 0f;
        if(l > maxLevel) l = maxLevel;
        return requiredExp(l + 1);
    }

    public int requiredExp(int l){
        return l * l * 25 * expScale;
    }

    public void setEFields(int l){
        for(EField<?> f : expFields){
            f.setLevel(l);
        }
    }

    public class ExpTurretBuild extends TurretBuild implements ExpHolder {
        public int exp;

        @Override
        public int getExp(){
            return exp;
        }

        @Override
        public int handleExp(int amount){
            int e = Math.min(amount, maxExp - exp);
            int before = level();
            exp += e;
            int after = level();

            if(exp > maxExp) exp = maxExp;
            if(exp < 0) exp = 0;

            if(after > before) levelup();
            return e;
        }

        @Override
        public boolean acceptOrb(){
            return exp < maxExp;
        }

        @Override
        public boolean handleOrb(int orbExp){
            int a = (int)(orbScale * orbExp);
            if(a < 1) return false;
            handleExp(a);
            return true;
        }

        public int level(){
            return expLevel(exp);
        }

        public int maxLevel(){
            return maxLevel;
        }

        public float expf(){
            int lv = level();
            if(lv >= maxLevel) return 1f;
            float lb = expCap(lv - 1);
            float lc = expCap(lv);
            return ((float) exp - lb) / (lc - lb);
        }

        public float levelf(){
            return level() / (float)maxLevel;
        }

        public void levelup(){
            upgradeSound.at(this);
            upgradeEffect.at(this, size);
        }

        public Color shootColor(Color tmp){
            return tmp.set(fromColor).lerp(toColor, exp / (float)maxExp);
        }

        //updateTile is untouched
        @Override
        public void update(){
            setEFields(level());
            //todo remove
            if(exp < maxExp) exp++;
            super.update();
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, rangeField == null ? range : rangeField.fromLevel(level()), team.color);
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            table.table(t -> {
                t.defaults().height(18f).pad(4);
                t.label(() -> "Lv " + level()).color(Pal.accent).width(65f);
                t.add(new Bar(() -> level() >= maxLevel ? "MAX" : Core.bundle.format("bar.expp", (int)(expf() * 100f)), () -> UnityPal.expColor, this::expf)).growX();
            }).pad(0).growX().padTop(4).padBottom(4);
            table.row();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(exp);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            exp = read.i();
        }
    }

    public abstract class EField<T> {
        /*public Prov<T> get;
        public Cons<T> set;

        public EField(Prov<T> get, Cons<T> set){
            this.get = get;
            this.set = set;
        }*/
        public @Nullable Stat stat;
        public EField(Stat stat){
            this.stat = stat;
        }

        public abstract T fromLevel(int l);
        public abstract void setLevel(int l);

        @Override
        public String toString(){
            return "[#84ff00]NULL[]";
        }
    }

    public class ELinear extends EField<Float> {
        public Floatc set;
        public float start, scale;
        public Func<Float, String> format;

        public ELinear(Floatc set, float start, float scale, Stat stat, Func<Float, String> format){
            super(stat);
            this.start = start;
            this.scale = scale;
            this.set = set;
            this.format = format;
        }

        public ELinear(Floatc set, float start, float scale, Stat stat){
            this(set, start, scale, stat, f -> Strings.autoFixed(f, 1));
        }

        @Override
        public Float fromLevel(int l){
            return start + l * scale;
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            //return Strings.autoFixed(start, 1) + " + " + "[#84ff00]" + Strings.autoFixed(scale, 1) + " per level[]";
            return Core.bundle.format("field.linear", format.get(start), format.get(scale));
        }
    }

    //reloadtime calculation sucks
    public class LinearReloadTime extends EField<Float> {
        public Floatc set;
        public float start, scale;

        public LinearReloadTime(Floatc set, float start, float scale){
            super(Stat.reload);
            this.start = start;
            this.scale = scale;
            this.set = set;
        }

        @Override
        public Float fromLevel(int l){
            return start + l * scale;
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            return Core.bundle.format("field.linearreload", Strings.autoFixed(start, 2), Strings.autoFixed(start + scale * maxLevel, 2));
        }
    }

    public class EBool extends EField<Boolean> {
        public Boolc set;
        public boolean start;
        public int thresh;

        public EBool(Boolc set, boolean start, int thresh, Stat stat){
            super(stat);
            this.start = start;
            this.thresh = thresh;
            this.set = set;
        }

        @Override
        public Boolean fromLevel(int l){
            return (l >= thresh) != start;
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            return Core.bundle.format("field.bool", bs(start), bs(!start), thresh);
        }

        public String bs(boolean b){
            return Core.bundle.get(b ? "yes" : "no");
        }
    }

    public class EList<T> extends EField<T> {
        public Cons<T> set;
        public T[] list;
        public String unit;

        public EList(Cons<T> set, T[] list, Stat stat, String unit){
            super(stat);
            this.set = set;
            this.list = list;
            this.unit = unit;
        }

        public EList(Cons<T> set, T[] list, Stat stat){
            this(set, list, stat, "");
        }

        @Override
        public T fromLevel(int l){
            return list[Math.min(list.length - 1, l)];
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            return Core.bundle.format("field.list", list[0], list[list.length - 1], unit);
        }
    }
}
