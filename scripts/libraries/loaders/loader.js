const toLoad = new Seq();

Events.on(ClientLoadEvent, e => {
    Core.app.post(() => {
		if(!toLoad.isEmpty()) toLoad.each(i => {
			if(typeof(i.init) == "function"){
               	i.init();
			}else if(i instanceof ContentList){
				i.load();
			};
		});
	});
});

module.exports = {
	addInit(list){
		toLoad.add(list);
	}
};
