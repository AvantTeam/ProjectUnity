package unity.entities.comp;

import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.type.*;
import unity.type.exp.*;
import unity.util.*;

import static mindustry.Vars.*;

public interface ExpBuildc extends ExpEntityc<Block, ExpBlock>, Buildingc{
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

    default void upgrade(int i){
        var upgrade = expType().upgrades.get(i);
        if(level() >= upgrade.min && level() <= upgrade.max){
            upgradeBlock(upgrade.type);
        }
    }

    default void upgradeBlock(Block block){
        Tile tile = tile();
        int[] links = power() == null ? new int[0] : power().links.toArray();

        if(block.size > expType().type.size){
            tile = Utils.getBestTile(this, block.size, expType().type.size);
            if(tile == null) return;

            tile.setBlock(block, team(), rotation());
            expType().upgradeSound.at(this);
            expType().upgradeEffect.at(tile.drawx(), tile.drawy(), block.size, expType().upgradeColor);

            if(!net.client()){
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
    default void read(Reads read){
        exp(read.f());
    }
}
