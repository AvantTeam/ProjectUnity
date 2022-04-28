package unity.type;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

/**
 * Objectified unit engines.
 * @author GlennFolker
 */
public class Engine{
    public float offset = 5f;
    public float size = 2.5f;
    public float trailScale = 1f;
    public Color color = null, innerColor = Color.white;

    public boolean drawTrail = true;

    public Engine apply(UnitType type){
        type.engineOffset = offset;
        type.engineSize = size;
        type.trailScl = trailScale;
        type.engineColor = color;
        type.engineColorInner = Color.white;
        return this;
    }

    public void draw(Unit unit){
        draw(unit, unit.x, unit.y);
    }

    public void draw(Unit unit, float x, float y){
        float scale = unit.elevation;
        float offset = this.offset / 2f + this.offset / 2f * scale;

        if(drawTrail && unit instanceof Trailc t){
            float trailSize = (size + Mathf.absin(Time.time, 2f, size / 4f) * scale) * trailScale;

            Trail trail = t.trail();
            trail.draw(unit.team.color, trailSize);
            trail.drawCap(unit.team.color, trailSize);
        }

        Draw.color(color == null ? unit.team.color : color);
        Fill.circle(
            x + Angles.trnsx(unit.rotation + 180f, offset),
            y + Angles.trnsy(unit.rotation + 180f, offset),
            (size + Mathf.absin(Time.time, 2f, size / 4f)) * scale
        );
        Draw.color(innerColor);
        Fill.circle(
            x + Angles.trnsx(unit.rotation + 180f, offset - 1f),
            y + Angles.trnsy(unit.rotation + 180f, offset - 1f),
            (size + Mathf.absin(Time.time, 2f, size / 4f)) / 2f * scale
        );
        Draw.color();
    }

    public static class MultiEngine extends Engine{
        public EngineHold[] engines;

        public MultiEngine(EngineHold... engines){
            this.engines = engines;
            for(EngineHold engine : this.engines){
                engine.engine.drawTrail = false; // Avoid rendering the trail multiple times.
            }
        }

        @Override
        public void draw(Unit unit, float x, float y){
            if(drawTrail && unit instanceof Trailc t){
                Trail trail = t.trail();
                trail.draw(unit.team.color, (size + Mathf.absin(Time.time, 2f, size / 4f) * unit.elevation) * trailScale);
            }

            for(EngineHold engine : engines){
                float
                    ox = Angles.trnsx(unit.rotation - 90f, engine.offsetX),
                    oy = Angles.trnsy(unit.rotation - 90f, engine.offsetX);

                engine.engine.draw(unit, x + ox, y + oy);
            }
        }

        public static class EngineHold{
            public final Engine engine;
            public final float offsetX;

            public EngineHold(Engine engine, float offsetX){
                this.engine = engine;
                this.offsetX = offsetX;
            }
        }
    }
}
