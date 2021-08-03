package unity.type.decal;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public abstract class UnitDecorationType{
    public Func<UnitDecorationType, UnitDecoration> decalType = UnitDecoration::new;
    public boolean top = false;

    public abstract void update(Unit unit, UnitDecoration deco);

    public abstract void added(Unit unit, UnitDecoration deco);

    public abstract void draw(Unit unit, UnitDecoration deco);

    public void drawIcon(MultiPacker packer, Pixmap icon, Cons<TextureRegion> outliner){

    }

    public void load(){

    }

    public static class UnitDecoration{
        public UnitDecorationType type;

        public UnitDecoration(UnitDecorationType type){
            this.type = type;
        }

        public void update(Unit unit){
            type.update(unit, this);
        }

        public void added(Unit unit){
            type.added(unit, this);
        }
    }
}
