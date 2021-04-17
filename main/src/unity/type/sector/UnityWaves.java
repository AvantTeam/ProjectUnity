package unity.type.sector;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.type.*;
import unity.mod.*;

import static mindustry.content.UnitTypes.*;
import static unity.content.UnityUnitTypes.*;

/** @author GlennFolker */
public final class UnityWaves{
    private static ObjectMap<Faction, Func3<Float, Rand, Boolean, Seq<SpawnGroup>>> generators = new ObjectMap<>();

    static{
        generators.put(Faction.monolith, (difficulty, rand, attack) -> {
            UnitType[][] species = {
                {stele, pedestal, pilaster, pylon, monument, colossus, bastion},
                {dagger, mace, fortress, scepter, reign, reign, reign},
                {crawler, atrax, spiroct, arkyid, toxopid, toxopid, toxopid},
                {flare, horizon, zenith, rand.chance(0.5) ? quad : antumbra, rand.chance(0.5) ? antumbra : eclipse, rand.chance(0.3) ? quad : eclipse, rand.chance(0.1) ? antumbra : eclipse}
            };

            Seq<SpawnGroup> out = new Seq<>();

            int cap = 150;

            float shieldStart = 40, shieldsPerWave = 20 + difficulty * 40f;
            float[] scaling = {1, 2f, 3f, 4f, 5f, 6f, 7f};

            Intc createProgression = start -> {
                UnitType[] curSpecies = Structs.random(species);
                int curTier = 0;

                for(int i = start; i < cap;){
                    int f = i;
                    int next = rand.random(8, 16) + (int)Mathf.lerp(5f, 0f, difficulty) + curTier * 4;
    
                    float shieldAmount = Math.max((i - shieldStart) * shieldsPerWave, 0);
                    int space = start == 0 ? 1 : rand.random(1, 2);
                    int ctier = curTier;

                    out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                        unitAmount = f == start ? 1 : 6 / (int)scaling[ctier];
                        begin = f;
                        end = f + next >= cap ? never : f + next;
                        max = 13;
                        unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[ctier];
                        shields = shieldAmount;
                        shieldScaling = shieldsPerWave;
                        spacing = space;
                    }});

                    out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                        unitAmount = 3 / (int)scaling[ctier];
                        begin = f + next - 1;
                        end = f + next + rand.random(6, 10);
                        max = 6;
                        unitScaling = rand.random(2f, 4f);
                        spacing = rand.random(2, 4);
                        shields = shieldAmount/2f;
                        shieldScaling = shieldsPerWave;
                    }});

                    i += next + 1;
                    if(curTier < 5 || (rand.chance(0.2) && difficulty > 0.8)){
                        curTier++;
                    }

                    curTier = Math.min(curTier, 5);

                    if(rand.chance(0.3)){
                        curSpecies = Structs.random(species);
                    }
                }
            };

            createProgression.get(0);

            int step = 5 + rand.random(5);

            while(step <= cap){
                createProgression.get(step);
                step += (int)(rand.random(15, 30) * Mathf.lerp(1f, 0.5f, difficulty));
            }

            int bossWave = (int)(rand.random(50, 70) * Mathf.lerp(1f, 0.7f, difficulty));
            int bossSpacing = (int)(rand.random(25, 40) * Mathf.lerp(1f, 0.6f, difficulty));

            int bossTier = difficulty < 0.6 ? 5 : 6;

            out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
                unitAmount = 1;
                begin = bossWave;
                spacing = bossSpacing;
                end = never;
                max = 16;
                unitScaling = bossSpacing;
                shieldScaling = shieldsPerWave;
                effect = StatusEffects.boss;
            }});

            out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
                unitAmount = 1;
                begin = bossWave + rand.random(3, 5) * bossSpacing;
                spacing = bossSpacing;
                end = never;
                max = 16;
                unitScaling = bossSpacing;
                shieldScaling = shieldsPerWave;
                effect = StatusEffects.boss;
            }});
    
            int finalBossStart = 120 + rand.random(30);

            out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
                unitAmount = 1;
                begin = finalBossStart;
                spacing = bossSpacing/2;
                end = never;
                unitScaling = bossSpacing;
                shields = 500;
                shieldScaling = shieldsPerWave * 4;
                effect = StatusEffects.boss;
            }});

            out.add(new SpawnGroup(Structs.random(species)[bossTier]){{
                unitAmount = 1;
                begin = finalBossStart + 15;
                spacing = bossSpacing/2;
                end = never;
                unitScaling = bossSpacing;
                shields = 500;
                shieldScaling = shieldsPerWave * 4;
                effect = StatusEffects.boss;
            }});

            if(attack && difficulty >= 0.5){
                int amount = Mathf.random(1, 3 + (int)(difficulty*2));
    
                for(int i = 0; i < amount; i++){
                    int wave = Mathf.random(3, 20);
                    out.add(new SpawnGroup(mega){{
                        unitAmount = 1;
                        begin = wave;
                        end = wave;
                        max = 16;
                    }});
                }
            }

            int shift = Math.max((int)(difficulty * 20f - 5f), 0);

            for(SpawnGroup group : out){
                group.begin -= shift;
                group.end -= shift;
            }

            return out;
        });
    }

    public static Seq<SpawnGroup> generate(Faction faction, float difficulty, Rand rand, boolean attack){
        var gen = generators.get(faction);
        if(gen != null){
            return gen.get(difficulty, rand, attack);
        }else{
            return Waves.generate(difficulty, rand, attack);
        }
    }
}
