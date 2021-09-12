package unity.world.blocks.light;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.*;

/** @author GlennFolker */
@Merge(LightHoldc.class)
public class LightReflector extends LightHoldBlock{
    private static final Vec2 v1 = new Vec2(), v2 = new Vec2();

    /** Strength scale of lights that can fall through the mirror. >=0 disables light division */
    public float fallthrough = 0f;

    public TextureRegion baseRegion;

    public LightReflector(String name){
        super(name);
        solid = true;
        configurable = true;
        outlineIcon = true;

        // I'm aware that this will only be valid for 1x1 reflectors, but what kind of a psychopath that needs a
        // larger reflectors
        acceptors.add(new LightAcceptorType(){{
            x = 0;
            y = 0;
            width = 1;
            height = 1;
            required = -1f;
        }});

        config(Boolean.class, (LightReflectorBuild tile, Boolean value) -> tile.lightRot = Mathf.mod(tile.lightRot + (value ? Light.rotationInc : -Light.rotationInc) / 2f, 360f));
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }

    @Override
    public float getRotation(Building build){
        return build instanceof LightReflectorBuild b ? b.lightRot : 0f;
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class LightReflectorBuild extends LightHoldBuild{
        public float lightRot = 90f;

        @Override
        public Float config(){
            return lightRot;
        }

        @Override
        public void interact(Light light){
            super.interact(light);

            light.child(l -> {
                synchronized(LightReflector.class){
                    v1.trnsExact(lightRot, 1f);
                    return Float2.construct(Light.fixRot(v2
                        .trnsExact(l.rotation(), 1f)
                        .sub(v1.scl(2 * v2.dot(v1)))
                        .angle()), 1f - fallthrough
                    );
                }
            });

            if(!Mathf.zero(fallthrough)){
                light.child(l -> Float2.construct(l.rotation(), fallthrough));
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.left, () -> configure(true)).size(40f);
            table.button(Icon.right, () -> configure(false)).size(40f);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(lightRot);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lightRot = read.f();
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.rect(region, x, y, lightRot - 90f);
        }
    }
}
