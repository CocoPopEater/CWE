package me.cocopopeater.regions;

import me.cocopopeater.blocks.BlockZone;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.blocks.Zone;
import me.cocopopeater.util.BlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class SchematicRegion extends CuboidRegion {
    protected List<BlockZone> zones;

    public SchematicRegion(BlockPos start, BlockPos end, World world, SimpleBlockPos startPosition) {
        super(start, end);
        this.zones = generateZones(world);
        updateOffsets(startPosition);
    }

    public List<BlockZone> getZones(){
        return this.zones;
    }

    public List<String> generateFillCommands(){
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
        // block data might exceed command length
        // if command length more than 250 then set the default block
        // then run data merge commands for the excess data

        // e.g: dark_oak_fence[east\u003dtrue,north\u003dtrue,south\u003dfalse,waterlogged\u003dfalse,west\u003dfalse]
        // becomes
        // setblock ~ ~ ~ dark_oak_fence
        // data merge ~ ~ ~ [east\u003dtrue,north\u003dtrue,south\u003dfalse,waterlogged\u003dfalse,west\u003dfalse]
        // or
        // data merge ~ ~ ~ [east\u003dtrue,north\u003dtrue]
        // data merge ~ ~ ~ [south\u003dfalse,waterlogged\u003dfalse,west\u003dfalse]
        // use {} for data merge commands

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

                    SimpleBlockPos start = currentPos;
                    SimpleBlockPos end = findZoneEnd(start, blockData, blockDataMap, visited);

                    BlockZone zone = new BlockZone(start, end, blockData);
                    visited.addAll(zone.getAllPoints());
                    zones.add(zone);
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

        // expand zone across z
        while (blockDataMap.get(new SimpleBlockPos(endX, endY, endZ + 1)) != null &&
                !visited.contains(new SimpleBlockPos(endX, endY, endZ + 1)) &&
                blockDataMap.get(new SimpleBlockPos(endX, endY, endZ + 1)).equals(blockData) ) {
            endZ++;
        }

        // expand across x
        xExpansion:
        while (true) {
            // get all blocks from minZ to maxZ in the current X row
            SimpleBlockPos lineStart = new SimpleBlockPos(endX + 1, start.y(), start.z());
            SimpleBlockPos lineEnd = new SimpleBlockPos(endX +1, start.y(), endZ);

            Zone zone = new Zone(lineStart, lineEnd);

            for(SimpleBlockPos pos : zone.getAllPoints()){
                String value = blockDataMap.get(pos);
                if(value == null || !value.equals(blockData)){
                    break xExpansion;
                }
            }
            endX++;
        }
        yExpansion:
        while (true) {
            // get all blocks from minY to maxY in the current X/Z plane
            SimpleBlockPos lineStart = new SimpleBlockPos(start.x(), endY + 1, start.z());
            SimpleBlockPos lineEnd = new SimpleBlockPos(endX, endY + 1, endZ);

            Zone zone = new Zone(lineStart, lineEnd);

            for(SimpleBlockPos pos : zone.getAllPoints()){
                String value = blockDataMap.get(pos);
                if(value == null || !value.equals(blockData)){
                    break yExpansion;
                }
            }
            endY++;
        }
        return new SimpleBlockPos(endX, endY, endZ);
    }

    private String getBlockData(BlockPos pos, World world) {
        return BlockUtils.extractBlockData(world, pos);
    }
}
