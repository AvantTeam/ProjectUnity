package unity.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.Conveyor.ConveyorBuild;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.blocks.production.Incinerator.IncineratorBuild;
import unity.content.UnityFx;
import unity.graphics.UnityPal;
import unity.world.blocks.ExpBuildBase;

import static mindustry.Vars.*;
import static unity.content.UnityBullets.exporb;

public class ExpOrb extends BulletType{
    public static final float expAmount = 10f;
    protected final int[] d4x = new int[]{1, 0, -1, 0}, d4y = new int[]{0, 1, 0, -1};

    public ExpOrb(){
        damage = 8f;
        drag = 0.05f;
        lifetime = 180f;
        speed = 0.0001f;
        despawnEffect = UnityFx.expDespawn;
        pierce = true;
        hitSize = 2f;
        hittable = collides = collidesTiles = collidesAir = collidesGround = keepVelocity = absorbable = false;
        lightColor = UnityPal.expColor;
        hitEffect = Fx.none;
        shootEffect = Fx.none;
    }

    @Override
    public void draw(Bullet b){
        if(b.fin() > 0.5f && Time.time() % 14 < 7f) return;
        Draw.color(UnityPal.endColor, Color.white, 0.1f + 0.1f * Mathf.sin(Time.time() * 0.03f + b.id * 2f));
        Fill.circle(b.x, b.y, 1.5f);
        Lines.stroke(0.5f);
        for(int i = 0; i < 4; i++) Drawf.tri(b.x, b.y, 4f, 4 + 1.5f * Mathf.sin(Time.time() * 0.12f + b.id * 3f), i * 90f + Mathf.sin(Time.time() * 0.04f + b.id * 5f) * 28f);
    }

    @Override
    public void update(Bullet b){
        if(b.moving()) b.time = 0f;
        Tile tile = world.tileWorld(b.x, b.y);
        //TODO update below condition
        if(tile.build instanceof ExpBuildBase){
            ((ExpBuildBase)tile.build).incExp(expAmount);
            UnityFx.expAbsorb.at(b.x, b.y);
            b.remove();
        }
        /*TODO update this
        else if(false){
        
        }*/else if(tile.block() instanceof Incinerator && ((IncineratorBuild)tile.build).heat > 0.5f){
            UnityFx.expAbsorb.at(b.x, b.y);
            b.remove();
        }else if(tile.solid()){
            b.trns(-1.1f * b.vel.x, -1.1f * b.vel.y);
            b.vel.scl(0f);
        }else if(tile.block() instanceof Conveyor) conveyor(b, (Conveyor)tile.block(), (ConveyorBuild)tile.build);
    }

    protected void conveyor(Bullet b, Conveyor block, ConveyorBuild build){
        if(build.clogHeat > 0.5f || !build.enabled) return;
        float mspeed = build.delta() * block.speed / 3f;
        b.vel.add(d4x[build.rotation] * mspeed, d4y[build.rotation] * mspeed);
    }

    public static void createExp(float x, float y, float amount, float r){
        if(!net.client()){
            int n = Mathf.floorPositive(amount / expAmount);
            for(int i = 0; i < n; i++) exporb.createNet(Team.derelict, x - r + Mathf.random() * 2f * r, y - r + Mathf.random() * 2f * r, 0f, 0f, 1f, 1f);
        }
    }

    public static void createExp(float x, float y, float amount){
        createExp(x, y, amount, 4f);
    }

    public static void spreadExp(float x, float y, float amount, float v){
        if(!net.client()){
            v *= 1000f;
            int n = Mathf.floorPositive(amount / expAmount);
            for(int i = 0; i < n; i++) exporb.createNet(Team.derelict, x, y, Mathf.random() * 360f, 0, v, 1f);
        }
    }

    public static void spreadExp(float x, float y, float amount){
        spreadExp(x, y, amount, 4f);
    }

    public static void outputExp(float x, float y, int n, float v){
        if(!net.client()){
            v *= 1000f;
            for(int i = 0; i < n; i++) exporb.createNet(Team.derelict, x, y, Mathf.random() * 360f, 0, v, 1f);
        }
    }

    public static void outputExp(float x, float y, int n){
        outputExp(x, y, n, 4f);
    }

    public static void spewExp(float x, float y, int n, float r, float v){
        if(net.client()){
            v *= 1000f;
            for(int i = 0; i < n; i++) exporb.createNet(Team.derelict, x, y, r - 5f + 10 * Mathf.random(), 0, v, 1f);
        }
    }

    public static void spewExp(float x, float y, int n, float r){
        spewExp(x, y, n, r, 8f);
    }
}
