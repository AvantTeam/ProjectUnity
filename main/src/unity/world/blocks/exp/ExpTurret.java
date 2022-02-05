package unity.world.blocks.exp;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import unity.content.*;
import unity.entities.*;
import unity.graphics.*;

import static mindustry.Vars.*;

/** Identical to {@link ExpBase} but repurposed as a base for turrets.
 * @implNote Replaces {@link Turret}
 * @author sunny
 */
public class ExpTurret extends Turret {
    public int maxLevel = 10; //must be below 200
    public int maxExp;
    public EField<?>[] expFields;

    public @Nullable ExpTurret pregrade = null;
    public int pregradeLevel = -1;

    public float orbScale = 0.8f;
    public int expScale = 1;
    public Effect upgradeEffect = UnityFx.expPoof, upgradeBlockEffect = UnityFx.expShineRegion;
    public Sound upgradeSound = Sounds.message;
    public Color fromColor = Pal.lancerLaser, toColor = UnityPal.exp;
    public Color[] effectColors;

    protected @Nullable EField<Float> rangeField = null;//special field, it is special because it's the only one used for drawing stuff
    protected float rangeStart, rangeEnd;
    private Seq<Building> seqs = new Seq<>();//uwagh

    public ExpTurret(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        if(expFields == null) expFields = new EField[]{};
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
                t.button(Icon.infoSmall, Styles.cleari, 20f, () -> ui.content.show(pregrade)).size(26);
            });
        }

        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.lvlAmount", maxLevel));
        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.expAmount", maxExp));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);

        if(rangeStart != rangeEnd) Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, rangeEnd, UnityPal.exp);
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

        //check is there is ONLY a single pregrade block INSIDE all the tiles it will replace - by tracking SEQS. This protocol is also known as UWAGH standard.
        seqs.clear();
        tile.getLinkedTilesAs(this, inside -> {
            if(inside.build == null || seqs.contains(inside.build) || seqs.size > 1) return; //no point of checking if there are already two in seqs
            if(tile.block() == pregrade && ((ExpTurretBuild) tile.build).level() >= pregradeLevel) seqs.add(tile.build);
        });
        return seqs.size == 1; //no more, no less; a healthy monogamous relationship.
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
        return Math.min(maxLevel, (int)(Mathf.sqrt(e / (5f * expScale))));
    }

    public float expCap(int l){
        if(l < 0) return 0f;
        if(l > maxLevel) l = maxLevel;
        return requiredExp(l + 1);
    }

    public int requiredExp(int l){
        return l * l * 5 * expScale;
    }

    public void setEFields(int l){
        for(EField<?> f : expFields){
            f.setLevel(l);
        }
    }

    public class ExpTurretBuild extends TurretBuild implements ExpHolder {
        public int exp;
        public @Nullable ExpOutput.ExpOutputBuild hub = null;

        @Override
        public int getExp(){
            return exp;
        }

        @Override
        public int handleExp(int amount){
            int e = Math.min(amount, maxExp - exp);
            if(e == 0) return 0;
            int before = level();
            exp += e;
            int after = level();

            if(exp > maxExp) exp = maxExp;
            if(exp < 0) exp = 0;

            if(after > before) levelup();
            return e;
        }

        @Override
        public int unloadExp(int amount){
            int e = Math.min(amount, exp);
            exp -= e;
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
            upgradeEffect.at(this);
            if(upgradeBlockEffect != Fx.none) upgradeBlockEffect.at(x, y, rotation - 90, Color.white, region);
        }

        public Color shootColor(Color tmp){
            return tmp.set(fromColor).lerp(toColor, exp / (float)maxExp);
        }

        /**
         * @return a color picked from effectColors[]. Cannot be modified.
         */
        public Color effectColor(){
            if(effectColors == null) return Color.white;
            return effectColors[Math.min((int)(levelf() * effectColors.length), effectColors.length - 1)];
        }

        //updateTile is untouched
        @Override
        public void update(){
            setEFields(level());
            super.update();
        }

        @Override
        protected void effects(){
            Effect fshootEffect = shootEffect == Fx.none ? peekAmmo().shootEffect : shootEffect;
            Effect fsmokeEffect = smokeEffect == Fx.none ? peekAmmo().smokeEffect : smokeEffect;
            Color effectc = effectColor();

            fshootEffect.at(x + tr.x, y + tr.y, rotation, effectc);
            fsmokeEffect.at(x + tr.x, y + tr.y, rotation, effectc);
            shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));

            if(shootShake > 0){
                Effect.shake(shootShake, shootShake, this);
            }

            recoil = recoilAmount;
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
            if(exp > maxExp) exp = maxExp;
        }

        //hub methods
        public boolean hubValid(){
            return hub != null && !hub.dead; //todo hub.links.contains this
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
}
