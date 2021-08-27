package unity.type.decal;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import unity.util.*;

public class WingDecorationType extends UnitDecorationType{
    public float flapScl = 120f;
    public Interp flapAnimation = Interp.pow3Out, flapInterp = Interp.sine;
    public TextureRegion[] textures;
    public String name;
    public Seq<Wing> wings = new Seq<>();
    public int textureVariants;

    public WingDecorationType(String name, int variants){
        decalType = WingDecoration::new;
        this.name = name;
        textureVariants = variants;
    }

    @Override
    public void load(){
        textures = new TextureRegion[textureVariants];
        for(int i = 0; i < textureVariants; i++){
            textures[i] = Core.atlas.find(name + "-" + i);
        }
    }

    @Override
    public void draw(Unit unit, UnitDecoration deco){
        WingDecoration wd = (WingDecoration)deco;
        unit.type.applyColor(unit);
        for(Wing wing : wings){
            float l = slope(((wd.left / flapScl) + wing.offset) % 1f) * -wing.mag;
            float r = slope(((wd.right / flapScl) + wing.offset) % 1f) * -wing.mag;

            for(int s : Mathf.signs){
                float side = s > 0 ? r : l,
                rotation = unit.rotation - 90f,
                x = Angles.trnsx(rotation, wing.x * s, wing.y) + unit.x,
                y = Angles.trnsy(rotation, wing.x * s, wing.y) + unit.y,
                wingAngle = rotation + (side * s);
                TextureRegion region = textures[wing.textureIndex];

                Draw.rect(region, x, y, region.width * s * Draw.scl, region.height * Draw.scl, wingAngle);
            }
        }
        Draw.reset();
    }

    @Override
    public void drawIcon(Func<TextureRegion, Pixmap> prov, Pixmap icon, Func<TextureRegion, TextureRegion> outliner){
        for(Wing w : wings){
            TextureRegion region = outliner.get(textures[w.textureIndex]);

            float scl = Draw.scl / 4f;
            Pixmap pix = prov.get(region);

            icon.draw(pix,
            (int)(w.x / scl + icon.width / 2f - pix.width / 2f),
            (int)(-w.y / scl + icon.height / 2f - pix.height / 2f),
            true);

            icon.draw(pix.flipX(),
            (int)(-w.x / scl + icon.width / 2f - pix.width / 2f),
            (int)(-w.y / scl + icon.height / 2f - pix.height / 2f),
            true);
        }
    }

    float slope(float in){
        return flapInterp.apply((0.5f - Math.abs(flapAnimation.apply(in) - 0.5f)) * 2f);
    }

    @Override
    public void update(Unit unit, UnitDecoration deco){
        WingDecoration wd = (WingDecoration)deco;
        if(unit.moving()){
            float len = unit.deltaLen();
            wd.left += len;
            wd.right += len;
        }
        float angDst = Utils.angleDistSigned(unit.rotation, wd.lastRot);
        if(Math.abs(angDst) > 0.0001f){
            if(angDst > 0){
                wd.right += angDst;
            }else{
                wd.left += -angDst;
            }
        }else{
            //float mid = (((wd.left % 1f) + (wd.right % 1f)) / 2f);
            float mid = Math.max(wd.left % flapScl, wd.right % flapScl);
            wd.left = Mathf.lerpDelta(wd.left, Mathf.round(wd.left, flapScl) + mid, 0.08f);
            wd.right = Mathf.lerpDelta(wd.right, Mathf.round(wd.right, flapScl) + mid, 0.08f);
        }
        wd.lastRot = unit.rotation;
    }

    @Override
    public void added(Unit unit, UnitDecoration deco){
        WingDecoration wd = (WingDecoration)deco;
        wd.lastRot = unit.rotation;
    }

    public static class Wing{
        float offset, mag, x, y;
        int textureIndex;

        public Wing(int idx, float x, float y, float offset, float mag){
            textureIndex = idx;
            this.x = x;
            this.y = y;
            this.offset = offset;
            this.mag = mag;
        }
    }

    public static class WingDecoration extends UnitDecoration{
        float left = 0f, right = 0f, lastRot;

        public WingDecoration(UnitDecorationType type){
            super(type);
        }
    }
}
