package unity.tools;

import arc.*;
import arc.struct.*;

public class NoopApplication implements Application{
    protected Seq<ApplicationListener> listeners;
    protected String clipboard;

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
        return clipboard;
    }

    @Override
    public void setClipboardText(String text){
        clipboard = text;
    }

    @Override
    public void post(Runnable runnable){
        runnable.run();
    }

    @Override
    public void exit(){}
}
