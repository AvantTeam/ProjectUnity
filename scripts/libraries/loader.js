const Integer = java.lang.Integer;

const toLoad = new Seq();

const loaderBlock = extendContent(Block, "loader-block", {
	/*load(){
		this.region = Core.atlas.white();
	},*/

	init(){
		Core.app.post(run(() => {
			if(!toLoad.isEmpty()) toLoad.each(i => {
				if(typeof(i.init) == "function"){
						i.init();
				}else if(i instanceof ContentList){
					i.load();
				};
			});
		}));
	},

	isHidden(){
		return true;
	}
});

this.global.loader = {};


module.exports = {
	addInit(contentList){
		toLoad.add(contentList);
	}
};