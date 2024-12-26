package me.cocopopeater.tools;

import net.minecraft.util.hit.BlockHitResult;

public abstract class Tool {
    private ToolType toolType;

    public Tool(ToolType type){
        this.toolType = type;
    }

    public ToolType getToolType(){
        return this.toolType;
    }

    public abstract void applyEffect(BlockHitResult hitResult);
}
