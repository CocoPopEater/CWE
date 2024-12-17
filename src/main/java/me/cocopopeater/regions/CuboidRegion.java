package me.cocopopeater.regions;

import me.cocopopeater.blocks.SimpleBlockPos;
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

    public BlockPos getMin(){
        return new BlockPos(minX, minY, minZ);
    }

    public BlockPos getMax(){
        return new BlockPos(maxX, maxY, maxZ);
    }

    public CuboidRegion(SimpleBlockPos start, SimpleBlockPos end){
        this.minX = Math.min(start.x(), end.x());
        this.minY = Math.max(Math.min(start.y(), end.y()), PlayerUtils.getPlayerCurrentWorldMinHeight());
        this.minZ = Math.min(start.z(), end.z());

        this.maxX = Math.max(start.x(), end.x());
        this.maxY = Math.min(Math.max(start.y(), end.y()), PlayerUtils.getPlayerCurrentWorldMaxHeight());
        this.maxZ = Math.max(start.z(), end.z());
    }

    public CuboidRegion(BlockPos start, BlockPos end){
        this(SimpleBlockPos.fromBlockPos(start), SimpleBlockPos.fromBlockPos(end));
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

    public String getFillCommand(String blockData){
        return "fill %s %s %s %s %s %s %s"
                .formatted(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, blockData);
    }

    public String getReplaceCommand(String fromBlockData, String toBlockData){
        return "fill %s %s %s %s %s %s %s replace %s"
                .formatted(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, toBlockData, fromBlockData);
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

    public String getPositionsAsString(){
        StringBuilder sb = new StringBuilder();

        sb.append(this.minX);
        sb.append("\n");
        sb.append(this.minY);
        sb.append("\n");
        sb.append(this.minZ);
        sb.append("\n");
        sb.append(this.maxX);
        sb.append("\n");
        sb.append(this.maxY);
        sb.append("\n");
        sb.append(this.maxZ);
        return sb.toString();
    }
}
