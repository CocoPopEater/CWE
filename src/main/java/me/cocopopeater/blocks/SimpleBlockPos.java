package me.cocopopeater.blocks;

import net.minecraft.util.math.BlockPos;

public class SimpleBlockPos {
    private int x;
    private int y;
    private int z;

    public SimpleBlockPos(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SimpleBlockPos fromBlockPos(BlockPos pos){
        return new SimpleBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getZ(){
        return z;
    }



}
