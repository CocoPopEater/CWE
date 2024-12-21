package me.cocopopeater.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum TreeType {
    RANDOM,
    ACACIA,
    AZALEA_TREE,
    BIRCH,
    BIRCH_BEES_0002,
    BIRCH_BEES_002,
    BIRCH_BEES_005,
    BIRCH_TALL,
    CHERRY,
    CHERRY_BEES_005,
    CHORUS_PLANT,
    DARK_OAK,
    FANCY_OAK,
    FANCY_OAK_BEES,
    FANCY_OAK_BEES_0002,
    FANCY_OAK_BEES_002,
    FANCY_OAK_BEES_005,
    MANGROVE,
    OAK,
    OAK_BEES_0002,
    OAK_BEES_002,
    OAK_BEES_005,
    PINE,
    ROOTED_AZALEA_TREE,
    SPRUCE,
    SUPER_BIRCH_BEES,
    SUPER_BIRCH_BEES_0002,
    SWAMP_OAK,
    TALL_MANGROVE,
    TREES_BIRCH_AND_OAK,
    TREES_FLOWER_FOREST,
    TREES_GROVE,
    TREES_JUNGLE,
    TREES_OLD_GROWTH_SPRUCE_TAIGA,
    TREES_PLAINS,
    TREES_SAVANNA,
    TREES_SPARSE_JUNGLE,
    TREES_TAIGA,
    TREES_WATER,
    TREES_WINDSWEPT_HILLS;

    private static Random random;
    static{
        random = new Random();
    }

    public static List<String> getCleanValues(){
        ArrayList<String> strings = new ArrayList<>();
        for(TreeType type : values()){
            strings.add(type.toString().toLowerCase());
        }
        return strings;
    }
    public static TreeType getRandomTree(){
        return values()[random.nextInt(1, values().length)]; // random tree type, not including RANDOM
    }
}
