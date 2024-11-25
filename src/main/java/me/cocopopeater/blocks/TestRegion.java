package me.cocopopeater.blocks;

import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.BlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TestRegion extends CuboidRegion {
    protected List<BlockZone> zones;

    public TestRegion(BlockPos start, BlockPos end, World world, SimpleBlockPos startPosition) {
        super(start, end);
        this.zones = generateZones(world);
        updateOffsets(startPosition);
    }

    public List<BlockZone> getZones(){
        return this.zones;
    }

    public List<String> generateFillCommands(SimpleBlockPos playerPos){
        ArrayList<String> commands = new ArrayList<>();

        for(BlockZone zone : this.zones){
            for(String command : zone.generateFillCommands()){
                commands.add(command);
            }
        }
        return commands;
    }

    protected void updateOffsets(SimpleBlockPos basePosition){

        List<BlockZone> newBlockZones = new ArrayList<>();

        for(BlockZone zone : this.zones){
            SimpleBlockPos min = zone.getMinPos();
            SimpleBlockPos max = zone.getMaxPos();

            int startOffsetX = min.x() - basePosition.x();
            int startOffsetY = min.y() - basePosition.y();
            int startOffsetZ = min.z() - basePosition.z();

            int endOffsetX = max.x() - basePosition.x();
            int endOffsetY = max.y() - basePosition.y();
            int endOffsetZ = max.z() - basePosition.z();

            BlockZone newZone = new BlockZone(
                    new SimpleBlockPos(startOffsetX, startOffsetY, startOffsetZ),
                    new SimpleBlockPos(endOffsetX, endOffsetY, endOffsetZ),
                    zone.blockData
            );

            newBlockZones.add(newZone);
        }
        this.zones = newBlockZones;
    }



    private void cacheBlockData(Map<SimpleBlockPos, String> blockDataMap, World world){
        // cache block data for each position for easy checks later
        for (int x = getMin().getX(); x <= getMax().getX(); x++) {
            for (int y = getMin().getY(); y <= getMax().getY(); y++) {
                for (int z = getMin().getZ(); z <= getMax().getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    blockDataMap.put(new SimpleBlockPos(x, y, z), getBlockData(pos, world));
                }
            }
        }
    }

    public List<BlockZone> generateZones(World world) {

        // this doesnt work, rewrite it
        ArrayList<BlockZone> zones = new ArrayList<>();
        Map<SimpleBlockPos, String> blockDataMap = new HashMap<>();
        Set<SimpleBlockPos> visited = new HashSet<>();

        cacheBlockData(blockDataMap, world);

        for (int y = this.minY; y <= this.maxY; y++) {
            for (int x = this.minX; x <= this.maxX; x++) {
                for (int z = this.minZ; z <= this.maxZ; z++) {
                    SimpleBlockPos currentPos = new SimpleBlockPos(x, y, z);

                    if (visited.contains(currentPos)) {
                        continue;
                    }

                    String blockData = blockDataMap.get(currentPos);
                    if (blockData == null) {
                        continue;
                    }

                    SimpleBlockPos start = currentPos;
                    SimpleBlockPos end = findZoneEnd(start, blockData, blockDataMap, visited);

                    markVisited(start, end, visited);

                    zones.add(new BlockZone(start, end, blockData));
                }
            }
        }
        return zones;
    }

    private SimpleBlockPos findZoneEnd(SimpleBlockPos start, String blockData,
                                       Map<SimpleBlockPos, String> blockDataMap,
                                       Set<SimpleBlockPos> visited) {
        int endX = start.x();
        int endY = start.y();
        int endZ = start.z();

        // expand zone across x y z
        while (blockDataMap.get(new SimpleBlockPos(endX + 1, endY, endZ)) != null &&
                blockDataMap.get(new SimpleBlockPos(endX + 1, endY, endZ)).equals(blockData) &&
                !visited.contains(new SimpleBlockPos(endX + 1, endY, endZ))) {
            endX++;
        }

        while (blockDataMap.get(new SimpleBlockPos(endX, endY + 1, endZ)) != null &&
                blockDataMap.get(new SimpleBlockPos(endX, endY + 1, endZ)).equals(blockData) &&
                !visited.contains(new SimpleBlockPos(endX, endY + 1, endZ))) {
            endY++;
        }

        while (blockDataMap.get(new SimpleBlockPos(endX, endY, endZ + 1)) != null &&
                blockDataMap.get(new SimpleBlockPos(endX, endY, endZ + 1)).equals(blockData) &&
                !visited.contains(new SimpleBlockPos(endX, endY, endZ + 1))) {
            endZ++;
        }

        return new SimpleBlockPos(endX, endY, endZ);
    }

    private void markVisited(SimpleBlockPos start, SimpleBlockPos end, Set<SimpleBlockPos> visited) {
        for (int x = start.x(); x <= end.x(); x++) {
            for (int y = start.y(); y <= end.y(); y++) {
                for (int z = start.z(); z <= end.z(); z++) {
                    visited.add(new SimpleBlockPos(x, y, z));
                }
            }
        }
    }



    private String getBlockData(BlockPos pos, World world) {
        return BlockUtils.extractBlockData(world, pos);
    }
}
