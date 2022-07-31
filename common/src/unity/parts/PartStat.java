package unity.parts;

import arc.scene.ui.layout.*;
import unity.parts.PartType.*;
import unity.util.*;

public abstract class PartStat{
    public String name;

    public PartStat(String name){
        this.name = name;
    }

    public abstract void merge(ValueMap id, Part part);

    public abstract void mergePost(ValueMap id, Part part);

    public void display(Table e){}
}
