const plib = this.global.unity.mechpad;

plib.extendBlock("pad-dagger", {}, {});

plib.extendBlock("pad-buffer", {
  craftTime: 300,
  init(){
    this.super$init();
    this.unit = Vars.content.getByName(ContentType.unit, "unity-buffer");
  }
}, {});

plib.extendBlock("pad-nova", {
  craftTime: 120,
  unit: UnitTypes.nova
}, {});

plib.extendBlock("pad-cache", {
  craftTime: 300,
  init(){
    this.super$init();
    this.unit = Vars.content.getByName(ContentType.unit, "unity-cache");
  }
}, {});
