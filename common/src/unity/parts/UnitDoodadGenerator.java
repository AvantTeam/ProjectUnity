package unity.parts;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import unity.parts.Blueprint.*;
import unity.parts.PanelDoodadType.*;

public class UnitDoodadGenerator{
    public static final Seq<PanelDoodadPalette> unitDoodads = new Seq();

    //Initialize draw config of ModularUnit.
    public static void initDoodads(int rngSeed, Seq<PanelDoodad> doodads, Construct<ModularPart> construct){
        Rand rand = new Rand();
        rand.setSeed(rngSeed);
        /// :I welp i tried
        if(construct != null){
            if(construct.parts.length == 0){
                return;
            }
            boolean[][] filled = new boolean[construct.parts.length][construct.parts[0].length];


            int w = construct.parts.length;
            int h = construct.parts[0].length;
            int minY = 999, maxY = 0;
            for(int i = 0; i < w; i++){
                for(int j = 0; j < h; j++){
                    filled[i][j] = construct.parts[i][j] != null && !construct.parts[i][j].type.open;
                    if(filled[i][j]){
                        minY = Math.min(j, minY);
                        maxY = Math.max(j, maxY);
                    }
                }
            }

            //Shading of each part tile. It gets brighter near to front and the center.
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

            Seq<Point2> seeds = new Seq();
            int[][] seedSpace = new int[w][h];
            for(int i = 0; i < Math.max(Mathf.floor(Mathf.sqrt(tiles) / 2), 1); i++){
                int cnx, cny;
                do{
                    cnx = rand.random(0, Mathf.floor(w) - 1);
                    cny = rand.random(0, h - 1);
                }while(!filled[cnx][cny]);
                seeds.add(new Point2(cnx, cny));
                seedSpace[cnx][cny] = seeds.size;
                if(filled[w - cnx - 1][cny]){
                    seedSpace[w - cnx - 1][cny] = seeds.size;
                }
            }

            boolean hasEmpty = true;
            int[][] seedSpaceBuf = new int[w][h];
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
                for(int i = 0; i < w; i++){
                    for(int j = 0; j < h; j++){
                        seedSpace[i][j] = seedSpaceBuf[i][j];
                    }
                }
            }
            for(int i = 0; i < Math.round(w / 2f) - 1; i++){
                for(int j = 0; j < h; j++){
                    float val = Mathf.map(Mathf.floor(i * 34.343f + j * 844.638f), -0.1f, 0.1f);
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
            //shading end.

            ///finally apply ModularUnit drawing palette.
            boolean[][] placed = new boolean[construct.parts.length][construct.parts[0].length];
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
                        for(var pal : unitDoodads){
                            if(pal.w == 1 && pal.h == 1){
                                draw.add(pal.get(1 - lightness[i][j]));
                            }else{
                                var type = pal.get(1 - lightness[i][j]);
                                boolean allowed = false;
                                if((pal.w % 2 == 0 || pal.sides) && i + pal.w - 1 < middleX){
                                    allowed = true;
                                }
                                if(pal.center && i == middleX - (pal.w / 2)){
                                    allowed = true;
                                }
                                if(allowed && type.canFit(construct.parts, i, j)){
                                    draw.add(type);
                                }
                            }
                        }
                        PanelDoodadType doodad = draw.random(rand);
                        mirrored = doodad;

                        addDoodad(doodads, placed, get(doodad, i + ox, j + oy), i, j);
                    }
                    if(filled[w - i - 1][j] && !placed[w - i - 1][j]){
                        if(mirrored != null){
                            addDoodad(doodads, placed, get(mirrored, w - i - mirrored.w + ox, j + oy), w - i - mirrored.w, j);
                            continue;
                        }
                        draw.clear();
                        for(var pal : unitDoodads){
                            if(pal.w == 1 && pal.h == 1){
                                draw.add(pal.get(1 - lightness[w - i - 1][j]));
                            }else{
                                var type = pal.get(1 - lightness[w - i - 1][j]);
                                boolean allowed = false;
                                if((pal.w % 2 == 0 || pal.sides) && w - i - 1 > middleX){
                                    allowed = true;
                                }
                                if(allowed && type.canFit(construct.parts, i, j)){
                                    draw.add(type);
                                }
                            }
                        }
                        PanelDoodadType doodad = draw.random(rand);
                        addDoodad(doodads, placed, get(doodad, w - i - doodad.w + ox, j + oy), w - i - doodad.w, j);
                    }
                }
            }
        }
    }

    public static void addDoodad(Seq<PanelDoodad> doodadList, boolean[][] placed, PanelDoodad p, int x, int y){
        doodadList.add(p);
        for(int i = 0; i < p.type.w; i++){
            for(int j = 0; j < p.type.h; j++){
                placed[x + i][y + j] = true;
            }
        }
    }

    public static PanelDoodad get(PanelDoodadType type, float x, float y){
        return type.create((type.w * 0.5f + x) * ModularPartType.partSize, (type.h * 0.5f + y) * ModularPartType.partSize, x > 0);
    }

}
