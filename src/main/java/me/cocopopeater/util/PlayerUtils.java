package me.cocopopeater.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class PlayerUtils {

    public static void sendPlayerMessageChat(Text msg){
        MinecraftClient.getInstance().player.sendMessage(msg, false);
    }

    public static void sendPlayerMessageHotbar(Text msg){
        MinecraftClient.getInstance().player.sendMessage(msg, true);
    }

    public static void sendCommandAsPlayer(String command){
        MinecraftClient.getInstance().getNetworkHandler().sendCommand(command);
    }
}
