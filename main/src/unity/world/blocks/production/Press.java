package unity.world.blocks.production;

import arc.*;
import arc.audio.*;
import arc.math.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.world.blocks.production.*;
import unity.content.*;
import unity.gen.*;

import static mindustry.Vars.*;

public class Press extends GenericCrafter {
    public float movementSize = 10f;
    public float fxYVariation = 15f / tilesize;
    public Sound clangSound = UnitySounds.clang;
    public Effect sparkEffect = UnityFx.spark;
    public TextureRegion leftRegion, rightRegion, baseRegion;

    public Press(String name){
        super(name);
        update = true;
        updateEffectChance = 0f;
    }

    @Override
    public void load(){
        super.load();
        leftRegion = Core.atlas.find(name + "-left");
        rightRegion = Core.atlas.find(name + "-right");
        baseRegion = Core.atlas.find(name + "-base");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, leftRegion, rightRegion};
    }

    public class PressBuilding extends GenericCrafterBuild {
        public float realMovementSize = movementSize / tilesize;
        public float alphaValueMax = 0.4f;
        public float alphaValue = 0f;

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color(Team.crux.color);
            if(alphaValue > 0f) {
                Draw.alpha(alphaValue);
                for (var i = 0; i < 10; i++) {
                    Fill.circle(x, y, i * 0.6f + Mathf.sin((totalProgress + Time.time) / 16f) / 3f);
                }
            }
            Draw.color();
            Draw.rect(leftRegion, x - Math.abs(Mathf.sin(Mathf.clamp(progress * 1.2f - 0.2f, 0, 1) / 2 * 360 * Mathf.degreesToRadians)) * realMovementSize, y);
            Draw.rect(rightRegion, x + Math.abs(Mathf.sin(Mathf.clamp(progress * 1.2f - 0.2f, 0, 1) / 2 * 360 * Mathf.degreesToRadians)) * realMovementSize, y);
            Draw.rect(region, x, y);
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(efficiency() > 0.001f){
                alphaValue += 0.01f;
            } else {
                alphaValue -= 0.01f;
            }
            alphaValue = Mathf.clamp(alphaValue, 0f, alphaValueMax);
        }

        @Override
        public void consume(){
            super.consume();

            clangSound.at(x, y, Mathf.random(0.6f, 0.8f));

            for(int i = 0; i < 8; i++){
                sparkEffect.at(x,  y + Mathf.range(fxYVariation), Mathf.random() * 360, Items.surgeAlloy.color, Mathf.random() + 0.5f);
            }
        }
    }
}
