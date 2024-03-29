package unity.type;

import arc.func.*;
import mindustry.type.*;

public class CloneableSetWeapon extends Weapon{

    public CloneableSetWeapon(String name){
        super(name);
    }

    public Weapon set(Cons<Weapon> con){
        Weapon w = copy();
        con.get(w);
        return w;
    }

    public Weapon flp(Cons<Weapon> con){
        Weapon w = copy();
        w.name += "-flipped";
        w.flipSprite = true;
        con.get(w);
        return w;
    }
}
