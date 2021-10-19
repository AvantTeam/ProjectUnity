package unity.editor;

import arc.*;
import mindustry.core.GameState.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import unity.map.*;
import unity.mod.*;

import static mindustry.Vars.*;

/**
 * Base class for custom game editor listeners. These listeners will only attach themselves if the edited map's name
 * matches with one of the scripted sectors, e.g. the map {@code salvaged-laboratory} will trigger the listeners if
 * a sector preset with the name {@code unity-salvaged-laboratory} exists and is an instance of {@link ScriptedSector}.
 * @author GlennFolker
 */
public abstract class EditorListener{
    private ScriptedSector sector;
    private boolean attached;

    public EditorListener(){
        Events.on(ClientLoadEvent.class, c -> registerEvents());
    }

    protected void registerEvents(){
        Triggers.listen(Trigger.update, () -> valid(this::update));
        Triggers.listen(Trigger.drawOver, () -> valid(this::draw));

        Events.on(StateChangeEvent.class, e -> {
            if(e.from == State.menu && e.to == State.playing && state.isEditor()){
                MappableContent c = content.getByName(ContentType.sector, "unity-" + editor.tags.get("name"));
                if(c instanceof ScriptedSector sect){
                    attached = true;
                    sector = sect;
                    begin();
                }
            }else if(attached && e.to == State.menu){
                end();
                attached = false;
                sector = null;
            }
        });
    }

    public void begin(){}

    public void end(){}

    public void update(){}

    public void draw(){}

    public void valid(Runnable run){
        if(attached && state.isEditor()) run.run();
    }

    public ScriptedSector sector(){
        return sector;
    }
}
