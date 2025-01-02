package me.cocopopeater.tools.impls;

import me.cocopopeater.blocks.BlockZone;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.tools.Tool;
import me.cocopopeater.tools.ToolType;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class WallTool extends Tool {
    private String blockData;
    private SimpleBlockPos selectedPoint;

    public WallTool(){
        super(ToolType.WALL);
    }

    public void setBlockData(String data){
        this.blockData = data;
        this.selectedPoint = null;
    }

    @Override
    public void applyEffect(BlockHitResult hitResult) {
        // if selected tree is random, get random tree
        // otherwise create string representation of currently selected tree type
        if(this.selectedPoint == null){
            // selecting first point
            this.selectedPoint = SimpleBlockPos.fromBlockPos(hitResult.getBlockPos());
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal(this.selectedPoint.toString())
            );
            PlayerUtils.sendPlayerMessageBoth(
                    Text.translatable("region.selection.position1", this.selectedPoint.toShortString()).withColor(GlobalColorRegistry.getLimeGreen())
            );
            return;
        }
        // if we get here, selectedPos is not null, meaning the user selected their second position

        BlockZone zone = new BlockZone(selectedPoint, SimpleBlockPos.fromBlockPos(hitResult.getBlockPos()), this.blockData);
        
        this.resetTool(false);

        List<String> commands = new ArrayList<>();
        for(BlockZone wallZone : zone.getWallBlockZones()){
            commands.addAll(wallZone.generateFillCommandsRaw());
        }

        PlayerUtils.sendCommandList(commands);
    }

    @Override
    public void resetTool(boolean messagePlayer) {
        this.selectedPoint = null;
        if(messagePlayer){
            PlayerUtils.sendPlayerMessageBoth(
                    Text.translatable("tools.tool_reset").withColor(GlobalColorRegistry.getLimeGreen())
            );
        }

    }


}
