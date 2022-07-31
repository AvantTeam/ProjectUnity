package unity.parts;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import unity.parts.ModularUnitBlueprint.*;
import unity.parts.PanelDoodadType.*;

import static unity.parts.Blueprint.sb;

//Assumes Immutable.
public class ModularUnitConstruct extends Blueprint.Construct<ModularUnitPart>{
    public ModularUnitConstruct(ModularUnitPart[][] parts, Seq<ModularUnitPart> partsList){
        super(parts, partsList);
        for(var part : partsList){
            part.setupPanellingIndex(parts);
            if(part.type.hasCustomDraw) hasCustomDraw.add(part);
        }
        if(!Vars.headless) initDoodads();
    }

    public boolean isEmpty(){
        return partsList.isEmpty();
    }

    @Override
    public byte[] toData(){
        var step = PartData.step();
        var data = new byte[2 + partsList.size * step];
        data[0] = sb((byte)(parts.length));
        data[1] = sb((byte)(parts[0].length));
        for(int i = 0; i < partsList.size; i++){
            var part = partsList.get(i);
            new PartData(part.type.id, part.x, part.y).pack(data, 2 + step * i);
        }
        return data;
    }

    //Initialize draw config of ModularUnit.
    @Override
    public void initDoodads(){
        Rand rand = new Rand();
        rand.setSeed(parts.length);
        /// :I welp i tried
        if(parts.length == 0){
            return;
        }
        boolean[][] filled = new boolean[parts.length][parts[0].length];

        int w = parts.length;
        int h = parts[0].length;
        int minY = 256, maxY = 0;
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                filled[i][j] = parts[i][j] != null && !parts[i][j].type.open;
                if(filled[i][j]){
                    minY = Math.min(j, minY);
                    maxY = Math.max(j, maxY);
                }
            }
        }

        float[][] lightness = new float[w][h];
        int tiles = 0;
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                lightness[i][j] = Mathf.clamp(Mathf.map(j, minY, maxY, 0, 2), 0, 1);
                tiles += filled[i][j] ? 1 : 0;
            }
        }
        if(tiles == 0){
            return;
        }
        Seq<Point2> seeds = new Seq<>();
        int[][] seedSpace = new int[w][h];
        for(int i = 0; i < Math.max(Mathf.floor(Mathf.sqrt(tiles) / 2), 1); i++){
            int cnx = rand.random(0, Mathf.floor(w) - 1);
            int cny = rand.random(0, h - 1);
            while(!filled[cnx][cny]){
                cnx = rand.random(0, Mathf.floor(w) - 1);
                cny = rand.random(0, h - 1);
            }
            seeds.add(new Point2(cnx, cny));
            seedSpace[cnx][cny] = seeds.size;
            if(filled[w - cnx - 1][cny]){
                seedSpace[w - cnx - 1][cny] = seeds.size;
            }
        }

        int[][] seedSpaceBuf = new int[w][h];
        boolean hasEmpty = true;
        while(hasEmpty){
            hasEmpty = false;
            for(int i = 0; i < w; i++){
                for(int j = 0; j < h; j++){
                    if(seedSpace[i][j] != 0){
                        int seed = seedSpace[i][j];
                        seedSpaceBuf[i][j] = seed;
                        if(i > 0 && seedSpace[i - 1][j] == 0 && seedSpaceBuf[i - 1][j] < seed){
                            seedSpaceBuf[i - 1][j] = seed;
                        }
                        if(i < w - 1 && seedSpace[i + 1][j] == 0 && seedSpaceBuf[i + 1][j] < seed){
                            seedSpaceBuf[i + 1][j] = seed;
                        }
                        if(j > 0 && seedSpace[i][j - 1] == 0 && seedSpaceBuf[i][j - 1] < seed){
                            seedSpaceBuf[i][j - 1] = seed;
                        }
                        if(j < h - 1 && seedSpace[i][j + 1] == 0 && seedSpaceBuf[i][j + 1] < seed){
                            seedSpaceBuf[i][j + 1] = seed;
                        }
                    }else{
                        hasEmpty = true;
                    }
                }
            }
            seedSpace = seedSpaceBuf;
        }
        for(int i = 0; i < Math.round(w / 2f) - 1; i++){
            for(int j = 0; j < h; j++){
                float val = Mathf.map((i * 34.343f + j * 844.638f) % 1f, -0.1f, 0.1f);
                lightness[i][j] += val;
                lightness[w - i - 1][j] += val;
            }
        }
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){

                if(j > 0 && seedSpace[i][j] != seedSpace[i][j - 1]){
                    lightness[i][j] -= 0.5;
                }
                if(i == Math.round(w / 2f) - 1){
                    continue;
                }
                lightness[i][j] = Mathf.clamp(lightness[i][j], 0, 1);
            }
        }

        ///finally apply doodads
        boolean[][] placed = new boolean[parts.length][parts[0].length];
        float ox = -w * 0.5f;
        float oy = -h * 0.5f;
        int middleX = Math.round(w / 2f) - 1;
        Seq<PanelDoodadType> draw = new Seq<>();
        PanelDoodadType mirrored;
        for(int i = 0; i < Math.round(w / 2f); i++){
            for(int j = 0; j < h; j++){
                mirrored = null;

                if(filled[i][j] && !placed[i][j]){
                    draw.clear();
                    for(var pal : ModularUnitPartType.unitDoodads){
                        if(pal.w == 1 && pal.h == 1){
                            draw.add(pal.get(1 - lightness[i][j]));
                        }else{
                            var type = pal.get(1 - lightness[i][j]);
                            boolean allowed = (pal.w % 2 == 0 || pal.sides) && i + pal.w - 1 < middleX || pal.center && i == middleX - (pal.w / 2);
                            if(allowed && type.canFit(parts, i, j)){
                                draw.add(type);
                            }
                        }
                    }
                    PanelDoodadType doodad = draw.random(rand);
                    mirrored = doodad;

                    addDoodad(placed, get(doodad, i + ox, j + oy), i, j);
                }
                if(filled[w - i - 1][j] && !placed[w - i - 1][j]){
                    if(mirrored != null){
                        addDoodad(placed, get(mirrored, w - i - mirrored.w + ox, j + oy), w - i - mirrored.w, j);
                        continue;
                    }
                    draw.clear();
                    for(var pal : ModularUnitPartType.unitDoodads){
                        if(pal.w == 1 && pal.h == 1){
                            draw.add(pal.get(1 - lightness[w - i - 1][j]));
                        }else{
                            var type = pal.get(1 - lightness[w - i - 1][j]);
                            boolean allowed = (pal.w % 2 == 0 || pal.sides) && w - i - 1 > middleX;
                            if(allowed && type.canFit(parts, i, j)){
                                draw.add(type);
                            }
                        }
                    }
                    PanelDoodadType doodad = draw.random(rand);
                    addDoodad(placed, get(doodad, w - i - doodad.w + ox, j + oy), w - i - doodad.w, j);
                }
            }

        }
    }

    void addDoodad(boolean[][] placed, PanelDoodad p, int x, int y){
        doodads.add(p);
        for(int i = 0; i < p.type.w; i++){
            for(int j = 0; j < p.type.h; j++){
                placed[x + i][y + j] = true;
            }
        }
    }

    PanelDoodad get(PanelDoodadType type, float x, float y){
        return type.create((type.w * 0.5f + x) * PartType.partSize, (type.h * 0.5f + y) * PartType.partSize, x > 0);
    }
}
