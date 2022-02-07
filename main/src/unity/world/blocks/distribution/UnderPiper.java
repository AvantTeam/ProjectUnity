package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.game.Team;
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
        Time.run(30, UnderworldBlocks::load);
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

                t.pane(p -> {
                    p.setWidth(Core.scene.getWidth() - p.parent.getChildren().get(1).getWidth() - 10f);
                    UnderworldMap map = new UnderworldMap();
                    map.reset();
                    map.updateAll();
                    p.add(map).grow();
                }).width(600f).height(600f);

                t.table(tl -> {
                    tl.center().top();

                    tl.table(Tex.pane, tt -> {
                        tt.center().top();
                        tt.labelWrap(bundle.get("block.unity-underpiper.info")).labelAlign(Align.center).marginBottom(5f).growX();
                        tt.row();
                        tt.image().color(Pal.accent).height(4f).growX().marginLeft(5).marginRight(5).growX();
                        tt.row();
                    }).padBottom(5f).grow();

                    tl.row();

                    tl.table(Tex.pane, tt -> {
                        tt.center().top();
                        tt.labelWrap(bundle.get("block.unity-underpiper.blocks")).labelAlign(Align.center).marginBottom(5f).growX();
                        tt.row();
                        tt.image().color(Pal.accent).height(4f).growX().marginLeft(5).marginRight(5).growX();
                        tt.row();

                        tt.pane(p -> {
                            for (int i = 0; i < UnderworldBlocks.blocks.size; i++) {
                                UnderworldBlock bloc = UnderworldBlocks.blocks.get(i);

                                p.button(b -> {
                                    b.image(bloc.region).width(32).height(32);
                                }, () -> {

                                }).width(35).height(35).pad(2f).style(Styles.clearTransi).tooltip(bloc.localizedName);

                                if ((i + 1) % 4 == 0) p.row();
                            }
                        });
                    }).height(215f);
                }).growY();
            }).grow();

            piping.show();
        }
    }
}
