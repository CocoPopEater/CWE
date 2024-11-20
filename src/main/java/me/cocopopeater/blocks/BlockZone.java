package me.cocopopeater.blocks;

import me.cocopopeater.util.varmanagers.GlobalVariableManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a cuboid to be filled with a single set of block data
 */
public class BlockZone extends Zone{
    public final String blockData;

    public BlockZone(SimpleBlockPos pos1, SimpleBlockPos pos2, String blockData){
        super(pos1, pos2);
        this.blockData = blockData;
    }


    public ArrayList<BlockZone> subdivide() {
        ArrayList<BlockZone> subRegions = new ArrayList<>();

        int totalVolume = getTotalVolume();
        if (totalVolume <= GlobalVariableManager.MAX_BLOCKS) {
            subRegions.add(this);
            return subRegions;
        }

        SimpleBlockPos min = getMinPos();
        SimpleBlockPos max = getMaxPos();

        int minX = min.x();
        int minY = min.y();
        int minZ = min.z();

        int maxX = max.x();
        int maxY = max.y();
        int maxZ = max.z();

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

                    subRegions.add(new BlockZone(new SimpleBlockPos(x, y, z), new SimpleBlockPos(subMaxX, subMaxY, subMaxZ), blockData));
                }
            }
        }

        return subRegions;
    }

    public List<String> generateFillCommands(){
        int totalVolume = getTotalVolume();
        ArrayList<String> commands = new ArrayList<>();

        for(BlockZone zone : this.subdivide()){
            commands.add(
                    "fill %d %d %d %d %d %d %s".formatted(
                            zone.point1.x(),
                            zone.point1.y(),
                            zone.point1.z(),
                            zone.point2.x(),
                            zone.point2.y(),
                            zone.point2.z(),
                            zone.blockData
                    )
            );
        }
        return commands;
    }
}
