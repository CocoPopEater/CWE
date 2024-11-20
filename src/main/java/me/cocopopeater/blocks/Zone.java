package me.cocopopeater.blocks;

import me.cocopopeater.util.MathHelper;

public class Zone {

    public final SimpleBlockPos point1;
    public final SimpleBlockPos point2;

    public Zone(SimpleBlockPos pos1, SimpleBlockPos pos2){
        this.point1 = pos1;
        this.point2 = pos2;
    }

    public SimpleBlockPos getMinPos(){
        return new SimpleBlockPos(
                Math.min(point1.x(), point2.x()),
                Math.min(point1.y(), point2.y()),
                Math.min(point1.z(), point2.z())
        );
    }

    public SimpleBlockPos getMaxPos(){
        return new SimpleBlockPos(
                Math.max(point1.x(), point2.x()),
                Math.max(point1.y(), point2.y()),
                Math.max(point1.z(), point2.z())
        );
    }

    public int getTotalVolume(){
        return MathHelper.getTotalVolume(point1, point2);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Zone other = (Zone) obj;
        return this.point1.equals(other.point1)
                && this.point2.equals(other.point2);
    }

    @Override
    public int hashCode() {
        return 31 * point1.hashCode() + point2.hashCode();
    }
}
