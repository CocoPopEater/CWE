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




        for(int y = this.minY; y <= this.maxY; y++){
            for(int x = this.minX; x <= this.maxX; x++){
                for(int z = this.minZ; z <= this.maxZ; z++){
                    end.set(x,y,z);
                    // first figure out if the next block is identical
                    currentBlockData = BlockUtils.extractBlockData(world, end); // block data for the current world state
                    if(currentBlockData.equals(previousBlockData)){
                        // this means the current and previous blocks are identical and can be grouped together
                        // need to check block count and store processed positions
                        // maybe use some other util class?

                    }


                }
            }



        }
        return commands;
    }
}
