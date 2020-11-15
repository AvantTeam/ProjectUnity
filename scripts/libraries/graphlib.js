//imports

//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.
//rotpowerlib with the fat cut out.
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


const _GraphCommonBuild = {
	graphs:{},
	prev_tile_rotation: -1,
	getGraphConnector(name){
		return this.graphs[name];
	},
	setGraphConnector(graph){
		if(!this.graphs){
			this.graphs= {};
		}
		this.graphs[graph.name] = graph;
		this.graphs[graph.name].setBuild(this);
	},
	get(){
		return this;
	},
	create(block, team) {
        let building = this.super$create(block, team);
		
		block.injectGraphConnectors(this);
		for(let graphname in this.graphs) {
		  let graphConn = this.graphs[graphname];
		  graphConn.onCreate(building)
		}
        this.prev_tile_rotation = -1;
    },
	efficiency() {
		let e = this.super$efficiency();
		for(let graphname in this.graphs) {
			e*=this.graphs[graphname].efficiency() ;
		}
		return Math.max(0,e);
	},
	onRemoved() {
        this.updateGraphRemovals();
        this.super$onRemoved();
    },
    onDestroyed() {
        this.updateGraphRemovals();
        this.super$onDestroyed();
    },
	
	updateGraphRemovals() {
		for(let graphname in this.graphs) {
			this.graphs[graphname].onRemoved() ;
		}
	},
	
	updateTile() {
		if (this.block.getUseOgUpdate()) {
            this.super$updateTile();
        }
		this.updatePre();
		if(!this.block.rotate){
			this.rotation=0;
		}
		if (this.prev_tile_rotation != this.rotation) {
			for(let graphname in this.graphs) {
				this.graphs[graphname].onRotationChanged(this.prev_tile_rotation,this.rotation);
			}
		}
		for(let graphname in this.graphs) {
			this.graphs[graphname].onUpdate();
		}
		this.updatePost();
		this.prev_tile_rotation = this.rotation;
	},
	updatePost() {},
    updatePre() {},
	onGraphUpdate() {},
	onNeighboursChanged() {},
	samepos(b) {
        return b.tileX() == this.tileX() && b.tileY() == this.tileY();
    },
    onProximityUpdate() {
        this.super$onProximityUpdate();
        //removal soon tm
		for(let graphname in this.graphs) {
			this.graphs[graphname].proximityUpdateCustom();
		}
		this.proxUpdate();
    },
	proxUpdate() {},
	display(table) {
		this.super$display(table);
		for(let graphname in this.graphs) {
			this.graphs[graphname].display(table);
		}
	},
	displayBars(barsTable){
		this.super$displayBars(barsTable);
		for(let graphname in this.graphs) {
			this.graphs[graphname].displayBars(barsTable);
		}
		this.displayBarsExt(barsTable);
	},
	displayBarsExt(barsTable){},
	write(stream) {
        this.super$write(stream);
        for(let graphname in this.graphs) {
			this.graphs[graphname].write(stream);
		}
    },
    read(stream, revision) {
        this.super$read(stream, revision);
        for(let graphname in this.graphs) {
			this.graphs[graphname].read(stream, revision);
		}
    }
}

const _GraphCommonBlock = {
	graphBlocks:{},
	graphBuilders:{},
	graphBuildings:{},
	_use_original_update: true,
	_network_connector: true,
    // whether it calls the super's updateTile
    getUseOgUpdate() {
        return this._use_original_update;
    },
    setUseOgUpdate(newaccept) {
        this._use_original_update = newaccept;
    },
	getGraphConnectorBlock(name){
		return this.graphBlocks[name];
	},
	getGraphConnectorBlockSet(){
		return this.graphBlocks;
	},
	setGraphConnectorBlock(graph){
		if(!this.graphBlocks){
			this.graphBlocks= {};
		}
		this.graphBlocks[graph.name] = graph;
	},
	injectGraphConnectors(building){
		for(let graphname in this.graphBlocks) {
			let gt = deepCopy(this.graphBuildings[graphname]);
			building.setGraphConnector(gt);
			gt.parentblock = this.graphBlocks[graphname];
		}
	},
	getGraphConnectorBuilding(name){
		return this.graphBuildings[name];
	},
	setGraphConnectorBuilding(graph){
		if(!this.graphBuildings){
			this.graphBuildings= {};
		}
		this.graphBuildings[graph.name] = graph;
	},
	getGraphConnectorBuilder(name){
		return this.graphBuilders[name];
	},
	setGraphConnectorBuilder(graph){
		if(!this.graphBuilders){
			this.graphBuilders= {};
		}
		this.graphBuilders[graph.name] = graph;
	},
	setStats() {
        this.super$setStats();
		let block = this;
        const sV = new StatValue({
            display(table) {
				for(let graphstat in block.graphBlocks) {
					
					block.graphBlocks[graphstat].setStats(table);
				}
            }
        });
        this.stats.add(Stat.abilities, sV);
    },
	 // if false the network will not 'bridge' across this block when rebuilding (but will still connect to it, like how a diode connects).
	getIsNetworkConnector() {
        return this._network_connector;
    },
    setIsNetworkConnector(newaccept) {
        this._network_connector = newaccept;
    },
	drawPlace(x, y, rotation, valid) {
        for(let graphstat in this.graphBlocks) {
			this.graphBlocks[graphstat].drawPlace(x, y, this.size, rotation, valid);
		}
        this.super$drawPlace(x, y, rotation, valid);

    },
}



const _GraphCommon = {

    setStats(table) {
		table.add("test [cyan]1[white]2[gray]3");
	},
    _accept: [],
    _multi_graph_connector: false,
    // the connection points it permits attachment to
    getAccept() {
        return this._accept;
    },
    setAccept(newaccept) {
        this._accept = newaccept;
    },
	drawPlace(x, y,size, rotation, valid){
		
	}
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


const _GraphPropsCommon = {
	parentbuilding:null,
	parentblock:null,
    getConnectSidePos(index) {
        return getConnectSidePos(index, this.getBuild().block.size, this.getBuild().rotation);
    },
    canConnect(pos) {
        for (let i = 0; i < this._acceptPorts.length; i++) {
            if (this._acceptPorts[i].x + this.getBuild().tile.x == pos.x && this._acceptPorts[i].y + this.getBuild().tile.y == pos.y) {
                return this._acceptPorts[i].index;
            }
        }
        return -1;
    },
	getBuild() {
        return this.parentbuilding;
    },
	setBuild(ohno) {
		//print("setting build:");
		//print(ohno);
        this.parentbuilding=ohno;
    },
    onCreate(building) {
        this.initAllNets(building);
        this.needsNetworkUpdate = true;
		this._lastRecalc=-1;
		this.initStats();
    },
	
	getBlockData(){
		return this.parentblock;
	},
    recalcPorts() {
		////print("reclac ports with rotation " + this.rotation);
		if(this._lastRecalc==this.getBuild().rotation){
			return;
		}
		//print("recalc build:");
		//print(this.getBuild());
        this.setAcceptPorts([]);
        for (let index = 0; index < this.getBuild().block.size * 4; index++) {
			//print(this.getBlockData());
            if (this.getBlockData().getAccept()[index] !== 0) {
				let out = this.getConnectSidePos(index);
                let pos = out.toPos;
                let fpos = out.fromPos;
                pos.index = index;
                pos.fromx = fpos.x;
                pos.fromy = fpos.y;
                this._acceptPorts.push(pos);
            }
        }
		this._lastRecalc=this.getBuild().rotation;

    },
    onRemoved() {
        this.deleteSelfFromNetwork();
        this.deleteFromNeighbours();
    },
    deleteFromNeighbours() {
        if (this.getNeighbourArray()) {
            this.eachNeighbour(neighbourindex => {
                neighbourindex.build.removeNeighbour(this);
            });
        }
    },
    deleteSelfFromNetwork() {
        this._dead = true;
        if (this._network) {
            this._network.remove(this);
        }

    },
    
    onUpdate() {
        if (this._dead) {
            return;
        }
        this.updateNetworks();
        this.updateExtension();
    },
	onRotationChanged(prevrot,newrot){
		//print("rotation changed to "+newrot+"...");
		if (prevrot != -1) {
			this.deleteSelfFromNetwork();
			this.deleteFromNeighbours();
			this._dead = false;
			this.initAllNets(this);
			this._neighbourArray = [];
		}
		this.recalcPorts();
		this.needsNetworkUpdate = true;
		
	},
    updateNetworks() {
        if (this._network) {
            if (this.needsNetworkUpdate) {
                this.needsNetworkUpdate = false;
                this._network.rebuildGraph(this);
                if (this.networkSaveState) {
					this.applySaveState(this._network, this._saveCache[0]);
                    this.networkSaveState = 0;
                }
				this.getBuild().onGraphUpdate();
            }
            this._network.update();
            this.updateProps(this._network,0);
        }
    },
	applySaveState(graph,cache) {},
    updateExtension() {},
	updateProps(graph,index) {},
    proximityUpdateCustom() {},
	display(table) {},
	initStats() {},
	displayBars(barsTable){},
    //variables for network
    _graphset: null,
    _dead: false,
    _acceptPorts: [],
	
	//stored shit 
	_saveCache:[],
	
	//stuff like rotation.
	_propsList: [],
	
    _neighbourArray: null,
    _network: null,
	_lastRecalc: 0,
    //this is used to store local speed.
    _speedcache: 0,
	drawSelect() {
    },
    initAllNets(buildingnew) {
        //_BlockGraph.new(building)
        this.recalcPorts();
        this.setNetwork(_BlockGraph.new(this));
    },
    getDead() {
        return this._dead;
    },
    getNeighbourArray() {
        return this._neighbourArray;
    },
	getLastRecalc() {
        return this._lastRecalc;
    },
    getNeighbour(building) {
        if (!this._neighbourArray) {
            return;
        }
        let found = null;
        this.eachNeighbour(neigh => {
            if (neigh.build.getBuild().samepos(building.getBuild())) {
                found = neigh;
            }
        });
        return found;
    },
    eachNeighbour(func) {
        let prev = null;
		if(!this._neighbourArray){return;}
        for (let i = 0; i < this._neighbourArray.length; i++) {
            if (prev == this._neighbourArray[i] || !this._neighbourArray[i]) {
                continue;
            }
            func(this._neighbourArray[i]);
            prev = this._neighbourArray[i];
        }
    },
	efficiency() {
		return 1;
	},
    countNeighbours() {
        if (!this._neighbourArray) {
            return 0;
        }
        let found = 0;
        this.eachNeighbour(neigh => {
            found++;
        });
        return found;
    },
    removeNeighbour(building) {
        for (let i = 0; i < this._neighbourArray.length; i++) {
            if (this._neighbourArray[i] && this._neighbourArray[i].build.getBuild().samepos(building.getBuild())) {
                this._neighbourArray[i] = null;
            }
        }
		this.getBuild().onNeighboursChanged();
    },
    addNeighbour(n) {
        if (!this._neighbourArray) {
            let temp = [];
            temp[n.portindex] = n;
            this.setNeighbourArray(temp);
            return;
        }
        this._neighbourArray[n.portindex] = n;
		this.getBuild().onNeighboursChanged();
    },
    setNeighbourArray(nset) {
        this._neighbourArray = nset;
		this.getBuild().onNeighboursChanged();
    },
    getAcceptPorts() {
        return this._acceptPorts;
    },
    setAcceptPorts(n_rotation) {
        this._acceptPorts = n_rotation;
    },
    getConnectedNeighours(index) {
        return this.getAcceptPorts();
    },
    setNetwork(set) {
        this._network = set;
    },
    clearNetworks(set) {
        this._network = null;
    },
    replaceNetwork(old, set) {
        this._network = set;
        return true;
    },
    getNetwork() {
        return this._network;
    },
    hasNetwork(net) {
        return this._network.id == net.id;
    },
    getNetworkOfPort(index) {
        return this._network;
    },
    setNetworkOfPort(index, net) {
        this._network = net;
    },
	writeGlobal(stream) {},
	readGlobal(stream, revision) {},
	writeLocal(stream,graph) {},
	readLocal(stream, revision) {}, //returns a cache object
    write(stream) {
        this.writeGlobal(stream);
        this.writeLocal(stream,this._network);
    },
    read(stream, revision) {
        this.readGlobal(stream, revision);
        let cachearray = [this.readLocal(stream, revision)];
		this._saveCache = cachearray;
        this.networkSaveState = 1;
    }

}




const _GraphMulticonnectorProps = Object.assign(Object.create(_GraphPropsCommon), {
    _networkList: [],
    getNetworks() {
        return this._networkList;
    },
    setNetworks(nv) {
        this._networkList = nv;
    },
    clearNetworks(set) {
        for (let i = 0; i < this._networkList.length; i++) {
            this._networkList[i] = null;
        }
    },
    hasNetwork(net) {
        for (let i = 0; i < this._networkList.length; i++) {
            if (this._networkList[i].id == net.id) {
                return true;
            }
        }
        return false;
    },
    initAllNets(buildingnew) {
        //_BlockGraph.new(building)
        this.recalcPorts();
        let templist = [];
        this._networkList = [];
        let portarray = this.getBlockData().getAccept();
        let networksmade = 0;
        for (let i = 0; i < portarray.length; i++) {
            if (!templist[portarray[i] - 1] && portarray[i] != 0) {
                templist.push(_BlockGraph.new(this));
                networksmade++;
            }
        }
        this.setNetworks(templist);
    },
    getNetworkFromSet(index) {
        return this._networkList[index];
    },

    setNetworkFromSet(index, net) {
        for (let i = 0; i < this._networkList.length; i++) {
            if (this._networkList[i].id == net.id) {
                return false;
            }
        }
        this._networkList[index] = net;
        return true;
    },
    replaceNetwork(old, set) {
        let index = -1;
        for (let i = 0; i < this._networkList.length; i++) {
            if (this._networkList[i] && this._networkList[i].id == old.id) {
                index = i;
            }
            if (set && this._networkList[i].id == set.id) {
                return false;
            }
        }
        if (index == -1) {
            return false;
        }
        this._networkList[index] = set;
        return true;
    },
    getNetworkOfPort(index) {
        let l = this.getBlockData().getAccept()[index];
        if (l == 0) {
            return undefined;
        }
        return this._networkList[l - 1];
    },
    setNetworkOfPort(index, net) {
        let l = this.getBlockData().getAccept()[index];
        if (l == 0) {
            return;
        }
        this._networkList[l - 1] = net;
    },
    getConnectedNeighours(index) {
        let portarray = this.getAcceptPorts();
		let blk = this.getBlockData();
        let targetport = blk.getAccept()[index];
        let output = [];
        for (let i = 0; i < portarray.length; i++) {
            if (blk.getAccept()[portarray[i].index] == targetport) {
                output.push(portarray[i]);
            }
        }
        return output;
    },
    updateNetworks() {
        if (this._networkList.length == 0) {
            return;
        }
        if (this.needsNetworkUpdate) {
            this.needsNetworkUpdate = false;
            let covered = [];
            let portarray = this.getBlockData().getAccept();
            for (let i = 0; i < portarray.length; i++) {
                if (portarray[i] == 0 || covered[portarray[i] - 1]) {
                    continue;
                }
                this.getNetworkOfPort(portarray[i] - 1).rebuildGraphIndex(this, i);
                covered[portarray[i] - 1] = 1;
            }
            if (this.networkSaveState) {
                for (let i = 0; i < this._networkList.length; i++) {
					this.applySaveState(this._networkList[i], this._saveCache[i]);
                }
            }
            this.networkSaveState = 0;
        }

        for (let i = 0; i < this._networkList.length; i++) {
            if (!this._networkList[i]) {
                continue;
            }
            this._networkList[i].update();
            this.updateProps(this._networkList[i],i);
        }
        this.needsNetworkUpdate = false;
    },
    deleteSelfFromNetwork() {
        this._dead = true;
        if (this._networkList.length == 0) {
            return;
        }
        for (let i = 0; i < this._networkList.length; i++) {
			if (!this._networkList[i]) {
                continue;
            }
            this._networkList[i].remove(this);
        }
    },
	
	
	write(stream) {
        this.writeGlobal(stream);
		stream.i(this._networkList.length);
		for (let i = 0; i < this._networkList.length; i++) {
			this.writeLocal(stream,this._networkList[i]);
		}
    },
    read(stream, revision) {
        this.readGlobal(stream, revision);
		let netam = stream.i();
        let cachearray = [];
		for (let i = 0; i < netam; i++) {
			cachearray.push(this.readLocal(stream, revision));
		}
		this._saveCache = cachearray;
        this.networkSaveState = 1;
    }

});




var uniqueidincre = 0;

function getnetID() {
    uniqueidincre++;
    return uniqueidincre;
}

const _BlockGraph = {
    lastFrameUpdated: 0,
    id: 0,
    //'connected' is the graph's field of building set
    new(building) {
        const graph = Object.assign(Object.create(_BlockGraph),deepCopy(building.getBuild().block.getGraphConnectorBuilder(building.name)));
        graph.id = getnetID();
        graph.connected = ObjectSet.with(building);
		graph.updateOnGraphChanged();
		graph.addMergeStats(building);	
        return graph;

    },

    copyGraph(building) {
        const copygraph = _BlockGraph.new(building)
        copygraph.copyGraphStatsFrom(this);
        return copygraph;

    },
	copyGraphStatsFrom(graph) {
	},

    update() {
        if (Core.graphics.getFrameId() == this.lastFrameUpdated)
            return;
        this.lastFrameUpdated = Core.graphics.getFrameId();
		this.updateDirect();
        this.updateGraph();
    },
	updateOnGraphChanged() {},
    updateGraph() {},
	updateDirect() {}, // updating directly derived, non-time dependant values from graph connectors/buildings.
	canConnect(b1,b2) {return true;},
    addBuilding(building, connectIndex) {
        this.connected.add(building);
		this.updateOnGraphChanged();
		this.addMergeStats(building);
        building.setNetworkOfPort(connectIndex, this);
		
    },
	addMergeStats(building){}, // used for graph systems in which blocks 'techinically' have their own values, but are as a whole managed by a graph. 
    mergeGraph(graph) {
        ////print(graph);
        if (!graph) {
            return;
        }
        //optimisation over original, only merging the smaller graph, makes placing individual blocks in large networks better.
        if (graph.connected.size > this.connected.size) {
            graph.mergeGraph(this);
            return;
        }
        //avoiding unupdated graphs connecting.
        this.updateDirect();
        graph.updateDirect();
		this.mergeStats(graph);
        graph.connected.each(cons(building => {
            //some buildings may be connected to two seperate networks due to how gear transmission works.
            if (!this.connected.contains(building)) {
                if (building.replaceNetwork(graph, this)) {
                    this.connected.add(building);
                }
            }
        }));
		
		this.updateOnGraphChanged();
    },
	mergeStats(graph){},
	killGraph(){
		this.connected.clear();
	},
    remove(building) {
        if (!this.connected.contains(building)) {
            return;
        }
        let c = building.countNeighbours();
        if (c === 0) {
            return;
        }
        if (c === 1) {
			//if theres only one neighbour it means removing this wont disconnect two seperate parts of the graph, and therefore can be just removed directly.
            this.connected.remove(building);
            building.eachNeighbour(neighbourindex => {
                neighbourindex.build.removeNeighbour(building);
            });
			this.updateOnGraphChanged();
            return;
        }
		this.killGraph();
        let networksadded = null;
        let newnets = 0;

		//reset each neighbour to a contained network
        building.eachNeighbour(neighbour => {
            let copynet = building.getNetworkOfPort(neighbour.portindex);
            if (copynet == this) {
                let selfref = neighbour.build.getNeighbour(building);
                if (!selfref) {
                    return;
                }
                neighbour.build.setNetworkOfPort(selfref.portindex, copynet.copyGraph(neighbour.build));
            }
        });

        building.eachNeighbour(neighbourindex => {
            let neighbour = neighbourindex.build;
            if (building.getNetworkOfPort(neighbourindex.portindex) == this) {
                let selfref = neighbour.getNeighbour(building);
                if (!selfref) {
                    return;
                }
                let neinet = neighbour.getNetworkOfPort(selfref.portindex);
                if (!networksadded || !networksadded.contains(neinet)) {
                    if (!networksadded) {
                        networksadded = ObjectSet.with(neinet);
                    } else {
                        networksadded.add(neinet);
                    }
                    neinet.rebuildGraphIndex(neighbour, selfref.portindex);
                }
            }
        });
        building.replaceNetwork(this, null);
    },

    rebuildGraph(building) {
        this.rebuildGraphWithSet(building, ObjectSet.with(building), -1);
    },
    rebuildGraphIndex(building, index) {
        this.rebuildGraphWithSet(building, ObjectSet.with(building), index);
    },
    rebuildGraphWithSet(root, searched, rootindex) {

        //guess ill die
        //thank god this was already tail recursed.
        let tree = {
            complete: false,
            parent: null,
            children: [],
            build: root,
            parentConnectPort: rootindex,
        }
        let current = tree;

        //debug
        let total = 0;
        //
		////print("starting rebuild....");
        mainloop:
            while (current) {

                total++;

                let buildConnector = current.build;
                let index = current.parentConnectPort;

                if (!buildConnector.getBlockData().getAccept()) {
                    //print("oh no, accept ports not found");
                    return;
                }
                let acceptports = buildConnector.getAcceptPorts();
                if (index != -1) {
                    acceptports = buildConnector.getConnectedNeighours(index);
                }
                let prevbuilding = null;
                searched.add(buildConnector);
                //print("rebuilding from:"+buildConnector.getBuild().block.localizedName+" at port"+ index+ ",buildConnector has rotation of: "+buildConnector.getBuild().rotation);
                ////print("ports to scan:"+acceptports);

                for (let port = 0; port < acceptports.length; port++) {
                    let portindex = acceptports[port].index;
                    if (!buildConnector.getNetworkOfPort(portindex)) {
						//print("this tile doesnt have a network");
                        continue;
                    }
                    let portinfo = acceptports[port];
                    if (!buildConnector.getBuild().tile) {
						//print("this tile no exist?");
                        return;
                    }
                    let tile = buildConnector.getBuild().tile.nearby(portinfo.x, portinfo.y);
                    // guess the world doesnt exist or something
                    if (!tile) {
						//print("nearby tile no exist");
                        return;
                    }
                    if (tile.block().getIsNetworkConnector != undefined) {
                        //conbuild -> connected buildConnector
                        let conbuild = tile.bc().getGraphConnector(root.name);
                        if (!conbuild || conbuild == prevbuilding || conbuild.getDead() || !this.canConnect(current,conbuild)) {
							//conbuild is dead or duplicate or undefined or not allowed to connect
                            continue;
                        }
                        let thisgraph = buildConnector.getNetworkOfPort(portindex);
						// networks in multiplayer are intialised with rotation of 0 then updated later. this results in many problems.
						if(conbuild.getBuild().rotation!=conbuild.getLastRecalc()){
							conbuild.recalcPorts();
						}
                        let fpos = {
                            x: portinfo.fromx,
                            y: portinfo.fromy
                        };
                        fpos.x += buildConnector.getBuild().tile.x;
                        fpos.y += buildConnector.getBuild().tile.y;
                        let connectIndex = conbuild.canConnect(fpos);
                        if (connectIndex == -1) {
                            continue;
                        }
                        //print("found suitable connecting buildConnector: current block is connected at port "+connectIndex+" of other buildConnector at coord "+fpos.x+","+fpos.y);
                        buildConnector.addNeighbour({
                            build: conbuild,
                            portindex: portindex
                        });
                        conbuild.addNeighbour({
                            build: buildConnector,
                            portindex: connectIndex
                        });
                        //buildings without a network instance are assumed to be dead
                        let connet = conbuild.getNetworkOfPort(connectIndex);
                        if (!thisgraph.connected.contains(conbuild) && connet) {
                            if (!buildConnector.hasNetwork(connet)) {
                                if (connet.connected.contains(conbuild)) {
                                    thisgraph.mergeGraph(connet);
                                    thisgraph = buildConnector.getNetworkOfPort(portindex);
                                    ////print("external net:"+connet.connectedToString());
                                    ////print("network merged:"+thisgraph.connectedToString());
                                } else {
                                    ////print("network doesnt not contain target buildConnector, assuming hollowed network, directly adding...");
                                    ////print("network polled:"+connet.connectedToString());
                                    thisgraph.addBuilding(conbuild, connectIndex);
                                }
                                //placing it outside will result in the entire graph be re-searched for any nodes that havent beeen assimilated into the graph yet.
                                //may be problematic and cause lag^ but is a good way to rebuild the <entire> graph from any one point.
                                //placing it inside will only search available nodes that are not blocked by already assimilated nodes.
                                if (tile.block().getIsNetworkConnector() && !searched.contains(conbuild)) {
                                    current.children.push({
                                        complete: false,
                                        parent: current,
                                        children: [],
                                        build: conbuild,
                                        parentConnectPort: connectIndex,
                                    });
                                    //thisgraph.rebuildGraphWithSet(conbuild,searched,connectIndex);
                                }
                            } else {
                                ////print("Graphs are the same(id:"+connet.id+"), skipping..");
                            }
                        } else {
                            ////print("Graph already contains buildConnector, skipping..");
                        }
                        prevbuilding = conbuild;
                    }else{
						//print("nearby tile no network");
						
					}
                }
                if (current.children.length > 0) {
                    for (let i = 0; i < current.children.length; i++) {
                        if (!current.children[i].complete) {
                            current = current.children[i];
                            continue mainloop;
                        }
                    }
                }
                current.complete = true;
                current = current.parent;

            }

        ////print("total traversals:"+total);
    },
    //debug
    connectedToString() {
        let s = "Network:" + this.id + ":";
        this.connected.each(cons(buildConnector => {
            s += buildConnector.getBuild().block.localizedName + ", "
        }));
        return s;
    }
}

module.exports = {
    blockGraph: _BlockGraph,
    graphProps: _GraphPropsCommon,
	graphMultiProps: _GraphMulticonnectorProps,
    graphCommon: _GraphCommon,
    dirs: _dirs,
	dcopy: deepCopy,
	init(){
		return {
			block:deepCopy(_GraphCommonBlock),
			build:deepCopy(_GraphCommonBuild),
		}
	},
	addGraph(bcustom, graphConnector) {
		bcustom.block.setGraphConnectorBlock(deepCopy(graphConnector.block));
		bcustom.block.setGraphConnectorBuilding(deepCopy(graphConnector.build));
		bcustom.block.setGraphConnectorBuilder(deepCopy(graphConnector.graph));
	},
	setGraphName(graphConnector,name) {
		graphConnector.block.name = name;
		graphConnector.build.name= name;
		graphConnector.graph.name= name;
	},
	finaliseExtend(Type, Entity, name, bcustom,custBlock, custBuild) {
		const rotpowerBlock = extendContent(Type, name, Object.assign(bcustom.block,custBlock));
		rotpowerBlock.buildType = () => {
            let building = extend(Entity, Object.assign(deepCopy(bcustom.build),deepCopy(custBuild)));
            building.block = rotpowerBlock;
            return building;
        };
		return rotpowerBlock;
	},
	finaliseExtendContent(Type, Entity, name, bcustom,custBlock, custBuild) {
		const rotpowerBlock = extendContent(Type, name, Object.assign(bcustom.block,custBlock));
		rotpowerBlock.buildType = () => {
            let building = extendContent(Entity,rotpowerBlock, Object.assign(deepCopy(bcustom.build),deepCopy(custBuild)));
            building.block = rotpowerBlock;
            return building;
        };
		return rotpowerBlock;
	},
}