package unity.world.blocks.power;

import arc.*;
import arc.math.*;
import arc.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;

import static mindustry.Vars.*;

public class Absorber extends PowerGenerator {
    public float range = 50f;
    public int capacity = 5;
    public float powerProduction = 1.2f;
    public StatusEffect status = StatusEffects.slow;
    public TextureRegion laserRegion, laserEndRegion, baseRegion;
    public float rotateSpeed = 2f;
    public float cone = 2f;
    public float damagePerTick = 1f;

    public Absorber(String name){
        super(name);
        update = true;
        outlineIcon = true;
    }

    @Override
    public void load(){
        super.load();

        laserRegion = Core.atlas.find("laser");
        laserEndRegion = Core.atlas.find("laser-end");
        baseRegion = Core.atlas.find("block-" + size);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class AbsorberBuilding extends GeneratorBuild {
        public Unit unit;
        public float time = 0f;
        public float rotation = 90f;
        public float targetRot = 90f;
        public boolean canAbsorb = false;

        @Override
        public void update(){
            super.update();

            canAbsorb = Angles.angleDist(rotation, targetRot) <= cone;

            unit = Units.closestEnemy(this.team, this.x, this.y, range, e -> !e.dead() && !e.isFlying());

            if(unit != null){
                targetRot = Angles.angle(x, y, unit.x, unit.y);

                turnToTarget(targetRot);

                if(canAbsorb){
                    unit.apply(status);
                    unit.damage(damagePerTick);
                }
            }

            turnToTarget(targetRot);

            productionEfficiency = unit == null ? 0f : 1f;

            time += 1f / 60f * Time.delta;
        }

        @Override
        public float getPowerProduction(){
            return powerProduction * productionEfficiency;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public void draw(){
            super.draw();

            Draw.z(Layer.block);

            Draw.rect(baseRegion, x, y);
            Draw.rect(region, x, y, rotation - 90f);

            if(unit == null || !canAbsorb) return;

            Tmp.v1.set(0, 0).trns(rotation, size * tilesize / 2f);

            Draw.z(Layer.flyingUnit - 1);
            Draw.color(unit.team.color);
            Lines.circle(unit.x, unit.y, unit.hitSize + Mathf.sin(time * 2f));
            Draw.color(Color.white);
            Drawf.laser(team, laserRegion, laserEndRegion, x + Tmp.v1.x + Mathf.sin(time * 2), y + Tmp.v1.y + Mathf.cos(time * 2), unit.x, unit.y, 0.6f);

            Draw.reset();
        }

        public void turnToTarget(float targetRot){
            rotation = Angles.moveToward(rotation, targetRot, rotateSpeed * Time.delta);
        }
    }
}
