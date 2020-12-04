var floors = [];
var items = [];
var ores = [];

//internal, do not use!
var floorArr = [];
var oreArr = [];
var itemArr = [];
var floorArrFx = [];
var oreArrFx = [];
var itemArrFx = [];
var itemChance = [];

var v1 = new Vec2(0, 0);
var tile = null;
var id = 0;

const thresh = (Vars.mobile) ? 2 : 3;
//TODO: nake it a setting

if(!Vars.headless){
  Events.run(Trigger.update, () => {
    if(Vars.state.is(GameState.State.playing)){
      for(var i=0; i < thresh; i++){
        v1 = Core.camera.unproject(Mathf.random() * Core.graphics.getWidth(), Mathf.random() * Core.graphics.getHeight());
        tile = Vars.world.tileWorld(v1.x, v1.y);

        if(tile == null || tile.block() != Blocks.air) continue;

        if(floorArr.length > 0){
          id = floorArr.indexOf(tile.floor().id);
          if(id >= 0) floorArrFx[id].at(v1.x, v1.y);
        }
        if(oreArr.length > 0){
          id = oreArr.indexOf(tile.overlay().id);
          if(id >= 0) oreArrFx[id].at(v1.x, v1.y);
        }
      }

      //print(tile);
      if(tile != null && (tile.block() instanceof Conveyor) && tile.build.items.total() > 0){
        id = itemArr.indexOf(tile.build.items.first().id);
        //print("ID: "+id);
        if(id >= 0 && (itemChance[id] == 1 || Mathf.chance(itemChance[id]))) itemArrFx[id].at(v1.x, v1.y);
      }
    }
  });

  Events.on(EventType.ClientLoadEvent, () => {
    for(var i=0; i<floors.length; i+=2){
      floorArrFx.push(floors[1 + i]);
      floorArr.push(Vars.content.getByName(ContentType.block, floors[i]).id);
    }
    for(var i=0; i<ores.length; i+=2){
      oreArrFx.push(ores[1 + i]);
      oreArr.push(Vars.content.getByName(ContentType.block, ores[i]).id);
    }
    for(var i=0; i<items.length; i+=3){
      itemArrFx.push(items[1 + i]);
      itemChance.push(items[2 + i]);
      itemArr.push(Vars.content.getByName(ContentType.item, items[i]).id);
    }
    //print("ItemArr: "+itemArr);
  });
}

//region end

const diriumColor = Color.valueOf("96f7c3");
const lightRedColor = Color.valueOf("f25555"); //bloom

items.push("unity-dirium", new Effect(20, e => {
  Draw.color(diriumColor);
  Angles.randLenVectors(e.id, e.id % 3 + 2, e.fin() * 6, (x, y) => {
    Fill.square(e.x + x, e.y + y, e.fout() * 1.5);
  });
}), 0.3);

items.push("unity-termina-alloy", new Effect(35, e => {
  Draw.color(Color.white, Color.red, e.fin());
  for(var i=0; i<4; i++){
    Drawf.tri(e.x, e.y, e.fout() * 1.35, 3 * e.fout(), 90 * i + e.finpow() * (0.5-Mathf.randomSeed(e.id)) * 150);
  }
}), 0.4);

items.push("unity-termination-fragment", new Effect(16, e => {
  Draw.color(Color.white, Color.red, e.fin());
  for(var i=0; i<2; i++){
    Drawf.tri(e.x, e.y, e.fout() * 1.5, 5 * (Mathf.random() + 0.3) * e.fout(), 360 * Mathf.randomSeed(e.id) + i * 180);
  }
}), 1);//"unstable" items get more particles

items.push("unity-terminum", new Effect(20, e => {
  Draw.color(lightRedColor);
  Lines.stroke(e.fout());
  Angles.randLenVectors(e.id, e.id % 3 + 1, e.fin() * 4, (x, y) => {
    Lines.square(e.x + x, e.y + y, e.fin(), 45);
  });
}), 0.4);
