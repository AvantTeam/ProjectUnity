package unity.world.blocks.light;

public interface LightRepeaterBuildBase{
	default LightData calcLight(LightData ld, int i){
		return new LightData(ld).length(ld.length - i + 1);
	}
}
