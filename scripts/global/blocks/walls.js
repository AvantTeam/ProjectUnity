const mgWall = extendContent(Wall, "metaglass-wall", {
  lightRepeater(){
    return true;
  }
});

mgWall.buildType = () => {
  const ent = extendContent(Wall.WallBuild, mgWall, {
    calcLight(ld, i){
      return [ld[0], ld[1], ld[2] - i + 1, ld[3]];
    }
  });
  return ent;
}

const mgWallLarge = extendContent(Wall, "metaglass-wall-large", {
  lightRepeater(){
    return true;
  }
});

mgWallLarge.buildType = () => {
  const ent = extendContent(Wall.WallBuild, mgWallLarge, {
    calcLight(ld, i){
      return [ld[0], ld[1], ld[2] - i + 1, ld[3]];
    }
  });
  return ent;
}
