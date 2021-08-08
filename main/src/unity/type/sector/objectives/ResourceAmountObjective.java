package unity.type.sector.objectives;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.type.sector.*;

import static mindustry.Vars.*;

/**
 * An objective that will complete once a team has gathered resources as described in {@link #items}.
 * @author GlennFolker
 */
public class ResourceAmountObjective extends SectorObjective{
    protected Table container;
    public final ItemStack[] items;

    public final Team team;
    public final Color from;
    public final Color to;

    public ResourceAmountObjective(ItemStack[] items, Team team, ScriptedSector sector, String name, Cons<ResourceAmountObjective> executor){
        this(items, team, Color.lightGray, Color.green, sector, name, executor);
    }

    public ResourceAmountObjective(ItemStack[] items, Team team, Color from, Color to, ScriptedSector sector, String name, Cons<ResourceAmountObjective> executor){
        super(sector, name, 1, executor);
        this.items = items;
        this.team = team;
        this.from = from;
        this.to = to;
    }

    @Override
    public void init(){
        super.init();

        if(!headless){
            ui.hudGroup.fill(table -> {
                table.name = name;

                table.actions(
                    Actions.scaleTo(0f, 1f),
                    Actions.visible(true),
                    Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out)
                );

                table.center().left();

                var cell = table.table(Styles.black6, t -> {
                    container = t;

                    ScrollPane pane = t.pane(Styles.defaultPane, cont -> {
                        cont.defaults().pad(4f);

                        for(int i = 0; i < items.length; i++){
                            if(i > 0) cont.row();

                            ItemStack item = items[i];
                            cont.table(Styles.black3, hold -> {
                                hold.defaults().pad(4f);

                                hold.left();
                                hold.image(() -> item.item.uiIcon)
                                    .size(iconMed);

                                hold.right();
                                hold.labelWrap(() -> {
                                    int amount = Math.min(state.teams.playerCores().sum(b -> b.items.get(item.item)), item.amount);
                                    return
                                        "[#" + Tmp.c1.set(from).lerp(to, (float)amount / (float)item.amount).toString() + "]" + amount +
                                            " []/ [accent]" + item.amount + "[]";
                                })
                                    .grow()
                                    .get()
                                    .setAlignment(Align.right);
                            })
                                .height(40f)
                                .growX()
                                .left();
                        }
                    })
                        .update(p -> {
                            if(p.hasScroll()){
                                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                                if(result == null || !result.isDescendantOf(p)){
                                    Core.scene.setScrollFocus(null);
                                }
                            }
                        })
                        .grow()
                        .pad(4f, 0f, 4f, 4f)
                        .get();

                    pane.setScrollingDisabled(true, false);
                    pane.setOverscroll(false, false);
                })
                    .minSize(300f, 48f)
                    .maxSize(300f, 156f);

                cell.visible(() -> ui.hudfrag.shown && sector.valid() && container == cell.get());
            });
        }
    }

    @Override
    public boolean completed(){
        if(state.teams.cores(team).isEmpty()) return false;

        for(ItemStack item : items){
            if(state.teams.cores(team).sum(b -> b.items.get(item.item)) < item.amount){
                return false;
            }
        }

        return true;
    }

    @Override
    public void doFinalize(){
        super.doFinalize();
        if(container != null){
            container.actions(
                Actions.moveBy(-container.getWidth(), 0f, 2f, Interp.pow3In),
                Actions.visible(false),
                new Action(){
                    @Override
                    public boolean act(float delta){
                        container.parent.removeChild(container);
                        container = null;

                        return true;
                    }
                }
            );
        }
    }
}
