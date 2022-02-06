package unity.world.blocks.exp;

import arc.*;
import arc.audio.*;
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
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import unity.content.*;
import unity.entities.*;
import unity.graphics.*;

import static mindustry.Vars.*;

//TODO is there REALLY no other way than to copy & paste ExpTurret's code here? What if we use annos to generate ExpTurret using this code?
/** Serves as a base fore EXP producers that also level up and receives stat bonuses.
 * @implNote Replaces {@link Block}
 * @apiNote This block serves as the direct parent of a purposed block class (i.e. the original block class should extend off of Block); for blocks that have {@link Block} as its grandparent or more, create a new class like {@link ExpTurret}!
 * @author sunny
 */
public class ExpBase extends Block {
    public int maxLevel = 10; //must be below 200
    public int maxExp;
    public EField<?>[] expFields;

    public @Nullable ExpBase pregrade = null;
    public int pregradeLevel = -1;

    public float orbScale = 0.8f;
    public int expScale = 1;
    public Effect upgradeEffect = UnityFx.upgradeBlockFx;
    public Sound upgradeSound = Sounds.message;

    public ExpBase(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        if(expFields == null) expFields = new EField[]{};
        maxExp = requiredExp(maxLevel);
        if(expLevel(maxExp) < maxLevel) maxExp++; //floating point error

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
                t.button(Icon.infoSmall, Styles.cleari, 20f, () -> ui.content.show(pregrade)).size(26);
            });
        }

        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.lvlAmount", maxLevel));
        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.expAmount", maxExp));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

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
        return (tile.block() == pregrade && ((ExpTurret.ExpTurretBuild) tile.build).level() >= pregradeLevel);
    }

    @Override
    public void placeBegan(Tile tile, Block previous){
        //finish placement immediately when a block is replaced.
        if(pregrade != null && previous == pregrade){
            tile.setBlock(this, tile.team());
            UnityFx.placeShine.at(tile.drawx(), tile.drawy(), tile.block().size * tilesize, UnityPal.exp);
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

    public class ExpBaseBuild extends Building implements ExpHolder {
        public int exp;
        public @Nullable
        ExpHub.ExpHubBuild hub = null;

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

        //updateTile is untouched
        @Override
        public void update(){
            setEFields(level());
            super.update();
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            table.table(t -> {
                t.defaults().height(18f).pad(4);
                t.label(() -> "Lv " + level()).color(Pal.accent).width(65f);
                t.add(new Bar(() -> level() >= maxLevel ? "MAX" : Core.bundle.format("bar.expp", (int)(expf() * 100f)), () -> UnityPal.exp, this::expf)).growX();
            }).pad(0).growX().padTop(4).padBottom(4);
            table.row();
        }

        @Override
        public void killed(){
            ExpOrbs.spreadExp(x, y, exp * 0.3f, 3f * size);
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

        //hub methods
        public boolean hubValid(){
            return hub != null && !hub.dead && hub.links.contains(pos());
        }
    }
}
