package unity.tools;

import arc.files.*;
import arc.struct.*;
import arc.util.*;
import unity.*;

public class Tools{
    public static void main(String[] args){
        Unity.print("Hello world!");
    }

    public static void command(Seq<String> list, String... commands){
        list.clear().addAll(commands);
        if(OS.isWindows){
            list.insert(0, "cmd");
            list.insert(1, "/c");
        }
    }

    public static void clear(Fi file){
        file.mkdirs();
        for(var child : file.list()){
            child.delete();
        }
    }
}
