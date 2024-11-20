package me.cocopopeater.blocks;

import me.cocopopeater.regions.AdvancedCuboidRegion;
import me.cocopopeater.util.MathHelper;
import me.cocopopeater.util.varmanagers.GlobalVariableManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TestZone extends AdvancedCuboidRegion {


    public TestZone(BlockPos start, BlockPos end, World world) {
        super(start, end, world);
    }

    public List<String> generateFillCommands(){
        // testing method for greedy meshing-esque algo

        ArrayList<String> commands = new ArrayList<>();
        List<Map.Entry<SimpleBlockPos, String>> reqBlockList = this.blocks.entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().y()))
                .sorted(Comparator.comparing(e -> e.getKey().x()))
                .sorted(Comparator.comparing(e -> e.getKey().z()))
                .toList();

        return commands;
    }
}
