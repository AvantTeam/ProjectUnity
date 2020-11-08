package unity.world.blocks;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;

import static arc.Core.*;

public class Teleporter extends Block{
    protected static final Color[] selection = new Color[]{Color.royal, Color.orange, Color.scarlet, Color.forest, Color.purple, Color.gold, Color.pink, Color.black};
    protected static final ObjectSet<TeleporterBuild>[][] teleporters;
    protected float powerUse = 2.5f;
    protected TextureRegion blankRegion, topRegion;

    static{
        teleporters = new ObjectSet[Team.baseTeams.length][selection.length];
        for(int i = 0; i < Team.baseTeams.length; i++){
            if(teleporters[i] == null) teleporters[i] = new ObjectSet[selection.length];
            for(int j = 0; j < selection.length; j++) teleporters[i][j] = new ObjectSet<>();
        }
    }

    public Teleporter(String name){
        super(name);
        update = true;
        solid = true;
        configurable = true;
        saveConfig = true;
        unloadable = false;
        hasItems = true;
        Events.on(WorldLoadEvent.class, e -> {
            for(int i = 0; i < teleporters.length; i++){
                for(int j = 0; j < teleporters[i].length; j++) teleporters[i][j].clear();
            }
        });
        config(Integer.class, (TeleporterBuild build, Integer value) -> {
            if(build.toggle != -1) teleporters[build.team.id][build.toggle].remove(build);
            if(value != -1) teleporters[build.team.id][value].add(build);
            build.toggle = value;
        });
        configClear((TeleporterBuild build) -> build.toggle = -1);
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    @Override
    public void init(){
        consumes.powerCond(powerUse, TeleporterBuild::isConsuming);
        super.init();
    }

    @Override
    public void load(){
        super.load();
        blankRegion = atlas.find(name + "-blank");
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list){
        drawRequestConfigCenter(req, req.config, "nothing");
    }

    @Override
    public void drawRequestConfigCenter(BuildPlan req, Object content, String region){
        if(!(content instanceof Integer)) return;
        int temp = (int) content;
        Draw.color(selection[temp]);
        Draw.rect(blankRegion, req.drawx(), req.drawy());
    }

    public class TeleporterBuild extends Building{
        protected int toggle = -1, entry;
        protected float duration;
        protected TeleporterBuild target;
        protected Team previousTeam;

        protected void onDuration(){
            if(duration < 0f) duration = 0f;
            else duration -= Time.delta;
        }

        protected boolean isConsuming(){
            return duration > 0f;
        }

        protected boolean isTeamChanged(){
            return previousTeam != team;
        }

        @Override
        public void draw(){
            super.draw();
            if(toggle != -1){
                Draw.color(selection[toggle]);
                Draw.rect(blankRegion, x, y);
            }
            Draw.color(Color.white);
            Draw.alpha(0.45f + Mathf.absin(Time.time(), 7f, 0.26f));
            Draw.rect(topRegion, x, y);
            Draw.reset();
        }

        @Override
        public void updateTile(){
            onDuration();
            if(items.any()) dump();
            if(isTeamChanged() && toggle != -1){
                teleporters[team.id][toggle].add(this);
                teleporters[previousTeam.id][toggle].remove(this);
                previousTeam = team;
            }
        }

        @Override
        public void buildConfiguration(Table table){
            final ButtonGroup<Button> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            for(int i = 0; i < selection.length; i++){
                int j = i;
                ImageButton button = table.button(Tex.whiteui, Styles.clearToggleTransi, 24f, () -> {}).size(34f).group(group).get();
                button.changed(() -> configure(button.isChecked() ? j : -1));
                button.getStyle().imageUpColor = selection[j];
                button.update(() -> button.setChecked(toggle == j));
                if(i % 4 == 3) table.row();
            }
        }

        protected TeleporterBuild findLink(int value){
            ObjectSet<TeleporterBuild> teles = teleporters[team.id][value];
            Seq<TeleporterBuild> entries = teles.asArray();
            if(entry >= entries.size) entry = 0;
            if(entry == entries.size - 1){
                TeleporterBuild other = teles.get(entries.get(entry));
                if(other == this) entry = 0;
            }
            for(int i = entry, len = entries.size; i < len; i++){
                TeleporterBuild other = teles.get(entries.get(i));
                if(other != this){
                    entry = i + 1;
                    return other;
                }
            }
            return null;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(toggle == -1) return false;
            target = findLink(toggle);
            if(target == null) return false;
            return source != this && cons.valid() && Mathf.zero(1 - efficiency()) && target.items.total() < target.getMaximumAccepted(item);
        }

        @Override
        public void handleItem(Building source, Item item){
            target.items.add(item, 1);
            duration = 0f;
        }

        @Override
        public void created(){
            if(toggle != -1) teleporters[team.id][toggle].add(this);
            previousTeam = team;
        }

        @Override
        public void onRemoved(){
            if(toggle != -1){
                if(isTeamChanged()) teleporters[previousTeam.id][toggle].remove(this);
                else teleporters[team.id][toggle].remove(this);
            }
            //unity.Unity.print(teleporters[team.id]);
        }

        @Override
        public Integer config(){
            return toggle;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(toggle);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            toggle = read.b();
        }
    }
}
