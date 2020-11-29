const ambientMusic = Vars.control.sound.ambientMusic;
const darkMusic = Vars.control.sound.darkMusic;
const bossMusic = Vars.control.sound.bossMusic;

//TODO make this an ObjectMap instead
const monolithDarkMusics = Seq.with(loadMusic("monolith-dark1"));
Events.on(SectorLaunchEvent, e => {
    if(e.sector.planet == Vars.content.getByName(ContentType.planet,"unity-megalith")){
        monolithDarkMusics.each(music => {
            if(!darkMusic.contains(music)){
                darkMusic.add(music);
            };
        });
    }else{
        monolithDarkMusics.each(music => {
            if(darkMusic.contains(music)){
                darkMusic.remove(music);
            };
        });
    }
});

module.exports = {
    monolithDark1: monolithDarkMusics.get(0)
};
