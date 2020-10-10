package unity.blocks.experience;

import arc.util.io.*;

public interface ExpBuildBase{
	void setExpStats();

	int totalExp();

	void setExp(int a);

	void incExp(int a);

	default void levelUp(int lvl){

	}

	default void customUpdate(){

	}

	default void customWrite(Writes write){

	}

	default void customRead(Reads read, byte revision){

	}
}
