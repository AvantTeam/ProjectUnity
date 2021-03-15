package unity.entities.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.graphics.*;

public class UnitCutEffect extends EffectState{
    static Vec2 tmpPoint = new Vec2(), tmpPoint2 = new Vec2();
    static Color color = new Color();
    Unit unit;
    Vec3 cutDirection = new Vec3();
    Vec2 vel = new Vec2(), offset = new Vec2();

    public static void createCut(Unit unit, float x, float y, float x2, float y2){
        Intersector.nearestSegmentPoint(x, y, x2, y2, unit.x, unit.y, tmpPoint);
        tmpPoint.sub(unit);
        tmpPoint.limit(unit.hitSize / 4f);
        float rot = tmpPoint.angle();
        unit.hitTime = 0f;
        for(int i = 0; i < 2; i++){
            UnitCutEffect l = Pools.obtain(UnitCutEffect.class, UnitCutEffect::new);
            l.cutDirection.set(tmpPoint.x, tmpPoint.y, rot + (i * 180f));
            l.lifetime = 40f + (unit.hitSize / 20f) + Mathf.range(1f, 5f);
            l.x = unit.x;
            l.y = unit.y;
            l.unit = unit;
            l.offset.setZero();
            l.vel.trns(rot + (i * 180f), unit.hitSize / 60f);
            l.add();
        }
        UnityFx.tenmeikiriCut.at(unit.x + tmpPoint.x, unit.y + tmpPoint.y, rot + 90f, unit.hitSize * 1.5f);
    }

    @Override
    public float clipSize(){
        return unit.clipSize() * 1.5f;
    }

    @Override
    public void reset(){
        super.reset();
        unit = null;
    }

    @Override
    public void update(){
        if(time >= lifetime){
            Effect.shake(unit.hitSize / 3f, unit.hitSize / 3f, x + offset.x, y + offset.x);
            Fx.dynamicExplosion.at(x + offset.x, y + offset.y, (unit.bounds() / 2f) / 8f);
            Effect.scorch(x + offset.x, y + offset.y, (int)(unit.hitSize / 5));
            Fx.explosion.at(x + offset.x, y + offset.y);
            unit.type.deathSound.at(x + offset.x, y + offset.y);
            remove();
            return;
        }
        unit.hitTime = 0f;
        offset.add(vel.x * Time.delta, vel.y * Time.delta);
        vel.scl(1f - Math.min(unit.drag, 0.1f));
        if(Mathf.chanceDelta(0.3f * (unit.hitSize / 45f))){
            tmpPoint2.trns(cutDirection.z, 0f, Mathf.range(unit.hitSize / 2f)).add(cutDirection.x + offset.x, cutDirection.y + offset.y).add(unit);
            Fx.fallSmoke.at(tmpPoint2.x, tmpPoint2.y);
        }
        time += Time.delta;
    }

    @Override
    public void draw(){
        float z = unit.elevation > 0.5 ? (unit.type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : unit.type.groundLayer + Mathf.clamp(unit.type.hitSize / 4000f, 0f, 0.01f);

        Draw.draw(z, () -> {
            tmpPoint.set(Core.camera.position);
            Core.camera.position.set(tmpPoint).add(offset);
            Core.camera.update();
            Draw.proj(Core.camera);

            //probably laggy as hell.
            //code is intentionally coded to not capture other layers as it could interfere with other cut effects
            color.set(Color.green);
            UnityShaders.stencilShader.stencilColor.set(color);
            UnityShaders.stencilShader.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, fin());
            Vars.renderer.effectBuffer.begin(Color.clear);

            unit.draw();
            Draw.reset();

            float[] verts = new float[8];
            int[] dx = {-1, -1, 1, 1};
            int[] dy = {0, 1, 1, 0};

            for(int i = 0; i < 4; i++){
                tmpPoint2.trns(cutDirection.z, dy[i] * unit.hitSize * 1.5f, dx[i] * unit.hitSize * 1.5f).add(cutDirection.x, cutDirection.y).add(unit);
                for(int j = 0; j < 2; j++){
                    verts[(i * 2) + j] = j == 0 ? tmpPoint2.x : tmpPoint2.y;
                }
            }
            Draw.color(color);
            Fill.quad(verts[0], verts[1], verts[2], verts[3], verts[4], verts[5], verts[6], verts[7]);

            Vars.renderer.effectBuffer.end();
            Draw.blit(Vars.renderer.effectBuffer, UnityShaders.stencilShader);

            Core.camera.position.set(tmpPoint);
            Core.camera.update();
            Draw.proj(Core.camera);
        });
    }
}
