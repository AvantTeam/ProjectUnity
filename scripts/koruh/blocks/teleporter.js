const selection = [Color.royal, Color.orange, Color.scarlet, Color.forest, Color.purple, Color.gold, Color.pink, Color.black];
const teleporters = [];
Events.on(WorldLoadEvent, e => {
    //print("claerTeleporters");
    for(var i in Team.baseTeams) {
        var tmp = Team.get(i).toString();
        if(teleporters[tmp] == null) teleporters[tmp] = [];
        for(var j = 0; j < selection.length; j++) {
            if(teleporters[tmp][j] != null) teleporters[tmp][j].clear();
            else teleporters[tmp].push(new ObjectSet());
        }
    }
});
const teleporter = extendContent(Block, "teleporter", {
    lastColor: -1,
    /*playerPlaced(tile) {
        if (this.lastColor != null) tile.configure(this.lastColor);
    },*/
    outputsItems() {
        return true;
    },
    init() {
        this.hasPower = true;
        this.consumes.powerCond(2.5, boolf(entity => entity != null ? entity.isConsuming() : false));
        this.super$init();
    },
    load() {
        this.super$load();
        this.blankRegion = Core.atlas.find(this.name + "-blank");
        this.topRegion = Core.atlas.find(this.name + "-top");
    },
    drawRequestConfig(req, list) {
        this.drawRequestConfigCenter(req, req.config, "blank");
    },
    drawRequestConfigCenter(req, content, region) {
        if(isNaN(content) || selection[content] == null) return;
        Draw.color(selection[content]);
        Draw.rect(this.blankRegion, req.drawx(), req.drawy());
        Draw.color();
    }
});
teleporter.update = true;
teleporter.solid = true;
teleporter.configurable = true;
teleporter.saveConfig = true;
teleporter.unloadable = false;
teleporter.buildType = prov(() => extend(Building, {
    draw() {
        this.super$draw();
        if(this.toggle != -1) {
            Draw.color(selection[this.toggle]);
            Draw.rect(teleporter.blankRegion, this.x, this.y);
        }
        Draw.color(Color.white);
        Draw.alpha(0.45 + Mathf.absin(Time.time(), 7, 0.26));
        Draw.rect(teleporter.topRegion, this.x, this.y);
        Draw.reset();
    },
    updateTile() {
        this.onDuration();
        if(this.items.total() > 0) this.dump();
        if(this.isTeamChanged() && this.toggle != -1) {
            teleporters[this.team.toString()][this.toggle].add(this);
            teleporters[this.previousTeam.toString()][this.toggle].remove(this);
            this.previousTeam = this.team;
        }
    },
    buildConfiguration(table) {
        const cont = new Table();
        const group = new ButtonGroup();
        group.setMinCheckCount(0);
        for(var i = 0; i < selection.length; i++) {
            (function(i, entity) {
                var button = cont.button(Tex.whiteui, Styles.clearToggleTransi, 24, run(() => {
                    teleporter.lastColor = button.isChecked() ? i : -1;
                    entity.configure(button.isChecked() ? i : -1);
                })).size(34, 34).group(group).get();
                button.getStyle().imageUpColor = selection[i];
                button.setChecked(entity != null ? entity.toggle == i ? true : false : false);
            })(i, this)
            if(i % 4 == 3) cont.row();
        }
        table.add(cont);
    },
    configured(player, value) {
        if(isNaN(value) || selection[value] == null) return;
        var team = this.team.toString();
        if(this.toggle != -1) teleporters[team][this.toggle].remove(this);
        if(value != -1) teleporters[team][value].add(this);
        this.toggle = value;
    },
    findLink(value) {
        var valueTeles = teleporters[this.team.toString()][value];
        var entries = valueTeles.asArray();
        if(this.entry >= entries.size) this.resetEntry();
        if(this.entry == entries.size - 1) {
            var other_ = valueTeles.get(entries.get(this.entry))
            if(other_ == this) this.resetEntry();
        }
        for(var i = this.entry; i < entries.size; i++) {
            var other = valueTeles.get(entries.get(i))
            if(other != this) {
                this.entry = i + 1;
                return other;
            }
        }
        return null;
    },
    acceptItem(source, item) {
        if(this.toggle == -1) return false;
        this.target = this.findLink(this.toggle);
        if(this.target == null) return false;
        return source != this && this.cons.valid() && Mathf.zero(1 - this.efficiency()) && this.target.items.total() < this.target.getMaximumAccepted(item);
    },
    handleItem(source, item) {
        this.target.items.add(item, 1);
        this.resetDuration();
    },
    toggle: -1,
    resetEntry() {
        this.entry = 0;
    },
    entry: 0,
    target: null,
    resetDuration() {
        this.duration = 60;
    },
    onDuration() {
        if(this.duration <= 0) this.duration = 0;
        else this.duration -= Time.delta;
    },
    isConsuming() {
        return this.duration > 0;
    },
    duration: 0,
    previousTeam: null,
    isTeamChanged() {
        return this.previousTeam != this.team;
    },
    created() {
        if(this.toggle != -1) teleporters[this.team.toString()][this.toggle].add(this);
        this.previousTeam = this.team;
    },
    onRemoved() {
        //print(teleporters[this.team.toString()]);
        if(this.toggle != -1) {
            if(this.isTeamChanged()) teleporters[this.previousTeam.toString()][this.toggle].remove(this);
            else teleporters[this.team.toString()][this.toggle].remove(this);
        }
    },
    config() {
        return this.toggle;
    },
    write(writes) {
        this.super$write(writes);
        writes.b(this.toggle);
    },
    read(reads, revision) {
        this.super$read(reads, revision);
        this.toggle = reads.b();
    }
}));
