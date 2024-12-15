package me.cocopopeater.util;

import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.tasks.TimedCommandRunner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerUtils {

    public static void sendPlayerMessageChat(Text msg){
        MinecraftClient.getInstance().player.sendMessage(msg, false);
    }

    public static void sendPlayerMessageHotbar(Text msg){
        MinecraftClient.getInstance().player.sendMessage(msg, true);
    }

    public static boolean sendCommandAsPlayer(String command){

        return MinecraftClient.getInstance().getNetworkHandler().sendCommand(command);
    }

    public static void sendCommandList(List<String> commands){
        TimedCommandRunner runner = new TimedCommandRunner();
        runner.addTask(() -> sendCommandAsPlayer("gamerule sendCommandFeedback false"));
        for(String command : commands){
            runner.addTask(() -> PlayerUtils.sendCommandAsPlayer(command));
        }
        runner.addTask(() -> sendCommandAsPlayer("gamerule sendCommandFeedback true"));
        runner.start(ConfigHandler.getInstance().getCommandDelay(), TimeUnit.MILLISECONDS);
    }
}
