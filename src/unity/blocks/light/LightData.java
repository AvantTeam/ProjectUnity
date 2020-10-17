package unity.blocks.light;

import arc.graphics.Color;

public class LightData{
	protected boolean initialized = false;
	protected int angle = 0, length = 50;
	protected float strength = 100f;
	Color color = Color.white;

	public LightData(){}

	public LightData(int length, Color color){
		this.length = length;
		this.color = color;
		initialized = true;
	}

	public LightData(int angle, float strength, int length, Color color){
		this.angle = angle;
		this.strength = strength;
		this.length = length;
		this.color = color;
		initialized = true;
	}

	public void set(int angle, float strength, int length, Color color){
		this.angle = angle;
		this.strength = strength;
		this.length = length;
		this.color = color;
		initialized = true;
	}

	public void set(LightData ld){
		angle = ld.angle;
		strength = ld.strength;
		length = ld.length;
		color = ld.color;
		initialized = true;
	}
}
