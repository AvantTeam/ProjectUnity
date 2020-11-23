//imports
importPackage(Packages.arc.g2d);
const graphLib = require("libraries/graphlib");
//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.

function deepCopy(obj) {
    var clone = {};
    for (var i in obj) {
        if (typeof(obj[i]) == "object" && obj[i] != null) clone[i] = deepCopy(obj[i]);
        else clone[i] = obj[i];
    }
    return clone;
}
const _dirs = [{
        x: 1,
        y: 0
    },
    {
        x: 0,
        y: 1
    },
    {
        x: -1,
        y: 0
    },
    {
        x: 0,
        y: -1
    }

]

function sqrd(x) {
    return x * x;
}

const _Torque_Speed_Funcs = {
    //s- max rated speed
    //m- max torque
    //h- inital torque

    //basic motor/generator
    linear(x, s, m, h, k) {
        x = Math.min(s, x);
        return Math.min(k * (s - x) * m / s, 99999);
    },
    //used for combustion
    quadratic(x, s, m, h, k) {
        let s2 = s * s;
        x = Math.min(s, x);
        let h1 = Mathf.sqrt(m * s2 * s2 * (m - h)) + m * s2;
        return h + ((h * s2 - 2 * h1) * x * x) / (s2 * s2) + 2 * ((h1 - h * s2) * x * x) / (s2 * s);
    },
    //advanced electric motor
    induction(x, s, m, h, k) {
        x = Math.min(s, x);
        return m * Mathf.log(s + 1 - x) * Mathf.pow(e, -k * sqrd(x - s * 0.8)) + (s - x) * (m / s);
    },

};



const _RotPowerCommon = Object.assign(deepCopy(graphLib.graphCommon),{
	baseFriction:0.1,
	baseInertia:10,
	
	getBaseFriction() {
        return this.baseFriction;
    },
    setBaseFriction(n_friction) {
        this.baseFriction = n_friction;
    },
	getBaseInertia() {
        return this.baseInertia;
    },
    setBaseInertia(v) {
        this.baseInertia = v;
    },
    drawPlace(x, y, size, rotation, valid) {
        for (let i = 0; i < this._accept.length; i++) {
            if (this._accept[i] == 0) {
                continue;
            }
            Lines.stroke(3.5, Color.white);
            let outpos = getConnectSidePos(i, size, rotation);
            let dx = (outpos.toPos.x + x) * Vars.tilesize;
            let dy = (outpos.toPos.y + y) * Vars.tilesize;
            let dir = _dirs[outpos.dir];
            Lines.line(dx - dir.x, dy - dir.y, dx - dir.x * 2, dy - dir.y * 2);
        }

    },

    setStats(table) {
		table.row();
		table.left();
        table.add("Torque system").color(Pal.accent).fillX();
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.friction") + ":[] ").left();
		table.add((this.getBaseFriction()*1000)+"Nmv^-2");
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.inertia") + ":[] ").left();
		table.add(this.getBaseInertia()+"t m^2");
		this.setStatsExt(table);
    },
	setStatsExt(table) {},
    otherStats(table) {},
    _torqueFunc: _Torque_Speed_Funcs.linear,
    //objects that generate torque have a max speed, its torque is dependant on how close the system shaft speed is to it, gear transmission is needed to achieve higher speeds.
    getForce(current_speed, target_speed, max_torque, init_torque, force_coeff) {
        return this._torqueFunc(current_speed, target_speed, max_torque, init_torque, force_coeff);
    },
});
// gets a conneciton point on its index, runs anticlockwise around the block.
/*
e.g:

1x1:      2x2:       3x3:
 1         32        543    
2▣0       4▣▣1      6▣▣▣2
 3        5▣▣0      7▣▣▣1   
           67       8▣▣▣0
		             9AB
*/
function getConnectSidePos(index, size, rotation) {
    let side = Mathf.floor(index / size);
    side = (side + rotation) % 4;
    let normal = _dirs[(side + 3) % 4]
    let tangent = _dirs[(side + 1) % 4]
    let originx = 0;
    let originy = 0;

    if (size > 1) {
        originx += Mathf.floor(size / 2);
        originy += Mathf.floor(size / 2);
        originy -= (size - 1);
        if (side > 0) {
            for (let i = 1; i <= side; i++) {
                originx += _dirs[i].x * (size - 1);
                originy += _dirs[i].y * (size - 1);
            }
        }
        originx += tangent.x * (index % size);
        originy += tangent.y * (index % size);
    }
    return {
        fromPos: {
            x: originx,
            y: originy
        },
        toPos: {
            x: originx + _dirs[side].x,
            y: originy + _dirs[side].y
        },
        dir: side

    }
}


const _RotPowerPropsCommon = Object.assign(Object.create(graphLib.graphProps),{

    display(table) {
        if (!this._network) {
            return;
        }
        let ps = " " + StatUnit.perSecond.localized();
        let net = this._network;
        table.row();
        table.table(
            cons(sub => {
                sub.clearChildren();
                sub.left();
                sub.label(prov(() => {
                    return Strings.fixed(net.lastVelocity / 6.0, 2) + "r" + ps;
                })).color(Color.lightGray);
            })
        ).left();
    },

    
    drawSelect() {
        if (this._network) {
            this._network.connected.each(cons(building => {
                Drawf.selected(building.getBuild().tileX(), building.getBuild().tileY(), building.getBuild().block, Pal.accent);
            }));
        }
    },
	
	updateProps(graph,index) {
		if(!this._propsList){
			const blank = [];
			this._propsList=blank;
		}
		if(!this._propsList[index]){
			this._propsList[index] = {rotation:0};
		}
		this._propsList[index].rotation += graph.lastVelocity;
		this._propsList[index].rotation = this._propsList[index].rotation % (360 * 24);
	},
	
	
    _force: 0,
    _inertia: 10,
    _friction: 0.1,
    getForce() {
        return this._force;
    },
    setForce(n_force) {

        this._force = n_force;
    },
    getInertia() {
        return this._inertia;
    },
    setInertia(n_inertia) {
		var diff = n_inertia-this._inertia;
		if(diff!=0){
			this.getNetwork().injectIntertia(diff);
		}
        this._inertia = n_inertia;
    },
	getRotation() {
		if(!this._propsList[0]){return 0;}// map editor 'edit in game' will not load graph save data.
        return this._propsList[0].rotation;
    },
    getRotationOf(index) {
		if(!this._propsList[index]){return 0;} // map editor 'edit in game' will not load graph save data.
        return this._propsList[index].rotation;
    },
    getFriction() {
        return this._friction;
    },
    setFriction(n_friction) {
        this._friction = n_friction;
    },
	applySaveState(graph,cache) {
		graph.lastVelocity = Math.max(graph.lastVelocity, cache.speed);
	},
	writeGlobal(stream) {
		stream.f(this._force);
        stream.f(this._inertia);
        stream.f(this._friction);
	},
	writeLocal(stream,graph) {
		stream.f(graph.lastVelocity?graph.lastVelocity:0.0);
	},
	readGlobal(stream, revision) {
		this._force = stream.f();
        this._inertia = stream.f();
        this._friction = stream.f();
	},
	readLocal(stream, revision) {
		let fk = stream.f();
		print(fk);
		return {speed:fk}
	}

});




const _TorqueMulticonnectorProps = Object.assign(Object.create(graphLib.graphMultiProps),_RotPowerPropsCommon, {
    _networkRots: [],
    
    getNetworkRotation(index) {
        let r = this._networkRots[index];
        if (r === undefined) {
            return 0;
        }
        return this._networkRots[index];
    },
    setInertia(n_inertia) {
		var diff = n_inertia-this._inertia;
		if(diff!=0){
			for (let i = 0; i < this._networkList.length; i++) {
				this._networkList[i].injectIntertia(diff);
			}
		}
        this._inertia = n_inertia;
    },
    drawSelect() {
        if (this._networkList.length == 0) {
            return;
        }
        let pals = [Pal.accent, Pal.redSpark, Pal.plasticSmoke, Pal.lancerLaser];
        for (let i = 0; i < this._networkList.length; i++) {
            this._networkList[i].connected.each(cons(building => {
                Drawf.selected(building.tileX(), building.tileY(), building.block, pals[i]);
            }));

        }
    },
	

});


const _TorqueTransmission = Object.assign(Object.create(_RotPowerCommon),{

    //transmission ratio
    _ratio: [1, 2],
    getRatio() {
        return this._ratio
    },
    setRatio(new_val) {
        this._ratio = new_val
    },
	setStatsExt(table) {
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.transratio") + ":[] ").left();
		let ratio = this._ratio[0]+":"+this._ratio[1];
		table.add(ratio);
	},


});


const _TorqueTransmissionProps = Object.assign(Object.create(_TorqueMulticonnectorProps), {
	getPortRatio(index) {
        let l = this.getBlockData().getAccept()[index];
        if (l == 0) {
            return 0;
        }
        return this.getBlockData().getRatio()[l - 1];
    },
    getPortRatioNeighour(index) {
        return this.getPortRatio(this.getAcceptPorts()[index].index);
    },
    updateExtension() {
        //transmission distribution 
		
        if (this._networkList.length == 0 || this.dead) {
            return;	
        }
        let ratios = this.getBlockData().getRatio();
        let totalmratio = 0;
        let totalm = 0;
        let allpositive = true;
        for (let i = 0; i < ratios.length; i++) {
			if(!this._networkList[i]){return;}
            totalmratio += this._networkList[i].lastInertia * ratios[i];
            totalm += this._networkList[i].lastInertia * this._networkList[i].lastVelocity;
            allpositive = allpositive && this._networkList[i].lastInertia > 0;
        }
        if (totalmratio != 0 && totalm != 0 && allpositive) {
            for (let i = 0; i < ratios.length; i++) {
                let cratio = (this._networkList[i].lastInertia * ratios[i]) / totalmratio;
                this._networkList[i].lastVelocity = totalm * cratio / this._networkList[i].lastInertia;
            }
        }
    },

});
//very basic.
const _TorqueGenerator = Object.assign(Object.create(_RotPowerCommon),{

    //motor max rated speed
    _max_speed: 10,
    //a parameter for motor strength for some motors
    _torque_coeff: 1,
    //motor max rated stength, edit this if you need to change motor strength 
    _maxtorque: 5,
    //motor strength at no rotation speed used for the combustion motors
    _starttorque: 5,
	
	
    getMaxSpeed() {
        return this._max_speed
    },
    setMaxSpeed(new_val) {
        this._max_speed = new_val
    },
    getTorqueCoeff() {
        return this._torque_coeff
    },
    setTorqueCoeff(new_val) {
        this._torque_coeff = new_val
    },
    getMaxTorque() {
        return this._maxtorque
    },
    setMaxTorque(new_val) {
        this._maxtorque = new_val
    },
    getStartTorque() {
        return this._starttorque
    },
    setStartTorque(new_val) {
        this._starttorque = new_val
    },

	setStatsExt(table) {
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.maxspeed") + ":[] ").left();
		table.add((this.getMaxSpeed()*0.1)+"rps");
		
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.maxtorque") + ":[] ").left();
		table.add(this.getMaxTorque()+"KNm");
	},


});


const _TorqueGeneratorProps = Object.assign(Object.create(_RotPowerPropsCommon), {
    _motor_force_mult: 1.0,
	_max_motor_force_mult: 1.0,
    _smoothedForce: null,
    getSmoothedForce() {
        if (!this._smoothedForce) {
            return 0;
        }
        return this._smoothedForce.mean();
    },
    updateExtension() {
        let block = this.getBlockData();
        this.setForce(this.getBlockData().getForce(
            this.getNetwork().lastVelocity,
            block.getMaxSpeed(),
            block.getMaxTorque(),
            block.getStartTorque(),
            block.getTorqueCoeff()
        ) * this.getBuild().edelta() * this._motor_force_mult * this._max_motor_force_mult);
        if (!this._smoothedForce) {
            this._smoothedForce = new WindowedMean(40);
        }
        this._smoothedForce.add(this.getForce());
    },
	

    getMotorForceMult() {
        return this._motor_force_mult
    },
    setMotorForceMult(new_val) {
        this._motor_force_mult = new_val
    },
	setMaxMotorForceMult(new_val) {
        this._max_motor_force_mult = new_val
    },
    displayBars(barsTable) {
        let block = this.getBlockData();

        barsTable.add(new Bar(
            prov(() => Core.bundle.get("stat.unity.torque") + ": " + Strings.fixed(this.getSmoothedForce(), 1) + "/" + Strings.fixed(block.getMaxTorque()*this._max_motor_force_mult, 1)),
            prov(() => Pal.darkishGray),
            floatp(() => this.getSmoothedForce() / (block.getMaxTorque()*this._max_motor_force_mult) ))).growX();
        barsTable.row();
    },
});

const _TorqueConsumer = Object.assign(Object.create(_RotPowerCommon), {
    //speed at which diminshing returns kicks in
    _nominal_speed: 10,
    //a multiplier ontop the dimishing returns, higher the less diminshing the returns, anything above 0.7 will result in a temporary reversal of diminishing returns
    _oversupply_falloff: 0.7,
    //idle friction
    _idle_friction: 0.01,
    //working friction
    _working_friction: 0.1,

    getNominalSpeed() {
        return this._nominal_speed
    },
    setNominalSpeed(new_val) {
        this._nominal_speed = new_val
    },

    getFalloff() {
        return this._oversupply_falloff
    },
    setFalloff(new_val) {
        this._oversupply_falloff = new_val
    },

    getIdleFriction() {
        return this._idle_friction
    },
    setIdleFriction(new_val) {
        this._idle_friction = new_val
    },

    getWorkingFriction() {
        return this._working_friction
    },
    setWorkingFriction(new_val) {
        this._working_friction = new_val
    },
	setStatsExt(table) {
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.nominalspeed") + ":[] ").left();
		table.add((this.getNominalSpeed()*0.1)+"rps");
		
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.idlefric") + ":[] ").left();
		table.add((this.getIdleFriction()*1000)+"Nmv^-2");
		
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.workfric") + ":[] ").left();
		table.add((this.getWorkingFriction()*1000)+"Nmv^-2");
	},


});

const _TorqueConsumerProps = Object.assign(Object.create(_RotPowerPropsCommon), {
    offCondition() {
        return false;
    },
    updateExtension() {
        if (!this.enabled || this.offCondition()) {
            this.setFriction(this.getBlockData().getIdleFriction());
        } else {
            this.setFriction(this.getBlockData().getWorkingFriction());
        }
    },
    efficiency() {
        let block = this.getBlockData();
        let vel = this.getNetwork().lastVelocity;
        let ratio = vel / block.getNominalSpeed();
        if (ratio > 1) {
            ratio = Mathf.log2(ratio);
            ratio = 1 + ((ratio) * block.getFalloff());
        }
        return ratio;
    }
});

const rotGraph = {
	lastInertia: 0,
    lastGrossForceApplied: 0,
    lastNetForceApplied: 0,
    lastVelocity: 0,
    lastFrictionCoefficent: 0,
	copyGraphStatsFrom(graph) {
		this.lastVelocity = graph.lastVelocity;
	},
	updateDirect(){
		let forceapply = 0;
        let friccoeff = 0;
        let iner = 0;
        this.connected.each(cons(building => {
            forceapply += building.getForce();
            friccoeff += building.getFriction();
            iner += building.getInertia();
        }));
        this.lastFrictionCoefficent = friccoeff;
        this.lastGrossForceApplied = forceapply;
        this.lastInertia = iner;
	},
	canConnect(b1,b2) {return b1.getBuild().team == b2.getBuild().team;},
	updateGraph() {
		let netForce = this.lastGrossForceApplied - this.lastFrictionCoefficent * this.lastVelocity * this.lastVelocity;

        this.lastNetForceApplied = netForce;
        //newton's second law
        let acceleration = netForce / this.lastInertia;
        if (this.lastInertia == 0) {
            acceleration = 0;
        }
        this.lastVelocity = this.lastVelocity + acceleration * Time.delta;
        this.lastVelocity = Math.max(0, this.lastVelocity);
	},
	mergeStats(graph){
		let momentumA = this.lastVelocity * this.lastInertia;
        let momentumB = graph.lastVelocity * graph.lastInertia;
        this.lastVelocity = (momentumA + momentumB) / (this.lastInertia + graph.lastInertia);
	},
	injectIntertia(iner){
        this.lastVelocity = ( this.lastVelocity * this.lastInertia ) / (this.lastInertia + iner);
		if((this.lastInertia + iner)==0){
			this.lastVelocity = 0;
		}
	},
};



//draws a non-rectangular quad sprite by directly polling vertex data.
//mindustry's vertex batcher loads float arrays in the following format:
/*
	0-x
	1-y
	2-color (packed)
	3-u
	4-v
	... repeat for every vertex
*/

//r is texture region
function _drawQuad(r, x, y, x2, y2, x3, y3, x4, y4) {
    let color = Draw.getColor().toFloatBits();

    Draw.vert([
        x, y, color, r.u, r.v,
        x2, y2, color, r.u2, r.v,
        x3, y3, color, r.u2, r.v2,
        x4, y4, color, r.u, r.v2
    ]);
}

function _drawQuadA(r, verts) {
    let color = Draw.getColor().toFloatBits();

    Draw.vert([
        verts[0], verts[1], color, r.u, r.v,
        verts[2], verts[3], color, r.u2, r.v,
        verts[4], verts[5], color, r.u2, r.v2,
        verts[6], verts[7], color, r.u, r.v2
    ]);
}

//same as below, but used for sloped surfaces (e.g. bevel gears)
function _drawRotQuad(region, x, y, w, h1, h2, rot, ang1, ang2) {
    if (!Core.settings.getBool("effects")) {
        return;
    }
    let amod1 = Mathf.mod(ang1, 360);
    let amod2 = Mathf.mod(ang2, 360);
    if (amod1 >= 180 && amod2 >= 180) {
        return;
    }

    let s1 = -Mathf.cos(ang1 * Mathf.degreesToRadians);
    let s2 = -Mathf.cos(ang2 * Mathf.degreesToRadians);
    if (amod1 > 180) {
        s1 = -1;
    } else if (amod2 > 180) {
        s2 = 1;
    }
    vert = [
        -w * 0.5, //x1
        Mathf.map(s1, -1, 1, -h1 * 0.5, h1 * 0.5), //y1
        -w * 0.5, //x2
        Mathf.map(s2, -1, 1, -h1 * 0.5, h1 * 0.5), //y2
        w * 0.5, //etc
        Mathf.map(s2, -1, 1, -h2 * 0.5, h2 * 0.5),
        w * 0.5,
        Mathf.map(s1, -1, 1, -h2 * 0.5, h2 * 0.5),
    ];

    //Draw.rect gives us a convinient rotate paramter, we dont have such luxury here.
    let s = Mathf.sin(rot * Mathf.degreesToRadians);
    let c = Mathf.cos(rot * Mathf.degreesToRadians);
    for (let i = 0; i < 8; i += 2) {
        //(x+iy)*(c+is) = xc-sy + i(cy+sx)
        //can be optimised to one temp variable but who cares.
        let nx = vert[i] * c - vert[i + 1] * s;
        let ny = vert[i + 1] * c + vert[i] * s;
        vert[i] = nx;
        vert[i + 1] = ny;
    }
    //pray for the best.
    this._drawQuadA(region, vert);
}


//draws the distorted sprite used to make the rotating shaft effect.
//x and y are assumed to refer to the center of the area.
//w,h is the size in world units of the texture to distort.
//th is the hieght of the texture in world units.
//rot is used for the rotation of the block itself
//ang1 ,ang2 is the two angles the sprite is distorted across, only draws if its visible, aka one of the angles is between 0 and 180
function _drawRotRect(region, x, y, w, h, th, rot, ang1, ang2) {
    if (!Core.settings.getBool("effects")) {
        return;
    }
    if (!region) {
        print("oh no there is no texture");
        return;
    }
    let amod1 = Mathf.mod(ang1, 360);
    let amod2 = Mathf.mod(ang2, 360);
    if (amod1 >= 180 && amod2 >= 180) {
        return;
    }

    let nregion = new TextureRegion(region);
    let scale = h / th;

    let uy1 = nregion.v;
    let uy2 = nregion.v2;
    let ucenter = (uy1 + uy2) / 2;
    let usize = (uy2 - uy1);
    uy1 = ucenter - (usize * scale * 0.5);
    uy2 = ucenter + (usize * scale * 0.5);
    nregion.v = uy1;
    nregion.v2 = uy2;

    let s1 = -Mathf.cos(ang1 * Mathf.degreesToRadians);
    let s2 = -Mathf.cos(ang2 * Mathf.degreesToRadians);
    if (amod1 > 180) {
        nregion.v2 = Mathf.map(0, amod1 - 360, amod2, uy2, uy1);
        s1 = -1;
    } else if (amod2 > 180) {
        nregion.v = Mathf.map(180, amod1, amod2, uy2, uy1);
        s2 = 1;
    }
    s1 = Mathf.map(s1, -1, 1, y - h / 2, y + h / 2);
    s2 = Mathf.map(s2, -1, 1, y - h / 2, y + h / 2);
    Draw.rect(nregion, x, (s1 + s2) * 0.5, w, (s2 - s1), w * 0.5, y - s1, rot);

}

//draws the distorted sprite used to make the sliding worm gear effect.
//x and y are assumed to refer to the center of the area.
//w,h is the size in world units of the texture to distort.
//tw, th is the width, hieght of the texture in world units.
//rot is used for the rotation of the block itself
//step is the width of the repeating pattern, offset is the offset
function _drawSlideRect(region, x, y, w, h, tw, th, rot, step, offset) {
    if (!region) {
        print("oh no there is no texture");
        return;
    }
    let nregion = new TextureRegion(region);
    let scaley = h / th;
    let scalex = w / tw;
    let texw = nregion.u2 - nregion.u;
    let texu = nregion.u;
    nregion.u += Mathf.map(offset % 1.0, 0, 1.0, 0, texw * step / tw);
    nregion.u2 = nregion.u + (scalex * texw);
    Draw.rect(nregion, x, y, w, h, w * 0.5, h * 0.5, rot);

}


const _baseTypes = {
    torqueConnector: {
        block: _RotPowerCommon,
        build: _RotPowerPropsCommon,
		graph: rotGraph,
    },
    torqueGenerator: {
        block: _TorqueGenerator,
        build: _TorqueGeneratorProps,
		graph: rotGraph,
    },
    torqueConsumer: {
        block: _TorqueConsumer,
        build: _TorqueConsumerProps,
		graph: rotGraph,
    },
    torqueTransmission: {
        block: _TorqueTransmission,
        build: _TorqueTransmissionProps,
		graph: rotGraph,
    },
    torqueMultiConnect: {
        block: _RotPowerCommon,
        build: _TorqueMulticonnectorProps,
		graph: rotGraph,
    }
}

for(let key in _baseTypes){
	graphLib.setGraphName(_baseTypes[key],"torque graph");
}


module.exports = {
    energyGraph: rotGraph,
    powerProps: _RotPowerPropsCommon,
    powercommon: _RotPowerCommon,
    dirs: _dirs,
	//legacy support
    torqueExtend(Type, Entity, name, baseType, def, customEnt) {	
		let blankobj = graphLib.init();
		graphLib.addGraph(blankobj, baseType);
		return graphLib.finaliseExtend(Type, Entity,name,blankobj,def,customEnt);
    },
    torqueExtendContent(Type, Entity, name, baseType, def, customEnt) {
        let blankobj = graphLib.init();
		graphLib.addGraph(blankobj, baseType);
		return graphLib.finaliseExtendContent(Type, Entity,name,blankobj,def,customEnt);
    },

    //_TorqueConsumer
    getConnectSidePos: getConnectSidePos,
    drawRotRect: _drawRotRect,
    drawRotQuad: _drawRotQuad,
    drawQuad: _drawQuad,
    drawQuadA: _drawQuadA,
    drawSlideRect: _drawSlideRect,
    torqueFuncs: _Torque_Speed_Funcs,
    baseTypes: _baseTypes
}