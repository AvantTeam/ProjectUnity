package unity.world.blocks.exp;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityFx;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;

public class KoruhReactor extends ImpactReactor{
    public int expUse = 2;
    public int expCapacity = 24;

    public KoruhReactor(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.itemCapacity, "@", Core.bundle.format("exp.expAmount", expCapacity));
        stats.add(Stat.input, "@ [lightgray]@[]", Core.bundle.format("explib.expAmount", (expUse / itemDuration) * 60), StatUnit.perSecond.localized());
    }

    @Override
    public void setBars(){
        super.setBars();

        bars.add("exp", (KoruhReactor.KoruhReactorBuild entity) -> new Bar(() -> Core.bundle.get("bar.exp"), () -> UnityPal.exp, ()->1f*entity.exp/expCapacity));
    }

    public class KoruhReactorBuild extends ImpactReactorBuild implements ExpHolder{
        public int exp;

        @Override
        public int getExp(){
            return exp;
        }

        /** handle exp in this building
         * function taken from {@link unity.world.blocks.exp.KoruhCrafter.KoruhCrafterBuild#handleExp(int)}
         * @return exp after handling
         */
        @Override
        public int handleExp(int amount){
            if(amount > 0){
                int e = Math.min(expCapacity - exp, amount);
                exp += e;
                return e;
            }
            else{
                int e = Math.min(-amount, exp);
                exp -= e;
                return -e;
            }
        }

        @Override
        public int unloadExp(int amount){
            int e = Math.min(amount, exp);
            exp -= e;
            return e;
        }

        @Override
        public boolean acceptOrb() { return true; }

        @Override
        public boolean handleOrb(int orbExp){
            return handleExp(orbExp) > 0;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(consValid()) {
                if (exp >= expUse) {
                    if (productionEfficiency >= 0.8f && Mathf.randomBoolean(0.001f)) {
                        float dir = Mathf.random(360f);
                        Vec2 vec = new Vec2();
                        vec.trns(dir, (size + Mathf.random(0.5f, 1.5f)) * Vars.tilesize).add(x, y);
                        UnityFx.expDump.at(x, y, 0, vec);
                        Time.run(UnityFx.expDump.lifetime, () -> ExpOrbs.spreadExp(vec.x, vec.y, 10, 0));
                    }
                } else {
                    damage(1);
                    if(health<=0) {
                        for(int i = 0, m = Mathf.ceilPositive(exp*1.5f); i < m; i++) {
                            Time.run(i*10, () -> {
                                float dir = Mathf.random(360f);
                                Vec2 vec = new Vec2();
                                vec.trns(dir, (size + Mathf.random(0.5f, 1.5f)) * Vars.tilesize).add(x, y);
                                UnityFx.expDump.at(x, y, 0, vec);
                                Time.run(UnityFx.expDump.lifetime, () -> ExpOrbs.spreadExp(vec.x, vec.y, 10, 0));
                            });
                        }
                    }
                }
            }
        }

        @Override
        public void consume(){
            super.consume();

            if(exp >= expUse) handleExp(-expUse);
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(exp);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            exp = read.i();
        }
    }
}
