const lightningSpawnAbility = extend(Ability, {
    update(unit){
        if(this.timer <= 0){
            for(let i = 0; i < this.lightningCount; i++){
                let lightning = this.lightnings[i];

                if(this.phase > 0){
                    let u = Units.closestTarget(unit.team, lightning.x, lightning.y, this.lightningRange);

                    if(u != null){
                        Tmp.v2.set(u.x, u.y).sub(unit.x, unit.y);
                        let angle = Tmp.v2.angle();
                        let len = Math.min(Math.round(Tmp.v2.len() / 6), Math.round(this.lightningRange / 6));

                        Lightning.create(unit.team, this.lightningColor, this.lightningDamage, lightning.x, lightning.y, angle, len);
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
                this.lightnings[i] = new Vec2();
            }

            let lightning = this.lightnings[i];
            lightning.trns((360 * i / this.lightningCount) + (Time.time() * this.rotateSpeed * Mathf.signs[unit.id % 2]), this.lightningOffset * this.phase).add(unit);
        }

        this.phase = Mathf.lerpDelta(this.phase, unit.ammof(), this.phaseSpeed);
        this.phase = this.phase < 0.01 ? 0 : this.phase;
    },

    draw(unit){
        for(let i = 0; i < this.lightningCount; i++){
            let lightning = this.lightnings[i];
            let region = Core.atlas.find("circle-shadow");
            let r = this.phase * region.width * Draw.scl + this.lightningRadius + (Mathf.sin(Time.time(), 6, 4) * this.phase);

            Draw.color(this.backColor);
            Draw.rect(region, lightning.x, lightning.y, r, r);

            Draw.color(this.frontColor);
            Draw.rect(region, lightning.x, lightning.y, r / 2, r / 2);
            
            Draw.color(Color.white);
            Draw.rect(region, lightning.x, lightning.y, r / 3, r / 3);
        }
    },

    localized(){
        return Core.bundle.get("ability." + "lightning-spawn-ability");
    }
});
lightningSpawnAbility.lightnings = [];
lightningSpawnAbility.timerTarget = 48;
lightningSpawnAbility.timer = lightningSpawnAbility.timerTarget;
lightningSpawnAbility.lightningCount = 8;
lightningSpawnAbility.lightningRange = 80;
lightningSpawnAbility.lightningOffset = 56;
lightningSpawnAbility.lightningColor = Pal.lancerLaser;
lightningSpawnAbility.lightningDamage = 100;
lightningSpawnAbility.lightningSound = Sounds.spark;
lightningSpawnAbility.lightningRadius = 18;
lightningSpawnAbility.rotateSpeed = 2;
lightningSpawnAbility.phase = 0;
lightningSpawnAbility.phaseSpeed = 0.05;
lightningSpawnAbility.backColor = Pal.lancerLaser.cpy();
    lightningSpawnAbility.backColor.a = 0.5;
lightningSpawnAbility.frontColor = Color.white.cpy();
    lightningSpawnAbility.frontColor.a = 0.8;

const colossus = extendContent(UnitType, "colossus", {});
colossus.abilities.add(lightningSpawnAbility);
colossus.constructor = () => {
    return extend(LegsUnit, {});
};
