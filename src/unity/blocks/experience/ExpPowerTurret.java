package unity.blocks.experience;

import java.util.EnumMap;
import arc.util.io.*;
import arc.audio.Sound;
import arc.struct.ObjectSet;
import mindustry.gen.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.meta.ExpType;

public class ExpPowerTurret extends PowerTurret implements ExpBlockBase{
	protected final int maxLevel;
	protected final EnumMap<ExpType, ObjectSet<ExpField>> expFields = new EnumMap<>(ExpType.class);
	protected Effect levelUpFx = Fx.upgradeCore;
	protected Sound levelUpSound = Sounds.message;
	public final boolean hasCustomUpdate;
	{
		for (ExpType type : ExpType.values()) expFields.put(type, new ObjectSet<>(4));
	}

	public ExpPowerTurret(String name, int maxLevel, boolean hasCustomUpdate){
		super(name);
		this.maxLevel = maxLevel;
		this.hasCustomUpdate = hasCustomUpdate;
	}

	public ExpPowerTurret(String name, boolean hasCustomUpdate){ this(name, 20, hasCustomUpdate); }

	public ExpPowerTurret(String name, int maxLevel){ this(name, maxLevel, false); }

	public ExpPowerTurret(String name){ this(name, 20, false); }

	@Override
	public int getMaxLevel(){ return maxLevel; }

	@Override
	public void setBars(){
		super.setBars();
		expSetBars(bars);
	}

	@Override
	public void setStats(){
		super.setStats();
		expSetStats(stats);
	}

	@Override
	public EnumMap<ExpType, ObjectSet<ExpField>> getExpFields(){ return expFields; }

	public class ExpPowerTurretBuild extends PowerTurretBuild implements ExpBuildBase{
		private int exp = 0, level = 0;

		@Override
		public void updateTile(){
			setExpStats();
			if (hasCustomUpdate) expUpdate();
			else super.updateTile();
		}

		@Override
		public void write(Writes write){
			super.write(write);
			expWrite(write);
		}

		@Override
		public void read(Reads read, byte revision){
			super.read(read, revision);
			expRead(read, revision);
		}

		@Override
		public Block getBlock(){ return block; }

		@Override
		public EnumMap<ExpType, ObjectSet<ExpField>> getBlockExpFields(){ return expFields; }

		@Override
		public void levelUpEffect(){
			levelUpFx.at(x, y, size);
			levelUpSound.at(x, y);
		}

		@Override
		public int totalExp(){ return exp; }

		@Override
		public void setExp(int a){ exp = a; }

		@Override
		public int getBlockMaxLevel(){ return maxLevel; }

		@Override
		public int getLevel(){ return level; }

		@Override
		public void setLevel(int a){ level = a; }
	}
}
