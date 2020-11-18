
//imports
importPackage(Packages.arc.graphics.gl);

const graphLib = require("libraries/graphlib");
//credit to younggam setting example of how to build new overlay resrouce(heat) and deltanedas for example of block graph system via phase router.
print("youndcha test2")
//ui test, if sucessful will be moved to seperate js file
importPackage(Packages.arc.util.pooling);
importPackage(Packages.arc.scene);


function _getRegionRect(region, x,y,rw,rh,w,h) {
    if (!region) {
        print("oh no there is no texture");
        return;
    }
    let nregion = new TextureRegion(region);
    let tilew = (nregion.u2 - nregion.u)/w;
	let tileh = (nregion.v2 - nregion.v)/h;
	let tilex = x/w;
	let tiley = y/h;
	
	nregion.u = Mathf.map(tilex,0,1,nregion.u,nregion.u2)+tilew*0.02;
	nregion.v = Mathf.map(tiley,0,1,nregion.v,nregion.v2)+tileh*0.02; //y is flipped h 
	nregion.u2 = nregion.u+tilew*rw - tilew*0.02;
	nregion.v2 = nregion.v+tileh*rh - tileh*0.02;
	nregion.width = 32*rw;
	nregion.height = 32*rh;
    return nregion;
}


const StackedBarChart ={
	_prefHeight:100,
	_barStats: prov(()=>
		{
			return [
				{name: "default", weight:1.6, filled: 0.5, color: Color.valueOf("6bc7ff")},
				{name: "default2", weight:1, filled: 0.8, color: Color.valueOf("ea7a55")}
			];
		}
	),
	draw(){
		let font = Fonts.outline;
        let lay = Pools.obtain(GlyphLayout,prov(()=>{return new GlyphLayout();}));
		
		let data = this._barStats.get();
		let totalweight = 0;
		for(let i=0;i<data.length;i++){
			totalweight += data[i].weight;
		}
		let ypos = this.y;
		for(let i=0;i<data.length;i++){
			let ah =  this.height*(data[i].weight/totalweight) ;
			let aw =  this.width*data[i].filled;
			let text = data[i].name;
			let dark = data[i].color.cpy().mul(0.5);
			Draw.color(dark);
			Fill.rect(this.x + this.width*0.5,ypos+ah*0.5,this.width,ah);
			Draw.color(data[i].color);
			Fill.rect(this.x + aw*0.5,ypos+ah*0.5,aw,ah);
			lay.setText(font, text);
			font.setColor(Color.white);
			font.draw(text, this.x + this.width / 2.0 - lay.width / 2.0, ypos + ah / 2.0 + lay.height / 2.0 + 1);
			
			ypos += ah;
		}

        Pools.free(lay);
		
		
	},
	setSize(x,y){
		this.super$setSize(x,y);
	},
	getPrefHeight() {
        return this._prefHeight;
    },
	getPrefWidth() {
        return 180.0;
    },
	setPrefHeight(s) {
        this._prefHeight=s;
    },
	setBarStatsProv(s) {
        this._barStats=s;
    }
	
};


function getStackedBarChart(pheight, datafunction, partsImage){
	let pp=  extend(Element,Object.create(StackedBarChart));
	//pp.setName("abcd");
	pp.setPrefHeight(pheight);
	pp.setBarStatsProv(datafunction);
	return pp;
	
}






const modularConstructorUI ={
	_prefHeight:100,
	_partsSprite: null,
	_partsSelect: null,
	fireClick() {
		this.super$fireClick();
		println("hello");
	},
	draw(){
		let amx = this.x + this.width*0.5;
		let amy = this.y + this.height*0.5;
		Draw.color(Color.valueOf("354654"));
		Fill.rect(amx,amy,this.width,this.height);
		Draw.color();
		Draw.rect(this._partsSprite,amx,amy);
	},
	getPrefHeight() {
        return this._prefHeight;
    },
	setPrefHeight(s) {
        this._prefHeight=s;
    },
	onIsClicked(event,x,y,point,butt){
		print(event);
		print("pos:"+x+","+y);
		print("this pos:"+this.x+","+this.y);
		print(point);
		print(butt);
	},
	init(){
		let that = this;
		this.addListener(extend(InputListener, {
			touchDown( event,  x,  y,  pointer,  button) {
				print("touchdown +4 points");
                if (that.disabled) {
                    return false;
                }else{
                    that.onIsClicked(event, x, y,pointer,button);
                    return true;
                }
            },
			touchUp( event,  x,  y,  pointer,  button) {
				print("touchup");
                that.onIsClicked(event, x, y,pointer,button);
            },
			touchDragged( event,  x,  y,  pointer) {
				that.onIsClicked(event, x, y,pointer,null);
            }
		}));
	},
	setPartsSprite(s){
		this._partsSprite=s;
	},
	setPartSelect(s){
		print("PART SELECT:"+s.name);
		this._partsSelect=s;
	}
}



function getModularConstructorUI(pheight,partssprite){
	let pp=  extend(Element,Object.create(modularConstructorUI));
	pp.init();
	pp.setPrefHeight(pheight);
	pp.setPartsSprite(partssprite);
	return pp;
	
}


function addConsButton(table,consFunc, style, runnable){
	let button = new Button(style);
	button.clearChildren();
	button.clicked(runnable);
	consFunc.get(button);
	return table.add(button);
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
			cost:[
				{
					itemid:
					cost:
				}
			]
			connection points oh no.
			
		},
		...
	]
*/
function applyModularConstructorUI(table,partssprite,spritew,spriteh, partsConfig){
	let modelement = getModularConstructorUI(350,partssprite);
	let itemcache = {};
	table.pane(Styles.defaultPane,cons((scrolltbl)=>{
		scrolltbl.top().left();
		scrolltbl.add("Parts");
		for(let i =0;i<partsConfig.length;i++){
			partsConfig[i].id = i;
			let pinfo = partsConfig[i];
			scrolltbl.row();
			addConsButton(scrolltbl,cons((butt) => {
				
				
				
				
				butt.top().left();
				butt.margin(12);

				butt.defaults().left().top();
				butt.table(cons((toptbl) => {
					toptbl.add(pinfo.name).size(100,45); //name
					let texreg = _getRegionRect(partssprite,pinfo.tx,pinfo.ty,pinfo.tw,pinfo.th,spritew,spriteh);
					toptbl.add(new BorderImage(texreg,2)).size(40 - 4).padTop(-4).padLeft(-4).padRight(4);
					
					
				}));
				
				//butt.image(_getRegionRect(partssprite,pinfo.tx,pinfo.ty,pinfo.tw,pinfo.th,spritew,spriteh));  //part sprite
				butt.row();
				butt.add("[grey]"+pinfo.desc).padBottom(8); 
				butt.row();
				butt.add("[accent]Cost").padBottom(4); 
				butt.row();
				butt.table(cons((bottbl) => {
					for(let cstitem = 0;cstitem<pinfo.cost.length;cstitem++){
						let cst = pinfo.cost[cstitem];
						if(!itemcache[cst.name]){
							itemcache[cst.name]=Vars.content.getByName(ContentType.item,cst.name);
						}
						bottbl.image(itemcache[cst.name].icon(Cicon.small)).left();
						bottbl.add("[gray]"+cst.amount).padLeft(2).left().padRight(4);
						bottbl.row();
					}
				}));
				
			}), 
			Styles.defaultt, 
			() => {
				modelement.setPartSelect(pinfo);//set part
			}).minWidth(150.0).padBottom(8);
		}
		
	}));
	
	table.add(modelement).size(750,350);
	
}
















function deepCopy(obj) {
    var clone = {};
    for (var i in obj) {
        if (typeof(obj[i]) == "object" && obj[i] != null) clone[i] = deepCopy(obj[i]);
        else clone[i] = obj[i];
    }
    return clone;
}

function _getRegion(region, tile,w,h) {
    if (!region) {
        print("oh no there is no texture");
        return;
    }
    let nregion = new TextureRegion(region);
    let tilew = (nregion.u2 - nregion.u)/w;
	let tileh = (nregion.v2 - nregion.v)/h;
	let tilex = (tile%w)/w;
	let tiley = Math.floor(tile/w)/h;
	
	nregion.u = Mathf.map(tilex,0,1,nregion.u,nregion.u2)+tilew*0.02;
	nregion.v = Mathf.map(tiley,0,1,nregion.v,nregion.v2)+tileh*0.02; //y is flipped h 
	nregion.u2 = nregion.u+tilew*0.96;
	nregion.v2 = nregion.v+tileh*0.96;
	nregion.width = 32;
	nregion.height = 32;
    return nregion;
}



function _drawTile(region, x, y, w, h, rot, tile) {
    Draw.rect(_getRegion(region, tile) , x, y, w, h, w * 0.5, h * 0.5, rot);
}



module.exports = {
	drawTile: _drawTile,
	getRegion:_getRegion,
	getConstructorUi: getModularConstructorUI,
	applyUI: applyModularConstructorUI,
}