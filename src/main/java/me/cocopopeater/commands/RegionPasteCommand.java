package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.regions.ClipboardRegion;
import me.cocopopeater.util.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.PlayerVariableManager;
import me.cocopopeater.util.TimedCommandRunner;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RegionPasteCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/paste").executes(RegionPasteCommand::run));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        FabricClientCommandSource source = context.getSource();

        if(!source.hasPermissionLevel(2)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("You dont have permission to perform this command")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        ClipboardRegion clipboardRegion = PlayerVariableManager.getClipBoardRegion();
        if(clipboardRegion == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("You do not have a region copied").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        TimedCommandRunner runner = new TimedCommandRunner();
        for(String command : clipboardRegion.getPasteCommandList()){
            runner.addTask(() -> PlayerUtils.sendCommandAsPlayer(command));
        }

        runner.start(ConfigHandler.getInstance().getCommandDelay(), TimeUnit.MILLISECONDS);
        return 1;
    }
}
