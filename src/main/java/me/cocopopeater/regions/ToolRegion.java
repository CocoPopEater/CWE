package me.cocopopeater.regions;

import me.cocopopeater.blocks.BlockZone;
import me.cocopopeater.blocks.SimpleBlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ToolRegion extends SchematicRegion{

    public ToolRegion(SchematicRegion region){
        super(new BlockPos(0,0,0), new BlockPos(0,0,0), MinecraftClient.getInstance().world, SimpleBlockPos.fromBlockPos(MinecraftClient.getInstance().player.getBlockPos()));
        this.zones = region.getZones();

        this.minX = region.minX;
        this.minY = region.minY;
        this.minZ = region.minZ;

        this.maxX = region.maxX;
        this.maxY = region.maxY;
        this.maxZ = region.maxZ;
    }

    public void setOrigin(SimpleBlockPos pos){
        updateOffsets(pos);
    }

    @Override
    public List<String> generateFillCommands(){
        ArrayList<String> commands = new ArrayList<>();

        for(BlockZone zone : this.zones){
            if(zone.getBlockData().toLowerCase().equals("air")) continue;
            for(String command : zone.generateFillCommands()){
                commands.add(command);
            }
        }
        return commands;
    }
}
