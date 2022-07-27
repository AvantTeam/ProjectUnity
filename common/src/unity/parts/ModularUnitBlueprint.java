package unity.parts;

import arc.math.geom.*;
import arc.struct.Queue;
import arc.struct.*;
import mindustry.type.*;

import java.util.*;

public class ModularUnitBlueprint extends Blueprint<ModularPartType, ModularPart>{
    public ModularUnitBlueprint(int w, int h){
        super(w, h);
        parts = new ModularPart[w][h];
        partsList = new OrderedSet<>();
        valid = new boolean[w][h];
        data = new OrderedSet<>();
    }

    public ModularUnitBlueprint(byte[] data){
        super(ub(data[0]), ub(data[1]));
        decode(data);
    }

    @Override
    public void clear(){
        parts = new ModularPart[w][h];
        partsList = new OrderedSet<>();
        valid = new boolean[w][h];
        data = new OrderedSet<>();
        super.clear();
    }

    @Override
    public void decode(byte[] data){
        clear();
        int ox = (w - ub(data[0])) / 2;
        int oy = (h - ub(data[1])) / 2;
        int step = PartData.step();
        int partAmount = (data.length - 2) / step;

        for(int i = 0; i < partAmount; i++){
            var partData = new PartData(data, 2 + i * step);
            var partType = ModularPartType.getPartFromId(partData.id);
            if(canPlace(partType, partData.x + ox, partData.y + oy)) place(partType, partData.x + ox, partData.y + oy);
        }

        onChange();
    }

    @Override
    protected void rebuildValid(){
        valid = new boolean[w][h];
        partsList = new OrderedSet<>();
        if(root == null) return;

        //BFS to check it's valid. Valid parts have connections to root.
        Queue<Point2> queue = new Queue<>();
        var point = new Point2(root.x, root.y);
        queue.addLast(point);

        valid[root.x][root.y] = true;
        partsList.add(parts[root.x][root.y]);
        while(!queue.isEmpty()){
            var pt = queue.removeFirst();
            int left = pt.x - 1, back = pt.y - 1, right = pt.x + 1, front = pt.y + 1;

            if(left >= 0){
                var leftPart = parts[left][pt.y];
                if(leftPart != null && !valid[left][pt.y]){
                    valid[left][pt.y] = true;
                    point = new Point2(left, pt.y);
                    queue.addLast(point);
                    partsList.add(leftPart);
                }
            }
            if(back >= 0){
                var backPart = parts[pt.x][back];
                if(backPart != null && !valid[pt.x][back]){
                    valid[pt.x][back] = true;
                    point = new Point2(pt.x, back);
                    queue.addLast(point);
                    partsList.add(backPart);
                }
            }
            if(right < w){
                var rightPart = parts[right][pt.y];
                if(rightPart != null && !valid[right][pt.y]){
                    valid[right][pt.y] = true;
                    point = new Point2(right, pt.y);
                    queue.addLast(point);
                    partsList.add(rightPart);
                }
            }
            if(front < h){
                var frontPart = parts[pt.x][front];
                if(frontPart != null && !valid[pt.x][front]){
                    valid[pt.x][front] = true;
                    point = new Point2(pt.x, front);
                    queue.addLast(point);
                    partsList.add(frontPart);
                }
            }
        }
    }

    @Override
    public boolean canPlace(ModularPartType type, int x, int y){
        //Checks whether part is in bound by testing left bottom corner and right top corner
        if(!canPlace(x, y) || !canPlace(type.w - 1 + x, type.h - 1 + y)) return false;
        //Check all required space is empty.
        for(int i = x; i < x + type.w; i++){
            for(int j = y; j < y + type.h; j++){
                if(parts[i][j] != null) return false;
            }
        }
        //makes root unique.
        return !type.root || root == null;
    }

    @Override
    public boolean tryPlace(ModularPartType type, int x, int y){
        if(!canPlace(type, x, y)) return false;
        place(type, x, y);

        onChange();
        return true;
    }

    @Override
    protected void place(ModularPartType type, int x, int y){
        arc.util.Log.infoList("place", x, y);
        var part = type.create(x, y);
        if(type.root) root = part;
        for(int i = x; i < x + type.w; i++){
            for(int j = y; j < y + type.h; j++) parts[i][j] = part;
        }

        data.add(new PartData(type.id, x, y));
        part.ax = x - w * 0.5f + 0.5f;
        part.ay = y - h * 0.5f + 0.5f;
        part.cx = x - w * 0.5f + type.w * 0.5f;
        part.cy = y - h * 0.5f + type.h * 0.5f;
        part.setupPanellingIndex(parts);
    }

    @Override
    public void displace(int x, int y){
        var part = parts[x][y];
        if(part != null){
            data.remove(new PartData(part.type.id, part.x, part.y));
            if(part == root) root = null;
            for(int i = part.x; i < part.x + part.type.w; i++){
                for(int j = part.y; j < part.y + part.type.h; j++) parts[i][j] = null;
            }
        }
        onChange();
    }

    @Override
    public ItemSeq itemRequirements(){
        if(itemReqChanged){
            itemRequirements = new ItemSeq();
            for(var mp : partsList.orderedItems()) itemRequirements.add(mp.type.cost);
            itemReqChanged = false;
        }
        return itemRequirements;
    }

    @Override
    protected ModularUnitBlueprint validate(){
        return new ModularUnitBlueprint(encodeCropped());
    }

    @Override
    public ModularUnitConstruct construct(){
        var blueprint = validate();
        return new ModularUnitConstruct(blueprint.parts, blueprint.partsList.orderedItems());
    }

    @Override
    public byte[] encode(){
        int step = PartData.step();
        byte[] output = new byte[2 + data.size * step];
        output[0] = sb((byte)w);
        output[1] = sb((byte)h);

        var dataSeq = data.orderedItems();
        for(int i = 0; i < dataSeq.size; i++) dataSeq.get(i).pack(output, 2 + step * i);
        return output;
    }

    @Override
    public byte[] encodeCropped(){
        int maxX = 0, minX = 256;
        int maxY = 0, minY = 256;

        for(var validPart : partsList){
            maxX = Math.max(validPart.x + validPart.type.w - 1, maxX);
            minX = Math.min(validPart.x, minX);
            maxY = Math.max(validPart.y + validPart.type.h - 1, maxY);
            minY = Math.min(validPart.y, minY);
        }
        if(root != null){
            int rootRight = maxX - root.x - root.type.w + 1, rootLeft = root.x - minX;
            if(rootLeft > rootRight) maxX = Math.min(256, maxX + rootLeft - rootRight);
            else if(rootLeft < rootRight) minX = Math.max(0, minX - rootRight + rootLeft);
        }

        int step = PartData.step();
        byte[] output = new byte[2 + partsList.size * step];
        output[0] = sb((byte)(maxX - minX + 1));
        output[1] = sb((byte)(maxY - minY + 1));

        var parts = partsList.orderedItems();
        for(int i = 0; i < parts.size; i++){
            var part = parts.get(i);
            PartData data = new PartData(part.type.id, part.x - minX, part.y - minY);
            data.pack(output, 2 + i * step);
        }
        return output;
    }

    //As parts package only uses this, it's default.
    static class PartData extends Data{
        int id, x, y;

        public PartData(int id, int x, int y){
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public PartData(byte[] data, int offset){
            id = ub(data[offset]); //ignore warning, it's for if there is somehow more than 255 parts
            x = ub(data[offset + 1]);
            y = ub(data[offset + 2]);
        }

        public static int step(){
            return 3;
        }

        @Override
        public void pack(byte[] data, int offset){
            data[offset] = sb(id);//ignore warning, it's for if there is somehow more than 255 parts
            data[offset + 1] = sb(x);
            data[offset + 2] = sb(y);
        }

        @Override
        public boolean equals(Object obj){
            if(this == obj) return true;
            if(obj instanceof PartData data) return id == data.id && x == data.x && y == data.y;
            return false;
        }

        //Thanks intellij wizard.
        @Override
        public int hashCode(){
            return Objects.hash(id, x, y);
        }
    }
}
