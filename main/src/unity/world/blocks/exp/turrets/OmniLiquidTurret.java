package unity.world.blocks.exp.turrets;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import unity.world.blocks.exp.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class OmniLiquidTurret extends ExpTurret {
    public TextureRegion liquidRegion;
    public TextureRegion topRegion;
    public boolean extinguish = true;
    public BulletType shootType;
    public float shootAmount = 0.5f;

    public OmniLiquidTurret(String name){
        super(name);
        acceptCoolant = false;
        hasLiquids = true;
        loopSound = Sounds.spray;
        shootSound = Sounds.none;
        smokeEffect = Fx.none;
        shootEffect = Fx.none;
        outlinedIcon = 1;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));
    }

    @Override
    public void load(){
        super.load();
        liquidRegion = atlas.find(name + "-liquid");
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons(){
        if(topRegion.found()) return new TextureRegion[]{baseRegion, region, topRegion};
        return super.icons();
    }

    public class OmniLiquidTurretBuild extends ExpTurretBuild{
        @Override
        public void draw(){
            super.draw();

            if(liquidRegion.found()){
                Drawf.liquid(liquidRegion, x + tr2.x, y + tr2.y, liquids.total() / liquidCapacity, liquids.current().color, rotation - 90);
            }
            if(topRegion.found()) Draw.rect(topRegion, x + tr2.x, y + tr2.y, rotation - 90);
        }

        @Override
        public boolean shouldActiveSound(){
            return wasShooting && enabled;
        }

        @Override
        public void updateTile(){
            unit.ammo(unit.type().ammoCapacity * liquids.currentAmount() / liquidCapacity);

            super.updateTile();
        }

        @Override
        protected void findTarget(){
            if(extinguish && liquids.current().canExtinguish()){
                int tx = World.toTile(x), ty = World.toTile(y);
                Fire result = null;
                float mindst = 0f;
                int tr = (int)(range / tilesize);
                for(int x = -tr; x <= tr; x++){
                    for(int y = -tr; y <= tr; y++){
                        Tile other = world.tile(x + tx, y + ty);
                        var fire = Fires.get(x + tx, y + ty);
                        float dst = fire == null ? 0 : dst2(fire);
                        //do not extinguish fires on other team blocks
                        if(other != null && fire != null && Fires.has(other.x, other.y) && dst <= range * range && (result == null || dst < mindst) && (other.build == null || other.team() == team)){
                            result = fire;
                            mindst = dst;
                        }
                    }
                }

                if(result != null){
                    target = result;
                    //don't run standard targeting
                    return;
                }
            }

            super.findTarget();
        }

        @Override
        protected void effects(){
            BulletType type = peekAmmo();

            Effect fshootEffect = shootEffect == Fx.none ? type.shootEffect : shootEffect;
            Effect fsmokeEffect = smokeEffect == Fx.none ? type.smokeEffect : smokeEffect;

            fshootEffect.at(x + tr.x, y + tr.y, rotation, liquids.current().color);
            fsmokeEffect.at(x + tr.x, y + tr.y, rotation, liquids.current().color);
            shootSound.at(tile);

            if(shootShake > 0){
                Effect.shake(shootShake, shootShake, tile.build);
            }

            recoil = recoilAmount;
        }

        @Override
        public BulletType useAmmo(){
            if(cheating()) return shootType;
            liquids.remove(liquids.current(), shootAmount);
            return shootType;
        }

        @Override
        public BulletType peekAmmo(){
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            return liquids.total() >= shootAmount;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return false;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(!hasLiquids) return false;
            return liquids.current() == liquid || liquids.currentAmount() < 0.2f;
        }

        @Override
        protected void bullet(BulletType type, float angle){
            Log.info("Shoot with " + liquids.current().name);
            float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f;

            type.create(this, team, x + tr.x, y + tr.y, angle, -1f, 1f + Mathf.range(velocityInaccuracy), lifeScl, liquids.current());
        }
    }
}