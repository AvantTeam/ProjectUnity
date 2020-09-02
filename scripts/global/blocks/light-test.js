const test = extendContent(Block, "light-reflector", {
  calcReflection(dir){
    return (dir+2)%8; //temp
  }
});
test.lightReflector = true;
test.solid = true;
