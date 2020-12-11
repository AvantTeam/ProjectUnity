package unity.logic;

import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import mindustry.ui.*;
import unity.graphics.*;

public class ExpSensorStatement extends LStatement{
    public String res = "result", type = "block1";
    public ExpContentList cont = ExpContentList.totalExp;

    @Override
    public void build(Table table){
        table.clearChildren();
        table.table(t -> {
            t.left();
            t.setColor(table.color);
            field(t, res, text -> res = text);
            t.add(" = ");
        });
        row(table);
        table.table(t -> {
            t.left();
            t.setColor(table.color);
            TextField tfield = field(t, cont.name(), text -> {
                try{
                    cont = ExpContentList.valueOf(text);
                }catch(Exception e){}
            }).padRight(0f).get();
            Button b = new Button(Styles.logict);
            b.image(Icon.pencilSmall);
            b.clicked(() -> showSelect(b, ExpContentList.all, cont, t2 -> {
                tfield.setText(t2.name());
                cont = t2;
                build(table);
            }, 1, cell -> cell.size(240f, 40f)));
            t.add(b).color(table.color).size(40f).padLeft(-1f);
            t.add(" in ");
            field(t, type, text -> type = text);
        }).left();
    }

    //fieldlist


    @Override
    public void write(StringBuilder builder){
        builder.append("expsensor " + res);
        builder.append(" ");
        builder.append(cont.name());
        builder.append(" ");
        builder.append(type);
    }

    @Override
    public Color color(){
        return UnityPal.expColor;
    }

    @Override
    public LInstruction build(LAssembler builder){
        return new ExpSenseI(builder.var(res), cont, builder.var(type));
    }
}
