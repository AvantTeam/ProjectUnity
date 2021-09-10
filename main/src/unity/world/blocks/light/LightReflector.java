package unity.world.blocks.light;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** @author GlennFolker */
@Merge(LightHoldc.class)
public class LightReflector extends LightHoldBlock{
    /** Strength scale of lights that can fall through the mirror. >=0 disables light division */
    public float fallthrough = 0f;

    public TextureRegion baseRegion;

    public LightReflector(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;
        outlineIcon = true;

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
        public void interact(Light light){
            light.child(l -> {
                Tmp.v1.trnsExact(lightRot, 1f);
                return Float2.construct(Tmp.v2
                    .trnsExact(l.realRotation(), 1f)
                    .sub(Tmp.v1.scl(2 * Tmp.v2.dot(Tmp.v1)))
                    .angle(), 1f - fallthrough
                );
            });

            if(!Mathf.zero(fallthrough)){
                light.child(l -> Float2.construct(l.realRotation(), fallthrough));
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
