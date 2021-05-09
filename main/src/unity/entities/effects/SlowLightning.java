package unity.entities.effects;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.util.*;

public class SlowLightning extends EffectState{
    public Seq<SlowLightningNode> nodes = new Seq<>();
    public Color colorFrom = Pal.lancerLaser;
    public Color colorTo = Color.white;
    public Team team = Team.derelict;
    public Vec2 influence;
    public Floatp liveDamage;
    public float splitChance = 0.035f;
    public float nodeTime = 32f;
    public float transTime = 1.9f;
    public float damage = 12f;
    public float range = 120f;
    public float lightningLength = 35f;
    public boolean hasIntersected = false;

    @Override
    public float clipSize(){
        return (range * 2f) + 12f;
    }

    @Override
    public void update(){
        nodes.each(SlowLightningNode::update);
        super.update();
    }

    @Override
    public void draw(){
        float z = Draw.z();
        Draw.z(Layer.effect - 1f);
        nodes.each(SlowLightningNode::draw);
        Draw.reset();
        Draw.z(z);
    }

    @Override
    public void add(){
        if(isAdded()) return;
        super.add();
        Tmp.v1.trns(rotation, lightningLength);
        Tmp.v1.add(x, y);

        SlowLightningNode l = Pools.obtain(SlowLightningNode.class, SlowLightningNode::new);
        l.fromPos = new Vec2(x, y);
        l.toPos = new Vec2(Tmp.v1.x, Tmp.v1.y);
        l.rotation = Angles.angle(x, y, Tmp.v1.x, Tmp.v1.y);
        l.origin = this;
        l.score = 0f;
        l.init();
    }

    @Override
    public void reset(){
        super.reset();
        team = Team.derelict;
        liveDamage = null;
        influence = null;
        splitChance = 0.035f;
        nodeTime = 32f;
        transTime = 1.9f;
        damage = 12f;
        range = 120f;
        lightningLength = 35f;
        hasIntersected = false;
        nodes.clear();
    }

    @Override
    public void remove(){
        if(!added) return;
        super.remove();
        nodes.each(SlowLightningNode::remove);
        nodes.clear();
    }

    static class SlowLightningNode implements Poolable{
        public Vec2 fromPos;
        public Vec2 toPos;
        public float rotation;
        public SlowLightning origin;
        public float score = 0f;
        public float visualTime = 0f;
        public float altTime = 0f;
        float timerC = 0f;

        /*public SlowLightningNode(SlowLightning origin, float x, float y, float xa, float ya){
            fromPos = new Vec2(x, y);
            toPos = new Vec2(xa, ya);
            rotation = Angles.angle(x, y, xa, ya);
            this.origin = origin;
            this.origin.nodes.add(this);
            init();
        }

        public SlowLightningNode(SlowLightning origin, Position a, Position b){
            this(origin, a.getX(), a.getY(), b.getX(), b.getY());
        }*/

        @Override
        public void reset(){
            fromPos = null;
            toPos = null;
            rotation = 0f;
            origin = null;
            score = 0f;
            visualTime = 0f;
            altTime = 0f;
            timerC = 0f;
        }

        public void remove(){
            Pools.free(this);
        }

        public void draw(){
            Draw.color(origin.colorFrom, origin.colorTo, visualTime / origin.nodeTime);
            Lines.stroke(Mathf.clamp(origin.fout() * 5f) * 2f);
            Tmp.v1.set(fromPos);
            Tmp.v1.lerp(toPos, Mathf.clamp(altTime / origin.transTime));
            Lines.line(fromPos.x, fromPos.y, Tmp.v1.x, Tmp.v1.y);
        }

        public void update(){
            if(visualTime < origin.nodeTime){
                visualTime += Time.delta;
                visualTime = Mathf.clamp(visualTime, 0, origin.nodeTime);
            }
            if(timerC >= origin.transTime){
                int chance = Mathf.chance(origin.splitChance) ? 2 : 1;
                for(int i = 0; i < chance; i++){
                    float rand = chance == 2 ? Mathf.range(60f) : Mathf.range(20f);
                    //Tmp.v2.trns(rotation + rand, origin.lightningLength);
                    //Tmp.v2.add(toPos.x, toPos.y);

                    if(score < origin.range){
                        Vec2 inf = origin.influence;
                        float rotationC = (inf != null && !origin.hasIntersected) ? Angles.moveToward(rotation + rand, Angles.angle(toPos.x, toPos.y, inf.x, inf.y) + (rand / 1.12f), 17f * Mathf.clamp((600f - Mathf.dst(toPos.x, toPos.y, inf.x, inf.y)) / 600f)) : rotation + rand;
                        Tmp.v2.trns(rotationC, origin.lightningLength);
                        float nScore = Tmp.v2.len() + score;
                        Tmp.v2.add(toPos);
                        if(inf != null && inf.within(Tmp.v2, 43f) && Angles.within(toPos.angleTo(Tmp.v2), Tmp.v2.angleTo(inf), 90f)) origin.hasIntersected = true;

                        SlowLightningNode l = Pools.obtain(SlowLightningNode.class, SlowLightningNode::new);
                        l.origin = origin;
                        l.fromPos = new Vec2(toPos.x, toPos.y);
                        l.toPos = new Vec2(Tmp.v2.x, Tmp.v2.y);
                        l.rotation = Angles.angle(toPos.x, toPos.y, Tmp.v2.x, Tmp.v2.y);
                        l.score = nScore;
                        l.init();
                    }
                }
                timerC = -2f;
            }else if(timerC > -1){
                altTime += Time.delta;
                timerC += Time.delta;
            }
        }

        public void init(){
            origin.nodes.add(this);
            float tDamage = origin.liveDamage != null ? origin.liveDamage.get() : origin.damage;
            Boolf2<Building, Boolean> bf = (building, direct) -> {
                if(direct){
                    building.damage(tDamage);
                    if(building.block.absorbLasers){
                        Tmp.v1.trns(rotation, fromPos.dst(building));
                        Tmp.v1.add(fromPos);
                        toPos.set(Tmp.v1);
                        score = origin.range + 1f;
                    }
                }
                return building.block.absorbLasers;
            };
            Cons<Unit> uc = unit -> unit.damage(tDamage);
            Utils.collideLineRawEnemy(origin.team, fromPos.x, fromPos.y, toPos.x, toPos.y, bf, uc, null, (ex, ey) -> UnityFx.coloredHitSmall.at(ex, ey, origin.colorFrom));
        }
    }
}
