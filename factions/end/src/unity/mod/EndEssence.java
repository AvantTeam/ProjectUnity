package unity.mod;

import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.world.*;
import unity.assets.list.*;
import unity.assets.list.PUShaders.*;
import unity.world.blocks.EndConcetratorBlock.*;

import static mindustry.Vars.*;

public class EndEssence{
    float[][] velocity, nextVelocity;
    float[] pressure, nextPressure;
    EssenceSink[] sink;
    Seq<EssenceSink> sinkSeq;
    boolean updateAir, updateV, updateV2;
    byte[] walls;
    int width, height;
    float offX, offY;
    float timer;

    IntSeq wallsDraw = new IntSeq(20);
    boolean preDraw = true;

    static int div = 5;
    static float takeAmount = 0.3f;
    static float velocityLoss = 1f - 0.15f;
    static float velocityLimit = 90f * 90f;
    static float pressureDiffToVelConstant = 40f;
    static int wallMinBlock = 5;
    static int wallFullBlock = 25 - wallMinBlock;

    static float[] tmpPos = new float[8];
    static FrameBuffer airBuffer;
    static boolean updateVelocity = true;
    static IntIntMap tmpMap = new IntIntMap();
    static IntSeq tmpSeq = new IntSeq();

    public EndEssence(){
    }

    public void init(){
        reset();
        width = world.width() / div + 1;
        height = world.height() / div + 1;

        if(updateVelocity){
            velocity = new float[2][width * height];
            nextVelocity = new float[3][width * height];
        }
        pressure = new float[width * height];
        nextPressure = new float[width * height];
        walls = new byte[width * height];
        sink = new EssenceSink[width * height];
        sinkSeq = new Seq<>();

        /*
        Time.run(4f * 60f, () -> {
            addAir(width / 2, height / 2, 100f, 50f, 20f);
            addAir(width / 4, height / 4, 200f);
        });
        */

        float w1 = width * div, h1 = height * div;
        offX = (w1 - world.width()) * tilesize / 2f;
        offY = (h1 - world.height()) * tilesize / 2f;

        if(!headless){
            if(airBuffer == null){
                airBuffer = new FrameBuffer(width, height);
                airBuffer.getTexture().setFilter(TextureFilter.linear);
                //airBuffer.getTexture().setFilter(TextureFilter.nearest);
            }else{
                airBuffer.resize(width, height);
            }
            preDraw = true;
        }
        for(int sx = 0; sx < width; sx++){
            for(int sy = 0; sy < height; sy++){
                int count = -wallMinBlock;
                int x = sx * div, y = sy * div;
                scan:
                for(int ix = 0; ix < div; ix++){
                    for(int iy = 0; iy < div; iy++){
                        int wx = x + ix - (int)(offX / tilesize), wy = y + iy - (int)(offY / tilesize);
                        Tile t = world.tile(wx, wy);
                        if(t != null && !t.block().isAir() && !t.breakable()){
                            count++;
                            if(count >= wallFullBlock){
                                break scan;
                            }
                        }
                    }
                }
                if(count > 0){
                    walls[sx + sy * width] = (byte)count;
                    wallsDraw.add(sx + sy * width);
                }
            }
        }
        int ex = 0, ey = 0;
        while(ex < width || ey < height){
            if(ex < width){
                for(int s = 0; s < 2; s++){
                    int eyy = s * (height - 1);
                    for(Point2 d : Geometry.d8){
                        if(inBounds(ex + d.x, eyy + d.y)){
                            byte nw = walls[ex + d.x + (eyy + d.y) * width];
                            if(walls[ex + eyy * width] < nw){
                                walls[ex + eyy * width] = nw;
                            }
                        }
                    }
                }
            }
            if(ey < height){
                for(int s = 0; s < 2; s++){
                    int exx = s * (width - 1);
                    for(Point2 d : Geometry.d8){
                        if(inBounds(exx + d.x, ey + d.y)){
                            byte nw = walls[exx + d.x + (ey + d.y) * width];
                            if(walls[exx + ey * width] < nw){
                                walls[exx + ey * width] = nw;
                            }
                        }
                    }
                }
            }
            ex++;
            ey++;
        }
    }
    
    public void reset(){
        pressure = null;
        velocity = null;

        wallsDraw.clear();

        preDraw = true;
        timer = 0f;
    }

    void updateVelocity(int pos){
        int x = pos % width, y = pos / width;
        float ovx = velocity[0][pos], ovy = velocity[1][pos];
        float value = pressure[pos];
        
        if(ovx != 0 || ovy != 0){
            float len = Mathf.sqrt(ovx * ovx + ovy * ovy);
            float angle = Mathf.angle(ovx, ovy);
            float transfer = Mathf.clamp(len) * 0.7f;
            //float prsTransfer = Mathf.clamp(len) * 0.9f;
            //float prsTransfer = Mathf.clamp(len) * 0.75f;
            //float transfer = Mathf.clamp(len) * 0.3f;
            float prsTransfer = Mathf.clamp(len) * 0.4f;
            
            float total = 0f;
            float ptake = 0f;
            float vtake = 0f;
            
            float ax = 0f, ay = 0f;
            float maxEnt = 0f;
            float minEnt = velocityLimit * 2f;
            
            for(int t = 0; t < 2; t++){
                int s = 0;
                //entropy = Mathf.clamp(entropy) * 0.7f;
                for(Point2 d : Geometry.d8){
                    float a = s * 45f, scl = (s % 2 == 0) ? 1f : 0.707106f;
                    //float a = s * 45f;
                    if(t == 0) tmpPos[s] = 0f;
                    int ox = x + d.x, oy = y + d.y;
                    float adst = Angles.angleDist(a, angle);
                    float cs = Mathf.cosDeg(adst) * scl;
                    if(inBounds(ox, oy)){
                        int np = ox + oy * width;
                        float wallr = wallr(np);
                        if(t == 0){
                            float nvx = velocity[0][np], nvy = velocity[1][np];
                            float nlen = nvx * nvx + nvy * nvy;
                            maxEnt = Math.max(maxEnt, nlen);
                            minEnt = Math.min(minEnt, nlen);

                            //total += Math.abs(cs) * (2f - wallr);
                            total += Math.abs(cs);

                            tmpPos[(s + 4) % 8] = (1f - wallr) * Math.max(cs, 0f) * 0.5f;
                        }else{
                            float diffEnt = Mathf.clamp(Mathf.sqrt(Math.abs(maxEnt - minEnt)) / 20f) * 0.5f;

                            //float u = (Math.abs(cs) / total) * transfer * wallr;
                            float u = (Math.max(cs, tmpPos[s]) / total) * transfer * wallr;
                            float u2 = (cs / total) * prsTransfer * wallr;
                            float ptk = value * u2;
                            float ptk2;
                            if(cs < 0){
                                float other = pressure[np];
                                ptk2 = Math.max(-other * (prsTransfer / 8) * scl * wallr, ptk) * 0.35f;
                            }else{
                                ptk2 = ptk;
                            }
                            addVelocity(np, ovx * u, ovy * u, ptk2);
                            ptake += ptk2;
                            vtake += u;

                            ax += (velocity[0][np] - ovx) * (Math.abs(cs) / total) * (0.3f + diffEnt);
                            ay += (velocity[1][np] - ovy) * (Math.abs(cs) / total) * (0.3f + diffEnt);
                        }
                    }else{
                        if(t == 0){
                            total += Math.abs(cs);
                        }else{
                            ptake += (value / 6.828424f) * scl;
                        }
                    }
                    s++;
                }
            }
            addVelocity(pos, -ovx * vtake + ax, -ovy * vtake + ay, -ptake);
        }
    }

    void updateVelocities(){
        int llen = width * height;
        if(updateV2){
            for(int i = 0; i < llen; i++){
                if(velocity[0][i] == 0f && velocity[1][i] == 0f) continue;
                updateVelocity(i);
            }
            updateV2 = false;
        }
        if(updateV){
            for(int i = 0; i < llen; i++){
                float vx = nextVelocity[0][i], vy = nextVelocity[1][i], pres = nextVelocity[2][i];
                if(vx == 0f && vy == 0f && pres == 0f) continue;
                float vloss = wallr(i) * velocityLoss;
                velocity[0][i] += vx;
                velocity[1][i] += vy;
                pressure[i] += pres;
                float nvx = (velocity[0][i] *= vloss), nvy = (velocity[1][i] *= vloss);
                if(!Mathf.zero(nvx, 0.0001f) || !Mathf.zero(nvy, 0.0001f)){
                    float len = nvx * nvx + nvy * nvy;
                    if(len > velocityLimit){
                        float scl = (float)Math.sqrt(velocityLimit / len);
                        velocity[0][i] *= scl;
                        velocity[1][i] *= scl;
                    }
                    updateV2 = true;
                }else{
                    velocity[0][i] = 0f;
                    velocity[1][i] = 0f;
                }
                nextVelocity[0][i] = 0f;
                nextVelocity[1][i] = 0f;
                nextVelocity[2][i] = 0f;
            }
            updateV = false;
        }
    }

    void update(){
        if(!state.isGame() || state.isPaused()) return;
        timer += Time.delta;
        if(timer < 3f) return;
        timer = 0f;

        if(updateVelocity) updateVelocities();
        int llen = width * height;
        if(updateAir){
            for(int i = 0; i < llen; i++){
                if(pressure[i] > 0f) updateAir(i);
            }
            updateAir = false;
            for(int i = 0; i < llen; i++){
                float newA = (pressure[i] += nextPressure[i]);
                if(newA <= 0.0001f){
                    pressure[i] = 0f;
                }else{
                    updateAir = true;
                }
                nextPressure[i] = 0f;
            }

        }
        for(EssenceSink sink : sinkSeq){
            sink.update();
        }
    }

    void draw(){
        Draw.draw(145, () -> {
            Tmp.m1.set(Draw.proj());
            Draw.flush();
            airBuffer.begin(Color.black);
            Draw.proj().setOrtho(0, 0, airBuffer.getWidth(), airBuffer.getHeight());
            int llen = width * height;
            for(int i = 0; i < llen; i++){
                float pr;
                if((pr = pressure[i]) > 0.0075f){
                    int x = i % width, y = i / width;
                    Draw.color(Tmp.c1.set(Color.red).a(Mathf.clamp(pr * 5f)));
                    Fill.rect(x + 0.5f, y + 0.5f, 1, 1);
                }
            }
            Draw.proj(Tmp.m1);
            Draw.color();
            airBuffer.end();

            EndAirShader s = PUShaders.endAirShader;
            s.offsetX = offX;
            s.offsetY = offY;
            s.div = div * tilesize;
            s.tex = airBuffer.getTexture();
            Blending.additive.apply();
            Draw.blit(airBuffer, s);
            Blending.normal.apply();
        });
    }

    void updateAir(int p){
        int x = p % width, y = p / width;
        float value = get(p);
        float take = 0f;
        int s = 0;
        float vx = 0f, vy = 0f;

        for(Point2 d : Geometry.d8){
            int ox = x + d.x, oy = y + d.y;
            int opos = ox + oy * width;
            if(!inBounds(ox, oy)){
                s++;
                continue;
            }
            float v = get(opos);
            float wallr = wallr(opos);
            
            float scl = ((s % 2 == 0 ? 1f : 0.707106f) * takeAmount / 8) * wallr;

            float diff = Math.max(value - v, 0f) * pressureDiffToVelConstant * wallr;
            float dif = 0.75f + Mathf.clamp(Math.abs(v - value) / 5f) * 0.4f;
            
            float dx = d.x * scl * diff, dy = d.y * scl * diff;
            float dd = dx * dx + dy * dy;

            if(dd > velocityLimit){
                float da = Mathf.sqrt(velocityLimit / dd);
                dx *= da;
                dy *= da;
            }
            vx += dx;
            vy += dy;
            
            take += scl * dif;
            addAir(ox, oy, scl * value * dif);
            addVelocity(opos, dx, dy);
            s++;
        }
        addAir(p, -value * take);
        if((vx * vx + vy * vy) > 0.0001f * 0.0001f) addVelocity(p, vx / 8f, vy / 8f);
    }

    boolean inBounds(int x, int y){
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    float wallr(int pos){
        if(walls[pos] != 0){
            float v = 1f - Mathf.clamp(walls[pos] / (float)wallFullBlock);
            return (v * v * 0.95f) + 0.05f;
        }
        return 1f;
    }

    void addVelocity(int pos, float vx, float vy){
        addVelocity(pos, vx, vy, 0f);
    }
    
    void addVelocity(int pos, float vx, float vy, float pressure){
        if(!updateVelocity) return;
        nextVelocity[0][pos] += vx;
        nextVelocity[1][pos] += vy;
        nextVelocity[2][pos] += pressure;
        updateV = true;
    }

    int posWorld(float x, float y){
        int ix = (int)((x + offX) / tilesize) / div;
        int iy = (int)((y + offY) / tilesize) / div;
        if(!inBounds(ix, iy)) return -1;
        return ix + iy * width;
    }

    void addAir(int x, int y, float value, float vx, float vy){
        int pos = x + y * width;
        //addAir(pos, value, vx, vy);
        addAir(pos, value);
        addVelocity(pos, vx, vy);
    }

    void addAir(int x, int y, float value){
        addAir(x + y * width, value);
    }

    void addAir(int pos, float value){
        nextPressure[pos] += value;
        updateAir = true;
    }

    float get(int p){
        return pressure[p];
    }

    public void addSink(EndConcetratorBuilding b){
        tmpMap.clear();
        tmpSeq.clear();
        int tx = b.tile.x, ty = b.tile.y;
        int size = b.block.size;
        int offset = -(size - 1) / 2;
        for(int dx = 0; dx < size; dx++){
            for(int dy = 0; dy < size; dy++){
                int x = Mathf.clamp(((tx + dx + offset) + (int)(offX / tilesize)) / div, 0, width - 1);
                int y = Mathf.clamp(((ty + dy + offset) + (int)(offY / tilesize)) / div, 0, height - 1);
                int pos = x + y * width;
                if(!tmpMap.containsKey(pos)){
                    tmpSeq.add(pos);
                }
                tmpMap.increment(pos);
            }
        }
        for(int i = 0; i < tmpSeq.size; i++){
            int pos = tmpSeq.items[i];
            EssenceSink s = sink[pos];
            if(s == null){
                s = new EssenceSink();
                s.pos = pos;
                sink[pos] = s;
                sinkSeq.add(s);
            }
            s.add(b, tmpMap.get(pos));
        }
    }

    public void removeSink(EndConcetratorBuilding b){
        int tx = b.tile.x, ty = b.tile.y;
        int size = b.block.size;
        int offset = -(size - 1) / 2;
        for(int dx = 0; dx < size; dx++){
            for(int dy = 0; dy < size; dy++){
                int x = Mathf.clamp(((tx + dx + offset) + (int)(offX / tilesize)) / div, 0, width - 1);
                int y = Mathf.clamp(((ty + dy + offset) + (int)(offY / tilesize)) / div, 0, height - 1);
                int pos = x + y * width;
                EssenceSink s = sink[pos];
                if(s == null) continue;
                s.remove(b);
                if(s.size <= 0){
                    sinkSeq.remove(s);
                    sink[pos] = null;
                }
            }
        }
    }

    class EssenceSink{
        byte[] weight = new byte[div * div];
        EndConcetratorBuilding[] buildings = new EndConcetratorBuilding[div * div];
        byte size = 0;
        int pos;

        void update(){
            //float take = 0f, mw = 0f;
            float mw = 0f;
            for(int i = 0; i < size; i++){
                EndConcetratorBuilding b = buildings[i];
                float abs = b.absorbAmount();
                //take += abs;
                mw += (weight[i] / (float)(b.block.size * b.block.size)) * abs;
            }
            if(mw > 0){
                float t = Math.min(get(pos) / 2f, mw);
                pressure[pos] -= t;
                for(int i = 0; i < size; i++){
                    EndConcetratorBuilding b = buildings[i];
                    b.module.lastEssence = b.module.essence;
                    b.module.essence += ((weight[i] * b.absorbAmount()) / mw) * t;
                }
            }
        }

        void remove(EndConcetratorBuilding b){
            for(int i = 0; i < size; i++){
                EndConcetratorBuilding e = buildings[i];
                if(e == b){
                    size--;
                    buildings[i] = buildings[size];
                    buildings[size] = null;

                    weight[i] = weight[size];
                    weight[size] = 0;
                    break;
                }
            }
        }

        void add(EndConcetratorBuilding b, int weight){
            if(size >= div * div) return;
            buildings[size] = b;
            this.weight[size] = (byte)weight;
            size++;
        }
    }
}
