package unity.entities;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.production.Incinerator.*;
import unity.content.*;
import unity.gen.*;
import unity.gen.Expc.*;
import unity.world.blocks.exp.*;

import static mindustry.Vars.*;

/** @author GlennFolker
 * @author sunny */
public class ExpOrbs{
    public static final float expAmount = 10f;

    private static final Color expColor = Color.valueOf("84ff00");
    private static final int[] d4x = new int[]{1, 0, -1, 0};
    private static final int[] d4y = new int[]{0, 1, 0, -1};
    private static final ExpOrb expOrb = new ExpOrb();

    public static void spreadExp(float x, float y, float amount){
        spreadExp(x, y, amount, 4f);
    }

    public static void spreadExp(float x, float y, float amount, float v){
        if(net.server() || !net.active()){
            v *= 1000f;

            int n = Mathf.floorPositive(amount / expAmount);
            for(int i = 0; i < n; i++){
                expOrb.createNet(Team.derelict, x, y, Mathf.random() * 360f, 0f, v, 1f);
            }
        }
    }

    public static final class ExpOrb extends BulletType{
        {
            absorbable = false;
            damage = 8f;
            drag = 0.05f;
            lifetime = 180f;
            speed = 0.0001f;
            keepVelocity = false;
            pierce = true;
            hitSize = 2f;

            hittable = false;
            collides = false;
            collidesTiles = false;
            collidesAir = false;
            collidesGround = false;

            lightColor = expColor;
            hitEffect = Fx.none;
            shootEffect = Fx.none;
            despawnEffect = UnityFx.orbDespawn;
        }

        private ExpOrb(){}

        @Override
        public void draw(Bullet b){
            if((b.fin() > 0.5f) && Time.time % 14f < 7f) return;

            Draw.color(expColor, Color.white, 0.1f + 0.1f * Mathf.sin(Time.time * 0.03f + b.id * 2f));

            Fill.circle(b.x, b.y, 1.5f);
            Lines.stroke(0.5f);
            for(var i = 0; i < 4; i++){
                Drawf.tri(b.x, b.y, 4f, 4f + 1.5f * Mathf.sin(Time.time * 0.12f + b.id * 3f), i * 90 + Mathf.sin(Time.time * 0.04f + b.id * 5f) * 28f);
            }
        }

        @Override
        public void update(Bullet b){
            if(b.moving()) b.time(0f);

            Tile tile = world.tileWorld(b.x, b.y);
            if(tile == null || tile.build == null) return;

            if(tile.build instanceof ExpHolder exp && exp.acceptOrb() && exp.handleOrb((int)expAmount)){
                b.remove();
            }
            else if(tile.block() instanceof Conveyor conv){
                if(conv.absorbLasers){ //this will be used as a flag for exp conveyors
                    expConveyor(b, conv, (ConveyorBuild)tile.build);
                }else{
                    conveyor(b, conv, (ConveyorBuild)tile.build);
                }
            }
            else if(tile.block() instanceof Incinerator && ((IncineratorBuild)tile.build).heat > 0.5f){
                //TODO the effect
                b.remove();
            }
            else if(tile.solid()){
                b.trns(-1.1f * b.vel.x, -1.1f * b.vel.y);
                b.vel.scl(0f);
            }
        }

        private void conveyor(Bullet b, Conveyor block, ConveyorBuild build){
            if(build.clogHeat > 0.5f || !build.enabled) return;

            float speed = block.speed / 3f;
            b.vel.add(d4x[build.rotation] * speed * build.delta(), d4y[build.rotation] * speed * build.delta());
        }

        private void expConveyor(Bullet b, Conveyor block, ConveyorBuild build){
            if(build.clogHeat > 0.5f || !build.enabled) return;

            float speed = block.speed * 2f;
            b.vel.scl(0.7f);
            b.vel.add(d4x[build.rotation] * speed * build.delta(), d4y[build.rotation] * speed * build.delta());
        }
    }
}
