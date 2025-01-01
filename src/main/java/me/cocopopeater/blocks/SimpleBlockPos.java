package me.cocopopeater.blocks;

import net.minecraft.util.math.BlockPos;

public record SimpleBlockPos(int x, int y, int z) {

    public static SimpleBlockPos fromBlockPos(BlockPos pos) {
        return new SimpleBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }
    public BlockPos toBlockPos(){
        return new BlockPos(this.x, this.y, this.z);
    }

    public String toShortString() {
        return this.x() + ", " + this.y() + ", " + this.z();
    }
}
