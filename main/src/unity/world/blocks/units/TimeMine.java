package unity.world.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class TimeMine extends Block {
    public float range = 1.5f * tilesize;
    public float pullTime = 300f;
    public float shake = 2f;

    public TimeMine(String name) {
        super(name);

        configurable = update = sync = solid = true;
        hasItems = hasPower = hasLiquids = noUpdateDisabled = targetable = false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
        Draw.reset();
    }

    public class TimeMineBuild extends Building implements Ranged{
        boolean pulling = false;

        @Override
        public float range(){
            return range;
        }

        @Override
        public void updateTile(){
            pulling = Units.count(x, y, 24f, u -> !u.dead() && u.team != team && u.dst(this) <= range) > 0 && !dead;

            if (pulling){
                Units.nearbyEnemies(team, x, y, range, u -> {
                    u.vel.trns(u.angleTo(this), u.dst(this));
                    u.vel.limit(0.2f);
                });
                Effect.shake(shake * 2, shake, this);
            }else{
                timer.reset(0,0);
            }

            if (timer(0, pullTime)) kill();
        }

        @Override
        public void draw(){
            super.draw();

            if (pulling){
                Draw.color(Color.black, Pal.darkerMetal, 0.2f);
                Fill.circle(x, y, timer.getTime(0)/25);
            }
        }
    }
}
