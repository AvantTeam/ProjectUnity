//imports
importPackage(Packages.arc.g2d);

//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.

function deepCopy(obj){
  var clone={};
  for(var i in obj){
    if(typeof(obj[i])=="object"&&obj[i]!=null) clone[i]=deepCopy(obj[i]);
    else clone[i]=obj[i];
  }
  return clone;
}
const _dirs=[
	{x: 1, y: 0},
	{x: 0, y: 1},
	{x: -1, y: 0},
	{x: 0, y: -1}
	
]

function sqrd(x){return x*x;}

const _Torque_Speed_Funcs = {
	//s- max rated speed
	//m- max torque
	//h- inital torque
	
	//basic electric motor
	linear(x,s,m,h,k){
		x = Math.min(s,x);
		return k*(s-x)*m/s;
	},
	//used for combustion
	quadratic(x,s,m,h,k){
		let s2 = s*s;
		x = Math.min(s,x);
		let h1 = Mathf.sqrt(m*s2*s2*(m-h))+m*s2;
		return h + ((h*s2-2*h1)*x*x)/(s2*s2) + 2*((h1-h*s2)*x*x)/(s2*s);
	},
	//advanced electric motor
	induction(x,s,m,h,k){
		x = Math.min(s,x);
		return m*Mathf.log(s+1-x)*Mathf.pow(e,-k*sqrd(x-s*0.8)) + (s-x)*(m/s);
	},
	
};

const _RotPowerCommon = {
	
	drawPlace(x, y, rotation, valid){
		for(let i =0;i<this._accept.length;i++){
			if(this._accept[i]==0){continue;}
			Lines.stroke(3.5, Pal.accent);
			let outpos = getConnectSidePos(i,this.size,rotation);
			let dx = (outpos.toPos.x+x) * Vars.tilesize;
			let dy = (outpos.toPos.y+y) * Vars.tilesize;
			Lines.line(dx,dy,dx-_dirs[outpos.dir].x,dy-_dirs[outpos.dir].y);
		}
		this.super$drawPlace(x, y, rotation, valid);
		
    },
	
	_accept:[],
	_network_connector: true,
	_torqueFunc:_Torque_Speed_Funcs.linear,
	//objects that generate torque have a max speed, its torque is dependant on how close the system shaft speed is to it, gear transmission is needed to achieve higher speeds.
	getForce(current_speed,target_speed,max_torque,init_torque,force_coeff){
		return this._torqueFunc(current_speed,target_speed,max_torque,init_torque,force_coeff);
	},
	// the connection points it permits attachment to
	getAccept(){return this._accept;},
	setAccept(newaccept){this._accept=newaccept;},
	
	// if false the network will not 'bridge' across this block when rebuilding (but will still connect to it, like how a diode connects).
	getIsNetworkConnector(){return this._network_connector;},
	setIsNetworkConnector(newaccept){this._network_connector=newaccept;},
	
};
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
function getConnectSidePos(index,size,rotation){
	let side = Mathf.floor(index/size);
	side=(side+rotation)%4;
	let normal = _dirs[(side+3)%4]
	let tangent = _dirs[(side+1)%4]
	let originx = 0;
	let originy = 0;
	
	if(size>1){
		originx+=Mathf.floor(size/2);
		originy+=Mathf.floor(size/2);
		originy-=(size-1);
		if(side>0){
			for(let i = 1;i<=side;i++){
				originx+=_dirs[i].x*(size-1);
				originy+=_dirs[i].y*(size-1);
			}
		}
		originx+=tangent.x*(index%size);
		originy+=tangent.y*(index%size);
	}
	return {
		fromPos:{
			x:originx,
			y:originy
		},
		toPos:{
			x:originx+_dirs[side].x,
			y:originy+_dirs[side].y
		},
		dir:side
		
	}
}


const _RotPowerPropsCommon = {
	
	getConnectSidePos(index){
		return getConnectSidePos(index,this.block.size,this.rotation);
	},
	canConnect(pos){
		for(let i =0;i<this._acceptPorts.length;i++){
			if(this._acceptPorts[i].x+this.tile.x ==pos.x && this._acceptPorts[i].y+this.tile.y ==pos.y){
				return true;
			}
		}
		return false;	
	},
	onProximityUpdate() {
		this.super$onProximityUpdate();
		//removal soon tm
		this.proximityUpdateCustom();
	},
	
	create(block,team){
		let building = this.super$create(block,team);
		this.setNetwork(_EnergyGraph.new(building));
		this.needsNetworkUpdate =  true;
		this.recalcPorts();
		
		
	},
	
	rotation(newrot){
		this.super$rotation(newrot);
		print("rotated to "+newrot);
		this.recalcPorts();
	},
	
	recalcPorts(){
		this.setAcceptPorts([]);
		for(let index = 0 ;index< this.block.size*4;index++){
			if(this.block.getAccept()[index]===1){
				this._acceptPorts.push(this.getConnectSidePos(index).toPos);
			}
		}
		
	},
	onRemoved() {
		this.deleteSelfFromNetwork();
		this.super$onRemoved();
	},
	onDestroyed(){
		this.deleteSelfFromNetwork();
		this.super$onDestroyed();
	},
	deleteSelfFromNetwork(){
		this._dead=true;
		if(this._network){
			this._network.remove(this);
		}
		
	},
	prev_tile_rotation:0,
	updateTile(){
		this.super$updateTile();
		if(this.prev_tile_rotation!=this.rotation){
			this.recalcPorts();
		}
		this.updatePre();
		if(this._network){
			if(this.needsNetworkUpdate){
				this.needsNetworkUpdate=false;
				this._network.rebuildGraph(this);
				if(this.networkSaveState){
					this._network.lastVelocity=this._speedcache;
					this.networkSaveState=0;
				}
			}
			this._network.update();
			this.accumRotation(this._network.lastVelocity);
			this.setSpeedCache(this._network.lastVelocity);
		}
		this.updateExtension();
		this.updatePost();
		this.prev_tile_rotation = this.rotation;
	},
	updateExtension(){},
	updatePost(){},updatePre(){},
	proximityUpdateCustom(){},
	
	drawSelect(){
		this.super$drawSelect();
		if(this._network){
			this._network.connected.each(cons(building=>{
				Drawf.selected(building.tileX(), building.tileY(), building.block, Pal.accent);
			}));
		}
	},
            
	
	//variables for network
	_dead:false,
	_neighbourset:null,
	_acceptPorts:[],
	_network:null,
	_rotation:0,
	_force:0,
	_inertia:10,
	_friction:0.1,
	//this is used to store local speed.
	_speedcache:0,
	getDead(){
		return this._dead;
	},
	getNeighbourset(){
		return this._neighbourset;
	},
	addNeighbour(n){
		if(!this._neighbourset){
			this._neighbourset= ObjectSet.with(n);
		}else{
			this._neighbourset.add(n);
		}
	},
	setNeighbourset(nset){
		this._neighbourset=nset;
	},
	getAcceptPorts(){
		return this._acceptPorts;
	},
	setAcceptPorts(n_rotation){
		this._acceptPorts=n_rotation;
	},
	setNetwork(set) {this._network = set;},
	getNetwork() {return this._network;},
	getForce(){
		return this._force;
	},
	setForce(n_force){
		this._force=n_force;
	},
	getInertia(){
		return this._inertia;
	},
	setInertia(n_inertia){
		this._inertia=n_inertia;
	},
	getRotation(){
		return this._rotation;
	},
	setRotation(n_rotation){
		this._rotation=n_rotation;
	},
	accumRotation(n_rotation){
		this._rotation+=n_rotation;
		this._rotation=this._rotation%360;
	},
	getSpeedCache(){
		return this._speedcache;
	},
	setSpeedCache(ns){
		this._speedcache=ns;
	},
	getFriction(){
		return this._friction;
	},
	setFriction(n_friction){
		this._friction=n_friction;
	},
	write(stream){
		this.super$write(stream);
		stream.f(this._force);
		stream.f(this._inertia);
		stream.f(this._friction);
		stream.f(this._speedcache);
	},
	read(stream,revision){
		this.super$read(stream,revision);
		this._force=stream.f();
		this._inertia=stream.f();
		this._friction=stream.f();
		this._speedcache =  stream.f();
		this.networkSaveState = 1;
	}
	
}

const _TorqueGenerator = Object.assign({
	
	//motor max rated speed
	_max_speed:10,
	//a parameter for motor strength for some motors
	_torque_coeff:1,
	//motor max rated stength, edit this if you need to change motor strength 
	_maxtorque:5,
	//motor strength at no rotation speed used for the combustion motors
	_starttorque:5,
	getMaxSpeed(){return this._max_speed},
	setMaxSpeed(new_val){this._max_speed=new_val},
	getTorqueCoeff(){return this._torque_coeff},
	setTorqueCoeff(new_val){this._torque_coeff=new_val},
	getMaxTorque(){return this._maxtorque},
	setMaxTorque(new_val){this._maxtorque=new_val},
	getStartTorque(){return this._starttorque},
	setStartTorque(new_val){this._starttorque=new_val},
},_RotPowerCommon);

const _TorqueGeneratorProps = Object.assign(Object.create(_RotPowerPropsCommon),{
	_motor_force_mult:1.0,
	updateExtension(){
		let block = this.block;
		this.setForce(_RotPowerCommon.getForce(
				this.getNetwork().lastVelocity,
				block.getMaxSpeed(),
				block.getMaxTorque(),
				block.getStartTorque(),
				block.getTorqueCoeff()
			)*this.edelta()*this._motor_force_mult);
	},
	
	getMotorForceMult(){return this._motor_force_mult},
	setMotorForceMult(new_val){this._motor_force_mult=new_val},
	
	
});

const _EnergyGraph = {
	lastInertia:0,
	lastGrossForceApplied:0,
	lastNetForceApplied:0,
	lastVelocity:0,
	lastFrictionCoefficent:0,
	lastFrameUpdated:0,
	//'connected' is the graph's field of building set
	new(building) {
		const graph = Object.create(_EnergyGraph);
		graph.connected = ObjectSet.with(building);
		return graph;
		
	},
	
	copyGraph(building){
		const copygraph = _EnergyGraph.new(building)
		copygraph.lastVelocity=this.lastVelocity;
		return copygraph;
		
	},
	
	update(){
		if(Core.graphics.getFrameId() == this.lastFrameUpdated)
            return;
		this.lastFrameUpdated = Core.graphics.getFrameId();
		this.updateStat();
		
		let netForce = this.lastGrossForceApplied - this.lastFrictionCoefficent*this.lastVelocity*this.lastVelocity;
		this.lastNetForceApplied = netForce;
		//newton's second law
		let acceleration = netForce/this.lastInertia;
		if(this.lastInertia==0){
			acceleration=0;
		}
		this.lastVelocity = this.lastVelocity+acceleration*Time.delta;
		
	},
	
	updateStat(){
		let forceapply = 0;
		let friccoeff = 0;
		let iner = 0;
		this.connected.each(cons(building=>{
			forceapply+=building.getForce();
			friccoeff+=building.getFriction();
			iner+=building.getInertia();
		}));
		this.lastFrictionCoefficent = friccoeff;
		this.lastGrossForceApplied = forceapply;
		this.lastInertia = iner;
		
	},
	addBuilding(building){
		this.connected.add(building);
		building.setNetwork(this);
	},
	mergeGraph(graph){
		//print(graph);
		if(!graph){return;}
		//optimisation over original, only merging the smaller graph, makes placing individual blocks in large networks better.
		if(graph.connected.size>this.connected.size){
			graph.mergeGraph(this);
			return;
		}
		//avoiding unupdated graphs connecting.
		this.updateStat();
		graph.updateStat();
		let momentumA = this.lastVelocity*this.lastInertia;
		let momentumB = graph.lastVelocity*graph.lastInertia;
		this.lastVelocity = (momentumA+momentumB)/(this.lastInertia+graph.lastInertia);
		graph.connected.each(cons(building=>{
			//some buildings may be connected to two seperate networks due to how gear transmission works.
			if(!this.connected.contains(building)){
				this.connected.add(building);
				building.setNetwork(this);
			}
			
		}));
		
	},
	remove(building){
		if(!this.connected.contains(building)){return;}
		if(!building.getNeighbourset()){return;}
		print("begining remove");
		building.setNetwork(null);
		this.connected.clear();
		let networksadded = null;
		let newnets=0;
		
		//need to erase all the graph references of the adjacent blocks first, but not with null since each tile is garanteed* to have a graph
		building.getNeighbourset().each(cons(neighbour=>{
			neighbour.setNetwork(this.copyGraph(neighbour));
		}));
		
		building.getNeighbourset().each(cons(neighbour=>{
			
			neighbour.getNeighbourset().remove(building);
			if(!networksadded || !networksadded.contains(neighbour.getNetwork())){
				if(!networksadded){
					networksadded=ObjectSet.with(neighbour.getNetwork());
				}else{
					networksadded.add(neighbour.getNetwork());
				}
				neighbour.getNetwork().rebuildGraph(neighbour);
			}
		}));
	},
	
	//this function is graph independant and can possibily be moved outside
	rebuildGraph(building){
		this.rebuildGraphWithSet(building,ObjectSet.with(building));
	},
	rebuildGraphWithSet(building,searched){
		if(!building.block.getAccept()){
			print("oh no, accept ports not found");
			return;
		}
		let acceptports = building.block.getAccept();
		let prevbuilding = null;
		searched.add(building);
		for(let port =0;port<acceptports.length;port++){
			if(acceptports[port]==0){continue;}
			let portinfo = building.getConnectSidePos(port);
			let portpos = portinfo.toPos;
			
			if(!building.tile){return;}
			let tile = building.tile.nearby(portpos.x,portpos.y);
			// guess the world doesnt exist or something
			if(!tile){return;}
			if(tile.block().getIsNetworkConnector!=undefined ){
				let conbuild = tile.bc();
				if(conbuild==prevbuilding||conbuild.getDead()){
					continue;
				}	
				let thisgraph = building.getNetwork();
				
				let fpos = portinfo.fromPos;
				fpos.x+=building.tile.x;
				fpos.y+=building.tile.y;
				if(!conbuild.canConnect(fpos)){
					continue;
				}
				building.addNeighbour(conbuild);
				//buildings without a network instance are assumed to be dead
				if(!thisgraph.connected.contains(conbuild) && conbuild.getNetwork()){
					if(conbuild.getNetwork()!=thisgraph){
						if(conbuild.getNetwork().connected.contains(conbuild)){
							thisgraph.mergeGraph(conbuild.getNetwork());
						}else{
							thisgraph.addBuilding(conbuild);
						}
						//placing it outside will result in the entire graph be researched for any nodes that havent beeen assimilated into the graph yet.
						//may be problematic and cause lag^ but is a good way to rebuild the <entire> graph from any one point.
						//placing it inside will only search available nodes that are not blocked by already assimilated nodes.
						if(tile.block().getIsNetworkConnector()&&!searched.contains(conbuild)){
							thisgraph.rebuildGraphWithSet(conbuild,searched);
						}
					}
					
					
				}
				prevbuilding =conbuild;
			}
			
			
		}
		
	}	
}
//draws the distorted sprite used to make the rotating shaft effect.
//x and y are assumed to refer to the center of the area.
//w,h is the size in world units of the texture to distort.
//rot is used for the rotation of the block itself
//ang1 ,ang2 is the two angles the sprite is distorted across, only draws if its visible, aka one of the angles is between 0 and 180
function _drawRotRect(region, x, y, w, h, rot, ang1, ang2){
	if(region===undefined){
		print("oh no texture undefined");
		return;
	}
	let amod1 = Mathf.mod(ang1,360);
	let amod2 = Mathf.mod(ang2,360);
	if(amod1>=180 && amod2>=180){return;}
	
	let nregion = new TextureRegion(region);
	let scale = h/8;
	
	let uy1 = nregion.v;
	let uy2 = nregion.v2;
	let ucenter = (uy1+uy2)/2;
	let usize = (uy2-uy1);
	uy1 = ucenter-(usize*scale*0.5);
	uy2 = ucenter+(usize*scale*0.5);
	nregion.v =uy1;
	nregion.v2 =uy2;
	
	let s1 = -Mathf.cos(ang1*Mathf.degreesToRadians);
	let s2 = -Mathf.cos(ang2*Mathf.degreesToRadians);
	if(amod1>180){
		nregion.v2 = Mathf.map(0,amod1-360,amod2,uy2,uy1);	
		s1 = -1;
	}else if(amod2>180){
		nregion.v = Mathf.map(180,amod1,amod2,uy2,uy1);	
		s2 = 1;
	}
	s1 = Mathf.map(s1,-1,1,y-h/2,y+h/2);
	s2 = Mathf.map(s2,-1,1,y-h/2,y+h/2);
	Draw.rect(nregion,x,(s1+s2)/2,w,(s2-s1),w/2,y-s1,rot);
	
}

module.exports={
	energyGraph: _EnergyGraph,
	powerProps: _RotPowerPropsCommon,
	powercommon: _RotPowerCommon,
	dirs: _dirs,
	powerUser(Type,Entity,name,def,customEnt){
		const block=Object.create(_RotPowerCommon);
		Object.assign(block,def);
		const rotpowerBlock=extendContent(Type,name,block);
		rotpowerBlock.buildType=()=>
			{	
				let building =  extend(Entity,Object.create(Object.assign(deepCopy(_RotPowerPropsCommon),deepCopy(customEnt))));
				building.block = rotpowerBlock;
				return building;
			};
		return rotpowerBlock;
	},
	torqueGenerator(Type,Entity,name,def,customEnt){
		const block=Object.create(_TorqueGenerator);
		Object.assign(block,def);
		const rotpowerBlock=extendContent(Type,name,block);
		rotpowerBlock.buildType=()=>
			{	
				let building =  extend(Entity,Object.create(Object.assign(deepCopy(_TorqueGeneratorProps),deepCopy(customEnt))));
				building.block = rotpowerBlock;
				return building;
			};
		return rotpowerBlock;
	},
	drawRotRect: _drawRotRect,
	torqueFuncs: _Torque_Speed_Funcs
	
}