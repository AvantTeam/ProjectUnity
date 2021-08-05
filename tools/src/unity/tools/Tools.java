package unity.tools;

import arc.*;
import arc.assets.*;
import arc.audio.*;
import arc.files.*;
import arc.struct.*;

import java.nio.file.*;

/** Main entry point of the tools module. This must only affect the main project's asset directory. */
public class Tools{
    public static final Fi
    assetsDir,
    spritesDir, spritesGenDir;

    static{
        assetsDir = new Fi(Paths.get("").toFile());
        spritesDir = assetsDir.child("sprites");
        spritesGenDir = spritesDir.child("gen");
    }

    public static void main(String[] args){
        new Application(){
            final Seq<ApplicationListener> listeners = new Seq<>();

            {
                Core.app = this;
                start();
            }

            @Override
            public Seq<ApplicationListener> getListeners(){
                return listeners;
            }

            @Override
            public ApplicationType getType(){
                return ApplicationType.headless;
            }

            @Override
            public String getClipboardText(){
                return null;
            }

            @Override
            public void setClipboardText(String text){}

            @Override
            public void post(Runnable runnable){
                runnable.run();
            }

            @Override
            public void exit(){}
        };
    }

    private static void start(){

    }
}
