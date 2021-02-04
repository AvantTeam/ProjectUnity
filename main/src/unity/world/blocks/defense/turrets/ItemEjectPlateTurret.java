package unity.world.blocks.defense.turrets;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.world.blocks.defense.turrets.*;

public class ItemEjectPlateTurret extends ItemTurret{
    /** Eject x and y, since vanilla has them tied together */
    public float ammoEjectX, ammoEjectY;
    /** Position of the eject sprites. */
    public float ejectX, ejectY;
    /** How far back the sprite moves on eject */
    public float ejectRecoilAmount;
    /** Like restitution for turrets, but eject */
    public float ejectRestitution = 0.3f;
    /** Should eject sprite be duplicated, mirrored, and alternating */
    public boolean altEject = true;

    public TextureRegion[] ejectRegions;

    protected Vec2 tr3 = new Vec2();

    public Cons2<ItemEjectPlatTurretBuild, Object> ejectDrawer = (tile, drawSide) -> {
        int side = Mathf.signs[(int)drawSide];
        tr3.trns(tile.rotation - 90, ejectX * side, ejectY -tile.ejectRecoil[(int)drawSide]);
        Draw.rect(ejectRegions[(int)drawSide], tile.x + tr2.x + tr3.x, tile.y + tr2.y + tr3.y, tile.rotation - 90);
    };

    public ItemEjectPlateTurret(String name) {
        super(name);
        ammoEjectX = ammoEjectY = -1f;
        ejectX = ejectY = 1f;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 2; i++){
            ejectRegions[i] = Core.atlas.find(name + "-eject-" + i);
        }
    }

    public class ItemEjectPlatTurretBuild extends ItemTurretBuild{
        public float[] ejectRecoil = {0, 0};

        @Override
        public void draw(){
            super.draw();

            for(int i = 0; i < 2; i++){
                if(Core.atlas.isFound(ejectRegions[i])){
                    ejectDrawer.get(this, i);
                }
            }
        }

        @Override
        public void updateTile(){
            for(int i = 0; i < 2; i++){
                ejectRecoil[i] = Mathf.lerpDelta(ejectRecoil[i], 0, ejectRestitution);
            }

            super.updateTile();
        }

        protected void ejectEffetcs(){
            if(!isValid()) return;

            //alternate sides when using a double turret
            float scl = (altEject && shotCounter % 2 == 1 ? -1f : 1f);
            ammoUseEffect.at(x + Angles.trnsx(rotation, ammoEjectX), y + Angles.trnsy(rotation, ammoEjectY), rotation * scl);

            ejectRecoil[(int)scl] = ejectRecoilAmount;
        }
    }
}
