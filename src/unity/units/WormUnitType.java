package unity.units;

import arc.func.*;
import arc.struct.Seq;
import arc.graphics.g2d.TextureRegion;
import mindustry.type.*;

import static arc.Core.*;

public class WormUnitType extends UnitType{
	public TextureRegion segmentRegion, tailRegion, segmentCellRegion;
	private int idType;
	protected float segmentOffset;
	protected final Seq<Weapon> segWeapSeq = new Seq();
	public final int segmentLength;

	public WormUnitType(String name, int segmentLength){
		super(name);
		this.segmentLength = segmentLength;
	}

	public WormUnitType(String name){ this(name, 9); }

	@Override
	public void load(){
		super.load();
		segmentRegion = atlas.find(name + "-segment");
		segmentCellRegion = atlas.find(name + "-segment-cell");
		tailRegion = atlas.find(name + "-tail");
		segWeapSeq.each(w -> w.load());
	}

	@Override
	public void init(){
		super.init();
		//TODO
	}

	public TextureRegion segmentRegion(){ return segmentRegion; }

	public TextureRegion tailRegion(){ return tailRegion; }

	public TextureRegion getSegmentCell(){ return segmentCellRegion; }

	public void setTypeID(int id){ idType = id; }

	public int getTypeID(){ return idType; }

	public float segmentOffset(){ return segmentOffset; }

	public Seq<Weapon> getSegmentWeapon(){ return segWeapSeq; }
}
