package unity.entities.merge;

import arc.func.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.Stemc.*;

/** @author GlennFolker */
@MergeComp
@SuppressWarnings("unchecked")
public class StemComp extends Block{
    public boolean preserveDraw = true;
    public boolean preserveUpdate = true;

    protected @ReadOnly Cons<StemBuildc> draw = e -> {};
    protected @ReadOnly Cons<StemBuildc> update = e -> {};

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
