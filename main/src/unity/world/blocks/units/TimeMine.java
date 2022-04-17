package unity.world.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class TimeMine extends Block {
    public float maxRange = 2f * tilesize;
    public float rangeScale = 2f;
    public float pullTime = 300f;
    public float shake = 1f;
    public float force = 15f;
    public float forceScale = 25f;
    public int maxUnits = 5;

    public TimeMine(String name) {
        super(name);

        update = true;
        solid = targetable = hasItems = hasPower = hasLiquids = false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize, y * tilesize, maxRange, Pal.accent);
        Drawf.dashCircle(x * tilesize, y * tilesize, maxRange * rangeScale, Pal.lancerLaser);
        Draw.reset();
    }

    public class TimeMineBuild extends Building implements Ranged{
        @Nullable Unit[] pulledUnits = new Unit[maxUnits];
        float heat = 0f;
        float range = maxRange;

        @Override
        public float range(){
            return range;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            Drawf.dashCircle(x, y, range, team.color);
        }

        public int getPullCount() {
            int count = 0;
            for(Unit unit : pulledUnits) {
                if (unit == null||unit.dst(this)>range) continue;
                count++;
            }
            return count;
        }

        public boolean isPulling() {
            return getPullCount() > 0 && !dead();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            int[] i = {0};

            Units.nearbyEnemies(team, x, y, range, unit -> {
                if(i[0]<maxUnits) pulledUnits[i[0]++] = unit;
            });

            if (isPulling()){
                if(heat < pullTime) heat += delta();
                else kill();

                if(range < maxRange * rangeScale) range = Mathf.lerpDelta(range, maxRange * rangeScale, 2/pullTime);
                for(Unit unit : pulledUnits) {
                    if(unit==null||unit.dst(this)>range) continue;
                    unit.apply(StatusEffects.muddy, pullTime-heat);
                    unit.impulseNet(Tmp.v1.set(this).sub(unit).limit((force + (1f - unit.dst(this) / range) * forceScale) * delta()));
                }
                damage(0.1f);
                Effect.shake(getPullCount()/(maxUnits*1f)*shake, 1, this);
            }
            else {
                if(heat > 0) heat -= delta();
                if(range > maxRange) range = Mathf.lerpDelta(maxRange, range, 2/pullTime);
            }
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(Color.black, Pal.darkerMetal, 0.4f);
            Draw.alpha(0.7f);
            Fill.circle(x, y, heat/pullTime * range);

            for(Unit unit : pulledUnits) {
                if(unit==null||unit.dst(this)>range) continue;
                float fin = 0.75f * unit.dst(this)/range;
                float lope = (0.5f - Math.abs(fin - 0.5f)) * 1.25f;
                Draw.color(Pal.lancerLaser, Color.black, fin);
                Lines.stroke(0.25f + lope * (1-fin));
                Lines.square(unit.x, unit.y,  unit.hitSize * 1.25f, 45);
            }
        }
    }
}
