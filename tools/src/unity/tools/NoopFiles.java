package unity.tools;

import arc.*;
import arc.files.*;

import static mindustry.Vars.*;

/**
 * A no-operation implementation of {@link Files} just so that {@link Core#files} isn't {@code null}.
 * @author GlennFolker
 */
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
