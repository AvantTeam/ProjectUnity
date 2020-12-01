var mapname = "nullspacecore";
var boss = UnitTypes.antumbra;
var bossnow = null;
var playing = false;
var cutnow = false;
var cutstart = 0;
var bosscolor = Color.purple;

const cuttime = 5 * 60;

const disabled = new StatusEffect("disabledboss");
disabled.reloadMultiplier = 0.000001;
disabled.speedMultiplier = 0.001;

print("h");

function addHpBar(){
  Vars.ui.hudGroup.fill(cons(cont => {
    var hpb = cont.add(new Bar(boss.localizedName, bosscolor, () => ((Time.time - cutstart < cuttime) ? (Time.time - cutstart) / cuttime: bossnow.healthf()))).width(Core.graphics.getWidth()*0.6).height(50).top().pad(40).get();
    hpb.blink(Color.white);
    cont.top();
    cont.update(() => {
      if(!playing || bossnow == null) cont.remove();
    });

    Time.run(cuttime, () => {
      hpb.reset(1);
    });
  }));
}

function gameover(win){
  //TODO
  playing = false;
  Vars.logic.gameOver(win ? Team.sharded : Team.crux);
  print("Win: "+win);
}

function cutscene(){
  cutstart = Time.time;
  bossnow.apply(disabled, cuttime);
  Groups.player.each(p => {
    if(p.unit() != null) p.unit().apply(disabled, cuttime);
  });
  cutnow = true;

  Time.run(cuttime, () => {
    print("unpause!");
    cutnow = false;
    Core.app.post(() => {
      Vars.control.input.panning = false;
    });
  });
}

Events.on(EventType.WorldLoadEvent, () => {
  print(Vars.state.map.name());
  Time.run(60, () => {
    if(!(Vars.state.map.name() == mapname)) return;

    Vars.state.rules.canGameOver = false;
    Core.app.post(() => {
      Vars.state.teams.playerCores().each(build => {
        build.kill();
      });

      //spawn in boss
      bossnow = boss.create(Team.crux);
      bossnow.set(60 * 8, 60 * 8);
      if(!Vars.net.client()) bossnow.add();
      if(!Vars.headless){
        addHpBar();
        cutscene();
      }
      playing = true;
    });
  });
});

Events.run(Trigger.update, () => {
  if(!playing || bossnow == null) return;
  //print("playing!");
  if(Vars.state.is(GameState.State.playing)){
    //print("Pcount: "+Groups.player.count(p => !p.dead()));
    if(bossnow.dead) gameover(true);
    else if(Groups.player.count(p => !p.dead()) <= 0) gameover(false);
  }
  else if(Vars.state.is(GameState.State.menu)) playing = false;

  if(playing && cutnow && (bossnow != null)){
    Vars.control.input.panning = true;
    Core.camera.position.set(bossnow);
  }
});

module.exports = {
  getArena(){
    return Vars.maps.byName(mapname);
  },
  setBoss(unit){
    boss = unit;
  },
  setBossColor(clr){
    bosscolor = clr;
  },
  setMap(str){
    mapname = str;
  },
  start(){
    //TODO: save player unit
    //TODO: save the save to return
    if(!Vars.net.client()) Vars.world.loadMap(this.getArenaMap());
  }
}
