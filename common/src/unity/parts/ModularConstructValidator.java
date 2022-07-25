package unity.parts;

import arc.math.geom.*;
import arc.struct.*;
import mindustry.type.*;

public class ModularConstructValidator{
    public ModularPart[][] parts;
    public boolean[][] valid;
    public int w, h;
    public ModularPart root = null;

    private Runnable onChange = () -> {};

    boolean updatedItemReq = false;
    public ItemSeq itemRequirements;

    public ModularConstructValidator(int w, int h){
        this.w = w;
        this.h = h;
        parts = new ModularPart[w][h];
        valid = new boolean[w][h];
    }

    public ModularConstructValidator(byte[] data){
        set(data);
    }

    public void setOnChange(Runnable onChange){
        this.onChange = onChange;
    }

    public void clear(){
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                parts[i][j] = null;
                valid[i][j] = false;
            }
        }
        root = null;
        onChange.run();
        updatedItemReq = false;
    }

    //converts data to ModularPart with ModularConstruct.
    public void set(byte[] data){
        //Temporary uses ModularConstructs decoder of byte data.
        ModularConstruct design = new ModularConstruct(data);
        //If data is judged to invalid, reset and skip.
        if(design.parts == null){
            return;
        }
        parts = design.parts;
        w = parts.length;
        h = parts[0].length;
        valid = new boolean[w][h];
        root = null;
        findRoot();
        onChange.run();
        //Build whether each parts valid.
        rebuildValid();
        updatedItemReq = false;
    }

    public void paste(ModularConstructValidator e){
        int ox = (w - e.w) / 2;
        int oy = (h - e.h) / 2;
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(!e.canHave(i - ox, j - oy)){
                    continue;
                }
                if(e.parts[i - ox][j - oy] == null){
                    continue;
                }
                var part = e.parts[i - ox][j - oy];
                if(canHave(part, ox, oy) && part.isHere(i - ox, j - oy)){
                    placePartDirect(part.type, i, j);
                }
            }
        }
        findRoot();
        rebuildValid();
    }

    private void findRoot(){
        for(int i = 0; i < parts.length; i++){
            for(int j = 0; j < parts[0].length; j++){
                if(parts[i][j] != null && parts[i][j].type.root){
                    root = parts[i][j];
                }
            }
        }
    }

    public boolean canHave(int x, int y){
        return x >= 0 && y >= 0 && x < w && y < h;
    }

    public boolean canHave(ModularPart p, int ox, int oy){
        return canHave(p.x + ox, p.y + oy) && canHave(p.x + p.type.w - 1 + ox, p.y + p.type.h - 1 + oy);
    }

    public boolean canHave(ModularPartType p, int ox, int oy){
        return canHave(ox, oy) && canHave(p.w - 1 + ox, p.h - 1 + oy);
    }

    public Seq<ModularPart> getList(){
        OrderedSet<ModularPart> partsList = new OrderedSet<>();
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(parts[i][j] != null && !partsList.contains(parts[i][j])){
                    partsList.add(parts[i][j]);
                }
            }
        }
        return partsList.orderedItems();
    }

    public void rebuildValid(){
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                valid[i][j] = false;
            }
        }
        if(root == null){
            return;
        }

        //BFS to check it's valid. Valid parts have connections to root.
//        OrderedSet<Point2> points = new OrderedSet<>();
        Queue<Point2> queue = new Queue<>();
        var point = new Point2(root.x, root.y);
//        points.add(point);
        queue.addLast(point);
        valid[root.x][root.y] = true;
        while(!queue.isEmpty()){
            var pt = queue.removeFirst();

            if(pt.x > 0 && parts[pt.x - 1][pt.y] != null && !valid[pt.x - 1][pt.y]){
                valid[pt.x - 1][pt.y] = true;
                point = new Point2(pt.x - 1, pt.y);
//                points.add(point);
                queue.addLast(point);
            }
            if(pt.y > 0 && parts[pt.x][pt.y - 1] != null && !valid[pt.x][pt.y - 1]){
                valid[pt.x][pt.y - 1] = true;
                point = new Point2(pt.x, pt.y - 1);
//                points.add(point);
                queue.addLast(point);
            }
            if(pt.x < w - 1 && parts[pt.x + 1][pt.y] != null && !valid[pt.x + 1][pt.y]){
                valid[pt.x + 1][pt.y] = true;
                point = new Point2(pt.x + 1, pt.y);
//                points.add(point);
                queue.addLast(point);
            }
            if(pt.y < h - 1 && parts[pt.x][pt.y + 1] != null && !valid[pt.x][pt.y + 1]){
                valid[pt.x][pt.y + 1] = true;
                point = new Point2(pt.x, pt.y + 1);
//                points.add(point);
                queue.addLast(point);
            }
        }
    }

    public ItemSeq itemRequirements(){
        if(!updatedItemReq){
            itemRequirements = new ItemSeq();
            var list = getList();
            for(ModularPart mp : list){
                itemRequirements.add(mp.type.cost);
            }
            updatedItemReq = true;
        }
        return itemRequirements;
    }

    public byte[] export(){
        var partList = getList();
        arc.util.Log.infoList(partList.map(p -> p.type.name));
        byte[] output = new byte[2 + partList.size * (ModularConstruct.idSize + 2)];
        output[0] = ModularConstruct.sb(w);
        output[1] = ModularConstruct.sb(h);
        int blockSize = (ModularConstruct.idSize + 2);
        for(int i = 0; i < partList.size; i++){
            var part = partList.get(i);
            ModularConstruct.writeID(output, 2 + blockSize * i, part.type.id);
            output[2 + blockSize * i + ModularConstruct.idSize] = ModularConstruct.sb(part.x);
            output[2 + blockSize * i + ModularConstruct.idSize + 1] = ModularConstruct.sb(part.y);
        }
        return output;
    }

    //trims empty tiles.
    public byte[] exportCropped(){
        OrderedSet<ModularPart> partsList = new OrderedSet<>();
        int maxX = 0, minX = 256;
        int maxY = 0, minY = 256;

        for(int j = 0; j < h; j++){
            for(int i = 0; i < w; i++){
                if(valid[i][j] && parts[i][j] != null){
                    maxX = Math.max(i, maxX);
                    minX = Math.min(i, minX);
                    maxY = Math.max(j, maxY);
                    minY = Math.min(j, minY);
                }
            }
        }
        for(int i = minX; i <= maxX; i++){
            for(int j = minY; j <= maxY; j++){
                if(valid[i][j] && parts[i][j] != null && !partsList.contains(parts[i][j])){
                    partsList.add(parts[i][j]);
                }
            }
        }
        byte[] output = new byte[2 + partsList.size * (ModularConstruct.idSize + 2)];
        output[0] = ModularConstruct.sb(maxX - minX + 1);
        output[1] = ModularConstruct.sb(maxY - minY + 1);
        var partSeq = partsList.orderedItems();
        int blockSize = (ModularConstruct.idSize + 2);
        for(int i = 0; i < partSeq.size; i++){
            var part = partSeq.get(i);
            ModularConstruct.writeID(output, 2 + blockSize * i, part.type.id);
            output[2 + blockSize * i + ModularConstruct.idSize] = ModularConstruct.sb(part.x - minX);
            output[2 + blockSize * i + ModularConstruct.idSize + 1] = ModularConstruct.sb(part.y - minY);
        }
        return output;
    }

    public boolean canPlace(ModularPartType selected, int x, int y){
        if(!canHave(selected, x, y)){
            return false;
        }
        for(int i = x; i < x + selected.w; i++){
            for(int j = y; j < y + selected.h; j++){
                if(parts[i][j] != null){
                    return false;
                }
            }
        }
        if(selected.root && root != null){
            return false;
        }
        return true;
    }

    public void placePartDirect(ModularPartType selected, int x, int y){
        if(!canPlace(selected, x, y)){
            return;
        }
        var part = selected.create(x, y);
        for(int i = x; i < x + selected.w; i++){
            for(int j = y; j < y + selected.h; j++){
                parts[i][j] = part;
            }
        }
        updatedItemReq = false;
    }

    public boolean placePart(ModularPartType selected, int x, int y){
        if(!canPlace(selected, x, y)){
            return false;
        }
        var part = selected.create(x, y);
        for(int i = x; i < x + selected.w; i++){
            for(int j = y; j < y + selected.h; j++){
                parts[i][j] = part;
            }
        }
        if(selected.root){
            root = part;
        }
        onChange.run();
        rebuildValid();
        updatedItemReq = false;
        return true;
    }

    public void deletePartAt(int x, int y){
        if(parts[x][y] != null){
            if(parts[x][y] == root){
                root = null;
            }
            var part = parts[x][y];
            for(int i = part.x; i < part.x + part.type.w; i++){
                for(int j = part.y; j < part.y + part.type.h; j++){
                    parts[i][j] = null;
                }
            }
        }
        onChange.run();
        rebuildValid();
        updatedItemReq = false;
    }
}
