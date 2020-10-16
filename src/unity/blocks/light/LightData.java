package unity.blocks.light;

import arc.graphics.Color;

public class LightData{
	protected int angle = 0, length = 50;
	protected float strength = 100f;
	Color color = Color.white;

	public LightData(int length, Color color){
		this.length = length;
		this.color = color;
	}

	public LightData(int angle, float strength, int length, Color color){
		this.angle = angle;
		this.strength = strength;
		this.length = length;
		this.color = color;
	}
}
