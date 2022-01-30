package unity.world.blocks.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class TimeMine extends Block {
    public float range = 2 * tilesize;
    public float reload = 30f, teleportRange = 500f;
    public Effect tpEffect;
    public float lifeTime = 180f;

    public TimeMine(String name) {
        super(name);

        update = sync = configurable = true;
        logicConfigurable = solid = rotate = noUpdateDisabled = false;
        size = 1;
        hasPower = hasItems = hasLiquids = false;
        timers = 2;

        config(Integer.class, (TimeMineBuild entity, Integer value) -> {
            Building other = world.build(value);
            TimeMineBuild otherB = (TimeMineBuild) other;
            if (entity.teleporter == value){
                entity.teleporter = -1;
                otherB.fromPos = -1;
            }else if(entity.tpValid(entity, other)){
                entity.teleporter = other.pos();
                otherB.fromPos = entity.pos();
            }

            /* linked to each other */
            if (entity.teleporter == value && otherB.teleporter == entity.pos()){
                otherB.teleporter = -1;
                entity.fromPos = -1;
                entity.teleporter = other.pos();
                otherB.fromPos = entity.pos();
            }
        });
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
        Drawf.dashCircle(x * tilesize, y * tilesize, teleportRange, Pal.accent);

        Draw.reset();
    }

    public class TimeMineBuild extends Building{
        public Building dest, from;
        public Seq<Unit> teleportUnit = new Seq<>();
        public int teleporter = -1, fromPos = -1;

        @Override
        public void updateTile(){
            if (teleporter != -1) dest = world.build(teleporter);
            if (fromPos != -1) from = world.build(fromPos);

            if(connected() && unitCount(team) >= 3){
                Units.nearbyEnemies(team, x, y, range, e -> {
                    e.impulseNet(Tmp.v1.trns(e.angleTo(this), e.dst(this) - e.vel.len()).scl(Time.delta * Mathf.floor(Mathf.pow(e.mass(), Mathf.lerpDelta(0.2f, 0.5f, 0.3f / lifeTime)))));
                    e.disarmed = true;
                    if (e.dst(this) <= 4f){
                        e.set(this);
                        e.vel.limit(0.01f);
                        if (!teleportUnit.contains(e) && teleportUnit.size < 5) teleportUnit.add(e);
                    }
                });

                if (teleportUnit.size > 0) {
                    if (timer(0, reload)) {
                        for (Unit toTeleport: teleportUnit){
                            teleport(toTeleport);
                            teleportUnit.remove(toTeleport);
                        }
                        if (tpEffect != null) tpEffect.at(x, y);
                        timer.reset(0, 0);
                    }
                }else{
                    timer.reset(0,0);
                }

            }else{
                timer.reset(1, 0);
            }

            if (timer.getTime(1) >= 180f){
                kill();
            }
        }

        /* adding links */
        @Override
        public boolean onConfigureTileTapped(Building other){
            if (tpValid(this, other)){
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public void drawConfigure(){
            if (dest != null && teleporter != -1){
                Drawf.circles(dest.x, dest.y, 16f, Pal.accent);
                Drawf.arrow(x, y, dest.x, dest.y, 12f, 6f);
            }

            Drawf.dashCircle(x, y, teleportRange, Pal.accent);
        }

        /* whether this mine is connected to a teleporter. */
        public boolean connected(){
            return teleporter != -1 && dest == world.build(teleporter);
        }

        public boolean tpValid(Building tile, Building link){
            return tile != link && tile.dst(link) <= teleportRange && link != null && tile.team == link.team && !link.dead() && link instanceof TimeMine.TimeMineBuild;
        }

        public int unitCount(Team t){
            return Units.count(x, y, 2 * tilesize, 2*tilesize, e -> e.team != t);
        }

        public void teleport(Unit unit){
            unit.set(dest.x, dest.y);
            if (unit.isPlayer() && unit.getPlayer() == Vars.player && !Vars.headless) Core.camera.position.set(dest.x, dest.y);
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(teleporter);
            write.i(fromPos);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            teleporter = read.i();
            fromPos = read.i();
        }
    }
}