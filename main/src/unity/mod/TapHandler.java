package unity.mod;

import arc.*;
import arc.input.*;
import arc.input.GestureDetector.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import unity.sync.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class TapHandler{
    private final Seq<TapListener> listeners = new Seq<>();
    private boolean press = false;

    public TapHandler(){
        if(headless) return;

        if(mobile){
            Core.input.addProcessor(new GestureDetector(new GestureListener(){
                @Override
                public boolean tap(float x, float y, int count, KeyCode button){
                    if(count == 2){
                        if(
                            state.isMenu() ||
                            //control.input.lineMode || <- doesn't exist
                            Core.scene.hasMouse(x, y) ||
                            control.input.isPlacing() ||
                            control.input.isBreaking() ||
                            control.input.selectedUnit() != null
                        ){
                            return false;
                        }

                        UnityCall.tap(player, x, y);
                    }

                    return false;
                }

                //this is to suppress android throwing AbstractMethodError which happens because of god knows what.
                @Override
                public boolean touchDown(float x, float y, int pointer, KeyCode button){
                    return false;
                }

                @Override
                public boolean longPress(float x, float y){
                    return false;
                }

                @Override
                public boolean fling(float velocityX, float velocityY, KeyCode button){
                    return false;
                }

                @Override
                public boolean pan(float x, float y, float deltaX, float deltaY){
                    return false;
                }

                @Override
                public boolean panStop(float x, float y, int pointer, KeyCode button){
                    return false;
                }

                @Override
                public boolean zoom(float initialDistance, float distance){
                    return false;
                }

                @Override
                public boolean pinch(Vec2 initialPointer1, Vec2 initialPointer2, Vec2 pointer1, Vec2 pointer2){
                    return false;
                }

                @Override
                public void pinchStop(){}
            }));
        }else{
            Events.run(Trigger.update, () -> {
                if(!state.isMenu()){
                    if(Core.input.keyDown(Binding.boost) && !(Core.scene.getKeyboardFocus() instanceof TextField)){
                        if(!press){
                            press = true;
                            UnityCall.tap(player, Core.input.mouseWorldX(), Core.input.mouseWorldY());
                        }
                    }else{
                        press = false;
                    }
                }else{
                    press = false;
                }
            });
        }
    }

    public void tap(Player player, float x, float y){
        for(var listener : listeners){
            listener.tap(player, x, y);
        }
    }

    public void addListener(TapListener listener){
        listeners.add(listener);
    }

    public void removeListener(TapListener listener){
        listeners.remove(listener);
    }

    public interface TapListener{
        void tap(Player player, float x, float y);
    }
}
