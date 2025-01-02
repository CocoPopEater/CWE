package me.cocopopeater.tools;

import me.cocopopeater.tools.impls.FillTool;
import me.cocopopeater.tools.impls.ReplacerTool;
import me.cocopopeater.tools.impls.TreeTool;
import me.cocopopeater.tools.impls.WallTool;

public class ToolFactory {

    public static Tool createTool(ToolType type, String extraInfo){
        final boolean defaultInfo = extraInfo == null || extraInfo.isEmpty() || extraInfo.equalsIgnoreCase("default");
        switch(type){
            case ToolType.TREE -> {
                TreeTool treeTool = new TreeTool();
                TreeType treeType = null;
                try{
                    // tries to cast the extra info as a tree type
                    treeType = TreeType.valueOf(extraInfo.toUpperCase());
                } catch (IllegalArgumentException e){

                }
                if(treeType == null) treeType = TreeType.RANDOM; // this means the extra info is either not a valid tree or is set to default, might be null too
                treeTool.setTreeType(treeType);
                return treeTool;
            }
            case ToolType.REPLACER -> {
                ReplacerTool tool = new ReplacerTool();
                if(defaultInfo){
                    // blank/default extra info
                    tool.setBlockData("air"); // will set replacer to air by default
                }else{
                    tool.setBlockData(extraInfo);
                }
                return tool;
            }
            case ToolType.FILL -> {
                FillTool tool = new FillTool();
                if(defaultInfo){
                    // blank/default extra info
                    tool.setBlockData("stone"); // will set to stone by default
                }else{
                    tool.setBlockData(extraInfo);
                }
                return tool;
            }
            case ToolType.WALL -> {
                WallTool tool = new WallTool();
                if(defaultInfo){
                    // blank/default extra info
                    tool.setBlockData("stone"); // will set wall to stone by default
                }else{
                    tool.setBlockData(extraInfo);
                }
                return tool;
            }

            // can add more cases when shiny new tools are decided
            default -> {
                return null;
            }
        }
    }
}
