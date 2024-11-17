package me.cocopopeater.util;

import net.minecraft.block.BlockState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUtils {

    public static String extractBlockDataFromState(String state){
        StringBuilder sb = new StringBuilder();

        Pattern pattern = Pattern.compile("Block\\{(.*?)\\}(.*)");
        Matcher matcher = pattern.matcher(state);

        if (matcher.find()) {
            sb.append(matcher.group(1));
            sb.append(matcher.group(2));
        }

        return sb.toString();
    }
}
