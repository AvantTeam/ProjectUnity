package unity.entities.merge;

import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
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
import unity.gen.Expc.*;
import unity.gen.*;
import unity.type.*;
import unity.util.*;
import unity.world.meta.*;

import static mindustry.Vars.*;

@SuppressWarnings({"unused"})
@MergeComponent
abstract class ExpComp extends Block implements Stemc{
    int maxLevel = 20;
    float maxExp;
    float orbRefund = 0.3f;

    Color minLevelColor = Pal.accent;
    Color maxLevelColor = Color.valueOf("fff4cc");
    Color minExpColor = Color.valueOf("84ff00");
    Color maxExpColor = Color.valueOf("90ff00");
    Color upgradeColor = Color.green;

    DynamicProgression progression = new DynamicProgression();

    Seq<ExpUpgrade> upgrades = new Seq<>();
    /** Maps {@link #upgrades} into 2D array. Do NOT modify */
    @ReadOnly ExpUpgrade[][] upgradesPerLevel;
    boolean enableUpgrade;
    boolean hasUpgradeEffect = true;

    float sparkleChance = 0.08f;
    Effect sparkleEffect = UnityFx.sparkleFx;
    Effect upgradeEffect = UnityFx.upgradeBlockFx;
    Sound upgradeSound = Sounds.message;

    boolean hasExp = false;
    boolean hub = false;
    boolean conveyor = false;
    boolean noOrbCollision = true;

    float orbMultiplier = 0.8f;
    boolean condConfig;

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

        if(enableUpgrade){
            configurable = condConfig = true;
            saveConfig = false;

            config(Integer.class, (build, value) -> {
                if(value >= 0 && build instanceof ExpBuildc exp){
                    exp.upgrade(value);
                }
            });
        }
    }

    public void setUpgrades(){}

    @Override
    public void setStats(){
        if(headless){
            stats.add(Stat.itemCapacity, "@", Core.bundle.get("explib.lvlAmount"));
            stats.add(Stat.itemCapacity, "@", Core.bundle.get("explib.expAmount"));
        }else{
            stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.lvlAmount", maxLevel));
            stats.add(Stat.itemCapacity, "@", Core.bundle.format("explib.expAmount", requiredExp(maxLevel)));
        }

        if(upgrades.size > 0){
            stats.add(Stat.abilities, table -> table.table(t -> {
                t.row();
                t.add("$explib.upgrades");
                t.row();

                for(ExpUpgrade upgrade : upgrades){
                    if(upgrade.min > maxLevel || upgrade.hide) continue;

                    float size = 8f * 3f;

                    t.add("[green]" + Core.bundle.get("explib.level") + " " + upgrade.min + "[] ");

                    t.image(upgrade.type.uiIcon).size(size).padRight(4).scaling(Scaling.fit);
                    t.add(upgrade.type.localizedName).left();
                    t.row();
                }
            }));
        }
    }

    @Override
    public void setBars(){
        bars.add("level", b -> {
            if(b instanceof ExpBuildc build){
                return new Bar(
                    () -> Core.bundle.get("explib.level") + " " + build.level(),
                    () -> Tmp.c1.set(minLevelColor).lerp(maxLevelColor, build.levelf()),
                    build::levelf
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
                    :    Core.bundle.get("explib.max"),
                    () -> Tmp.c1.set(minExpColor).lerp(maxExpColor, build.expf()),
                    build::expf
                );
            }else{
                throw new IllegalStateException("Building type for '" + localizedName + "' is not an instance of 'ExpBuildc'!");
            }
        });
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

    public abstract class ExpBuildComp extends Building implements StemBuildc{
        float exp;
        boolean checked = true;

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
                        Tile powtile = world.tile(link);

                        if(powtile.block() instanceof PowerNode){
                            powtile.build.configure(link);
                        }
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
            if(!enableUpgrade) return;

            checked = true;

            int level = level();
            if(!condConfig){
                upgradeTable(table, level);
            }else{
                if(currentUpgrades(level).length == 0) return;

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
                    t.image(block.uiIcon).size(38).padRight(2);

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
            table.button(Icon.infoCircle, Styles.emptyi, () -> ui.content.show(block)).size(40);
        }

        private void upgradeButton(Table table, int index, int level){
            Integer i = index;
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
            progression.apply(level());
        }

        @Override
        public void killed(){
            ExpOrbs.spreadExp(x, y, exp * orbRefund, spreadAmount());
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
            return super.configTapped() && (
                !enableUpgrade ||
                condConfig ||
                !upgrades.isEmpty()
            );
        }
    }
}
