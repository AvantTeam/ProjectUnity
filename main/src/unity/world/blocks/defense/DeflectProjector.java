package unity.world.blocks.defense;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import unity.content.*;
import unity.entities.*;
import unity.entities.comp.*;
import unity.gen.*;
import unity.graphics.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class DeflectProjector extends Block{
    public final int timerUse = timers++;

    public float radius = 8f * tilesize;
    public float deflectTime = 30f;
    public float deflectPower = 1f;

    public float warmup = 0.01f;

    public Color shieldColor = Pal.lancerLaser;

    public TextureRegion topRegion;

    /** Static reference to building to avoid memory allocation */
    private static DeflectProjectorBuild build;
    static final Cons<Bullet> deflector = b -> {
        if(b.team != build.team && b.type.absorbable && b.within(build, build.radf())){
            Tmp.v1.set(b.x - build.x, b.y - build.y).nor();
            b.vel.sub(Tmp.v1.scl(2f * b.vel.dot(Tmp.v1)));

            build.hit = 1f;
            build.deflected = true;
        }
    };

    public DeflectProjector(String name){
        super(name);
        update = true;
        solid = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasLiquids = true;
        hasItems = true;
        ambientSound = Sounds.shield;
        ambientSoundVolume = 0.08f;
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    public class DeflectProjectorBuild extends Building implements ExtensionHolder, Ranged{
        protected Extension ext;

        public float heat;
        public float hit;
        protected boolean deflected;

        @Override
        public void created(){
            super.created();
            ext = Extension.create();
            ext.holder = this;
            ext.set(x, y);
            ext.add();
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            ext.remove();
        }

        @Override
        public float clipSizeExt(){
            return radf() * 2f;
        }

        @Override
        public void drawExt(){
            float rad = radf();

            float z = Draw.z();
            Draw.z(UnityShaders.holoShield.getLayer());
            Draw.color(shieldColor, Color.white, Mathf.clamp(hit));

            if(Core.settings.getBool("animatedshields")){
                Fill.circle(x, y, rad);
            }else{
                Lines.stroke(1.5f);
                Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                Fill.circle(x, y, rad);
                Draw.alpha(1f);
                Lines.circle(x, y, rad);
                Draw.reset();
            }

            Draw.z(z);
            Draw.reset();
        }

        public float radf(){
            return radius * heat;
        }

        @Override
        public void updateTile(){
            super.updateTile();

            heat = Mathf.lerpDelta(heat, efficiency(), warmup);
            if(timer.get(timerUse, deflectTime)){
                deflected = false;

                build = this;
                Groups.bullet.intersect(x - radf(), y - radf(), radf() * 2f, radf() * 2f, deflector);
            }

            if(!deflected){
                timer.reset(timerUse, 0f);
            }

            if(hit > 0f){
                hit = Mathf.lerpDelta(hit, 0f, 0.01f);
            }
        }

        @Override
        public boolean shouldAmbientSound(){
            return radf() > 1f;
        }

        @Override
        public float range(){
            return radf();
        }
    }
}
