package unity.world.blocks.distribution;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
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
                    map.reset();
                    map.updateAll();
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
                            p.left().top();

                            for (int i = 0; i < UnderworldBlocks.blocks.size; i++) {
                                UnderworldBlock bloc = UnderworldBlocks.blocks.get(i);

                                p.button(b -> {
                                    b.image(bloc.region).width(32).height(32);
                                }, () -> {

                                }).width(35).height(35).pad(2f).style(Styles.clearTransi).tooltip(bloc.localizedName);

                                if ((i + 1) % 4 == 0) p.row();
                            }
                        }).grow();
                    }).height(215f).growX();
                }).growY().width(205f);
            }).grow();

            piping.show();
        }
    }
}
