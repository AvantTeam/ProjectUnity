const loader = global.unity.loader;

const ambientMusic = Vars.control.sound.ambientMusic;
const darkMusic = Vars.control.sound.darkMusic;
const bossMusic = Vars.control.sound.bossMusic;

const listener = new Seq();

const addMusic = (category, planet, musics) => {
    if(planet.equals("global")){
        musics.forEach(music => {
            if(!category.contains(music)){
                category.add(music);
            };
        });

        return;
    };

    listener.add(cons(e => {
        if(e.sector.planet == Vars.content.getByName(ContentType.planet, "unity-" + planet)){
            musics.forEach(music => {
                if(!category.contains(music)){
                    category.add(music);
                };
            });
        }else{
            musics.forEach(music => {
                if(category.contains(music)){
                    category.remove(music);
                };
            });
        };
    }));
};

const musicLoader = extend(ContentList, {
    load(){
        addMusic(darkMusic, "megalith", [
            // Dark musics
            loadMusic("monolith-dark1"),
            loadMusic("monolith-dark2")
        ]);

        Events.on(SectorLaunchEvent, e => {
            listener.each(cons => cons.get(e));
        });
    }
});

loader.addInit(musicLoader);
