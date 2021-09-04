package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.assets.type.g3d.*;
import unity.util.*;

public class WavefrontTurret extends PowerTurret{
    public Model model;
    public float scale = 20f;

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

        float gap = 0f;
        float offset = 0f;
        float angle = 0f;
        float waitTime = 0f;

        @Override
        public void created(){
            super.created();
            inst = new ModelInstance(model);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(isShooting() && consValid()){
                gap = Math.min(0.5f, gap + (0.005f * Time.delta));
                angle += (reload / reloadTime) * objectRotationSpeed;
                offset = (reload / reloadTime) * 0.25f;
            }else{
                angle = Mathf.slerp(angle, Mathf.round(angle / 90f) * 90f, 0.1f);
                if(resetAvailable()){
                    gap = Math.max(0f, gap - (0.005f * Time.delta));
                }
            }
            if(waitTime > 0f){
                waitTime -= Time.delta;
            }

            tr2.trns(rotation, -recoil);
            inst.transform.set(
                Tmp.v31.set(x + tr2.x, y + tr2.y, 0f),
                Utils.q1
                    .setFromAxis(0f, 0f, 1f, rotation - 90f)
                    .mul(Utils.q2.setFromAxis(0f, 1f, 0f, angle)),
                Tmp.v32.set(scale, scale, scale)
            );
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
