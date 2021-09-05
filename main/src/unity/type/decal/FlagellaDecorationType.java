package unity.type.decal;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.util.*;

public class FlagellaDecorationType extends UnitDecorationType{
    public float x, y;
    public float startIntensity = 15f, endIntensity = 40f;
    public float swayScl = 40f, swayOffset;
    public float angleLimit = 25f;
    public String name;
    float segmentLength;
    int segments;
    TextureRegion[] regions;
    TextureRegion end;

    public FlagellaDecorationType(String name, int textures, int segments, float length){
        this.name = name;
        this.segments = segments;
        segmentLength = length;
        regions = new TextureRegion[textures];

        decalType = FlagellaDecoration::new;
    }

    @Override
    public void load(){
        for(int i = 0; i < regions.length; i++){
            regions[i] = Core.atlas.find(name + "-" + i);
        }
        end = Core.atlas.find(name + "-end");
    }

    @Override
    public void update(Unit unit, UnitDecoration deco){
        FlagellaDecoration d = (FlagellaDecoration)deco;
        float dLen = unit.deltaLen();
        d.progress += dLen;
        Tmp.v1.trns(unit.rotation - 90f, x, y).add(unit);

        FlagellaSegment c = d.root;
        int idx = 0;
        while(c != null){
            Tmp.v2.trns(c.tr, dLen);
            c.tx += Tmp.v2.x;
            c.ty += Tmp.v2.y;
            c.length = offset(d, idx);
            if(c.prev == null){
                c.tr = Utils.clampedAngle(Tmp.v1.angleTo(c.tx, c.ty), unit.rotation + 180f, angleLimit);
                Tmp.v2.trns(c.tr, c.length).add(Tmp.v1);
            }else{
                FlagellaSegment p = c.prev;
                c.tr = Utils.clampedAngle(Angles.angle(p.tx, p.ty, c.tx, c.ty), p.tr, angleLimit);
                Tmp.v2.trns(c.tr, c.length).add(p.tx, p.ty);
            }
            c.tx = Tmp.v2.x;
            c.ty = Tmp.v2.y;

            c = c.next;
            idx++;
        }
        idx = 0;
        c = d.root;
        while(c != null){
            float rot = c.tr + swayAngle(d, idx);

            if(c.prev == null){
                Tmp.v2.trns(rot, segmentLength).add(Tmp.v1);
            }else{
                FlagellaSegment p = c.prev;
                Tmp.v2.trns(rot, segmentLength).add(p.x, p.y);
            }
            c.x = Tmp.v2.x;
            c.y = Tmp.v2.y;

            c = c.next;
            idx++;
        }
    }

    @Override
    public void draw(Unit unit, UnitDecoration deco){
        FlagellaDecoration d = (FlagellaDecoration)deco;
        FlagellaSegment cur = d.end;

        Tmp.v1.trns(unit.rotation - 90f, x, y).add(unit);
        int regL = regions.length - 1;
        int idx = 0;
        while(cur != null){
            int reg = Mathf.clamp(regL - Mathf.round((idx / (float)segments) * regL), 0, regL);
            TextureRegion region = cur == d.end ? end : regions[reg];

            unit.type.applyColor(unit);
            Lines.stroke(region.height * Draw.scl);
            if(cur.prev == null){
                Tmp.v2.set(cur.x, cur.y).sub(Tmp.v1).setLength(region.width * Draw.scl).add(Tmp.v1);
                Lines.line(region, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
            }else{
                FlagellaSegment pr = cur.prev;
                Tmp.v2.set(cur.x, cur.y).sub(pr.x, pr.y).setLength(region.width * Draw.scl).add(pr.x, pr.y);
                Lines.line(region, pr.x, pr.y, Tmp.v2.x, Tmp.v2.y, false);
            }

            idx++;
            cur = cur.prev;
        }
        Draw.reset();
    }

    float offset(FlagellaDecoration d, int idx){
        return Mathf.cosDeg(swayAngle(d, idx)) * segmentLength;
    }

    float swayAngle(FlagellaDecoration d, int idx){
        return Mathf.sin(d.progress - (idx * swayOffset), swayScl, Mathf.lerp(startIntensity, endIntensity, idx / (segments - 1f)));
    }

    @Override
    public void added(Unit unit, UnitDecoration deco){
        FlagellaDecoration d = (FlagellaDecoration)deco;

        float ox = Angles.trnsx(unit.rotation + 180f, segmentLength),
        oy = Angles.trnsy(unit.rotation + 180f, segmentLength);

        Tmp.v1.trns(unit.rotation - 90f, x, y).add(unit);

        FlagellaSegment last = null;
        for(int i = 0; i < segments; i++){
            FlagellaSegment c = new FlagellaSegment();
            c.tx = (ox * (i + 1f)) + Tmp.v1.x;
            c.ty = (oy * (i + 1f)) + Tmp.v1.y;
            c.tr = unit.rotation + 180f;
            c.length = segmentLength;
            if(last == null){
                d.root = c;
            }else{
                c.prev = last;
                last.next = c;
            }
            d.end = c;

            last = c;
        }
    }

    static class FlagellaDecoration extends UnitDecoration{
        float progress;
        FlagellaSegment root, end;

        public FlagellaDecoration(UnitDecorationType type){
            super(type);
        }
    }

    static class FlagellaSegment{
        float tx, ty, tr, length;
        float x, y;

        FlagellaSegment next, prev;
    }
}
