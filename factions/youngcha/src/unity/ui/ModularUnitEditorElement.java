package unity.ui;

import arc.*;
import arc.Graphics.Cursor.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.struct.*;
import mindustry.input.*;
import unity.parts.*;

import static arc.Core.scene;
import static unity.graphics.YoungchaPal.*;

public class ModularUnitEditorElement extends Element{
    //clipping
    private final Rect scissorBounds = new Rect();
    private final Rect widgetAreaBounds = new Rect();
    //history for undos
    Seq<byte[]> prev = new Seq<>();
    int index = -1;

    public ModularUnitBlueprint blueprint;
    public ModularUnitPartType selected = null;
    public boolean mirror = true;
    //selection boxes?

    float panX = 0, panY = 0;
    float prevPanX, prevPanY;

    float mouseX, mouseY;
    float anchorX, anchorY;
    float scl = 1, targetScl = 1;
    int dragTriggered = 0;


    public ModularUnitEditorElement(){
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                dragTriggered = 0;
                anchorX = x;
                anchorY = y;
                prevPanX = panX;
                prevPanY = panY;
                mouseX = x;
                mouseY = y;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                if(Mathf.dst(anchorX, anchorY, x, y) < 5){
                    onClicked(x, y, button);
                }
            }


            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                panX = (x - anchorX) + prevPanX;
                panY = (y - anchorY) + prevPanY;
                dragTriggered++;
                mouseX = x;
                mouseY = y;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y){
                scene.setScrollFocus(ModularUnitEditorElement.this); ///AAAAAA
                mouseX = x;
                mouseY = y;
                var g = uiToGrid(x, y);
                if(!tileValid(g.x, g.y)){
                    Core.graphics.cursor(SystemCursor.hand);
                    return true;
                }
                if((selected != null && blueprint.parts[g.x][g.y] != null) ||
                (selected == null && blueprint.parts[g.x][g.y] == null)){
                    Core.graphics.cursor(SystemCursor.hand);
                }else{
                    Core.graphics.cursor(SystemCursor.arrow);
                }
                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Element toActor){
                super.exit(event, x, y, pointer, toActor);
                Core.graphics.cursor(SystemCursor.arrow);
                scene.setScrollFocus(null);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Element fromActor){
                scene.setScrollFocus(ModularUnitEditorElement.this);
            }

        });
    }

    @Override
    public void act(float delta){
        super.act(delta);
        targetScl += Core.input.axis(Binding.zoom) / 10f * targetScl;
        targetScl = Mathf.clamp(targetScl, 0.2f, 5);
    }

    public void onClicked(float x, float y, KeyCode button){
        if(button == KeyCode.mouseRight){
            selected = null;
            return;
        }
        var g = uiToGrid(x, y);
        if(selected != null){
            g.x -= (selected.w - 1) / 2;
            g.y -= (selected.h - 1) / 2;
        }
        if(tileValid(g.x, g.y)){
            if(selected != null){
                boolean b = blueprint.tryPlace(selected, g.x, g.y);
                if(b && mirror && mirrorX(g.x) != g.x){
                    blueprint.tryPlace(selected, mirrorX(g.x, selected.w), g.y);
                }
            }else{
                blueprint.displace(g.x, g.y);
                if(mirror && mirrorX(g.x) != g.x){
                    blueprint.displace(mirrorX(g.x), g.y);
                }
            }
            onAction();
        }
    }

    public boolean tileValid(int x, int y){
        return (x >= 0 && x < blueprint.w && y >= 0 && y < blueprint.h);
    }

    public void setBlueprint(ModularUnitBlueprint blueprint){
        this.blueprint = blueprint;
    }

    float gx = 0, gy = 0;

    @Override
    public void draw(){
        float cx = panX + width * 0.0f; // cam center relative to gx
        float cy = panY + height * 0.0f; // cam center relative to gy
        float prevScl = scl;
        scl += (targetScl - scl) * 0.1;

        float sclDiff = scl / prevScl;


        float dx = cx * (sclDiff - 1);
        panX += dx;
        float dy = cy * (sclDiff - 1);
        panY += dy;

        gx = gx();
        gy = gy();
        float midX = x + width * 0.5f;
        float midY = y + height * 0.5f;


        Draw.color(bgCol);
        Fill.rect(midX, midY, width, height);

        widgetAreaBounds.set(x, y, width, height);
        scene.calculateScissors(widgetAreaBounds, scissorBounds);
        if(!ScissorStack.push(scissorBounds)){
            return;
        }
        Draw.color(blueprintCol);
        rectCorner(0, 0, blueprint.w * 32, blueprint.h * 32);
        Draw.color(blueprintColAccent);
        //draw border and center lines
        Lines.stroke(5);
        rectLine(0, 0, blueprint.w * 32, blueprint.h * 32);
        int mid = blueprint.w / 2;
        if(blueprint.w % 2 == 1){
            rectLine(mid * 32, 0, 32, blueprint.h * 32);
        }else{
            line(mid * 32, 0, mid * 32, blueprint.h * 32);
        }
        mid = blueprint.h / 2;
        if(blueprint.h % 2 == 1){
            rectLine(0, mid * 32, blueprint.w * 32, 32);
        }else{
            line(0, mid * 32, blueprint.w * 32, mid * 32);
        }
        Draw.reset();

        Point2 minPoint = uiToGrid(0, 0);
        int minx = Mathf.clamp(minPoint.x, 0, blueprint.w - 1);
        int miny = Mathf.clamp(minPoint.y, 0, blueprint.h - 1);

        Point2 maxPoint = uiToGrid(width, height);
        int maxX = Mathf.clamp(maxPoint.x, 0, blueprint.w - 1);
        int maxY = Mathf.clamp(maxPoint.y, 0, blueprint.h - 1);
        //draw grid
        Draw.color(blueprintColAccent);
        for(int i = minx; i <= maxX; i++){
            for(int j = miny; j <= maxY; j++){
                if(i > 0 && j > 0){
                    Fill.square(gx + i * 32 * scl, gy + j * 32 * scl, 3, 45);
                }
            }
        }
        //draw highlight cursor
        Point2 cursor = uiToGrid(mouseX, mouseY);
        if(selected == null){
            rectCorner(cursor.x * 32, cursor.y * 32, 32, 32);
            if(mirror && mirrorX(cursor.x) != cursor.x){
                rectCorner(mirrorX(cursor.x) * 32, cursor.y * 32, 32, 32);
            }
        }


        //draw modules
        for(int i = minx; i <= maxX; i++){
            for(int j = miny; j <= maxY; j++){
                var part = blueprint.parts[i][j];
                if(part == null){
                    continue;
                }
                if(!(Math.max(part.x(), minx) == i && Math.max(part.y(), miny) == j)){
                    continue;
                }
                Draw.color(bgCol);
                rectCorner(part.x() * 32, part.y() * 32, part.type.w * 32, part.type.h * 32);
                Draw.color(blueprint.valid[i][j] ? Color.white : Color.red);
                rectCorner(part.type.icon, part.x() * 32, part.y() * 32, part.type.w * 32, part.type.h * 32);
            }
        }

        if(selected != null){
            Color highlight = Color.white;
            //trying to move center of part.
            cursor.x -= (selected.w - 1) / 2;
            cursor.y -= (selected.h - 1) / 2;
            if(!blueprint.canPlace(selected, cursor.x, cursor.y)){
                highlight = Color.red;
            }
            Draw.color(bgCol, 0.5f);
            rectCorner(cursor.x * 32, cursor.y * 32, selected.w * 32, selected.h * 32);
            Draw.color(highlight, 0.5f);
            rectCorner(selected.icon, cursor.x * 32, cursor.y * 32, 32 * selected.w, 32 * selected.h);

            if(mirror && mirrorX(cursor.x) != cursor.x){
                Draw.color(bgCol, 0.5f);
                rectCorner(mirrorX(cursor.x, selected.w) * 32, cursor.y * 32, selected.w * 32, selected.h * 32);
                Draw.color(highlight, 0.5f);
                rectCorner(selected.icon, mirrorX(cursor.x, selected.w) * 32, cursor.y * 32, 32 * selected.w, 32 * selected.h);
            }
        }

        ScissorStack.pop();

    }

    public int mirrorX(int x, int w){
        return blueprint.w - x - w;
    }

    public int mirrorX(int x){
        return blueprint.w - x - 1;
    }

    public void rectCorner(float x, float y, float w, float h){
        Fill.rect(gx + (x + w * 0.5f) * scl, gy + (y + h * 0.5f) * scl, w * scl, h * scl);
    }

    public void rectCorner(TextureRegion tr, float x, float y, float w, float h){
        Draw.rect(tr, gx + (x + w * 0.5f) * scl, gy + (y + h * 0.5f) * scl, w * scl, h * scl);
    }

    public void rectLine(float x, float y, float w, float h){
        Lines.rect(gx + x * scl, gy + y * scl, w * scl, h * scl);
    }

    public void line(float x, float y, float x2, float y2){
        Lines.line(gx + x * scl, gy + y * scl, gx + x2 * scl, gy + y2 * scl);
    }

    public float gx(){
        return x + (width - blueprint.w * 32 * scl) * 0.5f + panX;
    }

    public float gy(){
        return y + (height - blueprint.h * 32 * scl) * 0.5f + panY;
    }

    public Point2 uiToGrid(float x, float y){
        return new Point2(Mathf.floor((x - gx() + this.x) / (32f * scl)), Mathf.floor((y - gy() + this.y) / (32f * scl)));
    }

    @Override
    public float getPrefWidth(){
        return Core.graphics.getWidth();
    }

    @Override
    public float getPrefHeight(){
        return Core.graphics.getHeight();
    }

    @Override
    public float getMinWidth(){
        return 32;
    }

    @Override
    public float getMinHeight(){
        return 32;
    }

    public void select(ModularUnitPartType type){
        selected = type;
    }

    public void deselect(){
        selected = null;
    }

    public void onAction(){
        prev.add(blueprint.encode());
        if(index != prev.size - 2){
            prev.removeRange(index + 2, prev.size - 1);
        }
        index++;
    }

    public void redo(){
        index++;
        if(index >= prev.size){
            index = prev.size - 1;
            return;
        }
        blueprint.decode(prev.get(index));
    }

    public void undo(){
        index--;
        if(index < 0){
            index = -1;
            return;
        }
        blueprint.decode(prev.get(index));
    }

}
