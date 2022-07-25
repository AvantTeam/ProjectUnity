package unity.parts;

import arc.math.geom.*;
import arc.struct.*;
import mindustry.type.*;

public class ModularUnitBlueprint extends Blueprint<ModularPart>{
    public Seq<ModularPart> hasCustomDraw = new Seq<>();

    public ModularUnitBlueprint(byte[] data){
        super(data);
    }

    public void decode(byte[] data){
        w = ub(data[0]);
        h = ub(data[1]);
        parts = new ModularPart[w][h];

        int step = PartData.step();
        int partAmount = (data.length - 2) / step;
        this.data = new Data[partAmount];
        for(int i = 0; i < partAmount; i++){
            var partData = PartData.unpack(data, step);
            var part = ModularPartType.getPartFromId(partData.data).create(partData.x, partData.y);
            this.data[i] = partData;
            partsList.put(part, partData);
            if(part.type.open || part.type.hasCellDecal || part.type.hasExtraDecal){
                hasCustomDraw.add(part);
            }
            part.ax = partData.x - w * 0.5f + 0.5f;
            part.ay = partData.y - h * 0.5f + 0.5f;
            part.cx = partData.x - w * 0.5f + part.type.w * 0.5f;
            part.cy = partData.y - h * 0.5f + part.type.h * 0.5f;

            for(int px = 0; px < part.type.w; px++){
                for(int py = 0; py < part.type.h; py++){
                    parts[partData.x + px][partData.y + py] = part;
                }
            }
        }
        for(ModularPart mp : partsList.orderedKeys()){
            mp.type.setupPanellingIndex(mp, parts);
        }

        validate();
        onChange.run();
        itemReqChanged = true;
    }

    protected void setUpRoot(){
        root = null;
        for(ModularPart[] parts1 : parts){
            for(ModularPart part : parts1){
                if(part != null && part.type.root){
                    root = part;
                }
            }
        }
    }

    protected void validate(){
        valid = new boolean[w][h];
        setUpRoot();

        //BFS to check it's valid. Valid parts have connections to root.
        Queue<Point2> queue = new Queue<>();
        var point = new Point2(root.x, root.y);
        queue.addLast(point);
        valid[root.x][root.y] = true;
        var partsList = new OrderedMap<ModularPart, Data>();
        while(!queue.isEmpty()){
            var pt = queue.removeFirst();
            int left = pt.x - 1, back = pt.y - 1, right = pt.x + 1, front = pt.y + 1;
            ModularPart leftPart = parts[left][pt.y], backPart = parts[pt.x][back], frontPart = parts[pt.x][front], rightPart = parts[right][pt.y];

            if(pt.x > 0 && leftPart != null && !valid[left][pt.y]){
                valid[left][pt.y] = true;
                point = new Point2(left, pt.y);
                queue.addLast(point);
                partsList.put(leftPart, this.partsList.get(leftPart));
            }
            if(pt.y > 0 && backPart != null && !valid[pt.x][back]){
                valid[pt.x][back] = true;
                point = new Point2(pt.x, back);
                queue.addLast(point);
                partsList.put(backPart, this.partsList.get(backPart));
            }
            if(pt.x < w - 1 && rightPart != null && !valid[right][pt.y]){
                valid[right][pt.y] = true;
                point = new Point2(right, pt.y);
                queue.addLast(point);
                partsList.put(rightPart, this.partsList.get(rightPart));
            }
            if(pt.y < h - 1 && frontPart != null && !valid[pt.x][front]){
                valid[pt.x][front] = true;
                point = new Point2(pt.x, front);
                queue.addLast(point);
                partsList.put(frontPart, this.partsList.get(frontPart));
            }
        }
        this.partsList = partsList;
    }

    @Override
    public ItemSeq itemRequirements(){
        if(itemReqChanged){
            itemRequirements = new ItemSeq();
            for(var mp : partsList.orderedKeys()){
                itemRequirements.add(mp.type.cost);
            }
            itemReqChanged = false;
        }
        return itemRequirements;
    }

    public byte[] encode(){
        int step = PartData.step();
        byte[] output = new byte[2 + data.length * step];
        output[0] = sb((byte)w);
        output[1] = sb((byte)h);

        for(int i = 0; i < data.length; i++){
            data[i].pack(output, 2 + step * i);
        }
        return output;
    }

    //trims empty tiles.
    public byte[] encodeCropped(){
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

        int step = PartData.step();
        byte[] output = new byte[2 + partsList.size * step];
        output[0] = sb((byte)(maxX - minX + 1));
        output[1] = sb((byte)(maxY - minY + 1));
        var partSeq = partsList.orderedKeys();
        for(int i = 0; i < partSeq.size; i++){
            var part = partSeq.get(i);
            PartData data = partsList.get(part).self().copy();
            data.x -= minX;
            data.y -= minY;
            data.pack(output, 2 + i * step);
        }
        return output;
    }

    public static class PartData extends Data{
        byte x, y, data;

        public PartData(byte x, byte y, byte data){
            this.x = x;
            this.y = y;
            this.data = data;
        }

        public static int step(){
            return 3;
        }

        public static PartData unpack(byte[] data, int offset){
            byte x = ub(data[offset]);
            byte y = (byte)(ub(data[offset + 1]) * (256 << 1 * 8));
            byte d = (byte)(ub(data[offset + 2]) * (256 << 2 * 8)); //ignore warning, its for if there is somehow more then 255 parts
            return new PartData(x, y, d);
        }

        @Override
        public void pack(byte[] data, int offset){
            data[offset] = sb(x);
            data[offset + 1] = sb(y);
            data[offset + 2] = sb(this.data);//ignore warning, its for if there is somehow more then 255 parts
        }

        @Override
        public PartData copy(){
            return new PartData(x, y, data);
        }
    }
}
