package me.cocopopeater.tools;


import java.util.ArrayList;
import java.util.List;

public enum ToolType {
    NONE,
    TREE,
    REPLACER,
    FILL,
    WALL;


    public static List<String> getTools(){
        List<String> items = new ArrayList<>();

        for(ToolType tool : ToolType.values()){
            if(tool == NONE) continue;
            items.add(tool.toString().toLowerCase());
        }
        return items;
    }
}
