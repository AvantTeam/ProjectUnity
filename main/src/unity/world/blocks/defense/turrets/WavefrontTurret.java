package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.assets.type.g3d.*;
import unity.util.*;

public class WavefrontTurret extends PowerTurret{
    public Model model;
    public float scale = 4f;

    public float objectRotationSpeed = 7f;

    public WavefrontTurret(String name){
        super(name);
        recoilAmount = 6f;
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }

    public class WavefrontTurretBuild extends PowerTurretBuild{
        public ModelInstance inst;
        public AnimControl cont;

        float gap = 0f;
        float offset = 0f;
        float angle = 0f;
        float waitTime = 0f;
        float animTime = 0f;

        @Override
        public void created(){
            super.created();

            inst = new ModelInstance(model);
            cont = new AnimControl(inst);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(isShooting() && consValid()){
                gap = Math.min(0.5f, gap + (0.005f * Time.delta));
                angle += (reload / reloadTime) * objectRotationSpeed;
                offset = (reload / reloadTime) * 0.25f;

                animTime = Mathf.approach(animTime, 40f, Time.delta);
            }else{
                angle = Mathf.slerp(angle, Mathf.round(angle / 90f) * 90f, 0.1f);
                if(resetAvailable()){
                    gap = Math.max(0f, gap - (0.005f * Time.delta));
                }

                animTime = Mathf.approach(animTime, 0f, Time.delta);
            }

            if(waitTime > 0f){
                waitTime -= Time.delta;
            }

            tr2.trns(rotation, -recoil);
            inst.transform.set(
                Tmp.v31.set(x + tr2.x, y + tr2.y, gap),
                Utils.q1.set(Vec3.Z, rotation - 90f),
                Tmp.v33.set(scale, scale, scale)
            );

            /*cont.begin();
            cont.apply("node-outer|outer-fold", animTime);
            cont.apply("node-inner|inner-fold", animTime);
            cont.end();*/
        }

        @Override
        public boolean shouldTurn(){
            return super.shouldTurn() && waitTime <= 0f;
        }

        @Override
        protected void shoot(BulletType type){
            super.shoot(type);
            waitTime = 60f;
        }

        private boolean resetAvailable(){
            return Angles.within(angle, Mathf.round(angle / 90f) * 90f, 3f);
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color();

            Draw.draw(Draw.z(), inst::render);
        }
    }
}
