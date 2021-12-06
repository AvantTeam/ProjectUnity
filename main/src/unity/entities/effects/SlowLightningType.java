package unity.entities.effects;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.gen.*;
import unity.util.*;

public class SlowLightningType{
    private static int seed = 1;
    public final static int maxNodes = 60;
    public static BasicPool<SlowLightningNode> nodes = new BasicPool<>(8, 300, SlowLightningNode::new);

    public Color colorFrom = Color.white, colorTo = Pal.lancerLaser;
    public float damage = 12;
    public float colorTime = 32f, fadeTime = 20f;
    public float splitChance = 0.035f;
    public float nodeLength = 50f, nodeTime = 3f, range = 150f;
    public float randSpacing = 20f, splitRandSpacing = 60f;
    public float lineWidth = 2f, lifetime = 120f;
    public float maxRotationSpeed = 22f, minRotationSpeed = 1.5f, rotationDistance = 600f;
    public boolean continuous = false;
    public Effect hitEffect = HitFx.coloredHitSmall;

    public SlowLightning create(Team team, float x, float y, float rotation, Floatp liveDamage, Posc parent, Position target){
        SlowLightning s = SlowLightning.create();
        s.seed = seed;
        s.type = this;
        s.team = team;
        s.set(x, y);
        s.rotation = rotation;
        s.liveDamage = liveDamage;
        s.parent = parent;
        s.target = target;
        s.add();
        seed++;
        return s;
    }

    public void damageUnit(SlowLightningNode s, Unit unit){
        Floatp l = s.main.liveDamage;
        unit.damage(l != null ? l.get() : damage);
    }

    public void damageBuilding(SlowLightningNode s, Building building){
        Floatp l = s.main.liveDamage;
        building.damage(l != null ? l.get() : damage);
    }

    public void hit(SlowLightningNode s, float x, float y){
        hitEffect.at(x, y, s.rotation, colorFrom);
    }

    public static class SlowLightningNode implements Position, Poolable{
        public float x, y, colorProgress, time, rotation, rotRand, dist;
        public int layer = 0;
        public SlowLightning main;
        public SlowLightningNode parent;
        public boolean ended = false;

        public void move(int originLayer, float mx, float my){
            float scl = 1f - (layer / (float)originLayer);
            x += mx * scl;
            y += my * scl;
        }

        public void update(){
            SlowLightningType type = main.type;
            if(colorProgress < 1f) colorProgress = Math.min(1f, colorProgress + (Time.delta / type.colorTime));
            if(time < 1f){
                time = Math.min(1f, time + (Time.delta / type.nodeTime));
                if(time >= 1f){
                    end();
                }
            }
        }

        public void draw(){
            SlowLightningType type = main.type;
            Draw.color(type.colorFrom, type.colorTo, colorProgress);
            Position p = getLast();
            if(time >= 1f){
                Lines.line(p.getX(), p.getY(), x, y);
            }else{
                Vec2 v = Tmp.v1.set(this).sub(p).scl(time).add(p);
                Lines.line(p.getX(), p.getY(), v.x, v.y);
            }
        }

        void line(float x, float y, float x2, float y2){
            SlowLightningType type = main.type;
            Utils.collideLineRawEnemy(main.team, x, y, x2, y2, type.lineWidth / 3f, (building, direct) -> {
                if(direct) type.damageBuilding(this, building);
                return building.block.absorbLasers;
            }, unit -> {
                type.damageUnit(this, unit);
                return false;
            }, null, (ex, ey) -> type.hit(this, ex, ey), false);
        }

        void end(){
            SlowLightningType type = main.type;
            if(!type.continuous){
                Position p = getLast();
                line(p.getX(), p.getY(), x, y);
            }
            if(!ended && main.distance < type.range && main.nodes.size < maxNodes){
                main.end(this);
            }
        }

        Position getLast(){
            return parent != null ? parent : main;
        }

        @Override
        public float getX(){
            return x;
        }

        @Override
        public float getY(){
            return y;
        }

        @Override
        public void reset(){
            x = y = colorProgress = time = rotation = rotRand = 0f;
            layer = 0;
            main = null;
            parent = null;
            ended = false;
        }
    }
}
