package me.cocopopeater.util.varmanagers;

import me.cocopopeater.regions.SchematicRegion;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PlayerVariableManager {

    private static BlockPos pos1;
    private static BlockPos pos2;
    private static SchematicRegion schematicRegion;

    public static BlockPos getPos1(){
        return pos1;
    }
    public static BlockPos getPos2(){
        return pos2;
    }

    public static void saveToClipboard(){
        schematicRegion = new SchematicRegion(
                PlayerVariableManager.getPos1(),
                PlayerVariableManager.getPos2(),
                MinecraftClient.getInstance().world,
                SimpleBlockPos.fromBlockPos(MinecraftClient.getInstance().player.getBlockPos())
        );
    }

    public static SchematicRegion createSchematicRegion(){
        return new SchematicRegion(
                PlayerVariableManager.getPos1(),
                PlayerVariableManager.getPos2(),
                MinecraftClient.getInstance().world,
                SimpleBlockPos.fromBlockPos(MinecraftClient.getInstance().player.getBlockPos())
        );
    }

    public static void setSchematicRegion(SchematicRegion region){
        schematicRegion = region;
    }

    public static SchematicRegion getSchematicRegion(){
        return schematicRegion;
    }
    public static void setPos1(PlayerEntity playerEntity, BlockPos pos){
        if(pos.equals(pos1)){
            return;
        }
        pos1 = pos;
        String blockPosAsString = pos.toShortString();
        playerEntity.sendMessage(Text.literal("First position set to: (%s)".formatted(blockPosAsString)).withColor(GlobalColorRegistry.getLimeGreen()), true);
        playerEntity.sendMessage(Text.literal("First position set to: (%s)".formatted(blockPosAsString)).withColor(GlobalColorRegistry.getLimeGreen()), false);

    }

    public static void setPos2(PlayerEntity playerEntity, BlockPos pos){
        if(pos.equals(pos2)){
            return;
        }
        pos2 = pos;
        String blockPosAsString = pos.toShortString();
        playerEntity.sendMessage(Text.literal("Second position set to: (%s)".formatted(blockPosAsString)).withColor(GlobalColorRegistry.getLimeGreen()), true);
        playerEntity.sendMessage(Text.literal("Second position set to: (%s)".formatted(blockPosAsString)).withColor(GlobalColorRegistry.getLimeGreen()), false);

    }

    public static void expand(int amount, String direction){
        switch(direction.toUpperCase()) {
            case "NORTH" -> {
                // needs to expand negitive z
                // find lowest z coord and expand that

                if(pos1.getZ() < pos2.getZ()){
                    pos1 = pos1.add(0,0,-amount);
                }else{
                    pos2 = pos2.add(0,0,-amount);
                }
            }
            case "EAST" -> {
                // needs to expand positive x
                // find highest x coord

                if(pos1.getX() > pos2.getX()){
                    pos1 = pos1.add(amount,0,0);
                }else{
                    pos2 = pos2.add(amount,0,0);
                }
            }
            case "SOUTH" -> {
                // needs to expand positive z
                // find highest z coord

                if(pos1.getZ() > pos2.getZ()){
                    pos1 = pos1.add(0,0,amount);
                }else{
                    pos2 = pos2.add(0,0,amount);
                }
            }
            case "WEST" -> {
                // needs to expand negative x
                // find lowest x coord

                if(pos1.getX() < pos2.getX()){
                    pos1 = pos1.add(-amount,0,0);
                }else{
                    pos2 = pos2.add(-amount,0,0);
                }
            }
            case "UP" -> {
                // needs to expand positive x
                // find highest x coord

                if(pos1.getY() > pos2.getY()){
                    pos1 = pos1.add(0,amount,0);
                }else{
                    pos2 = pos2.add(0, amount,0);
                }
            }
            case "DOWN" -> {
                // needs to expand positive x
                // find highest x coord

                if(pos1.getY() < pos2.getY()){
                    pos1 = pos1.add(0,-amount,0);
                }else{
                    pos2 = pos2.add(0,-amount,0);
                }
            }
            default -> {
                PlayerUtils.sendPlayerMessageChat(
                        Text.literal("Unknown direction: %s".formatted(direction))
                                .withColor(GlobalColorRegistry.getBrightRed())
                );
            }
        }
    }

    public static CuboidRegion getCuboid(){
        return new CuboidRegion(pos1, pos2);
    }
}
