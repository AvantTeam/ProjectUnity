package unity.world.blocks.defense;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.content.*;
import unity.entities.*;
import unity.entities.comp.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public class DeflectProjector extends Block{
    public final int timerUse = timers++;

    public float radius = 8f * tilesize;
    public float deflectTime = 30f;
    public float deflectPower = 1f;

    public float warmup = 0.01f;

    public Color shieldColor = Pal.lancerLaser;

    static final Cons2<Bullet, DeflectProjectorBuild> deflector = (b, tile) -> {
        if(b.team.isEnemy(tile.team) && b.type.absorbable && b.within(tile, 0f)){
            Tmp.v1.set(b.x - tile.x, b.y - tile.y).nor();
            b.vel.sub(Tmp.v1.scl(2f * b.vel.dot(Tmp.v1)));

            tile.hit = 1f;
        }
    };

    public DeflectProjector(String name){
        super(name);
    }

    public class DeflectProjectorBuild extends Building implements ExtensionHolder{
        protected Extensionc ext;

        public float heat;
        public float hit;

        @Override
        public void created(){
            super.created();
            ext = (Extensionc)UnityUnitTypes.extension.create(team);
            ext.holder(this);
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

            heat = Mathf.lerpDelta(heat, edelta(), warmup);
            if(timer.get(timerUse, deflectTime)){
                boolean[] deflected = {false};

                Groups.bullet.intersect(x - radf(), y - radf(), radf() * 2f, radf() * 2f, b -> {
                    deflected[0] = true;
                    deflector.get(b, this);
                });

                if(!deflected[0]){
                    timer.reset(timerUse, 0f);
                }
            }

            if(hit > 0f){
                hit -= 1f / 5f * Time.delta;
            }
        }
    }
}
