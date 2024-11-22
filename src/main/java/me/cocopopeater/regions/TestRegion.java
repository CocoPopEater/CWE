package me.cocopopeater.regions;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.util.BlockUtils;
import me.cocopopeater.util.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TestRegion extends AdvancedCuboidRegion {
    protected SimpleBlockPos startPos;


    public TestRegion(BlockPos start, BlockPos end, World world, SimpleBlockPos userPos) {
        super(start, end, world);
        this.startPos = userPos;
        updateOffsets();
    }

    protected void updateOffsets(){
        HashMap<SimpleBlockPos, String> newBlocks = new HashMap<>();
        for(SimpleBlockPos pos : this.blocks.keySet()){
            String state = this.blocks.get(pos);

            int offsetX = pos.x() - this.startPos.x();
            int offsetY = pos.y() - this.startPos.y();
            int offsetZ = pos.z() - this.startPos.z();

            BlockPos newPos = new BlockPos(offsetX, offsetY, offsetZ);
            newBlocks.put(SimpleBlockPos.fromBlockPos(newPos), state);
        }
        this.blocks = newBlocks;
    }

    public List<String> generateFillCommands(){
        // testing method for greedy meshing-esque algo
        ArrayList<String> commands = new ArrayList<>();

        World world = MinecraftClient.getInstance().player.getWorld();

        BlockPos.Mutable start = new BlockPos.Mutable();
        BlockPos.Mutable end = new BlockPos.Mutable();

        String currentBlockData = "";
        String nextBlockData = "";

        start.set(this.minX, this.minY, this.minZ);
        end.set(this.minX, this.minY, this.minZ);



        boolean completed = false;
        boolean firstPass = true;

        int currentX = this.minX;
        int currentY = this.minY;
        int currentZ = this.minZ;

        while(!completed){
            currentBlockData = BlockUtils.extractBlockData(world, end);
            nextBlockData = BlockUtils.extractBlockData(world, end.add(0,0,1));
            if(currentBlockData.equals(nextBlockData)){
                //we can group them together
                
            }
        }
        return commands;
    }
}
