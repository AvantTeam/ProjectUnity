package unity.content;

import arc.math.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import unity.content.effects.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.entities.weapons.*;
import unity.gen.assets.*;
import unity.gen.entities.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.graphics.trail.*;
import unity.mod.*;

import static mindustry.Vars.*;
import static unity.gen.entities.EntityRegistry.*;
import static unity.mod.FactionRegistry.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} unit types.
 * @author GlennFolker
 */
public final class MonolithUnitTypes{
    public static PUUnitType stray;

    private MonolithUnitTypes(){
        throw new AssertionError();
    }

    public static void load(){
        stray = register(Faction.monolith, content("stray", MonolithUnit.class, n -> new PUUnitType(n){{
            health = 300f;
            speed = 5f;
            accel = 0.08f;
            drag = 0.045f;
            rotateSpeed = 8f;
            flying = true;
            hitSize = 12f;
            lowAltitude = true;
            faceTarget = false;
            outlineColor = Palettes.darkOutline;

            engineColor = Palettes.monolithLight;
            engineSize = 2.5f;
            engineOffset = 12.5f;
            setEnginesMirror(new UnitEngine(4.5f, -10f, 1.5f, -90f));

            trail(20, unit -> {
                TexturedTrail right = MonolithTrails.singlePhantasmal(20, new VelAttrib(0.3f, 0f, (t, v) -> unit.rotation, 0.1f));
                right.trailChance = 0f;
                right.fadeInterp = e -> (1f - Interp.pow2Out.apply(Mathf.curve(e, 0.84f, 1f))) * Interp.pow2In.apply(Mathf.curve(e, 0f, 0.56f));
                right.sideFadeInterp = e -> (1f - Interp.pow3Out.apply(Mathf.curve(e, 0.7f, 1f))) * Interp.pow3In.apply(Mathf.curve(e, 0f, 0.7f));

                TexturedTrail left = right.copy();
                left.attrib(VelAttrib.class).velX *= -1f;

                return new MultiTrail(BaseTrail.rot(unit),
                    new TrailHold(MonolithTrails.phantasmal(BaseTrail.rot(unit), 16, 3.6f, 6f, speed, 2f), engineColor),
                    new TrailHold(right, 4.5f, 2.5f, 0.44f, Palettes.monolithLight),
                    new TrailHold(left, -4.5f, 2.5f, 0.44f, Palettes.monolithLight)
                );
            });

            prop(new MonolithProps(){{
                maxSouls = 3;
                soulLackStatus = content.getByName(ContentType.status, "unity-disabled");
            }});

            weapons.add(new EnergyRingWeapon(){{
                rings.add(new Ring(){{
                    radius = 5.5f;
                    thickness = 1f;
                    spikes = 4;
                    spikeOffset = 1.5f;
                    spikeWidth = 2f;
                    spikeLength = 4f;
                    color = Palettes.monolithDark.cpy().lerp(Palettes.monolith, 0.5f);
                }}, new Ring(){{
                    shootY = radius = 2.5f;
                    rotate = false;
                    thickness = 1f;
                    divisions = 2;
                    divisionSeparation = 30f;
                    angleOffset = 90f;
                    color = Palettes.monolith;
                }});

                x = y = 0f;
                mirror = false;
                rotate = true;
                reload = 60f;
                inaccuracy = 30f;
                layerOffset = 10f;
                eyeRadius = 1.8f;

                shootSound = PUSounds.energyBolt;
                shoot = new ShootPattern(){{
                    shots = 6;
                    shotDelay = 1f;
                }};

                bullet = new BasicBulletType(1f, 6f, "shell"){
                    {
                        drag = -0.08f;
                        lifetime = 35f;
                        width = 8f;
                        height = 13f;

                        homingDelay = 6f;
                        homingPower = 0.09f;
                        homingRange = 160f;
                        weaveMag = 6f;
                        keepVelocity = false;

                        frontColor = trailColor = Palettes.monolith;
                        backColor = Palettes.monolithDark;
                        trailChance = 0.3f;
                        trailParam = 1.5f;
                        trailWidth = 2f;
                        trailLength = 12;

                        shootEffect = MonolithShootFx.strayShoot;
                        hitEffect = despawnEffect = Fx.hitLancer;
                    }

                    @Override
                    public void updateTrail(Bullet b){
                        if(!headless && b.trail == null) b.trail = MonolithTrails.singlePhantasmal(trailLength);
                        super.updateTrail(b);
                    }

                    @Override
                    public void removed(Bullet b){
                        super.removed(b);
                        b.trail = null;
                    }
                };
            }});
        }}));
    }
}
