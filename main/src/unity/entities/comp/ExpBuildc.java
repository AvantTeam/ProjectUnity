package unity.entities.comp;

import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.type.*;
import unity.type.exp.*;
import unity.util.*;

import static mindustry.Vars.*;

public interface ExpBuildc extends ExpEntityc<Block, ExpBlock>, Buildingc{
    @Initialize(eval = "false")
    boolean checked();

    void checked(boolean checked);

    @Override
    default ExpBlock expType(){
        ExpType<?> type = ExpMeta.map(block());
        if(!(type instanceof ExpBlock block)){
            throw new IllegalStateException("No 'ExpBlock' type found for '" + block().localizedName + "'");
        }

        return block;
    }

    @Override
    default float spreadAmount(){
        return 3f * block().size;
    }

    default boolean consumesOrb(){
        return expType().hasExp && exp() < expType().maxExp;
    }

    @Override
    default void killed(){
        ExpEntityc.super.killed();
    }

    @Override
    default void upgradeDefault(){
        ExpEntityc.super.upgradeDefault();
        expType().upgradeEffect.at(this, block().size);

        if(expType().enableUpgrade){
            if(
                !Structs.eq(currentUpgrades(level() - 1), currentUpgrades(level())) &&
                currentUpgrades(level()).length > 0
            ){
                checked(false);
            }

            if(!headless && control.input.frag.config.getSelectedTile() == this){
                control.input.frag.config.hideConfig();
            }
        }
    }

    @Override
    default void sparkle(){
        expType().sparkleEffect.at(x(), y(), block().size, expType().upgradeColor);
    }

    default void upgrade(int index){
        var upgrade = expType().upgrades.get(index);
        if(level() >= upgrade.min && level() <= upgrade.max){
            upgradeBlock(upgrade.type);
        }
    }

    default void upgradeBlock(Block block){
        Tile tile = tile();
        int[] links = power() == null ? new int[0] : power().links.toArray();

        if(block.size > expType().type.size){
            tile = Utils.getBestTile(this, block.size, expType().type.size);
        }
        if(tile == null) return;

        tile.setBlock(block, team(), rotation());
        expType().upgradeSound.at(this);
        if(Mathf.chance(expType().sparkleChance)) expType().sparkleEffect.at(tile.drawx(), tile.drawy(), block.size, expType().upgradeColor);
        expType().upgradeEffect.at(tile.drawx(), tile.drawy(), block.size, expType().upgradeColor);

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
    default void buildConfiguration(Table table){
        expBuildConfiguration(table);
    }

    default void expBuildConfiguration(Table table){
        if(!expType().enableUpgrade){
            return;
        }

        checked(true);

        int level = level();
        if(!expType().condConfig){
            upgradeTable(table, level);
        }else{
            if(currentUpgrades(level).length == 0){
                return;
            }

            table.table(t -> upgradeTable(t, level));
            table.row();
            table.image().pad(2f).width(130f).height(4f).color(expType().upgradeColor);
            table.row();
        }
    }

    default void upgradeTable(Table table, int level){
        var upgrades = currentUpgrades(level);
        if(upgrades.length == 0) return;

        int[] i = {0};
        for(; i[0] < upgrades.length; i[0]++){
            Block block = upgrades[i[0]].type;
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
                    Styles.emptyi.imageUpColor = expType().upgradeColor;
                }

                upgradeButton(t, upgrades[i[0]].index(), level);
                if(upgrades[i[0]].min == level){
                    Styles.emptyi.imageUpColor = Color.white;
                }
            }).height(50).growX();

            if(i[0] < upgrades.length - 1) table.row();
        }
    }

    default void infoButton(Table table, Block block){
        table.button(Icon.infoCircle, Styles.emptyi, () -> {
            ui.content.show(block);
        }).size(40);
    }

    default void upgradeButton(Table table, int index, int level){
        Integer i = Integer.valueOf(index);
        table.button(Icon.up, Styles.emptyi, () -> {
            control.input.frag.config.hideConfig();
            configure(i);
        }).size(40);
    }

    default ExpType<Block>.ExpUpgrade[] currentUpgrades(int level){
        return expType().upgradesPerLevel[level];
    }

    @Override
    @MethodPriority(-1)
    default void update(){
        ExpEntityc.super.update();
    }

    @Override
    default void updateTile(){
        if(expType().enableUpgrade && !checked() && Mathf.chance(expType().sparkleChance)){
            sparkle();
        }
    }

    @Override
    @Replace
    default Cursor getCursor(){
        Cursor cursor = block().configurable && team() == player.team() ? SystemCursor.hand : SystemCursor.arrow;
        if(!expType().enableUpgrade || expType().condConfig){
            return cursor;
        }else{
            return expType().upgrades.isEmpty() ? SystemCursor.arrow : SystemCursor.hand;
        }
    }

    @Override
    default boolean configTapped(){
        return
            !expType().enableUpgrade ||
            expType().condConfig ||
            !expType().upgrades.isEmpty();
    }

    @Override
    default void write(Writes write){
        write.f(exp());
    }

    @Override
    default void read(Reads read, byte revision){
        exp(read.f());
    }
}
