package unity.blocks.light;

//blame sk
public interface LightRepeaterBuildBase{
	default LightData calcLight(LightData ld, int i){
		return new LightData(ld.angle, ld.strength, ld.length - i + 1, ld.color);
	}
}
