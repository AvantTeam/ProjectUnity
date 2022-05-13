package younggamExperimental;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class ModularConstructorUI extends Element{
    static final Color[] colorPorts = new Color[100];
    static ImageButton prevChecked;
    static PartType currentCat;
    Point2 hover;
    final Seq<PartPlaceObj> partList = new Seq<>(), rootList = new Seq<>();
    final GridMap<PartPlaceObj> grid = new GridMap<>();
    Runnable onTileAction;
    TextureRegion partsSprite;
    PartInfo partsSelect;
    KeyCode dragButton;
    float prefHeight = 100f, costAccum = 1f, costAccumRate = 0.2f;
    int gridW = 1, gridH = 1;
    boolean isClickedRN;

    static{
        //xeloo....
        for(int i = 0; i < 100; i++) colorPorts[i] = Color.HSVtoRGB(360f * Mathf.random(), 100f * Mathf.random(0.3f, 1f), 100f * Mathf.random(0.9f, 1f), 1f);
    }

    public static ModularConstructorUI getModularConstructorUI(float pHeight, TextureRegion partsSprite, PartInfo[] partsConfig, IntSeq preConfig, int maxW, int maxH, float cstacc){
        ModularConstructorUI pp = new ModularConstructorUI();
        pp.init();
        pp.prefHeight = pHeight;
        pp.partsSprite = partsSprite;
        pp.gridW = maxW;
        pp.gridH = maxH;
        pp.costAccum = cstacc;
        if(preConfig.isEmpty()){
            for(var pinfo : partsConfig){
                if(pinfo.prePlace != null) pp.placeTile(pinfo, pinfo.prePlace.x, pinfo.prePlace.y);
            }
        }else pp.loadSave(preConfig, partsConfig);
        return pp;
    }

    public static ModularConstructorUI applyModularConstructorUI(Table table, TextureRegion partsSprite, int spriteW, int spriteH, PartInfo[] partsConfig, int maxW, int maxH, IntSeq preConfig, PartType[] categories, float cstaccum){
        PartInfo.preCalcConnection(partsConfig);
        PartInfo.assignPartSprites(partsConfig, partsSprite, spriteW, spriteH);
        ModularConstructorUI modElement = getModularConstructorUI(400f, partsSprite, partsConfig, preConfig, maxW, maxH, cstaccum);
        currentCat = null;
        Cons<Table> partSelectCons = scrollTable -> {
            float costInc = modElement.costAccum;
            scrollTable.clearChildren();
            scrollTable.top().left();
            for(var pinfo : partsConfig){
                if(pinfo.cannotPlace || pinfo.category != currentCat) continue;
                scrollTable.row();
                addConsButton(scrollTable, butt -> {
                    butt.top().left().margin(12f).defaults().left().top();
                    butt.add(pinfo.name).size(170f, 45f).row();
                    butt.table(topTable -> {
                        topTable.add(new BorderImage(pinfo.texRegion, 2f)).size(36f).padTop(-4f).padLeft(-4f).padRight(4f);
                        topTable.button(Tex.whiteui, Styles.cleari, 50f, () -> {
                            displayPartInfo(pinfo);
                        }).size(50f).get().getStyle().imageUp = Icon.infoSmall;
                    }).marginLeft(4f).row();
                    butt.add("[accent]Cost").padBottom(4f).row();
                    butt.table(botTable -> {
                        int i = 0;
                        for(var cst : pinfo.cost){
                            botTable.image(cst.item.uiIcon).size(iconSmall).left();
                            botTable.add("[gray]" + Mathf.floor(cst.amount * costInc)).padLeft(2f).left().padRight(4f);
                            if(i++ % 2 == 1) botTable.row();
                        }
                    });
                }, Styles.defaultb, () -> modElement.partsSelect = pinfo).minWidth(150f).padBottom(8f);
            }
        };
        Table parts = new Table();
        Runnable rebuildParts = () -> partSelectCons.get(parts);
        ScrollPane pane = new ScrollPane(parts, Styles.defaultPane);
        prevChecked = null;
        Table catTable = new Table();
        catTable.margin(12f).top().left();
        for(var i : categories){
            ImageButton catButt = new ImageButton(i.region, Styles.clearTogglei);
            catButt.clicked(() -> {
                currentCat = i;
                rebuildParts.run();
                catButt.setChecked(true);
                if(prevChecked != null) prevChecked.setChecked(false);
                prevChecked = catButt;
            });
            catTable.add(catButt);
        }
        rebuildParts.run();
        Table leftSide = new Table();
        leftSide.add(catTable).align(Align.left).row();
        leftSide.add(pane).minWidth(200f).maxHeight(400f).align(Align.top).get().setScrollingDisabled(true, false);

        Cons<Table> costCons = cstTable -> {
            cstTable.clearChildren();
            cstTable.add("[accent]Total Cost").padBottom(4).row();
            cstTable.table(botTable -> {
                OrderedMap<Item, Integer> csTot = modElement.getTotalCost();
                for(var cost : csTot){
                    botTable.image(cost.key.uiIcon).size(iconSmall).left();
                    botTable.add("[gray]" + cost.value).padLeft(2f).left().padRight(4f).row();
                }
            });
        };
        Table totals = new Table();
        Runnable rebuildTotals = () -> costCons.get(totals);
        table.add(leftSide).minWidth(150f).align(Align.top);
        table.add(modElement).size(750f, 400f);
        table.add(totals).minWidth(100f).maxHeight(400f).align(Align.top);
        modElement.onTileAction = () -> {
            rebuildParts.run();
            rebuildTotals.run();
        };
        rebuildTotals.run();
        return modElement;
    }

    static Cell<Button> addConsButton(Table table, Cons<Button> consFunc, ButtonStyle style, Runnable runnable){
        Button button = new Button(style);
        button.clearChildren();
        button.clicked(runnable);
        consFunc.get(button);
        return table.add(button);
    }

    static void displayPartInfo(PartInfo part){
        BaseDialog dialog = new BaseDialog("Part:" + part.name);
        dialog.setFillParent(false);
        Table cont = dialog.cont;
        cont.add("[lightgray]Name:[white]" + part.name).left().row();
        cont.add("[lightgray]Description:").left().row();
        cont.add("[white]" + part.desc).wrap().fillX().left().width(500f).maxWidth(500f).get().setWrap(true);
        cont.row();
        cont.add("[accent] Stats");
        for(var stat : part.stats){
            cont.row();
            cont.add("[lightgray]" + Core.bundle.get(stat.key.name) + ": [white]" + stat.value.value.toString()).left();
        }
        dialog.buttons.button("@ok", dialog::hide).size(130f, 60f);
        dialog.update(() -> {});
        dialog.show();
    }

    @Override
    public void draw(){
        float amx = x + width * 0.5f;
        float amy = y + height * 0.5f;
        int gw = gridW * 32;
        int gh = gridH * 32;
        float gamx = amx - gw * 0.5f;
        float gamy = amy - gh * 0.5f;
        Draw.color(UnityPal.bgCol);
        Fill.rect(amx, amy, width, height);
        Draw.color(UnityPal.blueprintCol);
        Fill.rect(amx, amy, gw, gh);
        Draw.color();
        for(var p : partList){
            if(!p.valid){
                Draw.color(p.flash % 10 < 5 ? Color.pink : Color.white);
                p.flash++;
            }else Draw.color();
            Draw.rect(p.part.texRegion, p.x * 32f + gamx + p.part.tw * 16f, p.y * 32f + gamy + p.part.th * 16f, p.part.tw * 32f, p.part.th * 32f);
            drawOpenConnectionPorts(p.part, p.x, p.y, gamx, gamy);
        }
        Draw.color(Color.black);
        Fill.rect(x + 20f, y + 20f, 40f, 40f);
        Draw.color();
        if(partsSelect != null){
            Draw.rect(partsSelect.texRegion, x + 20f, y + 20f, 32f, 32f);
            if(hover != null){
                Draw.color(canPlace(partsSelect, hover.x, hover.y) ? Color.white : Color.red, 0.3f);
                Draw.rect(partsSelect.texRegion, hover.x * 32f + gamx + partsSelect.tw * 16f, hover.y * 32f + gamy + partsSelect.th * 16f, partsSelect.tw * 32f, partsSelect.th * 32f);
                drawOpenConnectionPorts(partsSelect, hover.x, hover.y, gamx, gamy);
            }
        }
    }

    void drawOpenConnectionPorts(PartInfo ps, int x, int y, float offx, float offy){
        for(var conout : ps.connInList){
            int opcx = x + conout.x + conout.dir.x;
            int opcy = y + conout.y + conout.dir.y;
            if(getPartAt(opcx, opcy) == null){
                float brcx = (opcx - conout.dir.x * 0.5f + 0.5f) * 32f + offx;
                float brcy = (opcy - conout.dir.y * 0.5f + 0.5f) * 32f + offy;
                Draw.color(Color.black);
                Fill.square(brcx, brcy, 6f, 45f);
                Draw.color(colorPorts[conout.id - 1]);
                Fill.square(brcx, brcy, 2f, 45f);
                Draw.color();
            }
        }
        for(var conout : ps.connOutList){
            int opcx = x + conout.x + conout.dir.x;
            int opcy = y + conout.y + conout.dir.y;
            if(getPartAt(opcx, opcy) == null){
                float brcx = (opcx - conout.dir.x * 0.5f + 0.5f) * 32f + offx;
                float brcy = (opcy - conout.dir.y * 0.5f + 0.5f) * 32f + offy;
                Draw.color(Color.black);
                Fill.square(brcx, brcy, 6f, 45f);
                Draw.color(colorPorts[conout.id - 1]);
                Lines.stroke(2f);
                Lines.poly(brcx, brcy, 4, 3f, 0f);
                Draw.color();
            }
        }
    }

    @Override
    public float getPrefHeight(){
        return prefHeight;
    }

    boolean inBounds(PartInfo partType, int x, int y){
        return partType != null && inBoundsRect(x, y, partType.tw, partType.th);
    }

    boolean inBoundsRect(int x, int y, int w, int h){
        return !(x < 0 || x + w > gridW || y < 0 || y + h > gridH);
    }

    boolean canPlace(PartInfo partType, int x, int y){
        return canPlaceConn(partType, x, y, true);
    }

    boolean canPlaceConn(PartInfo partType, int x, int y, boolean chkConnection){
        if(!inBounds(partType, x, y)) return false;
        for(int px = 0, lenX = partType.tw; px < lenX; px++){
            for(int py = 0, lenY = partType.th; py < lenY; py++){
                if(getPartAt(x + px, y + py) != null) return false;
            }
        }
        if(chkConnection){
            boolean hasConnection = partType.connInList.isEmpty();
            for(var i : partType.connInList){
                PartPlaceObj fromPart = getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
                if(fromPart != null) hasConnection |= partCanConnectOut(fromPart, i.x + x, i.y + y, i.id);
            }
            if(!hasConnection){
                for(var i : partType.connOutList){
                    PartPlaceObj fromPart = getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
                    if(fromPart != null) hasConnection |= partCanConnectIn(fromPart, i.x + x, i.y + y, i.id);
                }
            }
            return hasConnection;
        }
        return true;
    }

    OrderedSet<PartPlaceObj> floodFrom(PartPlaceObj part){
        OrderedSet<PartPlaceObj> visited = new OrderedSet<>(12);
        visited.add(part);
        Seq<PartPlaceObj> toVisit = new Seq<>();
        for(var i : part.parents) toVisit.add(i);
        for(var i : part.children) toVisit.add(i);
        int index = 0;
        while(index < toVisit.size){
            PartPlaceObj cPart = toVisit.get(index);
            visited.add(cPart);
            for(var i : cPart.parents){
                if(!visited.contains(i)) toVisit.add(i);
            }
            for(var i : cPart.children){
                if(!visited.contains(i)) toVisit.add(i);
            }
            index++;
        }
        return visited;
    }

    void rebuildFromRoots(){
        for(int i = 0, len = partList.size; i < len; i++) partList.get(i).valid = false;
        for(int i = 0, len = rootList.size; i < len; i++){
            //massive iterating wtf xelo
            OrderedSet<PartPlaceObj> k = floodFrom(rootList.get(i));
            for(var part : k) part.valid = true;
        }
    }

    boolean removeTile(PartPlaceObj part){
        if(part == null) return false;
        PartInfo prt = part.part;
        if(prt.isRoot) return false;
        for(var i : part.parents){
            i.children.remove(part);
        }
        int lenX = prt.tw;
        int lenY = prt.th;
        for(int px = 0; px < lenX; px++){
            for(int py = 0; py < lenY; py++) grid.remove(part.x + px, part.y + py);
        }
        partList.remove(part);
        rebuildFromRoots();
        costAccum -= costAccumRate * lenX * lenY;
        return true;
    }

    boolean placeTile(PartInfo partType, int x, int y){
        if(!canPlace(partType, x, y)) return false;
        placeTileDirect(partType, x, y);
        return true;
    }

    boolean placeTileNoConn(PartInfo partType, int x, int y){
        if(!canPlaceConn(partType, x, y, true)) return false;
        placeTileDirect(partType, x, y);
        return true;
    }

    boolean placeTileDirect(PartInfo partType, int x, int y){
        PartPlaceObj partPlaceObj = new PartPlaceObj(x, y, partType);
        for(var i : partType.connInList){
            PartPlaceObj fromPart = getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
            if(fromPart != null && partCanConnectOut(fromPart, i.x + x, i.y + y, i.id)){
                partPlaceObj.parents.add(fromPart);
                fromPart.children.add(partPlaceObj);
            }
        }
        for(var i : partType.connOutList){
            PartPlaceObj fromPart = getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
            if(fromPart != null && partCanConnectOut(fromPart, i.x + x, i.y + y, i.id)){
                partPlaceObj.children.add(fromPart);
                fromPart.parents.add(partPlaceObj);
            }
        }
        int xLen = partType.tw;
        int yLen = partType.th;
        for(int px = 0; px < xLen; px++){
            for(int py = 0; py < yLen; py++) grid.put(x + px, y + py, partPlaceObj);
        }
        if(partType.isRoot) rootList.add(partPlaceObj);
        partList.add(partPlaceObj);
        rebuildFromRoots();
        costAccum += costAccumRate * xLen * yLen;
        return true;
    }

    void onIsClicked(InputEvent event, float x, float y, int point, KeyCode butt){
        isClickedRN = true;
        Point2 gPos = uiToGridPos(x, y);
        boolean success;
        if(butt == KeyCode.mouseRight) success = removeTile(getPartAt(gPos.x, gPos.y));
        else success = placeTile(partsSelect, gPos.x, gPos.y);
        dragButton = butt;
        if(onTileAction != null && success) onTileAction.run();
    }

    void onIsDragged(InputEvent event, float x, float y, int point){
        if(isClickedRN){
            Point2 gPos = uiToGridPos(x, y);
            boolean success;
            if(dragButton == KeyCode.mouseRight) success = removeTile(getPartAt(gPos.x, gPos.y));
            else success = placeTile(partsSelect, gPos.x, gPos.y);
            if(onTileAction != null && success) onTileAction.run();
        }
    }

    boolean onIsHovering(InputEvent event, float x, float y){
        if(x < 0f || x > width || y < 0f || y > height){
            hover = null;
            return false;
        }
        hover = uiToGridPos(x, y);
        return true;
    }

    void init(){
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                //if(disabed) ?
                onIsClicked(event, x, y, pointer, button);
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y){
                return onIsHovering(event, x, y);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                isClickedRN = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                onIsDragged(event, x, y, pointer);
            }
        });
    }

    OrderedMap<Item, Integer> getTotalCost(){
        OrderedMap<Item, Integer> cst = new OrderedMap<>(partList.size);
        for(var i : partList){
            for(var p : i.part.cost){
                int cur = cst.get(p.item, 0);
                cst.put(p.item, cur + Mathf.floor(p.amount * (costAccum - costAccumRate)));
            }
        }
        return cst;
    }

    PartPlaceObj getPartAt(int x, int y){
        if(!inBoundsRect(x, y, 1, 1)) return null;
        return grid.get(x, y);
    }

    boolean partCanConnectOut(PartPlaceObj part, int x, int y, byte portId){
        for(var i : part.part.connOutList){
            if(i.id == portId && x == part.x + i.x + i.dir.x && y == part.y + i.y + i.dir.y) return true;
        }
        return false;
    }

    boolean partCanConnectIn(PartPlaceObj part, int x, int y, byte portId){
        for(var i : part.part.connInList){
            if(i.id == portId && x == part.x + i.x + i.dir.x && y == part.y + i.y + i.dir.y) return true;
        }
        return false;
    }

    Point2 uiToGridPos(float x, float y){
        int gw = gridW * 32;
        int gh = gridH * 32;
        float gamx = (width - gw) * 0.5f;
        float gamy = (height - gh) * 0.5f;
        return new Point2(Mathf.floor((x - gamx) / 32f), Mathf.floor((y - gamy) / 32f));
    }

    public String getPackedSave(){
        IntPacker packer = new IntPacker();
        for(int px = 0; px < gridW; px++){
            for(int py = 0; py < gridH; py++){
                PartPlaceObj p = getPartAt(px, py);
                if(p != null && p.x == px && p.y == py && p.valid) packer.add(p.part.id + 1);
                else packer.add(0);
            }
        }
        packer.end();
        return packer.toStringPack();
    }

    void loadSave(IntSeq array, PartInfo[] partList){
        for(int i = 0, len = array.size; i < len; i++){
            int temp = array.get(i);
            if(temp == 0) continue;
            int px = i / gridH;
            int py = i % gridH;
            placeTileNoConn(partList[temp - 1], px, py);
        }
    }

    static class PartPlaceObj{
        final PartInfo part;
        final int x, y;
        int flash;
        boolean valid;
        final OrderedSet<PartPlaceObj> parents = new OrderedSet<>(12), children = new OrderedSet<>(12);

        PartPlaceObj(int x, int y, PartInfo part){
            this.x = x;
            this.y = y;
            this.part = part;
        }
    }
}
