//represents a class constructor
const LightningSpawn = () => {
    this.x = 0;
    this.y = 0;
};

const lightningSpawnAbility = extend(Ability, {
    update(unit){
        if(this.timer <= 0){
            for(let i = 0; i < this.lightningCount; i++){
                let lightning = this.lightnings[i];

                if(this.phase > 0){
                    let u = Units.closestTarget(unit.team(), lightning.x, lightning.y, this.lightningRange);

                    if(u != null){
                        Tmp.v2.set(u.x, u.y).sub(unit.x, unit.y);
                        let angle = Tmp.v2.angle();
                        let len = Math.min(Math.round(Tmp.v2.len() / 6), Math.round(this.lightningRange / 6));

                        Lightning.create(unit.team(), this.lightningColor, this.lightningDamage, lightning.x, lightning.y, angle, len);
                        this.lightningSound.at(lightning.x, lightning.y, Mathf.random(0.8, 1.2));
                    }
                }
            }

            this.timer = this.timerTarget;
        }else{
            this.timer = Math.max(this.timer - Time.delta, 0);
        };

        //update position
        for(let i = 0; i < this.lightningCount; i++){
            if(typeof(this.lightnings[i]) === "undefined"){
                this.lightnings[i] = new LightningSpawn();
            }

            Tmp.v1.trns((360 / i + (Time.time() * this.rotateSpeed * Mathf.signs[unit.id % 2])) % 360, this.lightningOffset * this.phase);

            let lightning = this.lightnings[i];
            lightning.x = unit.x + Tmp.v1.x;
            lightning.y = unit.y + Tmp.v1.y;
        }

        this.phase = Mathf.lerpDelta(this.phase, unit.ammof(), this.phaseSpeed);
        this.phase = this.phase < 0.01 ? 0 : this.phase;
    },

    draw(unit){
        for(let i = 0; i < this.lightningCount; i++){
            let lightning = this.lightnings[i];

            Draw.color(Pal.lancerLaser, 0.5);
            Fill.circle(lightning.x, lightning.y, this.phase * 32);

            Draw.color(Color.white, 0.8);
            Fill.circle(lightning.x, lightning.y, this.phase * 16);
        }
    },

    localized(){
        return Core.bundle.get("ability." + "lightning-spawn-ability");
    }
});
lightningSpawnAbility.lightnings = [];
lightningSpawnAbility.timerTarget = 60;
lightningSpawnAbility.timer = lightningSpawnAbility.timerTarget;
lightningSpawnAbility.lightningCount = 12;
lightningSpawnAbility.lightningRange = 60;
lightningSpawnAbility.lightningOffset = 128;
lightningSpawnAbility.lightningColor = Pal.lancerLaser;
lightningSpawnAbility.lightningDamage = 100;
lightningSpawnAbility.lightningSound = Sounds.spark;
lightningSpawnAbility.rotateSpeed = 4;
lightningSpawnAbility.phase = 0;
lightningSpawnAbility.phaseSpeed = 0.05;

const colossus = extendContent(UnitType, "colossus", {});
colossus.abilities.add(lightningSpawnAbility);
colossus.constructor = () => {
    return extend(LegsUnit, {});
};
