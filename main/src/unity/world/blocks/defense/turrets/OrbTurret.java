package unity.world.blocks.defense.turrets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.content.UnityFx;
import unity.graphics.*;

public class OrbTurret extends PowerTurret {
    public int orbsPerLayer = 3;
    public int layers = 2;

    public float bulletWidth = 2f;

    public Color bulletHeadColor = Color.white;
    public Color bulletTrailColor = Pal.accent;

    public float layerSpeedMultiplier = 1f;
    public float layerDamageMultiplier = 1f;

    public OrbTurret(String name){
        super(name);

        solid = true;
        update = true;
    }

    public class OrbTurretBuild extends PowerTurret.PowerTurretBuild {
        public Seq<TexturedTrail> trails = new Seq<>();
        public Rand rand = new Rand();
        public Seq<Float> offsets = new Seq<>();
        public float loader = 0f;

        public float getX(int i){
            return x + Mathf.cosDeg((360f/orbsPerLayer)*(int)(i%orbsPerLayer) + Time.time*5f + offsets.get(i/orbsPerLayer))*(bulletWidth*3f+bulletWidth*2f)*(int)(1+i/orbsPerLayer)*Mathf.cosDeg(90f + Time.time*5f + offsets.get(i/orbsPerLayer));
        }

        public float getY(int i){
            return y + Mathf.sinDeg((360f/orbsPerLayer)*(int)(i%orbsPerLayer) + Time.time*5f + offsets.get(i/orbsPerLayer))*(bulletWidth*3f+bulletWidth*2f)*(int)(1+i/orbsPerLayer)/**Mathf.cosDeg(Time.time*5f + offsets.get(i/orbsPerLayer))*/;
        }

        public void addTrail(int i){
            TexturedTrail trail = new TexturedTrail(null, 3+(1+i/layers)*6);
            trail.mixAlpha = 1f;
            trail.baseWidth = bulletWidth;

            trails.add(trail);
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            for(int i = 0; i < layers*orbsPerLayer; i++){
                addTrail(i);
            }

            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public void placed() {
            super.placed();

            for (int i = 0; i < layers; i++){
                rand.setSeed(pos() + i*69);
                offsets.add(rand.nextFloat() * 420f);
            }
        }

        @Override
        protected boolean validateTarget() {
            return super.validateTarget() && trails.size > 0;
        }

        @Override
        protected void bullet(BulletType type, float angle) {
            int l = (int)Math.ceil((float)trails.size / (float)layers);
            float xP = getX(trails.size-1) + tr.x;
            float yP = getY(trails.size-1) + tr.y;
            Bullet bullet = type.create(this, team, xP, yP, Angles.angle(xP, yP, targetPos.x, targetPos.y), (1f + Mathf.range(velocityInaccuracy)) * (1f + l*layerSpeedMultiplier), 1f);
            bullet.damage = bullet.damage * (1f + l*layerDamageMultiplier);

            if(!Vars.headless){
                UnityFx.orbShot.at(xP, yP, team.color);
            }

            bullet.trail = trails.pop();
            bullet.data = new Color[]{bulletHeadColor, bulletTrailColor};
        }

        @Override
        public void update() {
            super.update();

            if(offsets.size == 0) {
                for (int i = 0; i < layers; i++) {
                    rand.setSeed(pos() + i * 69);
                    offsets.add(rand.nextFloat() * 420f);
                }
            }

            for(int i = 0; i < trails.size; i++){
                trails.get(i).update(getX(i), getY(i));
            }

            if(trails.size < layers*orbsPerLayer) {
                if (loader >= 1f) {
                    addTrail(trails.size);
                    loader = 0f;
                }
                loader += 3f/60f;
            } else {
                loader = 0f;
            }
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);

            Draw.z(Layer.effect);

            trails.each(e -> {
                Draw.color(bulletTrailColor);
                e.draw(bulletHeadColor, 1f);
                e.drawCap(bulletHeadColor, 1f);
            });

            Draw.color();
        }

        @Override
        protected void effects(){
            shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));

            recoil = recoilAmount;
        }
    }
}
