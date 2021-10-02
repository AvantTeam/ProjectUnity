package unity.ui;

import arc.input.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.util.*;

public class MoveDragListener extends InputListener{
    private final Element element;
    private float lastx, lasty;

    public MoveDragListener(Element element){
        this.element = element;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
        var v = element.localToStageCoordinates(Tmp.v1.set(x, y));
        lastx = v.x;
        lasty = v.y;

        element.toFront();
        return true;
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer){
        var v = element.localToStageCoordinates(Tmp.v1.set(x, y));

        element.moveBy(v.x - lastx, v.y - lasty);
        lastx = v.x;
        lasty = v.y;
    }
}
