module.exports = {
    extend(type, build, name, obj = {}, objb = {}){
        obj = Object.assign({
            idleSound: Sounds.machine,
            idleSoundVolume: 1,

            chargeReloadTime: 120
        }, obj, {
            shouldIdleSound(tile){
                return false;
            },

            shouldActiveSound(tile){
                var entity = tile.ent();
                return entity.isValid() && objb.chargeReload > 0;
            }
        });

        var turret = extendContent(type, name, obj);

        objb = Object.assign({
            chargeReload: obj.chargeReloadTime
        }, objb, {
            loop: {
                sound: obj.idleSound,
                baseVolume: obj.idleSoundVolume,
                id: -1,

                update(x, y, vol, pitch){
                    if(this.baseVolume < 0) return;
                    if(this.id < 0){
                        this.id = this.sound.loop(
                            this.sound.calcVolume(x, y) * this.baseVolume * vol,
                            0.5 + 1.5 * pitch,
                            this.sound.calcPan(x, y)
                        );
                    }else{
                        if(val <= 0.01){
                            this.sound.stop(this.id);
                            this.id = -1;

                            return;
                        };

                        this.sound.setPan(
                            this.id,
                            this.sound.calcPan(x, y),
                            this.sound.calcVolume(x, y) * this.baseVolume * vol
                        );

                        this.sound.setPitch(this.id, 0.5 + 1.5 * pitch);
                    };
                },

                stop(){
                    if(this.id < 0) return;

                    this.sound.stop(this.id);
                    this.id = -1;
                    this.baseVolume = -1;
                }
            },

            updateTile(){
                this.super$updateTile();

                if(this.reload >= turret.reloadTime && (this.consValid() || this.cheating())){
                    this.chargeReload = Math.max(this.chargeReload - this.efficiency() * Time.delta, 0);
                }else{
                    this.chargeReload = Mathf.lerp(this.chargeReload, obj.chargeReloadTime, Time.delta);
                };

                this.loop.update(this.x, this.y, 1 - this.getChargeReload(), 1 - this.getChargeReload());
            },

            updateShooting(){
                if(this.reload >= turret.reloadTime && this.chargeReload <= 0){
                    var type = this.peekAmmo();

                    this.shoot(type);

                    this.reload = 0;
                };
            },

            getChargeReload(){
                return this.chargeReload / obj.chargeReloadTime;
            },

            onDestroyed(){
                this.super$onDestroyed();

                this.loop.stop();
            },

            onRemoved(){
                this.super$onRemoved();

                this.loop.stop();
            }
        });

        turret.buildType = b => {
            b = extendContent(build, turret, objb);

            return b;
        };

        return turret;
    }
};
