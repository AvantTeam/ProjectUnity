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

/** @author GlennFolker */
@Merge(LightHoldc.class)
public class LightReflector extends LightHoldBlock{
    private static final Vec2 v1 = new Vec2(), v2 = new Vec2();

    public TextureRegion baseRegion;

    public LightReflector(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;
        outlineIcon = true;

        config(Boolean.class, (LightReflectorBuild tile, Boolean value) -> tile.lightRot = Mathf.mod(tile.lightRot + (value ? Light.rotationInc : -Light.rotationInc), 360f));
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
        public void interact(Light light){
            light.child(l -> {
                synchronized(LightReflector.class){
                    v1.trnsExact(lightRot, 1f);
                    return v2
                        .trnsExact(l.rotation(), 1f)
                        .sub(v1.scl(2 * v2.dot(v1)))
                        .angle();
                }
            });
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
