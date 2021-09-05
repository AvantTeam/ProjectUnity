package unity.world.blocks.light;

import arc.*;
import arc.graphics.g2d.*;
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

    public float angleRange = 22.5f;
    public float rotateSpeed = 5f;

    public TextureRegion baseRegion;

    public LightReflector(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;
        outlineIcon = true;

        config(Boolean.class, (LightReflectorBuild tile, Boolean value) -> tile.lightRot += value ? 1 : -1);
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }

    @Override
    public float getRotation(Building build){
        if(build instanceof LightReflectorBuild b){
            return Light.unpackRot(b.lightRot);
        }else{
            return super.getRotation(build);
        }
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class LightReflectorBuild extends LightHoldBuild{
        public byte lightRot = Light.packRot(90f);

        @Override
        public void interact(Light light){
            light.child(l -> {
                synchronized(LightReflector.class){
                    v1.trnsExact(Light.unpackRot(lightRot), 1f);
                    return Light.packRot(v2
                        .trnsExact(l.realRotation(), 1f)
                        .sub(v1.scl(2 * v2.dot(v1)))
                        .angle()
                    );
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
            write.b(lightRot);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lightRot = read.b();
        }

        @Override
        public void draw(){
            super.draw();
            Draw.rect(region, x, y, Light.unpackRot(lightRot));
        }
    }
}
