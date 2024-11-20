package me.cocopopeater.util;

import me.cocopopeater.blocks.SimpleBlockPos;
import net.minecraft.util.math.BlockPos;

public class MathHelper {
    public static int getTotalVolume(int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
        return (Math.abs(minX - maxX) + 1) * (Math.abs(minY - maxY) + 1) * (Math.abs(minZ - maxZ) + 1);
    }

    public static int getTotalVolume(BlockPos pos1, BlockPos pos2){
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());

        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        return getTotalVolume(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static int getTotalVolume(SimpleBlockPos pos1, SimpleBlockPos pos2){
        int minX = Math.min(pos1.x(), pos2.x());
        int minY = Math.min(pos1.y(), pos2.y());
        int minZ = Math.min(pos1.z(), pos2.z());

        int maxX = Math.max(pos1.x(), pos2.x());
        int maxY = Math.max(pos1.y(), pos2.y());
        int maxZ = Math.max(pos1.z(), pos2.z());
        return getTotalVolume(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
