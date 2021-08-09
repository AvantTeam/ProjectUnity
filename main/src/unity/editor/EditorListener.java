package unity.editor;

import arc.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import unity.mod.*;

import static mindustry.Vars.*;

/**
 * Base class for custom game editor listeners.
 * @author GlennFolker
 */
public abstract class EditorListener{
    public EditorListener(){
        Events.on(ClientLoadEvent.class, c -> registerEvents());
    }

    protected void registerEvents(){
        Triggers.listen(Trigger.update, () -> valid(this::update));
        Triggers.listen(Trigger.draw, () -> valid(this::draw));

        Events.on(StateChangeEvent.class, e -> valid(() -> {
            if(e.from == State.menu && e.to == State.playing){
                begin();
            }else if(e.to == State.menu){
                end();
            }
        }));
    }

    public void begin(){}

    public void end(){}

    public void update(){}

    public void draw(){}

    public void valid(Runnable run){
        if(state.isEditor()) run.run();
    }
}
