const mgWall = extendContent(Wall, "metaglass-wall", {
  lightRepeater(){
    return true;
  }
});

mgWall.entityType = () => {
  const ent = extendContent(Wall.WallBuild, mgWall, {
    calcLight(ld, i){
      return [ld[0], ld[1], ld[2] - i + 1, ld[3]];
    },
    collision(bullet){
      this.super$collision(bullet);

      var type = bullet.type;
      if(type instanceof LaserBulletType){
        var penX = Math.abs(this.x - bullet.x);
        var penY = Math.abs(this.y - bullet.y);

        Tmp.v1.trns(bullet.rotation(), Mathf.dst(bullet.x, bullet.y, this.x, this.y));

        if(penX > penY){
          Tmp.v1.x *= -1;
        }else{
          Tmp.v1.y *= -1;
        };

        var newObj = Object.assign({}, type, {
          length: type.length - Mathf.dst(bullet.x, bullet.y, this.x, this.y)
        });
        extend(LaserBulletType, newObj).create(this, this.team, this.x, this.y, Mathf.angle(Tmp.v1.x, Tmp.v1.y));
      };
    }
  });
  return ent;
}

const mgWallLarge = extendContent(Wall, "metaglass-wall-large", {
  lightRepeater(){
    return true;
  }
});

mgWallLarge.entityType = () => {
  const ent = extendContent(Wall.WallBuild, mgWallLarge, {
    calcLight(ld, i){
      return [ld[0], ld[1], ld[2] - i + 1, ld[3]];
    }
  });
  return ent;
}
