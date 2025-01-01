package me.cocopopeater.util.varmanagers;

import me.cocopopeater.regions.SchematicRegion;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.tools.Tool;
import me.cocopopeater.tools.ToolFactory;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.tools.ToolType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerVariableManager {

    private static BlockPos pos1;
    private static BlockPos pos2;
    private static SchematicRegion schematicRegion;

    private static final Map<String, Tool> toolMap = new HashMap<>();

    public static void bindTool(String itemName, ToolType newTool, String optionalInfo){
        toolMap.put(itemName, ToolFactory.createTool(newTool, optionalInfo));

    }
    public static void unbindTool(String itemName){
        toolMap.remove(itemName);
    }

    public static Tool getTool(String itemName){
        return toolMap.getOrDefault(itemName, null);
    }

    public static BlockPos getPos1(){
        return pos1;
    }
    public static BlockPos getPos2(){
        return pos2;
    }

    public static void saveToClipboard(){
        CompletableFuture.runAsync(() -> schematicRegion = new SchematicRegion(
                PlayerVariableManager.getPos1(),
                PlayerVariableManager.getPos2(),
                MinecraftClient.getInstance().world,
                SimpleBlockPos.fromBlockPos(MinecraftClient.getInstance().player.getBlockPos())
        ));

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
    public static void setPos1(BlockPos pos){
        if(pos.equals(pos1)){
            return;
        }
        pos1 = pos;
        String blockPosAsString = pos.toShortString();
        PlayerUtils.sendPlayerMessageBoth(
                Text.translatable("region.selection.position1", blockPosAsString).withColor(GlobalColorRegistry.getLimeGreen())
        );

    }

    public static void setPos2(BlockPos pos){
        if(pos.equals(pos2)){
            return;
        }
        pos2 = pos;
        String blockPosAsString = pos.toShortString();
        PlayerUtils.sendPlayerMessageBoth(
                Text.translatable("region.selection.position2", blockPosAsString).withColor(GlobalColorRegistry.getLimeGreen())
        );

    }

    private static void expandNorth(int amount){
        if(pos1.getZ() < pos2.getZ()){
            pos1 = pos1.add(0,0,-amount);
        }else{
            pos2 = pos2.add(0,0,-amount);
        }
    }

    private static void expandEast(int amount){
        if(pos1.getX() > pos2.getX()){
            pos1 = pos1.add(amount,0,0);
        }else{
            pos2 = pos2.add(amount,0,0);
        }
    }

    private static void expandSouth(int amount){
        if(pos1.getZ() > pos2.getZ()){
            pos1 = pos1.add(0,0,amount);
        }else{
            pos2 = pos2.add(0,0,amount);
        }
    }

    private static void expandWest(int amount){
        if(pos1.getX() < pos2.getX()){
            pos1 = pos1.add(-amount,0,0);
        }else{
            pos2 = pos2.add(-amount,0,0);
        }
    }

    private static void expandUp(int amount){
        if(pos1.getY() > pos2.getY()){
            pos1 = pos1.add(0,amount,0);
        }else{
            pos2 = pos2.add(0, amount,0);
        }
    }

    private static void expandDown(int amount){
        if(pos1.getY() < pos2.getY()){
            pos1 = pos1.add(0,-amount,0);
        }else{
            pos2 = pos2.add(0,-amount,0);
        }
    }
    private static void expandVert(){
        if(pos1.getY() < pos2.getY()){
            pos1 = new BlockPos(pos1.getX(), PlayerUtils.getPlayerCurrentWorldMinHeight(), pos1.getZ());
            pos2 = new BlockPos(pos2.getX(), PlayerUtils.getPlayerCurrentWorldMaxHeight(), pos2.getZ());
        }else{
            // Yes future me, this is different to the above, the min and max are switched
            pos2 = new BlockPos(pos2.getX(), PlayerUtils.getPlayerCurrentWorldMinHeight(), pos2.getZ());
            pos1 = new BlockPos(pos1.getX(), PlayerUtils.getPlayerCurrentWorldMaxHeight(), pos1.getZ());
        }
    }

    public static void expand(int amount, String direction){

        // north, east, south, west, up, down, vert, all
        switch(direction.toUpperCase().charAt(0)){
            case 'N' -> expandNorth(amount);
            case 'E' -> expandEast(amount);
            case 'S' -> expandSouth(amount);
            case 'W' -> expandWest(amount);
            case 'U' -> expandUp(amount);
            case 'D' -> expandDown(amount);
            case 'V' -> expandVert(); // vert, should cover the entire y limit

            // All
            case 'A' -> {
                expandNorth(amount);
                expandEast(amount);
                expandSouth(amount);
                expandWest(amount);
                expandUp(amount);
                expandDown(amount);
            }
            // These next ones are relational:
            // forward, left, right, back, luckily no directions share the same first letter
            // so we can get away with a tiny bit of recursion
            case 'F' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().getName();
                expand(amount, dir);
            }
            case 'L' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().rotateYCounterclockwise().getName();
                expand(amount, dir);
            }
            case 'R' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().rotateYClockwise().getName();
                expand(amount, dir);
            }
            case 'B' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().getOpposite().getName();
                expand(amount, dir);
            }

            default -> PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.generic.unknown_direction", direction)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
    }
    public static void contract(int amount, String direction){

        switch(direction.toUpperCase().charAt(0)){
            case 'N' -> expandSouth(-amount);
            case 'E' -> expandWest(-amount);
            case 'S' -> expandNorth(-amount);
            case 'W' -> expandEast(-amount);
            case 'U' -> expandDown(-amount);
            case 'D' -> expandUp(-amount);
            // All
            case 'A' -> {
                expandNorth(-amount);
                expandEast(-amount);
                expandSouth(-amount);
                expandWest(-amount);
                expandUp(-amount);
                expandDown(-amount);
            }
            // These next ones are relational:
            // forward, left, right, back, luckily no directions share the same first letter
            // so we can get away with a tiny bit of recursion
            case 'F' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().getName();
                contract(amount, dir);
            }
            case 'L' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().rotateYCounterclockwise().getName();
                contract(amount, dir);
            }
            case 'R' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().rotateYClockwise().getName();
                contract(amount, dir);
            }
            case 'B' -> {
                String dir = MinecraftClient.getInstance().player.getHorizontalFacing().getOpposite().getName();
                contract(amount, dir);
            }

            default -> PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.generic.unknown_direction", direction)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
    }

    public static CuboidRegion getCuboid(){
        return new CuboidRegion(pos1, pos2);
    }
}
