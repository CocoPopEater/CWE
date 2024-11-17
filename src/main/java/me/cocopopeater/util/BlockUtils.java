package me.cocopopeater.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUtils {

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
        BlockState state = client.world.getBlockState(pos);

        String blockData = extractBlockDataFromState(state.toString());

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
        BlockState state = client.world.getBlockState(pos);

        String blockData = extractBlockDataFromState(state.toString());

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
