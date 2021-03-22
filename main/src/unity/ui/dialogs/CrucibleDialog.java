package unity.ui.dialogs;

import mindustry.graphics.*;
import mindustry.ui.dialogs.*;
import unity.world.blocks.production.Crucible.*;

import static arc.Core.*;

public class CrucibleDialog extends BaseDialog{
    private CrucibleBuild build;
    
    public CrucibleDialog(CrucibleBuild build){
        super("@info.title");
        
        this.build = build;
        
        shown(() -> {
            app.post(this::setup);
        });
        
        shown(this::setup);
        onResize(this::setup);
    }
    
    void setup(){
        cont.clear();
        buttons.clear();
        
        float w = graphics.isPortrait() ? 320f : 640f;
        
        cont.table(t -> {
            Runnable set = () -> { //TODO also show the contents in a form of list
                t.clearChildren();
                t.left();
                
                t.label(() -> bundle.get("stat.unity.crucible.temp")).color(Pal.accent).growX().row();
                t.add(build.crucible().getIconBar()).padTop(4f).growX().row();
                
                t.label(() -> bundle.get("stat.unity.crucible.contents")).color(Pal.accent).growX().row();
                t.add(build.crucible().getStackedBars()).padTop(4f).growX();
            };
            
            set.run();
            t.update(() -> { //TODO detect whether things have changed or not
                set.run();
            });
        }).width(w);
        
        addCloseButton();
    }
}