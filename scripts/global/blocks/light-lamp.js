const lib = require("unity/libraries/light/lightSource");

const lamp = lib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "light-lamp", {
    lightLength: 30,
    //The original Block extension object.
    drawRequestRegion(req, list){
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
        Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, req.rotation*90);
	},
    load(){
        this.super$load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.lightRegion = Core.atlas.find("unity-light-center");
    }
}, {
    //The original Building extension object.
    draw(){
        Draw.z(Layer.block);
        Draw.rect(lamp.baseRegion, this.x, this.y);
        if(this.lightPower() > lamp.lightStrength/2){
            Draw.z(Layer.effect - 2);
            Draw.rect(lamp.lightRegion, this.x, this.y);
        }
        Draw.z(Layer.effect + 2);
        Draw.rect(lamp.topRegion, this.x, this.y, this.rotation*90);
        Draw.reset();
    }
});

const oilLamp = lib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "oil-lamp", {
    lightLength: 150,
    lightStrength: 750,
    angleConfig: true,
    //The original Block extension object.
    drawRequestRegion(req, list){
		const scl = Vars.tilesize * req.animScale * 3;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
        //Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, req.rotation*90);
        if(req.config != null){
            this.drawRequestConfig(req, list);
        }
	},
    drawRequestConfig(req, list){
        const scl = Vars.tilesize * req.animScale * 3;
        Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, (req.config == null)?0:req.config*45);
    },
    load(){
        this.super$load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.liquidRegion = Core.atlas.find(this.name + "-liquid");
        this.lightRegion = Core.atlas.find(this.name + "-light");
    }
}, {
    //The original Building extension object.
    draw(){
        Draw.z(Layer.block);
        Draw.rect(oilLamp.baseRegion, this.x, this.y);
        if(this.liquids.total() > 0.001){
            Draw.color(Liquids.oil.color);
            Draw.alpha(this.liquids.get(Liquids.oil) / oilLamp.liquidCapacity);
            Draw.rect(oilLamp.liquidRegion, this.x, this.y);
            Draw.color();
        }
        if(this.lightPower() > oilLamp.lightStrength/2){
            Draw.z(Layer.effect - 2);
            Draw.rect(oilLamp.lightRegion, this.x, this.y);
        }
        Draw.z(Layer.effect + 2);
        Draw.rect(oilLamp.topRegion, this.x, this.y, this.getAngleDeg());
        Draw.reset();
    }
});

const laser = lib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "light-laser", {
    lightLength: 30,
    lightInterval: 0,
    //The original Block extension object.
    drawRequestRegion(req, list){
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
        Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, req.rotation*90);
	},
    load(){
        this.super$load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.lightRegion = Core.atlas.find("unity-light-center");
    }
}, {
    //The original Building extension object.
    draw(){
        Draw.z(Layer.block);
        Draw.rect(laser.baseRegion, this.x, this.y);
        if(this.lightPower() > laser.lightStrength/2){
            Draw.z(Layer.effect - 2);
            Draw.rect(laser.lightRegion, this.x, this.y);
        }
        Draw.z(Layer.effect + 2);
        Draw.rect(laser.topRegion, this.x, this.y, this.rotation*90);
        Draw.reset();
    }
});

const lampInfi = lib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "light-lamp-infi", {
    lightLength: 150,
    lightStrength: 600000,
    scaleStatus: false,
    maxLightLength: 7500,
    //The original Block extension object.
    drawRequestRegion(req, list){
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
        Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, req.rotation*90);
	},
    load(){
        this.super$load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.topRegion = Core.atlas.find(lamp.name + "-top");
        this.lightRegion = Core.atlas.find("unity-light-center");
    }
}, {
    //The original Building extension object.
    draw(){
        Draw.z(Layer.block);
        Draw.rect(lampInfi.baseRegion, this.x, this.y);
        if(this.lightPower() > lampInfi.lightStrength/2){
            Draw.z(Layer.effect - 2);
            Draw.rect(lampInfi.lightRegion, this.x, this.y);
        }
        Draw.z(Layer.effect + 2);
        Draw.rect(lampInfi.topRegion, this.x, this.y, this.rotation*90);
        Draw.reset();
    }
});
