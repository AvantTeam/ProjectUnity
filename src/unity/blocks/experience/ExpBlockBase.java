package unity.blocks.experience;

import arc.struct.ObjectMap;
import mindustry.world.meta.BlockStat;

public interface ExpBlockBase{
	public static final ObjectMap<String, BlockStat> forStats = ObjectMap.of(/*Turret stats*/"range",
		BlockStat.shootRange, "inaccuracy", BlockStat.inaccuracy, "reloadTime", BlockStat.reload, "targetAir",
		BlockStat.targetsAir, "targetGround", BlockStat.targetsGround);

	int getLevel(int exp);

	default int getRequiredExp(int lvl){ return lvl * lvl * 10; }

	void addExpField(String expType, String field, int start, int intensity);

	float getLvlf(int exp);

	default boolean isNumerator(BlockStat stat){ return stat == BlockStat.inaccuracy || stat == BlockStat.shootRange; }
}