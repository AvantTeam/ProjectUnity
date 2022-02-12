package unity.world.blocks.distribution;

import arc.Events;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import unity.content.*;
import unity.type.UnderworldBlock;
import unity.ui.UnderworldMap;

import static arc.Core.*;

@SuppressWarnings("unused")
public class UnderPiper extends Block {
    private TextureRegion pencil, eraser, move;

    public UnderPiper(String name, int itemCapacity){
        super(name);

        configurable = true;
        hasItems = true;
        acceptsItems = true;
        solid = true;
        update = true;
        this.itemCapacity = itemCapacity;
    }

    @Override
    public void load() {
        super.load();
        pencil = atlas.find("unity-pencil");
        eraser = atlas.find("unity-eraser");
        move = atlas.find("unity-move");

        Events.on(EventType.WorldLoadEvent.class, e -> {
            // TODO add electrode planet check
            UnderworldMap.reset();
            UnderworldMap.updateAll();
        });

        Time.runTask(30, UnderworldBlocks::load);
    }

    public class UnderPiperBuild extends Building {
        public BaseDialog piping;

        @Override
        public boolean shouldHideConfigure(Player player) {
            return true;
        }

        @Override
        public void drawConfigure() {}

        @Override
        public void buildConfiguration(Table table) {
            if(piping == null){
                piping = new BaseDialog(block.localizedName);
                piping.addCloseListener();
            }

            piping.cont.clear();
            piping.cont.center().top();

            piping.cont.table(t -> {
                t.center().bottom();

                ScrollPane pane = t.pane(Styles.nonePane, p -> {
                    UnderworldMap map = new UnderworldMap();
                    p.add(map).grow();
                }).fill().padRight(5f).get();

                pane.setScrollX((tile.x + 2) * 32f - 16f);
                pane.setScrollY((tile.y + 2) * 32f - 16f);

                t.table(tl -> {
                    tl.center().top();

                    tl.table(Tex.pane, tt -> {
                        tt.center().top();
                        tt.labelWrap(bundle.get("block.unity-underpiper.info")).labelAlign(Align.center).marginBottom(5f).growX();
                        tt.row();
                        tt.image().color(Pal.accent).height(4f).growX().marginLeft(5).marginRight(5).growX();
                        tt.row();
                        tt.labelWrap("Planet: ???").growX().labelAlign(Align.center);
                    }).padBottom(5f).grow();

                    tl.row();

                    tl.table(Tex.pane, tt -> {
                        tt.center().top();
                        tt.labelWrap(bundle.get("block.unity-underpiper.blocks")).labelAlign(Align.center).marginBottom(5f).growX();
                        tt.row();
                        tt.image().color(Pal.accent).height(4f).growX().marginLeft(5).marginRight(5).growX();
                        tt.row();

                        tt.pane(p -> {
                            p.center().top();

                            for (int i = 0; i < UnderworldBlocks.blocks.size; i++) {
                                UnderworldBlock bloc = UnderworldBlocks.blocks.get(i);

                                p.button(b -> b.image(bloc.region).size(32), () -> {

                                }).size(34f).pad(2f).style(Styles.clearTransi).tooltip(bloc.localizedName);

                                if ((i + 1) % 4 == 0) p.row();
                            }
                        }).grow();

                        tt.row();
                        tt.image().color(Pal.accent).height(4f).growX().marginLeft(5).marginRight(5).padBottom(2f).growX();
                        tt.row();

                        tt.table(ttt -> {
                            ttt.left();
                            ttt.button(b -> b.image(pencil), Styles.cleari, () -> { }).size(32f).padRight(5f);
                            ttt.button(b -> b.image(eraser), Styles.cleari, () -> { }).size(32f).padRight(5f);
                            ttt.button(b -> b.image(move), Styles.cleari, () -> { }).size(32f);
                        }).growX();
                    }).height(240f).growX();
                }).growY().width(205f);
            }).grow();

            piping.show();
        }
    }
}
