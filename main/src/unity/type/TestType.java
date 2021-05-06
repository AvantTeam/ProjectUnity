package unity.type;

import mindustry.ctype.*;
import unity.content.*;

public class TestType extends UnlockableContent{
    public TestType(String name){
        super(name);
    }

    @Override
    public ContentType getContentType(){
        return UnityContentTypes.test;
    }
}
