package me.cocopopeater.regions;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.util.BlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class AdvancedCuboidRegion extends CuboidRegion{
    protected HashMap<SimpleBlockPos, String> blocks; // the String should be the blockstate as string
    public AdvancedCuboidRegion(BlockPos start, BlockPos end, World world) {
        super(start, end);
        blocks = new HashMap<>();
        loadRegionBlocks(world);
    }

    public AdvancedCuboidRegion(SimpleBlockPos pos1, SimpleBlockPos pos2) {
        super(
                new BlockPos(pos1.x(), pos1.y(), pos1.z()),
                new BlockPos(pos2.x(), pos2.y(), pos2.z())
                );
    }

    public Map<SimpleBlockPos, String> getBlockMap(){
        return this.blocks;
    }
    protected void loadRegionBlocks(World world){
        BlockPos.Mutable currentPos = new BlockPos.Mutable();

        for(int x = this.minX; x <= this.maxX; x++){
            for(int y = this.minY; y <= this.maxY; y++){
                for(int z = this.minZ; z <= this.maxZ; z++){
                    currentPos.set(x,y,z);
                    blocks.put(
                            SimpleBlockPos.fromBlockPos(currentPos.toImmutable()),
                            BlockUtils.extractBlockDataFromState(world.getBlockState(currentPos).toString())
                    );
                }
            }
        }
    }
}
