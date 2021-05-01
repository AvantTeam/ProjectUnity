package y.world.blocks.effect;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import unity.util.BlockMovement;

public class UnityThruster extends Block{
    public final int timerUse = timers++;
    public int maxBlocks=10;
    public float maxSpeed = 1;
    public float acceleration = 1;
    public float engineSize = 8;

    public float itemDuration = 150;

    public UnityThruster(String name){
        super(name);
        update=true;
        hasItems=true;
        sync=true;

    }

    public class UnityThrusterBuild extends Building{
        public float speed=0;

        @Override public void updateTile(){
            super.updateTile();
            if(consValid()){
                if(timer(timerUse, itemDuration / timeScale())){
                    consume();
                }
                speed+=acceleration*edelta();
                speed=Mathf.clamp(speed,0,maxSpeed);
            }else{
                speed*=0.9;
            }
            if(speed>0.05){
                BlockMovement.pushBlock(this,rotation,maxBlocks,speed,building -> true);
            }
        }

        @Override public void draw(){
            super.draw();
            float scale = speed/maxSpeed;
            Draw.color(team.color);
            float blockRotation = rotation*90;
            Fill.circle(
                x + Angles.trnsx(blockRotation + 180, offset),
                y + Angles.trnsy(blockRotation + 180, offset),
                (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) * scale
            );
            Draw.color(Color.white);
            Fill.circle(
                x + Angles.trnsx(blockRotation + 180, offset - 1f),
                y + Angles.trnsy(blockRotation + 180, offset - 1f),
                (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) / 2f  * scale
            );
            Draw.color();
            
        }
    }
}
