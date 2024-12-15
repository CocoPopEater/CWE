package me.cocopopeater.blocks;

import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.tasks.TimedCommandRunner;
import me.cocopopeater.util.varmanagers.GlobalVariableManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a cuboid to be filled with a single set of block data
 */
public class BlockZone extends Zone{
    public final String blockData;

    public BlockZone(SimpleBlockPos pos1, SimpleBlockPos pos2, String blockData){
        super(pos1, pos2);
        this.blockData = blockData;
    }

    public BlockZone(Zone zone, String blockData){
        super(zone.point1, zone.point2);
        this.blockData = blockData;
    }

    public String getBlockData(){
        return this.blockData;
    }

    public void paste(){
        PlayerUtils.sendCommandList(this.generateFillCommands());
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
        ArrayList<String> commands = new ArrayList<>();
        int totalVolume = getTotalVolume();
        ClientPlayerEntity player = MinecraftClient.getInstance().player; // get players current position

        if(totalVolume < GlobalVariableManager.MAX_BLOCKS){
            commands.add(
                    "fill %d %d %d %d %d %d %s".formatted(
                            player.getBlockPos().add(this.point1.x(),0,0).getX(),
                            player.getBlockPos().add(0,this.point1.y(),0).getY(),
                            player.getBlockPos().add(0,0,this.point1.z()).getZ(),
                            player.getBlockPos().add(this.point2.x(),0,0).getX(),
                            player.getBlockPos().add(0,this.point2.y(),0).getY(),
                            player.getBlockPos().add(0,0,this.point2.z()).getZ(),
                            this.blockData
                    )
            );
            return commands;
        }


        for(BlockZone zone : this.subdivide()){
            commands.add(
                    "fill %d %d %d %d %d %d %s".formatted(
                            player.getBlockPos().add(zone.point1.x(),0,0).getX(),
                            player.getBlockPos().add(0,zone.point1.y(),0).getY(),
                            player.getBlockPos().add(0,0,zone.point1.z()).getZ(),
                            player.getBlockPos().add(zone.point2.x(),0,0).getX(),
                            player.getBlockPos().add(0,zone.point2.y(),0).getY(),
                            player.getBlockPos().add(0,0,zone.point2.z()).getZ(),
                            this.blockData
                    )
            );
        }
        return commands;
    }
}
