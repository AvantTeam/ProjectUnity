package unity.libraries;

import mindustry.content.*;
import mindustry.type.*;
public class Recipe {
	public final InputContents input;
	public final OutputContents output;
	public final float craftTime;
	public Recipe(InputContents input,OutputContents output,float craftTime) {
		this.input=input;
		this.output=output;
		this.craftTime=craftTime;
	}
	public static class InputContents{
		public final ItemStack[] items;
		public final LiquidStack[] liquids;
		public final float power;
		public InputContents(ItemStack[] items,LiquidStack[] liquids,float power) {
			this.items=items;
			this.liquids=liquids;
			this.power=power;
		}
		public InputContents(ItemStack[] items,LiquidStack[] liquids) {
			this.items=items;
			this.liquids=liquids;
			power=0;
		}
		public InputContents(ItemStack[] items) {
			this.items=items;
			liquids=new LiquidStack[] {};
			power=0;
		}
		public InputContents(LiquidStack[] liquids) {
			items=new LiquidStack[] {};
			this.liquids=liquids;
			power=0;
		}
		public InputContents(float power) {
			items=new ItemStack[] {};
			liquids=new LiquidStack[] {};
			this.power=power;
		}
		public InputContents() {
			items=new ItemStack[] {};
			liquids=new LiquidStack[] {};
			power=0;
		}
	}
	public static class OutputContents{
		public final ItemStack[] items;
		public final LiquidStack[] liquids;
		public final float power;
		public OutputContents(ItemStack[] items,LiquidStack[] liquids,float power) {
			this.items=items;
			this.liquids=liquids;
			this.power=power;
		}
		public OutputContents(ItemStack[] items,LiquidStack[] liquids) {
			this.items=items;
			this.liquids=liquids;
			power=0;
		}
		public OutputContents(ItemStack[] items) {
			this.items=items;
			liquids=new LiquidStack[] {};
			power=0;
		}
		public OutputContents(LiquidStack[] liquids) {
			items=new LiquidStack[] {};
			this.liquids=liquids;
			power=0;
		}
		public OutputContents(float power) {
			items=new ItemStack[] {};
			liquids=new LiquidStack[] {};
			this.power=power;
		}
		public OutputContents() {
			items=new ItemStack[] {};
			liquids=new LiquidStack[] {};
			power=0;
		}
	}
}
