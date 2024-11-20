package me.cocopopeater.regions;

import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.GlobalVariableManager;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class CuboidRegion {
    protected int minX;
    protected int minY;
    protected int minZ;

    protected int maxX;
    protected int maxY;
    protected int maxZ;

    public BlockPos getMinPos(){
        return new BlockPos(minX, minY, minZ);
    }

    public BlockPos getMaxPos(){
        return new BlockPos(maxX, maxY, maxZ);
    }

    public CuboidRegion(BlockPos start, BlockPos end){
        this.minX = Math.min(start.getX(), end.getX());
        this.minY = Math.min(start.getY(), end.getY());
        this.minZ = Math.min(start.getZ(), end.getZ());

        this.maxX = Math.max(start.getX(), end.getX());
        this.maxY = Math.max(start.getY(), end.getY());
        this.maxZ = Math.max(start.getZ(), end.getZ());
    }



    public ArrayList<CuboidRegion> separateRegion() {
        ArrayList<CuboidRegion> subRegions = new ArrayList<>();

        int totalVolume = getTotalBlocks();
        if (totalVolume <= GlobalVariableManager.MAX_BLOCKS) {
            subRegions.add(this);
            return subRegions;
        }

        int regionX = maxX - minX + 1;
        int regionY = maxY - minY + 1;
        int regionZ = maxZ - minZ + 1;



        int splitX = (int) Math.ceil(regionX / Math.cbrt(GlobalVariableManager.MAX_BLOCKS));
        int splitY = (int) Math.ceil(regionY / Math.cbrt(GlobalVariableManager.MAX_BLOCKS));
        int splitZ = (int) Math.ceil(regionZ / Math.cbrt(GlobalVariableManager.MAX_BLOCKS));

        int stepX = (int) Math.ceil((double) regionX / splitX);
        int stepY = (int) Math.ceil((double) regionY / splitY);
        int stepZ = (int) Math.ceil((double) regionZ / splitZ);

        for (int x = minX; x <= maxX; x += stepX) {
            for (int y = minY; y <= maxY; y += stepY) {
                for (int z = minZ; z <= maxZ; z += stepZ) {
                    int subMaxX = Math.min(x + stepX - 1, maxX);
                    int subMaxY = Math.min(y + stepY - 1, maxY);
                    int subMaxZ = Math.min(z + stepZ - 1, maxZ);

                    subRegions.add(new CuboidRegion(new BlockPos(x, y, z), new BlockPos(subMaxX, subMaxY, subMaxZ)));
                }
            }
        }

        return subRegions;
    }

    public void fill(String blockName){
        PlayerUtils.sendCommandAsPlayer(
                "fill %s %s %s %s %s %s %s"
                        .formatted(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, blockName)
        );
    }

    public void replace(String fromBlock, String toBlock){
        PlayerUtils.sendCommandAsPlayer(
                "fill %s %s %s %s %s %s %s replace %s"
                        .formatted(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, toBlock, fromBlock)
        );
    }

    public int getTotalBlocks(){
        return (Math.abs(minX - maxX) + 1) * (Math.abs(minY - maxY) + 1) * (Math.abs(minZ - maxZ) + 1);
    }
}
