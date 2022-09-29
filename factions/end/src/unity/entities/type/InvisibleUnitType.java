package unity.entities.type;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.assets.list.PUShaders.*;
import unity.gen.entities.*;

public class InvisibleUnitType extends PUUnitType{
    public InvisibilityShader invisibilityShader;
    public Color fadeColor = Color.red.cpy().a(0f);
    boolean drawnWeapons = false;

    static boolean valid;

    public InvisibleUnitType(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        for(Weapon w : weapons){
            if(!w.name.equals("")){
                drawnWeapons = true;
                break;
            }
        }
    }

    @Override
    public void draw(Unit unit){
        valid = unit instanceof Invisiblec i && i.invisProgress() > 0.00001f;
        super.draw(unit);
    }

    @Override
    public void drawBody(Unit unit){
        if(valid && invisibilityShader != null){
            Invisiblec i = unit.as();

            if(Vars.player != null && Vars.player.team() == unit.team){
                Tmp.c1.set(fadeColor).a(0.2f * i.invisProgress());
                Draw.color(Tmp.c1);
                Draw.rect(fullIcon, unit.x, unit.y, unit.rotation - 90f);
            }

            Draw.draw(Draw.z(), () -> {
                invisibilityShader.progress = i.invisProgress();
                invisibilityShader.overrideColor = fadeColor;
                invisibilityShader.begin();
                super.drawBody(unit);
                invisibilityShader.end();
            });
        }else{
            super.drawBody(unit);
        }
    }

    @Override
    public void drawOutline(Unit unit){
        if(valid && invisibilityShader != null){
            Invisiblec i = unit.as();
            Draw.draw(Draw.z(), () -> {
                invisibilityShader.progress = i.invisProgress();
                invisibilityShader.overrideColor = fadeColor;
                invisibilityShader.begin();
                super.drawOutline(unit);
                invisibilityShader.end();
            });
        }else{
            super.drawOutline(unit);
        }
    }

    @Override
    public void drawWeaponOutlines(Unit unit){
        if(valid && drawnWeapons && invisibilityShader != null){
            Invisiblec i = unit.as();
            Draw.draw(Draw.z(), () -> {
                invisibilityShader.progress = i.invisProgress();
                invisibilityShader.overrideColor = fadeColor;
                invisibilityShader.begin();
                super.drawWeaponOutlines(unit);
                invisibilityShader.end();
            });
        }else{
            super.drawWeaponOutlines(unit);
        }
    }

    @Override
    public void drawWeapons(Unit unit){
        if(valid && drawnWeapons && invisibilityShader != null){
            Invisiblec i = unit.as();
            Draw.draw(Draw.z(), () -> {
                invisibilityShader.progress = i.invisProgress();
                invisibilityShader.overrideColor = fadeColor;
                invisibilityShader.begin();
                super.drawWeapons(unit);
                invisibilityShader.end();
            });
        }else{
            super.drawWeapons(unit);
        }
    }

    @Override
    public void drawCell(Unit unit){
        if(valid && invisibilityShader != null){
            Invisiblec i = unit.as();
            Draw.draw(Draw.z(), () -> {
                invisibilityShader.progress = i.invisProgress();
                invisibilityShader.overrideColor = fadeColor;
                invisibilityShader.begin();
                super.drawCell(unit);
                invisibilityShader.end();
            });
        }else{
            super.drawCell(unit);
        }
    }

    @Override
    public void drawMech(Mechc mech){
        if(valid && invisibilityShader != null){
            Invisiblec i = mech.as();
            Draw.draw(Draw.z(), () -> {
                invisibilityShader.progress = i.invisProgress();
                invisibilityShader.overrideColor = fadeColor;
                invisibilityShader.begin();
                super.drawMech(mech);
                invisibilityShader.end();
            });
        }else{
            super.drawMech(mech);
        }
    }

    @Override
    public void applyColor(Unit unit){
        if(valid && invisibilityShader == null){
            Invisiblec i = unit.as();
            Draw.color(Color.white, fadeColor, i.invisProgress());
            if(healFlash){
                Tmp.c1.set(Color.white).lerp(healColor, Mathf.clamp(unit.healTime - unit.hitTime));
            }
            Draw.mixcol(Tmp.c1, Math.max(unit.hitTime, !healFlash ? 0f : Mathf.clamp(unit.healTime)));

            if(unit.drownTime > 0 && unit.lastDrownFloor != null){
                Draw.mixcol(Tmp.c1.set(unit.lastDrownFloor.mapColor).mul(0.83f), unit.drownTime * 0.9f);
            }
        }else{
            super.applyColor(unit);
        }
    }

    @Override
    public void applyOutlineColor(Unit unit){
        super.applyOutlineColor(unit);
        if(invisibilityShader == null && valid){
            Invisiblec i = unit.as();
            Tmp.c1.set(Draw.getColor()).lerp(fadeColor, i.invisProgress());
            Draw.color(Tmp.c1);
        }
    }
}
