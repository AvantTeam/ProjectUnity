const contentList = ["@totalExp", "@totalLevel", "@expCapacity", "@maxLevel"];
const expColor = Color.valueOf("84ff00");

const ExpSensorI = {
  _(builder, res, cont, type) {
    this.res = builder.var(res);
    this.cont = builder.var(cont);
    this.type = builder.var(type);
  },

  run(vm) {
    const cont = vm.numi(this.cont);
    var build = vm.obj(this.type);
    if(build == null || (typeof build) != "object" || !(build instanceof Building)){
      vm.setnum(this.res, 0);
      return;
    }

    switch(cont){
      case 0:
      vm.setnum(this.res, (build.totalExp) ? build.totalExp() : 0);
      break;
      case 1:
      vm.setnum(this.res, (build.totalLevel) ? build.totalLevel() : 0);
      break;
      case 2:
      vm.setnum(this.res, (build.block.expCapacityf) ? build.block.expCapacityf() : 0);
      break;
      case 3:
      vm.setnum(this.res, (build.block.maxLevelf) ? build.block.maxLevelf() : 0);
      break;
    }
  }
};

const ExpSensorStatement = {
  new: words => {
    const st = extend(LStatement, Object.create(ExpSensorStatement));
    st.read(words);
    return st;
  },

  read(words) {
    this.res = words[1];
    this.cont = (isNaN(Number(words[2]))) ? contentList.indexOf(words[2]) : words[2];
    this.type = words[3];
  },

  build(h) {
    if (h instanceof Table) {
      return this.buildt(h);
    }

    const inst = extend(LExecutor.LInstruction, Object.create(ExpSensorI));
    inst._(h, this.res, this.cont, this.type);
    return inst;
  },

  buildt(table) {
    //todo dropdown
    table.clearChildren();//this just sounds horrible

    table.table(cons(t => {
      t.left();
      t.setColor(table.color);
      this.field(t, this.res, text => {this.res = text});
      t.add(" = ");
    })).left();

    this.row(table);

    table.table(cons(t => {
      t.left();
      t.setColor(table.color);

      var tfield = this.field(t, contentList[this.cont], text => {
        var tm = contentList.indexOf(text);
        this.cont = tm < 0 ? 0 : tm;
      }).padRight(0).get();

      var b = new Button(Styles.logict);
      b.image(Icon.pencilSmall);
      b.clicked(() => this.showSelect(b, contentList, contentList[this.cont], t2 => {
          tfield.setText(contentList.indexOf(t2));
          this.cont = contentList.indexOf(t2);
          this.buildt(table);
      }, 1, cell => cell.size(240, 40)));
      t.add(b).color(table.color).size(40).padLeft(-1);

      t.add(" in ");
      this.field(t, this.type, text => {this.type = text});
    })).left();
  },

  fieldlist(table, list, def, defname, parent, w){
    var b = new Button(Styles.logict);
    b.label(prov(() => "" + list[Number(def)]));
    b.clicked(() => this.showSelect(b, list, list[Number(def)], t => {
        this[defname] = list.indexOf(t);
        if(parent !== false) this.buildt(parent);
    }, 2, cell => cell.size(100, 50)));
    return table.add(b).color(table.color).left().padLeft(2);
  },

  write(builder) {
    builder.append("expsensor " + this.res + "");
    builder.append(" ");
    builder.append(contentList[this.cont] + "");
    builder.append(" ");
    builder.append(this.type + "");
  },

  name: () => "Exp. Sensor",
  color: () => expColor
};

/* Mimic @RegisterStatement */
LAssembler.customParsers.put("expsensor", func(ExpSensorStatement.new));

LogicIO.allStatements.add(prov(() => ExpSensorStatement.new([
  "expsensor",
  "result",
  "@totalExp",
  "block1"
])));
