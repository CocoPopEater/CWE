package me.cocopopeater.tools.impls;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.tools.Tool;
import me.cocopopeater.tools.ToolType;
import me.cocopopeater.tools.TreeType;
import me.cocopopeater.util.PlayerUtils;
import net.minecraft.util.hit.BlockHitResult;

public class ReplacerTool extends Tool {
    private String blockData;

    public ReplacerTool(){
        super(ToolType.REPLACER);
    }

    public void setBlockData(String data){
        this.blockData = data;
    }

    @Override
    public void applyEffect(BlockHitResult hitResult) {
        SimpleBlockPos pos = SimpleBlockPos.fromBlockPos(hitResult.getBlockPos());
        String command = "setblock %d %d %d %s".formatted(
                pos.x(),
                pos.y(),
                pos.z(),
                this.blockData
        );
        PlayerUtils.sendCommandAsPlayer(
                command
        );
    }

    @Override
    public void resetTool(boolean messagePlayer) {

    }
}
