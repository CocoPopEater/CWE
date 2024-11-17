package me.cocopopeater.regions;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ClipboardRegion extends AdvancedCuboidRegion{
    protected BlockPos startPos;

    public ClipboardRegion(BlockPos start, BlockPos end, BlockPos userPos, World world) {
        super(start, end, world);
        this.startPos = userPos;
        updateOffsets();
    }

    protected void updateOffsets(){
        HashMap<SimpleBlockPos, String> newBlocks = new HashMap<>();
        for(SimpleBlockPos pos : this.blocks.keySet()){
            String state = this.blocks.get(pos);

            int offsetX = pos.getX() - this.startPos.getX();
            int offsetY = pos.getY() - this.startPos.getY();
            int offsetZ = pos.getZ() - this.startPos.getZ();

            BlockPos newPos = new BlockPos(offsetX, offsetY, offsetZ);
            newBlocks.put(SimpleBlockPos.fromBlockPos(newPos), state);
        }
        this.blocks = newBlocks;
    }

    public List<String> getPasteCommandList(){
        final String baseSetBlockCommand = "setblock %d %d %d %s"; // setblock x y z blockName
        final String baseFillCommand = "fill %d %d %d %d %d %d %s"; // fill x1 y1 z1 x2 y2 z2 blockName

        ClientPlayerEntity player = MinecraftClient.getInstance().player; // get players current position
        World playerWorld = player.getWorld();

        ArrayList<String> commands = new ArrayList<>();

        List<Map.Entry<SimpleBlockPos, String>> orderedCoords = this.blocks.entrySet()
                .stream()
                .sorted(Comparator.comparing(es -> es.getKey().getZ()))
                .sorted(Comparator.comparing(es -> es.getKey().getX()))
                .sorted(Comparator.comparing(es -> es.getKey().getY()))
                .toList();

        for(Map.Entry<SimpleBlockPos, String> entry : orderedCoords){
            SimpleBlockPos pos = entry.getKey();
            String state = this.blocks.get(pos); // this retrieves the saved blockstate, pos being the offset from player
            BlockPos newPos = player.getBlockPos().add(pos.getX(), pos.getY(), pos.getZ());

            if(playerWorld.getBlockState(newPos).equals(state)) continue; // If the block is already correct, ignore it

            String block = BlockUtils.extractBlockDataFromState(state);
            String command = String.format(baseSetBlockCommand, newPos.getX(), newPos.getY(), newPos.getZ(), block);
            commands.add(command);
        }

        return commands;
    }
}
