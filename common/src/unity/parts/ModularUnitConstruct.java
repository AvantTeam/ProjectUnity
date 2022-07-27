package unity.parts;

import arc.struct.*;
import unity.parts.ModularUnitBlueprint.*;

import static unity.parts.Blueprint.sb;

public class ModularUnitConstruct extends Blueprint.Construct<ModularPart>{
    public final Seq<ModularPart> hasCustomDraw;

    public ModularUnitConstruct(ModularPart[][] parts, Seq<ModularPart> partsList){
        super(parts, partsList);
        hasCustomDraw = new Seq<>();
        for(var part : partsList){
            if(part.type.open || part.type.hasCellDecal || part.type.hasExtraDecal) hasCustomDraw.add(part);
        }
    }

    public boolean isEmpty(){
        return partsList.isEmpty();
    }

    @Override
    public byte[] toData(){
        var step = PartData.step();
        var data = new byte[2 * partsList.size * step];
        data[0] = sb((byte)(parts.length));
        data[1] = sb((byte)(parts[0].length));
        for(int i = 0; i < partsList.size; i++){
            var part = partsList.get(i);
            new PartData(part.type.id, part.x, part.y).pack(data, 2 + step * i);
        }
        return data;
    }
}
