package unity.units;

import arc.struct.Seq;
import arc.graphics.g2d.*;
import mindustry.type.*;
import mindustry.gen.*;

import static arc.Core.*;

public class WormUnitType extends UnitType{
	public TextureRegion segmentRegion, tailRegion, segmentCellRegion;
	private int idType;
	protected float segmentOffset;
	protected final Seq<Weapon> segWeapSeq = new Seq<Weapon>();
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
		sortWeapons(segWeapSeq);
	}

	public void sortWeapons(Seq<Weapon> weaponSeq){
		Seq<Weapon> mapped = new Seq<Weapon>();
		for (int i = 0, len = weaponSeq.size; i < len; i++){
			Weapon w = weaponSeq.get(i);
			mapped.add(w);
			if (w.mirror){
				Weapon copy = w.copy();
				copy.x *= -1;
				copy.shootX *= -1;
				copy.flipSprite = !copy.flipSprite;
				mapped.add(copy);
				w.reload *= 2;
				copy.reload *= 2;
				w.otherSide = mapped.size - 1;
				copy.otherSide = mapped.size - 2;
			}
		}
		weaponSeq.set(mapped);
	}

	public TextureRegion segmentRegion(){ return segmentRegion; }

	public TextureRegion tailRegion(){ return tailRegion; }

	public TextureRegion getSegmentCell(){ return segmentCellRegion; }

	public void setTypeID(int id){ idType = id; }

	public int getTypeID(){ return idType; }

	@Override
	public void drawBody(Unit unit){
		super.drawBody(unit);
		float originZ = Draw.z();
		if (!(unit instanceof WormDefaultUnit)) return;
		WormDefaultUnit wormUnit = (WormDefaultUnit) unit;
		for (int i = 0; i < segmentLength; i++){
			Draw.z(originZ - (i + 1) / 500f);
			wormUnit.segmentUnits[i].drawBody();
			drawWeapons(wormUnit.segmentUnits[i]);
		}
		Draw.z(originZ);
	}

	@Override
	public void drawShadow(Unit unit){
		super.drawShadow(unit);
		if (!(unit instanceof WormDefaultUnit)) return;
		WormDefaultUnit wormUnit = (WormDefaultUnit) unit;
		for (int i = 0; i < segmentLength; i++) wormUnit.segmentUnits[i].drawShadow();
	}

	@Override
	public void drawOcclusion(Unit unit){
		super.drawOcclusion(unit);
		if (unit instanceof WormDefaultUnit) ((WormDefaultUnit) unit).drawOcclusion();
	}
}
