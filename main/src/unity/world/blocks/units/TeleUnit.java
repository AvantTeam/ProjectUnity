package unity.world.blocks.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import unity.content.*;
import unity.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TeleUnit extends Block{
    //this class was made assuming only one instance. if more, static should be removed or further improvement.
    protected static final TeleUnitBuild[] heads = new TeleUnitBuild[Team.baseTeams.length];
    protected static final int[] listSizes = new int[Team.baseTeams.length];
    protected TextureRegion lightRegion, topRegion, arrowRegion;

    public TeleUnit(String name){
        super(name);
        update = configurable = outputsPayload = true;
        Events.on(WorldLoadEvent.class, e -> {
            for(int i = 0; i < heads.length; i++){
                heads[i] = null;
                listSizes[i] = 0;
            }
        });
    }

    @Override
    public void load(){
        super.load();
        lightRegion = atlas.find(name + "-lights");
        topRegion = atlas.find(name + "-top");
        arrowRegion = atlas.find("transfer-arrow");
    }

    public class TeleUnitBuild extends Building{
        protected float warmup, warmup2;
        //slow adding O(n), but fast removing O(1).
        //I hope non-primitive fields are just pointer variable.
        protected TeleUnitBuild next, prev;
        protected Team previousTeam;

        protected boolean isTeamChanged(){
            return previousTeam != team;
        }

        @Override
        public void updateTile(){
            warmup = Mathf.lerpDelta(warmup, canConsume() ? 1f : 0f, 0.05f);
            warmup2 = Mathf.lerpDelta(warmup2, canConsume() && enabled ? 1f : 0f, 0.05f);
            if(isTeamChanged()){
                onRemoved();
                created();
            }
        }

        @Override
        public void draw(){
            super.draw();
            Draw.color(Color.white);
            Draw.alpha(0.45f + Mathf.absin(7f, 0.26f));
            Draw.rect(topRegion, x, y);
            if(warmup >= 0.001f){
                Draw.z(Layer.bullet);
                Draw.color(UnityPal.dirium, team.color, Mathf.absin(19f, 1f));
                Lines.stroke((Mathf.absin(62f, 0.5f) + 0.5f) * warmup);
                Lines.square(x, y, 10.5f, 45f);
                if(warmup2 >= 0.001f){
                    Lines.stroke((Mathf.absin(62f, 1f) + 1f) * warmup2);
                    Lines.square(x, y, 8.5f, Time.time / 2f);
                    Lines.square(x, y, 8.5f, -1 * Time.time / 2f);
                }
            }
            Draw.reset();
        }

        @Override
        public void drawSelect(){
            Draw.color(canConsume() ? inRange(player) ? UnityPal.dirium : Pal.accent : Pal.darkMetal);
            float length = tilesize * size / 2f + 3f + Mathf.absin(5f, 2f);
            Draw.rect(arrowRegion, x + length, y, 180f);
            Draw.rect(arrowRegion, x, y + length, 270f);
            Draw.rect(arrowRegion, x - length, y, 0f);
            Draw.rect(arrowRegion, x, y - length, 90f);
            Draw.color();
        }

        @Override
        public boolean shouldAmbientSound(){
            return canConsume();
        }

        @Override
        public void created(){
            previousTeam = team;
            TeleUnitBuild temp = heads[team.id];
            listSizes[team.id]++;
            if(temp == null){
                heads[team.id] = next = prev = this;
                return;
            }
            for(int i = 0, len = listSizes[team.id] - 1; i < len; i++){
                if(temp.pos() > pos()){
                    if(i == 0) heads[team.id] = this;
                    next = temp;
                    prev = temp.prev;
                    prev.next = temp.prev = this;
                    return;
                }else temp = temp.next;
            }
            next = temp;
            prev = temp.prev;
            prev.next = temp.prev = this;
        }

        @Override
        public void onRemoved(){
            int a = isTeamChanged() ? previousTeam.id : team.id;
            if(heads[a] != null){
                if(this == heads[a]){
                    if(listSizes[a] > 1) heads[a] = next;
                    else heads[a] = null;
                }
                prev.next = next;
                next.prev = prev;
                next = prev = null;
                listSizes[a]--;
            }
        }

        protected TeleUnitBuild getDest(){
            TeleUnitBuild temp = this;
            for(int i = 0, len = listSizes[previousTeam.id]; i < len; i++){
                temp = temp.next;
                if(temp != null && temp.enabled && temp.power.graph == power.graph) return temp;
            }
            return temp;
        }

        protected boolean inRange(Player player){
            return player.unit() != null && player.unit().isValid() && Math.abs(player.unit().x - x) <= 2.5f * tilesize && Math.abs(player.unit().y - y) <= 2.5f * tilesize;
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            return canConsume() && inRange(player);
        }

        @Override
        public boolean configTapped(){
            if(!canConsume() || !inRange(player)) return false;
            configure(null);
            Sounds.click.at(this);
            return false;
        }

        @Override
        public void configured(Unit unit, Object value){
            if(unit != null && unit.isPlayer() && !(unit instanceof BlockUnitc)) tpPlayer(unit.getPlayer());
        }

        protected void tpPlayer(Player player1){
            tpUnit(player1.unit(), player1 == player);
            if(player != null && player1 == player) camera.position.set(player1);
        }

        protected void tpUnit(Unit unit, boolean isPlayer){
            TeleUnitBuild dest = getDest();
            if(dest == null) return;
            if(!headless) UnityFx.tpIn.at(unit.x, unit.y, unit.rotation - 90f, Color.white, unit.type);
            unit.set(dest.x, dest.y);
            unit.snapInterpolation();
            unit.set(dest.x, dest.y);
            if(!headless) effects(dest, unit.hitSize * 1.7f, isPlayer, unit);
        }

        protected void effects(TeleUnitBuild dest, float hitSize, boolean isPlayer, Unit unit){
            if(isPlayer){
                Sounds.plasmadrop.at(dest, Mathf.random() * 0.2f + 1f);
                Sounds.lasercharge2.at(this, Mathf.random() * 0.2f + 0.7f);
            }else{
                Sounds.plasmadrop.at(this, Mathf.random() * 0.2f + 1f);
                Sounds.lasercharge2.at(dest, Mathf.random() * 0.2f + 0.7f);
            }
            UnityFx.tpOut.at(dest, hitSize);
            UnityFx.tpFlash.at(dest.x, dest.y, 0f, Color.white, unit);
        }

        @Override
        public void unitOn(Unit unit){
            if(!canConsume()) return;
            if(unit.hasEffect(UnityStatusEffects.tpCoolDown) || unit.isPlayer()) return;
            tpUnit(unit, false);
            unit.apply(UnityStatusEffects.tpCoolDown, 120f);
        }

        @Override
        public boolean canConsume(){
            return power.status > 0.98f;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            TeleUnitBuild dest = getDest();
            if(!canConsume() || !dest.enabled) return false;
            int ntrns = 1 + size / 2;
            Building nextBuild = dest.nearby(Geometry.d4x(source.rotation) * ntrns, Geometry.d4y(source.rotation) * ntrns);
            boolean result = nextBuild != null && (
                //same size
                (nextBuild.block.size == size && dest.tileX() + Geometry.d4(source.rotation).x * size == nextBuild.tileX() && dest.tileY() + Geometry.d4(source.rotation).y * size == nextBuild.tileY()) ||

                    //differing sizes
                    (nextBuild.block.size > size &&
                        (source.rotation % 2 == 0 ? //check orientation
                            Math.abs(nextBuild.y - dest.y) <= (nextBuild.block.size * tilesize - size * tilesize) / 2f : //check Y alignment
                            Math.abs(nextBuild.x - dest.x) <= (nextBuild.block.size * tilesize - size * tilesize) / 2f   //check X alignment
                        )));
            if(result && nextBuild.block.outputsPayload && !nextBuild.tile.solid() && (nextBuild.rotation + 2) % 4 != source.rotation){
                result = nextBuild.acceptPayload(source, payload);
                if(result) nextBuild.handlePayload(source, payload);
                return result;
            }else return false;
        }
    }
}
