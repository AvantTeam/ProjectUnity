package unity.world.blocks.defense.turrets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.annotations.Annotations.*;
import unity.assets.type.g3d.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.content.effects.*;
import unity.gen.*;
import unity.graphics.*;
import unity.util.*;

import static mindustry.Vars.*;

@Merge(base = PowerTurret.class, value = Soulc.class)
public class PrismTurret extends SoulPowerTurret{
    public Model model;
    public float prismOffset = 10f;
    public float prismRotateSpeed = 20f;
    public float scale = 0.6f;

    public Color fromColor = UnityPal.monolithDark;
    public Color toColor = UnityPal.monolith;

    public Effect damageEffect = SpecialFx.chainLightningActive;
    public float warmup = 0.1f;

    public int maxShots = 5;
    public float shootRate = 2f;
    public float sortRange = tilesize * 5f;

    public PrismTurret(String name){
        super(name);
        unitSort = (unit, x, y) -> Groups.unit.intersect(x - sortRange, y - sortRange, sortRange * 2f, sortRange * 2f).count(u -> u.within(unit, sortRange) && u.checkTarget(targetAir, targetGround));
    }

    public class PrismTurretBuild extends SoulPowerTurretBuild{
        public ModelInstance inst;

        public float prismHeat = 0f;
        public float prismRotation = 0f;

        protected Seq<Posc> targets = new Seq<>(false);

        @Override
        public void created(){
            super.created();
            inst = new ModelInstance(model);
        }

        @Override
        public void updateTile(){
            super.updateTile();

            boolean act = isActive();
            prismHeat = Mathf.lerpDelta(prismHeat, act ? efficiency() : 0f, act ? warmup : cooldown);
            prismRotation += prismHeat * prismRotateSpeed * Mathf.signs[id % 2];

            inst.transform.set(
                Tmp.v31.set(
                    x + Angles.trnsx(rotation, prismOffset - recoil),
                    y + Angles.trnsy(rotation, prismOffset - recoil),
                    0f
                ),
                Utils.q1
                    .setFromAxis(0f, 0f, 1f, rotation - 90f)
                    .mul(Utils.q2.setFromAxis(0f, 1f, 0f, prismRotation)),
                Tmp.v32.set(scale, scale, scale)
            );

            color().set(fromColor).lerp(toColor, prismHeat + Mathf.sin(4f, 0.1f) * prismHeat);
        }

        @Override
        protected void findTarget(){
            super.findTarget();

            targets.clear().add(target);
            for(int i = 0; i < maxShots; i++){
                Teamc t = Units.closestTarget(team, targetPos.x, targetPos.y, sortRange,
                    u -> u != target && u.checkTarget(targetAir, targetGround) && !targets.contains(u),
                    b -> b != target && targetGround && !targets.contains(b)
                );

                if(t != null) targets.add(t);
            }
        }

        public Color color(){
            return inst.getMaterial().<ColorAttribute>get(ColorAttribute.diffuse).color;
        }

        @Override
        protected void shoot(BulletType type){
            for(int i = 0; i < targets.size; i++){
                Posc u = targets.get(i);
                Time.run(i / shootRate, () -> {
                    if(!isValid() || u == null || !(u instanceof Healthc h ? h.isValid() : u.isAdded())) return;

                    float angle = angleTo(u);
                    shootType.create(this, u.x(), u.y(), angle);

                    heat = 1f;

                    damageEffect.at(
                        x + Angles.trnsx(rotation, prismOffset - recoil),
                        y + Angles.trnsy(rotation, prismOffset - recoil),
                        2f,
                        color(), u
                    );

                    type.hitEffect.at(u.x(), u.y(), angle);

                    effects();
                });
            }
        }

        @Override
        public void draw(){
            super.draw();
            Draw.draw(Draw.z(), inst::render);
        }
    }
}
