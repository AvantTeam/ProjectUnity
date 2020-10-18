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
		this(length, color);
		this.angle = angle;
		this.strength = strength;
	}

	public LightData(LightData ld){ this(ld.angle, ld.strength, ld.length, ld.color); }

	public LightData set(int angle, float strength, int length, Color color){
		this.angle = angle;
		this.strength = strength;
		this.length = length;
		this.color = color;
		initialized = true;
		return this;
	}

	public LightData set(LightData ld){
		angle = ld.angle;
		strength = ld.strength;
		length = ld.length;
		color = ld.color;
		initialized = true;
		return this;
	}

	public LightData angle(int angle){
		this.angle = angle;
		return this;
	}

	public LightData strength(float strength){
		this.strength = strength;
		return this;
	}

	public LightData length(int length){
		this.length = length;
		return this;
	}

	public LightData color(Color color){
		this.color = color;
		return this;
	}
}
