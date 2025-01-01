package me.cocopopeater.blocks;

import me.cocopopeater.util.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    public List<Zone> getWallZones() {
        SimpleBlockPos minPos = this.getMinPos(); // Minimum corner
        SimpleBlockPos maxPos = this.getMaxPos(); // Maximum corner

        SimpleBlockPos frontMin = new SimpleBlockPos(minPos.x(), minPos.y(), minPos.z());
        SimpleBlockPos frontMax = new SimpleBlockPos(maxPos.x(), maxPos.y(), minPos.z());
        Zone frontWall = new Zone(frontMin, frontMax);

        SimpleBlockPos backMin = new SimpleBlockPos(minPos.x(), minPos.y(), maxPos.z());
        SimpleBlockPos backMax = new SimpleBlockPos(maxPos.x(), maxPos.y(), maxPos.z());
        Zone backWall = new Zone(backMin, backMax);

        SimpleBlockPos leftMin = new SimpleBlockPos(minPos.x(), minPos.y(), minPos.z());
        SimpleBlockPos leftMax = new SimpleBlockPos(minPos.x(), maxPos.y(), maxPos.z());
        Zone leftWall = new Zone(leftMin, leftMax);

        SimpleBlockPos rightMin = new SimpleBlockPos(maxPos.x(), minPos.y(), minPos.z());
        SimpleBlockPos rightMax = new SimpleBlockPos(maxPos.x(), maxPos.y(), maxPos.z());
        Zone rightWall = new Zone(rightMin, rightMax);


        return Arrays.asList(frontWall, backWall, leftWall, rightWall);
    }

    public void markZonePoints(Set<SimpleBlockPos> storageSet){
        for(int x = this.getMinPos().x(); x <= this.getMaxPos().x(); x++){
            for(int y = this.getMinPos().y(); y <= this.getMaxPos().y(); y++){
                for(int z = this.getMinPos().z(); z <= this.getMaxPos().z(); z++){
                    storageSet.add(new SimpleBlockPos(x,y,z));
                }
            }
        }
    }

    public List<SimpleBlockPos> getAllPoints(){
        ArrayList<SimpleBlockPos> positions = new ArrayList<>();
        for(int x = this.getMinPos().x(); x <= this.getMaxPos().x(); x++){
            for(int y = this.getMinPos().y(); y <= this.getMaxPos().y(); y++){
                for(int z = this.getMinPos().z(); z <= this.getMaxPos().z(); z++){
                    positions.add(new SimpleBlockPos(x,y,z));
                }
            }
        }
        return positions;
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
