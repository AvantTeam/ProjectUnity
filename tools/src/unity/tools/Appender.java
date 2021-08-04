package unity.tools;

import arc.files.*;
import arc.util.Log.*;
import unity.*;

public class Appender{
    public static Fi assetsDir;

    public static void main(String[] args){
        if(args.length <= 0) return;

        assetsDir = Fi.get(args[0]);
        if(!assetsDir.exists() || !assetsDir.isDirectory()){
            Unity.print(LogLevel.err, "", "Assets directory not found.");
            return;
        }
    }
}
