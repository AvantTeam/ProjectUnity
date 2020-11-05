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
	
	//basic motor/generator
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
			Lines.stroke(3.5, Color.white);
			let outpos = getConnectSidePos(i,this.size,rotation);
			let dx = (outpos.toPos.x+x) * Vars.tilesize;
			let dy = (outpos.toPos.y+y) * Vars.tilesize;
			let dir = _dirs[outpos.dir];
			Lines.line(dx-dir.x,dy-dir.y,dx-dir.x*2,dy-dir.y*2);
		}
		this.super$drawPlace(x, y, rotation, valid);
		
    },
	
	setStats(){
		this.super$setStats();
		const sV = new StatValue({
			display(table){
				table.add("test [cyan]1[white]2[gray]3");
			}
		});
		this.stats.add(Stat.powerDamage, sV);
		
	},
	otherStats(){},
	
	_accept:[],
	_multi_graph_connector:false,
	_network_connector: true,
	_use_original_update: true,
	_torqueFunc:_Torque_Speed_Funcs.linear,
	//objects that generate torque have a max speed, its torque is dependant on how close the system shaft speed is to it, gear transmission is needed to achieve higher speeds.
	getForce(current_speed,target_speed,max_torque,init_torque,force_coeff){
		return this._torqueFunc(current_speed,target_speed,max_torque,init_torque,force_coeff);
	},
	// whether it calls the super's updateTile
	getUseOgUpdate(){return this._use_original_update;},
	setUseOgUpdate(newaccept){this._use_original_update=newaccept;},
	
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
				return this._acceptPorts[i].index;
			}
		}
		return -1;	
	},
	onProximityUpdate() {
		this.super$onProximityUpdate();
		//removal soon tm
		this.proximityUpdateCustom();
	},
	display(table){
		this.super$display(table);
		if(!this._network){return;}
		let ps = " " + StatUnit.perSecond.localized();
		let net = this._network;
		let speed = this._network.lastVelocity/60.0;
		table.row();
		table.table(
			cons(sub=>{
				sub.clearChildren();
				sub.left();
				sub.label(prov(()=>
					{
						return Strings.fixed(net.lastVelocity/6.0,2)+"r"+ps;
					})).color(Color.lightGray);
					
				/*l.update(run(()=>{
					
				});*/
			})
		).left();
	},
	
	create(block,team){
		let building = this.super$create(block,team);
		this.initAllNets(building);
		this.needsNetworkUpdate =  true;
		this.prev_tile_rotation =  -1;
		
		
	},
	
	rotation(newrot){
		this.super$rotation(newrot);
		print("rotated to "+newrot);
		this.recalcPorts();
	},
	
	recalcPorts(){
		this.setAcceptPorts([]);
		for(let index = 0 ;index< this.block.size*4;index++){
			if(this.block.getAccept()[index]!==0){
				let pos = this.getConnectSidePos(index).toPos;
				let fpos = this.getConnectSidePos(index).fromPos;
				pos.index = index;
				pos.fromx = fpos.x;
				pos.fromy = fpos.y;
				this._acceptPorts.push(pos);
			}
		}
		
	},
	onRemoved() {
		this.deleteSelfFromNetwork();
		this.deleteFromNeighbours();
		this.super$onRemoved();
	},
	onDestroyed(){
		this.deleteSelfFromNetwork();
		this.deleteFromNeighbours();
		this.super$onDestroyed();
	},
	deleteFromNeighbours(){
		if(this.getNeighbourArray()){
			this.eachNeighbour(neighbourindex=>{
				neighbourindex.build.removeNeighbour(this);
			});
		}
	},
	deleteSelfFromNetwork(){
		this._dead=true;
		if(this._network){
			this._network.remove(this);
		}
		
	},
	prev_tile_rotation:-1,
	updateTile(){
		if(this.block.getUseOgUpdate()){
			this.super$updateTile();
		}
		if(this.prev_tile_rotation!=this.rotation){
			if(this.prev_tile_rotation!=-1){
				print("rotated to "+this.rotation);
				this.deleteSelfFromNetwork();
				this.deleteFromNeighbours();
				this._dead=false;
				this.initAllNets(this);
			}
			this.recalcPorts();
			this.needsNetworkUpdate=true;
		}
		this.updatePre();
		this.updateNetworks();
		this.updateExtension();
		this.updatePost();
		this.prev_tile_rotation = this.rotation;
	},
	updateNetworks(){
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
	_graphset:null,
	_dead:false,
	_acceptPorts:[],
	_neighbourArray:null,
	_network:null,
	_rotation:0,
	_force:0,
	_inertia:10,
	_friction:0.1,
	//this is used to store local speed.
	_speedcache:0,
	initAllNets(buildingnew){
		//_EnergyGraph.new(building)
		this.recalcPorts();
		this.setNetwork(_EnergyGraph.new(buildingnew));
	},
	getDead(){
		return this._dead;
	},
	getNeighbourArray(){
		return this._neighbourArray;
	},
	samepos(b){
		return b.tileX()==this.tileX() && b.tileY()==this.tileY();
	},
	getNeighbour(building){
		if(!this._neighbourArray){return;}
		let found = null;
		this.eachNeighbour(neigh=>{
			if(neigh.build.samepos(building)){
				found = neigh;
			}
		});
		return found;	
	},
	eachNeighbour(func){
		let prev=null;
		for(let i =0;i<this._neighbourArray.length;i++){
			if(prev==this._neighbourArray[i] || !this._neighbourArray[i]){continue;}
			func(this._neighbourArray[i]);
			prev=this._neighbourArray[i];
		}
	},
	countNeighbours(){
		if(!this._neighbourArray){return 0;}
		let found = 0;
		this.eachNeighbour(neigh=>{
			found++;
		});
		return found;	
	},
	removeNeighbour(building){
		for(let i =0;i<this._neighbourArray.length;i++){
			if(this._neighbourArray[i]&&this._neighbourArray[i].build.samepos(building)){
				this._neighbourArray[i]=null;
			}
		}
	},
	addNeighbour(n){
		if(!this._neighbourArray){
			let temp = [];
			temp[n.portindex] = n;
			this.setNeighbourArray(temp);
			return;
		}
		this._neighbourArray[n.portindex] = n;
	},
	setNeighbourArray(nset){
		this._neighbourArray=nset;
	},
	getAcceptPorts(){
		return this._acceptPorts;
	},
	setAcceptPorts(n_rotation){
		this._acceptPorts=n_rotation;
	},
	getConnectedNeighours(index){
		return this.getAcceptPorts();
	},
	setNetwork(set) {this._network = set;},
	clearNetworks(set) {this._network = null;},
	replaceNetwork(old,set) {this._network = set;return true;},
	getNetwork() {return this._network;},
	hasNetwork(net) {return this._network.id==net.id;},
	getNetworkOfPort(index){
		return this._network;
	},
	setNetworkOfPort(index,net){
		this._network=net;
	},
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
		this._rotation=this._rotation%(360*24);
	},
	getFriction(){
		return this._friction;
	},
	setFriction(n_friction){
		this._friction=n_friction;
	},
	getSpeedCache(){
		return this._speedcache;
	},
	setSpeedCache(ns){
		this._speedcache=ns;
	},
	readSpeedCache(stream,revision){
		this._speedcache =  stream.f();
	},
	writeSpeedCache(stream){
		stream.f(this._speedcache);
	},
	
	write(stream){
		this.super$write(stream);
		stream.f(this._force);
		stream.f(this._inertia);
		stream.f(this._friction);
		this.writeSpeedCache(stream);
	},
	read(stream,revision){
		this.super$read(stream,revision);
		this._force=stream.f();
		this._inertia=stream.f();
		this._friction=stream.f();
		this.readSpeedCache(stream,revision);
		this.networkSaveState = 1;
	}
	
}




const _TorqueMulticonnectorProps = Object.assign(Object.create(_RotPowerPropsCommon),{
	_networkList:[],
	_networkRots:[],
	_networkSpeeds:[],
	getNetworks(){
		return this._networkList;
	},
	setNetworks(nv){
		this._networkList=nv;
	},
	clearNetworks(set) {
		for(let i = 0;i<this._networkList.length;i++){
			this._networkList[i] = null;
		}
	},
	hasNetwork(net) {
		for(let i = 0;i<this._networkList.length;i++){
			if(this._networkList[i].id==net.id){
				return true;
			}
		}
		return false;
	},
	initAllNets(buildingnew){
		//_EnergyGraph.new(building)
		this.recalcPorts();
		let templist = [];
		this._networkList=[];
		let portarray = this.block.getAccept();
		let networksmade = 0;
		for(let i = 0;i<portarray.length;i++){
			if(!templist[portarray[i]-1]&&portarray[i]!=0){
				templist.push(_EnergyGraph.new(buildingnew));
				networksmade++;
			}
		}
		this.setNetworks(templist);
	},
	getNetworkFromSet(index){
		return this._networkList[index];
	},
	getNetworkRotation(index){
		let r =  this._networkRots[index];
		if(r===undefined){return 0;}
		return this._networkRots[index];
	},
	setNetworkFromSet(index,net){
		for(let i = 0;i<this._networkList.length;i++){
			if(this._networkList[i].id==net.id){
				return false;
			}
		}
		this._networkList[index]=net;
		return true;
	},
	replaceNetwork(old,set) {
		let index = -1;
		for(let i = 0;i<this._networkList.length;i++){
			if(this._networkList[i]&&this._networkList[i].id==old.id){
				index=i;
			}
			if(set&&this._networkList[i].id==set.id){
				return false;
			}
		}
		if(index==-1){return false;}
		this._networkList[index]=set;
		return true;
	},
	getNetworkOfPort(index){
		let l = this.block.getAccept()[index];
		if(l==0){return undefined;}
		return this._networkList[l-1];
	},
	setNetworkOfPort(index,net){
		let l = this.block.getAccept()[index];
		if(l==0){return;}
		this._networkList[l-1]=net;
	},
	getPortRatio(index){
		let l = this.block.getAccept()[index];
		if(l==0){return 0;}
		return this.block.getRatio()[l-1];
	},
	getPortRatioNeighour(index){
		return this.getPortRatio(this.getAcceptPorts()[index].index);
	},
	getConnectedNeighours(index){
		let portarray = this.getAcceptPorts();
		let targetport = this.block.getAccept()[index];
		let output = [];
		
		for(let i = 0;i<portarray.length;i++){
			if(this.block.getAccept()[portarray[i].index]==targetport){
				output.push(portarray[i]);
			}
		}
		return output;
	},
	drawSelect(){
		this.super$drawSelect();
		if(this._networkList.length==0){return;}
		let pals = [Pal.accent,Pal.redSpark, Pal.plasticSmoke, Pal.lancerLaser];
		for(let i = 0;i<this._networkList.length;i++){
			this._networkList[i].connected.each(cons(building=>{
				Drawf.selected(building.tileX(), building.tileY(), building.block, pals[i]);
			}));
			
		}
	},
       
	updateNetworks(){
		if(this._networkList.length==0){return;}
		if(this.needsNetworkUpdate){
			this.needsNetworkUpdate=false;
			let covered = [];
			let portarray = this.block.getAccept();
			for(let i = 0;i<portarray.length;i++){
				if(portarray[i]==0||covered[portarray[i]-1]){continue;}
				this.getNetworkOfPort(portarray[i]-1).rebuildGraphIndex(this,i);
				covered[portarray[i]-1]=1;
			}
			if(this.networkSaveState){
				for(let i = 0;i<this._networkList.length;i++){
					if(this._networkSpeeds[i]){
						this._networkList[i].lastVelocity=Math.max(this._networkList[i].lastVelocity,this._networkSpeeds[i]);
					}
				}
			}
			this.networkSaveState=0;
		}
		
		//rotation for vfx
		for(let i = 0;i<this._networkList.length;i++){
			this._networkList[i].update();
			if(this._networkRots[i]=== undefined ){
				this._networkRots[i]=0;
			}
			this._networkSpeeds[i] = this._networkList[i].lastVelocity;
			this._networkRots[i]+=this._networkList[i].lastVelocity;
			this._networkRots[i]=Mathf.mod(this._networkRots[i],360);
		}
		this.needsNetworkUpdate=false;
	},
	updateExtension(){
		
		if(this._networkList.length==0){return;}
		
		
		this.updateExtension2();
		//rotation for vfx
		for(let i = 0;i<this._networkList.length;i++){
			if(this._networkRots[i]=== undefined ){
				this._networkRots[i]=0;
			}
			this._networkRots[i]+=this._networkList[i].lastVelocity;
			this._networkRots[i]=Mathf.mod(this._networkRots[i],360);
		}
	},
	updateExtension2(){},
	deleteSelfFromNetwork(){
		this._dead=true;
		if(this._networkList.length==0){return;}
		for(let i = 0;i<this._networkList.length;i++){
			this._networkList[i].remove(this);
		}
	},
	readSpeedCache(stream,revision){
		
		let netam =  stream.i();
		//print("loading multiconnector with "+netam+" connections");
		for(let i = 0;i<netam;i++){
			this._networkSpeeds[i]=stream.f();
			//print("net "+i+"'s speeds");
			if(this._networkSpeeds[i]===undefined){
				this._networkSpeeds[i]=0;
			}
		}
	},
	writeSpeedCache(stream){
		stream.i(this._networkList.length);
		for(let i = 0;i<this._networkList.length;i++){
			if(this._networkSpeeds[i]===undefined){
				this._networkSpeeds[i]=0;
			}
			stream.f(this._networkSpeeds[i]);
		}
	},
	
});


const _TorqueTransmission = Object.assign({
	
	//transmission ratio
	_ratio:[1,2],
	getRatio(){return this._ratio},
	setRatio(new_val){this._ratio=new_val},
	
	
	
},_RotPowerCommon);


const _TorqueTransmissionProps = Object.assign(Object.create(_TorqueMulticonnectorProps),{
	
	updateExtension2(){
		//transmission distribution 
		let ratios = this.block.getRatio();
		let totalmratio = 0;
		let totalm = 0;
		let allpositive = true;
		for(let i = 0;i<ratios.length;i++){
			totalmratio += this._networkList[i].lastInertia*ratios[i];
			totalm+=this._networkList[i].lastInertia*this._networkList[i].lastVelocity;
			allpositive = allpositive && this._networkList[i].lastInertia>0;
		}
		if(totalmratio!=0&&totalm!=0&&allpositive){
			for(let i = 0;i<ratios.length;i++){
				let cratio = (this._networkList[i].lastInertia*ratios[i])/totalmratio;
				this._networkList[i].lastVelocity = totalm*cratio/this._networkList[i].lastInertia;
			}
		}
	},
	
});

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
	_smoothedForce:null,
	getSmoothedForce(){
		if(!this._smoothedForce){
			return 0;
		}
		return this._smoothedForce.mean();
	},
	updateExtension(){
		let block = this.block;
		this.setForce(_RotPowerCommon.getForce(
				this.getNetwork().lastVelocity,
				block.getMaxSpeed(),
				block.getMaxTorque(),
				block.getStartTorque(),
				block.getTorqueCoeff()
			)*this.edelta()*this._motor_force_mult);
		if(!this._smoothedForce){
			this._smoothedForce = new WindowedMean(40);
		}
		this._smoothedForce.add(this.getForce());
	},
	
	getMotorForceMult(){return this._motor_force_mult},
	setMotorForceMult(new_val){this._motor_force_mult=new_val},
	
	displayBars(barsTable){
		this.super$displayBars(barsTable);
		let block = this.block;
		
		barsTable.add(new Bar(
			prov(()=>Core.bundle.get("stat.unity.torque")+": "+Strings.fixed(this.getSmoothedForce(), 1)+"/"+Strings.fixed(block.getMaxTorque(), 1)),
			prov(()=>Pal.darkishGray), 
			floatp(() => this.getSmoothedForce()/ block.getMaxTorque()))).growX();
		barsTable.row();
	},
});

const _TorqueConsumer = Object.assign(Object.create(_RotPowerCommon),{
	//speed at which diminshing returns kicks in
	_nominal_speed: 10,
	//a multiplier ontop the dimishing returns, higher the less diminshing the returns, anything above 2 will result in a temporary reversal of diminishing returns
	_oversupply_falloff: 1.5,
	//idle friction
	_idle_friction: 0.01,
	//working friction
	_working_friction: 0.1,
	
	getNominalSpeed(){return this._nominal_speed},
	setNominalSpeed(new_val){this._nominal_speed=new_val},
	
	getFalloff(){return this._oversupply_falloff},
	setFalloff(new_val){this._oversupply_falloff=new_val},
	
	getIdleFriction(){return this._idle_friction},
	setIdleFriction(new_val){this._idle_friction=new_val},
	
	getWorkingFriction(){return this._working_friction},
	setWorkingFriction(new_val){this._working_friction=new_val},
	
	
	
});

const _TorqueConsumerProps = Object.assign(Object.create(_RotPowerPropsCommon),{
	offCondition(){return false;},
	updateExtension(){
		if (!this.enabled||this.offCondition()){
			this.setFriction(this.block.getIdleFriction());
		}else{
			this.setFriction(this.block.getWorkingFriction());
		}
	},
	efficiency() {
		let block = this.block;
		let vel = this.getNetwork().lastVelocity;
		let p = this.super$efficiency();
		let ratio = vel/block.getNominalSpeed();
		if(ratio>1){
			ratio = Mathf.sqrt(ratio);
			ratio = 1+((ratio-1)*block.getFalloff());
		}
		p*=ratio;
		return p;
	}
});



var uniqueidincre = 0;

function getnetID(){
	uniqueidincre++;
	return uniqueidincre;
}

const _EnergyGraph = {
	lastInertia:0,
	lastGrossForceApplied:0,
	lastNetForceApplied:0,
	lastVelocity:0,
	lastFrictionCoefficent:0,
	lastFrameUpdated:0,
	id:0,
	relativeRatio:1,
	//'connected' is the graph's field of building set
	new(building) {
		const graph = Object.create(_EnergyGraph);
		graph.id = getnetID();
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
	addBuilding(building,connectIndex){
		this.connected.add(building);
		building.setNetworkOfPort(connectIndex,this);
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
		
		
		//print("merging:"+graph.connectedToString());
		//print("into:"+this.connectedToString());
		graph.connected.each(cons(building=>{
			//some buildings may be connected to two seperate networks due to how gear transmission works.
			if(!this.connected.contains(building)){
				if(building.replaceNetwork(graph,this)){
					this.connected.add(building);
				}
			}
			
		}));
		//print("result:"+this.connectedToString());
		
	},
	remove(building){
		if(!this.connected.contains(building)){return;}
		let c = building.countNeighbours();
		if(c===0){print("removed, no neighbours lol");return;}
		//print("-------------begining remove");
		if(c===1){
			//todo: find out why this isnt triggering.
			//print("only one neighbour, removing without rebuilding network.");
			this.connected.remove(building);
			building.eachNeighbour(neighbourindex=>{
				neighbourindex.build.removeNeighbour(building);
			});
			return;
		}
		this.connected.clear();
		let networksadded = null;
		let newnets=0;
		//print(building.getNeighbourArray());
		//need to erase all the graph references of the adjacent blocks first, but not with null since each tile is garanteed* to have a graph
		//having multi-connector blocks makes this hard to brain
		building.eachNeighbour(neighbour=>{
			let copynet = building.getNetworkOfPort(neighbour.portindex);
			if(copynet==this){
				let selfref = neighbour.build.getNeighbour(building);
				neighbour.build.setNetworkOfPort(selfref.portindex,copynet.copyGraph(neighbour.build) );
			}
		});
		
		building.eachNeighbour(neighbourindex=>{
			let neighbour = neighbourindex.build;
			if(building.getNetworkOfPort(neighbourindex.portindex)==this){
				let selfref = neighbour.getNeighbour(building);
				let neinet = neighbour.getNetworkOfPort(selfref.portindex);
				if(!networksadded || !networksadded.contains(neinet)){
					if(!networksadded){
						networksadded=ObjectSet.with(neinet);
					}else{
						networksadded.add(neinet);
					}
					neinet.rebuildGraphIndex(neighbour,selfref.portindex);
				}
			}
		});
		building.replaceNetwork(this,null);
	},
	
	rebuildGraph(building){
		//print("----Starting new rebuild");
		this.rebuildGraphWithSet(building,ObjectSet.with(building),-1);
	},
	rebuildGraphIndex(building,index){
		//print("----Starting new rebuild");
		this.rebuildGraphWithSet(building,ObjectSet.with(building),index);
	},
	rebuildGraphWithSet(root,searched,rootindex){
		
		//guess ill die
		//thank god this was already tail recursed.
		let tree ={
			complete:false,
			parent: null,
			children:[],
			build:root,
			parentConnectPort:rootindex,
		}
		let current = tree;
		
		//debug
		let total = 0;
		//
		
		mainloop:
		while(current!=null){
			
			total++;
			
			let building = current.build;
			let index = current.parentConnectPort;
			
			if(!building.block.getAccept()){
				print("oh no, accept ports not found");
				return;
			}
			let acceptports = building.getAcceptPorts();
			if(index!=-1){
				acceptports = building.getConnectedNeighours(index);
			}
			let prevbuilding = null;
			searched.add(building);
			//print("rebuilding from:"+building.block.localizedName+" at port"+ index);
			//print("ports to scan:"+acceptports);
			
			for(let port =0;port<acceptports.length;port++){
				let portindex = acceptports[port].index;
				if(!building.getNetworkOfPort(portindex)){
					continue;
				}
				let portinfo = acceptports[port];
				if(!building.tile){return;}
				let tile = building.tile.nearby(portinfo.x,portinfo.y);
				// guess the world doesnt exist or something
				if(!tile){return;}
				if(tile.block().getIsNetworkConnector!=undefined ){
					//conbuild -> connected building
					let conbuild = tile.bc();
					if(conbuild==prevbuilding||conbuild.getDead()){
						continue;
					}	
					let thisgraph = building.getNetworkOfPort(portindex);
					
					let fpos = {x:portinfo.fromx,y:portinfo.fromy};
					fpos.x+=building.tile.x;
					fpos.y+=building.tile.y;
					let connectIndex = conbuild.canConnect(fpos);
					if(connectIndex==-1){
						continue;
					}
					//print("found suitable connecting building: current block is connected at port "+connectIndex+" of other building");
					building.addNeighbour({build:conbuild, portindex:portindex});
					conbuild.addNeighbour({build:building, portindex:connectIndex});
					//buildings without a network instance are assumed to be dead
					let connet = conbuild.getNetworkOfPort(connectIndex);
					if(!thisgraph.connected.contains(conbuild) && connet){
						if(!building.hasNetwork(connet)){
							if(connet.connected.contains(conbuild)){
								
								thisgraph.mergeGraph(connet);
								thisgraph = building.getNetworkOfPort(portindex);
								//print("external net:"+connet.connectedToString());
								//print("network merged:"+thisgraph.connectedToString());
								
							}else{
								//print("network doesnt not contain target building, assuming hollowed network, directly adding...");
								//print("network polled:"+connet.connectedToString());
								thisgraph.addBuilding(conbuild,connectIndex);
							}
							//placing it outside will result in the entire graph be re-searched for any nodes that havent beeen assimilated into the graph yet.
							//may be problematic and cause lag^ but is a good way to rebuild the <entire> graph from any one point.
							//placing it inside will only search available nodes that are not blocked by already assimilated nodes.
							if(tile.block().getIsNetworkConnector()&&!searched.contains(conbuild)){
								current.children.push({
									complete:false,
									parent: current,
									children:[],
									build:conbuild,
									parentConnectPort:connectIndex,
								});
								//thisgraph.rebuildGraphWithSet(conbuild,searched,connectIndex);
							}
						}else{
							//print("Graphs are the same(id:"+connet.id+"), skipping..");
						}	
					}else{
						//print("Graph already contains building, skipping..");
					}
					prevbuilding =conbuild;
				}
			}
			if(current.children.length>0){
				for(let i =0;i<current.children.length;i++){
					if(!current.children[i].complete){
						current = current.children[i];
						continue mainloop;
					}
				}
			}
			current.complete=true;
			current = current.parent;
			
		}
		
		//print("total traversals:"+total);
	},
	//debug
	connectedToString(){
		let s = "Network:"+this.id+":";
		this.connected.each(cons(building=>{
			s+=building.block.localizedName+", "
		}));
		return s;
	}
}


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
function _drawQuad(r, x, y, x2 ,y2, x3, y3, x4 ,y4){
	let color = Draw.getColor().toFloatBits();
	
	Draw.vert([
		x,y,color,r.u,r.v,
		x2,y2,color,r.u2,r.v,
		x3,y3,color,r.u2,r.v2,
		x4,y4,color,r.u,r.v2
	]);
}
function _drawQuadA(r, verts){
	let color = Draw.getColor().toFloatBits();
	
	Draw.vert([
		verts[0],verts[1],color,r.u,r.v,
		verts[2],verts[3],color,r.u2,r.v,
		verts[4],verts[5],color,r.u2,r.v2,
		verts[6],verts[7],color,r.u,r.v2
	]);
}

//same as below, but used for sloped surfaces (e.g. bevel gears)
function _drawRotQuad(region, x, y, w, h1,h2,  rot, ang1, ang2){
	if(!Core.settings.getBool("effects")){return;}
	let amod1 = Mathf.mod(ang1,360);
	let amod2 = Mathf.mod(ang2,360);
	if(amod1>=180 && amod2>=180){return;}
	
	let s1 = -Mathf.cos(ang1*Mathf.degreesToRadians);
	let s2 = -Mathf.cos(ang2*Mathf.degreesToRadians);
	if(amod1>180){
		s1 = -1;
	}else if(amod2>180){
		s2 = 1;
	}
	vert =[
		-w*0.5,							  //x1
		Mathf.map(s1,-1,1,-h1*0.5,h1*0.5),//y1
		-w*0.5,							  //x2
		Mathf.map(s2,-1,1,-h1*0.5,h1*0.5),//y2
		w*0.5,							  //etc
		Mathf.map(s2,-1,1,-h2*0.5,h2*0.5),
		w*0.5,
		Mathf.map(s1,-1,1,-h2*0.5,h2*0.5),
	];
	
	//Draw.rect gives us a convinient rotate paramter, we dont have such luxury here.
	let s= Mathf.sin(rot*Mathf.degreesToRadians);
	let c= Mathf.cos(rot*Mathf.degreesToRadians);
	for(let i =0;i<8;i+=2){
		//(x+iy)*(c+is) = xc-sy + i(cy+sx)
		//can be optimised to one temp variable but who cares.
		let nx = vert[i]*c-vert[i+1]*s;
		let ny = vert[i+1]*c+vert[i]*s;
		vert[i] = nx;
		vert[i+1] = ny;
	}
	//pray for the best.
	this._drawQuadA(region,vert);
}


//draws the distorted sprite used to make the rotating shaft effect.
//x and y are assumed to refer to the center of the area.
//w,h is the size in world units of the texture to distort.
//th is the hieght of the texture in world units.
//rot is used for the rotation of the block itself
//ang1 ,ang2 is the two angles the sprite is distorted across, only draws if its visible, aka one of the angles is between 0 and 180
function _drawRotRect(region, x, y, w, h,th,  rot, ang1, ang2){
	if(!Core.settings.getBool("effects")){return;}
	if(!region){
		print("oh no there is no texture");
		return;
	}
	let amod1 = Mathf.mod(ang1,360);
	let amod2 = Mathf.mod(ang2,360);
	if(amod1>=180 && amod2>=180){return;}
	
	let nregion = new TextureRegion(region);
	let scale = h/th;
	
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


const _baseTypes={
	torqueConnector:{
		block:_RotPowerCommon,
		build:_RotPowerPropsCommon
	},
	torqueGenerator:{
		block:_TorqueGenerator,
		build:_TorqueGeneratorProps
	},
	torqueConsumer:{
		block:_TorqueConsumer,
		build:_TorqueConsumerProps
	},
	torqueTransmission:{
		block:_TorqueTransmission,
		build:_TorqueTransmissionProps
	},
	torqueMultiConnect:{
		block:_RotPowerCommon,
		build:_TorqueMulticonnectorProps
	}
	
	
}

module.exports={
	energyGraph: _EnergyGraph,
	powerProps: _RotPowerPropsCommon,
	powercommon: _RotPowerCommon,
	dirs: _dirs,
	
	torqueExtend(Type,Entity,name,baseType,def,customEnt){
		const block=Object.create(baseType.block);
		Object.assign(block,def);
		const rotpowerBlock=extendContent(Type,name,block);
		rotpowerBlock.buildType=()=>
			{	
				let building =  extend(Entity,Object.create(Object.assign(deepCopy(baseType.build),deepCopy(customEnt))));
				building.block = rotpowerBlock;
				return building;
			};
		return rotpowerBlock;
	},
	torqueExtendContent(Type,Entity,name,baseType,def,customEnt){
		const block=Object.create(baseType.block);
		Object.assign(block,def);
		const rotpowerBlock=extendContent(Type,name,block);
		rotpowerBlock.buildType=()=>
			{	
				let building =  extendContent(Entity,rotpowerBlock,Object.create(Object.assign(deepCopy(baseType.build),deepCopy(customEnt))));
				building.block = rotpowerBlock;
				return building;
			};
		return rotpowerBlock;
	},
	
	//_TorqueConsumer
	getConnectSidePos: getConnectSidePos,
	drawRotRect: _drawRotRect,
	drawRotQuad:_drawRotQuad,
	drawQuad: _drawQuad,
	drawQuadA: _drawQuadA,
	torqueFuncs: _Torque_Speed_Funcs,
	baseTypes:_baseTypes
}