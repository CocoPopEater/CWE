package me.cocopopeater.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUtils {

    public static String extractBlockData(World world, BlockPos pos){
        StringBuilder sb = new StringBuilder();

        String state = world.getBlockState(pos).toString();
        Pattern pattern = Pattern.compile("Block\\{minecraft:(.*?)\\}(.*)");
        Matcher matcher = pattern.matcher(state);

        if (matcher.find()) {
            sb.append(matcher.group(1));
            sb.append(matcher.group(2));
            if(matcher.group(1).contains("head")){
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if(blockEntity == null) return sb.toString();

                String regex = "ProfileComponent\\[name=Optional\\[(.*?)]";
                Pattern namePattern = Pattern.compile(regex);

                for(var component : blockEntity.createComponentMap()){
                    System.out.println(component);

                    Matcher nameMatcher = namePattern.matcher(component.toString());
                    if(nameMatcher.find()){
                        sb.append("{profile:\"%s\"}".formatted(nameMatcher.group(1)));
                    }

                }
            }
        }

        return sb.toString();
    }

    public static String extractBlockDataFromState(String state){
        StringBuilder sb = new StringBuilder();

        Pattern pattern = Pattern.compile("Block\\{minecraft:(.*?)\\}(.*)");
        Matcher matcher = pattern.matcher(state);

        if (matcher.find()) {
            sb.append(matcher.group(1));
            sb.append(matcher.group(2));
        }

        return sb.toString();
    }

    public static boolean canSetBlocks(){
        MinecraftClient client = MinecraftClient.getInstance();
        assert client != null;

        BlockPos pos = client.player.getBlockPos();

        String blockData = extractBlockData(client.player.getWorld(), pos);

        return PlayerUtils.sendCommandAsPlayer("setblock %d %d %d %s".formatted(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                blockData
        ));
    }
    public static boolean canFillBlocks(){
        MinecraftClient client = MinecraftClient.getInstance();
        assert client != null;

        BlockPos pos = client.player.getBlockPos();

        String blockData = extractBlockData(client.player.getWorld(), pos);

        return PlayerUtils.sendCommandAsPlayer("fill %d %d %d %d %d %d %s replace barrier".formatted(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                blockData
        ));
    }


}
