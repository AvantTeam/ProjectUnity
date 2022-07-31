package unity.parts;

import arc.scene.ui.layout.*;
import unity.parts.PartType.*;

public abstract class PartStat{
    public String name;

    public PartStat(String name){
        this.name = name;
    }

    public abstract void merge(PartStatMap id, Part part);

    public abstract void mergePost(PartStatMap id, Part part);

    public void display(Table e){}
}
