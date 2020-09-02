const test = extendContent(Block, "light-reflector", {
  calcReflection(dir){
    return (dir+3)%8; //temp
  },
  lightReflector(){
    return true;
  }
});
