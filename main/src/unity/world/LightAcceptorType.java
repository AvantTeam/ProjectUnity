package unity.world;

import arc.func.*;
import unity.gen.LightHoldc.*;

/**
 * Base class for light-accepting slot types in building. These slots take place in the building's spanned tiles and are
 * able to receive light lasers. Also provides extensive functions like drawing and updating
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class LightAcceptorType{
    /** The x slot position relative to the building's top left position */
    public int x;
    /** The y slot position relative to the building's top left position */
    public int y;
    /** The slot width */
    public int width;
    /** The slot height */
    public int height;
    /** Required light strength; ranges from [0..1] if enabled, <=0 otherwise */
    public float required;

    public Cons2<LightHoldBuildc, LightAcceptor> update = (e, s) -> {};
    public Cons2<LightHoldBuildc, LightAcceptor> draw = (e, s) -> {};

    public LightAcceptorType(){
        this(0, 0, 1f);
    }

    public LightAcceptorType(int x, int y, float required){
        this(x, y, 1, 1, required);
    }

    public LightAcceptorType(int x, int y, int width, int height, float required){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.required = required;
    }

    public <T extends LightHoldBuildc, V extends LightAcceptor> LightAcceptorType update(Cons2<T, V> update){
        this.update = (Cons2<LightHoldBuildc, LightAcceptor>)update;
        return this;
    }

    public <T extends LightHoldBuildc, V extends LightAcceptor> LightAcceptorType draw(Cons2<T, V> draw){
        this.draw = (Cons2<LightHoldBuildc, LightAcceptor>)draw;
        return this;
    }

    public LightAcceptor create(LightHoldBuildc hold){
        return new LightAcceptor(this, hold);
    }
}
