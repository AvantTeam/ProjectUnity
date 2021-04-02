package unity.world.blocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import unity.mod.*;

import static mindustry.Vars.*;

public class LoreMessageBlock extends Block{
    public final Faction faction;
    public final Color color;
    public final Color lightColor;

    public TextureRegion topRegion;

    public LoreMessageBlock(String name, Faction faction){
        super(name);

        this.faction = faction;
        color = faction.color;
        lightColor = color.cpy().mul(1.2f);

        size = 1;
        health = Integer.MAX_VALUE; //don't make it so easy to destroy
        configurable = true;
        solid = false;
        destructible = true;
        group = BlockGroup.logic;
        drawDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find("unity-lore-message");
        topRegion = Core.atlas.find("unity-lore-message-top");
    }

    public class LoreMessageBuild extends Building{
        private String message;
        private boolean messageSet;

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            Draw.color(color, lightColor, Mathf.absin(4f, 1f));
            Draw.rect(topRegion, x, y);
            Draw.color();
        }

        public void setMessage(String message){
            if(!messageSet){
                this.message = message;
                messageSet = true;
            }else{
                throw new IllegalArgumentException("Lore message already set!");
            }
        }

        public String getMessage(){
            return message;
        }

        @Override
        public void drawLight(){
            Drawf.light(team, this, 4f * tilesize, color, 0.5f);
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black6, cont -> {
                cont.add("@lore.unity.message", lightColor).align(Align.center);

                cont.row();
                cont.image(Tex.whiteui, color)
                    .growX()
                    .height(3f)
                    .pad(6f);

                cont.row();
                var scrl = cont.pane(Styles.defaultPane, pane -> {
                    pane.setBackground(Tex.scroll);
                    pane.labelWrap(() -> message)
                        .align(Align.topLeft)
                        .grow()
                        .pad(6f);
                })
                    .grow()
                    .pad(6f)
                    .get();
                scrl.setScrollingDisabled(true, false);
                scrl.setOverscroll(false, false);
            }).size(360f, 240f);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(message);
            write.bool(messageSet);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            message = read.str();
            messageSet = read.bool();
        }
    }
}
