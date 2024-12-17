package me.cocopopeater.util;

import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.regions.CuboidRegion;

public class RegionUtils {

    public static CuboidRegion createCuboidAroundPoint(SimpleBlockPos point, int radius){
        return new CuboidRegion(
                new SimpleBlockPos(
                        point.x() - radius,
                        point.y() - radius,
                        point.z() - radius
                ),
                new SimpleBlockPos(
                        point.x() + radius,
                        point.y() + radius,
                        point.z() + radius
                )
        );
    }
}
