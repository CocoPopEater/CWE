package me.cocopopeater.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
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
            }else if(matcher.group(1).contains("sign")){
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if(!(blockEntity instanceof SignBlockEntity signBlockEntity)) return sb.toString();

                sb.append('{');
                if(signBlockEntity.isWaxed()) sb.append("is_waxed:1b,");


                sb.append("front_text:{");
                SignText front = signBlockEntity.getFrontText();
                if(front.isGlowing()) sb.append("has_glowing_text:1b,");
                sb.append("color:\"%s\",".formatted(front.getColor().asString()));

                sb.append("messages:["); // ']}
                int index = 0;

                // oak_sign{front_text:{messages:['["Line1"]','[" "]','[""]','[""]']}}
                for(Text text : front.getMessages(false)){
                    System.out.println(text.getString());
                    sb.append("'[\"%s\"]'".formatted(text.getString())); // adds the string surrounded by [""] example: ["Line1"]
                    if(!(index == 3)){
                        // inside this if means it isn't the last line
                        sb.append(',');
                    }else{
                        // inside this if means it is the last line
                        // we should close the messages  section
                        sb.append("]}");
                    }
                    index++;
                }

                sb.append(",back_text:{");
                SignText back = signBlockEntity.getBackText();
                if(back.isGlowing()) sb.append("has_glowing_text:1b,");
                sb.append("color:\"%s\",".formatted(back.getColor().asString()));

                sb.append("messages:["); // ']}
                index = 0;

                // oak_sign{front_text:{messages:['["Line1"]','[" "]','[""]','[""]']}}
                for(Text text : back.getMessages(false)){
                    System.out.println(text.getString());
                    sb.append("'[\"%s\"]'".formatted(text.getString())); // adds the string surrounded by [""] example: ["Line1"]
                    if(!(index == 3)){
                        // inside this if means it isn't the last line
                        sb.append(',');
                    }else{
                        // inside this if means it is the last line
                        // we should close the messages  section
                        sb.append("]}");
                    }
                    index++;
                }

                sb.append('}');
            }
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
