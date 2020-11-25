const ambientMusic = Vars.control.sound.ambientMusic;
const darkMusic = Vars.control.sound.darkMusic;
const bossMusic = Vars.control.sound.bossMusic;

const monolithDark = loadMusic("monolith-dark");
Events.on(SectorLaunchEvent, e => {
    if(
        e.sector.planet == Vars.content.getByName("unity-megalith")
    ){
        if(darkMusic.contains(monolithDark)){
            darkMusic.add(monolithDark);
        };
    }else if(darkMusic.contains(monolithDark)){
        darkMusic.remove(monolithDark);
    };
});

module.exports = {
    monolithDark: monolithDark
};
