
//imports
importPackage(Packages.arc.graphics.gl);

const graphLib = require("libraries/graphlib");
//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.
print("youndcha test2")
//ui test, if sucessful will be moved to seperate js file
importPackage(Packages.arc.util.pooling);
importPackage(Packages.arc.scene);

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
const costaccumrate = 0.5;

const modularConstructorUI = {
	_prefHeight: 100,
	_partsSprite: null,
	_partsSelect: null,
	_costAccum: 1.0,
	_onTileAction: null,
	_PartList: [], //just store in list instead of grid h.
	//ok i actualy need a grid now fk.
	_Grid:[],
	_gridW: 1,
	_gridH: 1,
	
	

	//Ui
	_isClickedRN: false,
	_hover:null,

	fireClick() {
		this.super$fireClick();
		print("hello");
	},
	draw() {
		let amx = this.x + this.width * 0.5;
		let amy = this.y + this.height * 0.5;

		let gw = this._gridW * 32;
		let gh = this._gridH * 32;
		let gamx = amx - gw * 0.5 ;
		let gamy = amy - gh * 0.5 ;
		Draw.color(bgCol);
		Fill.rect(amx, amy, this.width, this.height);
		Draw.color(blueprintCol);
		Fill.rect(amx, amy, gw, gh);
		Draw.color();
		for (let i = 0; i < this._PartList.length; i++) {
			let p = this._PartList[i];
			Draw.rect(p.part.texRegion, p.x*32 + gamx + (p.part.tw*16), p.y*32 + gamy + (p.part.th*16), p.part.tw*32, p.part.th*32);
		}
		Draw.color(Color.black);
		Fill.rect(this.x + 20, this.y + 20, 40, 40);
		Draw.color();
		if(this._partsSelect){
			Draw.rect(this._partsSelect.texRegion, this.x + 20, this.y + 20, 32, 32);
			if(this._hover){
				let ps = this._partsSelect;
				Draw.color(this.canPlace(ps,this._hover.x,this._hover.y)?Color.white:Color.red,0.3);
				Draw.rect(ps.texRegion, this._hover.x*32 + gamx + (ps.tw*16), this._hover.y*32 + gamy + (ps.th*16), ps.tw*32, ps.th*32);
			}
		}
		

	},
	getPrefHeight() {
		return this._prefHeight;
	},
	setPrefHeight(s) {
		this._prefHeight = s;
	},
	setGrid(p,x,y){
		if(!this._Grid[x]){
			this._Grid[x]=[];
		}
		this._Grid[x][y]=p;
	},
	inBounds(partType,x,y){
		return partType && this.inBoundsRect(x,y,partType.tw,partType.th);
	},
	inBoundsRect(x,y,w,h){
		return !(x < 0 || x + w > this._gridW || y < 0 || y + h > this._gridH);
	},
	canPlace(partType, x, y) {
		if (!this.inBounds(partType,x,y)) {
			return false;
		}
		for(let px =0;px<partType.tw;px++){
			for(let py =0;py<partType.th;py++){
				if (this.getPartAt(x+px,y+py)) {
					return false;
				}
			}
		}
		
		//conection
		
		return true;
	},
	placeTile(partType, x, y) {
		if (!this.canPlace(partType,x,y)) {
			return false;
		}
		let partPlaceobj = {
			x: x,
			y: y,
			part: partType
		};
		for(let px =0;px<partType.tw;px++){
			for(let py =0;py<partType.th;py++){
				this.setGrid(partPlaceobj,x+px,y+py);
			}
		}
		
		this._PartList.push(partPlaceobj);
		this._costAccum += costaccumrate;
		return true;
	},
	onIsClicked(event, x, y, point, butt) {
		this._isClickedRN = true;
		let gpos = this.uiToGridPos(x, y);
		let success = this.placeTile(this._partsSelect, gpos.x, gpos.y);
		if (this._onTileAction&&success) {
			this._onTileAction.run();
		}
	},
	onIsReleased(event, x, y, point, butt) {
		this._isClickedRN = false;
	},
	onIsDragged(event, x, y, point, butt) {
		if (this._isClickedRN) {
			let gpos = this.uiToGridPos(x, y);
			let success = this.placeTile(this._partsSelect, gpos.x, gpos.y);
			if (this._onTileAction&&success) {
				this._onTileAction.run();
			}
		}
	},
	onIsHovering(event, x, y) {
		if (x < 0 || x> this.width || y < 0 || y > this.height) {
			this._hover=null;
			return false;
		}
		this._hover = this.uiToGridPos(x,y);
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
			mouseMoved(event,  x,  y) {
				return that.onIsHovering(event, x, y);
			},
			touchUp(event, x, y, pointer, button) {
				print("touchup");
				that.onIsReleased(event, x, y, pointer, button);
			},
			touchDragged(event, x, y, pointer) {
				that.onIsDragged(event, x, y, pointer, null);
			}
		}));
	},
	getTotalCost(){
		let cst  = {};
		for (let i = 0; i < this._PartList.length; i++) {
			let p = this._PartList[i].part.cost;
			for (let cstitem = 0; cstitem < p.length; cstitem++) {
				if(!cst[p[cstitem].name]){
					cst[p[cstitem].name] = 0;
				}
				cst[p[cstitem].name] += Math.floor(p[cstitem].amount*(this._costAccum-0.5));
			}
		}
		return cst;
	},
	getPartAt(x, y) {
		if (!this.inBoundsRect(x,y,1,1) || !this._Grid[x]) {
			return  null;
		}
		return this._Grid[x][y]?this._Grid[x][y]:null;
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

	getCostAccum() {
		return this._costAccum;
	},
	setPartsSprite(s) {
		this._partsSprite = s;
	},
	setPartSelect(s) {
		print("PART SELECT:" + s.name);
		this._partsSelect = s;
	}
}

function getModularConstructorUI(pheight, partssprite,partsConfig,preconfig, maxw, maxh) {
	let pp = extend(Element, Object.create(deepCopy(modularConstructorUI)));
	pp.init();
	pp.setPrefHeight(pheight);
	pp.setPartsSprite(partssprite);
	pp.setGridSize(maxw, maxh);
	for (let i = 0; i < partsConfig.length; i++) {
		let pinfo = partsConfig[i];
		if(pinfo.prePlace){
			pp.placeTile(pinfo, pinfo.prePlace.x, pinfo.prePlace.y);
		}
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
	dialog.cont.add("[white]Name");
	dialog.cont.add("[gray]" + part.name);
	dialog.cont.row();
	dialog.cont.add("[white]Description:");
	dialog.cont.add("[gray]" + part.desc).maxWidth(500).get().setWrap(true);
	dialog.cont.row();

	dialog.buttons.button("@ok", () => {
		dialog.hide();
	}).size(130.0, 60.0);
	dialog.update(() => {});
	dialog.show();
}

/*
partsConfig format:
[
	{
		name,
		desc,
		tx,
		ty,
		tw,
		th,
		stats:{
			name: value, desc
		},
		cannotPlace:,
		prePlace:{
			x:,
			y:,
		},
		isRoot,
		cost:[
			{
				itemid:
				cost:
			}
		],
		connections:[]

	},
	...
]
 */
function applyModularConstructorUI(table, partssprite, spritew, spriteh, partsConfig, maxw, maxh, preconfig) {
	let modelement = getModularConstructorUI(400, partssprite,partsConfig,preconfig, maxw, maxh);
	let itemcache = {};

	let partSelectCons = cons((scrolltbl) => {
			let costinc = modelement.getCostAccum();
			scrolltbl.clearChildren();
			scrolltbl.top().left();
			for (let i = 0; i < partsConfig.length; i++) {
				partsConfig[i].id = i;
				let pinfo = partsConfig[i];
				if(pinfo.cannotPlace){
					pinfo.texRegion = _getRegionRect(partssprite, pinfo.tx, pinfo.ty, pinfo.tw, pinfo.th, spritew, spriteh);
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
							let texreg = _getRegionRect(partssprite, pinfo.tx, pinfo.ty, pinfo.tw, pinfo.th, spritew, spriteh);
							pinfo.texRegion = texreg;
							toptbl.add(new BorderImage(texreg, 2)).size(40 - 4).padTop(-4).padLeft(-4).padRight(4);
							
							toptbl.button(Tex.whiteui, Styles.clearTransi, 50, run(() => {
									displayPartInfo(pinfo)
								})).size(50).get().getStyle().imageUp = Icon.infoSmall;
							
						})).marginLeft(4);

						//butt.image(_getRegionRect(partssprite,pinfo.tx,pinfo.ty,pinfo.tw,pinfo.th,spritew,spriteh));  //part sprite
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
								bottbl.row();
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
	
	table.add(pane).minWidth(150).maxHeight(400).align(Align.top).get().setScrollingDisabled(true, false);
	table.add(modelement).size(750, 400);
	table.add(totals).minWidth(100).maxHeight(400).align(Align.top);
	modelement.setOnTileAction(run(() => {
			rebuildParts.run();
			rebuildTotals.run();
		}));
	rebuildParts.run();
	rebuildTotals.run();
	
	
}

function deepCopy(obj) {
	var clone = {};
	for (var i in obj) {
		if(Array.isArray(obj[i])){
			clone[i] = [];
			for(var z in obj[i]){
				if (typeof(obj[i][z]) == "object" && obj[i][z] != null){
					clone[i][z] = deepCopy(obj[i][z]);
				}else{
					clone[i][z] = obj[i][z];
				}
			}
		}
		else if (typeof(obj[i]) == "object" && obj[i] != null)
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

function _drawTile(region, x, y, w, h, rot, tile) {
	Draw.rect(_getRegion(region, tile), x, y, w, h, w * 0.5, h * 0.5, rot);
}

module.exports = {
	drawTile: _drawTile,
	getRegion: _getRegion,
	getConstructorUi: getModularConstructorUI,
	applyUI: applyModularConstructorUI,
}
