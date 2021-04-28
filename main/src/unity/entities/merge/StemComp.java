package unity.entities.merge;

import arc.func.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.Stemc.*;

@MergeComp
@SuppressWarnings("unchecked")
class StemComp extends Block{
    boolean preserveDraw = true;
    boolean preserveUpdate = true;

    @ReadOnly Cons<StemBuildc> draw = e -> {};
    @ReadOnly Cons<StemBuildc> update = e -> {};

    public StemComp(String name){
        super(name);
    }
    
    public <T extends StemBuildc> void draw(Cons<T> draw){
        this.draw = (Cons<StemBuildc>)draw;
    }

    public <T extends StemBuildc> void update(Cons<T> update){
        this.update = (Cons<StemBuildc>)update;
    }

    public class StemBuildComp extends Building{
        public float fdata;

        @Override
        @Replace
        public void draw(){
            if(preserveDraw){
                super.draw();
            }

            draw.get(self());
        }

        @Override
        @Replace
        public void updateTile(){
            if(preserveUpdate){
                super.updateTile();
            }

            update.get(self());
        }
    }
}
