package unity.type;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.gen.*;

public class CubeUnitType extends UnityUnitType{
    private final static Point2[][] edges, adjacent;
    private final static int maxTierSize = 3;
    private final static IntSeq tmp = new IntSeq();

    public TextureRegion[] regions, outlineRegions, cellRegions;
    public Seq<Seq<Weapon>> weaponsAll = new Seq<>();
    public int tier, maxEntities = 15;
    public float gridSpacing = 16f;

    public CubeUnitType(String name, int tier){
        super(name);
        this.tier = tier;
    }

    static{
        edges = new Point2[maxTierSize][0];
        adjacent = new Point2[maxTierSize][8];
        for(int i = 0; i < edges.length; i++){
            tmp.clear();
            int size = (i * i) + 1;
            for(int x = 0; x < size; x++){
                tmp.add(x, -1);
            }
            for(int y = 0; y < size; y++){
                tmp.add(size, y);
            }
            for(int x = 0; x < size; x++){
                tmp.add(x, size);
            }
            for(int y = 0; y < size; y++){
                tmp.add(-1, y);
            }
            edges[i] = new Point2[tmp.size / 2];
            int f = 0;
            for(int e = 0; e < tmp.size; e += 2){
                edges[i][f] = new Point2(tmp.items[e], tmp.items[e + 1]);
                f++;
            }
        }
    }

    @Override
    public void init(){
        super.init();
        if(!weapons.isEmpty() && weaponsAll.isEmpty()){
            weaponsAll.add(weapons);
        }
    }

    @Override
    public void load(){
        super.load();
        regions = new TextureRegion[tier];
        cellRegions = new TextureRegion[tier];
        outlineRegions = new TextureRegion[tier];

        for(int i = 0; i < tier; i++){
            if(i != 0){
                outlineRegions[i] = Core.atlas.find(name + "-outline-" + i);
                regions[i] = Core.atlas.find(name + "-" + i);
                cellRegions[i] = Core.atlas.find(name + "-cell-" + i);
            }else{
                outlineRegions[i] = outlineRegion;
                regions[i] = region;
                cellRegions[i] = cellRegion;
            }
        }
    }

    @Override
    public void drawCell(Unit unit){
        TextureRegion r = cellRegion;
        Cubec cube = unit instanceof Cubec ? (Cubec)unit : null;
        if(cube != null) cellRegion = cellRegions[cube.tier()];
        super.drawCell(unit);
        cellRegion = r;
    }

    @Override
    public void drawOutline(Unit unit){
        TextureRegion r = outlineRegion;
        Cubec cube = unit instanceof Cubec ? (Cubec)unit : null;
        if(cube != null) outlineRegion = outlineRegions[cube.tier()];
        super.drawOutline(unit);
        outlineRegion = r;
    }

    @Override
    public void drawBody(Unit unit){
        TextureRegion r = region;
        Cubec cube = unit instanceof Cubec ? (Cubec)unit : null;
        if(cube != null){
            if(cube.isMain() && cube.data() != null){
                for(Cubec c : cube.data().all){
                    if(!c.isAdded()){
                        Draw.draw(Draw.z(), () ->
                        Drawf.construct(c.x(), c.y(), outlineRegions[c.tier()], unit.team.color, c.rotation() - 90f, c.constructTime() / regenTime, 1f, c.constructTime()));
                    }
                }
            }
            region = regions[cube.tier()];
        }
        super.drawBody(unit);
        region = r;
    }

    public static class CubeEntityData{
        public int width, height, entities;
        public Seq<Cubec> all = new Seq<>();
        public Cubec[] grid;
        public Cubec main;
        public CubeUnitType type;

        public CubeEntityData(Cubec main){
            grid = new Cubec[3 * 3];
            width = height = 3;
            this.main = main;
            type = (CubeUnitType)main.type();
            add(main, 1, 1);
        }

        public void resize(int size){
            if(size <= 0) return;
            int nw = width + (size * 2), nh = height + (size * 2);
            Cubec[] n = new Cubec[nw * nh];

            for(int cx = 0; cx < nw; cx++){
                for(int cy = 0; cy < nh; cy++){
                    int ox = cx - size, oy = cy - size;
                    if(ox <= 0 && ox > width && oy <= 0 && oy > height){
                        Cubec c = grid[ox + (oy * width)];
                        if(c != null){
                            c.gx(cx);
                            c.gy(cy);
                            n[cx + (cy * nw)] = c;
                        }
                    }
                }
            }

            width = nw;
            height = nh;
            grid = n;
        }

        public void shift(int x, int y){
            if(x + y == 0) return;
            //tmpSet.clear();
            Cubec[] n = new Cubec[grid.length];
            for(int cx = 0; cx < width; cx++){
                for(int cy = 0; cy < height; cy++){
                    int vx = cx + x, vy = cy + y;
                    if(inbounds(vx, vy)){
                        n[vx + (vy * width)] = grid[cx + (cy * width)];
                    }
                }
            }
            for(Cubec c : all){
                c.gx(c.gx() + x);
                c.gy(c.gy() + y);
            }
            grid = n;
        }

        public boolean available(int x, int y){
            if(x >= 0 && y >= 0 && x < width && y < height){
                return grid[x * (y * width)] == null;
            }
            return false;
        }

        public boolean available(int size, int x, int y){
            boolean a = true;
            for(int cx = 0; cx < size; cx++){
                for(int cy = 0; cy < size; cy++){
                    a &= available(x + cx, y + cy);
                }
            }
            return a;
        }

        public boolean inbounds(int x, int y){
            return x >= 0 && y >= 0 && x < width && y < height;
        }

        public void add(Cubec unit, int x, int y){
            if(available(unit.tier() + 1, x, y)){
                for(int cx = 0; cx < unit.tier() + 1; cx++){
                    for(int cy = 0; cy < unit.tier() + 1; cy++){
                        grid[(x + cx) + ((y + cy) * width)] = unit;
                    }
                }
                unit.gx(x);
                unit.gy(y);
                all.add(unit);
                entities++;
            }
        }

        public void updateAdjacent(int tier, int x, int y){

        }

        public void remove(Cubec unit){
            int x = unit.gx(), y = unit.gy();
            if(available(unit.tier() + 1, x, y)){
                for(int cx = 0; cx < unit.tier() + 1; cx++){
                    for(int cy = 0; cy < unit.tier() + 1; cy++){
                        grid[(x + cx) + ((y + cy) * width)] = null;
                    }
                }
                all.remove(unit);
                entities -= unit.tier() + 1;
            }
        }

        public void addEdge(Cubec origin){
            if(entities >= type.maxEntities) return;
            tmp.clear();
            for(Point2 p : edges[origin.tier()]){
                int x = p.x + origin.gx(), y = p.y + origin.gy();
                if(available(x, y)){
                    tmp.add(x, y);
                }
            }
            int idx = Mathf.random(tmp.size / 2);

            int x = tmp.get(idx * 2), y = tmp.get((idx * 2) + 1);
            Cubec c = (Cubec)origin.type().constructor.get();
            c.health(origin.health());
            c.team(origin.team());
            c.setType(origin.type());
            c.ammo(origin.type().ammoCapacity);
            c.elevation(origin.type().flying ? 1f : 0f);
            c.data(this);
            add(c, x, y);
        }
    }
}
