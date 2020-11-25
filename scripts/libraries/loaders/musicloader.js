const loader = this.global.unity.loader;

const addMusic = (type, musicArray) => {
    for(var i = 0; i < musicArray.length; i++){
        type.add(musicArray[i]);
    }
};

const musicLoader = extend(ContentList, {
    //Awaiting custom music player
    /*addMusic(Vars.control.music.darkMusic, [
        loadMusic("youngcha")
    ]);*/
});

loader.addInit(musicLoader);