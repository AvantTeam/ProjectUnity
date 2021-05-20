package unity.entities.merge;

import arc.func.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.Stemc.*;
import unity.world.meta.*;

@SuppressWarnings({"unchecked", "unused"})
@MergeComponent
class StemComp extends Block{
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
        transient @ReadOnly StemData data = new StemData();

        @Override
        public void draw(){
            draw.get(self());
        }

        @Override
        public void updateTile(){
            update.get(self());
        }

        @Override
        public void write(Writes write){
            data.write(write);
        }

        @Override
        public void read(Reads read, byte revision){
            data.read(read);
        }
    }
}
