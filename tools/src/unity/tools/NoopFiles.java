package unity.tools;

import arc.*;
import arc.files.*;

import static mindustry.Vars.*;

public class NoopFiles implements Files{
    @Override
    public Fi get(String path, FileType type){
        return tree.get(path);
    }

    @Override
    public String getExternalStoragePath(){
        return null;
    }

    @Override
    public boolean isExternalStorageAvailable(){
        return false;
    }

    @Override
    public String getLocalStoragePath(){
        return null;
    }

    @Override
    public boolean isLocalStorageAvailable(){
        return false;
    }
}
