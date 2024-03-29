package unity.type.decal;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;

public abstract class UnitDecorationType{
    public Func<UnitDecorationType, UnitDecoration> decalType = UnitDecoration::new;
    public boolean top = false;

    public void update(Unit unit, UnitDecoration deco){}

    public void added(Unit unit, UnitDecoration deco){}

    public void draw(Unit unit, UnitDecoration deco){}

    public void drawIcon(Func<TextureRegion, Pixmap> prov, Pixmap icon, Func<TextureRegion, TextureRegion> outliner){}

    public void load(){}

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
