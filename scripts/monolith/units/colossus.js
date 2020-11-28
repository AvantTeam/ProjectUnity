const timers = new IntMap();
const getTimer = id => {
    if(!timers.containsKey(id)){
        timers.put(id, 48);
    };

    return timers.get(id);
};

const setTimer = (id, val) => {
    timers.put(id, val);
};

const phases = new IntMap();
const getPhase = id => {
    if(!phases.containsKey(id)){
        phases.put(id, 0);
    };

    return phases.get(id);
};

const setPhase = (id, val) => {
    phases.put(id, val);
};

const lightningSpawnAbility = extend(Ability, {
    update(unit){
        if(getTimer(unit.id) <= 0){
            if(getPhase(unit.id) > 0){
                for(let i = 0; i < this.lightningCount; i++){
                    Tmp.v1.trns(
                        (Time.time() * this.rotateSpeed + (360 * i / this.lightningCount) + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                        this.lightningOffset * getPhase(unit.id)
                    ).add(unit);

                    let u = Units.closestTarget(unit.team, Tmp.v1.x, Tmp.v1.y, this.lightningRange);

                    if(u != null){
                        Tmp.v2.set(u).sub(unit);
                        let angle = Tmp.v2.angle();
                        let len = Math.min(Math.round(Tmp.v2.len() / 6), Math.round(this.lightningRange / 6));

                        Lightning.create(unit.team, this.lightningColor, this.lightningDamage, Tmp.v1.x, Tmp.v1.y, angle, len);
                        this.lightningSound.at(Tmp.v1.x, Tmp.v1.y, Mathf.random(0.8, 1.2));
                    }
                };
            };

            setTimer(unit.id, this.timerTarget);
        }else{
            setTimer(unit.id, Math.max(getTimer(unit.id) - Time.delta, 0));
        };

        setPhase(unit.id, Mathf.lerpDelta(getPhase(unit.id), unit.ammof(), this.phaseSpeed));
        setPhase(unit.id, getPhase(unit.id) < 0.01 ? 0 : getPhase(unit.id));
    },

    draw(unit){
        let z = Draw.z();

        Draw.z(unit.type.groundLayer + Mathf.clamp(unit.type.hitSize / 4000, 0, 0.01) - 0.015);

        for(let i = 0; i < this.lightningCount; i++){
            Tmp.v1.trns(
                (Time.time() * this.rotateSpeed + (360 * i / this.lightningCount) + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                this.lightningOffset * getPhase(unit.id)
            ).add(unit);

            let region = Core.atlas.find("circle-shadow");
            let r = getPhase(unit.id) * region.width * Draw.scl + this.lightningRadius + (Mathf.sin(Time.time(), 6, 4) * getPhase(unit.id));

            Draw.color(this.backColor);
            Draw.rect(region, Tmp.v1.x, Tmp.v1.y, r, r);

            Draw.color(this.frontColor);
            Draw.rect(region, Tmp.v1.x, Tmp.v1.y, r / 2, r / 2);

            Draw.color(Color.white);
            Draw.rect(region, Tmp.v1.x, Tmp.v1.y, r / 3, r / 3);
        };
        
        Draw.z(z);
    },

    localized(){
        return Core.bundle.get("ability." + "lightning-spawn-ability");
    }
});
lightningSpawnAbility.timerTarget = 48;
lightningSpawnAbility.lightningCount = 8;
lightningSpawnAbility.lightningRange = 120;
lightningSpawnAbility.lightningOffset = 56;
lightningSpawnAbility.lightningColor = Pal.lancerLaser;
lightningSpawnAbility.lightningDamage = 100;
lightningSpawnAbility.lightningSound = Sounds.spark;
lightningSpawnAbility.lightningRadius = 18;
lightningSpawnAbility.rotateSpeed = 2;
lightningSpawnAbility.phaseSpeed = 0.05;
lightningSpawnAbility.backColor = Pal.lancerLaser.cpy();
    lightningSpawnAbility.backColor.a = 0.5;
lightningSpawnAbility.frontColor = Color.white.cpy();
    lightningSpawnAbility.frontColor.a = 0.8;

const colossus = extend(UnitType, "colossus", {});
colossus.ammoType = AmmoTypes.powerHigh;
colossus.groundLayer = Layer.legUnit;
colossus.abilities.add(lightningSpawnAbility);
colossus.constructor = () => {
    return extend(LegsUnit, {});
};
