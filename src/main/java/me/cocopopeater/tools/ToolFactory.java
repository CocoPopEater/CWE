package me.cocopopeater.tools;

import me.cocopopeater.tools.impls.TreeTool;

public class ToolFactory {

    public static Tool createTool(ToolType type, String extraInfo){
        switch(type){
            case ToolType.TREE -> {
                TreeTool treeTool = new TreeTool();
                TreeType treeType = null;
                try{
                    treeType = TreeType.valueOf(extraInfo.toUpperCase());
                } catch (IllegalArgumentException e){

                }
                if(treeType == null) treeType = TreeType.RANDOM;
                treeTool.setTreeType(treeType);
                return treeTool;
            }

            // can add more cases when shiny new tools are decided
            default -> {
                return null;
            }
        }
    }
}
