package me.cocopopeater.tools.impls;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.tools.Tool;
import me.cocopopeater.tools.ToolType;
import me.cocopopeater.tools.TreeType;
import me.cocopopeater.util.PlayerUtils;
import net.minecraft.util.hit.BlockHitResult;

public class TreeTool extends Tool {
    private TreeType treeType;

    public TreeTool(){
        super(ToolType.TREE);
    }

    public void setTreeType(TreeType type){
        this.treeType = type;
    }

    @Override
    public void applyEffect(BlockHitResult hitResult) {
        // if selected tree is random, get random tree
        // otherwise create string representation of currently selected tree type
        String tree = treeType.equals(TreeType.RANDOM) ? TreeType.getRandomTree().toString() : treeType.toString();
        SimpleBlockPos pos = SimpleBlockPos.fromBlockPos(hitResult.getBlockPos());
        String command = "place feature %s %d %d %d".formatted(
                tree,
                pos.x(),
                pos.y() + 1,
                pos.z()
        ).toLowerCase();
        PlayerUtils.sendCommandAsPlayer(
                command
        );
    }
}
