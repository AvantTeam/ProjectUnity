package unity.entities.type;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.content.*;
import unity.gen.entities.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.util.*;

import static mindustry.Vars.*;
import static unity.graphics.MonolithPalettes.*;

/**
 * Convenience class for {@link MonolithSoul} types.
 * @author GlennFolker
 */
public class MonolithSoulType extends PUUnitType{
    public Func<MonolithSoul, MultiTrail> corporealTrail = soul -> new MultiTrail(new TrailHold(new BaseTrail(trailLength)));

    public float trailChance = -1f, formChance = -1f, formAbsorbChance = -1f;
    public Effect trailEffect = Fx.none, formEffect = Fx.none, formAbsorbEffect = Fx.none;

    public MonolithSoulType(String name){
        super(name);

        lowAltitude = true;
        flying = true;
        omniMovement = false;
        playerControllable = false;
    }

    @Override
    public void update(Unit unit){
        if(unit instanceof MonolithSoul soul){
            if(soul.trail instanceof MultiTrail trail){
                float width = (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * soul.elevation);
                if(trail.trails.length == 3 && soul.corporeal()){
                    MultiTrail copy = trail.copy();
                    copy.rot = BaseTrail::rot;

                    MonolithFx.trailFadeLow.at(soul.x, soul.y, width, monolithLight, copy);
                    soul.trail = corporealTrail.get(soul);
                }else if(trail.trails.length == 1 && !soul.corporeal()){
                    MultiTrail copy = trail.copy();
                    copy.rot = BaseTrail::rot;

                    MonolithFx.trailFadeLow.at(soul.x, soul.y, width, monolithLight, copy);
                    soul.trail = kickstartTrail(soul, createTrail(soul));
                }
            }

            if(!soul.corporeal()){
                if(trailChance > 0f && Mathf.chanceDelta(trailChance)) trailEffect.at(soul.x, soul.y, Time.time, new Vec2(soul.vel).scl(-0.3f / Time.delta));
                if(soul.forming()){
                    if(formChance > 0f && formAbsorbChance > 0f) for(Tile form : soul.forms){
                        if(formChance > 0f && Mathf.chanceDelta(formChance)) formEffect.at(form.drawx(), form.drawy(), 4f);
                        if(formAbsorbChance > 0f && Mathf.chanceDelta(formAbsorbChance)) formAbsorbEffect.at(form.drawx(), form.drawy(), 0f, soul);
                    }
                }else if(soul.joining() && Mathf.chanceDelta(0.33f)){
                    MonolithFx.soulAbsorb.at(soul.x + Mathf.range(6f), soul.y + Mathf.range(6f), 0f, soul.joinTarget);
                }
            }
        }

        super.update(unit);
    }

    @Override
    public void draw(Unit unit){
        if(!(unit instanceof MonolithSoul soul)) return;
        if(!soul.corporeal){
            if(!headless && soul.trail == null) soul.trail = kickstartTrail(soul, createTrail(soul));

            float z = Draw.z();
            Draw.z(Layer.flyingUnitLow);

            float trailSize = (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * soul.elevation) * trailScl;
            soul.trail.drawCap(engineColor, trailSize);
            soul.trail.draw(engineColor, trailSize);

            Draw.z(Layer.effect - 0.01f);
            drawBase(soul);

            Draw.z(Layer.flyingUnit);
            drawForm(soul);

            Draw.z(Layer.flyingUnit);
            drawJoin(soul);

            Draw.z(z);
        }
    }

    public void drawBase(MonolithSoul soul){
        /*Draw.blend(Blending.additive);
        Draw.color(monolith);
        Fill.circle(soul.x, soul.y, 6f);

        Draw.color(monolithDark);
        Draw.rect(softShadowRegion, soul.x, soul.y, 16f, 16f);

        Draw.blend();
        Lines.stroke(1f, monolithDark);

        float rotation = Time.time * 3f * Mathf.sign(soul.id % 2 == 0);
        for(int i = 0; i < 5; i++){
            float r = rotation + 72f * i, sect = 60f;
            Lines.arc(soul.x, soul.y, 10f, sect / 360f, r - sect / 2f);

            Tmp.v1.trns(r, 10f).add(soul);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, 2.5f, 6f, r);
        }

        Draw.reset();*/
    }

    public void drawForm(MonolithSoul soul){
        /*for(int i = 0; i < wreckRegions.length; i++){
            float off = (360f / wreckRegions.length) * i;
            float fin = soul.formProgress, fout = 1f - fin;

            Tmp.v1.trns(soul.rotation + off, fout * 24f)
                .add(Tmp.v2.trns((Time.time + off) * 4f, fout * 3f))
                .add(soul);

            Draw.alpha(fin);
            Draw.rect(wreckRegions[i], Tmp.v1.x, Tmp.v1.y, soul.rotation - 90f);
        }*/
    }

    public void drawJoin(MonolithSoul soul){
        /*Lines.stroke(1.5f, monolith);

        TextureRegion reg = Core.atlas.find("unity-monolith-chain");
        Quat rot = MathUtils.q1.set(Vec3.Z, soul.ringRotation() + 90f).mul(MathUtils.q2.set(Vec3.X, 75f));
        float
            t = Interp.pow3Out.apply(soul.joinTime()), w = reg.width * Draw.scl * 0.5f * t, h = reg.height * Draw.scl * 0.5f * t,
            rad = t * 25f, a = Mathf.curve(t, 0.33f);

        Draw.alpha(a);
        DrawUtils.panningCircle(reg,
            soul.x, soul.y, w, h,
            rad, 360f, Time.time * 6f * Mathf.sign(soul.id % 2 == 0) + soul.id * 30f,
            rot, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        Draw.color(Color.black, monolithDark, 0.67f);
        Draw.alpha(a);

        Draw.blend(Blending.additive);
        DrawUtils.panningCircle(Core.atlas.find("unity-line-shade"),
            soul.x, soul.y, w + 6f, h + 6f,
            rad, 360f, 0f,
            rot, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        Draw.blend();*/
    }

    @Override
    public MonolithSoul create(Team team){
        return (MonolithSoul)super.create(team);
    }
}
