package me.cocopopeater.regions;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.util.MathHelper;
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

        BlockPos.Mutable start = new BlockPos.Mutable();
        BlockPos.Mutable end = new BlockPos.Mutable();

        start.set(this.minX, this.minY, this.minZ);
        end.set(this.minX, this.minY, this.minZ);

        for(int x = this.minX; x <= this.maxX; x++){
            // first figure out if the next block is identical
            // second figure out if it should increase in size
            end.setX(x);
            if(!(MathHelper.isOverBlockLimit(start, end))){
                end.setX(x-1);
                x--;

            }

        }
        return commands;
    }
}
