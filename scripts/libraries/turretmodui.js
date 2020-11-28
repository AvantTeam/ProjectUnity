
//imports
importPackage(Packages.arc.graphics.gl);
importPackage(Packages.arc.input);
const graphLib = require("libraries/graphlib");
//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.
print("youndcha test2")
importPackage(Packages.arc.util.pooling);
importPackage(Packages.arc.scene);

const _dirs = [{
		x: 1,
		y: 0
	}, {
		x: 0,
		y: 1
	}, {
		x: -1,
		y: 0
	}, {
		x: 0,
		y: -1
	}

];

function getConnectSidePos(index, sizew, sizeh) {
	if (sizew == 1 && sizeh == 1) {
		return {
			x: 0,
			y: 0,
			dir: {
				x: _dirs[index].x,
				y: _dirs[index].y
			}
		}
	}
	let cind = index - sizeh;
	let lstsub = sizeh;
	let gx = sizew - 1;
	let gy = 0;
	let side = 0;
	let forwarddir = _dirs[3];
	while (cind >= 0) {
		side++;
		gx += forwarddir.x * (lstsub - 1);
		gy += forwarddir.y * (lstsub - 1);
		forwarddir = _dirs[3 - side];
		lstsub = side % 2 == 1 ? sizew : sizeh;
		cind -= lstsub;
	}
	gx += forwarddir.x * (cind + lstsub);
	gy += forwarddir.y * (cind + lstsub);
	let pdir = _dirs[(side) % 4];
	return {
		x: gx,
		y: gy,
		dir: {
			x: pdir.x,
			y: pdir.y
		}
	};
}

function _getRegionRect(region, x, y, rw, rh, w, h) {
	if (!region) {
		print("oh no there is no texture");
		return;
	}
	let nregion = new TextureRegion(region);
	let tilew = (nregion.u2 - nregion.u) / w;
	let tileh = (nregion.v2 - nregion.v) / h;
	let tilex = x / w;
	let tiley = y / h;

	nregion.u = Mathf.map(tilex, 0, 1, nregion.u, nregion.u2) + tilew * 0.02;
	nregion.v = Mathf.map(tiley, 0, 1, nregion.v, nregion.v2) + tileh * 0.02; //y is flipped h
	nregion.u2 = nregion.u + tilew * rw - tilew * 0.02;
	nregion.v2 = nregion.v + tileh * rh - tileh * 0.02;
	nregion.width = 32 * rw;
	nregion.height = 32 * rh;
	return nregion;
}

const StackedBarChart = {
	_prefHeight: 100,
	_barStats: prov(() => {
		return [{
				name: "default",
				weight: 1.6,
				filled: 0.5,
				color: Color.valueOf("6bc7ff")
			}, {
				name: "default2",
				weight: 1,
				filled: 0.8,
				color: Color.valueOf("ea7a55")
			}
		];
	}),
	draw() {
		let font = Fonts.outline;
		let lay = Pools.obtain(GlyphLayout, prov(() => {
					return new GlyphLayout();
				}));

		let data = this._barStats.get();
		let totalweight = 0;
		for (let i = 0; i < data.length; i++) {
			totalweight += data[i].weight;
		}
		let ypos = this.y;
		for (let i = 0; i < data.length; i++) {
			let ah = this.height * (data[i].weight / totalweight);
			let aw = this.width * data[i].filled;
			let text = data[i].name;
			let dark = data[i].color.cpy().mul(0.5);
			Draw.color(dark);
			Fill.rect(this.x + this.width * 0.5, ypos + ah * 0.5, this.width, ah);
			Draw.color(data[i].color);
			Fill.rect(this.x + aw * 0.5, ypos + ah * 0.5, aw, ah);
			lay.setText(font, text);
			font.setColor(Color.white);
			font.draw(text, this.x + this.width / 2.0 - lay.width / 2.0, ypos + ah / 2.0 + lay.height / 2.0 + 1);

			ypos += ah;
		}

		Pools.free(lay);

	},

	setSize(x, y) {
		this.super$setSize(x, y);
	},
	getPrefHeight() {
		return this._prefHeight;
	},
	getPrefWidth() {
		return 180.0;
	},
	setPrefHeight(s) {
		this._prefHeight = s;
	},
	setBarStatsProv(s) {
		this._barStats = s;
	}

};

function getStackedBarChart(pheight, datafunction, partsImage) {
	let pp = extend(Element, Object.create(StackedBarChart));
	//pp.setName("abcd");
	pp.setPrefHeight(pheight);
	pp.setBarStatsProv(datafunction);
	return pp;

}

const blueprintCol = Color.valueOf("354654");
const bgCol = Color.valueOf("323232");
const costaccumrate = 0.2;

const colorPorts = [];
for (let i = 0; i < 100; i++) {
	colorPorts.push(Color.HSVtoRGB(360.0 * Mathf.random(), 100 * Mathf.random(0.3, 1), 100 * Mathf.random(0.9, 1), 1.0));
}

const modularConstructorUI = {
	_prefHeight: 100,
	_partsSprite: null,
	_partsSelect: null,
	_costAccum: 1.0,
	_onTileAction: null,
	_PartList: [],
	_RootList: [],
	//just store in list instead of grid h.
	//ok i actualy need a grid now fk.
	_Grid: [],
	_gridW: 1,
	_gridH: 1,

	//Ui
	_isClickedRN: false,
	_hover: null,
	_dragButtn: null,

	draw() {
		let amx = this.x + this.width * 0.5;
		let amy = this.y + this.height * 0.5;

		let gw = this._gridW * 32;
		let gh = this._gridH * 32;
		let gamx = amx - gw * 0.5;
		let gamy = amy - gh * 0.5;
		Draw.color(bgCol);
		Fill.rect(amx, amy, this.width, this.height);
		Draw.color(blueprintCol);
		Fill.rect(amx, amy, gw, gh);
		Draw.color();
		for (let i = 0; i < this._PartList.length; i++) {
			let p = this._PartList[i];
			if (!p.valid) {
				Draw.color(p.flash % 10 < 5 ? Color.pink : Color.white);
				p.flash++;
			} else {
				Draw.color();
			}
			Draw.rect(p.part.texRegion, p.x * 32 + gamx + (p.part.tw * 16), p.y * 32 + gamy + (p.part.th * 16), p.part.tw * 32, p.part.th * 32);
			this.drawOpenConnectionPorts(p.part, p.x, p.y, gamx, gamy);
		}
		Draw.color(Color.black);
		Fill.rect(this.x + 20, this.y + 20, 40, 40);
		Draw.color();
		if (this._partsSelect) {
			Draw.rect(this._partsSelect.texRegion, this.x + 20, this.y + 20, 32, 32);
			if (this._hover) {
				let ps = this._partsSelect;
				Draw.color(this.canPlace(ps, this._hover.x, this._hover.y) ? Color.white : Color.red, 0.3);
				Draw.rect(ps.texRegion, this._hover.x * 32 + gamx + (ps.tw * 16), this._hover.y * 32 + gamy + (ps.th * 16), ps.tw * 32, ps.th * 32);
				this.drawOpenConnectionPorts(ps, this._hover.x, this._hover.y, gamx, gamy);
			}
		}

	},

	drawOpenConnectionPorts(ps, x, y, offx, offy) {
		for (let o = 0; o < ps.connInList.length; o++) {
			let conout = ps.connInList[o];
			let opcx = x + conout.x + conout.dir.x;
			let opcy = y + conout.y + conout.dir.y;
			if (!this.getPartAt(opcx, opcy)) {
				let brcx = (opcx - conout.dir.x * 0.5 + 0.5) * 32 + offx;
				let brcy = (opcy - conout.dir.y * 0.5 + 0.5) * 32 + offy;
				Draw.color(Color.black);
				Fill.square(brcx, brcy, 6, 45);
				Draw.color(colorPorts[ps.connInList[o].id - 1]);
				Fill.square(brcx, brcy, 2, 45);
				Draw.color();
			}
		}
		for (let o = 0; o < ps.connOutList.length; o++) {
			let conout = ps.connOutList[o];
			let opcx = x + conout.x + conout.dir.x;
			let opcy = y + conout.y + conout.dir.y;
			if (!this.getPartAt(opcx, opcy)) {
				let brcx = (opcx - conout.dir.x * 0.5 + 0.5) * 32 + offx;
				let brcy = (opcy - conout.dir.y * 0.5 + 0.5) * 32 + offy;
				Draw.color(Color.black);
				Fill.square(brcx, brcy, 6, 45);
				Draw.color(colorPorts[ps.connOutList[o].id - 1]);
				Lines.stroke(2.0);
				Lines.poly(brcx, brcy, 4, 3, 0);
				Draw.color();
			}
		}
	},

	getPrefHeight() {
		return this._prefHeight;
	},
	setPrefHeight(s) {
		this._prefHeight = s;
	},
	setGrid(p, x, y) {
		if (!this._Grid[x]) {
			this._Grid[x] = [];
		}
		this._Grid[x][y] = p;
	},
	inBounds(partType, x, y) {
		return partType && this.inBoundsRect(x, y, partType.tw, partType.th);
	},
	inBoundsRect(x, y, w, h) {
		return !(x < 0 || x + w > this._gridW || y < 0 || y + h > this._gridH);
	},
	canPlace(partType, x, y) {
		return this.canPlaceConn(partType, x, y, true);
	},
	canPlaceConn(partType, x, y, chkConnection) {
		if (!this.inBounds(partType, x, y)) {
			return false;
		}
		for (let px = 0; px < partType.tw; px++) {
			for (let py = 0; py < partType.th; py++) {
				if (this.getPartAt(x + px, y + py)) {
					return false;
				}
			}
		}
		//conection
		if (chkConnection) {
			let hasConnection = partType.connInList.length == 0;
			let cin = partType.connInList;
			for (let i = 0; i < cin.length; i++) {
				let frompart = this.getPartAt(x + cin[i].x + cin[i].dir.x, y + cin[i].y + cin[i].dir.y);
				if (frompart) {
					hasConnection = hasConnection || this.partCanConnectOut(frompart, cin[i].x + x, cin[i].y + y, cin[i].id);
				}
			}
			if (!hasConnection) { // check childs
				let cout = partType.connOutList;
				for (let i = 0; i < cout.length; i++) {
					let frompart = this.getPartAt(x + cout[i].x + cout[i].dir.x, y + cout[i].y + cout[i].dir.y);
					if (frompart) {
						hasConnection = hasConnection || this.partCanConnectIn(frompart, cout[i].x + x, cout[i].y + y, cout[i].id);
					}
				}
			}
			return hasConnection;
		}
		return true;
	},
	floodFrom(part) {
		let visited = ObjectSet.with(part);
		let toVisit = [];
		let index = 0;
		for (let i = 0; i < part.parents.length; i++) {
			toVisit.push(part.parents[i]);
		}
		for (let i = 0; i < part.children.length; i++) {
			toVisit.push(part.children[i]);
		}
		while (index < toVisit.length) {
			let cpart = toVisit[index];
			visited.add(cpart);
			for (let i = 0; i < cpart.parents.length; i++) {
				if (!visited.contains(cpart.parents[i])) {
					toVisit.push(cpart.parents[i]);
				}
			}
			for (let i = 0; i < cpart.children.length; i++) {
				if (!visited.contains(cpart.children[i])) {
					toVisit.push(cpart.children[i]);
				}
			}
			index++;
		}
		return visited;
	},
	rebuildFromRoots() {
		for (let i = 0; i < this._PartList.length; i++) {
			this._PartList[i].valid = false;
		}
		for (let i = 0; i < this._RootList.length; i++) {
			let k = this.floodFrom(this._RootList[i]);
			k.each(cons(part => {
					part.valid = true;
				}));
		}
	},
	removeTile(part) {
		if (!part || part.part.isRoot) {
			return false;
		}
		let prt = part.part;
		//the children must perish.
		for (let i = 0; i < part.parents.length; i++) {
			for (let j = 0; j < part.parents[i].children.length; j++) {
				if (part.parents[i].children[j] == part) {
					part.parents[i].children.splice(j, 1);
					break;
				}
			}
		}
		for (let px = 0; px < prt.tw; px++) {
			for (let py = 0; py < prt.th; py++) {
				this.setGrid(null, part.x + px, part.y + py);
			}
		}
		for (let i = 0; i < this._PartList.length; i++) {
			if (this._PartList[i] == part) {
				this._PartList.splice(i, 1);
				break;
			}
		}
		this.rebuildFromRoots();
		this._costAccum -= costaccumrate * prt.tw * prt.th;
		return true;
	},
	placeTile(partType, x, y) {
		if (!this.canPlace(partType, x, y)) {
			return false;
		}
		this.placeTileDirect(partType, x, y);
		return true;
	},
	placeTileNoConn(partType, x, y) {
		if (!this.canPlaceConn(partType, x, y, false)) {
			return false;
		}
		this.placeTileDirect(partType, x, y);
		return true;
	},
	placeTileDirect(partType, x, y) {

		let partPlaceobj = {
			x: x,
			y: y,
			valid: false,
			flash: 0,
			part: partType,
			parents: [],
			children: [],
		};
		let cin = partType.connInList;
		for (let i = 0; i < cin.length; i++) {
			let frompart = this.getPartAt(x + cin[i].x + cin[i].dir.x, y + cin[i].y + cin[i].dir.y);
			if (frompart) {
				if (this.partCanConnectOut(frompart, cin[i].x + x, cin[i].y + y, cin[i].id)) {
					partPlaceobj.parents.push(frompart);
					frompart.children.push(partPlaceobj);
				}
			}
		}
		let cout = partType.connOutList;
		for (let i = 0; i < cout.length; i++) {
			let frompart = this.getPartAt(x + cout[i].x + cout[i].dir.x, y + cout[i].y + cout[i].dir.y);
			if (frompart) {
				if (this.partCanConnectIn(frompart, cout[i].x + x, cout[i].y + y, cout[i].id)) {
					partPlaceobj.children.push(frompart);
					frompart.parents.push(partPlaceobj);
				}
			}
		}

		for (let px = 0; px < partType.tw; px++) {
			for (let py = 0; py < partType.th; py++) {
				this.setGrid(partPlaceobj, x + px, y + py);
			}
		}
		if (partType.isRoot) {
			this._RootList.push(partPlaceobj);
		}
		this._PartList.push(partPlaceobj);
		this.rebuildFromRoots();
		this._costAccum += costaccumrate * partType.tw * partType.th;
		return true;
	},
	onIsClicked(event, x, y, point, butt) {
		this._isClickedRN = true;
		let gpos = this.uiToGridPos(x, y);
		let success = false;
		if (butt == KeyCode.mouseRight) {
			success = this.removeTile(this.getPartAt(gpos.x, gpos.y));
		} else {
			success = this.placeTile(this._partsSelect, gpos.x, gpos.y);
		}
		this._dragButtn = butt;
		if (this._onTileAction && success) {
			this._onTileAction.run();
		}
	},
	onIsReleased(event, x, y, point, butt) {
		this._isClickedRN = false;
	},
	onIsDragged(event, x, y, point, butt) {
		if (this._isClickedRN) {
			let gpos = this.uiToGridPos(x, y);
			let success = false;
			if (butt == KeyCode.mouseRight) {
				success = this.removeTile(this.getPartAt(gpos.x, gpos.y));
			} else {
				success = this.placeTile(this._partsSelect, gpos.x, gpos.y);
			}
			if (this._onTileAction && success) {
				this._onTileAction.run();
			}
		}
	},
	onIsHovering(event, x, y) {
		if (x < 0 || x > this.width || y < 0 || y > this.height) {
			this._hover = null;
			return false;
		}
		this._hover = this.uiToGridPos(x, y);
		return true;
	},
	init() {
		let that = this;
		this.addListener(extend(InputListener, {
				touchDown(event, x, y, pointer, button) {
					if (that.disabled) {
						return false;
					} else {
						that.onIsClicked(event, x, y, pointer, button);
						return true;
					}
				},
				mouseMoved(event, x, y) {
					return that.onIsHovering(event, x, y);
				},
				touchUp(event, x, y, pointer, button) {
					print("touchup");
					that.onIsReleased(event, x, y, pointer, button);
				},
				touchDragged(event, x, y, pointer) {
					that.onIsDragged(event, x, y, pointer, that._dragButtn);
				}
			}));
	},
	getTotalCost() {
		let cst = {};
		for (let i = 0; i < this._PartList.length; i++) {
			let p = this._PartList[i].part.cost;
			for (let cstitem = 0; cstitem < p.length; cstitem++) {
				if (!cst[p[cstitem].name]) {
					cst[p[cstitem].name] = 0;
				}
				cst[p[cstitem].name] += Math.floor(p[cstitem].amount * (this._costAccum - costaccumrate));
			}
		}
		return cst;
	},
	getPartAt(x, y) {
		if (!this.inBoundsRect(x, y, 1, 1) || !this._Grid[x]) {
			return null;
		}
		return this._Grid[x][y] ? this._Grid[x][y] : null;
	},
	partCanConnect(part, x, y, portid) {
		let cout = part.part.connOutList;
		for (let i = 0; i < cout.length; i++) {
			if (cout[i].id == portid && x == part.x + cout[i].x + cout[i].dir.x && y == part.y + cout[i].y + cout[i].dir.y) {
				return true;
			}
		}
		return false;
	},
	partCanConnectOut(part, x, y, portid) {
		let cout = part.part.connOutList;
		for (let i = 0; i < cout.length; i++) {
			if (cout[i].id == portid && x == part.x + cout[i].x + cout[i].dir.x && y == part.y + cout[i].y + cout[i].dir.y) {
				return true;
			}
		}
		return false;
	},
	partCanConnectIn(part, x, y, portid) {
		let cout = part.part.connInList;
		for (let i = 0; i < cout.length; i++) {
			if (cout[i].id == portid && x == part.x + cout[i].x + cout[i].dir.x && y == part.y + cout[i].y + cout[i].dir.y) {
				return true;
			}
		}
		return false;
	},
	uiToGridPos(x, y) {
		let gw = this._gridW * 32;
		let gh = this._gridH * 32;
		let gamx = (this.width - gw) * 0.5;
		let gamy = (this.height - gh) * 0.5;
		return {
			x: Math.floor((x - gamx) / 32.0),
			y: Math.floor((y - gamy) / 32.0)
		}
	},
	setOnTileAction(s) {
		this._onTileAction = s;
	},
	setGridSize(w, h) {
		this._gridW = w;
		this._gridH = h;
	},
	getPackedSave() {
		let packer = IntPacker.new();
		for (let px = 0; px < this._gridW; px++) {
			for (let py = 0; py < this._gridH; py++) {
				let p = this.getPartAt(px, py);
				if (p && p.x == px && p.y == py && p.valid) {
					packer.add(p.part.id + 1);
				} else {
					packer.add(0);
				}
			}
		}
		packer.end();
		return packer.toStringPack();
	},
	loadSave(array, partlist) {
		if (!array) {
			return;
		}
		for (let i = 0; i < array.length; i++) {
			if (array[i] == 0) {
				continue;
			}
			let px = Math.floor(i / this._gridH);
			let py = i % this._gridH;
			this.placeTileNoConn(partlist[array[i] - 1], px, py);
		}
	},
	getCostAccum() {
		return this._costAccum;
	},
	setPartsSprite(s) {
		this._partsSprite = s;
	},
	setPartSelect(s) {
		print("PART SELECT:" + s.name);
		this._partsSelect = s;
	},
	setPreconfig(s) {
		//this._partsSprite = s;
	},
}

function getModularConstructorUI(pheight, partssprite, partsConfig, preconfig, maxw, maxh) {
	let pp = extend(Element, Object.create(deepCopy(modularConstructorUI)));
	pp.init();
	pp.setPrefHeight(pheight);
	pp.setPartsSprite(partssprite);
	pp.setGridSize(maxw, maxh);
	if (!preconfig || !preconfig.length) {
		for (let i = 0; i < partsConfig.length; i++) {
			let pinfo = partsConfig[i];
			if (pinfo.prePlace) {
				pp.placeTile(pinfo, pinfo.prePlace.x, pinfo.prePlace.y);
			}
		}
	} else {
		pp.loadSave(preconfig, partsConfig);
	}
	return pp;
}

function addConsButton(table, consFunc, style, runnable) {
	let button = new Button(style);
	button.clearChildren();
	button.clicked(runnable);
	consFunc.get(button);
	return table.add(button);
}

function displayPartInfo(part) {
	let dialog = new BaseDialog("Part:" + part.name);
	dialog.setFillParent(false);
	dialog.cont.add("[lightgray]Name:[white]" + part.name).left();
	dialog.cont.row();
	dialog.cont.add("[lightgray]Description:").left();
	dialog.cont.row();
	dialog.cont.add("[white]" + part.desc).wrap().fillX().left().width(500).maxWidth(500).get().setWrap(true);
	dialog.cont.row();
	dialog.cont.add("[accent] Stats");
	for (var stat in part.stats) {
		dialog.cont.row();
		dialog.cont.add("[lightgray]" + Core.bundle.get(part.stats[stat].name) + ": [white]" + part.stats[stat].value).left();
	}

	dialog.buttons.button("@ok", () => {
		dialog.hide();
	}).size(130.0, 60.0);
	dialog.update(() => {});
	dialog.show();
}


function _preCalcConnection(partsConfig){
	for (let i = 0; i < partsConfig.length; i++) {
		partsConfig[i].id = i;
		let pinfo = partsConfig[i];
		if (!pinfo.connInList) {
			let tmp = [];
			for (let i = 0; i < pinfo.connectIn.length; i++) {
				if (pinfo.connectIn[i] != 0) {
					let t2 = getConnectSidePos(i, pinfo.tw, pinfo.th);
					t2.id = pinfo.connectIn[i];
					tmp.push(t2);
				}
			}
			pinfo.connInList = tmp;
		}
		if (!pinfo.connOutList) {
			let tmp = [];
			for (let i = 0; i < pinfo.connectOut.length; i++) {
				if (pinfo.connectOut[i] != 0) {
					let t2 = getConnectSidePos(i, pinfo.tw, pinfo.th);
					t2.id = pinfo.connectOut[i];
					tmp.push(t2);
				}
			}
			pinfo.connOutList = tmp;
		}
	}
}

function _assignPartSprties(partsConfig,partssprite, spritew, spriteh){
	for (let i = 0; i < partsConfig.length; i++) {
		partsConfig[i].id = i;
		let pinfo = partsConfig[i];
		pinfo.texRegion = _getRegionRect(partssprite, pinfo.tx, pinfo.ty, pinfo.tw, pinfo.th, spritew, spriteh);
	}
}

function applyModularConstructorUI(table, partssprite, spritew, spriteh, partsConfig, maxw, maxh, preconfig, categories) {
	//preinit
	_preCalcConnection(partsConfig);
	_assignPartSprties(partsConfig,partssprite,spritew,spriteh);
	let currentCat = "";

	let modelement = getModularConstructorUI(400, partssprite, partsConfig, preconfig, maxw, maxh);
	let itemcache = {};

	let partSelectCons = cons((scrolltbl) => {
			let costinc = modelement.getCostAccum();
			scrolltbl.clearChildren();
			scrolltbl.top().left();
			for (let i = 0; i < partsConfig.length; i++) {
				let pinfo = partsConfig[i];
				if (pinfo.cannotPlace) {
					continue;
				}
				if (pinfo.category != currentCat) {
					continue;
				}
				scrolltbl.row();
				addConsButton(scrolltbl, cons((butt) => {
						butt.top().left();
						butt.margin(12);
						butt.defaults().left().top();
						butt.add(pinfo.name).size(170, 45); //name

						butt.row();

						butt.table(cons((toptbl) => {
								toptbl.add(new BorderImage(pinfo.texRegion, 2)).size(40 - 4).padTop(-4).padLeft(-4).padRight(4);

								toptbl.button(Tex.whiteui, Styles.clearTransi, 50, run(() => {
										displayPartInfo(pinfo)
									})).size(50).get().getStyle().imageUp = Icon.infoSmall;

							})).marginLeft(4);

						butt.row();

						butt.add("[accent]Cost").padBottom(4);
						butt.row();
						butt.table(cons((bottbl) => {
								for (let cstitem = 0; cstitem < pinfo.cost.length; cstitem++) {
									let cst = pinfo.cost[cstitem];
									if (!itemcache[cst.name]) {
										itemcache[cst.name] = Vars.content.getByName(ContentType.item, cst.name);
									}
									bottbl.image(itemcache[cst.name].icon(Cicon.small)).left();
									bottbl.add("[gray]" + Math.floor(cst.amount * costinc)).padLeft(2).left().padRight(4);

									if (cstitem % 2 == 1) {
										bottbl.row();
									}
								}
							}));

					}),
					Styles.defaultt,
					() => {
					modelement.setPartSelect(pinfo); //set part
				}).minWidth(150.0).padBottom(8);
			}

		});
	let parts = new Table();
	let rebuildParts = run(() => partSelectCons.get(parts));
	let pane = new ScrollPane(parts, Styles.defaultPane);

	let prevChecked = null;
	let catTable = new Table();
	catTable.margin(12);
	catTable.top().left();
	for (let i in categories) {
		let catbutt = new ImageButton(categories[i], Styles.clearToggleTransi);
		const ft = i;
		catbutt.clicked(() => {
			currentCat = ft;
			rebuildParts.run();
			catbutt.setChecked(true);
			if (prevChecked) {
				prevChecked.setChecked(false);
			}
			prevChecked = catbutt;
		});
		catTable.add(catbutt);
	}
	rebuildParts.run();
	let leftside = new Table();
	leftside.add(catTable).align(Align.left);
	leftside.row();
	leftside.add(pane).minWidth(200).maxHeight(400).align(Align.top).get().setScrollingDisabled(true, false);

	let costCons = cons((csttbl) => {
			csttbl.clearChildren();
			csttbl.add("[accent]Total Cost").padBottom(4);
			csttbl.row();
			csttbl.table(cons((bottbl) => {
					let cstot = modelement.getTotalCost();

					for (let cstname in cstot) {
						let cstamount = cstot[cstname];
						if (!itemcache[cstname]) {
							itemcache[cstname] = Vars.content.getByName(ContentType.item, cstname);
						}
						bottbl.image(itemcache[cstname].icon(Cicon.small)).left();
						bottbl.add("[gray]" + Math.floor(cstamount)).padLeft(2).left().padRight(4);
						bottbl.row();
					}
				}));

		});
	let totals = new Table();
	let rebuildTotals = run(() => costCons.get(totals));

	table.add(leftside).minWidth(150).align(Align.top);
	table.add(modelement).size(750, 400);
	table.add(totals).minWidth(100).maxHeight(400).align(Align.top);
	modelement.setOnTileAction(run(() => {
			rebuildParts.run();
			rebuildTotals.run();
		}));

	rebuildTotals.run();

	return modelement;
}

const _ModularBlock = {
	gridW: 1,
	gridH: 1,
	_timerid:1,
	_autoBuildDelay: 10,
	
	getGridWidth() {
		return this.gridW;
	},
	setGridWidth(s) {
		this.gridW = Math.min(16, s);
	},
	getGridHeight() {
		return this.gridH;
	},
	setGridHeight(s) {
		this.gridH = Math.min(16, s);
	},

	setConfigs() {
		this.config(java.lang.String, (a, b) => a.setBlueprintFromString(b));
		this.configClear((tile) => tile.setBlueprint(null));
	},
	getBuildTimerId(){
		if(!this._timerid){
			this._timerid = this.timers++;
		}
		return this._timerid;
	},
	setAutoBuildDelay(s){
		this._autoBuildDelay=s;
	},
	getAutoBuildDelay(){
		return this._autoBuildDelay;
	}

}

const _ModularBuild = {
	_blueprint: null,
	_blueprintRemainingCost: null,
	_totalItemCountCost: 0,
	_totalItemCountPaid: 0,
	_buffer: null,
	_currentStats: null,
	getPaidRatio() {
		if (!this._totalItemCountCost) {
			return 0;
		}
		return this._totalItemCountPaid / this._totalItemCountCost;
	},
	setBlueprintFromString(s) {
		return this.setBlueprint(unpackIntsFromString(s));
	},
	setBlueprint(s) {
		if (!s && !this._blueprint) {
			return false;
		}
		if ((!s && this._blueprint) || (s && !this._blueprint) || s.length != this._blueprint.length) {
			this._blueprint = s;
			return true;
		}
		for (var p = 0; p < this._blueprint.length; p++) {
			if (this._blueprint[p] != s[p]) {
				this._blueprint = s;
				return true;
			}
		}
		return false;
	},
	getBlueprint(s) {
		return this._blueprint;
	},
	getBufferRegion() {
		if (!this._buffer) {
			return null;
		}
		return Draw.wrap(this._buffer.getTexture());
	},

	displayExt(table) {
		let that = this;
		let ps = " " + StatUnit.perSecond.localized();
		let csttable = new Table();
		table.row();

		let costCons = cons(sub => {
				sub.clearChildren();
				if (that._totalItemCountPaid == that._totalItemCountCost) {
					return;
				}
				sub.left();
				if (that._blueprintRemainingCost) {
					let rc = that._blueprintRemainingCost;
					for (let i in rc) {
						sub.image(rc[i].item.icon(Cicon.medium));
						sub.add(rc[i].paid + "/" + rc[i].total);
						sub.row();
					}
				} else {
					sub.labelWrap("No blueprint").color(Color.lightGray);
				}
				
			});

		table.add(csttable).left().update(() => {
			costCons.get(csttable);
		});
	},
	////team().core().items
	updateAutoBuild(){
		if (this._totalItemCountPaid >= this._totalItemCountCost) {return;}
		let rc = this._blueprintRemainingCost;
		if(this.team.rules().infiniteResources){
			for (let i in rc) {
				rc[i].paid=rc[i].total; 
			}
			this._totalItemCountPaid =this._totalItemCountCost;
			this.applyStats(this._currentStats);
			return;
		}
		if(this.timer.get(this.block.getBuildTimerId(),this.block.getAutoBuildDelay())){
			let citems = this.team.core().items;
			for (let i in rc) {
				if(rc[i].paid<rc[i].total && citems.get(rc[i].item)>0){
					citems.remove(rc[i].item, 1);
					this._totalItemCountPaid++;
					rc[i].paid++;
					if (this._totalItemCountPaid == this._totalItemCountCost) {
						this.applyStats(this._currentStats);
					}
					return;
				}
			}
		}
	},
	
	acceptItem(source, item) {
		var hasspace = this._blueprintRemainingCost && this._blueprintRemainingCost[item.name] && this._blueprintRemainingCost[item.name].paid < this._blueprintRemainingCost[item.name].total;
		return this.super$acceptItem(source, item) || hasspace || this.acceptItemExt(source, item);
	},

	handleItem(source, item) {
		if (this._totalItemCountPaid == this._totalItemCountCost) {
			this.handleItemExt(source, item)
			return;
		}
		this._totalItemCountPaid++;
		this._blueprintRemainingCost[item.name].paid++;
		if (this._totalItemCountPaid == this._totalItemCountCost) {
			this.applyStats(this._currentStats);
		}
	},
	
	acceptItemExt(source, item) {
		return false;
	},
	handleItemExt(source, item) {},
	getPartsCatagories() {},
	getPartsConfig() {},
	getPartsAtlas() {},
	resetStats() {},
	applyStats(total) {},
	accumStats(total, part, x, y, grid) {},
	drawPartBuffer(part, x, y, grid) {
		Draw.rect(part.texRegion, (x + part.tw * 0.5) * 32, (y + part.th * 0.5) * 32, part.tw * 32, part.th * 32);
	},
	
	
	buildConfiguration(table) {
		let buttoncell = table.button(Tex.whiteui, Styles.clearTransi, 50, run(() => {
					let dialog = new BaseDialog("Edit Blueprint");
					dialog.setFillParent(false);
					var patlas = this.getPartsAtlas();
					let mtd = applyModularConstructorUI(dialog.cont, patlas, Math.round(patlas.width / 32), Math.round(patlas.height / 32),
							this.getPartsConfig(),
							this.block.getGridWidth(),
							this.block.getGridHeight(),
							this._blueprint,
							this.getPartsCatagories());
					dialog.buttons.button("@ok", () => {
						this.configure(mtd.getPackedSave());
						dialog.hide();
					}).size(130.0, 60.0);
					dialog.update(() => {
						if (!this.tile.bc() || !this.tile.bc().getBlueprint) {
							dialog.hide();
						}
					});
					dialog.show();

				}));
		buttoncell.size(50);
		buttoncell.get().getStyle().imageUp = Icon.pencil;
		if(this.block.hasItems){
			Vars.control.input.frag.inv.showFor(this);
		}
	},
	configured(player, value) {
		let changed = false;
		if (!Array.isArray(value)) {
			changed = this.setBlueprintFromString(value);
		} else {
			changed = this.setBlueprint(unpackInts(value));
		}
		if (!changed) {
			print("Blueprint was not changed");
			return;
		}
		this.resetStats();
		let totalcst = {};
		let cstmult = 1;
		for (var p = 0; p < this._blueprint.length; p++) {
			if (this._blueprint[p] != 0) {
				var partL = this.getPartsConfig()[this._blueprint[p] - 1];
				cstmult += costaccumrate * partL.tw * partL.th;
			}
		}
		//getting the cost, and packing it back into a 2d array
		cstmult -= costaccumrate;
		this._totalItemCountCost = 0;
		this._totalItemCountPaid = 0;
		var gridprint = [];
		var newStatVals = [];
		for (var p = 0; p < this._blueprint.length; p++) {
			if (this._blueprint[p] != 0) {
				var partL = this.getPartsConfig()[this._blueprint[p] - 1];
				var prttmp = partL.cost;
				for (let cstitem = 0; cstitem < prttmp.length; cstitem++) {
					if (!totalcst[prttmp[cstitem].name]) {
						totalcst[prttmp[cstitem].name] = {
							total: 0,
							paid: 0,
							item: Vars.content.getByName(ContentType.item, prttmp[cstitem].name)
						};
					}
					totalcst[prttmp[cstitem].name].total += Math.floor(prttmp[cstitem].amount * cstmult);
					this._totalItemCountCost += Math.floor(prttmp[cstitem].amount * cstmult);
				}
			}
			if (!gridprint[Math.floor(p / this.block.getGridHeight())]) {
				gridprint[Math.floor(p / this.block.getGridHeight())] = [];
			}
			gridprint[Math.floor(p / this.block.getGridHeight())][p % this.block.getGridHeight()] = this._blueprint[p];
		}
		this._blueprintRemainingCost = totalcst;

		//detemining the stats of the blueprint,
		for (p = 0; p < this._blueprint.length; p++) {
			if (this._blueprint[p] == 0) {
				continue;
			}
			let px = Math.floor(p / this.block.getGridHeight());
			let py = (p % this.block.getGridHeight());
			this.accumStats(newStatVals, this.getPartsConfig()[this._blueprint[p] - 1], px, py, gridprint);
		}
		this._currentStats = newStatVals;

		//drawing the sprite.
		if (!Vars.headless) {
			Draw.draw(Draw.z(), () => {
				Tmp.m1.set(Draw.proj());
				if (!this._buffer) {

					this._buffer = new FrameBuffer(this.block.getGridWidth() * 32, this.block.getGridHeight() * 32);
				}
				Draw.proj(0, 0, this.block.getGridWidth() * 32, this.block.getGridHeight() * 32);
				this._buffer.begin(Color.clear);
				Draw.color(Color.white);
				for (var p = 0; p < this._blueprint.length; p++) {
					if (this._blueprint[p] == 0) {
						continue;
					}
					let px = Math.floor(p / this.block.getGridHeight());
					let py = (p % this.block.getGridHeight());
					this.drawPartBuffer(this.getPartsConfig()[this._blueprint[p] - 1], px, py, gridprint);
				}
				this._buffer.end();
				Draw.proj(Tmp.m1);
				Draw.reset();
			});
		}

	},
	config() {
		if (!this._blueprint) {
			return new java.lang.String("")
		}
		var tmp = _packArray(this._blueprint);
		return tmp.toStringPack();
	},
	
	writeExt(stream) {
		if (!this._blueprint) {
			stream.i(0);
			return;
		}
		var tmp = _packArray(this._blueprint);
		stream.s(tmp.packed.length);
		for (var i = 0; i < tmp.packed.length; i++) {
			stream.i(tmp.packed[i]);
		}
		if (this._blueprintRemainingCost) {

			let rc = this._blueprintRemainingCost;
			let am = 0;
			for (let i in rc) {
				am++;
			}
			stream.s(am);
			for (let i in rc) {
				stream.s(rc[i].item.id);
				stream.s(rc[i].paid);
			}
		} else {
			stream.s(0);
		}

	},
	readExt(stream, revision) {
		var packedsize = stream.s();
		var pack = [];
		for (var i = 0; i < packedsize; i++) {
			pack[i] = stream.i();
		}
		this.configured(null, pack);
		var costSize = stream.s();
		if (costSize) {
			let rc = this._blueprintRemainingCost;
			for (var i = 0; i < costSize; i++) {
				var itmid = stream.s();
				var pam = stream.s();
				for (let cid in rc) {
					if (itmid == rc[cid].item.id) {
						rc[cid].paid = pam;
						this._totalItemCountPaid += pam;
						break;
					}
				}
			}
		}
		if (this._totalItemCountPaid == this._totalItemCountCost) {
			this.applyStats(this._currentStats);
		}

	}
}

const ammotype = {
	normal:{
		"copper":{am:1},"graphite":{am:2},"unity-nickel":{am:1}
	},
	fire:{
		"coal":{am:1},"pyratite":{am:3},"blast-compound":{am:1},"plastanium":{am:1}
	},
	explosive:{
		"coal":{am:0.5},"blast-compound":{am:4},"thorium":{am:1}
	},
	frag:{
		"metaglass":{am:1},"plastanium":{am:3}
	},
	heavy:{
		"lead":{am:1},"titanium":{am:1.5},"unity-nickel":{am:1.5},"unity-super-alloy":{am:4}
	},
	shock:{
		"surge-alloy":{am:1}
	},
	homing:{
		"silicon":{am:2}
	},
}
const ammotypeIcons = {}
function getAmmoIcon(type){
	 if(ammotypeIcons[type]){
		 ammotypeIcons[type]= Core.atlas.find("unity-icon-ammo-"+type);
	 }
	 return  ammotypeIcons[type];
}


const normalBulletType = { // BasicBulletType with all the fat cut out.
		hasInit:false,
		backRegion: null,
		frontRegion: null,
		mixColorFrom: null,
		mixColorTo: null,
		backColor: null,
		frontColor: null,
		width:5,
		height:9,
		load() {
			this.backRegion = Core.atlas.find("bullet-back");
			this.frontRegion = Core.atlas.find("bullet");
			this.mixColorFrom = new Color(1.0, 1.0, 1.0, 0.0);
			this.mixColorTo = new Color(1.0, 1.0, 1.0, 0.0);
			this.backColor = Pal.bulletYellowBack;
			this.frontColor = Pal.bulletYellow;
			this.hasInit=true;
			this.loadExt();
		},
		loadExt(){
			
		},
		draw(b) {
			if(!this.hasInit){
				this.load(); // uh yeh, since this is created dynamically unfortunatly.
			}
			var height = this.height;
			var width = this.width;

			var mix = Tmp.c1.set(this.mixColorFrom).lerp(this.mixColorTo, b.fin());

			Draw.mixcol(mix, mix.a);

			Draw.color(this.backColor);
			Draw.rect(this.backRegion, b.x, b.y, width, height, b.rotation()-90);
			Draw.color(this.frontColor);
			Draw.rect(this.frontRegion, b.x, b.y, width, height, b.rotation()-90);

			Draw.reset();
		}
	};



const BulletTypesMap = {
	
	normal: normalBulletType,
	grenade: Object.assign(Object.create(normalBulletType),{
		trailEffect: null,
		loadExt(){
			this.trailEffect = Fx.artilleryTrail;
			this.width=6;
			this.height=4;
			this.hitEffect= Fx.blastExplosion;
			this.frontColor = Color.gray;
		},
		getZ(b){
			let x = b.fin();
			return Math.abs(Mathf.sin(5*x*3.1415))/(Math.floor(x*5)*Math.floor(x*5)+1);
		},
		justBounced(b){
			let x = b.fin();
			let px = (b.time-Time.delta*2.0)/b.lifetime;
			return Math.floor(5*x)!= Math.floor(5*px);
		},
		collides(bullet, tile){
			return this.super$collides(bullet,tile) && this.getZ(bullet)<0.2;
		},
		update(b){
			if(!this.hasInit){
				this.load(); // uh yeh, since this is created dynamically unfortunatly.
			}
			this.super$update(b);
			
			if(b.fin()<0.15&&b.timer.get(0, (3 + b.fslope() * 2) * 1.0)){
				this.trailEffect.at(b.x, b.y, b.fslope() * 4.0*Mathf.clamp(b.fout()), this.backColor);
			}
			
			if(this.justBounced(b)){
				b.vel.x *= 0.8;
				b.vel.y *= 0.8;
				
			}
			
			let zh = this.getZ(b);
			var tile = Vars.world.tileWorld(b.x, b.y);
			if(tile == null || tile.build == null|| zh>0.2 || b.fin()<0.05) return;
			
			if(tile.solid()){
				b.trns(-b.vel.x, -b.vel.y);
				
				let penX = Math.abs(tile.bc().x - b.x);
				let penY = Math.abs(tile.bc().y - b.y);

                if(penX > penY){
                    b.vel.x *= -0.5;
                }else{
                    b.vel.y *= -0.5;
                }
			}
			
		},
		draw(b) {
			if(!this.hasInit){
				this.load(); // uh yeh, since this is created dynamically unfortunatly.
			}
			let scl = this.getZ(b)+1;
			let offset = Time.time()*3.0;
			var height = this.height*scl;
			var width = this.width*scl;
			let flash = Mathf.pow(2,5*b.fin()-1)%1.0>0.5;

			var mix = Tmp.c1.set(this.mixColorFrom).lerp(this.mixColorTo, b.fin());

			Draw.mixcol(mix, mix.a);

			Draw.color(this.backColor);
			Draw.rect(this.backRegion, b.x, b.y, width, height, b.rotation()-90+offset);
			Draw.color(this.frontColor.cpy().lerp(Color.white,flash?1:0));
			Draw.rect(this.frontRegion, b.x, b.y, width, height, b.rotation()-90+offset);
			
			Draw.reset();
		}
		//todo
	}),
	shell: Object.assign(Object.create(normalBulletType),{
		loadExt(){
			this.width=10;
			this.height=14;
			this.hitEffect= Fx.flakExplosion;
		}
		//todo
	}),
	cluster: {
		//todo
	},

}

function getBulletTypeFromConfig(config) {
	let bullet = extend(BulletType, deepCopy(BulletTypesMap[config.type]));
	bullet.damage = config.damage;
	bullet.speed = config.speed;
	bullet.lifetime = config.lifetime ? config.lifetime : (config.range / config.speed);
	bullet.splashDamage = config.splashDamage ? config.splashDamage : 0;
	bullet.splashDamageRadius = config.splashDamageRadius ? config.splashDamageRadius : 0;
	bullet.pierceBuilding = config.pierce ? config.pierce > 0 : false;
	bullet.pierceCap = config.pierce ? config.pierce : -1;
	bullet.knockback = config.knockback ? config.knockback : 0;
	bullet.makeFire = config.incindiary ? config.incindiary : false;
	bullet.status = config.status ? config.status : StatusEffects.none;
	bullet.collidesAir = config.collidesAir ? config.collidesAir : true;
	bullet.collidesGround = config.collidesGround ? config.collidesGround : true;
	bullet.collidesTeam = config.collidesTeam ? config.collidesTeam : false;
	bullet.collidesTiles = config.collidesTiles ? config.collidesTiles : true;
	bullet.hittable = config.hittable ? config.hittable : true;
	bullet.reflectable = config.reflectable ? config.reflectable : true;
	bullet.absorbable = config.absorbable ? config.absorbable : true;
	bullet.healPercent = config.healPercent ? config.healPercent : 0;
	bullet.homingPower = config.homingPower ? config.homingPower : 0;
	bullet.homingRange = config.homingRange ? config.homingRange : 0;
	//frag bullets
	bullet.fragBullet = config.fragBullet ? config.fragBullet.get() : null;
	bullet.fragBullets = config.fragBullets ? config.fragBullets : 0;
	return bullet;

}


function mergeStats(tos, froms){
	if(froms.shots){
		froms.shots*=tos.shots;
	}
	if(froms.reloadmult){
		froms.reloadmult*=tos.reloadmult;
	}
	if(froms.shots){
		froms.shots*=tos.shots;
	}
	if(froms.ammoType){
		for(let type in tos.ammoType){
			if(!froms.ammoType[type]){
				froms.ammoType[type] = tos.ammoType[type];
			}else{
				froms.ammoType[type] += tos.ammoType[type];
			}
		}
	}
	if(froms.ammoCostMul){
		if(!froms.ammoType){
			for(let type in tos.ammoType){
				froms.ammoType[type] = tos.ammoType[type];
			}
		}
		for(let type in froms.ammoType){
			froms.ammoType[type]*=froms.ammoCostMul;
		}
	}
	return Object.assign(tos, froms);
}

const _TurretModularBuild = Object.assign(deepCopy(_ModularBuild), {
		/*
		[{
		offsetx,
		offsety,
		ammocost:[
		]
		bullettypes,
		chainfire,
		spread,
		}
		]
		 */
		guns: null,
		originalmaxhp: 0,
		currentBarrel: 0,
		validTurret: false,
		//gun stats
		turretRange:80,
		itemcap:10,

		acceptItemExt(source, item) {
			if(!this.validTurret){
				return false;
			}
			for (let i = 0; i < this.guns.length; i++) {
				let ammoreq = this.guns[i].ammoType;
				for(let ammot in ammoreq){
					let allowed = ammotype[ammot];
					let valid = false;
					for(let alloweditem in allowed){
						if(alloweditem==item.name){
							return this.items.get(item)<this.itemcap;
						}
					}
				}
			}
			return false;
		},
		handleItemExt(source, item) {
			this.super$handleItem(source, item);
		},
		hasAmmo() {
			if(!this.validTurret){
				return false;
			}
			if(this.guns[this.currentBarrel].magazine<=0){
				this.attemptRefillMag(this.guns[this.currentBarrel]);
			}
			return this.guns[this.currentBarrel].magazine>0;
		},
		attemptRefillMag(gun){
			let ammoreq = gun.ammoType;
			let consume = [];
			for(let ammot in ammoreq){
				let allowed = ammotype[ammot];
				let valid = false;
				for(let alloweditem in allowed){
					let item = Vars.content.getByName(ContentType.item, alloweditem);
					let itemsneeded = Math.ceil(ammoreq[ammot]/ammotype[ammot][alloweditem].am);
					if(this.items.has(item, itemsneeded)){
						//add the ammmo
						consume.push({item:item, am:itemsneeded});
						valid = true;
						break;
					}
				}
				if(!valid){
					return;
				}
			}
			gun.magazine+=gun.magazineSize;
			for(let i =0;i<consume.length;i++){
				this.items.remove(consume[i].item,consume[i].am);
			}
		},
		//Consume ammo and return a type.
		useAmmo() {
			if(this.guns[this.currentBarrel].magazine==0){
				this.attemptRefillMag(this.guns[this.currentBarrel]);
			}
			if(this.guns[this.currentBarrel].magazine>0){
				this.guns[this.currentBarrel].magazine--;
			}
			return this.guns[this.currentBarrel].bullettype;
		},
		//the ammo type that will be returned if useAmmo is called.
		peekAmmo() {
			return this.guns[this.currentBarrel].bullettype;
		},
		applyStats(total) {
			this.originalmaxhp = this.maxHealth;
			this.maxHealth = this.originalmaxhp + total.hpinc;
			this.turretRange=80 + (total.rangeinc?total.rangeinc:0);
			this.heal(total.hpinc* this.health/this.originalmaxhp);
			this.itemcap=10;
			if (!total.guns) {
				this.validTurret = false;
				return;
			}
			let lt = [];
			for (let i = 0; i < total.guns.length; i++) {
				if (!total.guns[i]) {
					continue;
				}
				lt.push(mergeStats(total.guns[i], total.globalStats));
				lt[i].bullettype = getBulletTypeFromConfig(lt[i]);
				lt[i].magazine =0;

			}
			this.guns = lt;
			this.validTurret = true;
			this.reloadTime = total.reload;
		},

		displayBarsExt(barstable){
			if(this.validTurret){
				barstable.row();
				let that =this;
				let debuglabel = new Label(prov(()=>{
					let text = "";
					for (let i = 0; i < that.guns.length; i++) {
						text+="[white]([gray]"+that.guns[i].magazine+"/[white]"+that.guns[i].magazineSize+")-"
					}
					return text;
				}));
				barstable.add(debuglabel);
			}
		},
		accumStats(total, part, x, y, grid) {
			if (!total.hpinc) {
				total.hpinc = 0;
			}
			if(!total.globalStats){
				total.globalStats = {};
			}
			total.hpinc += part.stats["hp"].value;
			if( part.stats["rangeinc"]){
				if(!total.rangeinc){
					total.rangeinc=0;
				}
				total.rangeinc +=  part.stats["rangeinc"].value;
			}
			if (part.category == "base") {
				print("encountered base part");
				total.reload = part.stats.reload.value;
				for (let i = 0; i < part.connOutList.length; i++) {
					let attach = part.connOutList[i];
					let atx = attach.x + x + attach.dir.x;
					let aty = attach.y + y + attach.dir.y;
					if (grid[atx] && grid[atx][aty]) {

						let guninfo = this.getPartsConfig()[grid[atx][aty] - 1];
						switch (guninfo.category) {
						case "breach":
							if (!total.guns) {
								total.guns = [];
							}
							let ammoType = {};
							ammoType[guninfo.stats.ammoType.value]=guninfo.stats.payload.value;
							let basestats = {
								damage: guninfo.stats.baseDmg.value,
								speed: guninfo.stats.baseSpeed.value,
								lifetime: guninfo.stats.lifetime.value,
								type: guninfo.stats.bulletType.value,
								ammoType: ammoType,
								shots: guninfo.stats.shots.value,
								spread: guninfo.stats.spread.value,
								reloadmult: guninfo.stats.reloadMultiplier.value,
								magazineSize: guninfo.stats.magazine.value,
							};
							if(guninfo.stats.mod){
								guninfo.stats.mod.cons.get(basestats);
							}
							total.guns.push(basestats);
							break;
						case "ammo":
							guninfo.stats.mod.cons.get(total.globalStats);
							break;
						}

						if (guninfo.category != "breach") {
							continue;
						}
						print("encountered breach");

					}
				}
				//find the rest of the gunz
			}
		},
		resetStats() {
			if (this.originalmaxhp) {
				this.maxHealth = this.originalmaxhp;
			}
			this.validTurret = false;
			this.turretRange=80;
			this.itemcap=10;
		},
		updateShooting() {
			if (!this.valid) {
				return;
			}
			if (this.reload >= this.reloadTime*this.guns[this.currentBarrel].reloadmult) {
				let type = this.guns[this.currentBarrel];
				this.shootType(type);
				this.currentBarrel++;
				this.currentBarrel = this.currentBarrel % this.guns.length;

				this.reload = 0;
			} else {
				this.reload += this.delta() * this.baseReloadSpeed();
			}
		},
		shootType(configtype){
			this.block.tr.trns(this.rotation, this.block.size * Vars.tilesize / 2.0, Mathf.range(this.block.xRand));

			for(let i = 0; i < configtype.shots; i++){
				this.bullet(configtype.bullettype, this.rotation + Mathf.range(configtype.spread));
			}
			this.useAmmo();
		},
		findTarget(){
			let targetAir = true;
			let targetGround = true;
            if(targetAir && !targetGround){
                this.target = Units.bestEnemy(this.team, this.x, this.y, this.turretRange, e => !e.dead && !e.isGrounded(), this.block.unitSort);
            }else{
                this.target = Units.bestTarget(this.team, this.x, this.y,  this.turretRange, e => !e.dead && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b => true, this.block.unitSort);
				//print(this.target);
				//print("r:"+this.turretRange+",t:"+this.team+",s:"+this.block.unitSort);
			}
			
        },
		drawSelect(){
            Drawf.dashCircle(this.x, this.y, this.turretRange, this.team.color);
        }

	});

const IntPacker = {
	packed: [],
	raw: [],
	prev: -1,
	count: 0,
	highi: false,
	packindex: -1,
	new() {
		return deepCopy(Object.create(IntPacker));
	},
	add(bytef) {
		if (bytef != this.prev) {
			if (this.prev != -1) {
				if (!this.highi) {
					this.packed.push(0);
					this.packindex++;
				}
				this.raw.push(this.count);
				this.raw.push(this.prev);
				let comb = this.prev + this.count * 256;
				this.packed[this.packindex] += comb << (this.highi ? 16 : 0);
				this.highi = !this.highi;
			}
			this.count = 1;
			this.prev = bytef;
		} else {
			this.count++;
		}
	},
	end() {
		if (this.prev != -1) {
			if (!this.highi) {
				this.packed.push(0);
				this.packindex++;
			}
			this.raw.push(this.count);
			this.raw.push(this.prev);
			let comb = this.prev + this.count * 256;
			this.packed[this.packindex] += comb << (this.highi ? 16 : 0);
			this.highi = !this.highi;
		}
		return this.packed;
	},
	toStringPack() {
		var str = "";
		for (var i = 0; i < this.raw.length; i++) {
			str += String.fromCharCode(this.raw[i]);
		}
		return new java.lang.String(str);
	}

}
function _packArray(a) {
	var packer = IntPacker.new();
	var i = 0;
	for (; i < a.length; i++) {
		packer.add(a[i]);
	}
	packer.end();
	return packer;

}

function unpackInts(intpack) {
	let out = [];
	for (let i = 0; i < intpack.length * 2; i++) {
		let cint = intpack[Math.floor(i / 2)];
		let value = (cint >> (i % 2 == 0 ? 0 : 16)) & 65535;
		let val = value & 255;
		let am = (value >> 8) & 255;
		for (let k = 0; k < am; k++) {
			out.push(val);
		}
	}
	return out;
}
function unpackIntsFromString(sintpack) {
	let out = [];
	let str = "" + sintpack;
	for (let i = 0; i < str.length; i += 2) {
		let val = str.charCodeAt(i + 1);
		let am = str.charCodeAt(i);
		for (let k = 0; k < am; k++) {
			out.push(val);
		}
	}
	return out;
}

function deepCopy(obj) {
	var clone = {};
	for (var i in obj) {
		if (Array.isArray(obj[i])) {
			clone[i] = [];
			for (var z in obj[i]) {
				if (typeof(obj[i][z]) == "object" && obj[i][z] != null) {
					clone[i][z] = deepCopy(obj[i][z]);
				} else {
					clone[i][z] = obj[i][z];
				}
			}
		} else if (typeof(obj[i]) == "object" && obj[i] != null)
			clone[i] = deepCopy(obj[i]);
		else
			clone[i] = obj[i];
	}
	return clone;
}

function _getRegion(region, tile, w, h) {
	if (!region) {
		print("oh no there is no texture");
		return;
	}
	let nregion = new TextureRegion(region);
	let tilew = (nregion.u2 - nregion.u) / w;
	let tileh = (nregion.v2 - nregion.v) / h;
	let tilex = (tile % w) / w;
	let tiley = Math.floor(tile / w) / h;

	nregion.u = Mathf.map(tilex, 0, 1, nregion.u, nregion.u2) + tilew * 0.02;
	nregion.v = Mathf.map(tiley, 0, 1, nregion.v, nregion.v2) + tileh * 0.02; //y is flipped h
	nregion.u2 = nregion.u + tilew * 0.96;
	nregion.v2 = nregion.v + tileh * 0.96;
	nregion.width = 32;
	nregion.height = 32;
	return nregion;
}
var tmpRegConstruct = null; //for some reason the runnable always holds the same reference of region.
function _drawConstruct(region, progress, color, alpha, time, layer, func) {
	if (!tmpRegConstruct) {
		tmpRegConstruct = new TextureRegion(region);
	}
	tmpRegConstruct.set(region);
	Draw.draw(layer, run(() => {
			Shaders.build.region = tmpRegConstruct;
			Shaders.build.progress = progress;
			Shaders.build.color.set(color);
			Shaders.build.color.a = alpha;
			Shaders.build.time = -time / 20.0;

			Draw.shader(Shaders.build);
			func(tmpRegConstruct);
			Draw.shader();
			Draw.reset();
		}));
}

function _drawTile(region, x, y, w, h, rot, tile) {
	Draw.rect(_getRegion(region, tile), x, y, w, h, w * 0.5, h * 0.5, rot);
}

module.exports = {
	preCalcConnection:_preCalcConnection,
	ModularBlock: _ModularBlock,
	ModularBuild: _ModularBuild,
	TurretModularBuild: _TurretModularBuild,
	drawConstruct: _drawConstruct,
	dcopy2: deepCopy,
	IntPack: IntPacker,
	unpack: unpackInts,
	unpackFromString: unpackIntsFromString,
	packArray: _packArray,
	drawTile: _drawTile,
	getRegion: _getRegion,
	getConstructorUi: getModularConstructorUI,
	applyUI: applyModularConstructorUI,
}
